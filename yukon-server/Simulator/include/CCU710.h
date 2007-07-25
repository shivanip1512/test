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
*    DESCRIPTION: Simulate the behavior of CCU 710s
*    
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#ifndef  __CCU710_H__
#define  __CCU710_H__
#include "Message.h"
#include "Winsock2.h"



class CCU710{
	public:
		//Default constructor
		CCU710();
		//  Constructor with socket needed by 710
		void setSocket(CTINEXUS * Socket);
		//Destructor

		//Send the message back to porter
		void SendMsg();
		//Build a new message
		void CreateMsg();
		//Listen for and store an incoming message
		void ReceiveMsg();
		//Returns a pointer to the listening socket
		CTINEXUS * getListenSocket();
		//Returns a pointer to newSocket
		CTINEXUS * getNewSocket();

	private:
		//Array to store incoming message
		Message incomingMsg[1];
		//Array to store outgoing message
		Message outgoingMsg[1];

		//Storage for sockets
		WSADATA wsaData;
		CTINEXUS * listenSocket;
		CTINEXUS * newSocket;
};

#endif

