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
#include "yukon.h"
#include "CCU711.h"
#include "ctinexus.h"
#include "numstr.h"
#include "cticalls.h"
#include "cti_asmc.h"
#include "color.h"
#include "time.h"
#include "logger.h"
#include "SharedFunctions.h"
#include <queue>
#include <Winsock2.h>

#include "EmetconWordBase.h"
#include "EmetconWordC.h"
#include "EmetconWordB.h"
#include "EmetconWordFactory.h"

#include <boost/thread/mutex.hpp>

using namespace std;

DLLIMPORT extern CtiLogger   dout;

/**************************************************
/*  CCU711 functions
***************************************************/
CCU711::CCU711(unsigned char addressFound) :
      _indexOfEnd(0),
      _indexOfWords(0),
      _outindexOfEnd(0),
      _outindexOfWords(0),
      _messageType(0),
      _commandType(0),
      _preamble(0),
      _bytesToFollow(0),
      _outmessageType(0),
      _outcommandType(0),
      _outpreamble(0),
      _mctNumber(0),
      _qmessagesReady(0),
      _strategy(0)
{
    magicWord = NULL;

    memset(_data, 0, 300);
    memset(_messageData, 0, 300);
    memset(_outmessageData, 0, 300);
}

int CCU711::determingMessageLength(unsigned char controlByte, unsigned char lenByte)
{
    if((controlByte & 0x1f)== 0x1f){
        //Reset Request
        return 1;
    }
    else if((controlByte & 0x01) == 0x00){
        //General Request
        return (lenByte + 0x02);
    }
    //Unhandled warning here?
    return 0;
}

void CCU711::processRequest(unsigned char ReadBuffer[], int bufferSize) 
{
    ////////////////
    //RequestHandlerFactory* factory = RequestHandlerFactory::getInstance();

    //RequestHandler* handler = factory->getHandler(ReadBuffer[0],ReadBuffer[2]);
    ///////////////////
    if( false/*handler != NULL /*We found one.  do stuff*/ ) {

/* Commenting out the request handler stuff.  Phase 2 work.
        unsigned char sendBuffer[300];//size ok?
        int retSize = handler->processRequest(ReadBuffer,bytesRead,sendBuffer);

        if (retSize < 0) {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << endl << retSize << endl << endl;
        }
        else
        {
            int napTime = (((retSize)*8.0)/1200.0)*2*1000.0;  //  Delay at 1200 baud in both directions
            CTISleep(napTime);

            //Why
            unsigned char SendData[300];
            unsigned char *WriteBuffer = sendBuffer;
            memcpy(SendData, WriteBuffer, 300);

            unsigned long bytesWritten = 0;
            socket->CTINexusWrite(&SendData, retSize, &bytesWritten, 15);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << endl << bytesWritten << endl << endl;
            }
        }
*/
    }
    else //////////LEGACY CASE.  Not handled in new handlers. Try this. Last resort.
    {
        if( ReadBuffer[1] != 0x7e )  //Make sure it didn't try to send two messages at once and overlap the two HDLC flags
        {

            int BytesToFollow = ReceiveMsg(ReadBuffer);

            if( BytesToFollow>0 )
            {
                int bytesRead=0;
                ///////////////////////////////
                //Commenting since we are reading it all in at once now. Left as a marker to roll back if needed.
                //while( bytesRead < BytesToFollow )
                //{
                //    socket->CTINexusRead(ReadBuffer + counter, 1, &bytesRead, 15);
                //    //TS: Bug, if we timeout on any reads we will have holes in the buffer.
                //    counter++;
                //}

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    for( int byteitr = 0; byteitr < BytesToFollow; byteitr++ )
                    {
                        if( byteitr == 1 )
                        {
                            //SET_FOREGROUND_BRIGHT_RED;
                            dout << string(CtiNumStr(ReadBuffer[byteitr+4]).hex().zpad(2)) << ' ';
                            //SET_FOREGROUND_BRIGHT_GREEN;
                        }
                        else {
                            dout << string(CtiNumStr(ReadBuffer[byteitr+4]).hex().zpad(2))<<' ';
                        }
                    }
                    dout << endl;
                }

                int mctAddressArray[50];
                memset(mctAddressArray, 0, 50);
                getNeededAddresses(mctAddressArray);  // ask the CCU711 which mct addresses it needs values from the db for

                int i = 0;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    while( (mctAddressArray[i] != 0) )
                    {
                        dout << "\nMct: " << mctAddressArray[i] << endl;
                        i++;
                    }
                }
                mctStruct testStruct;
                mctStruct structArray[100];
                structArray[0]=testStruct;
                ReceiveMore(ReadBuffer);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    PrintInput();
                }
            }
            CreateMsg();
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            //SET_FOREGROUND_BRIGHT_RED;
            dout << "Error: Two IDLC messages overlapped since bytes 0 and 1 are both 0x7e" << endl;
            //SET_FOREGROUND_WHITE;
        }
    }

}


void CCU711::handleRequest(CTINEXUS* socket) 
{
    //  It's a 711 IDLC message
    CtiTime AboutToRead;
    unsigned char ReadBuffer[300];
    int BytesToFollow;
    int counter = 0;
    unsigned long bytesRead = 0;
    BytesToFollow = 4;
    //  Read first few bytes
    while( bytesRead < BytesToFollow )
    {
        socket->CTINexusRead(ReadBuffer + counter  ,1, &bytesRead, 15);
        //TS: Bug. If we time out, we will miss bytes.
        counter++;
    }

    ///////////////////////////////////
    //Legacy Printing.  Remove?
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);    
        if( ReadBuffer[0]==0x7e )
        {
            //SET_FOREGROUND_BRIGHT_YELLOW;
            dout <<'\n'<< AboutToRead.asString();
            //SET_FOREGROUND_BRIGHT_CYAN;
            dout << " IN: " << endl;
        }
        //SET_FOREGROUND_BRIGHT_GREEN;
        for( int byteitr = 0; byteitr < (bytesRead); byteitr++ )
        {
            dout << string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2)) << ' ';
        }
        dout << endl;
    }
    //End Legacy Printing
    /////////////////////////////////////////////////////
    int remainingLength = determingMessageLength(ReadBuffer[2],ReadBuffer[3]);

    bytesRead=0;
    while( bytesRead < remainingLength )
    {
        socket->CTINexusRead(ReadBuffer + counter, 1, &bytesRead, 15);
        //TS: Bug. If we time out, we will miss bytes.
        counter++;
    }

    processRequest(ReadBuffer,bytesRead);

    unsigned char SendData[300];

    int MsgSize = SendMsg(SendData);

    if( MsgSize>0 )
    {
        int napTime = (((MsgSize)*8.0)/1200.0)*2*1000.0;  //  Delay at 1200 baud in both directions
        CTISleep(napTime);
        unsigned long bytesWritten = 0;
        socket->CTINexusWrite(&SendData, MsgSize, &bytesWritten, 15);

        CtiTime DateSent;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            //SET_FOREGROUND_BRIGHT_YELLOW;
            dout << DateSent.asString();
            //SET_FOREGROUND_BRIGHT_CYAN;
            dout << " OUT:" << endl;

            //SET_FOREGROUND_BRIGHT_MAGNETA;

            for( int byteitr = 0; byteitr < bytesWritten; byteitr++ )
            {
                dout << string(CtiNumStr(SendData[byteitr]).hex().zpad(2))<<' ';
            }
            PrintMessage();
            dout << endl;
        }

    }
    else
    {
        //SET_FOREGROUND_BRIGHT_RED;

        //SET_FOREGROUND_WHITE;
    }
}


