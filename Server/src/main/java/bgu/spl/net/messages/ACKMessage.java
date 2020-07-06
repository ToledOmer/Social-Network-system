package bgu.spl.net.messages;

import java.util.LinkedList;
import java.util.Vector;

public class ACKMessage implements Message{
    private short op ;
    private final short opCode =10;
    // NumOfusers of folowers/resgistered users
    private short NumOfusers ;
    private String output ;
    //stat details
    private short numPosts, numFollowers, numFollowing;
    private LinkedList<String> userNameList;

    public ACKMessage(short op, String output) {
        this.op = op;
        this.output = output;

    }
    public  ACKMessage(short op){
        this.output= "";
        this.op = op;
    }

    public LinkedList<String> getUserNameList() {
        return userNameList;
    }

    public void setUserNameList(LinkedList<String> userNameList) {
        this.userNameList = userNameList;
    }

    // userList Follow ack constructor
    public ACKMessage(short op,  short numOfusers ,LinkedList<String> userNameList) {
        this.output="";
        this.op = op;
        this.NumOfusers = numOfusers;
        this.userNameList = userNameList;
    }

    //stat ack constructor
    public ACKMessage(short op, short numPosts, short numFollowers, short numFollowing){
        this.op = op;
        this.numPosts=numPosts;
        this.numFollowers=numFollowers;
        this.numFollowing=numFollowing;
    }

    public short getNumPosts() {
        return numPosts;
    }

    public short getNumFollowers() {
        return numFollowers;
    }

    public short getNumFollowing() {
        return numFollowing;
    }

    public String getContent() {
        return output;
    }

    public short getOp() {
        return op;
    }

    public short getNumOfusers() {
        return NumOfusers;
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    @Override
    public String toString() {
        return "ACKMessage{" +
                "op=" + op +
                ", opCode=" + opCode +
                ", NumOfusers=" + NumOfusers +
                ", output='" + output + '\'' +
                ", numPosts=" + numPosts +
                ", numFollowers=" + numFollowers +
                ", numFollowing=" + numFollowing +
                '}';
    }
}

