
#pragma warning( disable : 4786)
#ifndef __TRANSDATA_DATA_H__
#define __TRANSDATA_DATA_H__

/*---------------------------------------------------------------------------------*
*
* File:   transdata_data
*
* Class:
* Date:   9/19/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/01/07 16:45:38 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "xfer.h"
/*
//change these into a enum.....        
#define HEADER_WIDTH    3
#define IDD_WIDTH       3
#define DATA_WIDTH      6
#define SIGN_WIDTH      1
#define FORMAT_WIDTH    2
*/
class IM_EX_PROT CtiTransdataData
{

public:

   CtiTransdataData( BYTE *data );
   ~CtiTransdataData();

   ULONG stringToInt( BYTE *data, int len );
   bool isDataNegative( BYTE *str, int len );
   void formatData( void );
   void formatTime( ULONG temp );
   bool dataIsTime( int id );
   void init( void );
   void fillData( BYTE *data );

   int getID( void );
   FLOAT getReading( void );
   unsigned getYear( void );
   unsigned getMonth( void );
   unsigned getDay( void );
   unsigned getHour( void );
   unsigned getMinute( void );
   unsigned getSecond( void );

private:

   enum
   {
      Sign_width     = 1,
      Format_width,
      Idd_width,
      Header_width   = 3,
      Data_width     = 6
   };
   
   unsigned       _year;
   unsigned       _month;
   unsigned       _day;
   unsigned       _hour;
   unsigned       _minute;
   unsigned       _second;

   int            _dataID;
   FLOAT          _reading;
   bool           _isNegative;
   int            _formatCode;

protected:

};

#endif // #ifndef __TRANSDATA_DATA_H__