//Listen for and store an incoming message
int CCU711::ReceiveMsg(unsigned char ReadBuffer[])
{
    //Grab first four bytes for identification
    _messageData[0] = ReadBuffer[0];
    _messageData[1] = ReadBuffer[1];
    _messageData[2] = ReadBuffer[2];
    _messageData[3] = ReadBuffer[3];
    _indexOfEnd = 4;           //   this may cause a PROBLEM  SINCE 710 should be 3 bytes !!!

    _bytesToFollow = DecodeIDLC();

    return _bytesToFollow;
}

//Listen for and store an incoming message
void CCU711::ReceiveMore(unsigned char ReadBuffer[])
{
    int setccuNumber = 0;

    DecodeCommand(ReadBuffer);

    for(int i=10; i<300; i++)
    {
        _messageData[i]=ReadBuffer[i];
    }

    int Ctr = 3;
    int dummyNum = 0;
    if (_commandType == DTRAN)
    {
        processMsgDTRAN();
    }
    else if (_commandType == LGRPQ)
    {
        processMsgLGRPQ();////   <--- Use struct kwhvalue HERE !!!!!!
    }
}


void CCU711::PrintInput()
{
    if(_messageData[0]==0x7e)
    {        
        string printMsg, printCmd, printPre, printWrd, printFnc;

        TranslateInfo(INCOMING, printMsg, printCmd, printPre, printWrd, printFnc);
        {
            dout << "Msg: " << printMsg << "    Cmd: " << printCmd << "    ";
        }
        if(_commandType==DTRAN)
        {
            subCCU710.PrintInput();
        }
        else if(!_messageQueue.empty())
        {
            dout << "Wrd: " << _messageQueue.front().getWord() << endl;
        }
        dout << endl;
    }
}

//Build a new message
void CCU711::CreateMsg()
{
    unsigned char someData[10];
    int setccuNumber = 0;
    if(_messageType== RESETREQ)
    {
        unsigned char Address = _messageData[1];
        CreateMessage(RESETACK, DEFAULT, someData, Address);
    }
    else if(_messageType==GENREQ)
    {
        //  General Request
        unsigned char Frame = getFrame();
        unsigned char Address = _messageData[1];
        if (_commandType == DTRAN)
        {
            if (getEmetconBWord()->getIO() == FUNC_READ || getEmetconBWord()->getIO() == READ) {
                CreateMessage(GENREP, FUNC_READ, someData, Address, Frame);
            }
            if (getEmetconBWord()->getIO() == WRITE || getEmetconBWord()->getIO() == FUNC_WRITE) {
                CreateMessage(GENREP, FUNC_READ, someData, Address, Frame);
            }
        }
        else if(_commandType==LGRPQ) {
            CreateResponse(LGRPQ);
        }
        else if(_commandType==ACTIN) {
            CreateResponse(ACTIN);
        }
        else if(_commandType==XTIME) {
            CreateResponse(XTIME);
        }
        else if(_commandType==WSETS) {
            CreateResponse(WSETS);
        }
        else if(_commandType==RCOLQ) {
            LoadQueuedMsg();
        }
        else{
            CreateMessage(GENREP, DEFAULT, someData, Address, Frame);
        }
    }
}

//Send the message back to porter
int CCU711::SendMsg(unsigned char SendData[]){
    unsigned char *WriteBuffer = _outmessageData;
    int MsgSize = _outindexOfEnd;

    memcpy(SendData, WriteBuffer, 300);

    return MsgSize;
}

