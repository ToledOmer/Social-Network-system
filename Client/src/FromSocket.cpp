//
// Created by yuvalman@wincs.cs.bgu.ac.il on 12/29/18.
//

#include <connectionHandler.h>
#include <mutex>
#include <condition_variable>
#include "../include/FromSocket.h"


FromSocket::FromSocket(ConnectionHandler *handler, std::mutex& mutex, std::condition_variable &cv,bool *isOnline) :
handler(handler),_mutex(mutex),cv(cv), isOnline(isOnline)  {
}

ConnectionHandler *FromSocket::getHandler() const {
    return handler;
}

void FromSocket::run() {
    while (*isOnline) {
//        char *opCode = new char[2];
        char opCode[2];
        getHandler()->getBytes(opCode, 2);
        short op = getHandler()->bytesToShort(opCode);
        switch (op) {
            //notification
            case 9: {
//                char *notification = new char[1];
                char notification;
                getHandler()->getBytes(&notification, 1);
                std::string notificationType;
                if (notification == 0) {
                    notificationType = "PM ";
                } else {
                    notificationType = "Public ";
                }
                std::string postingUser;
                getHandler()->getFrameAscii(postingUser, '\0');
                std::string content;
//                if (notification == 0)
//                    getHandler()->getFrameAscii(content, '\0');
                getHandler()->getFrameAscii(content, '\0');
                std::cout << "NOTIFICATION " + notificationType + postingUser + " " + content << std::endl;
            }
                break;
                //ack
            case 10: {
                char messageOpCode[2];
                getHandler()->getBytes(messageOpCode, 2);
//                getHandler()->bytesToShort(messageOpCode);
                short messageOp = getHandler()->bytesToShort(messageOpCode);
                if (messageOp == 1 || messageOp == 2 || messageOp == 5 || messageOp == 6) {
                    std::cout << "ACK " + std::to_string(messageOp) << std::endl;
                    break;
                }
                //logout(close connection
                if (messageOp == 3) {
                    std::cout << "ACK " + std::to_string(messageOp) << std::endl;
                    *isOnline = false;
                    cv.notify_all();

                    break;
                }
                if (messageOp == 4 || messageOp == 7) {
//                    char *numOfUsers = new char[2];
                    char numOfUsers[2];
                    getHandler()->getBytes(numOfUsers, 2);
                    short numOfUsersShort = getHandler()->bytesToShort(numOfUsers);
                    std::string userNameList = "";
                    std::string userName;
                    for (short j = 0; j <numOfUsersShort ; ++j) {
                        getHandler()->getFrameAscii(userName, '\0');
                        userNameList += userName+" ";
                        userName="";
                    }

//                        userNameList = userNameList + " " + userName.substr(0,userName.length()-1);

                    std::cout << "ACK " + std::to_string(messageOp) +" "+ std::to_string(numOfUsersShort)
                                 + " " + userNameList.substr(0,userNameList.length()-1) << std::endl;
                }
                if (messageOp == 8) {
//                    char *numOfPost = new char[2];
                    char numOfPost[2];
                    getHandler()->getBytes(numOfPost, 2);
                    short numOfPostShort = getHandler()->bytesToShort(numOfPost);

//                    char *numOfFollowers = new char[2];
                    char numOfFollowers[2];
                    getHandler()->getBytes(numOfFollowers, 2);
                    short numOfFollowersShort = getHandler()->bytesToShort(numOfFollowers);

//                    char *numOfFollowing = new char[2];
                    char numOfFollowing[2];
                    getHandler()->getBytes(numOfFollowing, 2);
                    short numOfFollowingShort = getHandler()->bytesToShort(numOfFollowing);

                    std::cout << "ACK " + std::to_string(messageOp) + " " + std::to_string(numOfPostShort)
                                 + " " + std::to_string(numOfFollowersShort) + " "
                                 + std::to_string(numOfFollowingShort) << std::endl;
                }
                break;

            }
                //error
            case 11: {
//                char *messageOpCode = new char[2];
                char messageOpCode[2];
                getHandler()->getBytes(messageOpCode, 2);
                short messageOp = getHandler()->bytesToShort(messageOpCode);
                std::cout << "ERROR " + std::to_string(messageOp) << std::endl;
                //release the wait of keyboard(in logout)
                cv.notify_all();

            }
                break;
        }
    }

}


