package tutorial.schema;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Tweet {
    private String username;
    private String content;
    private String timestamp;

    public Tweet(String username, String content) {
        this.username = username;
        this.content = content;
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_TIME);
    }

    public Tweet() {}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
