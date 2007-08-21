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
#include "Winsock2.h"
#include <iostream>
#include "CCU710.h"
#include "ctiTime.h"
#include <queue>


using namespace std;

class CCU711{
	public:
		//Default constructor
		CCU711(unsigned char addressFound);
		//Destructor

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
            G_WORD     = 36,
            DTRAN      = 11,
            LGRPQ      = 12,
            RCOLQ      = 13,
            ACTIN      = 14,
            WSETS      = 15,
            XTIME      = 16,
            FEEDEROP   = 21,
            PING       = 22,
            ONEACK     = 23,
            FUNCACK    = 41,
            READ       = 42,
            WRITE      = 43,
            READREP1   = 44,
            READREP2   = 45,
            READREP3   = 46,
            FUNC_WRITE = 47,
            FUNC_READ  = 48,
            ACKACK     = 51,
            INCOMING   = 0,
            OUTGOING   = 1
        };

        struct _queueMessage {
            public:
                int getmessageLength();
                void copyInto(unsigned char Data[], int bytes);
                void setmessageLength(int bytes);
                void setQENID(unsigned char one,unsigned char two, unsigned char three, unsigned char four);
                unsigned char getQENID(int index);
                void copyOut(unsigned char Data[]);
                void initializeMessage();
                int  getWord();
                void setWord(int type);
                void setiotype(int iotype);
                void setFunction(int function);
                int  getFunction();
                int  getioType();
                void setAddress(unsigned char address);
                void setbytesToReturn(int bytesToReturn);
                int  getbytesToReturn();
                unsigned char getAddress();
                void setTime(CtiTime currentTime, int delay);
                CtiTime getTime();
                bool isReady();
                unsigned char getmctAddress();
                void setmctAddress(unsigned char address);

            private:
                int _bytesToReturn;  // Store L1
                int _messageLength;
                unsigned char _data [300];
                CtiTime _timeWhenReady;
                unsigned char _address;
                //route infot (3 elements)
                unsigned char _RTE_CIRCUIT;
                unsigned char _RTE_RPTCON;
                unsigned char _RTE_TYPCON;
                int _wordType;      //a,b,g words
                int _ioType;       // i/o
                int _function;
                unsigned char _mctAddress;
                unsigned char _QENID[4];
        };

        // Constructor to build a new Message
        void CreateMessage(int MsgType, int WrdFnc, unsigned char Data[], int ccuNumber, int &setccuNumber, unsigned char Address = 0x00, unsigned char Frame = 0x00);
		//Send the message back to porter
		int SendMsg(unsigned char SendData[]);
		//Build a new message
		void CreateMsg(int ccuNumber);
		//Listen for and store an incoming message
		int ReceiveMsg(unsigned char ReadBuffer[], int &setccuNumber);
		//Listen for and store the rest of the message
		void ReceiveMore(unsigned char ReadBuffer[], int counter);
        //Print the incoming message information to the screen
        void PrintInput();
        //Output the outgoing message information to the screen
        void PrintMessage();
		//  Translate message for user
		void TranslateInfo(bool direction, string & printMsg, string & printCmd, string & printPre, string & printWrd, string & printFnc);
		//Returns a pointer to the listening socket
		CTINEXUS * getListenSocket();
		//Returns a pointer to newSocket
		CTINEXUS * getNewSocket();
        //  Figure out what the preamble says
		void DecodeCommand(unsigned char Data[]);
        //  Figure out what the preamble says
		int DecodePreamble(int &setccuNumber);
        // This is used to insert words into incoming messages 
		void InsertWord(int WordType, unsigned char Data[], int counter);
        //  Returns the frame of message
		unsigned char getFrame();
        //  Returns the frame when in queued mode
		unsigned char getFrame(int frameCount);
        //  Figure out the IDLC protocol
		int DecodeIDLC(int & setccuNumber);
        //  Determine what kind of word it is
		int DecodeDefinition();
        //  Determine the number of words to follow
		int DecodeWTF(int WordType, unsigned char Data[]);
		//  Determine what function the word specifies
		int DecodeFunction(int WordType, unsigned char Data[]);
        //  Build a message to store in the queue
        void CreateQueuedMsg();
        //  Create a response for immediate use (not to put into the queue)
        void CreateResponse(int command);
        //  Copy the message from the queue into the 711 outgoing message storage
        void LoadQueuedMsg();
        //  Return the correct RLEN plus 14
        unsigned char getRLEN();
        //  Create a response to a message that has been received and store it in the array of a queue message struct
        void CreateQueuedResponse();
        //  Decode the information contained in an incoming message 
        void decodeForQueueMessage(int & type, int & iotype, int & function, unsigned char & address, unsigned char & mctaddress,int & bytesToReturn, int offset);
        //  Returns number of data bytes to be sent to poerter at an index and fills the data array
        void getData(unsigned char mctAddress, int function, int ioType, int bytesToReturn);



	private:
		//Storage for sockets
		WSADATA wsaData;
		CTINEXUS * listenSocket;
		CTINEXUS * newSocket;

        CCU710 subCCU710;

        unsigned char _address;
        unsigned char _data[300];

        int _messageType;
        int _commandType;
        int _preamble;
        unsigned char _messageData[300];
        EmetconWord _words[4];
        int _bytesToFollow;
        int _indexOfEnd;
        int _indexOfWords;

        int _outmessageType;
        int _outcommandType;
        int _outpreamble;
        unsigned char _outmessageData[300];
        EmetconWord _outwords[4];
        int _outindexOfEnd;
        int _outindexOfWords;
        int _mctNumber;
        deque <_queueMessage> _messageQueue;
        int _qmessagesReady;
};

#endif


