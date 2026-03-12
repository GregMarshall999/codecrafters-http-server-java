package app.domain.request;

public record Request(HttpMethod method, String endpoint, String httpVersion) {}
