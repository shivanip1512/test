/*****************************************************************************
*
*    FILE NAME: CCU710.cpp
*
*    DATE: 7/1/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: Simulate the behavior of CCU 710s
*
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#include "yukon.h"
#include "CCU710.h"
#include "ctinexus.h"
#include "numstr.h"
#include "cticalls.h"
#include "color.h"
#include "logger.h"
#include "SharedFunctions.h"

#include <boost/thread/mutex.hpp>

using namespace std;

DLLIMPORT extern CtiLogger   dout;

/**************************************************
/*  CCU710 functions
***************************************************/

CCU710::CCU710() : 
      _indexOfEnd(0),
      _indexOfWords(0),
      _outindexOfEnd(0),
      _outindexOfWords(0),
      _wordsRequested(0),
      _messageType(0),
      _commandType(0),
      _preamble(0),
      _outmessageType(0),
      _outcommandType(0),
      _outpreamble(0),
      _bytesToFollow(0),
      _strategy(0)
{
      memset(_messageData, 0, 100);
      memset(_outmessageData, 0, 100);
}

void CCU710::handleRequest(CTINEXUS* socket) 
{
    CtiTime AboutToRead;
    unsigned char ReadBuffer[300];
    int BytesToFollow;
    int counter = 0;
    int ccuNumber = 0;
    int mctNumber = 0;
    unsigned long bytesRead = 0;
    BytesToFollow = 3;

    //  Read first few bytes
    while( bytesRead < BytesToFollow )
    {
        socket->CTINexusRead(ReadBuffer + counter  ,1, &bytesRead, 15);
        counter++;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        //SET_FOREGROUND_BRIGHT_YELLOW;
        dout << AboutToRead.asString();
        //SET_FOREGROUND_BRIGHT_CYAN;
        dout << " IN:" << endl;
        //SET_FOREGROUND_BRIGHT_GREEN;
        for( int byteitr = 0; byteitr < (bytesRead); byteitr++ )
        {
            dout << string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2)) << ' ';
        }
    }


    BytesToFollow = ReceiveMsg(ReadBuffer, ccuNumber);

    if( BytesToFollow>0 )
    {
        bytesRead=0;
        //  Read any additional bytes
        while( bytesRead < BytesToFollow )
        {
            socket->CTINexusRead(ReadBuffer + counter, 1, &bytesRead, 15);
            counter++;
        }
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            for( int byteitr = 0; byteitr < BytesToFollow; byteitr++ )
            {
                dout << string(CtiNumStr(ReadBuffer[byteitr+3]).hex().zpad(2)) << ' ';
            }
        
            ReceiveMore(ReadBuffer, mctNumber,counter);
            PrintInput();
        }
    }


    if( BytesToFollow>0 )
    {
        CreateMsg(ccuNumber, mctNumber);

        unsigned char SendData[300];

        int MsgSize = SendMsg(SendData);

        if( MsgSize>0 )
        {
            int napTime = ((MsgSize*8)/1200)*2*1000;  //  Delay at 1200 baud in both directions
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
                    dout << string(CtiNumStr(SendData[byteitr]).hex().zpad(2)) << ' ';
                }
                dout << endl;
                PrintMessage();
            }
        }
    }
    else
    {
        unsigned char SendData[300];

        int MsgSize = SendMsg(SendData);

        if( MsgSize>0 )
        {
            int napTime = ((MsgSize*8)/1200)*2*1000;  //  Delay at 1200 baud in both directions
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
    
                dout << endl;
                PrintMessage();
            }
        }
    }

}


