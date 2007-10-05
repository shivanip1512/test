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
#include <queue>

using namespace std;


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
      _qmessagesReady(0)
{
    _address = addressFound;

    memset(_data, 0, 300);
    memset(_messageData, 0, 300);
    memset(_outmessageData, 0, 300);
}

//Listen for and store an incoming message
int CCU711::ReceiveMsg(unsigned char ReadBuffer[], int &setccuNumber)
{
    //determine the type of message
    CreateMessage(INPUT, DEFAULT, ReadBuffer, 0, setccuNumber);
    return _bytesToFollow;
}

//Listen for and store an incoming message
void CCU711::ReceiveMore(unsigned char ReadBuffer[], int counter)
{
    int setccuNumber = 0;
    SET_FOREGROUND_WHITE;
    DecodeCommand(ReadBuffer);

    for(int i=10; i<300; i++)
    {
        _messageData[i]=ReadBuffer[i];   
    }

    int Ctr = 3;
    int dummyNum = 0;
    if(_commandType==DTRAN)
    {
        subCCU710.ReceiveMsg((_messageData + 7), dummyNum);
        _mctNumber = subCCU710.ReceiveMore((_messageData + 7), dummyNum, Ctr);
        float words = subCCU710.getWordsInbound();
        float repeaters = subCCU710.getRepeaters();
        cout<<"Repeaters "<<repeaters<<endl;
        cout<<"Waiting inbound  "<<(((repeaters +1 ) * (words+1) * 52.0)/72.0)*1000.0<<" miliseconds "<<endl;
        CTISleep((((repeaters +1 ) * (words+1) * 52.0)/72.0)*1000.0);  //  Delay at 1200 baud
    }
    else if(_commandType==LGRPQ)
    {
        CreateQueuedMsg();
    }
}


void CCU711::PrintInput()
{
    if(_messageData[0]==0x7e)
        {
        string printMsg, printCmd, printPre, printWrd, printFnc;

        TranslateInfo(INCOMING, printMsg, printCmd, printPre, printWrd, printFnc);

        cout<<"Msg: "<<printMsg<<"    Cmd: "<<printCmd<<"    ";
        if(_commandType==DTRAN)
        {
            subCCU710.PrintInput();
        }
        else if(!_messageQueue.empty())
        {
            cout<<"Wrd: "<<_messageQueue.front().getWord()<<endl;
        }
    }
}

