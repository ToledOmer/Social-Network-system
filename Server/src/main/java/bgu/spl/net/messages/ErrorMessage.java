package bgu.spl.net.messages;

public class ErrorMessage implements Message {
    private final short opCode = 11;
    private short op;
    private String content;

    public ErrorMessage(short op) {
        this.op = op;
    }

    public ErrorMessage(short op, String content) {
        this.op = op;
        this.content = content;
    }

    public short getOp() {
        return op;
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "opCode=" + opCode +
                ", op=" + op +
                ", content='" + content + '\'' +
                '}';
    }
}
