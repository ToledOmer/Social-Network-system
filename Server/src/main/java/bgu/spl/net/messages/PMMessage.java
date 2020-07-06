package bgu.spl.net.messages;

import bgu.spl.net.api.User;

public class PMMessage implements Message {
    private final short opCode = 6;
    private String content;
    private String userName;
    private String poster;


    public PMMessage(String content, String userName) {
        this.content = content;
        this.userName = userName;
    }

    public short getOpCode() {
        return opCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
