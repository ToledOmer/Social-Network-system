CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/FromKeyboard.o bin/FromSocket.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/echoClient.o bin/FromKeyboard.o bin/FromSocket.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/FromKeyboard.o: src/FromKeyboard.cpp
	g++ $(CFLAGS) -o bin/FromKeyboard.o src/FromKeyboard.cpp

bin/FromSocket.o: src/FromSocket.cpp
	g++ $(CFLAGS) -o bin/FromSocket.o src/FromSocket.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