//Build a new message
void CCU711::CreateMsg(int ccuNumber)
{
    unsigned char someData[10];
    int setccuNumber = 0;
	if(_messageType== RESETREQ)
	{
        //  Reset request
        unsigned char Address = _messageData[1];
        CreateMessage(RESETACK, DEFAULT, someData, ccuNumber, setccuNumber, Address);
    }
	else if(_messageType==GENREQ)
	{
        //  General Request
        unsigned char Frame = getFrame();
        unsigned char Address = _messageData[1];
        if(_commandType==DTRAN)
        {
            if(subCCU710.getWordFunction(0)==FUNCREAD) {
                CreateMessage(GENREP, FUNCREAD, someData, ccuNumber, setccuNumber, Address, Frame);
            }
            if(subCCU710.getWordFunction(0)==WRITE) {
                CreateMessage(GENREP, FUNCREAD, someData, ccuNumber, setccuNumber, Address, Frame);
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
            CreateMessage(GENREP, DEFAULT, someData, ccuNumber, setccuNumber, Address, Frame);
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
    cout<<endl;
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


    if(_messageData[0]==0x7e)
        {
        string printMsg, printCmd, printPre, printWrd, printFnc;

        TranslateInfo(OUTGOING, printMsg, printCmd, printPre, printWrd, printFnc);

        cout<<"Queue size: "<<_messageQueue.size();
        cout<<"     Msg: "<<printMsg<<"      "<<printCmd;
        if(_commandType==DTRAN)
        {
            subCCU710.PrintMessage();
        }
        /*else if(!_messageQueue.empty())
            {
            cout<<"QENID : "<<string(CtiNumStr(_messageQueue.front().getQENID(0)).hex().zpad(2))<<' ';
            cout<<string(CtiNumStr(_messageQueue.front().getQENID(1)).hex().zpad(2))<<' ';
            cout<<string(CtiNumStr(_messageQueue.front().getQENID(2)).hex().zpad(2))<<' ';
            cout<<string(CtiNumStr(_messageQueue.front().getQENID(3)).hex().zpad(2))<<endl;
            if(_commandType==RCOLQ)
            {
                if(!_messageQueue.empty() &&)
                {
                    _messageQueue.pop_front();
                }
            }
        }*/
    }
}

void CCU711::TranslateInfo(bool direction, string & printMsg, string & printCmd, string & printPre, string & printWrd, string & printFnc)
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


void CCU711::CreateMessage(int MsgType, int WrdFnc, unsigned char Data[], int ccuNumber, int &setccuNumber, unsigned char Address, unsigned char Frame)
{
    _messageType = MsgType;
	if(_messageType == INPUT) 
    {
        _messageData[0] = Data[0];
        _messageData[1] = Data[1];
        _messageData[2] = Data[2];
        _messageData[3] = Data[3];
        _indexOfEnd = 4;           //   this may cause a PROBLEM  SINCE 710 should be 3 bytes !!!
        _bytesToFollow = DecodeIDLC(setccuNumber);
    }
	else if(_messageType == RESETACK) 
    {
        _outmessageData[0] = 0x7e;
        _outmessageData[1] = _address;   //  slave address
        _outmessageData[2] = 0x73;
        unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), 2);
        _outmessageData[3] = HIBYTE(CRC);   //  insert CRC code
        _outmessageData[4] = LOBYTE(CRC);   //  insert CRC code
        _outindexOfEnd = 5;
    }
	else if(_messageType == GENREP) 
    {
        _outmessageType = GENREP;
        int Ctr = 0;
		if(WrdFnc==ACKACK) {
            _outmessageData[Ctr++] = 0x7e;
            _outmessageData[Ctr++] = _address;   //  slave address
            _outmessageData[Ctr++] = Frame;     //  control
            Ctr++;          // # of bytes to follow minus two filled in at bottom of section
            _outmessageData[Ctr++] = 0x02;      // SRC/DES
            _outmessageData[Ctr++] = 0xa6;      // Echo of command received
            _outmessageData[Ctr++] = 0x00;      // system status items
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      //    "   "
            _outmessageData[Ctr++] = 0x00;      //    "   "  
            _outmessageData[Ctr++] = 0x00;     // device status items
            _outmessageData[Ctr++] = 0x00;     //    "   "
            _outmessageData[Ctr++] = 0x00;     //    "   "   
            _outmessageData[Ctr++] = 0x00;     //    "   "
            _outmessageData[Ctr++] = 0x00;     //    "   "
            _outmessageData[Ctr++] = 0x00;     //    "   "
            _outmessageData[Ctr++] = 0x00;     // process status items
            _outmessageData[Ctr++] = 0x00;     //    "   "		
            _outmessageData[Ctr++] = 0x42;
            _outmessageData[Ctr++] = 0x42;
            _outmessageData[3] = Ctr-4;      // # of bytes to follow minus two (see note above)
										
            unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), Ctr-1);
            _outmessageData[Ctr++] = HIBYTE (CRC);
            _outmessageData[Ctr++] = LOBYTE (CRC);
        }
		else if(_commandType==DTRAN)
        {
                _outmessageData[Ctr++] = 0x7e;
                _outmessageData[Ctr++] = _address;   //  slave address
                _outmessageData[Ctr++] = Frame;     //  control
                Ctr++;  		// # of bytes to follow minus two filled in at bottom of section
                _outmessageData[Ctr++] = 0x02;      // SRC/DES
                _outmessageData[Ctr++] = 0xa6;      // Echo of command received
                _outmessageData[Ctr++] = 0x00;      // system status items
                _outmessageData[Ctr++] = 0x00;      //    "   "
                _outmessageData[Ctr++] = 0x00;      //    "   "
                _outmessageData[Ctr++] = 0x00;      //    "   "  
                _outmessageData[Ctr++] = 0x00;     // device status items
                _outmessageData[Ctr++] = 0x00;     //    "   "
                _outmessageData[Ctr++] = 0x00;     //    "   "   
                _outmessageData[Ctr++] = 0x00;     //    "   "
                _outmessageData[Ctr++] = 0x00;     //    "   "
                _outmessageData[Ctr++] = 0x00;     //    "   "
                _outmessageData[Ctr++] = 0x00;     // process status items
                _outmessageData[Ctr++] = 0x00;     //    "   "									

                if(subCCU710.getWordFunction(0) == FUNCREAD)
                {
                    // Use a 710 to form the content in the message
                    subCCU710.CreateMessage(FEEDEROP, FUNCREAD, _mctNumber, ccuNumber);
                    unsigned char SendData[300];
                    subCCU710.SendMsg(SendData);
                    int smallCtr = 0;
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];

                    float words = subCCU710.getWordsRequested();
                    float repeaters = subCCU710.getRepeaters();
                    cout<<"Repeaters "<<repeaters<<endl;
                    cout<<"Waiting "<<(((repeaters +1 ) * words * 52.0)/72.0)*1000.0<<" miliseconds "<<endl;
                    CTISleep((((repeaters +1 ) * words * 52.0)/72.0)*1000.0);  //  Delay at 1200 baud
                }
                else if(subCCU710.getWordFunction(0) == READ)
                {
                    unsigned char SendData[300];
                    int smallCtr = 0;

                    // Use a 710 to form the content in the message
                    switch(subCCU710.getWordsRequested())
                    {
                        case 1:
                            subCCU710.CreateMessage(FEEDEROP, READREP1, _mctNumber, ccuNumber);
                            break;
                        case 2:
                            subCCU710.CreateMessage(FEEDEROP, READREP2, _mctNumber, ccuNumber);
                            break;
                        case 3:
                            subCCU710.CreateMessage(FEEDEROP, READREP3, _mctNumber, ccuNumber);
                            break;
                    }
                    subCCU710.SendMsg(SendData);
                    smallCtr = 0;
                    for(int i = 0; i < subCCU710.getWordsRequested(); i++)
                    {
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                        _outmessageData[Ctr++] = SendData[smallCtr++];
                    }

                    float words = subCCU710.getWordsRequested();
                    float repeaters = subCCU710.getRepeaters();
                    CTISleep((((repeaters +1 ) * words * 52.0)/72.0)*1000.0);  //  Delay at 1200 baud
                }
                else if(subCCU710.getWordFunction(0) == WRITE)
                {
                                        // Use a 710 to form the content in the message
                    subCCU710.CreateMessage(FEEDEROP, FUNCREAD, _mctNumber, ccuNumber);
                    unsigned char SendData[300];
                    subCCU710.SendMsg(SendData);
                    int smallCtr = 0;
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];
                    _outmessageData[Ctr++] = SendData[smallCtr++];

                    float words = subCCU710.getWordsRequested();
                    float repeaters = subCCU710.getRepeaters();
                    CTISleep((((repeaters +1 ) * words * 52.0)/72.0)*1000.0);  //  Delay at 1200 baud
                }

                _outmessageData[3] = Ctr-4;      // # of bytes to follow minus two (see note above)

                unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), Ctr-1);
                _outmessageData[Ctr++] = HIBYTE (CRC);
                _outmessageData[Ctr++] = LOBYTE (CRC);

                //  Output for debugging only
                /*for(int i=0; i<Ctr; i++) {
                        std::cout<<"_outmessageData "<<string(CtiNumStr(_outmessageData[i]).hex().zpad(2))<<std::endl;
                    }*/
        }
        _outindexOfEnd = Ctr;
    }
    else if(_messageType == PING) {
        int Ctr = 0;
        _outmessageData[Ctr++] = 0xc3;                          
        _outmessageData[Ctr++] = 0xc3;
        _outmessageData[Ctr++] = 0xf5;                          
        _outmessageData[Ctr++] = 0x55;  
        _outindexOfEnd = Ctr;
    }
    else
        cout<<"Error: Could not form a suitable reply"<<endl;
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
    oneWord.InsertWord(WordType, Data, WordFunction, 0);
    oneWord.setWTF(WTF);
    _indexOfEnd += oneWord.getWordSize();
    //_words[_indexOfWords]= oneWord;    //  This line was here to count if there were multiple words: needs to be redone
    _indexOfWords++;

    //  If the first word is a b word, see how many c _words follow it
    //  and insert them into the incoming message
	if(_words[0].getWordType() == 2) {
		for(int i=0; i<InsertMore; i++) {
            EmetconWord anotherWord;
            anotherWord.InsertWord(3, Data, WordFunction, 0);
            _indexOfEnd += anotherWord.getWordSize();
            _words[_indexOfWords]= anotherWord;
            _indexOfWords++;
        }
    }
}