//Listen for and store an incoming message
int CCU710::ReceiveMsg(unsigned char Data[], int &setccuNumber)
{
    //Message inMsg;
    //determine the type of message
    //CreateMessage
    int MsgType = INPUT;
     int WrdFnc = DEFAULT;
     int mctNumber = 0;
     int Address = 0;

    _messageType = MsgType;
    if(_messageType == INPUT) {
        _messageData[0] = Data[0];
        _messageData[1] = Data[1];
        _messageData[2] = Data[2];
        _messageData[3] = Data[3];

        _indexOfEnd = 4;           //   this may cause a PROBLEM  SINCE 710 should be 3 bytes !!!
        _bytesToFollow = DecodePreamble(setccuNumber);
    }
    else if(_messageType == FEEDEROP) {
        int Ctr = 0;
        if(WrdFnc==FUNCREAD) {
            //_messageData[Ctr++] = Address;   //  slave address
            //Ctr++;        // btf -2 filled in @ bottom

            // ///////////////////////////////////////////////////////////////////////////
            // /////////////////////////////////////////// ////////////////////////////
            // ///  MAKE THE ADDRESS IN PREAMBLE GENERAL FOR ALL CUU ADDRESSES !!!
            _messageData[Ctr++] = 0xc3;
            _messageData[Ctr++] = 0xc3;
            _messageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _messageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _messageData[Ctr++] = 0xc3;


            //  Output for debugging only
            /*for(int i=0; i<Ctr; i++) {
                std::cout<<"_messageData "<<string(CtiNumStr(_messageData[i]).hex().zpad(2))<<std::endl;
            }*/
        }
        else if(WrdFnc==FUNCWRITE) {
            //_messageData[Ctr++] = Address;   //  slave address
            //Ctr++;        // btf -2 filled in @ bottom

            // ///////////////////////////////////////////////////////////////////////////
            // /////////////////////////////////////////// ////////////////////////////
            // ///  MAKE THE ADDRESS IN PREAMBLE GENERAL FOR ALL CUU ADDfRESSES !!!
            _messageData[Ctr++] = 0xc3;
            _messageData[Ctr++] = 0xc3;
            _messageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _messageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _messageData[Ctr++] = 0xc3;


            //  Output for debugging only
            /*for(int i=0; i<Ctr; i++) {
                std::cout<<"_messageData "<<string(CtiNumStr(_messageData[i]).hex().zpad(2))<<std::endl;
            }*/
        }

        _indexOfEnd = Ctr;
    }
    else if(_bytesToFollow == 0);  //_messageType == PING
    {
            int Ctr = 0;
            _outmessageData[Ctr++] = 0xc3;
            _outmessageData[Ctr++] = 0xc3;
            _outmessageData[Ctr++] = 0xf5;
            _outmessageData[Ctr++] = 0x55;

            _outindexOfEnd = Ctr;  //  Set the out index since no more bytes will be added to outbound message
    }
    return _bytesToFollow;
}


//Listen for and store an incoming message
int CCU710::ReceiveMore(unsigned char Data[], int &setmctNumber, int counter)
{
    _messageData[counter]=Data[counter++];
    _messageData[counter]=Data[counter++];
    _messageData[counter]=Data[counter++];
    _messageData[counter]=Data[counter++];
    _messageData[counter]=Data[counter++];
    _messageData[counter]=Data[counter++];

    int WordFunction;
    int InsertMore = 0;
    int WTF = 0;
    int WordType = DecodeDefinition();
    WordFunction = DecodeFunction(WordType);
    setmctNumber = DecodeMctAddress();
    WTF = DecodeWTF(WordType, Data);
    _wordsRequested = WTF;
    //  If there is a 'c' word following the 'b' word
    //  then WTF indicates 'c' _words to follow so they should be stored
    if(WTF>0) {
        if((Data[7] & 0xc0) == 0xc0) {
            InsertMore = WTF;
        }
    }

    EmetconWord oneWord;
    oneWord.setStrategy(_strategy);
    oneWord.InsertWord(WordType, Data, WordFunction, 0, 0, (getRepeaters()));
    oneWord.setWTF(WTF);
    _indexOfEnd += oneWord.getWordSize();
    _words[0]= oneWord;
    _indexOfWords++;
    //  If the first word is a b word, see how many c _words follow it
    //  and insert them into the incoming message
    if(_words[0].getWordType() == 2) {
        for(int i=0; i<InsertMore; i++) {
            EmetconWord anotherWord;
            anotherWord.setStrategy(_strategy);
            anotherWord.InsertWord(3, Data, WordFunction, 0, 0, (getRepeaters()));
            _indexOfEnd += anotherWord.getWordSize();
            _words[_indexOfWords]= anotherWord;
            _indexOfWords++;
        }
    }
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl;
    }
    return setmctNumber;
}


void CCU710::PrintInput()
{
    SET_FOREGROUND_WHITE;
    string printMsg, printCmd, printPre, printWrd, printFnc;

    TranslateInfo(INCOMING, printMsg, printCmd, printPre, printWrd, printFnc);

    dout<<"Pre: "<<printPre;
    dout<<"    Wrd: "<<printWrd<<"    Fnc: "<<printFnc<<endl;
}


