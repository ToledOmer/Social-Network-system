package bgu.spl.net.messages;

import java.util.LinkedList;

public class FollowMessage implements Message{
    private final short opCode = 4;
    private boolean toFollow ;
    private boolean toUnfollow ;
    private LinkedList<String> userTofollow = new LinkedList<>(); //list of users he will follow/unfollow
    private boolean first = true; //holds if the message succedd
    private short numOfUsers;
    private String username;
    public FollowMessage(byte command , short numOfUsers , String users ) {
        this.numOfUsers =numOfUsers;

        //check if the command is to follow or unfollow according to the given int
        if (command == '\0' )toFollow = true;
        else{toUnfollow = true; }


        while(users != "") {
            if (!users.contains(" ")){
                userTofollow.add(users);
                users = "";
            }
            else
            {
                while (users.contains(" ")) {
                    if (first){
                        //first user to add handels diffrently because he has no \0 before his name
                        username = users.substring(0,users.indexOf(" "));
                        users = users.substring(users.indexOf(" ") + 1);
                        first = false;
                    }
                    else {
                        //has no \0 before his name so substring from "1"
                        username = users.substring(0,users.indexOf(" "));
                        users = users.substring(users.indexOf(" ") + 1);
                    }

                    userTofollow.add(username);

                }
                //last user to add had no " " in the end
                userTofollow.add(users);
                //remove the last user in order to exit the while-loop
                users = "";
            }
        }
    }

    public FollowMessage(byte command, short numOfUsers) {
        if (command == '\0' )toFollow = true;
        else{toUnfollow = true; }

        this.numOfUsers =numOfUsers;

    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public boolean isToFollow() {
        return toFollow;
    }

    public boolean isToUnfollow() {
        return toUnfollow;
    }

    public LinkedList<String> getUserTofollow() {
        return userTofollow;
    }


    public int getNumOfUsers() {
        return numOfUsers;
    }

}
