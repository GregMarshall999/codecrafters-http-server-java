package app.domain.request;

public class Request {
    private final HttpMethod method;
    private final String endpoint;
    private final String httpVersion;

    public Request(HttpMethod method, String endpoint, String httpVersion) {
        this.method = method;
        this.endpoint = endpoint;
        this.httpVersion = httpVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getHttpVersion() {
        if(httpVersion == null) return "HTTP/1.1";

        return httpVersion;
    }
}
