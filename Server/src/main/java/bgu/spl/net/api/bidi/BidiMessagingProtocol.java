package bgu.spl.net.api.bidi;

public interface BidiMessagingProtocol<T>  {
	/**
	 * initiate the
	 * protocol with the active connections structure of the server and saves the
	 * owner client’s connection id.
	 * @param connectionId
	 * @param connections
	 */
    void start(int connectionId, Connections<T> connections);

	/**
	 * As in MessagingProtocol, processes a given
	 * message. Unlike MessagingProtocol, responses are sent via the
	 * connections object send function.
	 * @param message
	 */
	void process(T message);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
