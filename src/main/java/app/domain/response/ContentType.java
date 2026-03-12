package app.domain.response;

public enum ContentType {
    TEXT_PLAIN,
    NONE;

    @Override
    public String toString() {
        if(this.equals(NONE)) return "";

        return this.name().toLowerCase().replace("_", "/");
    }
}
