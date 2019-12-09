package com.hypnotriod.texttospeech.service;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Илья
 */
public class FilesManagementService {
    public ArrayList<File> getFilesFromFolder(String folderPath, String extension) {
        ArrayList<File> result = new ArrayList<>();
        File folder = new File(folderPath);

        if (folder.exists()) {
            for (final File file : folder.listFiles()) {
                if (file.getName().endsWith(extension)) {
                    result.add(file);
                }
            }
        }

        return result;
    }
}
