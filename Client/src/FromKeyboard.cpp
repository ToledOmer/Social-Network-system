//
// Created by yuvalman@wincs.cs.bgu.ac.il on 12/29/18.
//

#include <condition_variable>
#include <FromSocket.h>
#include "../include/FromKeyboard.h"


FromKeyboard::FromKeyboard(ConnectionHandler *handler,std::mutex& mutex, std::condition_variable &cv, bool *isOnline) :
handler(handler), _mutex(mutex),cv(cv), isOnline(isOnline)
{

}



void FromKeyboard::run(){
    while (*isOnline) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<std::string> words;
        boost::split(words,line,boost::is_any_of(" "));
        if (!words.empty()){
            //get op code
//            char *opCode = new char[2];
            char opCode[2];
            std::string op = words[0];
            if (op=="REGISTER"){
                std::string username = words[1];
                std::string password = words[2];
                getHandler()->shortToBytes((short)1, opCode);
                getHandler()->sendBytes(opCode,2);
//                std::cout << opCode << std::endl;

                getHandler()->sendFrameAscii(username,'\0');
                getHandler()->sendFrameAscii(password,'\0');
            }
            if (op=="LOGIN"){
                std::string username = words[1];
                std::string password = words[2];
                getHandler()->shortToBytes((short)2, opCode);
                getHandler()->sendBytes(opCode,2);
                getHandler()->sendFrameAscii(username,'\0');
                getHandler()->sendFrameAscii(password,'\0');
            }
            if (op=="LOGOUT"){
                getHandler()->shortToBytes((short)3, opCode);
                getHandler()->sendBytes(opCode,2);
                //wait until termainate(From echoClient)
                std::unique_lock<std::mutex> lk{_mutex};
                cv.wait(lk);

            }
            if (op=="FOLLOW"){
                getHandler()->shortToBytes((short)4, opCode);
                getHandler()->sendBytes(opCode,2);
                //send 1/0 (Follow/Unfollow)
//                char *isFollow = new char[1];
                    char isFollow;
//                if(std::stoi(words[1]) == 1)
//                    getHandler()->sendBytes(new char [1]{1},1);
//                else
//                    getHandler()->sendBytes(new char [1]{0},1);
                if(std::stoi(words[1]) == 1) isFollow = '\1';
                else isFollow = '\0';
                getHandler()->sendBytes(&isFollow,1);
                //send num of users
//                char *numOfUsers = new char[2];
                char numOfUsers[2];
                short myint2 = std::stoi(words[2]);
                getHandler()->shortToBytes(myint2,numOfUsers);
                getHandler()->sendBytes(numOfUsers,2);
                //send string of usernames to follow/unfollow
                std::string userNameList;
                for (unsigned int j = 3; j <words.size() ; ++j) {
                    if (j==3)
                        userNameList.append(words[j]);
                    else
                        userNameList.append('\0' + words[j]);
                }
                getHandler()->sendFrameAscii(userNameList,'\0');

            }
            if (op=="POST"){
                getHandler()->shortToBytes((short)5, opCode);
                getHandler()->sendBytes(opCode,2);
                std::string content;
                for (unsigned int j = 1; j <words.size() ; ++j) {
                    if (j==1)
                        content.append(words[j]);
                    else
                        content.append(" " + words[j]);
                }
                getHandler()->sendFrameAscii(content,'\0');

            }
            if (op=="PM"){
                getHandler()->shortToBytes((short)6, opCode);
                getHandler()->sendBytes(opCode,2);
                std::string username = words[1];
                getHandler()->sendFrameAscii(username,'\0');
                std::string content;
                for (unsigned int j = 2; j <words.size() ; ++j) {
                    if (j==2)
                        content.append(words[j]);
                    else
                        content.append(" " + words[j]);
                }
                getHandler()->sendFrameAscii(content,'\0');
            }
            if (op=="USERLIST"){
                getHandler()->shortToBytes((short)7 ,opCode);
                getHandler()->sendBytes(opCode,2);
            }
            if (op=="STAT"){
                getHandler()->shortToBytes((short)8 ,opCode);
                getHandler()->sendBytes(opCode,2);
                std::string username = words[1];
                getHandler()->sendFrameAscii(username,'\0');
            }
        }
    }
}

ConnectionHandler *FromKeyboard::getHandler() const {
    return handler;
}

void FromKeyboard::Terminate() {
    *isOnline = false;

}


