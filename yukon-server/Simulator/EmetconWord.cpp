/*****************************************************************************
*
*    FILE NAME: EmetconWord.cpp
*
*    DATE: 7/1/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: Make Words
*    
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#include "yukon.h"
#include "EmetconWord.h"
#include "stdio.h"
#include "cticalls.h"
#include "cti_asmc.h"

//  Delete these next two lines if not using rand()
#include "stdlib.h"
#include "time.h"
#include "numstr.h"

#include <iostream>

using namespace std;

/**************************************************
/*  EmetconWord functions   
***************************************************/

//  Default constructor
EmetconWord::EmetconWord()
{
	_wordFunction = 0;
	_wordType = 0;
	_wordSize = 0;
	_wtf = 0;
	memset(_wordData, 0, 20);
}

// build a new word
int EmetconWord::InsertWord(int Type, unsigned char * pMessageData, int WordFunc, int mctNumber, int Ctr, int Repeaters)
{
    //printf("\n REPEATERS VAR: %d \n", Repeaters);
	_wordType = Type;
	_wordFunction = WordFunc;

	//  Temporary code to randomly send back e words instead of d words
	/*if(_wordType == D_WORD) {
		if(rand() % 2){
			_wordType = E_WORD;
		}
	}*/
	if( _wordType == D_WORD ) 
	{
		_wordSize = 7;
        
		pMessageData[Ctr++] = ((mctNumber >> 12) & 0x01) | (Repeaters << 1) | 0xd0;   //beginning of d word
		pMessageData[Ctr++] = (mctNumber >> 4) & 0xff;
		pMessageData[Ctr++] = (mctNumber << 4) & 0xf0;   //data begins in second half of this byte
        pMessageData[Ctr++] = 0x0f;   // data
		pMessageData[Ctr++] = 0x00;   // data
		pMessageData[Ctr++] = 0x00;   // data ends first half of this byte
		pMessageData[Ctr++] = 0x00; 

		unsigned char BCH = BCHCalc_C (pMessageData+Ctr-7, 46);
		pMessageData[Ctr-2] |= BCH >> 6;
		pMessageData[Ctr-1] = BCH << 2;

		return Ctr;
	}
	else if(_wordType == 3) {
		_wordSize = 7;
		// calculate bch code
		unsigned short BCH;
		unsigned char* cWord = _wordData;

		cWord[0] = 0xc0;
		cWord[1] = 0x00; 
		cWord[2] = 0x00;
		cWord[3] = 0x00;            
		cWord[4] = 0x00;
		if(pMessageData[4]==0x0) {
			cWord[5] = 0x08; 
		}
		cWord[5] = 0x00;            
		cWord[6] = 0x00;  

		BCH = BCHCalc (cWord, 46);
		cWord[5] |= BCH >> 6;
		cWord[6] = BCH << 2;
        return Ctr;
	}
	else if(_wordType == 2) {
		_wordSize = 7;
		// calculate bch code
		unsigned short BCH;
		unsigned char* cWord = _wordData;

		cWord[0] = 0xa0;
		cWord[1] = 0x00; 
		cWord[2] = 0x00;
		cWord[3] = 0x00;            
		cWord[4] = 0x00;
		cWord[5] = 0x00;            
		cWord[6] = 0x00;  

		BCH = BCHCalc (cWord, 46);
		cWord[5] |= BCH >> 6;
		cWord[6] = BCH << 2;
        return Ctr;
	}
	else if(_wordType == 5) {
		_wordSize = 7;
		// calculate bch code
		unsigned short BCH;
		unsigned char* dWord = _wordData;

		int pickError = rand() % 6;

		dWord[0] = 0xe0;  
		dWord[1] = 0x24;
		switch(pickError) {
		case 1:  // incoming BCH error
			dWord[2] = 0x08;
			dWord[3] = 0x00; 
			break;
		case 2:  // incoming no response
			dWord[2] = 0x04;
			dWord[3] = 0x00; 
			break;
		case 3:  // listen ahead BCH eror
			dWord[2] = 0x02;
			dWord[3] = 0x00; 
			break;
		case 4:  // listen ahead no response
			dWord[2] = 0x01;
			dWord[3] = 0x00; 
			break;
		case 5:  // weak signal
			dWord[2] = 0x00;
			dWord[3] = 0x80; 
			break;
		case 6:  // repeater code mis-match
			dWord[2] = 0x00;
			dWord[3] = 0x40; 
			break;
		}
		dWord[4] = 0x00;
		dWord[5] = 0x00;            
		dWord[6] = 0x00;  

		BCH = BCHCalc (dWord, 46);
		dWord[5] |= BCH >> 6;
		dWord[6] = BCH << 2;
        return Ctr;
	}
	else if(_wordType == 1) {
		_wordSize = 4;
		// calculate bch code
		unsigned short BCH;
		unsigned char* cWord = _wordData;

		cWord[0] = 0x80;
		cWord[1] = 0x00; 
		cWord[2] = 0x00;
		cWord[3] = 0x00;             

		BCH = BCHCalc (cWord, 24);
		cWord[5] |= BCH >> 6;
		cWord[6] = BCH << 2;
        return Ctr;
	}
    else
    {
        return Ctr;
    }
}



