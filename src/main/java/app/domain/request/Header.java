package app.domain.request;

public record Header(String host, String acceptedMediaTypes, String userAgent) {}