void CCU711::PrintMessage(){
    dout << endl;
    //SET_FOREGROUND_WHITE;
    string printType;
    switch(_outmessageType){
    case INPUTTYPE:
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


    if(_messageData[0]==0x7e)
    {
        string printMsg, printCmd, printPre, printWrd, printFnc;

        TranslateInfo(OUTGOING, printMsg, printCmd, printPre, printWrd, printFnc);

        dout << "Queue size: "<< _messageQueue.size();
        dout << "     Msg: " << printMsg << "      " << printCmd;
        if(_commandType==DTRAN)
        {
            subCCU710.PrintMessage();
        }
    }
}

void CCU711::TranslateInfo(bool direction, string & printMsg, string & printCmd, string & printPre, string & printWrd, string & printFnc)
{
    if(direction == INCOMING){
        switch(_messageType){
            case INPUTTYPE:     printMsg.append("INPUT");       break;
            case RESETREQ:  printMsg.append("RESETREQ");    break;
            case RESETACK:  printMsg.append("RESETACK");    break;
            case GENREQ:    printMsg.append("GENREQ");      break;
            case GENREP:    printMsg.append("GENREP");      break;
        }
        switch(_commandType){
            case DTRAN:        printCmd.append("DTRAN");       break;
            case LGRPQ:        printCmd.append("LGRPQ");       break;
            case RCOLQ:        printCmd.append("RCOLQ");       break;
            case ACTIN:        printCmd.append("ACTIN");       break;
            case WSETS:        printCmd.append("WSETS");       break;
            case XTIME:        printCmd.append("XTIME");       break;

        }
        switch(_preamble){
            case FEEDEROP:  printPre.append("FEEDEROP");    break;
        }
        switch(_words[0].getWordType())
        {
            case A_WORD:    printWrd.append("A_WORD");      break;
            case B_WORD:    printWrd.append("B_WORD");      break;
            case C_WORD:    printWrd.append("C_WORD");      break;
            case D_WORD:    printWrd.append("D_WORD");      break;
            case E_WORD:    printWrd.append("E_WORD");      break;
        }
        switch(_words[0].getWordFunction()){
            case FUNCREAD:   printFnc.append("FUNCREAD");     break;
            case FUNC_READ:   printFnc.append("FUNC_READ");     break;
            case FUNC_WRITE:   printFnc.append("FUNC_WRITE");     break;
            case READ:      printFnc.append("READ");        break;
            case WRITE:     printFnc.append("WRITE");       break;
        }
    }
    else if(direction == OUTGOING){
        switch(_outmessageType){
            case INPUTTYPE:     printMsg.append("INPUT");       break;
            case RESETREQ:  printMsg.append("RESETREQ");    break;
            case RESETACK:  printMsg.append("RESETACK");    break;
            case GENREQ:    printMsg.append("GENREQ");      break;
            case GENREP:    printMsg.append("GENREP");      break;
        }
        switch(_outcommandType){
            case 11:        printCmd.append("DTRAN");       break;
        }
        switch(_outpreamble){
            case FEEDEROP:  printPre.append("FEEDEROP");    break;
        }
        switch(_outwords[0].getWordType())
        {
            case A_WORD:    printWrd.append("A_WORD");      break;
            case B_WORD:    printWrd.append("B_WORD");      break;
            case C_WORD:    printWrd.append("C_WORD");      break;
            case D_WORD:    printWrd.append("D_WORD");      break;
            case E_WORD:    printWrd.append("E_WORD");      break;
        }
        switch(_outwords[0].getWordFunction()){
            case FUNCREAD:   printFnc.append("FUNCREAD");     break;
            case READ:      printFnc.append("READ");        break;
            case WRITE:     printFnc.append("WRITE");       break;
        }
    }
}

void CCU711::CreateMessage(int MsgType, int WrdFnc, unsigned char Data[], unsigned char Address, unsigned char Frame)
{
    _messageType = MsgType;

    if(_messageType == RESETACK)
    {
        _outmessageData[0] = 0x7e;
        _outmessageData[1] = Address;   //  slave address
        _outmessageData[2] = 0x73;
        unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), 2);
        _outmessageData[3] = HIBYTE(CRC);   //  insert CRC code
        _outmessageData[4] = LOBYTE(CRC);   //  insert CRC code
        _outindexOfEnd = 5;
    }
    else if(_messageType == GENREP)
    {
        int ncocts = _qmessagesReady * 3;
        deque <_queueMessage> ::iterator itr;
        for(itr=_messageQueue.begin(); itr!=_messageQueue.end(); itr++)
        {
             _queueMessage temp = *itr;
             ncocts += temp.getbytesToReturn();
             _qmessagesReady += temp.isReady();
        }
        _outmessageType = GENREP;
        int Ctr = 0;

        //Does not ever get here...
        if(WrdFnc==ACKACK)
        {
            _outmessageData[Ctr++] = 0x7e;
            _outmessageData[Ctr++] = Address;  //  slave address
            _outmessageData[Ctr++] = Frame;     //  control
            Ctr++;          // # of bytes to follow minus two filled in at bottom of section
            _outmessageData[Ctr++] = 0x02;      // SRC/DES
            _outmessageData[Ctr++] = 0xa6;      // Echo of command received
            _outmessageData[Ctr++] = 0x00;      // system status items
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      // device status items
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x20-_messageQueue.size(); // "  "
            _outmessageData[Ctr++] = _qmessagesReady;     // NCSETS
            _outmessageData[Ctr++] = 0x00;                       // NCOCTS
            _outmessageData[Ctr++] = ncocts;    // "    "
            _outmessageData[Ctr++] = 0x00;      // process status items
            _outmessageData[Ctr++] = 0x00;      //    "   "

            //Thain: Generate ACK here: For now they will both be the same
            unsigned char ack = makeAck(Address);
            //Compute even parity add 1 if needed
            

            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[3] = Ctr-4;      // # of bytes to follow minus two (see note above)

            unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), Ctr-1);
            _outmessageData[Ctr++] = HIBYTE (CRC);
            _outmessageData[Ctr++] = LOBYTE (CRC);
        }
        else if(_commandType==DTRAN)//why is our else if testing a new variable?
        {
            Mct410Sim* mctPtr = NULL;
            std::map<int,Mct410Sim*>::iterator mctItr = mctMap.find(getEmetconBWord()->getAddress());
            if(mctItr == mctMap.end())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << " ERROR. Missing mct in Map, it should be there! \n";
                }
                return;
            }
            mctPtr = (*mctItr).second;

            _outmessageData[Ctr++] = 0x7e;
            _outmessageData[Ctr++] = Address;   //  slave address
            _outmessageData[Ctr++] = Frame;     //  control
            Ctr++;          // # of bytes to follow minus two filled in at bottom of section
            _outmessageData[Ctr++] = 0x02;      // SRC/DES
            _outmessageData[Ctr++] = 0xa6;      // Echo of command received
            _outmessageData[Ctr++] = 0x00;      // system status items
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      // device status items
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x20-_messageQueue.size(); // "  "
            _outmessageData[Ctr++] = _qmessagesReady;     // NCSETS
            _outmessageData[Ctr++] = 0x00;                       // NCOCTS
            _outmessageData[Ctr++] = ncocts;    // "    "
            _outmessageData[Ctr++] = 0x00;     // process status items
            _outmessageData[Ctr++] = 0x00;     //    "   "

            if(getEmetconBWord()->getIO() == FUNC_READ || getEmetconBWord()->getIO() == READ)
            {
                unsigned char ack = makeAck(Address>>1);
                _outmessageData[Ctr++] = ack;
                _outmessageData[Ctr++] = ack;
                _outmessageData[Ctr++] = 0x82;

                EmetconWord newWord;
                newWord.setStrategy(_strategy);

                //Look into getRepeaters InsertWord does not use this value anywhere. Should it?
                //Ctr = newWord.InsertWord(D_WORD, CtiTime(), _outmessageData, 0, _mctNumber, Ctr, getNumberOfRepeaters());

                //Generate response word(s).
                int size = 0;
                unsigned char *ptr = mctPtr->generateEmetconWordResponse(size,getEmetconBWord()->getFunction(),CtiTime());
                
                for (int i = 0; i < size; i++)
                {
                    _outmessageData[Ctr++] = ptr[i]; 
                }

                _outmessageData[Ctr++] = ack;
            }
            else if(getEmetconBWord()->getIO() == WRITE || getEmetconBWord()->getIO() == FUNC_WRITE)
            {
                int func = getEmetconBWord()->getFunction();
            
                if (func == 5)
                {
                    //Assumed 2.  This can be verified
                    EmetconWordC cWord  (_messageData+17,7);
                    EmetconWordC cWord2 (_messageData+24,7);
            
                    unsigned char ptr[5];
                    cWord.getData(ptr);
            
                    int poi = 0; //first two bytes are not the Data
                    poi = ptr[2];  poi = poi << 8;
                    poi += ptr[3]; poi = poi << 8;
                    poi += ptr[4]; poi = poi << 8;
            
                    cWord2.getData(ptr);
                    poi += ptr[0];
            
            
                    mctPtr->setPeroidOfInterest((long)poi);
            
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Debug: UTC Time in seconds: " << poi << ", Time: " << CtiTime((unsigned long)poi) <<  endl;
                }

                unsigned char ack = makeAck(Address>>1);
                _outmessageData[Ctr++] = ack;
                _outmessageData[Ctr++] = ack;
                _outmessageData[Ctr++] = 0x82;
    
                EmetconWord newWord;
                newWord.setStrategy(_strategy);

                //Ctr = newWord.InsertWord(D_WORD, CtiTime(), _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
                int size = 0;
                unsigned char *ptr = mctPtr->generateEmetconWordResponse(size,getEmetconBWord()->getFunction(),CtiTime());
                
                for (int i = 0; i < size; i++)
                {
                    _outmessageData[Ctr++] = ptr[i]; 
                }
                _outmessageData[Ctr++] = ack;

                delete [] ptr;

            }

            _outmessageData[3] = Ctr-4;      // # of bytes to follow minus two

            unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), Ctr-1);
            _outmessageData[Ctr++] = HIBYTE (CRC);
            _outmessageData[Ctr++] = LOBYTE (CRC);
        }
        _outindexOfEnd = Ctr;
    }
    else if(_messageType == PING) //why is our else if testing a new variable?
    {
        int Ctr = 0;
        _outmessageData[Ctr++] = 0xc3;
        _outmessageData[Ctr++] = 0xc3;
        _outmessageData[Ctr++] = 0xf5;
        _outmessageData[Ctr++] = 0x55;
        _outindexOfEnd = Ctr;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: Could not form a suitable reply" << endl;
    }
}


