package bgu.spl.net.messages;

public class UserListMessage implements Message {
    private final short opCode = 7;

    @Override
    public short getOpCode() {
        return opCode;
    }
}
