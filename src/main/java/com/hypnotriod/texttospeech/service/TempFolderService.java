package com.hypnotriod.texttospeech.service;

import com.hypnotriod.texttospeech.constants.Configurations;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TempFolderService {

    @Autowired
    private FilesManagementService filesManagementService;

    private HashSet<String> alreadyCopiedFilesToTempFolder = new HashSet<>();

    public String add(String folder, String fileName) {
        if (!alreadyCopiedFilesToTempFolder.contains(fileName)) {
            copyFileToTempFolder(folder, fileName);
        }
        return Configurations.PATH_TEMP_FOLDER + fileName;
    }

    public void remove(String folder, String fileName) {
        alreadyCopiedFilesToTempFolder.remove(fileName);
        filesManagementService.removeFile(folder + fileName);
    }

    public void untrack(String fileName) {
        alreadyCopiedFilesToTempFolder.remove(fileName);
    }

    private void copyFileToTempFolder(String folder, String fileName) {
        filesManagementService.copyFile(
                folder + fileName,
                Configurations.PATH_TEMP_FOLDER + fileName);
        alreadyCopiedFilesToTempFolder.add(fileName);
    }
}
