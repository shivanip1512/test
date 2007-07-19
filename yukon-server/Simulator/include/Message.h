/*****************************************************************************
*
*    FILE NAME: Message.h
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
#ifndef  __MESSAGE_H__
#define  __MESSAGE_H__
#include "EmetconWord.h"

//  The Message class can be used to build/store messages as well as to build outgoing messages
class Message{
	public:
		// Default constructor
		Message();
		// Constructor to build a new Message
		void CreateMessage(char MsgType, unsigned char Data[]);
		// Constructor to copy and decode an existing Message
		void DecodeMessage(unsigned char ReadBuffer[], CTINEXUS * newSocket);
		// Destructor

		// Add an existing word to a message
		void InsertWord(EmetconWord oneWord);
		//  Just store the definition of a word in a message
		void InsertWord(char WordType, unsigned char Data[]);
		// Function to read the data in the Message to the screen
		void ReadMessage();
		//  Figure out what the preamble says
		int DecodePreamble();
		//  Determine what kind of word it is
		char DecodeDefinition();
		//  Determine the number of words to follow
		int DecodeWTF();
		//  Determine what function the word specifies
		char DecodeFunction(char WordType, unsigned char Data[]);
		//  Returns a copy of the message array
		unsigned char * getMessageArray();
		//  Returns the number of bytes left to read in the message
		int getBytesToFollow();
		//  Returns the type of message
		char getMessageType();
		//  Returns the size of the message
		int getMessageSize();
		//  Returns the number type of word in the message
		char getWordType();
		//  Returns the number function of the word in the message
		char getWordFunction();
		//  Put the correct preamble at the beginning of message
		void CreatePreamble();
		//  Add an ack character to the current message
		void InsertAck();



	private:
		char MessageType;
		unsigned char MessageData[300];
		EmetconWord Words[50];
		int BytesToFollow;
		int IndexOfEnd;
};

#endif
