package com.hypnotriod.texttospeech.constants;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;

/**
 *
 * @author Ilya Pikin
 */
public class Configurations {

    public static final String APP_NAME = "Text To Speech Util v0.0.2";
    public static final int APP_WITH_MIN = 500;
    public static final int APP_HEIGHT_MIN = 350;
    
    public static final String PATH_GENERATED_PHRASES_FOLDER = "phrases/";
    public static final String FILE_EXTENSION_MP3 = ".mp3"; 

    public static final float SPEAKING_RATE = 1f;

    public static final SsmlVoiceGender[] VOICE_GENDERS = {
        SsmlVoiceGender.MALE, SsmlVoiceGender.FEMALE, SsmlVoiceGender.NEUTRAL
    };
}
