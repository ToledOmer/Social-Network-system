package bgu.spl.net.api;

import bgu.spl.net.messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private byte[] bytesSimpleAckOrErr;
    private byte[] bytesnoti ;
    private byte[] bytesFollowUser ;
    private byte[] bytesStat;
    private int len = 0;
    private short opCode = -1;
    //init user in order to prevent null pointer in line 29 and line 42 (case 1 and 2)
    private User user   = new User(null);
    private byte[] num = new byte[2];
    private short counter = 0;
    private short numOfUsers = -1;
    private byte command;
    private String toUser ;
    private String msg = "";
    private FollowMessage follow;

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (opCode != -1) {
            switch (opCode) {
                //Register request (REGISTER)
                case 1:
                    //retreive user name only if hasnt init
                    if (nextByte=='\0' && user.getName() ==null) {
                        user = new User(popStringByte0());
                        return null;
                    }
                    //user name is alreadyinit so next time we will init his password
                    if (nextByte=='\0') {
                        user.setPassword(popStringByte0());
                        Message register = new RegisterMessage(user);
                        opCode = -1;
                        user   = new User(null);
                        return register;
                    }
                    break;
                //Login request (LOGIN)
                case 2:
                    //retreive user name only if hasnt init
                    if (nextByte=='\0' && user.getName() == null) {
                        user.setName(popStringByte0());
                        return null;
                    }
                    //user name is alreadyinit so next time we will init his password
                    if (nextByte=='\0') {
                        user.setPassword(popStringByte0());
                        Message logInMessage = new LoginMessage(user);
                        opCode = -1;
                        user   = new User(null);
                        return logInMessage;
                    }
                    break;

                //Follow / Unfollow request (FOLLOW)
                case 4:
                    //means that i can "take" the command and the numOfUsers to follow
                    if (len == 3 && numOfUsers == -1) {
                        //gets the relevant command (follow or unfollow) and then "cuts" this parts of the messgae
                        command = bytes[0];
                        //gets the number of users to follow (or unfollow) and then "cuts" this parts of the messgae
                        //the next two bytes will represent the numOfUsers and will be
                        //translated to short
                        num[0] = bytes[1];
                        num[1] = bytes[2];
                        numOfUsers = bytesToShort(num);
                        len = 0;
                        follow = new FollowMessage(command, numOfUsers);
                        break;
                    }
                    if (numOfUsers != counter && nextByte=='\0' && follow!=null) {
                        follow.getUserTofollow().add(popStringByte0());
                        counter++;
                        if(counter ==numOfUsers){
                            numOfUsers = -1;
                            opCode = -1;
                            counter = 0;
                            return follow;
                        }
                        else
                            return null;

                    }
                    break;


                //Post request (POST)
                case 5:
                    if (nextByte=='\0') {
                        msg = (popStringByte0());
                        Message post = new PostMessage(msg);
                        opCode = -1;
                        return post;
                    }
                    break;

                //PM request (PM)
                case 6:
                    if (nextByte=='\0' && toUser == null) {
                        //get the user we want to send to
                        toUser = popStringByte0();
                        return null;
                    }
                    if (nextByte=='\0') {
                        //content of the message itself
                        String pmContent = popStringByte0();
                        Message pm = new PMMessage(pmContent, toUser);
                        opCode = -1;
                        toUser = null;
                        return pm;
                    }
                    break;
                //Stats request (STAT)
                case 8:
                    if (nextByte=='\0') {
                        msg = popStringByte0();
                        Message stat = new StatMessage(msg);
                        opCode = -1;
                        return stat;
                    }
                    break;


            }
        }
        if (len == 2 && opCode == -1) {


            opCode = bytesToShort(bytes);
            //create a new array not include the data that have been taken
            bytes = Arrays.copyOfRange(bytes, 2, bytes.length);
            len = 0;

        }
        //Logout request (LOGOUT)
        if ((opCode == -1 && nextByte=='\3')) {
            Message logOutnMessage = new LogoutMessage();
            opCode = -1;
            len = 0;
            return logOutnMessage;
        }
        //USERLIST request
        if ((opCode == -1 && nextByte=='\7')) {
            Message userlistMessage = new UserListMessage();
            opCode = -1;
            len = 0;
            return userlistMessage;
        }
        else {
            pushByte(nextByte);
            return null; //not a line yet
        }
    }



    @Override
    public byte[] encode(Message message) {
        //get the bytes of the ack op code
        switch (message.getOpCode()) {
            //Notification (NOTIFICATION)
            case 9:
                bytesnoti = new byte[3];
                bytesnoti[0] = shortToBytes(message.getOpCode())[0];
                bytesnoti[1] = shortToBytes(message.getOpCode())[1];
                NotificationMessage notimsg = (NotificationMessage) message;
                bytesnoti[2] = notimsg.getCommand();
                bytesnoti = MergeArrays(bytesnoti, notimsg.getPostingUser().getBytes(), 3);
                //add 1 byte of "0" to the byteArray
                bytesnoti = MergeArrays(bytesnoti, new byte[]{'\0'}, bytesnoti.length);
                //merge with the content itself
                bytesnoti = MergeArrays(bytesnoti, (notimsg.getContent()+'\0').getBytes(), bytesnoti.length);
                System.out.println(Arrays.toString(bytesnoti));
                return bytesnoti;

            //Ack (ACK)
            case 10:
                ACKMessage ack = (ACKMessage) message;
                //get the bytes of the message op code
                bytesSimpleAckOrErr = new byte[4];
                bytesSimpleAckOrErr[0] = shortToBytes(message.getOpCode())[0];
                bytesSimpleAckOrErr[1] = shortToBytes(message.getOpCode())[1];
                bytesSimpleAckOrErr[2] = shortToBytes(ack.getOp())[0];
                bytesSimpleAckOrErr[3] = shortToBytes(ack.getOp())[1];

                //ack for FollowMessage(4) or for UserListMessage(7)
                if (ack.getOp() == 4 || ack.getOp() == 7) {
                    //insert the number of user
                    //get the bytes of the message op code
                    bytesFollowUser = new byte[6];
                    bytesFollowUser[0] = shortToBytes(message.getOpCode())[0];
                    bytesFollowUser[1] = shortToBytes(message.getOpCode())[1];
                    bytesFollowUser[2] = shortToBytes(ack.getOp())[0];
                    bytesFollowUser[3] = shortToBytes(ack.getOp())[1];
                    bytesFollowUser[4] = shortToBytes(ack.getNumOfusers())[0];
                    bytesFollowUser[5] = shortToBytes(ack.getNumOfusers())[1];
                    while (counter!= ack.getNumOfusers()) {
                        //merge the two arrays into one ,the int "7" represent the size of the byte2 that we actually use
                        bytesFollowUser = MergeArrays(bytesFollowUser, (ack.getUserNameList().poll() +'\0').getBytes(), bytesFollowUser.length);
                        counter++;
                    } System.out.println(Arrays.toString(bytesFollowUser));
                    counter = 0;
                    follow = null;
                    return bytesFollowUser;
                }
                //ack for StatMessage (8)
                else if (ack.getOp() == 8) {
                    bytesStat =  new byte[10];
                    bytesStat[0] = shortToBytes(message.getOpCode())[0];
                    bytesStat[1] = shortToBytes(message.getOpCode())[1];
                    bytesStat[2] = shortToBytes(ack.getOp())[0];
                    bytesStat[3] = shortToBytes(ack.getOp())[1];
                    //get byte of NumofPosts
                    bytesStat[4] = shortToBytes(ack.getNumPosts())[0];
                    bytesStat[5] = shortToBytes(ack.getNumPosts())[1];
                    //get byte of NumofFollowers
                    bytesStat[6] = shortToBytes(ack.getNumFollowers())[0];
                    bytesStat[7] = shortToBytes(ack.getNumFollowers())[1];
                    //get byte of NumofFollowing
                    bytesStat[8] = shortToBytes(ack.getNumFollowing())[0];
                    bytesStat[9] = shortToBytes(ack.getNumFollowing())[1];
                    //merge the two arrays into one ,the int "9" represent the size of the byte2 that we actually use
                    System.out.println(Arrays.toString(bytesStat));
                    return bytesStat;
                }
                else{
                    System.out.println(Arrays.toString(bytesSimpleAckOrErr));
                    return bytesSimpleAckOrErr;
                }

                //Error (ERROR)
            case 11:
                ErrorMessage err = (ErrorMessage) message;
                bytesSimpleAckOrErr = new byte[4];
                bytesSimpleAckOrErr[0] = shortToBytes(message.getOpCode())[0];
                bytesSimpleAckOrErr[1] = shortToBytes(message.getOpCode())[1];
                bytesSimpleAckOrErr[2] = shortToBytes(err.getOp())[0];
                bytesSimpleAckOrErr[3] = shortToBytes(err.getOp())[1];
                //merge the two arrays into one, the int "4" represent the size of the byte2
                System.out.println(Arrays.toString(bytesSimpleAckOrErr));
                if (err.getOp() == 4 || err.getOp() == 7)
                    follow = null;
                return bytesSimpleAckOrErr;
        }
        System.out.println("encoded");
        return null;
    }


    public short bytesToShort ( byte[] byteArr){
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }


    public byte[] shortToBytes ( short num){
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }


    private void pushByte ( byte nextByte){
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }


    private byte[] MergeArrays ( byte[] b1, byte[] b2, int pointer){
        byte[] b3 = new byte[pointer + b2.length];
        for (int i = 0; i < pointer + b2.length; i++) {
            while (i < pointer) {
                b3[i] = b1[i];
                i++;
            }
            b3[i] = b2[i-pointer];
        }
        return b3;
    }

    private String popStringByte0() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        //create a new array not include the data that have been taken
        bytes = Arrays.copyOfRange(bytes, len , bytes.length);
        len = 0 ;
        return result;
    }


}


