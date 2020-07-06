package bgu.spl.net.api.bidi;

import java.io.IOException;

public interface Connections<T> {
    /**
     * sends a message T to client represented by the given connId
     * @param connectionId
     * @param msg
     * @return
     */
    boolean send(int connectionId, T msg);

    /**
     *  sends a message T to all active clients. This
     * includes clients that has not yet completed log-in by the BGSServer protocol.
     * Remember, Connections<T> belongs to the server pattern
     * implemenration, not the protocol!.
     * @param msg
     */

    void broadcast(T msg);

    /**
     * removes active client connId from map.
     * @param connectionId
     */

    void disconnect(int connectionId);
}
