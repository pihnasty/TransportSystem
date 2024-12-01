package org.pom.utils.yaml;

import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class SettingsManager {

    private static final String SETTINGS_FILE = "settings.yaml";
    private Settings settings;

    public SettingsManager() {
        loadSettings();
    }

    // Load settings from YAML file
    public void loadSettings() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = SettingsManager.class.getClassLoader().getResourceAsStream("settings.yaml")) {
            if (inputStream == null) {
                throw new RuntimeException("settings.yaml not found in resources");
            }
            settings = yaml.loadAs(inputStream, Settings.class); //yaml.load(inputStream);
            System.out.println(settings);  // Print loaded YAML content
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save settings to YAML file
    public void saveSettings() {
        Yaml yaml = new Yaml();
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
        try (FileOutputStream outputStream = new FileOutputStream(SETTINGS_FILE)) {
            yaml.dump(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAppName() {
        return settings.app.name;
    }

    public Locale getLocale() {
        return settings.app.locale;
    }

    public int getMinWidth() {
        return settings.prepareDataTableFormat.minWidth;
    }

    public Settings.App getApp() {
        return settings.app;
    }

    public Settings.PrepareDataTableFormat getPrepareDataTableFormat() {
        return settings.prepareDataTableFormat;
    }

    public void setAppName(String name) {
        settings.app.name = name;
    }

    public static void main(String[] args) {
        SettingsManager settingsManager = new SettingsManager();

        // Example of reading and updating settings
        System.out.println("App Name: " + settingsManager.getAppName());
        settingsManager.setAppName("NewAppName");
        settingsManager.saveSettings();
    }

    // Define the settings structure (adjust as needed)
    public static class Settings {
        public App app;
        public PrepareDataTableFormat prepareDataTableFormat;

        @Getter
        public static class App {
            public String name;
            public String version;
            public Locale locale;
            public String initTransportSystemFile;
        }

        @Getter
        public static class PrepareDataTableFormat {
            public int minWidth;
            public String cellFormat;
        }
    }
}
