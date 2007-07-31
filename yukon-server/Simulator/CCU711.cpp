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

#define INPUT    0
#define RESETREQ 1
#define RESETACK 2
#define GENREQ   3
#define GENREP   4

#define DTRAN 11

#define FEEDEROP 21

#define A_WORD 31
#define B_WORD 32

#define FUNCACK 41
#define READ    42
#define WRITE   43

#define INCOMING 0
#define OUTGOING 1


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
	inMsg.DecodeCommand(ReadBuffer);
	inMsg.DecodePreamble();
	inMsg.InsertWord(INPUT, ReadBuffer);
	_incomingMsg[0] = inMsg;

	std::string printMsg;
	std::string printCmd;
	std::string printPre;
	std::string printWrd;
	std::string printFnc;

	TranslateInfo(INCOMING, printMsg, printCmd, printPre, printWrd, printFnc);

	std::cout<<"Msg: "<<printMsg<<"    Cmd: "<<printCmd<<"    Pre: "<<printPre;
	std::cout<<"    Wrd: "<<printWrd<<"    Fnc: "<<printFnc<<std::endl;

}

//Build a new message
void CCU711::CreateMsg(){
	unsigned char someData[10];
	Message newMessage;
	if(_incomingMsg[0].getMessageType()== RESETREQ)
	{
		//  Reset request
		unsigned char Address = _incomingMsg[0].getAddress();
		newMessage.CreateMessage(RESETACK, someData, Address);
		_outgoingMsg[0]=newMessage;
	}
	else if(_incomingMsg[0].getMessageType()==GENREQ)
	{
		//  General Request
		unsigned char Frame = _incomingMsg[0].getFrame();
		unsigned char Address = _incomingMsg[0].getAddress();
		newMessage.CreateMessage(GENREP, someData, Address, Frame);

		_outgoingMsg[0]=newMessage;
	}
}

//Send the message back to porter
void CCU711::SendMsg(){
	unsigned long bytesWritten = 0;
	unsigned char * WriteBuffer = _outgoingMsg[0].getMessageArray();
	int MsgSize = _outgoingMsg[0].getMessageSize();
	unsigned char SendData[300];
	for(int i=0; i<100; i++) {
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
	switch(_outgoingMsg[0].getMessageType()){
	case 0:
		printType.append("INPUT");
		break;
	case 1:
		printType.append("RESETREQ");
		break;
	case 2:
		printType.append("RESETACK");
		break;
	case 3:
		printType.append("GENREQ");
		break;
	case 4:
		printType.append("GENREP");
		break;
	}

	std::string printMsg;
	std::string printCmd;
	std::string printPre;
	std::string printWrd;
	std::string printFnc;

	TranslateInfo(OUTGOING, printMsg, printCmd, printPre, printWrd, printFnc);

	std::cout<<"Msg: "<<printMsg<<"    Cmd: "<<printCmd<<"         Pre: "<<printPre;
	std::cout<<"    Wrd: "<<printWrd<<"    Fnc: "<<printFnc<<std::endl;
	std::cout<<"________________________________________________________________________________"<<std::endl;
	           

	// Reset inmessage
	Message blankMessage;
	_incomingMsg[0] = blankMessage;
	// reset outbound message
	_outgoingMsg[0] = blankMessage;
}

void CCU711::TranslateInfo(bool direction, string & printMsg, string & printCmd, string & printPre, string & printWrd, string & printFnc)
{
	if(direction == 0){
		switch(_incomingMsg[0].getMessageType()){
		case 0:
			printMsg.append("INPUT");
			break;
		case 1:
			printMsg.append("RESETREQ");
			break;
		case 2:
			printMsg.append("RESETACK");
			break;
		case 3:
			printMsg.append("GENREQ");
			break;
		case 4:
			printMsg.append("GENREP");
			break;
		}
		switch(_incomingMsg[0].getCommand()){
		case 11:
			printCmd.append("DTRAN");
			break;
		}
		switch(_incomingMsg[0].getPreamble()){
		case 21:
			printPre.append("FEEDEROP");
			break;
		}
		switch(_incomingMsg[0].getWordType()){
		case 31:
			printWrd.append("A_WORD");
			break;
		case 32:
			printWrd.append("B_WORD");
			break;
		case 33:
			printWrd.append("C_WORD");
			break;
		case 34:
			printWrd.append("D_WORD");
			break;
		case 35:
			printWrd.append("E_WORD");
			break;
		}
		switch(_incomingMsg[0].getWordFunction()){
		case 41:
			printFnc.append("FUNCACK");
			break;
		case 42:
			printFnc.append("READ");
			break;
		case 43:
			printFnc.append("WRITE");
			break;
		}
	}
	else if(direction == 1){
		switch(_incomingMsg[0].getMessageType()){
		case 0:
			printMsg.append("INPUT");
			break;
		case 1:
			printMsg.append("RESETREQ");
			break;
		case 2:
			printMsg.append("RESETACK");
			break;
		case 3:
			printMsg.append("GENREQ");
			break;
		case 4:
			printMsg.append("GENREP");
			break;
		}
		switch(_outgoingMsg[0].getCommand()){
		case 11:
			printCmd.append("DTRAN");
			break;
		}
		switch(_incomingMsg[0].getPreamble()){
		case 21:
			printPre.append("FEEDEROP");
			break;
		}
		switch(_outgoingMsg[0].getWordType()){
		case 31:
			printWrd.append("A_WORD");
			break;
		case 32:
			printWrd.append("B_WORD");
			break;
		case 33:
			printWrd.append("C_WORD");
			break;
		case 34:
			printWrd.append("D_WORD");
			break;
		case 35:
			printWrd.append("E_WORD");
			break;
		}
		switch(_outgoingMsg[0].getWordFunction()){
		case 41:
			printFnc.append("FUNCACK");
			break;
		case 42:
			printFnc.append("READ");
			break;
		case 43:
			printFnc.append("WRITE");
			break;
		}
	}
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



