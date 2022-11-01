package com.mmsoftware.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfo {
    private String fileInitialContent;
    private String filePath;
    private boolean isChanged;
}
