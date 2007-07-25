/*****************************************************************************
*
*    FILE NAME: CCU711.cpp
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
#include "CCU711.h"
#include "ctinexus.h"
#include "numstr.h"
#include "cticalls.h"
#include "color.h"

#define INPUT    '0'
#define RESETREQ '1'
#define RESETACK '2'
#define GENREQ   '3'
#define GENREP   '4'


/**************************************************
/*  CCU711 functions   
***************************************************/

CCU711::CCU711(){
	WSAStartup(MAKEWORD (1,1), &wsaData);

	listenSocket = new CTINEXUS();
	newSocket = new CTINEXUS();
}

//Listen for and store an incoming message
void CCU711::ReceiveMsg(){
	unsigned char ReadBuffer[300];
	unsigned long bytesRead=0;
	RWDBDateTime AboutToRead;
	while(bytesRead !=4) {
		newSocket->CTINexusRead(ReadBuffer,4, &bytesRead, 15);
	}
	SET_FOREGROUND_BRIGHT_YELLOW;
	std::cout<<AboutToRead.asString();
	SET_FOREGROUND_BRIGHT_CYAN;
	std::cout<<" IN:"<<std::endl;
	SET_FOREGROUND_BRIGHT_GREEN;
	for( int byteitr = 0; byteitr < bytesRead; byteitr++ )
	{
		std::cout<<std::string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2))<<' ';
	}

	Message inMsg;
	//determine the type of message
	inMsg.CreateMessage(INPUT,ReadBuffer);
	bytesRead=0;
	while(bytesRead !=inMsg.getBytesToFollow()) {
		newSocket->CTINexusRead(ReadBuffer,inMsg.getBytesToFollow(), &bytesRead, 15);
	}
	for( byteitr = 0; byteitr < bytesRead; byteitr++ )
	{
		std::cout<<string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2))<<' ';
	}
	std::cout<<std::endl;
	SET_FOREGROUND_WHITE;
	//  inMsg.InsertWord('i', ReadBuffer);

	std::string printType;
	switch(inMsg.getMessageType()){
	case '0':
		printType.append("INPUT");
		break;
	case '1':
		printType.append("RESETREQ");
		break;
	case '2':
		printType.append("RESETACK");
		break;
	case '3':
		printType.append("GENREQ");
		break;
	case '4':
		printType.append("GENREP");
		break;
	}

	std::cout<<"Message Type: "<<printType<<"    Word Type: "<<inMsg.getWordType();
	std::cout<<"    Word Function: "<<inMsg.getWordFunction()<<std::endl;
	incomingMsg[0] = inMsg;
}

//Build a new message
void CCU711::CreateMsg(){
	unsigned char someData[10];
	Message newMessage;
	if(incomingMsg[0].getMessageType()== RESETREQ)
	{
		//  Reset request
		unsigned char Address = incomingMsg[0].getAddress();
		newMessage.CreateMessage(RESETACK, someData, Address);
		outgoingMsg[0]=newMessage;
	}
	else if(incomingMsg[0].getMessageType()==GENREQ)
	{
		//  General Request
		unsigned char Address = incomingMsg[0].getAddress();
		newMessage.CreateMessage(GENREP, someData, Address);
		outgoingMsg[0]=newMessage;
	}
	CTISleep(1000);
}

//Send the message back to porter
void CCU711::SendMsg(){
	unsigned long bytesWritten = 0;
	unsigned char * WriteBuffer = outgoingMsg[0].getMessageArray();
	int MsgSize = outgoingMsg[0].getMessageSize();
	unsigned char SendData[300];
	for(int i=0; i<30; i++) {
		SendData[i]= WriteBuffer[i];
	}

	newSocket->CTINexusWrite(&SendData, MsgSize, &bytesWritten, 15); 
	RWDBDateTime DateSent;
	SET_FOREGROUND_BRIGHT_YELLOW;
	std::cout<<DateSent.asString();
	SET_FOREGROUND_BRIGHT_CYAN;
	std::cout<<" OUT:"<<std::endl;
	SET_FOREGROUND_BRIGHT_MAGNETA;
	for(int byteitr = 0; byteitr < bytesWritten; byteitr++ )
	{
		std::cout <<string(CtiNumStr(SendData[byteitr]).hex().zpad(2))<<' ';
	}
	std::cout<<std::endl;
	SET_FOREGROUND_WHITE;
	std::string printType;
	switch(outgoingMsg[0].getMessageType()){
	case '0':
		printType.append("INPUT");
		break;
	case '1':
		printType.append("RESETREQ");
		break;
	case '2':
		printType.append("RESETACK");
		break;
	case '3':
		printType.append("GENREQ");
		break;
	case '4':
		printType.append("GENREP");
		break;
	}

	std::cout<<"Message Type: "<<printType<<"    Word Type: "<<outgoingMsg[0].getWordType();
	std::cout<<"    Word Function: "<<outgoingMsg[0].getWordFunction()<<std::endl;
	std::cout<<"------------------------------------------------------"<<std::endl;

	// Reset inmessage
	Message blankMessage;
	incomingMsg[0] = blankMessage;
	// reset outbound message
	outgoingMsg[0] = blankMessage;
}

//Returns a pointer to the listening socket
CTINEXUS * CCU711::getListenSocket(){
	CTINEXUS * ListenSocket = listenSocket;
	return(ListenSocket);
}

//Returns a pointer to newSocket
CTINEXUS * CCU711::getNewSocket(){
	CTINEXUS * Socket = newSocket;
	return(Socket);
}