void CCU711::DecodeCommand(unsigned char Data[]){
    _messageData[4]=Data[4];
    _messageData[5]=Data[5];
    _messageData[6]=Data[6];
    _messageData[7]=Data[7];
    _messageData[8]=Data[8];
    _messageData[9]=Data[9];
    if(Data[5] == 0x26) {
        _commandType = DTRAN;
    }
    else if(Data[5] == 0x2b) {
        _commandType = LGRPQ;    //  Load request group into queue  CMND=43 in book
    }
    else if(Data[5] == 0x2a) {
        _commandType = XTIME;    //  Transmit time sync  CMND=42 in book
    }
    else if(Data[5] == 0x27) {
        _commandType = RCOLQ;    //  Read collected queue entries  CMND=39 in book
    }
    else if(Data[5] == 0x00) {
        _commandType = ACTIN;    //  Action/Action sequence  CMND=00 in book
    }
    else if(Data[5] == 0x09) {
        _commandType = WSETS;    //  Write Parameter sets by ID  CMND=9 in book
    }
}

/*
int CCU711::DecodePreamble(int &setccuNumber)
{
    char _bytesToFollow = 0;

    // set the ccu number by checking the preamble for the address
    setccuNumber = 0;

    if( _messageData[2] & 0x04 )
    {
        //  Feeder operation specified
        _preamble = FEEDEROP;

        //////////////////////////////////////////////////////////////////////////
        // ///////////////////////////////////////////////////////////////////////
        // ///////    CHANGE THESE NEXT TWO LINES SO THAT        /////////////////
        // //////     THEY DETERMINE THE BYTES TO FOLLOW BASED  //////////////////
        // //////     SOLELY ON THE B WORD  !!!                 //////////////////
        if( (_messageData[2] & 0x07) == 0x07 )
        {
            _bytesToFollow = 7;
        }
        else if( (_messageData[2] & 0x0e)== 0x0e )
        {
            _bytesToFollow = 14;
        }
    }
    else if((_messageData[3]==0x53) &&
            (_messageData[4]==0xf5) &&
            (_messageData[5]==0x55)) {
        // CCU710 ping in IDLC wrap
        _messageType = PING;
        _bytesToFollow = 0;
    }
    else if((_messageData[0]==0x53) &&
            (_messageData[1]==0xf5) &&
            (_messageData[2]==0x55)) {
        // CCU710 ping
        _messageType = PING;
        _bytesToFollow = 0;
    }

    else if((_messageData[3]==0x47) && (_messageData[4]==0x30) && (_messageData[5]==0x8e)) {
        // CCU710 ping
        _messageType = '1';
        _bytesToFollow = 14;
    }
    else if((_messageData[3]==0x47) && (_messageData[4]==0x30) && (_messageData[5]==0x95)) {
        // CCU710 ping
        _messageType = '2';
        _bytesToFollow = 21;
    }
    if(_messageData[4]== 0x0){
    }
    if(_messageData[5]== 0x0){
    }
    return _bytesToFollow;
}
*/
/* Not called.
//  This is used to insert _words into incoming messages
void CCU711::InsertWord(int WordType, unsigned char Data[], int counter)
{
    _messageData[10]=Data[10];
    _messageData[11]=Data[11];
    _messageData[12]=Data[12];
    _messageData[13]=Data[13];
    _messageData[14]=Data[14];
    _messageData[15]=Data[15];
    _messageData[16]=Data[16];
    int WordFunction;
    int InsertMore = 0;
    int WTF = 0;
    //_messageData[3]=Data[0];
    WordType = DecodeDefinition();
    WordFunction = DecodeFunction(WordType, Data);
    WTF = DecodeWTF(WordType, Data);
    //  If there is a 'c' word following the 'b' word
    //  then WTF indicates 'c' _words to follow so they should be stored
    if(WTF>0) {
        if((Data[7] & 0xc0) == 0xc0) {
            InsertMore = WTF;
        }
    }

    EmetconWord oneWord;
    oneWord.InsertWord(WordType, CtiTime(), Data, WordFunction, 0, 0, (subCCU710.getRepeaters()));
    oneWord.setWTF(WTF);
    _indexOfEnd += oneWord.getWordSize();
    //_words[_indexOfWords]= oneWord;    //  This line was here to count if there were multiple words: needs to be redone
    _indexOfWords++;

    //  If the first word is a b word, see how many c _words follow it
    //  and insert them into the incoming message
    if(_words[0].getWordType() == 2) {
        for(int i=0; i<InsertMore; i++) {
            EmetconWord anotherWord;
            anotherWord.InsertWord(3, CtiTime(), Data, WordFunction, 0, 0, (subCCU710.getRepeaters()));
            _indexOfEnd += anotherWord.getWordSize();
            _words[_indexOfWords]= anotherWord;
            _indexOfWords++;
        }
    }
}
*/

