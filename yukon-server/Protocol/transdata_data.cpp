
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/10/06 15:18:59 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

   _dataID = stringToInt( data, IDD_WIDTH );
   data += IDD_WIDTH;

   isTime = dataIsTime( _dataID );

   if( isTime )
   {
      temp = stringToInt( data, DATA_WIDTH );
   }
   else
   {
      _reading = stringToInt( data, DATA_WIDTH );
   }
   data += DATA_WIDTH;

   _isNegative = isDataNegative( data, SIGN_WIDTH );
   data += SIGN_WIDTH;

   _formatCode = stringToInt( data, FORMAT_WIDTH );

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
//   transdataTime  time;

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
   int holder;

   switch( _formatCode )
   {
   case 14:
      break;

   case 15:
      {
         _hour = temp / 10000;
         holder = temp % 10000 ;
         _minute = holder / 100;
         _second = holder % 100;
      }
      break;

   case 16:
      break;

   case 17:
      break;

   case 18:
      break;

   case 19:
      break;

   case 20:
      break;

   case 21:
      break;

   case 22:
      break;

   case 23:
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

