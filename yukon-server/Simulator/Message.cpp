/*****************************************************************************
*
*    FILE NAME: Message.cpp
*
*    DATE: 7/1/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: Make messages
*    
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#include "Message.h"
#include "stdio.h"

#define INPUT    '0'
#define RESETREQ '1'
#define RESETACK '2'
#define GENREQ   '3'
#define GENREP   '4'

namespace std {}

/**************************************************
/*  Message functions   
***************************************************/
Message::Message(){
	IndexOfEnd = 0;
	IndexOfWords = 0;
	MessageType = '?';
	for (int i =0; i < 48; i++) {
		MessageData[i] = 0x0;
	}
	MessageType;
	EmetconWord blankWord;
	Words[0] = blankWord;
	Words[1] = blankWord;
	Words[2] = blankWord;
	Words[3] = blankWord;
	BytesToFollow = 0;;
}
	
// Constructor to build a new Message 
void Message::CreateMessage(char MsgType, unsigned char Data[], unsigned char Address){
	MessageType = MsgType;
	if(MessageType == INPUT) {
		MessageData[0] = Data[0];
		MessageData[1] = Data[1];
		MessageData[2] = Data[2];
		MessageData[3] = Data[3];
		IndexOfEnd = 4;
		BytesToFollow = DecodeIDLC();
	}
	else if(MessageType == RESETACK) {
		MessageData[0] = 0x7e;
		MessageData[1] = Address;   //  slave address
		MessageData[2] = 0x73;
		MessageData[3] = 0x00;   //  insert CRC code
		IndexOfEnd = 4;
	}
	else if(MessageType == GENREP) {
		MessageData[0] = 0x7e;
		MessageData[1] = Address;   //  slave address
		MessageData[2] = 0x73;
		MessageData[3] = 0x00;
		MessageData[4] = 0x00;
		MessageData[5] = 0x00;
		MessageData[6] = 0x00;
		MessageData[7] = 0x00;
		MessageData[8] = 0x00;
		MessageData[9] = 0x00;
		MessageData[10] = 0x00;
		MessageData[11] = 0x00;
		MessageData[12] = 0x00;
		MessageData[13] = 0x00;
		MessageData[14] = 0x00;
		MessageData[15] = 0x00;
		MessageData[16] = 0x00;
		MessageData[17] = 0x00;
		MessageData[18] = 0x00;   //  insert CRC code
		IndexOfEnd = 9;
	}
}

int Message::DecodeIDLC(){
	char bytesToFollow = 0;
	if((MessageData[0] & 0x7e) == 0x7e){
		//  IDLC LAYER 2 Asynchronous Link Control
			if((MessageData[2] & 0x1f)== 0x1f){
			//  Reset Request
				MessageType = RESETREQ;
				bytesToFollow = 1;
			}
			if((MessageData[2] & 0x01) == 0x00){
			//  General Request
				MessageType = GENREQ;
				bytesToFollow = 5;
			}
	}
	return bytesToFollow;
}


int Message::DecodePreamble(){
	char bytesToFollow = 0;
	if((MessageData[0] & 0x4) == 0x4){
		//  Feeder operation specified
		MessageType = 'f';
			if((MessageData[2] & 0x7)== 0x7){
				bytesToFollow = 7;
			}
			else if((MessageData[2] & 0xe)== 0xe){
				bytesToFollow = 14;
			}
	}
	else if((MessageData[0]==0x53) && (MessageData[1]==0xf5) && (MessageData[2]==0x55)) {
		// CCU710 ping
		MessageType = 'p';
		bytesToFollow = 0;
	}
	else if((MessageData[0]==0x47) && (MessageData[1]==0x30) && (MessageData[2]==0x8e)) {
		// CCU710 ping
		MessageType = '1';
		bytesToFollow = 14;
	}
	else if((MessageData[0]==0x47) && (MessageData[1]==0x30) && (MessageData[2]==0x95)) {
		// CCU710 ping
		MessageType = '2';
		bytesToFollow = 21;
	}
	if(MessageData[1]== 0x0){
	}
	if(MessageData[2]== 0x0){
	}
	return bytesToFollow;
}

//  This is used to insert words into incoming messages
void Message::InsertWord(char WordType, unsigned char Data[]){
	char WordFunction;
	int InsertMore = 0;
	int WTF = 0;
	if(WordType == 'i') {
		MessageData[3]=Data[0];
		WordType = DecodeDefinition();
		WordFunction = DecodeFunction(WordType, Data);
		WTF = DecodeWTF(WordType, Data);
		//  If there is a 'c' word following the 'b' word
		//  then WTF indicates 'c' words to follow so they should be stored
		if(WTF>0) {
			if((Data[7] & 0xc0) == 0xc0) {
				InsertMore = WTF;
			}
		}
	}

	EmetconWord oneWord;
	oneWord.CreateWord(WordType, Data, WordFunction);
	oneWord.setWTF(WTF);
	IndexOfEnd += oneWord.getWordSize();
	Words[IndexOfWords]= oneWord;
	IndexOfWords++;
	//  If the first word is a b word, see how many c words follow it
	//  and insert them into the incoming message
	if(Words[0].getWordType() == 'b') {
		for(int i=0; i<InsertMore; i++) {
			EmetconWord anotherWord;
			anotherWord.CreateWord('c', Data, WordFunction);
			IndexOfEnd += anotherWord.getWordSize();
			Words[IndexOfWords]= anotherWord;
			IndexOfWords++;
		}
	}
}

