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
#include "CCU710.h"
#include "ctiTime.h"
#include "mctStruct.h"
#include "EmetconWordB.h"
#include "Mct410Sim.h"
#include <queue>


class CCU711
{
    public:
        void handleRequest(CTINEXUS* socket);
        void processRequest(unsigned char ReadBuffer[], int bufferSize);

    public:
        CCU711(unsigned char addressFound);

        struct queueMessage {
            public:
                int getmessageLength();
                void copyInto(unsigned char Data[], int bytes);
                void copyInOriginal(unsigned char buffer[], int bytes);
                void setmessageLength(int bytes);
                void setOrigLength(int bytes);
                int  getOrigLength();
                void setQENID(unsigned char one,unsigned char two, unsigned char three, unsigned char four);
                unsigned char getQENID(int index);
                void copyOut(unsigned char Data[]);
                void copyOutOriginal(unsigned char buf[]);
                void initializeMessage();
                int  getWord();
                void setWord(int type);
                void setiotype(int iotype);
                void setFunction(int function);
                int  getFunction();
                int  getioType();
                void setbytesToReturn(int bytesToReturn);
                int  getbytesToReturn();
                void setTime(CtiTime currentTime, int delay);
                CtiTime getTime();
                bool isReady();
                int getmctAddress();
                void setmctAddress(int address);

            private:
                int _bytesToReturn;  // Store L1
                int _messageLength;
                int origLength;
                unsigned char _data [300];
                unsigned char origData [300];
                CtiTime _timeWhenReady;
                int _wordType;      //a,b,g words
                int _ioType;       // i/o
                int _function;
                int _mctAddress;
                unsigned char _QENID[4];
        };

        // Constructor to build a new Message
        void CreateMessage(int MsgType, int WrdFnc, unsigned char Data[], unsigned char Address = 0x00, unsigned char Frame = 0x00);
        //Send the message back to porter
        int SendMsg(unsigned char SendData[]);
        //Build a new message
        void CreateMsg();
        //Listen for and store an incoming message
        int ReceiveMsg(unsigned char ReadBuffer[]);
        //Listen for and store the rest of the message
        void ReceiveMore(unsigned char ReadBuffer[]);
        //Print the incoming message information to the screen
        void PrintInput();
        //Output the outgoing message information to the screen
        void PrintMessage();
        //  Translate message for user
        void TranslateInfo(bool direction, std::string & printMsg, std::string & printCmd, std::string & printPre, std::string & printWrd, std::string & printFnc);
        //  Figure out what the preamble says
        void DecodeCommand(unsigned char Data[]);

        /* Not called
        //  Figure out what the preamble says
        int DecodePreamble(int &setccuNumber);
        // This is used to insert words into incoming messages
        void InsertWord(int WordType, unsigned char Data[], int counter);
        */

        //  Returns the frame of message
        unsigned char getFrame();
        //  Returns the frame when in queued mode
        unsigned char getFrame(int frameCount);
        //  Figure out the IDLC protocol
        int DecodeIDLC();

        //  Create a response for immediate use (not to put into the queue)
        void CreateResponse(int command);
        //  Copy the message from the queue into the 711 outgoing message storage
        void LoadQueuedMsg();

        /* LGRPQ Functions */
        bool createLGRPQResponse(Mct410Sim *mct, queueMessage &message);
        void decodeLGRPQLong(int &type, int &iotype, int &function, unsigned char &address, int &mctaddress,int &bytesToReturn, int &repeaters, int offset);
        void processMsgLGRPQ();

        void processMsgDTRAN();

        //  Return the mct addresses from the current message group
        void getNeededAddresses(int addressArray[]);
        void setStrategy(int strategy);
        int getStrategy();
        int determingMessageLength(unsigned char controlByte, unsigned char lenByte);

    private:

        CCU710 subCCU710;

        unsigned char _data[300];

        int _messageType;
        int _commandType;
        int _preamble;
        unsigned char _messageData[300];
        EmetconWord _words[4];
        int _bytesToFollow;

        int _outmessageType;
        int _outcommandType;
        int _outpreamble;
        unsigned char _outmessageData[300];
        EmetconWord _outwords[4];
        int _outindexOfEnd;
        int _mctNumber;
        std::deque <queueMessage> _messageQueue;
        int _qmessagesReady;
        int _strategy;

        EmetconWordB* getEmetconBWord();
        void setEmetconBWord(EmetconWordBase* word); // For memory protection
        EmetconWordBase* magicWord;//Incoming B Word  ?!? .sdflkjasdfl;kjas;lkjasg;lkwetg;lkfb

        std::map<int,Mct410Sim*> mctMap;
};

#endif

