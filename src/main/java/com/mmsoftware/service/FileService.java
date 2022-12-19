package com.mmsoftware.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final AppPropertiesService appPropertiesService;

    public ObservableList<String> getAllFilesFromDirectory(String folderAbsolutePath) {
        try {
            return Files.list(Paths.get(folderAbsolutePath))
                    .filter(path -> !Files.isDirectory(path))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(s -> isFileNameContainsOneOfTheGivenExtensions(s, appPropertiesService.getSupportedExtensions()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        } catch (IOException exception) {
            log.debug(String.format("Unexpected problem while loading the folder: <%s>", folderAbsolutePath), exception);
        }
        return FXCollections.observableArrayList();
    }

    public boolean isFileNameContainsOneOfTheGivenExtensions(String fileName, List<String> allowedExtensions) {
        for (String extension : allowedExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
