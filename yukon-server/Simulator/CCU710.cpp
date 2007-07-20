/*****************************************************************************
*
*    FILE NAME: CCU710.cpp
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
#include "CCU710.h"
#include "ctinexus.h"
#include "numstr.h"
#include "cticalls.h"
#include "color.h"


/**************************************************
/*  CCU710 functions   
***************************************************/

CCU710::CCU710(){
	WSAStartup(MAKEWORD (1,1), &wsaData);

	listenSocket = new CTINEXUS();
	newSocket = new CTINEXUS();
}

//Listen for and store an incoming message
void CCU710::ReceiveMsg(){
	unsigned char ReadBuffer[300];
	unsigned long bytesRead=0;
	RWDBDateTime AboutToRead;
	while(bytesRead !=3) {
		newSocket->CTINexusRead(ReadBuffer,3, &bytesRead, 15);
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
	inMsg.CreateMessage('i',ReadBuffer);
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
	inMsg.InsertWord('i', ReadBuffer);

	std::cout<<"Message Type: "<<inMsg.getMessageType()<<"    Word Type: "<<inMsg.getWordType();
	std::cout<<"    Word Function: "<<inMsg.getWordFunction()<<std::endl;
	incomingMsg[0] = inMsg;
}

//Build a new message
void CCU710::CreateMsg(){
	unsigned char someData[10];
	Message newMessage;
	if(incomingMsg[0].getMessageType()=='f')
	{
		if(incomingMsg[0].getWordType()=='b') {
			if(incomingMsg[0].getWordFunction()== 'r') {
				//  Read energy, etc
				newMessage.CreateMessage('d', someData);
				newMessage.InsertAck();
			}
			else if(incomingMsg[0].getWordFunction()== 'f') {
				if(incomingMsg[0].getMessageSize()==17){
					//  putconfig, etc
					newMessage.InsertAck();
					newMessage.InsertAck();
				}
				else if(incomingMsg[0].getMessageSize()==10){
					//  MCT410 ping
					newMessage.CreateMessage('d', someData);
					newMessage.InsertAck();
				}
			}
			else if(incomingMsg[0].getWordFunction()== 'w') {
				// getconfig etc
				newMessage.CreateMessage('d', someData);
				newMessage.InsertAck();
				EmetconWord newWord;
				for(int i=1; i<incomingMsg[0].getWTF(); i++) {
					newMessage.InsertWord(newWord);
					newMessage.InsertAck();
				}
			}
		}
		outgoingMsg[0]=newMessage;
	}
	else if(incomingMsg[0].getMessageType()=='p') {
		newMessage.CreateMessage('p', someData);
		outgoingMsg[0]=newMessage;
	}
	CTISleep(5000);
}

//Send the message back to porter
void CCU710::SendMsg(){
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
	std::cout<<"Message Type: "<<outgoingMsg[0].getMessageType()<<"    Word Type: "<<outgoingMsg[0].getWordType();
	std::cout<<"    Word Function: "<<outgoingMsg[0].getWordFunction()<<std::endl;
	std::cout<<"------------------------------------------------------"<<std::endl;

	// Reset inmessage
	Message blankMessage;
	incomingMsg[0] = blankMessage;
	// reset outbound message
	outgoingMsg[0] = blankMessage;
}

//Returns a pointer to the listening socket
CTINEXUS * CCU710::getListenSocket(){
	CTINEXUS * ListenSocket = listenSocket;
	return(ListenSocket);
}

//Returns a pointer to newSocket
CTINEXUS * CCU710::getNewSocket(){
	CTINEXUS * Socket = newSocket;
	return(Socket);
}


