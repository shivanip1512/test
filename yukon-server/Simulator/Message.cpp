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
#include "cticalls.h"
#include "cti_asmc.h"
#include "numstr.h"
#include <iostream>



using namespace std;

/**************************************************
/*  Message functions   
***************************************************/
Message::Message() : 
	_indexOfEnd(0),
	_indexOfWords(0)
{
	_messageType  = 0;
	_commandType  = 0;
	_preamble     = 0;

    memset(_messageData, 0, 100);

    _bytesToFollow = 0;
}
	
// Constructor to build a new Message 
void Message::CreateMessage(int MsgType, int WrdFnc, unsigned char Data[], unsigned char Address, unsigned char Frame){
	_messageType = MsgType;
	if(_messageType == INPUT) {
		_messageData[0] = Data[0];
		_messageData[1] = Data[1];
		_messageData[2] = Data[2];
		_messageData[3] = Data[3];
		_indexOfEnd = 4;
		_bytesToFollow = DecodeIDLC();
	}
	else if(_messageType == RESETACK) {
		_messageData[0] = 0x7e;
		_messageData[1] = Address;   //  slave address
		_messageData[2] = 0x73;
		unsigned short CRC = NCrcCalc_C ((_messageData + 1), 2);
		_messageData[3] = HIBYTE(CRC);   //  insert CRC code
		_messageData[4] = LOBYTE(CRC);   //  insert CRC code
		_indexOfEnd = 5;
	}
	else if(_messageType == GENREP) {
		int Ctr = 0;
		if(WrdFnc==ACKACK) {
			_messageData[Ctr++] = 0x7e;
			_messageData[Ctr++] = Address;   //  slave address
			_messageData[Ctr++] = Frame;     //  control
			Ctr++;  		// # of bytes to follow minus two filled in at bottom of section
			_messageData[Ctr++] = 0x02;      // SRC/DES
			_messageData[Ctr++] = 0x26;      // Echo of command received
			_messageData[Ctr++] = 0x00;      // system status items
			_messageData[Ctr++] = 0x00;      //    "   "
			_messageData[Ctr++] = 0x00;      //    "   "
			_messageData[Ctr++] = 0x00;      //    "   "  
			_messageData[Ctr++] = 0x00;     // device status items
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     //    "   "   
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     // process status items
			_messageData[Ctr++] = 0x00;     //    "   "		
			_messageData[Ctr++] = 0x42;
			_messageData[Ctr++] = 0x42;
			_messageData[3] = Ctr-4;      // # of bytes to follow minus two (see note above)
										
			unsigned short CRC = NCrcCalc_C ((_messageData + 1), Ctr-1);
			_messageData[Ctr++] = HIBYTE (CRC);
			_messageData[Ctr++] = LOBYTE (CRC);
		}
		else if(WrdFnc==DEFAULT){ 
			_messageData[Ctr++] = 0x7e;
			_messageData[Ctr++] = Address;   //  slave address
			_messageData[Ctr++] = Frame;     //  control
			Ctr++;  		// # of bytes to follow minus two filled in at bottom of section
			_messageData[Ctr++] = 0x02;      // SRC/DES
			_messageData[Ctr++] = 0x26;      // Echo of command received
			_messageData[Ctr++] = 0x00;      // system status items
			_messageData[Ctr++] = 0x00;      //    "   "
			_messageData[Ctr++] = 0x00;      //    "   "
			_messageData[Ctr++] = 0x00;      //    "   "  
			_messageData[Ctr++] = 0x00;     // device status items
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     //    "   "   
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     //    "   "
			_messageData[Ctr++] = 0x00;     // process status items
			_messageData[Ctr++] = 0x00;     //    "   "									
	
			_messageData[Ctr++] = 0x42;							
			_messageData[Ctr++] = 0x42;	
			_messageData[Ctr++] = 0x82;	

			EmetconWord newWord;
			int Function = 0;
			Ctr = newWord.InsertWord(D_WORD,  _messageData, Function, Ctr);
			_words[0]=newWord;
			_messageData[Ctr++] = 0x42; 
	
			_messageData[3] = Ctr-4;      // # of bytes to follow minus two (see note above)
										
			unsigned short CRC = NCrcCalc_C ((_messageData + 1), Ctr-1);
			_messageData[Ctr++] = HIBYTE (CRC);
			_messageData[Ctr++] = LOBYTE (CRC);
	
			//  Output for debugging only
			/*for(int i=0; i<Ctr; i++) {
				std::cout<<"_messageData "<<string(CtiNumStr(_messageData[i]).hex().zpad(2))<<std::endl;
			}*/
	
		}
		_indexOfEnd = Ctr;
	}
}

int Message::DecodeIDLC(){
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
	return _bytesToFollow;
}

void Message::DecodeCommand(unsigned char Data[]){
	_messageData[4]=Data[0];
	_messageData[5]=Data[1];
	_messageData[6]=Data[2];
	_messageData[7]=Data[3];
	_messageData[8]=Data[4];
	_messageData[9]=Data[5];
	if((Data[1] & 0x26)==0x26) {
		_commandType = DTRAN;
	}


}

