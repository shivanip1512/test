/*****************************************************************************
*
*    FILE NAME: CCU710.h
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
#ifndef  __CCU710_H__
#define  __CCU710_H__
#include "Message.h"
#include "Winsock2.h"
#include <iostream>




using namespace std;

class CCU710{
	public:
		//Default constructor
		CCU710();
		//Destructor

        enum WordTypes
        {   
            DEFAULT    = 0,
            INPUT      = 0,
            RESETREQ   = 1,
            RESETACK   = 2,
            GENREQ     = 3,
            GENREP     = 4,
            A_WORD     = 31,
            B_WORD     = 32,
            C_WORD     = 33,
            D_WORD     = 34,
            E_WORD     = 35,
            DTRAN      = 11,
            FEEDEROP   = 21,
            PING       = 22,
            FUNCACK    = 41,
            READ       = 42,
            WRITE      = 43,
            READENERGY = 44,
            ACKACK     = 51,
            INCOMING   = 0 ,
            OUTGOING   = 1 ,
        };
		//Send the message back to porter
		int SendMsg(unsigned char SendData[]);
		//Build a new message
		void CreateMsg();
		//Listen for and store an incoming message
		int ReceiveMsg(unsigned char ReadBuffer[]);
		//Listen for and store the rest of the message
		void ReceiveMore(unsigned char ReadBuffer[], int counter);
        //Print the incoming message information to the screen
        void PrintInput(unsigned char ReadBuffer[]);
        //Output the outgoing message information to the screen
        void PrintMessage();
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