//Build a new message
void CCU710::CreateMsg(int ccuNumber, int mctNumber){
    unsigned char someData[10];
    if(_preamble==FEEDEROP)
    {
        //  Feeder operation
        unsigned char Frame = getFrame();
        unsigned char Address = _messageData[1];
        if(_words[0].getWordFunction()==FUNCREAD) {
            CreateMessage(FEEDEROP, FUNCREAD, mctNumber, ccuNumber);
        }
        else if(_words[0].getWordFunction()==FUNCWRITE) {
            CreateMessage(FEEDEROP, FUNCWRITE, mctNumber, ccuNumber);
        }
        else{
            CreateMessage(FEEDEROP, DEFAULT, mctNumber, ccuNumber);
        }
    }
    else
    {   //  Ping response
       CreateMessage(PING, DEFAULT, mctNumber, ccuNumber);
    }
}


//Send the message back to porter
int CCU710::SendMsg(unsigned char SendData[]){
    int MsgSize = _outindexOfEnd;

    if(getStrategy()==4)
    {
        for(int i = 0; i<100; i++)
        {
            if(_outmessageData[i]!=0)
            {
                _outmessageData[i]=i;
            }
        }
    }
    memcpy(SendData, _outmessageData, 100);


    return MsgSize;
}


void CCU710::PrintMessage(){

    SET_FOREGROUND_WHITE;
    string printType;
    switch(_outmessageType){
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

    dout<<"Pre: "<<printPre;
    dout<<"    Wrd: "<<printWrd<<"    Fnc: "<<printFnc<<endl;
    dout<<"________________________________________________________________________________"<<endl;
}


void CCU710::TranslateInfo(bool direction, string & printMsg, string & printCmd, string & printPre, string & printWrd, string & printFnc)
{
    if(direction == INCOMING){
        switch(_messageType){
            case INPUT:     printMsg.append("INPUT");       break;
            case RESETREQ:  printMsg.append("RESETREQ");    break;
            case RESETACK:  printMsg.append("RESETACK");    break;
            case GENREQ:    printMsg.append("GENREQ");      break;
            case GENREP:    printMsg.append("GENREP");      break;
        }
        switch(_commandType){
            case 11:        printCmd.append("DTRAN");       break;
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
            case FUNCWRITE:   printFnc.append("FUNCWRITE");     break;
            case READ:      printFnc.append("READ");        break;
            case WRITE:     printFnc.append("WRITE");       break;
        }
    }
    else if(direction == OUTGOING){   // seperate these variables (eg _outmessageType) for output of outoing message info !!!
        switch(_outmessageType){
            case INPUT:     printMsg.append("INPUT");       break;
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
            case FUNCWRITE:   printFnc.append("FUNCWRITE");     break;
            case READ:      printFnc.append("READ");        break;
            case WRITE:     printFnc.append("WRITE");       break;
        }
    }
}

int CCU710::DecodePreamble(int &setccuNumber)
{
    char _bytesToFollow = 0;
    _preamble = 0;
    // set the ccu number by checking the preamble for the address
    setccuNumber = DecodeCCUAddress();

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

int CCU710::DecodeDefinition(){
    int WordType = 0;
    if((_messageData[3] & 0xa0)== 0xa0){   WordType = B_WORD;   }    //CCU710
    else
    {
            WordType = 999;
    }
    return WordType;
}


int CCU710::DecodeFunction(int WordType){
    char FunctionType = 0;
    if(WordType== B_WORD)
    {
        //   check to see what function is specified
        if((_messageData[8] & 0x0c) == 0x0c)      {  FunctionType = FUNCREAD;    }     //  Function read
        if((_messageData[8] & 0x04) == 0x04)      {  FunctionType = READ;        }     //  Read
        if((_messageData[8] & 0x00) == 0x00)      {  FunctionType = WRITE;       }     //  Write
        else if((_messageData[8] & 0x08) == 0x08) {  FunctionType = FUNCWRITE;   }     //  FuncWrite
    }
        return FunctionType;
}


int CCU710::DecodeWTF(int WordType, unsigned char Data[]){
    int WTF = 0;
    if((Data[7] & 0x10) == 0x10) {  WTF = 1;   }
    if((Data[7] & 0x20) == 0x20) {  WTF = 2;   }
    if((Data[7] & 0x30) == 0x30) {  WTF = 3;   }
    return WTF;
}


unsigned char CCU710::getFrame(){
    unsigned char Frame = 0x00;
    Frame = (_messageData[2] & 0xe0);
    Frame = (Frame >> 4);
    Frame = (Frame | 0x10);
    return Frame;
}


// Constructor to build a new Message
void CCU710::CreateMessage(int MsgType, int WrdFnc, int mctNumber, int ccuAddress){
    _messageType = MsgType;

    if(_messageType == FEEDEROP) {
        int Ctr = 0;
        if(WrdFnc==FUNCREAD) {
            //_messageData[Ctr++] = Address;   //  slave address
            //Ctr++;        // btf -2 filled in @ bottom

            unsigned char ack = makeAck(ccuAddress>>1);
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            if((getStrategy()==DEFAULT)||(_strategy==2)) {
                Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            }
            else if(getStrategy()==BAD_D_WORD) {
                Ctr = newWord.InsertWord(X_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            }
            else
            {
                Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            }

            _words[0]=newWord;
            _outmessageData[Ctr++] = ack;


            //  Output for debugging only
            /*for(int i=0; i<Ctr; i++) {
                std::cout<<"_messageData "<<string(CtiNumStr(_messageData[i]).hex().zpad(2))<<std::endl;
            }*/
        }
        else if(WrdFnc==FUNCWRITE) {
            //_messageData[Ctr++] = Address;   //  slave address
            //Ctr++;        // btf -2 filled in @ bottom

            unsigned char ack = makeAck(ccuAddress);
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _outmessageData[Ctr++] = ack;


            //  Output for debugging only
            /*for(int i=0; i<Ctr; i++) {
                std::cout<<"_messageData "<<string(CtiNumStr(_messageData[i]).hex().zpad(2))<<std::endl;
            }*/
        }
        else if(WrdFnc==READREP1) {
            unsigned char ack = makeAck(ccuAddress);
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _outmessageData[Ctr++] = ack;
        }
        else if(WrdFnc==READREP1) {
            unsigned char ack = makeAck(ccuAddress);
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _outmessageData[Ctr++] = ack;
        }
        else if(WrdFnc==READREP1) {
            unsigned char ack = makeAck(ccuAddress);
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _outmessageData[Ctr++] = ack;
        }
        else if(WrdFnc==READREP2) {
            unsigned char ack = makeAck(ccuAddress);
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _outmessageData[Ctr++] = ack;

            EmetconWord newWord2;
            newWord2.setStrategy(_strategy);
            Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[1]=newWord;
            _outmessageData[Ctr++] = ack;
        }
        else if(WrdFnc==READREP3) {
            unsigned char ack = makeAck(ccuAddress);
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = ack;
            _outmessageData[Ctr++] = 0x82;

            EmetconWord newWord;
            newWord.setStrategy(_strategy);
            int Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[0]=newWord;
            _outmessageData[Ctr++] = ack;

            EmetconWord newWord2;
            newWord2.setStrategy(_strategy);
            Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[1]=newWord;
            _outmessageData[Ctr++] = ack;

            EmetconWord newWord3;
            newWord3.setStrategy(_strategy);
            Function = 0;
            Ctr = newWord.InsertWord(D_WORD,  _outmessageData, Function, mctNumber, Ctr, (getRepeaters()));
            _words[2]=newWord;
            _outmessageData[Ctr++] = ack;
        }
        _outindexOfEnd = Ctr;
    }
    else if(_messageType == PING) {
        int Ctr = 0;
        unsigned char ack = makeAck(ccuAddress);
        _outmessageData[Ctr++] = ack;
        _outmessageData[Ctr++] = ack;
        _outmessageData[Ctr++] = 0xf5;
        _outmessageData[Ctr++] = 0x55;
        _outindexOfEnd = Ctr;
    }
}

int CCU710::DecodeMctAddress()
{
    int setmctAddress = 0;
    setmctAddress = ((_messageData[4] & 0x0f) << 20 |
                     _messageData[5]  << 12 |
                     _messageData[6]  << 4  |
                     _messageData[7] >> 4) >> 2;
    return setmctAddress;
}

int CCU710::DecodeCCUAddress()
{
    int setCCUAddress = 0;
    if(!(_messageData[0] & 0x03))
    {
        setCCUAddress = 0;
    }
    else if((_messageData[0] & 0x01) == 0x01)
    {
        setCCUAddress = 1;
    }
    if((_messageData[0] & 0x02) == 0x02)
    {
        setCCUAddress = 2;
    }
    if((_messageData[0] & 0x03) == 0x03)
    {
        setCCUAddress = 3;
    }

    return setCCUAddress;
}

int CCU710::getWordFunction(int wordNum)    {   return _words[wordNum].getWordFunction();   }
int CCU710::getWordsRequested()             {   return _wordsRequested;                     }
int CCU710::getRepeaters()                  {   return (_messageData[1] & 0x07);            }
int CCU710::getWordsInbound()               {   return (_indexOfEnd)/6;                     }

void CCU710::setStrategy(int strategy)
{
    _strategy = strategy;
}

int CCU710::getStrategy()
{
    return _strategy;
}
