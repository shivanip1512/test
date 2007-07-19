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
class EmetconWord{
	public:
		// Default constructor
		EmetconWord();
		// Initialize a word
		void CreateWord(char WordType, unsigned char Data[], char WordFunc = '?');
		// Constructor to copy and decode an existing word
		void DecodeWord(unsigned char ReadBuffer[], CTINEXUS * newSocket);
		// Calculate the BCH code
		unsigned short BCHCalc(unsigned char* pStr, unsigned long bits);
		// Destructor

		// Function to read the data in the word to the screen
		void ReadWord();
		// Return the word's definition
		char getWordType();
		// Return the word's function
		char getWordFunction();
		// Return the word's size
		int  getWordSize();
		//  Returns the number of words following the first b word
		int getWTF();
		//  Allow access to WordData for copying into messages
		unsigned char operator[](int index);
	private:
		unsigned char WordData[300];
		char WordType;
		char WordFunction;
		int  WordSize;
		int  WTF;
};
#endif

