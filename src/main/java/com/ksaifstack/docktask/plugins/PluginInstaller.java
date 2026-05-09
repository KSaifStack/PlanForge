package com.ksaifstack.docktask.plugins;

import com.ksaifstack.docktask.model.UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginInstaller {

    private static final String PLUGINS_DIR = "Data/plugins/";
    private static final String PLUGINS_TXT = "Data/Plugins.txt";
    private static final String SERVICE_FILE =
            "META-INF/services/com.ksaifstack.docktask.plugins.DockTaskPlugin";

    public static boolean isInstalled(String name) {
        try {
            File f = new File(PLUGINS_TXT);
            if (!f.exists()) return false;
            List<String> lines = Files.readAllLines(f.toPath());
            for (String line : lines) {
                String[] parts = line.split("<SEP>");
                if (parts.length > 0 && parts[0].equals(name)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean install(RegistryPlugin plugin) {
        try {
            File dir = new File(PLUGINS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String safeName = plugin.name().replaceAll("[^a-zA-Z0-9.-]", "_") + ".jar";
            File jarFile = new File(dir, safeName);

            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(plugin.downloadUrl()))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                try (InputStream is = response.body(); FileOutputStream fos = new FileOutputStream(jarFile)) {
                    is.transferTo(fos);
                }

                // Append to Plugins.txt
                String entry = String.format("%s<SEP>%s<SEP>%s<SEP>%s\n",
                        plugin.name(), plugin.version(), plugin.url(), jarFile.getAbsolutePath());
                Files.writeString(Path.of(PLUGINS_TXT), entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                return true;
            } else {
                System.err.println("Failed to download plugin JAR: HTTP " + response.statusCode());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error installing plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads Data/Plugins.txt and returns the registry URL stored for the plugin name.
     * Format: name<SEP>version<SEP>url<SEP>jarPath
     */
    /**
     * Copies a JAR from the user's filesystem into {@code Data/plugins/}.
     * {@link PluginManager} loads jars from that folder on next app start.
     *
     * @param sourceJar a {@code .jar} file chosen by the user
     * @return the destination path, or {@code null} if the copy failed
     */
    public static Path installLocalJar(File sourceJar) {
        try {
            if (sourceJar == null || !sourceJar.isFile()) return null;
            String nm = sourceJar.getName();
            if (!nm.toLowerCase().endsWith(".jar")) return null;
            if (!looksLikeDockTaskPluginJar(sourceJar)) return null;

            File dir = new File(PLUGINS_DIR);
            if (!dir.exists() && !dir.mkdirs()) return null;

            Path dest = Path.of(PLUGINS_DIR, sanitizeJarFileName(nm));
            Files.copy(sourceJar.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest.toAbsolutePath();
        } catch (Exception e) {
            System.err.println("Error copying local plugin JAR: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Minimal validation that the JAR can be discovered by DockTask's ServiceLoader.
     * Requires the standard provider-configuration file and at least one non-empty
     * implementation class entry.
     */
    private static boolean looksLikeDockTaskPluginJar(File jar) {
        try (JarFile jf = new JarFile(jar)) {
            JarEntry entry = jf.getJarEntry(SERVICE_FILE);
            if (entry == null) return false;

            String text;
            try (InputStream is = jf.getInputStream(entry)) {
                text = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }

            for (String line : text.split("\\R")) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                // basic FQCN sanity check (cheap, avoids accepting random text)
                if (trimmed.contains(" ") || !trimmed.contains(".")) continue;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String sanitizeJarFileName(String name) {
        String cleaned = name.replaceAll("[\\\\/]", "_").trim();
        if (cleaned.isEmpty()) return "plugin.jar";
        return cleaned;
    }

    public static String getInstalledUrl(String name) {
        try {
            File f = new File(PLUGINS_TXT);
            if (!f.exists()) return null;
            List<String> lines = Files.readAllLines(f.toPath());
            for (String line : lines) {
                String[] parts = line.split("<SEP>");
                if (parts.length >= 3 && parts[0].equals(name)) {
                    return parts[2];
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