// copy and decode an existing word
void EmetconWord::DecodeWord(unsigned char ReadBuffer[], CTINEXUS * newSocket){
}

void EmetconWord::setWTF(int newWTF){
	_wtf = newWTF;
}

unsigned short EmetconWord::BCHCalc(unsigned char* pStr, unsigned long bits)
{

   int     bitcnt;
   unsigned short  axreg = ((unsigned short)*pStr++) << 8;     // In ah reg.

   while(bits)
   {
      bitcnt = 8;
      if(bits > 8)   axreg |= ((unsigned short)*pStr++);      // In al reg.

      while(bitcnt && bits)
      {
         if(axreg & 0x8000)
         {
            axreg ^= 0x8600;
         }

         axreg <<= 1;
         bitcnt--;
         bits--;
      }
   }

   axreg >>= 8;        // Return in the lsb left justified
   return(axreg);
}

int EmetconWord::getWordType()		{   return _wordType;   	}

int EmetconWord::getWTF()			{   return _wtf;        	}

int EmetconWord::getWordFunction()	{   return _wordFunction;   }

int EmetconWord::getWordSize()		{   return _wordSize;   	}


// Function to read the data in the word to the screen
void EmetconWord::ReadWord(){
	if(_wordType == 'b') {
		unsigned char Data[3];

		Data[0] = ((_wordData[ 7] & 0x0f) << 4) | 
			      ((_wordData[ 8] & 0xf0) >> 4);

		Data[1] = ((_wordData[ 8] & 0x0f) << 4) | 
                  ((_wordData[ 9] & 0xf0) >> 4); 

		Data[2] = ((_wordData[ 9] & 0x0f) << 4) | 
                  ((_wordData[10] & 0xf0) >> 4); 

 //   	std::cout <<"Received 3 bytes of data after a b word"<<std::endl;
	//	std::cout <<"Byte 1: "<< string(CtiNumStr(Data[0]).xhex())<<std::endl;
//		std::cout <<"Byte 2: "<< string(CtiNumStr(Data[1]).xhex())<<std::endl;
	//	std::cout <<"Byte 3: "<< string(CtiNumStr(Data[2]).xhex())<<std::endl;
	}
	else if(_wordType == 'd') {
		unsigned char Data[3];
		Data[0] = ((_wordData[2] & 0x7) << 5) | ((_wordData[3] & 0xf8) >> 3);
		Data[1] = ((_wordData[3] & 0x7) << 5) | ((_wordData[4] & 0xf8) >> 3); 
		Data[2] = ((_wordData[4] & 0x7) << 5) | ((_wordData[5] & 0xf8) >> 3); 
	//	std::cout <<"Sent 3 bytes of data in a d word"<<std::endl;
	//	std::cout <<"Byte 1: "<< string(CtiNumStr(Data[0]).xhex())<<std::endl;
	//	std::cout <<"Byte 2: "<< string(CtiNumStr(Data[1]).xhex())<<std::endl;
	//	std::cout <<"Byte 3: "<< string(CtiNumStr(Data[2]).xhex())<<std::endl;
	}
}

unsigned char EmetconWord::operator[](int index){
	return _wordData[index];
}

