package com.hypnotriod.texttospeech.utils;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author IPikin
 */
public class FileUtil {

    public static ArrayList<File> findFilesInFolder(String folderPath, String extension) {
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
