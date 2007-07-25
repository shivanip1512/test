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
		void CreateMessage(char MsgType, unsigned char Data[], unsigned char Address = 0x00);
		// Destructor

		// This is used to insert words into incoming messages 
		void InsertWord(char WordType, unsigned char Data[]);
		// This is used to insert words into outgoing messages
		void InsertWord(EmetconWord oneWord);
		// Function to read the data in the Message to the screen
		void ReadMessage();
		//  Figure out the IDLC protocol
		int DecodeIDLC();
		//  Figure out what the preamble says
		int DecodePreamble();
		//  Determine what kind of word it is
		char DecodeDefinition();
		//  Determine the number of words to follow
		int DecodeWTF(char WordType, unsigned char Data[]);
		//  Determine what function the word specifies
		char DecodeFunction(char WordType, unsigned char Data[]);
		//  Determine and insert the CRC code
		unsigned short InsertCRC(unsigned long Length);
		//  Returns a copy of the message array
		unsigned char * getMessageArray();
		//  Returns the number of bytes left to read in the message
		int getBytesToFollow();
		//  Returns the type of message
		char getMessageType();
		//  Returns the type of message
		unsigned char getAddress();
		//  Returns the size of the message
		int getMessageSize();
		//  Returns the number type of word in the message
		char getWordType();
		//  Returns the words to follow value from a 'b' word
		int getWTF();
		//  Returns the number function of the word in the message
		char getWordFunction();
		//  Put the correct preamble at the beginning of message
		void CreatePreamble();
		//  Add an ack character to the current message
		void InsertAck();
		// Constructor to copy and decode an existing Message
		void DecodeMessage(unsigned char ReadBuffer[], CTINEXUS * newSocket);



	private:
		char MessageType;
		unsigned char MessageData[50];
		EmetconWord Words[4];
		int BytesToFollow;
		int IndexOfEnd;
		int IndexOfWords;
};

#endif
