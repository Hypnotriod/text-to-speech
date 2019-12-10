package com.hypnotriod.texttospeech.constants;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import java.util.regex.Pattern;

/**
 *
 * @author Ilya Pikin
 */
public class Configurations {

    public static final String APP_NAME = "Text To Speech Util v0.0.3";
    public static final int APP_WITH_MIN = 500;
    public static final int APP_HEIGHT_MIN = 350;

    public static final String PATH_GENERATED_PHRASES_FOLDER = "phrases/";
    public static final String PATH_TEMP_FOLDER = "temp/";
    public static final String FILE_EXTENSION_MP3 = ".mp3";
    public static final String GROUP_PHRASE_SEPARATOR = " - ";

    public static final Pattern FILE_NAME_REGEXP_PATTERN = Pattern.compile("[\\p{L}\\p{N}' ]");

    public static final float SPEAKING_RATE = 1f;

    public static final SsmlVoiceGender[] VOICE_GENDERS = {
        SsmlVoiceGender.MALE, SsmlVoiceGender.FEMALE, SsmlVoiceGender.NEUTRAL
    };
}
