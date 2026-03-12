package app.domain.request;

public record Header(String host, String userAgent, String acceptedMediaTypes) {}
