package com.mmsoftware.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileContentManipulationService {

    private static final Pattern VARIABLES_CATCH_PATTERN = Pattern.compile("\\{.*?\\}");

    public List<String> extractVariables(String line) {
        return VARIABLES_CATCH_PATTERN.matcher(line)
                .results()
                .map(MatchResult::group)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean isAnyVariablePresent(String text) {
        return VARIABLES_CATCH_PATTERN
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .findAny()
                .isPresent();
    }
}
