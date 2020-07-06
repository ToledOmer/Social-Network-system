//
// Created by yuvalman@wincs.cs.bgu.ac.il on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_FROMSOCKET_H
#define BOOST_ECHO_CLIENT_FROMSOCKET_H

#include "connectionHandler.h"
#include <stdlib.h>
#include <mutex>
#include <condition_variable>
#include <boost/thread.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

class FromSocket {
private:
    ConnectionHandler *handler;
    std::mutex & _mutex;
    std::condition_variable &cv;
    bool *isOnline;

public:
    FromSocket(ConnectionHandler *handler, std::mutex& mutex, std::condition_variable &cv, bool *isOnline);
    void run();
    ConnectionHandler *getHandler() const;

};


#endif //BOOST_ECHO_CLIENT_FROMSOCKET_H
