package bgu.spl.net.messages;

public class LogoutMessage implements Message {
    private final short opCode = 3;

    public LogoutMessage() {
    }

    @Override
    public short getOpCode() {
        return opCode;
    }
}