unsigned char CCU711::getFrame(){
    unsigned char Frame = 0x00;
    Frame = (_messageData[2] & 0xe0);
    Frame = (Frame >> 4);
    Frame = (Frame | 0x10);
    return Frame;
}


int CCU711::DecodeIDLC(int & setccuNumber){
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
            //if(!(_messageData[1] & 0x06))
            //{
            //    setccuNumber = 0;
            //}
            if((_messageData[1] & 0x02) ==0x02)
            {
                setccuNumber = 1;
            }
            else if((_messageData[1] & 0x04) == 0x04)
            { 
                setccuNumber = 2;
            }
            else if((_messageData[1] & 0x06) == 0x06)
            {
                setccuNumber = 3;
            }
            else
                setccuNumber = 0;
	}
    else{  //  The message is not an IDLC message.  Probably 710 protocol instead
        return -1;
    }
    return _bytesToFollow;
}


int CCU711::DecodeDefinition(){
    int WordType = 0;
   	if(_messageData[11] == 0xaf)    {   WordType = B_WORD;   }    //  IDLC CCU711 
	else if(_messageData[3] == 0xaf){   WordType = B_WORD;   }    //CCU710
    else
    {
            WordType = 999;
    }
    return WordType;
}


int CCU711::DecodeWTF(int WordType, unsigned char Data[]){
	if((Data[4] & 0x10) == 0x10) {  return 1;   }
	else if((Data[4] & 0x20) == 0x20) {  return 2;   }
	else if((Data[4] & 0x30) == 0x30) {  return 3;   }
    else return 0;
}


