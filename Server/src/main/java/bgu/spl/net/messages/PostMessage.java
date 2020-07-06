package bgu.spl.net.messages;

import java.util.LinkedList;

public class PostMessage implements Message {
    private final short opCode = 5;
    private LinkedList<String> userToNotify = new LinkedList<>();
    private String content;
    private String poster;

    public String getContent() {
        return content;
    }

    public PostMessage(String msg) {
        //save the content
        content = msg;
        while (msg.contains("@")) {
            String userToAdd ;
            //find the user to notify
            msg = msg.substring(msg.indexOf("@"));
            //take the user's name
            if (msg.contains(" "))
                userToAdd = msg.substring(1, msg.indexOf(" "));
            else{
                userToAdd = msg.substring(1);
                msg = " ";
            }
            //in case the same user got tagged twice
            if (!userToNotify.contains(userToAdd))
                //add the user to the list
                userToNotify.add(userToAdd);
            //cuts the message in order to keep searching
            msg = msg.substring(msg.indexOf(" "));
        }
    }
    public short getOpCode() {
        return opCode;
    }

    public LinkedList<String> getUserToNotify() {
        return userToNotify;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