unsigned char CCU711::getFrame(){
    unsigned char Frame = 0x00;
    Frame = (_messageData[2] & 0xe0);
    Frame = (Frame >> 4);
    Frame = (Frame | 0x10);
    return Frame;
}


int CCU711::DecodeIDLC(){
    int _bytesToFollow = 0;
    if((_messageData[0] & 0x7e) == 0x7e){
        //  IDLC LAYER 2 Asynchronous Link Control
        if((_messageData[2] & 0x1f)== 0x1f){
        //  Reset Request
            _messageType = RESETREQ;
            _bytesToFollow = 1;
        }
        else if((_messageData[2] & 0x01) == 0x00){
        //  General Request
            _messageType = GENREQ;
            _bytesToFollow = (_messageData[3] + 0x02);
        }
    }
    else{  //  The message is not an IDLC message.  Probably 710 protocol instead
        return -1;
    }
    return _bytesToFollow;
}

void CCU711::processMsgLGRPQ()
{    
    int counter = 0;
    //These are linked.  
    int length[20];
    int ptr[20];
    ptr[0] = 6;//seed the array with the first command

    bool more = true;
    while(more)
    {
        length[counter] = _messageData[ptr[counter]];
        if (length[counter] > 0)
        {
            counter++;
            ptr[counter] = length[counter-1] + ptr[counter-1];
        }
        else
        {
            more = false;
        }

        if (counter >= 19)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Error: OMG THE world is Ending! more than 20 commands in one message. " << endl;
            more = false;            
        }
    }

    for(int i = 0; i < counter; i++)
    {
        Mct410Sim* mctHolder = NULL;
        _queueMessage newMessage;
        newMessage.initializeMessage();

        //VERIFY THIS STILL WORKS FOR LEGACY
        unsigned char one, two, three, four;
        one   = _messageData[ptr[i]+1];
        two   = _messageData[ptr[i]+2];
        three = _messageData[ptr[i]+3];
        four  = _messageData[ptr[i]+4];
        newMessage.setQENID(one, two, three, four);
        
        newMessage.copyInOriginal(_messageData+ptr[i], length[i]);
        //Legacy below 
        int type = 0;
        int iotype = 0;
        int function = 0;
        unsigned char address;
        int mctaddress;
        int bytesToReturn = 0;
        int repeaters = 0;
        //Determing CMND Type.
        if ((one & 0x80) == 0)
        {
            //Long Format!
            decodeLGRPQLong(type, iotype, function, address, mctaddress, bytesToReturn,repeaters, ptr[i]);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Error: LGRPQ Short format not supported. Not returning any values. " << endl;
            continue;
        }

        newMessage.setWord(type);
        newMessage.setiotype(iotype);
        newMessage.setFunction(function);
        newMessage.setAddress(address);
        newMessage.setbytesToReturn(bytesToReturn);
        newMessage.setmctAddress(mctaddress);

        std::map<int,Mct410Sim*>::iterator mctItr = mctMap.find((int)mctaddress);
        if(mctItr == mctMap.end())
        {
            mctHolder = new Mct410Sim();
            mctHolder->setMctAddress(mctaddress);
            mctHolder->setNumberOfRepeaters(repeaters);
            mctMap[mctHolder->getMctAddress()] = mctHolder;
        }
        else
        {
            mctHolder = (*mctItr).second;
        }

        CtiTime currentTime;

        //Base Delay off number of repeaters
        int delay = repeaters*3;

        if(_messageQueue.empty())
        {
            newMessage.setTime(currentTime, delay);
        }
        else
        {
            newMessage.setTime(_messageQueue.back().getTime(), delay);   //Should be words !!!!!
        }

        _messageQueue.push_back(newMessage);
        createLGRPQResponse(mctHolder);
    }
}



