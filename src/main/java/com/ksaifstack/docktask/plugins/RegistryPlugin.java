package com.ksaifstack.docktask.plugins;

public record RegistryPlugin(
    String name,
    String version,
    String description,
    String author,
    String type,
    String url,
    String downloadUrl
) {}
