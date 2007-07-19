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

namespace std {}

/**************************************************
/*  Message functions   
***************************************************/
Message::Message(){
	IndexOfEnd = 0;
	MessageType = '?';
}
	
// Constructor to build a new Message 
void Message::CreateMessage(char MsgType, unsigned char Data[]){
	MessageType = MsgType;
	if(MessageType == 'i') {
		MessageData[0] = Data[0];
		MessageData[1] = Data[1];
		MessageData[2] = Data[2];
		IndexOfEnd = 3;
		BytesToFollow = DecodePreamble();
		//std::cout<<"MessageType: "<<MessageType<<std::endl;
	}
	else if(MessageType == 'c') {
		CreatePreamble();
		EmetconWord newWord;
		newWord.CreateWord('c', Data);
		InsertWord(newWord);
	}
	else if(MessageType == 'd') {
		CreatePreamble();
		EmetconWord newWord;
		newWord.CreateWord('d', Data);
		InsertWord(newWord);
	}
	else if(MessageType == 'p') {
		MessageData[0] = 0xc3;
		MessageData[1] = 0xc3;
		MessageData[2] = 0xf5;
		MessageData[3] = 0x55;
		IndexOfEnd = 4;
	}
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

int Message::getBytesToFollow(){
	return BytesToFollow;
}

char Message::getMessageType(){
	return MessageType;
}

int Message::getMessageSize(){
	return IndexOfEnd;
}

char Message::getWordType(){
	return Words[0].getWordType();
}

char Message::getWordFunction(){
	return Words[0].getWordFunction();
}

void Message::InsertWord(char WordType, unsigned char Data[]){
	char WordFunction;
	int WTF = 0;
	if(WordType == 'i') {
		MessageData[3]=Data[0];
		WordType = DecodeDefinition();
		WordFunction = DecodeFunction(WordType, Data);
		WTF = DecodeWTF(WordType, Data);
		}
		//std::cout<<"WordType: "<<WordType<<std::endl;

	EmetconWord oneWord;
	oneWord.CreateWord(WordType, Data, WordFunction);
	IndexOfEnd += oneWord.getWordSize();
	Words[0]= oneWord;
	//  If the first word is a b word, see how many c words follow it
	//  and insert them into the incoming message
	if(Words[0].getWordType() == 'b') {
		int InsertMore = Words[0].getWTF();
		for(int i=0; i<InsertMore; i++) {
			EmetconWord anotherWord;
			oneWord.CreateWord('c', Data, WordFunction);
			IndexOfEnd += anotherWord.getWordSize();
			Words[i+1]= anotherWord;
		}
	}
}

void Message::InsertWord(EmetconWord oneWord){
	Words[0] = oneWord;
	if(Words[0].getWordType()=='c') {
		MessageData[3] = Words[0][0];
		MessageData[4] = Words[0][1];
		MessageData[5] = Words[0][2];
		MessageData[6] = Words[0][3];
		MessageData[7] = Words[0][4];
		MessageData[8] = Words[0][5];
		MessageData[9] = Words[0][6];
		IndexOfEnd+=7;
	}
	else if(Words[0].getWordType()=='d') {
		MessageData[3] = Words[0][0];
		MessageData[4] = Words[0][1];
		MessageData[5] = Words[0][2];
		MessageData[6] = Words[0][3];
		MessageData[7] = Words[0][4];
		MessageData[8] = Words[0][5];
		MessageData[9] = Words[0][6];
		IndexOfEnd+=7;
	}
	//else 
		//std::cout<<"Could not insert word of type: "<<Words[0][0]<<std::endl;	
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
			//else
				//std::cout<<"Unknown number of characters to be transmitted"<<std::endl;
	}
	else if((MessageData[0]==0x53) && (MessageData[1]==0xf5) && (MessageData[2]==0x55)) {
		// CCU710 ping
		MessageType = 'p';
		bytesToFollow = 0;
	}
	//else
		//std::cout<<"Unknown operation specified"<<std::endl;
	if(MessageData[1]== 0x0){
	}
	if(MessageData[2]== 0x0){
	}
	return bytesToFollow;
}

char Message::DecodeDefinition(){
	char WordType ='?';
	if((MessageData[3] & 0xa0) == 0xa0){
		WordType = 'b';
	}
	//else
		//std::cout<<"Unknown word defined: "<< string(CtiNumStr(MessageData[3]).xhex())<<std::endl;

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