int CCU711::DecodeFunction(int WordType, unsigned char Data[]){
    char FunctionType = 0;
	if(WordType== B_WORD) 
    {
        //   check to see what function is specified
		if((_messageData[15] & 0xc) == 0x0c)       {  FunctionType = FUNCREAD;     }     //  Function with acknowledge
		else if((_messageData[15] & 0x04) == 0x04) {  FunctionType = READ;        }     //  Read
		else if((_messageData[15] & 0x00) == 0x00) {  FunctionType = WRITE;       }     //  Write
	}
    return FunctionType;
}


void CCU711::CreateQueuedMsg()
{
    int Offset = _messageData[6];
    int Length = _messageData[3];
    int counter = Length / Offset;

    for(int i = 0; i<(counter); i++)
    {
        //cout<<"Offset : "<<Offset<<" !"<<endl;
        _queueMessage newMessage;
        newMessage.initializeMessage();
    
        unsigned char one, two, three, four;
        one   = _messageData[7+(i*Offset)];
        two   = _messageData[8+(i*Offset)];
        three = _messageData[9+(i*Offset)];
        four  = _messageData[10+(i*Offset)];
        //cout<<"setting qenid to :";
        //cout <<string(CtiNumStr(_messageData[7+(i*Offset)]).hex().zpad(2))<<"   ";
        //cout <<string(CtiNumStr(_messageData[8+(i*Offset)]).hex().zpad(2))<<"   ";
        //cout <<string(CtiNumStr(_messageData[9+(i*Offset)]).hex().zpad(2))<<"   ";
        //cout <<string(CtiNumStr(_messageData[10+(i*Offset)]).hex().zpad(2))<<"!"<<endl;
        newMessage.setQENID(one, two, three, four);
    
        int type = 0;
        int iotype = 0;
        int function = 0;
        unsigned char address;
        long int mctaddress;
        int bytesToReturn = 0;
        decodeForQueueMessage(type, iotype, function, address, mctaddress, bytesToReturn, Offset);
        newMessage.setWord(type);
        newMessage.setiotype(iotype);
        newMessage.setFunction(function);
        newMessage.setAddress(address);
        newMessage.setbytesToReturn(bytesToReturn);
        newMessage.setmctAddress(mctaddress);
        if(_messageQueue.empty())
        {
            CtiTime currentTime;
            newMessage.setTime(currentTime, 6*(bytesToReturn/3));
        }
        else
        {
                newMessage.setTime(_messageQueue.back().getTime(), 6*(bytesToReturn/3));   //Should be words !!!!!
        }
            
        //RTE_CIRCUIT;
        //RTE_RPTCON;
        //RTE_TYPCON;
    
        _messageQueue.push_back(newMessage);
        CreateQueuedResponse();
        Offset = _messageData[6 + Offset];
    }
}



