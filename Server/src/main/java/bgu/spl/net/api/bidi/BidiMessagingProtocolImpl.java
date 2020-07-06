package bgu.spl.net.api.bidi;

import bgu.spl.net.api.User;
import bgu.spl.net.messages.*;
import bgu.spl.net.srv.BgsData;

import java.util.*;
import java.util.stream.Collectors;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private Connections<Message> connections;
    private int connectionId;
    private BgsData data;
    private User sendingUser;
    private byte one = '\1';
    private byte zero = '\0';
    public BidiMessagingProtocolImpl(BgsData data){
        this.data = data;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connections=connections;
        this.connectionId=connectionId;
    }

    @Override
    public void process(Message message) {
        switch (message.getOpCode()){
            //register
            case 1:{
                RegisterMessage registerMessage = (RegisterMessage) message;
                //sync because two theards might try to create a user with the same name at the same time
                //sync register with userlist
                synchronized (data.getUsers()){
                    if (!containsUser(registerMessage.getUser())){
                        data.getUsers().add(registerMessage.getUser());
                        connections.send(connectionId,new ACKMessage(registerMessage.getOpCode()));
                    }
                    else {
                        connections.send(connectionId,new ErrorMessage(registerMessage.getOpCode()));
                    }
                }
                break;
            }
            //login
            case 2: {
                //sync pm/post with login
                LoginMessage loginMessage = (LoginMessage) message;
                User userFromMessage = loginMessage.getUser();
                User user = getFromRegister(userFromMessage.getName());
                synchronized (user){
                    if (!containsUser(userFromMessage) || sendingUser !=null){
                        connections.send(connectionId, new ErrorMessage(loginMessage.getOpCode()));
                        break;
                    }
                    else
                    {
                        //if user is not registerd, password is wrong, user is already online

                        if (!userFromMessage.getPassword().equals(user.getPassword())
                                || user.isOnline()){
                            connections.send(connectionId,new ErrorMessage(loginMessage.getOpCode()));
                            break;
                        }
                        else {
                            //login the user and send him notifications
                            user.setOnline(true);
                            connections.send(connectionId, new ACKMessage(loginMessage.getOpCode()));
                            data.getConIdAndUsers().putIfAbsent(connectionId , user);
                            sendingUser = data.getConIdAndUsers().get(connectionId);
                            if (user.getUnreadMessages()!=null){
                                synchronized (user.getUnreadMessages()){
                                    int size = user.getUnreadMessages().size();
                                    for (int i =0 ; i< size;i++){
                                        Message unReadMessage = user.getUnreadMessages().poll();
                                        if (unReadMessage.getClass().isAssignableFrom(PostMessage.class)){
                                            PostMessage postMessage = (PostMessage) unReadMessage;
                                            connections.send(connectionId,new NotificationMessage(one,postMessage.getPoster(),postMessage.getContent()));
                                        }
                                        else {
                                            PMMessage pmMessage = (PMMessage) unReadMessage;
                                            connections.send(connectionId,new NotificationMessage(zero,pmMessage.getPoster(),pmMessage.getContent()));
                                        }
                                    }
                                }
                            }

                        }
                    }
                    }



                break;
            }


            //logout
            case 3:
            {
                //need sync in logout because: other actions might consider him online while he is about to logout, and data(Like post message) can be disapear
                synchronized (sendingUser){
                    if (sendingUser==null)
                        connections.send(connectionId,new ErrorMessage(message.getOpCode()));

                    else {
                        sendingUser.setOnline(false);
                        data.getConIdAndUsers().remove(connectionId,sendingUser);
                        sendingUser = null;
                        connections.send(connectionId,new ACKMessage(message.getOpCode()));
                    }

                }

            }
            break;
            //follow
            case 4:
                if(sendingUser==null) {
                    connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    break;
                }
                else
                {
                    FollowMessage followMsg = (FollowMessage) message;
                    short counter =0;
                    //if its follow command
                    if(followMsg.isToFollow()) {
                        for (String toFollow : followMsg.getUserTofollow()) {
                            synchronized (sendingUser){
                                if (!sendingUser.getFollowing().contains(toFollow) && getFromRegister(toFollow)!= null){
                                    //add to the sending user following list
                                    sendingUser.getFollowing().add(toFollow);
                                    //the sending user is becoming someone elses follower
                                    getFromRegister(toFollow).getFollowers().add(sendingUser.getName());
                                    counter++;
                                }
                                else
                                    followMsg.getUserTofollow().remove(toFollow);
                            }

                        }
                    }
                    //if its unfollow command
                    else if (followMsg.isToUnfollow()){
                        for (String toUnFollow : followMsg.getUserTofollow()) {
                            synchronized (sendingUser){
                                if (sendingUser.getFollowing().contains(toUnFollow) && getFromRegister(toUnFollow)!= null) {
                                    //remove the user from the sending user list
                                    sendingUser.getFollowing().remove(toUnFollow);
                                    //remove the sending user from the other user followers list
                                    getFromRegister(toUnFollow).getFollowers().remove(sendingUser.getName());
                                    counter++;
                                }
                                else
                                    followMsg.getUserTofollow().remove(toUnFollow);
                            }
                        }
                    }
                    if(counter==0)
                        connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    else
                        connections.send(connectionId,new ACKMessage(followMsg.getOpCode(), counter
                                ,followMsg.getUserTofollow()));
                }
                break;

                //post
            case 5:
                if(sendingUser==null) {
                    connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    break;
                }
                else
                {
                    PostMessage post = (PostMessage) message;
                    connections.send(connectionId,new ACKMessage(post.getOpCode()));
                    sendingUser.addPostMessage(post);
                    post.setPoster(sendingUser.getName());
                    //add a post to the user posts list
                    for (String postTo: post.getUserToNotify()) {
                        synchronized (data.getUsers()) {
                            //not exist
                            if (getFromRegister(postTo) != null) {
                                //check if the user follows the sending user
                                if (!sendingUser.getFollowers().contains(postTo)) {
                                    if (!getFromRegister(postTo).isOnline())
                                        getFromRegister(postTo).getUnreadMessages().add(post);
                                    else {
                                        Integer reciepientUserConectionId = 0;
                                        reciepientUserConectionId = getKeyByValue(data.getConIdAndUsers(), getFromRegister(postTo));
                                        message = new NotificationMessage(one, post.getPoster(), post.getContent());
                                        connections.send(reciepientUserConectionId, message);
                                    }
                                }
                            }
                        }
                    }
                    for (String postTo: sendingUser.getFollowers()) {
                        synchronized (data.getUsers()){
                            if (getFromRegister(postTo) != null) {
                                if (!getFromRegister(postTo).isOnline()) {
                                    getFromRegister(postTo).getUnreadMessages().add(post);
                                } else {
                                    Integer reciepientUserConectionId = 0;
                                    reciepientUserConectionId = getKeyByValue(data.getConIdAndUsers() , getFromRegister(postTo));
                                    message = new NotificationMessage(one, post.getPoster(), post.getContent());
                                    connections.send(reciepientUserConectionId, message);
                                }
                            }
                        }
                    }
                }


                break;
            //pm
            case 6:
                if(sendingUser==null) {
                    connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    break;
                }
                else
                {
                    PMMessage pmMessage = (PMMessage) message;
                    pmMessage.setPoster(sendingUser.getName());
                    //check if sending user is online or recipient user is not registered
                    if (!sendingUser.isOnline()
                            || getFromRegister(pmMessage.getUserName()) == null){
                        connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    }
                    else  {
                        User reciepientUser = getFromRegister(pmMessage.getUserName());
                        //add messages that the recipient will get in notiflication when he is login
                        synchronized (reciepientUser){
                            if (!reciepientUser.isOnline()){
                                reciepientUser.addUnreadMessage(message);
                                connections.send(connectionId,new ACKMessage(message.getOpCode()));
                                break;
                            }
                            else {
                                Integer reciepientUserConectionId = 0;
                                connections.send(connectionId,new ACKMessage(message.getOpCode()));
                                reciepientUserConectionId = getKeyByValue(data.getConIdAndUsers() , reciepientUser);
                                connections.send(reciepientUserConectionId,new NotificationMessage(zero, pmMessage.getPoster(), pmMessage.getContent()));
                            }
                        }
                    }
                }

                break;
            //userList
            case 7:
                //sync register with userlist
                if(sendingUser==null) {
                    connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    break;
                }
                else
                {
                    if (!sendingUser.isOnline()){
                        connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    }
                    else {
                        LinkedList registeredUsers = new LinkedList();
                        //user will be wait to sync of register
                        for (User userName : data.getUsers()){
                            registeredUsers.add(userName.getName());
                        }
                        connections.send(connectionId,new ACKMessage(message.getOpCode(),(short)registeredUsers.size(),registeredUsers));
                    }
                    break;
                }

                //stat
            case 8:
                if(sendingUser==null) {
                    connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    break;
                }
                else
                {
                    StatMessage statMessage = (StatMessage) message;
                    if (!sendingUser.isOnline() || getFromRegister(statMessage.getUserName())==null){
                        connections.send(connectionId,new ErrorMessage(message.getOpCode()));
                    }
                    else {
                        User user = getFromRegister(statMessage.getUserName());
                        synchronized (user){
                            connections.send(connectionId,new ACKMessage(message.getOpCode(),(short) user.getPostMessages().size()
                                    ,(short) user.getFollowers().size(),(short) user.getFollowing().size()));
                        }

                    }
                    break;
                }

        }

    }

    private boolean containsUser(User userFromMessage) {
        for (User user: data.getUsers()) {
            if(user.getName().equals(userFromMessage.getName()))
                return true;
        }
        return false;

    }

    //
    private User getFromRegister(String userIter) {
        User userTofind;
        Iterator it = data.getUsers().iterator();
        while (it.hasNext()) {
            userTofind = (User) it.next();
            if (userIter.equals(userTofind.getName()))
                return userTofind;
        }
        return null;
    }
    @Override
    public boolean shouldTerminate() {
        return false;
    }
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
