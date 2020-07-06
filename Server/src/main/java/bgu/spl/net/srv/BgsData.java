package bgu.spl.net.srv;

import bgu.spl.net.api.User;
import bgu.spl.net.messages.PMMessage;
import bgu.spl.net.messages.PostMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BgsData {

    private ConcurrentHashMap<Integer, User> ConIdAndUsers; //active users
    private Vector<User> users; //registered


    public BgsData() {
        this.users = new Vector<>();
        this.ConIdAndUsers = new ConcurrentHashMap<>();
    }


    public Vector<User> getUsers() {
        return users;
    }

    public ConcurrentHashMap<Integer, User> getConIdAndUsers() {
        return ConIdAndUsers;
    }



}
