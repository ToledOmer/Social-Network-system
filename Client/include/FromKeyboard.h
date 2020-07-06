//
// Created by yuvalman@wincs.cs.bgu.ac.il on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_FROMKEYBOARD_H
#define BOOST_ECHO_CLIENT_FROMKEYBOARD_H


#include "connectionHandler.h"
#include <stdlib.h>
#include <boost/thread.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>
#include <mutex>
#include <condition_variable>
#include "FromSocket.h"


class FromKeyboard {
public:
    FromKeyboard(ConnectionHandler *handler, std::mutex& mutex, std::condition_variable &cv,
            bool *isOnline);

private:
     ConnectionHandler *handler;
     std::mutex & _mutex;
    std::condition_variable &cv;
    bool *isOnline;

public:
    ConnectionHandler *getHandler() const;
    void run();
    void Terminate();

};


#endif //BOOST_ECHO_CLIENT_FROMKEYBOARD_H
