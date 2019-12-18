package com.hypnotriod.texttospeech.constants;

import com.hypnotriod.texttospeech.service.AsyncService;
import com.hypnotriod.texttospeech.service.FilesManagementService;
import com.hypnotriod.texttospeech.service.LoggerService;
import com.hypnotriod.texttospeech.service.MediaPlayerService;
import com.hypnotriod.texttospeech.service.SSLService;
import com.hypnotriod.texttospeech.service.SettingsService;
import com.hypnotriod.texttospeech.service.TTSFileGeneratorService;
import com.hypnotriod.texttospeech.service.TempFolderService;

/**
 *
 * @author Ilya Pikin
 */
public class Services {

    public static final AsyncService ASYNC_SERVICE = new AsyncService();
    public static final FilesManagementService FILES_MANAGEMENT_SERVICE = new FilesManagementService();
    public static final LoggerService LOGGER_SERVICE = new LoggerService();
    public static final MediaPlayerService MEDIA_PLAYER_SERVICE = new MediaPlayerService();
    public static final SSLService SSL_SERVICE = new SSLService();
    public static final TTSFileGeneratorService TTS_FILE_GENERATOR_SERVICE = new TTSFileGeneratorService();
    public static final TempFolderService TEMP_FOLDER_SERVICE = new TempFolderService();
    public static final SettingsService SETTINGS_SERVICE = new SettingsService();
}
