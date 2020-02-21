package com.hypnotriod.texttospeech.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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

    public String readFileToString(String filePath) {
        String result = null;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                result = FileUtils.readFileToString(file);
            } catch (IOException ex) {
                Logger.getLogger(FilesManagementService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public void writeStringToFile(String filePath, String content) {
        try {
            FileUtils.writeStringToFile(new File(filePath), content);
        } catch (IOException ex) {
            Logger.getLogger(FilesManagementService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean removeFile(String path) {
        File file = new File(path);
        boolean result = false;
        try {
            result = Files.deleteIfExists(file.toPath());
        } catch (IOException ex) {
            Logger.getLogger(FilesManagementService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void copyFile(String formPath, String toPath) {
        File original = new File(formPath);
        File copied = new File(toPath);

        createFolderIfNotExist(FilenameUtils.getPath(toPath));

        try {
            FileUtils.copyFile(original, copied);
        } catch (IOException ex) {
            Logger.getLogger(FilesManagementService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createFolderIfNotExist(String path) {
        File generatedFolder = new File(path);
        if (!generatedFolder.exists()) {
            generatedFolder.mkdir();
        }
    }

    public void removeFolderIfExist(String path) {
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(FilesManagementService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