//  This is used to insert words into outgoing messages
void Message::InsertWord(EmetconWord oneWord){
	Words[IndexOfWords] = oneWord;
	IndexOfWords++;
	//  Determine where the message write should take place
	//  considering words that have already been copied into MessageData
	int Here = (IndexOfWords-1) * 8;
	if(Words[0].getWordType()=='c') {
		MessageData[Here + 3] = Words[0][0];
		MessageData[Here + 4] = Words[0][1];
		MessageData[Here + 5] = Words[0][2];
		MessageData[Here + 6] = Words[0][3];
		MessageData[Here + 7] = Words[0][4];
		MessageData[Here + 8] = Words[0][5];
		MessageData[Here + 9] = Words[0][6];
		IndexOfEnd+=7;
	}
	else if(Words[0].getWordType()=='d') {
		MessageData[Here + 3] = Words[0][0];
		MessageData[Here + 4] = Words[0][1];
		MessageData[Here + 5] = Words[0][2];
		MessageData[Here + 6] = Words[0][3];
		MessageData[Here + 7] = Words[0][4];
		MessageData[Here + 8] = Words[0][5];
		MessageData[Here + 9] = Words[0][6];
		IndexOfEnd+=7;
	}
	else if(Words[0].getWordType()=='e') {
		MessageData[Here + 3] = Words[0][0];
		MessageData[Here + 4] = Words[0][1];
		MessageData[Here + 5] = Words[0][2];
		MessageData[Here + 6] = Words[0][3];
		MessageData[Here + 7] = Words[0][4];
		MessageData[Here + 8] = Words[0][5];
		MessageData[Here + 9] = Words[0][6];
		IndexOfEnd+=7;
	}
}

int Message::DecodeWTF(char WordType, unsigned char Data[]){
	if((Data[4] & 0x10) == 0x10) {
		return 1;
	}
	if((Data[4] & 0x20) == 0x20) {
		return 2;
	}
	if((Data[4] & 0x30) == 0x30) {
		return 3;
	}
	else return 0;
}

char Message::DecodeDefinition(){
	char WordType ='?';
	if((MessageData[3] & 0xa0) == 0xa0){
		WordType = 'b';
	}
	return WordType;
}

char Message::DecodeFunction(char WordType, unsigned char Data[]){
	char FunctionType = '?';
	if(WordType=='b') {
		//  Store the rest of the word into MessageData
		MessageData[4] = Data[1];
		MessageData[5] = Data[2];
		MessageData[6] = Data[3];
		MessageData[7] = Data[4];
		MessageData[8] = Data[5];
		MessageData[9] = Data[6];
		//   check to see what function is specified
		if((MessageData[8] & 0x3) == 0x03) {
			//  Function with acknowledge
			FunctionType = 'f';
		}
		else if((MessageData[8] & 0x01) == 0x01) {
			//  Read
			FunctionType = 'r';
		}
		else if((MessageData[8] & 0x01) == 0x00) {
			//  Write
			FunctionType = 'w';
		}
	}
		return FunctionType;
}

void Message::InsertAck(){
	MessageData[IndexOfEnd] = 0xc3;
	IndexOfEnd++;
}

// Constructor to copy and decode an existing Message
void Message::DecodeMessage(unsigned char ReadBuffer[], CTINEXUS * newSocket){
	EmetconWord newWord;
	newWord.DecodeWord(ReadBuffer, newSocket);
	newWord.ReadWord();
}

// Function to read the data in the Message to the screen
void Message::ReadMessage(){
}

//  Returns a copy of the message array
unsigned char * Message::getMessageArray(){
	return MessageData;
}

void Message::CreatePreamble(){
	MessageData[0] = 0xc3;
	MessageData[1] = 0xc3;
	MessageData[2] = 0x82;
	IndexOfEnd += 3;
}

unsigned char Message::getAddress(){
	return MessageData[1];
}

int Message::getBytesToFollow(){
	return BytesToFollow;
}

char Message::getMessageType(){
	return MessageType;
}

int Message::getMessageSize(){
	return IndexOfEnd;
}

int Message::getWTF(){
	return Words[0].getWTF();
}

char Message::getWordType(){
	return Words[0].getWordType();
}

char Message::getWordFunction(){
	return Words[0].getWordFunction();
}