void CCU711::createLGRPQResponse(Mct410Sim* mct)
{
    if (_messageQueue.back().getWord() != B_WORD)
    {
        _messageQueue.pop_back();
        return;
    }

    unsigned char Data[300];
    int ctr=0;

    if (_messageQueue.back().getioType() == FUNC_READ)
    {
        _queueMessage msg = _messageQueue.back();
        _messageQueue.pop_back();
        Data[ctr++] = (0x10 + msg.getbytesToReturn());
        Data[ctr++] = msg.getQENID(0);
        Data[ctr++] = msg.getQENID(1);
        Data[ctr++] = msg.getQENID(2);
        Data[ctr++] = msg.getQENID(3);
        Data[ctr++] = 0xf0; // ENSTA
        Data[ctr++] = 0x00;//7
        Data[ctr++] = 0x00;
        Data[ctr++] = 0x00;
        Data[ctr++] = 0x00; // ROUTE
        Data[ctr++] = 0x01; // NFUNC //always 1.
        Data[ctr++] = 0x40; // S1
        Data[ctr++] = 0x01; // "  " 13
        Data[ctr++] = msg.getbytesToReturn();
        //15-16 Missing since nfunc is 1
        //17 missing cause nfunc == 1
        //18-19 missing cause nfunc != 3
        //20 Missing cause nfunc != 3
        Data[ctr++] = 0x00; // TS
        Data[ctr++] = 0x00; // "  "
        unsigned char * ptr = NULL;

        int mctAddress = msg.getmctAddress();
        int function = msg.getFunction();
        int ioType = msg.getioType();
        int bytesToReturn = msg.getbytesToReturn();

        if (msg.getFunction() == 144)
        {
            ptr = mct->getKWHData(bytesToReturn);
        }
        else if (msg.getFunction() >= 64 && msg.getFunction() <= 79)
        {

            ptr = mct->getLongLoadProfileData(function,bytesToReturn);
        }
        else if (msg.getFunction() >= 80 && msg.getFunction() <= 143)
        {

            ptr = mct->getLoadProfileData(function,bytesToReturn);
        }

        if (ptr != NULL)
        {
            for(int i = 0; i < bytesToReturn; i++)
            {
                Data[ctr++] = ptr[i]; //Data
            }
            delete [] ptr;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Error: Unsupported Function, " << function << ", Sending back zero's " << endl;
            for(int i = 0; i < bytesToReturn; i++)
            {
                Data[ctr++] = 0x00; //Data
            }
        }

        msg.setmessageLength(ctr);
        msg.copyInto(Data, ctr);
        _messageQueue.push_back(msg);

        return;
    }
    else if (_messageQueue.back().getioType() == READ)
    {        
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << " Warning: This call was thought to be unreachable." << endl;
        }
        Data[ctr++] = (0x10 + _messageQueue.back().getbytesToReturn());
        Data[ctr++] = _messageQueue.back().getQENID(0);
        Data[ctr++] = _messageQueue.back().getQENID(1);
        Data[ctr++] = _messageQueue.back().getQENID(2);
        Data[ctr++] = _messageQueue.back().getQENID(3);
        Data[ctr++] = 0xf0; // ENSTA
        Data[ctr++] = 0x00;
        Data[ctr++] = 0x00;
        Data[ctr++] = 0x00;
        Data[ctr++] = 0x00; // ROUTE
        Data[ctr++] = 0x01; //      THIS SHOULD ALWAYS BE 0x01 !!!!        // NFUNC
        Data[ctr++] = 0x40; // S1
        Data[ctr++] = 0x01; // "  "
        Data[ctr++] = _messageQueue.back().getbytesToReturn(); // L1
        Data[ctr++] = 0x00; // TS
        Data[ctr++] = 0x00; // "  "
        int mctAddress = _messageQueue.back().getmctAddress();
        int bytesToReturn = _messageQueue.back().getbytesToReturn();
        unsigned char * ptr = mct->getKWHData(bytesToReturn);

        if (ptr != NULL)
        {
            for(int i = 0; i<bytesToReturn; i++)
            {
                Data[ctr++] = ptr[i]; //Data
            }
            delete [] ptr;
        } else {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Error: Unsupported Function, " << _messageQueue.back().getFunction() 
                 << ", No data generated. Unpredictable behavior from this point. " << endl;
        }

        _messageQueue.back().setmessageLength(ctr);
        _messageQueue.back().copyInto(Data, ctr);
        return;
    }
    else if (_messageQueue.back().getioType() == FUNC_WRITE)
    {
        int len = _messageQueue.back().getOrigLength();
        unsigned char* buf = new unsigned char [len];

        _messageQueue.back().copyOutOriginal(buf);
        int function = buf[15];

        switch (function)
        {
            case 5:
                if (buf[16] != 6)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Error: Data Size unexpected, " << buf[16] << ", expected 6. Unpredictable behavior from this point. " << endl;
                    return;
                }
                // byte 0(17) is SPID
                if (buf[17] != 255)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Warning: SPID not supported. Need 0xFF. Received: " << buf[17] 
                         << " ." << endl;
                    return;
                }
                // byte 1(18) is the Channel.  Dont know what to do with this.

                // Byte 2-5 is the UTC Start Time
                {
                    int poi = 0;
                    poi = buf[19];  poi = poi << 8;
                    poi += buf[20]; poi = poi << 8;
                    poi += buf[21]; poi = poi << 8;
                    poi += buf[22];
                    
                    mct->setPeroidOfInterest((long)poi);

                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Debug: UTC Time in seconds: " << poi << ", Time: " << CtiTime((unsigned long)poi) <<  endl;
                }
                break;
            default:
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Error: Unsupported Function in Write Command, " << function << "." << endl;
                break;
        }
        return;
    }
}

void CCU711::CreateResponse(int command)
{
    int ncocts = _qmessagesReady * 3;
    deque <_queueMessage> ::iterator itr;
    for(itr=_messageQueue.begin(); itr!=_messageQueue.end(); itr++)
    {
         _queueMessage temp = *itr;
         ncocts += temp.getbytesToReturn();
         _qmessagesReady += temp.isReady();
    }

    int Ctr = 0;
    unsigned char Frame = getFrame();
    unsigned char Address = _messageData[1];
    _outmessageData[Ctr++] = 0x7e;
    _outmessageData[Ctr++] = Address;   //  slave address
    _outmessageData[Ctr++] = Frame;     //  control
    Ctr++;          // # of bytes to follow minus two filled in at bottom of section
    _outmessageData[Ctr++] = 0x02;      // SRC/DES
    _outmessageData[Ctr++] = (0xa0 | (0x0f & _messageData[5]));      // Echo of command received and high bit set
    _outmessageData[Ctr++] = 0x00;      // system status items
    _outmessageData[Ctr++] = 0x00;      //    "   "
    _outmessageData[Ctr++] = 0x00;      //    "   "
    _outmessageData[Ctr++] = 0x00;      //    "   "
    _outmessageData[Ctr++] = 0x00;     // device status items
    _outmessageData[Ctr++] = 0x00;     //    "   "
    _outmessageData[Ctr++] = 0x20-_messageQueue.size();  // "  "
    _outmessageData[Ctr++] = _qmessagesReady;            // NCSETS
    _outmessageData[Ctr++] = 0x00;                       // NCOCTS
    _outmessageData[Ctr++] = ncocts;                     // "    "
    _outmessageData[Ctr++] = 0x00;     // process status items
    _outmessageData[Ctr++] = 0x00;     //    "   "

    if((command!=WSETS) && (command!=XTIME))
    {
        _outmessageData[Ctr++] = makeAck(Address);
        _outmessageData[Ctr++] = makeAck(Address);
    }
    _outmessageData[3] = Ctr-4;      // # of bytes to follow minus two (see note above)

    unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), Ctr-1);
    _outmessageData[Ctr++] = HIBYTE (CRC);
    _outmessageData[Ctr++] = LOBYTE (CRC);
    _outindexOfEnd = Ctr;
}