int Message::DecodePreamble()
{
	char _bytesToFollow = 0;

	if( _messageData[3] & 0x04 )
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
		// CCU710 ping
		_messageType = 'p';
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
void Message::InsertWord(int WordType, unsigned char Data[]){
	_messageData[10]=Data[6];
	_messageData[12]=Data[7];
	_messageData[13]=Data[8];
	_messageData[14]=Data[9];
	_messageData[15]=Data[10];
	_messageData[16]=Data[11];
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
	oneWord.InsertWord(WordType, Data, WordFunction);
	oneWord.setWTF(WTF);
	_indexOfEnd += oneWord.getWordSize();
	_words[_indexOfWords]= oneWord;
	_indexOfWords++;
	//  If the first word is a b word, see how many c _words follow it
	//  and insert them into the incoming message
	if(_words[0].getWordType() == 2) {
		for(int i=0; i<InsertMore; i++) {
			EmetconWord anotherWord;
			anotherWord.InsertWord(3, Data, WordFunction);
			_indexOfEnd += anotherWord.getWordSize();
			_words[_indexOfWords]= anotherWord;
			_indexOfWords++;
		}
	}
}

//  This is used to insert _words into outgoing messages
void Message::InsertWord(EmetconWord oneWord){
	_words[_indexOfWords] = oneWord;
	_indexOfWords++;
	//  Determine where the message write should take place
	//  considering _words that have already been copied into _messageData
	int Here = (_indexOfWords-1) * 8;
	if(_words[0].getWordType()==D_WORD) {
		_messageData[Here + 3] = _words[0][0];
		_messageData[Here + 4] = _words[0][1];
		_messageData[Here + 5] = _words[0][2];
		_messageData[Here + 6] = _words[0][3];
		_messageData[Here + 7] = _words[0][4];
		_messageData[Here + 8] = _words[0][5];
		_messageData[Here + 9] = _words[0][6];
		_indexOfEnd+=7;
	}
	else if(_words[0].getWordType()==4) {
		_messageData[Here + 3] = _words[0][0];
		_messageData[Here + 4] = _words[0][1];
		_messageData[Here + 5] = _words[0][2];
		_messageData[Here + 6] = _words[0][3];
		_messageData[Here + 7] = _words[0][4];
		_messageData[Here + 8] = _words[0][5];
		_messageData[Here + 9] = _words[0][6];
		_indexOfEnd+=7;
	}
	else if(_words[0].getWordType()==5) {
		_messageData[Here + 3] = _words[0][0];
		_messageData[Here + 4] = _words[0][1];
		_messageData[Here + 5] = _words[0][2];
		_messageData[Here + 6] = _words[0][3];
		_messageData[Here + 7] = _words[0][4];
		_messageData[Here + 8] = _words[0][5];
		_messageData[Here + 9] = _words[0][6];
		_indexOfEnd+=7;
	}
}


int Message::DecodeWTF(int WordType, unsigned char Data[]){
	if((Data[4] & 0x10) == 0x10) {
		return 1;
	}
	if((Data[4] & 0x20) == 0x20) {
		return 2;
	}
	if((Data[4] & 0x30) == 0x30) {
		return 3;
	}
	else return 0;
}

int Message::DecodeDefinition(){
	int WordType = 0;
	if(_messageData[10] == 0xaf){  
		WordType = B_WORD;
	}
	else
		WordType = 999;
	return WordType;
}

int Message::DecodeFunction(int WordType, unsigned char Data[]){
	char FunctionType = 0;
	if(WordType== B_WORD) {
		//   check to see what function is specified
		if((_messageData[15] & 0xc) == 0x0c) {
			//  Function with acknowledge
			FunctionType = FUNCACK;
		}
		else if((_messageData[15] & 0x04) == 0x04) {
			//  Read
			FunctionType = READ;
		}
		else if((_messageData[15] & 0x00) == 0x00) {
			//  Write
			FunctionType = WRITE;
		}
	}
		return FunctionType;
}

void Message::InsertAck(){
	_messageData[_indexOfEnd] = 0xc3;
	_indexOfEnd++;
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
	return _messageData;
}

void Message::CreatePreamble(){
	_messageData[0] = 0xc3;
	_messageData[1] = 0xc3;
	_messageData[2] = 0x82;
	_indexOfEnd += 3;
}

unsigned short Message::InsertCRC(unsigned long Length){
	unsigned short CRC;
	// ptr to UCHAR and USHORT
	unsigned char * Message = _messageData;
	CRC = NCrcCalc_C ((Message + 1), Length);
	return CRC;
}

unsigned char Message::getAddress(){
	return _messageData[1];
}

unsigned char Message::getFrame(){
	unsigned char Frame = 0x00;
	Frame =	(_messageData[2] & 0xe0);
	Frame = (Frame >> 4);
	Frame = (Frame | 0x10);
	return Frame;
}

int Message::getBytesToFollow(){    return _bytesToFollow;              }
int Message::getCommand(){          return _commandType;                }
int Message::getPreamble(){         return _preamble;                   }
int Message::getMessageType(){      return _messageType;                }
int Message::getMessageSize(){      return _indexOfEnd;                 }
int Message::getWTF(){              return _words[0].getWTF();          }
int Message::getWordType(){         return _words[0].getWordType();     }
int Message::getWordFunction(){     return _words[0].getWordFunction(); }

