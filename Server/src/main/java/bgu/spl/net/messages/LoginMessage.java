package bgu.spl.net.messages;

import bgu.spl.net.api.User;

public class LoginMessage implements Message {
    private final short opCode = 2;
    private User user ;
    private boolean isLoged;

    public LoginMessage(User user) {
        this.user = user;
    }
    public void toLogin(){

    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public User getUser() {
        return user;
    }
}
