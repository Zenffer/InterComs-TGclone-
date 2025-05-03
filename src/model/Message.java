package model;

public class Message {
    public String from;
    public String to;
    public String content;
    public String timestamp;

    public Message(String from, String to, String content, String timestamp) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.timestamp = timestamp;
    }
}
