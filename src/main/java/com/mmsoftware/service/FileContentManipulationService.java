package com.mmsoftware.service;

import com.mmsoftware.model.VARIABLE_PATTERN;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileContentManipulationService {

    private final AppProperties appProperties;

    public List<String> extractVariables(String line) {
        List<String> extractedVariables = new ArrayList<>();
        appProperties.getEnabledVariables().forEach(
                pattern -> extractedVariables.addAll(
                        pattern.regex.matcher(line)
                                .results()
                                .map(MatchResult::group)
                                .distinct()
                                .collect(Collectors.toList())
                )
        );
        return extractedVariables;
    }

    public boolean isAnyVariablePresent(String text) {
        for (VARIABLE_PATTERN pattern : appProperties.getEnabledVariables()) {
            boolean isPresent = pattern.regex
                    .matcher(text)
                    .results()
                    .map(MatchResult::group)
                    .findAny()
                    .isPresent();
            if (isPresent) {
                return true;
            }
        }
        return false;
    }
}
