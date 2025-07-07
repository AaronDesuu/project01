package com.blemeterkai.meterkai.ui.main; // Adjust package as needed

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blemeterkai.meterkai.databinding.FragmentUploadBinding;
// import com.blemeterkai.meterkai.MainActivity; // Assuming this is your main activity
import com.blemeterkai.meterkai.viewmodel.FileUploadViewModel; // Import your Java ViewModel

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadFragment extends Fragment {

    private static final String TAG = "UploadFragment"; // For logging
    private FragmentUploadBinding binding; // ViewBinding instance
    private Uri selectedFileUri;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private FileUploadViewModel fileUploadViewModel; // Add this
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    String fileName = getFileName(requireContext(), uri);
                    binding.textViewSelectedFileName.setText("Selected: " + (fileName != null ? fileName : "Unknown File"));
                    binding.buttonUploadFile.setVisibility(View.VISIBLE);
                    binding.buttonUploadFile.setEnabled(true);
                    binding.textViewUploadStatus.setVisibility(View.GONE);
                } else {
                    binding.textViewSelectedFileName.setText("No file selected");
                    binding.buttonUploadFile.setVisibility(View.GONE);
                }
            });

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel scoped to the Activity, so it's shared
        // Use requireActivity() to ensure the Activity context is available
        fileUploadViewModel = new ViewModelProvider(requireActivity()).get(FileUploadViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSelectFile.setOnClickListener(v -> selectAnyFile());

        binding.buttonUploadFile.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                uploadFileToAppFiles(selectedFileUri);
            } else {
                Toast.makeText(getContext(), "Please select a file first", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void selectAnyFile() {
        filePickerLauncher.launch("*/*");
    }

    private String getFileName(@NonNull Context context, Uri uri) {
        String fileName = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name from ContentResolver", e);
            }
        }
        if (fileName == null) {
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                if (cut != -1) {
                    fileName = path.substring(cut + 1);
                }
            }
        }
        return fileName;
    }

    private String getFileExtension(Context context, Uri uri) {
        String extension = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else if (uri.getScheme() != null && uri.getScheme().equals("file")) {
            String path = uri.getPath();
            if (path != null) {
                int lastDot = path.lastIndexOf('.');
                if (lastDot >= 0) {
                    extension = path.substring(lastDot + 1);
                }
            }
        }
        return extension;
    }

    private void uploadFileToAppFiles(Uri fileUri) {
        Context context = getContext();
        if (context == null) {
            Toast.makeText(requireContext(), "Error: Context not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String originalFileName = getFileName(context, fileUri);
        String fileExtension = getFileExtension(context, fileUri);
        String targetFileName;
        if (originalFileName != null && !originalFileName.isEmpty()) {
            targetFileName = originalFileName;
        } else if (fileExtension != null && !fileExtension.isEmpty()) {
            targetFileName = "uploaded_file_" + System.currentTimeMillis() + "." + fileExtension;
        } else {
            targetFileName = "uploaded_file_" + System.currentTimeMillis();
        }

        File externalAppFilesDir = context.getExternalFilesDir(null);
        if (externalAppFilesDir == null) {
            Log.e(TAG, "External storage is not available or not writable.");
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(context, "External storage not available. Upload failed.", Toast.LENGTH_LONG).show();
                    binding.progressBarUpload.setVisibility(View.GONE);
                    binding.buttonUploadFile.setEnabled(true);
                    binding.buttonSelectFile.setEnabled(true);
                    binding.textViewUploadStatus.setText("Upload failed: External storage unavailable.");
                });
            }
            return;
        }
        File targetFile = new File(externalAppFilesDir, targetFileName);

        binding.progressBarUpload.setVisibility(View.VISIBLE);
        binding.progressBarUpload.setIndeterminate(true);
        binding.textViewUploadStatus.setVisibility(View.VISIBLE);
        binding.textViewUploadStatus.setText("Uploading " + targetFileName + "...");
        binding.buttonUploadFile.setEnabled(false);
        binding.buttonSelectFile.setEnabled(false);


        String finalUserVisibleFileName = targetFileName;
        executorService.execute(() -> {
            boolean success = copyFileToInternalStorage(context, fileUri, targetFile);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.progressBarUpload.setVisibility(View.GONE);
                    binding.progressBarUpload.setIndeterminate(false);
                    binding.buttonUploadFile.setEnabled(true);
                    binding.buttonSelectFile.setEnabled(true);

                    if (success) {
                        Log.i(TAG, "File saved successfully to: " + targetFile.getAbsolutePath());
                        binding.textViewUploadStatus.setText("File uploaded: " + finalUserVisibleFileName);
                        Toast.makeText(context, "File uploaded successfully!", Toast.LENGTH_LONG).show();

                        // *** Notify FirstFragment via ViewModel ***
                        fileUploadViewModel.signalNewFileUploaded(targetFile.getName());

                        // Optional: Navigate back or to FirstFragment if desired
                        // NavHostFragment.findNavController(UploadFragment.this).popBackStack();
                        // or navigate to FirstFragment if it's not the previous screen
                        // NavHostFragment.findNavController(UploadFragment.this).navigate(R.id.action_UploadFragment_to_FirstFragment); // Replace with your actual action ID

                    } else {
                        binding.textViewUploadStatus.setText("Upload failed for " + finalUserVisibleFileName);
                        if (externalAppFilesDir != null) { // externalAppFilesDir should be checked earlier
                            Toast.makeText(context, "Failed to upload file.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean copyFileToInternalStorage(@NonNull Context context, Uri sourceUri, File destinationFile) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream for URI: " + sourceUri);
                return false;
            }
            outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            Log.d(TAG, "File copy successful to " + destinationFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error copying file", e);
            if (destinationFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                destinationFile.delete();
            }
            return false;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing streams", e);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}