package bgu.spl.net.messages;

public class NotificationMessage implements Message {
    private final short opCode = 9;
    private short command;
    private String postingUser;
    private String content;

    public NotificationMessage(short command, String postingUser, String content) {
        this.command = command;
        this.postingUser = postingUser;
        this.content = content;
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public byte getCommand() {
        if (command == 0)
            return 0;
        else
            return 1;
    }
    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "opCode=" + opCode +
                ", command=" + command +
                ", postingUser='" + postingUser + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
