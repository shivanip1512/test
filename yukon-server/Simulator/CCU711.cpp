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
#include "CCU711.h"
#include "ctinexus.h"
#include "numstr.h"
#include "cticalls.h"
#include "cti_asmc.h"
#include "color.h"
#include <queue>

using namespace std;


/**************************************************
/*  CCU711 functions   
***************************************************/

CCU711::CCU711() :
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
      _mctNumber(0)       
{

    memset(_messageData, 0, 100);
    memset(_outmessageData, 0, 100);
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

    for(int i=11; i<50; i++)
    {
        _messageData[i]=ReadBuffer[i];   
    }

    int Ctr = 3;
    int dummyNum = 0;
    if(_commandType==DTRAN)
    {
        subCCU710.ReceiveMsg((_messageData + 7), dummyNum);
        _mctNumber = subCCU710.ReceiveMore((_messageData + 7), dummyNum, Ctr);
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
            if(subCCU710.getWordFunction(0)==FUNCACK) {
                CreateMessage(GENREP, FUNCACK, someData, ccuNumber, setccuNumber, Address, Frame);
            }
        }
        else if(_commandType==LGRPQ) {
            CreateQueuedResponse();
            CreateResponse(LGRPQ);
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

    memcpy(SendData, WriteBuffer, 100);

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

        cout<<"Msg: "<<printMsg<<"      "<<printCmd;
        if(_commandType==DTRAN)
            {
            subCCU710.PrintMessage();
        }
        else if(!_messageQueue.empty())
            {
            cout<<"QENID : "<<string(CtiNumStr(_messageQueue.front().getQENID(0)).hex().zpad(2))<<' ';
            cout<<string(CtiNumStr(_messageQueue.front().getQENID(1)).hex().zpad(2))<<' ';
            cout<<string(CtiNumStr(_messageQueue.front().getQENID(2)).hex().zpad(2))<<' ';
            cout<<string(CtiNumStr(_messageQueue.front().getQENID(3)).hex().zpad(2))<<endl;
            if(_commandType==RCOLQ)
            {
                _messageQueue.pop();
            }
        }
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
            case FUNCACK:   printFnc.append("FUNCACK");     break;
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
            case FUNCACK:   printFnc.append("FUNCACK");     break;
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
		_outmessageData[1] = Address;   //  slave address
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
			_outmessageData[Ctr++] = Address;   //  slave address
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
                _outmessageData[Ctr++] = Address;   //  slave address
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

                if(subCCU710.getWordFunction(0) == FUNCACK)
                {
                    // Use a 710 to form the content in the message
                    subCCU710.CreateMessage(FEEDEROP, FUNCACK, _mctNumber, ccuNumber);
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
                }
                else if(subCCU710.getWordFunction(0) == WRITE)
                {
                    cout<<"Write detected"<<endl;/////////////////////////YOU WERE HERE//////////////////////
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
	Frame =	(_messageData[2] & 0xe0);
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
            if(!(_messageData[1] & 0x06))
            {
                setccuNumber = 0;
            }
            if((_messageData[1] & 0x02) ==0x02)
            {
                setccuNumber = 1;
            }
            if((_messageData[1] & 0x04) == 0x04)
            {
                setccuNumber = 2;
            }
            if((_messageData[1] & 0x06) == 0x06)
            {
                setccuNumber = 3;
            } 
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
		if((_messageData[15] & 0xc) == 0x0c)       {  FunctionType = FUNCACK;     }     //  Function with acknowledge
		else if((_messageData[15] & 0x04) == 0x04) {  FunctionType = READ;        }     //  Read
		else if((_messageData[15] & 0x00) == 0x00) {  FunctionType = WRITE;       }     //  Write
	}
    return FunctionType;
}


void CCU711::CreateQueuedMsg()
{
    _queueMessage newMessage;
    newMessage.initializeMessage();

    unsigned char one, two, three, four;
    one   = _messageData[7];
    two   = _messageData[8];
    three = _messageData[9];
    four  = _messageData[10];
    newMessage.setQENID(one, two, three, four);

    int type = 0;
    decodeForQueueMessage(type);
    newMessage.setWord(type);

    int _bytesToReturn;
    //CtiTime _timeWhenReady;
    unsigned char _address;
    unsigned char RTE_CIRCUIT;
    unsigned char RTE_RPTCON;
    unsigned char RTE_TYPCON;
    int _wordType;      //a,b,g words
    int _ioType;       // i/o
    int _function;
    unsigned char _QENID[4];

    _messageQueue.push(newMessage);
}



void CCU711::CreateQueuedResponse()
{
    unsigned char Data[50];
    _messageQueue.front().copyInto(Data, 3);
}

void CCU711::CreateResponse(int command)
{
    int Ctr = 0;
    unsigned char Frame = getFrame();
    unsigned char Address = _messageData[1];
    _outmessageData[Ctr++] = 0x7e;
    _outmessageData[Ctr++] = Address;   //  slave address
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
    _outmessageData[Ctr++] = 0x42;
    _outmessageData[Ctr++] = 0x42;
    _outmessageData[3] = Ctr-4;      // # of bytes to follow minus two (see note above)

    unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), Ctr-1);
    _outmessageData[Ctr++] = HIBYTE (CRC);
    _outmessageData[Ctr++] = LOBYTE (CRC);
    _outindexOfEnd = Ctr;
}


