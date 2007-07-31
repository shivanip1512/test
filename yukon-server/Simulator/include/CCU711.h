/*****************************************************************************
*
*    FILE NAME: CCU711.h
*
*    DATE: 7/1/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: Simulate the behavior of CCU 711s
*    
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#ifndef  __CCU711_H__
#define  __CCU711_H__
#include "Message.h"
#include "Winsock2.h"
#include <iostream>

#define DEFAULT  0

#define INPUT    0
#define RESETREQ 1
#define RESETACK 2
#define GENREQ   3
#define GENREP   4

#define DTRAN 11

#define FEEDEROP 21

#define A_WORD 31
#define B_WORD 32
#define C_WORD 33
#define D_WORD 34
#define E_WORD 35

#define FUNCACK 41
#define READ    42
#define WRITE   43

#define ACKACK  51

#define INCOMING 0
#define OUTGOING 1

using namespace std;

class CCU711{
	public:
		//Default constructor
		CCU711();
		//Destructor

		//Send the message back to porter
		void SendMsg();
		//Build a new message
		void CreateMsg();
		//Listen for and store an incoming message
		void ReceiveMsg();
		//  Translate message for user
		void TranslateInfo(bool direction, string & printMsg, string & printCmd, string & printPre, string & printWrd, string & printFnc);
		//Returns a pointer to the listening socket
		CTINEXUS * getListenSocket();
		//Returns a pointer to newSocket
		CTINEXUS * getNewSocket();

	private:
		//Array to store incoming message
		Message _incomingMsg[1];
		//Array to store outgoing message
		Message _outgoingMsg[1];

		//Storage for sockets
		WSADATA wsaData;
		CTINEXUS * listenSocket;
		CTINEXUS * newSocket;
};

#endif


