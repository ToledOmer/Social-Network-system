package bgu.spl.net.api;

import bgu.spl.net.messages.Message;
import bgu.spl.net.messages.PostMessage;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {

    private String name ;
    private String password ;
    private Vector<String> following;
    private Vector<String> followers;
    private ConcurrentLinkedQueue<Message> unreadMessages;
    private Vector<PostMessage> postMessages;

    private boolean isOnline;

    public User(String name, String password) {
        this.name = name;
        this.password = password;

    }
    public User(String name){
        this.name = name;
        unreadMessages = new ConcurrentLinkedQueue<>();
        postMessages = new Vector<>();
        followers = new Vector<>();
        following = new Vector<>();
    }

    public Vector<String> getFollowers() {
        return followers;
    }
    public void addFollwer(String userName){
        followers.add(userName);
    }

    public void addPostMessage(PostMessage message){
        postMessages.add(message);
    }

    public Vector<PostMessage> getPostMessages() {
        return postMessages;
    }

    public void addUnreadMessage(Message message){
        unreadMessages.add(message);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Vector<String> getFollowing() {
        return following;
    }

    public ConcurrentLinkedQueue<Message> getUnreadMessages() {
        return unreadMessages;
    }

    public void setFollowing(Vector<String> following) {
        this.following = following;
    }

    public void setUnreadMessages(ConcurrentLinkedQueue<Message> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
