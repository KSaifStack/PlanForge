package com.ksaifstack.docktask.plugins;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class PluginRegistryService {
    private static final String REGISTRY_URL = "https://raw.githubusercontent.com/KSaifStack/DockTask-Plugins/main/plugin-registry.json";

    public static List<RegistryPlugin> fetch() {
        List<RegistryPlugin> plugins = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            String cacheBusterUrl = REGISTRY_URL + "?t=" + System.currentTimeMillis();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(cacheBusterUrl))
                    .header("Cache-Control", "no-cache")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                plugins = parseJson(json);
            } else {
                System.err.println("Failed to fetch plugin registry: HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching plugin registry: " + e.getMessage());
        }
        return plugins;
    }

    private static List<RegistryPlugin> parseJson(String json) {
        List<RegistryPlugin> plugins = new ArrayList<>();
        // Simple manual parsing to avoid external dependencies
        Pattern pattern = Pattern.compile("\\{[^}]*\"name\"\\s*:\\s*\"([^\"]+)\"[^}]*\"description\"\\s*:\\s*\"([^\"]+)\"[^}]*\"version\"\\s*:\\s*\"([^\"]+)\"[^}]*\"author\"\\s*:\\s*\"([^\"]+)\"[^}]*\"type\"\\s*:\\s*\"([^\"]+)\"[^}]*\"url\"\\s*:\\s*\"([^\"]+)\"[^}]*\"downloadUrl\"\\s*:\\s*\"([^\"]+)\"[^}]*\\}");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            plugins.add(new RegistryPlugin(
                    matcher.group(1), // name
                    matcher.group(3), // version
                    matcher.group(2), // description
                    matcher.group(4), // author
                    matcher.group(5), // type
                    matcher.group(6), // url
                    matcher.group(7)  // downloadUrl
            ));
        }
        return plugins;
    }
}
