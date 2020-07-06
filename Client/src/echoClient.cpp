#include <stdlib.h>
#include <connectionHandler.h>
#include <FromKeyboard.h>
#include <FromSocket.h>
#include <thread>
#include <mutex>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);


    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::mutex mutex;
    std::condition_variable cv;
    bool isOnline = true;


    //From here we will see the rest of the ehco client implementation:
	FromKeyboard keyboardReader = FromKeyboard(&connectionHandler,mutex,cv,&isOnline);
    std::thread keyboradThread(&FromKeyboard::run,keyboardReader);

    //server
    FromSocket readFromSocket = FromSocket(&connectionHandler,mutex,cv,&isOnline);
    std::thread serverThread(&FromSocket::run, readFromSocket);

    serverThread.join();
//    keyboardReader->Terminate();
//    //unlock the keyboard until the end of the program
//    cv.notify_all();
    keyboradThread.join();


}
