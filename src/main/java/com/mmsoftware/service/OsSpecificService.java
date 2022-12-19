package com.mmsoftware.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class OsSpecificService {

    private static final String MAC_APP_DATA_FOLDER = "Library/Application Support/";
    private static final String WINDOWS_APP_DATA_VARIABLE_NAME = "AppData";
    private static final String USER_HOME_VARIABLE_NAME = "user.home";
    private static final String OS_NAME_VARIABLE_NAME = "os.name";
    private static final String COM_MMSOFTWARE = "com.mmsoftware";

    public enum OSFAMILY {
        WINDOWS,
        LINUX,
        MAC,
        UNKNOWN
    }

    public OSFAMILY getOsFamily() {
        String osName = Optional.ofNullable(System.getProperty(OS_NAME_VARIABLE_NAME))
                .orElse(OSFAMILY.UNKNOWN.toString());
        return Arrays.stream(OSFAMILY.values())
                .filter(os -> osName.toLowerCase().contains(os.name().toLowerCase()))
                .findFirst()
                .orElse(OSFAMILY.UNKNOWN);
    }

    public Optional<String> getHomeDirectory() {
        return Optional.ofNullable(System.getProperty(USER_HOME_VARIABLE_NAME));
    }

    public Optional<String> getLocalApplicationDataDirectory() {
        OSFAMILY osFamily = getOsFamily();
        if (osFamily.equals(OSFAMILY.WINDOWS)) {
            return Optional.of(System.getenv(WINDOWS_APP_DATA_VARIABLE_NAME))
                    .map(path -> String.join("\\",
                            path, COM_MMSOFTWARE,
                            "Injector\\")
                    );
        } else if (osFamily.equals(OSFAMILY.MAC)) {
            return Optional.ofNullable(getHomeDirectory())
                    .map(home -> String.join("/", home.orElseThrow(
                            () -> new IllegalStateException("Cannot find system home directory!")),
                            MAC_APP_DATA_FOLDER,
                            COM_MMSOFTWARE,
                            "Injector/"
                            )
                    );
        } else if (osFamily.equals(OSFAMILY.LINUX)) {
            throw new UnsupportedOperationException("Linux system is not yet supported!");
        }
        return Optional.empty();
    }
}
