package bgu.spl.net.messages;

public class StatMessage implements Message {
    private final short opCode = 8;
    private String userName;

    public StatMessage(String userName) {
        this.userName = userName;
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public String getUserName() {
        return userName;
    }
}
