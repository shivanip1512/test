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
#include "EmetconWord.h"
#include "stdio.h"

//  Delete these next two lines if not using rand()
#include "stdlib.h"
#include "time.h"

/**************************************************
/*  EmetconWord functions   
***************************************************/

//  Default constructor
EmetconWord::EmetconWord(){
	WordFunction = '?';
	WordType = '?';
	WordSize = 0;
	WTF = 0;
	for(int i=0; i<18; i++) {
		WordData[i] = 0x0;
	}
}

// build a new word
void EmetconWord::CreateWord(char Type, unsigned char Data[], char WordFunc){
	WordType = Type;
	WordFunction = WordFunc;

	//  Temporary code to randomly send back e words instead of d words
	if(WordType == 'd') {
		if(rand() % 2){
			WordType = 'e';
		}
	}

	if(WordType == 'd') {
		WordSize = 7;
		// calculate bch code
		unsigned short BCH;
		unsigned char* dWord = WordData;

		dWord[0] = 0xd0;
		dWord[1] = 0x24; 
		dWord[2] = 0x00;
		dWord[3] = 0x00;            
		dWord[4] = 0x00;
		dWord[5] = 0x00;            
		dWord[6] = 0x00;  

		BCH = BCHCalc (dWord, 46);
		dWord[5] |= BCH >> 6;
		dWord[6] = BCH << 2;
	}
	else if(WordType == 'c') {
		WordSize = 7;
		// calculate bch code
		unsigned short BCH;
		unsigned char* cWord = WordData;

		cWord[0] = 0xc0;
		cWord[1] = 0x00; 
		cWord[2] = 0x00;
		cWord[3] = 0x00;            
		cWord[4] = 0x00;
		if(Data[4]==0x0) {
			cWord[5] = 0x08; 
		}
		cWord[5] = 0x00;            
		cWord[6] = 0x00;  

		BCH = BCHCalc (cWord, 46);
		cWord[5] |= BCH >> 6;
		cWord[6] = BCH << 2;
	}
	else if(WordType == 'b') {
		WordSize = 7;
		// calculate bch code
		unsigned short BCH;
		unsigned char* cWord = WordData;

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
	}
	else if(WordType == 'e') {
		WordSize = 7;
		// calculate bch code
		unsigned short BCH;
		unsigned char* dWord = WordData;

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
	}
	else if(WordType == 'a') {
		WordSize = 4;
		// calculate bch code
		unsigned short BCH;
		unsigned char* cWord = WordData;

		cWord[0] = 0x80;
		cWord[1] = 0x00; 
		cWord[2] = 0x00;
		cWord[3] = 0x00;             

		BCH = BCHCalc (cWord, 24);
		cWord[5] |= BCH >> 6;
		cWord[6] = BCH << 2;
	}
}

// copy and decode an existing word
void EmetconWord::DecodeWord(unsigned char ReadBuffer[], CTINEXUS * newSocket){
}

void EmetconWord::setWTF(int newWTF){
	WTF = newWTF;
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

char EmetconWord::getWordType(){
	return WordType;
}

int EmetconWord::getWTF(){
	return WTF;
}

char EmetconWord::getWordFunction(){
	return WordFunction;
}

int EmetconWord::getWordSize(){
	return WordSize;
}

// Function to read the data in the word to the screen
void EmetconWord::ReadWord(){
	if(WordType == 'b') {
		unsigned char Data[3];
		Data[0] = ((WordData[7] & 0x0f) << 4) | ((WordData[8] & 0xf0) >> 4);
		Data[1] = ((WordData[8] & 0x0f) << 4) | ((WordData[9] & 0xf0) >> 4); 
		Data[2] = ((WordData[9] & 0x0f) << 4) | ((WordData[10] & 0xf0) >> 4); 
 //   	std::cout <<"Received 3 bytes of data after a b word"<<std::endl;
	//	std::cout <<"Byte 1: "<< string(CtiNumStr(Data[0]).xhex())<<std::endl;
//		std::cout <<"Byte 2: "<< string(CtiNumStr(Data[1]).xhex())<<std::endl;
	//	std::cout <<"Byte 3: "<< string(CtiNumStr(Data[2]).xhex())<<std::endl;
	}
	else if(WordType == 'd') {
		unsigned char Data[3];
		Data[0] = ((WordData[2] & 0x7) << 5) | ((WordData[3] & 0xf8) >> 3);
		Data[1] = ((WordData[3] & 0x7) << 5) | ((WordData[4] & 0xf8) >> 3); 
		Data[2] = ((WordData[4] & 0x7) << 5) | ((WordData[5] & 0xf8) >> 3); 
	//	std::cout <<"Sent 3 bytes of data in a d word"<<std::endl;
	//	std::cout <<"Byte 1: "<< string(CtiNumStr(Data[0]).xhex())<<std::endl;
	//	std::cout <<"Byte 2: "<< string(CtiNumStr(Data[1]).xhex())<<std::endl;
	//	std::cout <<"Byte 3: "<< string(CtiNumStr(Data[2]).xhex())<<std::endl;
	}
}

unsigned char EmetconWord::operator[](int index){
	return WordData[index];
}