void CCU711::LoadQueuedMsg()
{
    unsigned char Data[50];
    _messageQueue.front().copyOut(Data);
    for(int i=0; i<3; i++)
    {
        _outmessageData[i]=Data[i];
    }
    _outmessageData[3] = getRLEN(); 		// # of bytes to follow 
    _outmessageData[4] = 0x00;
    _outmessageData[5] = 0xa7;
    _outmessageData[18] = 0x14;
    _outmessageData[19] = 0x98;
    _outmessageData[20] = 0x00;
    _outmessageData[21] = 0x90;
    _outmessageData[22] = 0x03;
    _outmessageData[23] = 0x00;
    _outmessageData[24] = 0x00;
    _outmessageData[25] = 0x00;
    _outmessageData[26] = 0x00;
    _outmessageData[27] = 0x00;
    _outmessageData[28] = 0x01;
    unsigned short CRC = NCrcCalc_C ((_outmessageData + 1), 31);
    _outmessageData[32] = HIBYTE (CRC);
    _outmessageData[33] = LOBYTE (CRC);

    _outindexOfEnd = 34;    ////////  CHANGE THIS FROM HARDCODED to getBytesToReturn !!!  ///////////
}


unsigned char CCU711::getRLEN()
{
     unsigned char RLEN14;
     unsigned char rlen = _messageData[6];
     RLEN14 = rlen + 0x0e;
     return RLEN14;
}

void CCU711::decodeForQueueMessage(int & type)
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
}


/***************************************************************************************
*     Functions for the _queueMessage struct
****************************************************************************************/

void CCU711::_queueMessage::initializeMessage()
{
    memset(_data, 0, 50);
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
}

int CCU711::_queueMessage::getbytesToReturn()           {   return _bytesToReturn;  }
void CCU711::_queueMessage::setbytesToReturn(int bytes) {   _bytesToReturn = bytes; }


void CCU711::_queueMessage::copyInto(unsigned char Data[], int bytes)
{
    setbytesToReturn(bytes);
    for(int i = 0; i<50; i++)
    {
        _data[i]=Data[i];
    }
}

void CCU711::_queueMessage::copyOut(unsigned char Data[])
{
    for(int i = 0; i<50; i++)
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

unsigned char CCU711::_queueMessage::getQENID(int index)    {   return _QENID[index];   }
int CCU711::_queueMessage::getWord()                        {   return _wordType;       }
void CCU711::_queueMessage::setWord(int type)               {   _wordType = type;       }

                 
