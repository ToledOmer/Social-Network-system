package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.BgsData;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {
    //hashmap of connection id of connection handler of user
    private ConcurrentHashMap<Integer,ConnectionHandler<T>> connectionHandlers;

    public ConnectionsImpl() {
        this.connectionHandlers = new ConcurrentHashMap<Integer,ConnectionHandler<T>>();

    }

    @Override
    public boolean send(int connectionId, T msg) {
        //sync if trying to send to handler, and it was disconnect on the same time
        synchronized (connectionHandlers){
            if (connectionHandlers.containsKey(connectionId)){
                ConnectionHandler<T> handler =connectionHandlers.get(connectionId);
                System.out.println("sent");
                handler.send(msg);

                return true;
            }
            return false;
        }
    }

    @Override
    public void broadcast(T msg) {
        for (Integer id : connectionHandlers.keySet()) {
            connectionHandlers.get(id).send(msg);
        }

    }

    @Override
    public void disconnect(int connectionId) {
        //sync if trying to send to handler, and it was disconnect on the same time
        synchronized (connectionHandlers){
            connectionHandlers.remove(connectionId);
        }
    }
    public void connect(ConnectionHandler handler, int connectionId){
        connectionHandlers.putIfAbsent(connectionId,handler);
    }
}
