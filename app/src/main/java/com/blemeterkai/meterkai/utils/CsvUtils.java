package com.blemeterkai.meterkai.utils;

import android.content.Context;
import android.util.Log;

import com.blemeterkai.meterkai.data.parser.CSVParser;
// Keep AssetUtils if it's used for login.csv copying

import java.io.File;
import java.util.Arrays;

public class CsvUtils {

    private static final String TAG = "CsvUtils";

    private final Context appContext;
    private final File internalFilesDir;
    private final File externalFilesDir;
    private final File cacheDir;

    public static final String LOGIN_CSV_FILENAME = "login.csv";
    public static final String METER_CSV_FILENAME = "meter.csv";
    public static final String PRINTER_CSV_FILENAME = "printer.csv";
    public static final String RATE_CSV_FILENAME = "rate.csv";

    private static final String[] DEFAULT_LOGIN_ACCOUNTS_DATA = {
            "admin,admin,0",
            "manager,manager,1",
            "engineer,engineer,2",
            "user,user,3",
            "guest,guest,3"
    };
    private static final String DEFAULT_LOGIN_HEADER_USERNAME = "username";
    private static final String DEFAULT_LOGIN_HEADER_PASSWORD = "password";
    private static final String DEFAULT_LOGIN_HEADER_AUTH_LEVEL = "auth_level";

    public CsvUtils(Context context) {
        this.appContext = context.getApplicationContext();
        this.internalFilesDir = this.appContext.getFilesDir();
        this.externalFilesDir = this.appContext.getExternalFilesDir(null);
        this.cacheDir = this.appContext.getCacheDir();

        Log.d(TAG, "Internal files dir: " + internalFilesDir.getAbsolutePath());
        if (externalFilesDir != null) {
            Log.d(TAG, "External files dir: " + externalFilesDir.getAbsolutePath());
        } else {
            Log.w(TAG, "External files directory is not available.");
        }
        Log.d(TAG, "Cache dir: " + cacheDir.getAbsolutePath());

        // --- MODIFICATION: Call copyInitialAssets() again, but it will be specific ---
        copyInitialAssets();
        Log.i(TAG, "CsvUtils initialized. Initial asset check performed for login.csv.");
    }

    // --- MODIFICATION: copyInitialAssets now ONLY handles login.csv ---
    private void copyInitialAssets() {
        // Only ensure login.csv is copied from assets to internal storage if it doesn't exist.
        // Meter, printer, and rate CSVs are expected to be user-uploaded.
        ensureAssetCopied(LOGIN_CSV_FILENAME, internalFilesDir, "csv/" + LOGIN_CSV_FILENAME);
        Log.i(TAG, "Initial asset check for login.csv complete.");
    }

    // --- MODIFICATION: ensureAssetCopied is kept but might be simplified if only for login.csv ---
    // Added 'assetPathInAssetsFolder' parameter for clarity
    private void ensureAssetCopied(String targetFilename, File destinationDir, String assetPathInAssetsFolder) {
        File destinationFile = new File(destinationDir, targetFilename);
        if (!destinationFile.exists()) {
            // Assuming AssetUtils.copyFileFromAssets(Context context, String assetName, File targetDirectory, String targetFilename)
            boolean copied = AssetUtils.copyFileFromAssets(appContext, assetPathInAssetsFolder, destinationDir, targetFilename);
            if (copied) {
                Log.i(TAG, "Copied asset '" + assetPathInAssetsFolder + "' to " + destinationFile.getAbsolutePath());
            } else {
                Log.e(TAG, "Failed to copy asset '" + assetPathInAssetsFolder + "' to " + destinationFile.getAbsolutePath());
                // If login.csv copy from assets fails, getProcessedLoginCsv() will create it with defaults.
            }
        } else {
            Log.d(TAG, "Asset '" + targetFilename + "' already exists at " + destinationFile.getAbsolutePath() + ". No copy needed from assets.");
        }
    }

