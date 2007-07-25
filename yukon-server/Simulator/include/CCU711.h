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


