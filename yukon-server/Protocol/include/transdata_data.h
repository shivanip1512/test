#pragma once

#include "xfer.h"

class IM_EX_PROT CtiTransdataData
{

public:

   CtiTransdataData( const BYTE *data );
   ~CtiTransdataData();

   ULONG stringToInt( const BYTE *data, int len );
   bool isDataNegative( const BYTE *str, int len );
   void formatData( void );
   void formatTime( ULONG temp );
   bool dataIsTime( int id );
   void init( void );
   void fillData( const BYTE *data );

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
};
