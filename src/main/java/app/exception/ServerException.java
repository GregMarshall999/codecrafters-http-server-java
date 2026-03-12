package app.exception;

public class ServerException extends Exception {
    private final Severity severity;

    public ServerException(String message, Severity severity) {
        super(message);
        this.severity = severity;
    }

    public Severity getSeverity() {
        return severity;
    }
}
