package tutorial.schema;

public class Tweet {
    private String username;
    private String content;

    public Tweet(String username, String content) {
        this.username = username;
        this.content = content;
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
}
