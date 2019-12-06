package com.hypnotriod.texttospeech.utils;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author IPikin
 */
public class TextUtil {

    public static final Pattern FILE_NAME_REGEXP_PATTERN = Pattern.compile("[\\p{L}\\p{N}\\-_\\. ]");

    public static String toProperFileName(String input) {
        StringBuilder result = new StringBuilder();
        String withoutAccent = Normalizer.normalize(input, Normalizer.Form.NFD);
        Matcher matcher = FILE_NAME_REGEXP_PATTERN.matcher(withoutAccent);
        
        while (matcher.find()) {
            result.append(matcher.group());
        }

        return result.toString();
    }
}
