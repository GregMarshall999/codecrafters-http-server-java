package app.domain.response;

public record HttpResponse(
        String httpVersion,
        ResponseCode responseCode,
        ContentType contentType,
        int contentLength,
        String body
) {
    public byte[] getBytes() {
        String line = httpVersion + " " + responseCode.toString();

        if(contentType.equals(ContentType.NONE)) {
            line += "\r\n\r\n";
        }
        else {
            line += "\r\nContent-Type: " + contentType + "\r\nContent-Length: " + contentLength + "\r\n\r\n" + body;
        }

        return line.getBytes();
    }
}
