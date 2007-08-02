/*****************************************************************************
*
*    FILE NAME: EmetconWord.h
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
#ifndef  __EMETCONWORD_H__
#define  __EMETCONWORD_H__
class CTINEXUS;
	

//  The EmetconWord class can be used to build/store words 
class EmetconWord
{
	public:
		// Default constructor
		EmetconWord();
		// Initialize a word
		int InsertWord(int _wordType, unsigned char * MessageData, int WordFunc, int ccuNumber, int Ctr = 0);
		// Constructor to copy and decode an existing word
		void DecodeWord(unsigned char ReadBuffer[], CTINEXUS *newSocket);
		// Calculate the BCH code
		unsigned short BCHCalc(unsigned char* pStr, unsigned long bits);
		// Destructor

		enum WordTypes
		{
			A_WORD = 31,
			B_WORD = 32,
			C_WORD = 33,
			D_WORD = 34,
			E_WORD = 35,
		};

		// Function to read the data in the word to the screen
		void ReadWord();
		// Return the word's definition
		int getWordType();
		// Return the word's function
		int getWordFunction();
		// Return the word's size
		int  getWordSize();
		//  Returns the number of words following the first b word
		int getWTF();
		//  Allow the WTF to be set after it is determined
		void setWTF(int newWTF);
		//  Allow access to WordData for copying into messages
		unsigned char operator[](int index);
	private:
		unsigned char _wordData[20];
		int _wordType;
		int _wordFunction;
		int  _wordSize;
		int  _wtf;
};
#endif

