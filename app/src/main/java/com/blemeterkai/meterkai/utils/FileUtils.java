package com.blemeterkai.meterkai.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {

    /**
     * Deletes a file within a specified folder.
     * @param name The name of the file to delete.
     * @param folder The folder containing the file.
     */
    public static void deleteFile(final String name, final File folder) {
        if (folder == null || name == null || name.isEmpty()) {
            // Optional: Add logging for invalid parameters
            return;
        }
        File file = new File(folder, name);
        if (file.exists()) {
            if (!file.delete()) {
                // Optional: Add logging if delete fails
                System.err.println("Failed to delete file: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * Writes string data to a file in a specified folder using SHIFT_JIS encoding.
     * @param data The string data to write.
     * @param name The name of the file.
     * @param folder The folder where the file will be created.
     * @return true if writing was successful, false otherwise.
     */
    public static boolean writeFile(String data, String name, File folder) {
        if (data == null || name == null || name.isEmpty() || folder == null) {
            // Optional: Add logging for invalid parameters
            return false;
        }
        File file = new File(folder, name);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "SHIFT_JIS"))) {
            bw.write(data);
            // bw.close() is automatically called by try-with-resources
        } catch (Exception e) {
            e.printStackTrace(); // Consider more robust error handling/logging
            return false;
        }
        return true;
    }

    /**
     * Appends string data to a file.
     * @param data The string data to append.
     * @param file The file to append to.
     */
    public static void writeFile(String data, File file) {
        if (data == null || file == null) {
            // Optional: Add logging
            return;
        }
        // try-with-resources
        try (FileWriter writer = new FileWriter(file, true)) { // true for append mode
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace(); // Consider more robust error handling/logging
        }
    }

    /**
     * Writes byte array data to a binary file.
     * @param data The byte array to write.
     * @param filePath The full path to the file.
     */
    public static void writeBinaryFile(byte[] data, String filePath) {
        if (data == null || filePath == null || filePath.isEmpty()) {
            // Optional: Add logging
            return;
        }
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            out.write(data);
        } catch (IOException e) { // Catch IOException specifically
            e.printStackTrace(); // Consider more robust error handling/logging
        }
    }

    /**
     * Reads data from a binary file into a byte array.
     * Note: This method assumes the 'data' byte array is pre-allocated and appropriately sized.
     * @param data The byte array to read data into.
     * @param filePath The full path to the file.
     * @return The total number of bytes read into the buffer, or -1 if there is no more data
     *         because the end of the file has been reached.
     */
    public static int readBinaryFile(byte[] data, String filePath) {
        if (data == null || filePath == null || filePath.isEmpty()) {
            // Optional: Add logging
            return -1; // Or throw IllegalArgumentException
        }
        try (FileInputStream in = new FileInputStream(filePath)) {
            return in.read(data);
        } catch (IOException e) { // Catch IOException specifically
            e.printStackTrace(); // Consider more robust error handling/logging
            return -1; // Indicate error
        }
    }

    public static String readFile(final String name, File folder) {
        StringBuffer buffer = new StringBuffer();
        File file = new File(folder, name);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "SHIFT_JIS"))) {
                while (true) {
                    String read = br.readLine();
                    if (read != null) {
                        buffer.append("\n" + read);
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }
}