package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.BgsData;
import bgu.spl.net.srv.Server;

import static java.lang.Integer.parseInt;

public class TPCMain {
    public static void main(String[] args) {
        BgsData data = new BgsData(); //one shared object

// you can use any server...
        Server.threadPerClient(
                parseInt(args[0]), //port
                () -> new BidiMessagingProtocolImpl(data), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