void CCU711::LoadQueuedMsg()
{

    if(!_messageQueue.empty())
    {
        int ncocts = 0;//_qmessagesReady * 3;  MADE A CHANGE HERE
        deque <_queueMessage> ::iterator itr;
        for(itr=_messageQueue.begin(); itr!=_messageQueue.end(); itr++)
        {
             _queueMessage temp = *itr;
             _qmessagesReady += temp.isReady();
             if(temp.isReady()) {
                 ncocts += temp.getbytesToReturn();
             }
        }
        int ctr = 0;
        unsigned char preData[300];
        if(_qmessagesReady==0)
        {
            _outmessageData[ctr++] = 0x7e;
            _outmessageData[ctr++] = _messageData[1]; //  Stored in 711 on startup
            _outmessageData[ctr++] = getFrame(1);
            ctr++;                 //length set below
            _outmessageData[ctr++] = 0x00;
            _outmessageData[ctr++] = 0xa7;
            _outmessageData[ctr++] = 0x00; //Stats
            _outmessageData[ctr++] = 0x00; // "  "
            _outmessageData[ctr++] = 0x18; // "  "
            _outmessageData[ctr++] = 0x50; // "  "
            _outmessageData[ctr++] = 0x00; //StatD
            _outmessageData[ctr++] = 0x00; // "  "
            _outmessageData[ctr++] = 0x20-_messageQueue.size(); // "  "
            _outmessageData[ctr++] = 0x00;     // NCSETS
            _outmessageData[ctr++] = 0x00;                       // NCOCTS
            _outmessageData[ctr++] = 0x00;  // "    "
            _outmessageData[ctr++] = 0x00;    //StatP
            _outmessageData[ctr++] = 0x00;    // "  "
        }
        else
        {
            _outmessageData[ctr++] = 0x7e;
            _outmessageData[ctr++] = _messageData[1]; //  Stored in 711 on startup
            _outmessageData[ctr++] = getFrame(1);
            ctr++;                 //length set below
            _outmessageData[ctr++] = 0x00;
            _outmessageData[ctr++] = 0xa7;
            _outmessageData[ctr++] = 0x00; //Stats
            _outmessageData[ctr++] = 0x00; // "  "
            _outmessageData[ctr++] = 0x18; // "  "
            _outmessageData[ctr++] = 0x50; // "  "
            _outmessageData[ctr++] = 0x00; //StatD
            _outmessageData[ctr++] = 0x00; // "  "
            _outmessageData[ctr++] = 0x20-_messageQueue.size(); // "  "
            _outmessageData[ctr++] = _qmessagesReady;     // NCSETS
            _outmessageData[ctr++] = 0x00;                       // NCOCTS
            _outmessageData[ctr++] = ncocts;  // "    "
            _outmessageData[ctr++] = 0x00;    //StatP
            _outmessageData[ctr++] = 0x00;    // "  "
            while(!_messageQueue.empty() && _messageQueue.front().isReady() && ctr < 250)
            {
                unsigned char Data[300];
                _messageQueue.front().copyOut(Data);
				int x = _messageQueue.front().getmessageLength();
                for(int i=0; i<x; i++)
                {
                    _outmessageData[ctr++]=Data[i];
                }
                _qmessagesReady--;
                _messageQueue.pop_front();
            }
        }
        _outmessageData[ctr++] = 0x00;  //  SETL = 0
        _outmessageData[3] = ctr-4;
        unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), ctr-1);
        _outmessageData[ctr++] = HIBYTE (CRC);
        _outmessageData[ctr++] = LOBYTE (CRC);

        _outindexOfEnd = ctr;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            //SET_FOREGROUND_BRIGHT_RED;
            dout<<'\n'<<"Error: Message queue is empty"<<endl;
            //SET_FOREGROUND_WHITE;
        }
        int ncocts = _qmessagesReady * 3;
        deque <_queueMessage> ::iterator itr;
        for(itr=_messageQueue.begin(); itr!=_messageQueue.end(); itr++)
        {
             _queueMessage temp = *itr;
             ncocts += temp.getbytesToReturn();
             _qmessagesReady += temp.isReady();
        }
        int ctr = 0;
        unsigned char preData[300];

        _outmessageData[ctr++] = 0x7e;
        _outmessageData[ctr++] = _messageData[1]; //  Stored in 711 on startup
        _outmessageData[ctr++] = getFrame(1);
        ctr++;                 //length set below
        _outmessageData[ctr++] = 0x00;
        _outmessageData[ctr++] = 0xa7;
        _outmessageData[ctr++] = 0x00; //Stats
        _outmessageData[ctr++] = 0x00; // "  "
        _outmessageData[ctr++] = 0x18; // "  "
        _outmessageData[ctr++] = 0x50; // "  "
        _outmessageData[ctr++] = 0x00; //StatD
        _outmessageData[ctr++] = 0x00; // "  "
        _outmessageData[ctr++] = 0x20-_messageQueue.size(); // "  "
        _outmessageData[ctr++] = 0x00;     // NCSETS
        _outmessageData[ctr++] = 0x00;                       // NCOCTS
        _outmessageData[ctr++] = 0x00;  // "    "
        _outmessageData[ctr++] = 0x00;    //StatP
        _outmessageData[ctr++] = 0x00;    // "  "

        _outmessageData[ctr++] = 0x00;  //  SETL = 0
        _outmessageData[3] = ctr-4;
        unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), ctr-1);
        _outmessageData[ctr++] = HIBYTE (CRC);
        _outmessageData[ctr++] = LOBYTE (CRC);

        _outindexOfEnd = ctr;

    }
}


unsigned char CCU711::getRLEN()
{
     unsigned char RLEN14;
     unsigned char rlen = _messageData[6];
     RLEN14 = rlen + 0x0e;
     return RLEN14;
}