void CCU711::CreateQueuedResponse()
{
    if(!_messageQueue.empty())
    {
        unsigned char Data[300];
        int ctr=0;
        if(_messageQueue.back().getWord()==B_WORD)
            {
            if(_messageQueue.back().getioType()==FUNC_READ)
            {
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
                Data[ctr++] = 0x03;//_messageQueue.back().getbytesToReturn(); // L1
                Data[ctr++] = 0x00; // TS
                Data[ctr++] = 0x00; // "  "
                long int mctAddress = _messageQueue.back().getmctAddress();

                getData(mctAddress, _messageQueue.back().getFunction(), _messageQueue.back().getioType(), _messageQueue.back().getbytesToReturn());
                for(int i = 0; i<_messageQueue.back().getbytesToReturn(); i++)
                {
                    Data[ctr++] = _data[i]; //Data
                }
            }
            else if(_messageQueue.back().getioType()==READ)
            {
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
                long int mctAddress = _messageQueue.back().getmctAddress();
                getData(mctAddress, _messageQueue.back().getFunction(), _messageQueue.back().getioType(), _messageQueue.back().getbytesToReturn());
                for(int i = 0; i<_messageQueue.back().getbytesToReturn(); i++)
                {
                    Data[ctr++] = _data[i]; //Data
                }
            }
            else if(_messageQueue.back().getioType()==XTIME)
            {
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
                long int mctAddress = _messageQueue.back().getmctAddress();
                getData(mctAddress, _messageQueue.back().getFunction(), _messageQueue.back().getioType(), _messageQueue.back().getbytesToReturn());
                for(int i = 0; i<_messageQueue.back().getbytesToReturn(); i++)
                {
                    Data[ctr++] = _data[i]; //Data
                }
            }

        }
        _messageQueue.back().setmessageLength(ctr);
        _messageQueue.back().copyInto(Data, ctr);
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
    _outmessageData[Ctr++] = _address;   //  slave address
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
        _outmessageData[Ctr++] = 0x42;
        _outmessageData[Ctr++] = 0x42;
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
        if(_qmessagesReady==0)
        {
            _outmessageData[ctr++] = 0x7e;
            _outmessageData[ctr++] = _address; //  Stored in 711 on startup  
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
            _outmessageData[ctr++] = _address; //  Stored in 711 on startup  
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
            while(!_messageQueue.empty() && _messageQueue.front().isReady())
            {
                unsigned char Data[300];
                _messageQueue.front().copyOut(Data);
                for(int i=0; i<_messageQueue.front().getmessageLength(); i++)
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
        SET_FOREGROUND_BRIGHT_RED;
        cout<<'\n'<<"Error: Message queue is empty"<<endl;
        SET_FOREGROUND_WHITE;

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
        _outmessageData[ctr++] = _address; //  Stored in 711 on startup  
        _outmessageData[ctr++] = getFrame(1);
        ctr++;                 //length set below
        _outmessageData[ctr++] = 0x00;
        _outmessageData[ctr++] = 0xa7;
        _outmessageData[ctr++] = 0x01; //Stats
        _outmessageData[ctr++] = 0x00; // "  "
        _outmessageData[ctr++] = 0x18; // "  "
        _outmessageData[ctr++] = 0x50; // "  "
        _outmessageData[ctr++] = 0x00; //StatD
        _outmessageData[ctr++] = 0x00; // "  "
        _outmessageData[ctr++] = 0x20-_messageQueue.size(); // "  "
        _outmessageData[ctr++] = 0x01;     // NCSETS
        _outmessageData[ctr++] = 0x01;                       // NCOCTS
        _outmessageData[ctr++] = 0x01;  // "    "
        _outmessageData[ctr++] = 0x01;    //StatP
        _outmessageData[ctr++] = 0x01;    // "  "
        
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

void CCU711::decodeForQueueMessage(int & type, int & iotype, int & function, unsigned char & address, long int & mctaddress, int  & bytesToReturn, int offset)
{
    switch(_messageData[19] & 0xc0)
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

    switch(_messageData[19] & 0x18)
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

    //switch(_messageData[21] & 0x18)
    //{   INSERT CODE HERE TO DETERMINE FUNCTION
    //}

    bytesToReturn = _messageData[offset+5];

    address = _messageData[1];
    mctaddress = _messageData[12] << 16 |
                 _messageData[13] <<  8 |
                 _messageData[14];
}


unsigned char CCU711::getFrame(int frameCount)
{
    unsigned char frame = 0x10;
    unsigned char rrr = 0x00;
    unsigned char sss = 0x00;
    sss = _messageData[2];
    sss = (sss >> 4) & 0x0e;
    //cout <<"sss = "<<string(CtiNumStr(sss).hex().zpad(2))<<endl;
    rrr = 0x20;
    frame = frame | rrr;
    frame = frame | sss;

    return frame;
}


void CCU711::getData(long int mctAddress, int function, int ioType, int bytesToReturn)
{
    srand ( time(NULL) );
    int random = rand() % 10 + 1;
    _data[1]= (mctAddress >> 6) + random;
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


void CCU711::_queueMessage::copyInto(unsigned char Data[], int bytes)
{
    setmessageLength(bytes);
    for(int i = 0; i<300; i++)
    {
        _data[i]=Data[i];
    }
}

void CCU711::_queueMessage::copyOut(unsigned char Data[])
{
    for(int i = 0; i<300; i++)
    {
        Data[i]=_data[i];
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
void CCU711::_queueMessage::setmctAddress(long int address)        {   _mctAddress = address;                }
long int CCU711::_queueMessage::getmctAddress()                    {   return _mctAddress;                   }

