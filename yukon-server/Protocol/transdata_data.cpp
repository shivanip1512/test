#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   transdata_data
*
* Date:   9/19/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/02/10 23:23:58 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "logger.h"
#include "transdata_data.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataData::CtiTransdataData( BYTE *data )
{
   init();
   fillData( data );
}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataData::~CtiTransdataData()
{
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataData::init( void )
{
   _year       = 0;
   _month      = 0;
   _day        = 0;
   _hour       = 0;
   _minute     = 0;
   _second     = 0;

   _dataID     = -1;
   _reading    = 0;
   _isNegative = false;
   _formatCode = -1;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataData::fillData( BYTE *data )
{
   bool     isTime = false;
   ULONG    temp;

   isTime = false;

   _dataID = stringToInt( data, Idd_width );
   data += Idd_width;

   isTime = dataIsTime( _dataID );

   if( isTime )
   {
      temp = stringToInt( data, Data_width );
   }
   else
   {
      _reading = stringToInt( data, Data_width );
   }
   data += Data_width;

   _isNegative = isDataNegative( data, Sign_width );
   data += Sign_width;

   _formatCode = stringToInt( data, Format_width );

   if( isTime )
   {
      formatTime( temp );
   }
   else
   {
      formatData();
   }
}

//=====================================================================================================================
//=====================================================================================================================

ULONG CtiTransdataData::stringToInt( BYTE *str, int len )
{
   int            index = 0;
   ULONG          accumulator = 0;
   unsigned char  c;

   for( index; index < len; index++ )
   {
      c = *str;

      accumulator = accumulator * 10;
      accumulator += c - 48;

      str++;
   }

   return( accumulator );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataData::isDataNegative( BYTE *str, int len )
{
   bool neg = false;

   if( *str == 0x2d )
      neg = true;

   return( neg );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataData::formatData( void )
{
   switch( _formatCode )
   {
   case 2:
   case 9:
      _reading = _reading / 10000.0;
      break;

   case 3:
   case 10:
      _reading = _reading / 1000.0;
      break;

   case 4:
   case 11:
      _reading = _reading / 100.0;
      break;

   case 5:
   case 12:
      _reading = _reading / 10.0;
      break;

   case 6:
   case 13:
      _reading = _reading;
      break;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataData::formatTime( ULONG temp )
{
   int      holder;
   RWTime   time;
   RWDate   date;

   switch( _formatCode )
   {
   case 14:    //SS:MM:HH
      {
         _second = temp / 10000;
         holder = temp % 10000;
         _minute = holder / 100;
         _hour = holder % 100;
      
         _year = date.year();
         _month = date.month();
         _day = date.dayOfMonth();
      }
      break;

   case 15:    //HH:MM:SS
      {
         _hour = temp / 10000;
         holder = temp % 10000 ;
         _minute = holder / 100;
         _second = holder % 100;
      
         _year = date.year();
         _month = date.month();
         _day = date.dayOfMonth();
      }
      break;

   case 16:    //DD:MM:YY
      {
         _day = temp / 10000;
         holder = temp % 10000 ;
         _month = holder / 100;
         _year = holder % 100;
      
         _hour = time.hour();
         _minute = time.minute();
         _second = time.second();
      }
      break;

   case 17:    //MM:DD:YY
      {
         _month = temp / 10000;
         holder = temp % 10000 ;
         _day = holder / 100;
         _year = holder % 100;
      
         _hour = time.hour();
         _minute = time.minute();
         _second = time.second();
      }
      break;

   case 18:    //YY:DD:MM
      {
         _year = temp / 10000;
         holder = temp % 10000 ;
         _day = holder / 100;
         _month = holder % 100;
      
         _hour = time.hour();
         _minute = time.minute();
         _second = time.second();
      }
      break;

   case 19:    //YY:MM:DD
      {
         _year = temp / 10000;
         holder = temp % 10000 ;
         _month = holder / 100;
         _day = holder % 100;
      
         _hour = time.hour();
         _minute = time.minute();
         _second = time.second();
      }
      break;

   case 20:    //DD:HH:MM
      {
         _day = temp / 10000;
         holder = temp % 10000 ;
         _hour = holder / 100;
         _minute = holder % 100;
      
         _year = date.year();
         _month = date.month();
         _day = date.dayOfMonth();
      }
      break;

   case 21:    //MM:HH:DD
      {
         _minute = temp / 10000;
         holder = temp % 10000 ;
         _hour = holder / 100;
         _day = holder % 100;
   
         _year = date.year();
         _month = date.month();
         _day = date.dayOfMonth();
      }
      break;

   case 22:    //MM:DD:HH
      {
         _minute = temp / 10000;
         holder = temp % 10000 ;
         _day = holder / 100;
         _hour = holder % 100;
   
         _year = date.year();
         _month = date.month();
         _day = date.dayOfMonth();
      }
   
   case 23:    //HH:DD:MM
      {
         _hour = temp / 10000;
         holder = temp % 10000 ;
         _day = holder / 100;
         _minute = holder % 100;
   
         _year = date.year();
         _month = date.month();
         _day = date.dayOfMonth();
      }
      break;
   }
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataData::dataIsTime( int id )
{
   bool result = false;

   switch( id )
   {
   case 0:
   case 8:
   case 16:
   case 24:
   case 32:
   case 107:
   case 111:
   case 115:
   case 119:
   case 131:
   case 135:
   case 139:
   case 143:
   case 155:
   case 159:
   case 163:
   case 167:
   case 179:
   case 183:
   case 187:
   case 191:
   case 300:
   case 301:
   case 302:
   case 303:
   case 315:
   case 316:
   case 317:
   case 318:
   case 323:
   case 324:
   case 325:
   case 326:
   case 331:
   case 332:
   case 333:
   case 334:
   case 339:
   case 340:
   case 341:
   case 342:
      result = true;
      break;

   }

   return( result );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataData::getID( void )
{
   return( _dataID );
}

//=====================================================================================================================
//=====================================================================================================================

FLOAT CtiTransdataData::getReading( void )
{
   return( _reading );
}

//=====================================================================================================================
//=====================================================================================================================
   
unsigned CtiTransdataData::getYear( void )
{
   return( _year );
}

//=====================================================================================================================
//=====================================================================================================================
                     
unsigned CtiTransdataData::getMonth( void )
{
   return( _month );
}

//=====================================================================================================================
//=====================================================================================================================
   
unsigned CtiTransdataData::getDay( void )
{
   return( _day );
}

//=====================================================================================================================
//=====================================================================================================================
   
unsigned CtiTransdataData::getHour( void )
{
   return( _hour );
}

//=====================================================================================================================
//=====================================================================================================================
   
unsigned CtiTransdataData::getMinute( void )
{
   return( _minute );
}

//=====================================================================================================================
//=====================================================================================================================
   
unsigned CtiTransdataData::getSecond( void )
{
   return( _second );
}


