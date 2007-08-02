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
		void CreateMessage(int MsgType, int WrdFnc, unsigned char Data[], int ccuNumber, int &setccuNumber, unsigned char Address = 0x00, unsigned char Frame = 0x00);
		// Destructor
        
        enum WordTypes
        {   
            DEFAULT    = 0,
            INPUT      = 0,
            RESETREQ   = 1,
            RESETACK   = 2,
            GENREQ     = 3,
            GENREP     = 4,
            A_WORD     = 31,
            B_WORD     = 32,
            C_WORD     = 33,
            D_WORD     = 34,
            E_WORD     = 35,
            DTRAN      = 11,
            FEEDEROP   = 21,
            PING       = 22,
            FUNCACK    = 41,
            READ       = 42,
            WRITE      = 43,
            READENERGY = 44,
            ACKACK     = 51,

        };

		// This is used to insert words into incoming messages 
		void InsertWord(int WordType, unsigned char Data[], int counter);
		// This is used to insert words into outgoing messages
		void InsertWord(EmetconWord oneWord);
		// Function to read the data in the Message to the screen
		void ReadMessage();
		//  Figure out the IDLC protocol
		int DecodeIDLC(int & setccuNumber);
		//  Figure out what the preamble says
		void DecodeCommand(unsigned char Data[]);
		//  Figure out what the preamble says
		int DecodePreamble(int &setccuNumber);
		//  Determine what kind of word it is
		int DecodeDefinition();
		//  Determine the number of words to follow
		int DecodeWTF(int WordType, unsigned char Data[]);
		//  Determine what function the word specifies
		int DecodeFunction(int WordType, unsigned char Data[]);
		//  Determine and insert the CRC code
		unsigned short InsertCRC(unsigned long Length);
		//  Returns a copy of the message array
		unsigned char * getMessageArray();
		//  Returns the number of bytes left to read in the message
		int getBytesToFollow();
		//  Returns the type of message
		int getCommand();
		//  Returns the type of message
		int getPreamble();
		//  Returns the type of message
		int getMessageType();
		//  Returns the type of message
		unsigned char getAddress();
		//  Returns the type of message
		unsigned char getFrame();
		//  Returns the size of the message
		int getMessageSize();
		//  Returns the number type of word in the message
		int getWordType();
		//  Returns the words to follow value from a 'b' word
		int getWTF();
		//  Returns the number function of the word in the message
		int getWordFunction();
		//  Put the correct preamble at the beginning of message
		void CreatePreamble();
		//  Add an ack character to the current message
		void InsertAck();
		// Constructor to copy and decode an existing Message
		void DecodeMessage(unsigned char ReadBuffer[], CTINEXUS * newSocket);



	private:
		int _messageType;
		int _commandType;
		int _preamble;
		unsigned char _messageData[100];
		EmetconWord _words[4];
		int _bytesToFollow;
		int _indexOfEnd;
		int _indexOfWords;
};

#endif
