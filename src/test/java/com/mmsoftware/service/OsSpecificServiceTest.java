package com.mmsoftware.service;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OsSpecificServiceTest {

    private final OsSpecificService osSpecificService = new OsSpecificService();

    @Test
    void getOsFamily() {
        OsSpecificService.OSFAMILY osFamily = osSpecificService.getOsFamily();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            assertEquals(OsSpecificService.OSFAMILY.WINDOWS, osFamily);
        }
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            assertEquals(OsSpecificService.OSFAMILY.MAC, osFamily);
        }
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            assertEquals(OsSpecificService.OSFAMILY.LINUX, osFamily);
        }
    }

    @Test
    void getHomeDirectory() {
        Optional<String> homeDirectory = osSpecificService.getHomeDirectory();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            assertTrue(homeDirectory.isPresent());
            assertThat(homeDirectory.get().toLowerCase()).contains("c:\\users\\");
        }
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            assertTrue(homeDirectory.isPresent());
            assertThat(homeDirectory.get().toLowerCase()).contains("/users/");
        }
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            assertTrue(homeDirectory.isPresent());
            assertThat(homeDirectory.get().toLowerCase()).contains("/home/");
        }
    }

    @Test
    void getLocalApplicationDataDirectory() {
        Optional<String> localApplicationDataDirectory = osSpecificService.getLocalApplicationDataDirectory();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            assertTrue(localApplicationDataDirectory.isPresent());
            assertThat(localApplicationDataDirectory.get().toLowerCase()).contains("appdata");
        }
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            assertTrue(localApplicationDataDirectory.isPresent());
            assertThat(localApplicationDataDirectory.get().toLowerCase()).contains("library/application support");
        }
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            assertTrue(localApplicationDataDirectory.isPresent());
            assertThat(localApplicationDataDirectory.get().toLowerCase()).contains("/.config");
        }
    }
}