void CCU711::decodeLGRPQLong(int &type, int &iotype, int &function, unsigned char &address, int &mctaddress, int &bytesToReturn, int &repeaters, int offset)
{
    switch(_messageData[offset+13] & 0xc0)
    {
        case 0x40:
            type = A_WORD;
            break;
        case 0x80:
            type = B_WORD;
            break;
        case 0xc0:
            type = G_WORD;
            break;
    }

    switch(_messageData[offset+13] & 0x18)
    {
        case 0x00:
            iotype = WRITE;
            break;
        case 0x08:
            iotype = READ;
            break;
        case 0x10:
            iotype = FUNC_WRITE;
            break;
        case 0x18:
            iotype = FUNC_READ;
            break;
    }

    bytesToReturn = _messageData[offset+16];

    address = _messageData[1];
    mctaddress = _messageData[offset+6] << 16 |
                 _messageData[offset+7] <<  8 |
                 _messageData[offset+8];

    repeaters = _messageData[offset+11];

    //function
    function = _messageData[offset+15];
}

unsigned char CCU711::getFrame(int frameCount)
{
    unsigned char frame = 0x10;
    unsigned char rrr = 0x00;
    unsigned char sss = 0x00;
    sss = _messageData[2];
    sss = (sss >> 4) & 0x0e;

    rrr = 0x20;
    frame = frame | rrr;
    frame = frame | sss;

    return frame;
}


void CCU711::getNeededAddresses(int addressArray[])
{
    //Insert the needed addresses
    deque <_queueMessage> ::iterator itr;
    int i = 0;
    for (itr=_messageQueue.begin(); itr!=_messageQueue.end(); itr++) {
        _queueMessage temp = *itr;
        _qmessagesReady += temp.isReady();
        if (temp.isReady()) {
            addressArray[i] = temp.getmctAddress();
        }
    }

}

/***************************************************************************************
*     Functions for the _queueMessage struct
****************************************************************************************/

void CCU711::_queueMessage::initializeMessage()
{
    memset(_data, 0, 300);
    memset(_QENID, 0, 4);
    //CtiTime _timeWhenReady;
    //route infot (3 elements)
    _RTE_CIRCUIT =0x00;
    _RTE_RPTCON  =0x00;
    _RTE_TYPCON  =0x00;
    _address       = 0;
    _wordType      = 0;      //a,b,g words
    _ioType        = 0;       // i/o
    _function      = 0;
    _bytesToReturn = 0;
    _messageLength = 0;
    _mctAddress    = 0;
}

int CCU711::_queueMessage::getmessageLength()           {   return _messageLength;  }
void CCU711::_queueMessage::setmessageLength(int bytes) {   _messageLength = bytes; }
void CCU711::_queueMessage::setOrigLength(int bytes) {   origLength = bytes; }
int CCU711::_queueMessage::getOrigLength() {   return origLength; }

void CCU711::_queueMessage::copyInto(unsigned char Data[], int bytes)
{
    setmessageLength(bytes);
    for(int i = 0; i < 300; i++)
    {
        _data[i]=Data[i];
    }
}

void CCU711::_queueMessage::copyInOriginal(unsigned char buf[], int bytes)
{
    setOrigLength(bytes);
    for(int i = 0; i < bytes; i++)
    {
        origData[i]=buf[i];
    }
}

void CCU711::_queueMessage::copyOut(unsigned char Data[])
{
    for(int i = 0; i<300; i++)
    {
        Data[i]=_data[i];
    }
}

void CCU711::_queueMessage::copyOutOriginal(unsigned char buf[])
{
    int x = getOrigLength();
    for(int i = 0; i < x; i++)
    {
        buf[i]=origData[i];
    }
}

void CCU711::_queueMessage::setQENID(unsigned char one,unsigned char two, unsigned char three, unsigned char four)
{
    _QENID[0] = one;
    _QENID[1] = two;
    _QENID[2] = three;
    _QENID[3] = four;
}

bool CCU711::_queueMessage::isReady()
{
    CtiTime currentTime;
    if(_timeWhenReady < currentTime)
    {
        return true;
    }
    else
        return false;
}

unsigned char CCU711::_queueMessage::getQENID(int index)           {   return _QENID[index];                 }
int CCU711::_queueMessage::getWord()                               {   return _wordType;                     }
void CCU711::_queueMessage::setWord(int type)                      {   _wordType = type;                     }
void CCU711::_queueMessage::setiotype(int iotype)                  {   _ioType = iotype;                     }
void CCU711::_queueMessage::setFunction(int function)              {   _function = function;                 }
int CCU711::_queueMessage::getioType()                             {   return _ioType;                       }
void CCU711::_queueMessage::setAddress(unsigned char address)      {   _address = address;                   }
unsigned char CCU711::_queueMessage::getAddress()                  {   return _address;                      }
void CCU711::_queueMessage::setbytesToReturn(int bytesToReturn)    {   _bytesToReturn = bytesToReturn;       }
int CCU711::_queueMessage::getbytesToReturn()                      {   return _bytesToReturn;                }
CtiTime CCU711::_queueMessage::getTime()                           {   return _timeWhenReady;                }
void CCU711::_queueMessage::setTime(CtiTime currentTime, int delay){   _timeWhenReady = currentTime + delay; }
int CCU711::_queueMessage::getFunction()                           {   return _function;                     }
void CCU711::_queueMessage::setmctAddress(int address)             {   _mctAddress = address;                }
int CCU711::_queueMessage::getmctAddress()                         {   return _mctAddress;                   }

void CCU711::setStrategy(int strategy)
{
    _strategy = strategy;
}

int CCU711::getStrategy()
{
    return _strategy;
}

EmetconWordB* CCU711::getEmetconBWord()
{
    return (EmetconWordB*)magicWord;
}

void CCU711::setEmetconBWord(EmetconWordBase *word)
{
    if(magicWord != NULL){
        delete magicWord;
    }
    magicWord = word;
}

int CCU711::getNumberOfRepeaters()
{
    return _messageData[8] & 0x07;
}

void CCU711::processMsgDTRAN()
{
    Mct410Sim* mctPtr = NULL;

    setEmetconBWord(EmetconWordFactory().getEmetconWord(_messageData+10,7));
    _mctNumber = getEmetconBWord()->getAddress();

    std::map<int,Mct410Sim*>::iterator mctItr = mctMap.find(_mctNumber);
    if(mctItr == mctMap.end())
    {
        mctPtr = new Mct410Sim();
        mctPtr->setMctAddress(_mctNumber);
        //set repeaters here if we can get the info.
        mctMap[mctPtr->getMctAddress()] = mctPtr;
    }
}
