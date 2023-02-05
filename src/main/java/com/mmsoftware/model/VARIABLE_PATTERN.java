package com.mmsoftware.model;

import java.util.regex.Pattern;

public enum VARIABLE_PATTERN {
    CURLY_BRACKETS(Pattern.compile("\\{.*?\\}")), //OK
    ANGLE_BRACKETS(Pattern.compile("\\<.*?\\>")), //OK
    SQUARE_BRACKETS(Pattern.compile("\\[.*?\\]")), //OK
    DOLLAR_BRACKETS(Pattern.compile("\\$\\(.*?\\)")), //OK
    PERCENTAGE_BRACKETS(Pattern.compile("\\%.*?\\%")), //OK
    HASH_BRACKETS(Pattern.compile("\\#.*?\\#")); //OK

    public final Pattern regex;

    VARIABLE_PATTERN(Pattern regex) {
        this.regex = regex;
    }
}
