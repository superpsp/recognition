package org.psp.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecognitionTools {
    final static Logger LOG = LoggerFactory.getLogger(RecognitionTools.class.getName());

    public static boolean isMatched (String source, String pattern, boolean isCaseSencitive) {
        Pattern patternToCase = Pattern.compile(isCaseSencitive ? pattern : pattern.toLowerCase());
        Matcher matcher = patternToCase.matcher(isCaseSencitive ? source : source.toLowerCase());
        return matcher.find();
    }
}
