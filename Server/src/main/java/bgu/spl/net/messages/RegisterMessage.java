package bgu.spl.net.messages;

import bgu.spl.net.api.User;

import java.util.LinkedList;

public class RegisterMessage implements Message {
    private final short opCode = 1;
    private User user ;

    public RegisterMessage(User user) {
        this.user = user;
    }

    public void toRegister(LinkedList users){
        users.add(user);
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public User getUser() {
        return user;
    }
}