    public CSVParser getProcessedLoginCsv() {
        // 1. Check internal storage for login.csv (it might have been copied by copyInitialAssets)
        CSVParser internalLoginCsv = new CSVParser(LOGIN_CSV_FILENAME, internalFilesDir);

        // readFile will try to load it. If it fails (e.g., asset copy failed or file corrupted),
        // then create with defaults.
        if (!internalLoginCsv.readFile(LOGIN_CSV_FILENAME)) {
            Log.i(TAG, LOGIN_CSV_FILENAME + " not found, empty, or unreadable in internal storage (after asset check). Creating with default accounts.");
            String header = DEFAULT_LOGIN_HEADER_USERNAME + "," +
                    DEFAULT_LOGIN_HEADER_PASSWORD + "," +
                    DEFAULT_LOGIN_HEADER_AUTH_LEVEL;
            internalLoginCsv.New(header);
            for (String accountData : DEFAULT_LOGIN_ACCOUNTS_DATA) {
                internalLoginCsv.Add(accountData);
            }
            if (!internalLoginCsv.writeFile()) {
                Log.e(TAG, "Failed to write newly created default " + LOGIN_CSV_FILENAME + " to internal storage.");
                return new CSVParser(null, internalFilesDir); // Return an empty one
            }
            Log.i(TAG, "Successfully created default " + LOGIN_CSV_FILENAME + " in internal storage.");
        } else {
            Log.d(TAG, LOGIN_CSV_FILENAME + " loaded successfully from internal storage.");
        }

        // 2. Check for an external (user-uploaded) login.csv and process updates
        // This logic remains the same.
        if (externalFilesDir == null) {
            Log.w(TAG, "External storage not available, skipping check for external " + LOGIN_CSV_FILENAME);
            return internalLoginCsv;
        }

        File externalLoginFile = new File(externalFilesDir, LOGIN_CSV_FILENAME);
        if (externalLoginFile.exists() && externalLoginFile.isFile()) {
            Log.i(TAG, "Found an external " + LOGIN_CSV_FILENAME + " at " + externalLoginFile.getAbsolutePath() + ". Processing updates...");
            CSVParser externalLoginCsv = new CSVParser(LOGIN_CSV_FILENAME, externalFilesDir);

            if (externalLoginCsv.Columns() == null || externalLoginCsv.Columns().isEmpty()) {
                Log.e(TAG, "External " + LOGIN_CSV_FILENAME + " exists but could not be parsed or is empty.");
            } else {
                boolean internalLoginWasUpdated = processLoginUpdates(internalLoginCsv, externalLoginCsv);

                if (internalLoginWasUpdated) {
                    if (internalLoginCsv.writeFile()) {
                        Log.i(TAG, "Internal " + LOGIN_CSV_FILENAME + " updated successfully from external source.");
                    } else {
                        Log.e(TAG, "Failed to write updates to internal " + LOGIN_CSV_FILENAME);
                    }
                }

                if (externalLoginFile.delete()) {
                    Log.i(TAG, "Successfully deleted external " + LOGIN_CSV_FILENAME + " after processing.");
                } else {
                    Log.w(TAG, "Failed to delete external " + LOGIN_CSV_FILENAME + " after processing.");
                }
            }
        } else {
            Log.d(TAG, "No external " + LOGIN_CSV_FILENAME + " found at " + (externalLoginFile != null ? externalLoginFile.getAbsolutePath() : "unknown path") + " to process.");
        }
        return internalLoginCsv;
    }

