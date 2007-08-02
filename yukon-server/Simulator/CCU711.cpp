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

using namespace std;


/**************************************************
/*  CCU711 functions   
***************************************************/

CCU711::CCU711()
{
}

//Listen for and store an incoming message
int CCU711::ReceiveMsg(unsigned char ReadBuffer[])
{
	Message inMsg;
	//determine the type of message
	inMsg.CreateMessage(INPUT, DEFAULT, ReadBuffer, 0);
    _incomingMsg[0] = inMsg;
    return inMsg.getBytesToFollow();
}

//Listen for and store an incoming message
void CCU711::ReceiveMore(unsigned char ReadBuffer[], int counter)
{	
	SET_FOREGROUND_WHITE;
	_incomingMsg[0].DecodeCommand(ReadBuffer);
	_incomingMsg[0].DecodePreamble();
	_incomingMsg[0].InsertWord(INPUT, ReadBuffer, 6);      //  CHANGE THIS 6 TO SOMETHING ELSE !!!  (a counter passed in from serverNexus)
}


void CCU711::PrintInput(unsigned char ReadBuffer[])
{
    cout<<endl;
    string printMsg, printCmd, printPre, printWrd, printFnc;

	TranslateInfo(INCOMING, printMsg, printCmd, printPre, printWrd, printFnc);

	cout<<"Msg: "<<printMsg<<"    Cmd: "<<printCmd<<"    Pre: "<<printPre;
	cout<<"    Wrd: "<<printWrd<<"    Fnc: "<<printFnc<<endl;
}

//Build a new message
void CCU711::CreateMsg(int ccuNumber){
	unsigned char someData[10];
	Message newMessage;
	if(_incomingMsg[0].getMessageType()== RESETREQ)
	{
		//  Reset request
		unsigned char Address = _incomingMsg[0].getAddress();
		newMessage.CreateMessage(RESETACK, DEFAULT, someData, ccuNumber, Address);
		_outgoingMsg[0]=newMessage;
	}
	else if(_incomingMsg[0].getMessageType()==GENREQ)
	{
		//  General Request
		unsigned char Frame = _incomingMsg[0].getFrame();
		unsigned char Address = _incomingMsg[0].getAddress();
		if(_incomingMsg[0].getWordFunction()==FUNCACK) {
			newMessage.CreateMessage(GENREP, ACKACK, someData, ccuNumber, Address, Frame);
		}
		else{ 
			newMessage.CreateMessage(GENREP, DEFAULT, someData, ccuNumber, Address, Frame);
		}

		_outgoingMsg[0]=newMessage;
	}
}

//Send the message back to porter
int CCU711::SendMsg(unsigned char SendData[]){
    unsigned char *WriteBuffer = _outgoingMsg[0].getMessageArray();
    int MsgSize = _outgoingMsg[0].getMessageSize();

    memcpy(SendData, WriteBuffer, 100);

    return MsgSize;
}

void CCU711::PrintMessage(){
    cout<<endl;
    SET_FOREGROUND_WHITE;
    string printType;
    switch(_outgoingMsg[0].getMessageType()){
    case INPUT:
        printType.append("INPUT");
        break;
    case RESETREQ:
        printType.append("RESETREQ");
        break;
    case RESETACK:
        printType.append("RESETACK");
        break;
    case GENREQ:
        printType.append("GENREQ");
        break;
    case GENREP:
        printType.append("GENREP");
        break;
    }

    string printMsg, printCmd, printPre, printWrd, printFnc;

    TranslateInfo(OUTGOING, printMsg, printCmd, printPre, printWrd, printFnc);

    cout<<"Msg: "<<printMsg<<"    Cmd: "<<printCmd<<"         Pre: "<<printPre;
    cout<<"    Wrd: "<<printWrd<<"    Fnc: "<<printFnc<<endl;
    cout<<"________________________________________________________________________________"<<endl;


    // Reset inmessage
    Message blankMessage;
    _incomingMsg[0] = blankMessage;
    // reset outbound message
    _outgoingMsg[0] = blankMessage;
}

void CCU711::TranslateInfo(bool direction, string & printMsg, string & printCmd, string & printPre, string & printWrd, string & printFnc)
{
	if(direction == INCOMING){
		switch(_incomingMsg[0].getMessageType()){
            case INPUT:     printMsg.append("INPUT");       break;
            case RESETREQ:  printMsg.append("RESETREQ");    break;
            case RESETACK:  printMsg.append("RESETACK");    break;
            case GENREQ:    printMsg.append("GENREQ");      break;
            case GENREP:    printMsg.append("GENREP");      break;
		}
		switch(_incomingMsg[0].getCommand()){
            case 11:        printCmd.append("DTRAN");       break;
		}
		switch(_incomingMsg[0].getPreamble()){
            case FEEDEROP:  printPre.append("FEEDEROP");    break;
		}
		switch(_incomingMsg[0].getWordType())
        {
            case A_WORD:    printWrd.append("A_WORD");      break;
            case B_WORD:    printWrd.append("B_WORD");      break;
            case C_WORD:    printWrd.append("C_WORD");      break;
            case D_WORD:    printWrd.append("D_WORD");      break;
            case E_WORD:    printWrd.append("E_WORD");      break;
		}
		switch(_incomingMsg[0].getWordFunction()){
            case FUNCACK:   printFnc.append("FUNCACK");     break;
            case READ:      printFnc.append("READ");        break;
            case WRITE:     printFnc.append("WRITE");       break;
		}
	}
	else if(direction == OUTGOING){
        switch(_outgoingMsg[0].getMessageType()){
            case INPUT:     printMsg.append("INPUT");       break;
            case RESETREQ:  printMsg.append("RESETREQ");    break;
            case RESETACK:  printMsg.append("RESETACK");    break;
            case GENREQ:    printMsg.append("GENREQ");      break;
            case GENREP:    printMsg.append("GENREP");      break;
        }
        switch(_outgoingMsg[0].getCommand()){
            case 11:        printCmd.append("DTRAN");       break;
        }
        switch(_outgoingMsg[0].getPreamble()){
            case FEEDEROP:  printPre.append("FEEDEROP");    break;
        }
        switch(_outgoingMsg[0].getWordType())
        {
            case A_WORD:    printWrd.append("A_WORD");      break;
            case B_WORD:    printWrd.append("B_WORD");      break;
            case C_WORD:    printWrd.append("C_WORD");      break;
            case D_WORD:    printWrd.append("D_WORD");      break;
            case E_WORD:    printWrd.append("E_WORD");      break;
        }
        switch(_outgoingMsg[0].getWordFunction()){
            case FUNCACK:   printFnc.append("FUNCACK");     break;
            case READ:      printFnc.append("READ");        break;
            case WRITE:     printFnc.append("WRITE");       break;
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