    // processLoginUpdates remains the same
    private boolean processLoginUpdates(CSVParser targetLoginCsv, CSVParser sourceLoginCsv) {
        // ... (No change from your original working version) ...
        boolean wasModified = false;
        sourceLoginCsv.Reset();

        while (true) {
            String sourceUsername = sourceLoginCsv.Row(DEFAULT_LOGIN_HEADER_USERNAME);
            if (sourceUsername == null || sourceUsername.isEmpty()) {
                break;
            }
            String sourcePassword = sourceLoginCsv.Column(DEFAULT_LOGIN_HEADER_PASSWORD);
            String sourceAuthLevel = sourceLoginCsv.Column(DEFAULT_LOGIN_HEADER_AUTH_LEVEL);

            if (sourcePassword == null) sourcePassword = "";
            if (sourceAuthLevel == null) sourceAuthLevel = "3";

            if (targetLoginCsv.Find(DEFAULT_LOGIN_HEADER_USERNAME, sourceUsername)) {
                String currentTargetAuthLevelStr = targetLoginCsv.Column(DEFAULT_LOGIN_HEADER_AUTH_LEVEL);
                int currentTargetAuthLevel = -1;
                try {
                    if (currentTargetAuthLevelStr != null && !currentTargetAuthLevelStr.isEmpty()) {
                        currentTargetAuthLevel = Integer.parseInt(currentTargetAuthLevelStr);
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Could not parse auth_level '" + currentTargetAuthLevelStr + "' for user " + sourceUsername + " in target CSV.");
                }

                boolean needsPasswordUpdate = false;
                if (currentTargetAuthLevel != -1 && currentTargetAuthLevel < 2) {
                    String currentTargetPassword = targetLoginCsv.Column(DEFAULT_LOGIN_HEADER_PASSWORD);
                    if (!sourcePassword.equals(currentTargetPassword)) {
                        needsPasswordUpdate = true;
                    }
                } else if (currentTargetAuthLevel == -1) {
                    Log.w(TAG, "Skipping update for " + sourceUsername + " due to unreadable current auth_level in target.");
                }

                if (needsPasswordUpdate) {
                    if (targetLoginCsv.Update(sourcePassword, DEFAULT_LOGIN_HEADER_PASSWORD)) {
                        Log.i(TAG, "Updated password for account: " + sourceUsername + " in internal login.csv.");
                        wasModified = true;
                    } else {
                        Log.e(TAG, "Failed to update password for account: " + sourceUsername);
                    }
                } else {
                    Log.d(TAG, "No update needed for existing account: " + sourceUsername);
                }
            } else {
                String newAccountData = sourceUsername + "," + sourcePassword + "," + sourceAuthLevel;
                targetLoginCsv.Add(newAccountData);
                Log.i(TAG, "Added new account from external source: " + sourceUsername);
                wasModified = true;
            }
        }
        return wasModified;
    }

    // getCsvParserForFile remains the same
    private CSVParser getCsvParserForFile(String filename, File directory) {
        // ... (No change from your previous version) ...
        if (directory == null) {
            Log.w(TAG, "Directory is null, cannot get CSV parser for " + filename);
            return new CSVParser(filename, directory);
        }
        CSVParser parser = new CSVParser(filename, directory);
        if (new File(directory, filename).exists() && (parser.Columns() == null || parser.Columns().isEmpty())) {
            Log.w(TAG, filename + " exists in " + directory.getAbsolutePath() + " but could not be parsed or is empty.");
        } else if (!new File(directory, filename).exists()){
            Log.i(TAG, filename + " does not exist in " + directory.getAbsolutePath() + ". Parser will be empty or for a non-existent file.");
        } else {
            Log.d(TAG, filename + " loaded successfully from " + directory.getAbsolutePath());
        }
        return parser;
    }


    // --- MODIFICATION: Meter, Printer, Rate CSVs are expected from user upload (externalFilesDir) ---
    public CSVParser getMeterCsv() {
        if (externalFilesDir == null) {
            Log.w(TAG, "External files directory not available for " + METER_CSV_FILENAME);
            return new CSVParser(METER_CSV_FILENAME, internalFilesDir); // Fallback or error placeholder
        }
        return getCsvParserForFile(METER_CSV_FILENAME, externalFilesDir);
    }

    public CSVParser getPrinterCsv() {
        if (externalFilesDir == null) {
            Log.w(TAG, "External files directory not available for " + PRINTER_CSV_FILENAME);
            return new CSVParser(PRINTER_CSV_FILENAME, internalFilesDir); // Fallback or error placeholder
        }
        return getCsvParserForFile(PRINTER_CSV_FILENAME, externalFilesDir);
    }

    public CSVParser getRateCsv() {
        if (externalFilesDir == null) {
            Log.w(TAG, "External files directory not available for " + RATE_CSV_FILENAME);
            return new CSVParser(RATE_CSV_FILENAME, internalFilesDir); // Fallback or error placeholder
        }
        return getCsvParserForFile(RATE_CSV_FILENAME, externalFilesDir);
    }

    // loadRatiosFromRateCsv remains the same, relying on getRateCsv()
    public float[] loadRatiosFromRateCsv(String[] rateTableColumnKeys) {
        // ... (No change from your previous version) ...
        float[] ratios = new float[rateTableColumnKeys.length];
        Arrays.fill(ratios, 0.0f);

        CSVParser rateCsv = getRateCsv();

        if (rateCsv == null || rateCsv.size() == 0 || (rateCsv.Columns() != null && rateCsv.Columns().isEmpty())) {
            Log.w(TAG, RATE_CSV_FILENAME + " is not available, empty, or unreadable (likely not uploaded). Cannot load ratios.");
            return ratios;
        }
        final int dataRowIndex = 0;
        if (rateCsv.Line(dataRowIndex) == null) {
            Log.w(TAG, "No data row at index " + dataRowIndex + " in " + RATE_CSV_FILENAME);
            return ratios;
        }
        for (int i = 0; i < rateTableColumnKeys.length; i++) {
            String columnKey = rateTableColumnKeys[i];
            if (columnKey == null || columnKey.isEmpty()) {
                Log.w(TAG, "Empty or null column key at index " + i + " for rate.csv");
                continue;
            }
            String cellValue = rateCsv.Cell(dataRowIndex, columnKey);
            if (cellValue != null && !cellValue.isEmpty()) {
                try {
                    ratios[i] = Float.parseFloat(cellValue);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Could not parse float from value '" + cellValue + "' in " + RATE_CSV_FILENAME + " for column '" + columnKey + "'. Defaulting to 0.0f.", e);
                }
            } else {
                Log.w(TAG, "No value found in " + RATE_CSV_FILENAME + " for column '" + columnKey + "' in data row " + dataRowIndex + ". Defaulting to 0.0f.");
            }
        }
        return ratios;
    }
}