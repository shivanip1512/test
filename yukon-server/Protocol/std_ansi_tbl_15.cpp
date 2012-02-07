/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_15
*
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_tbl_15.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_five.cpp,v $
      Revision 1.9  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.8  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.7  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.6  2005/09/29 21:18:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.5  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.4  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_15.h"


using std::endl;
using std::string;
//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable15::CtiAnsiTable15( BYTE *dataBlob, int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2,
                                          int format1, int format2, DataOrder dataOrder)
{
   int      index;
   int      bytes;

    _RawConstantsSelector = selector;
    _NumberConstantsEntries = constants_entries;
    _NoOffsetFlag = noOffset;
    _SetOnePresentFlag = useSet1;
    _SetTwoPresentFlag = useSet2;
    _NIFormat1 = format1;
    _NIFormat2 = format2;
    _lsbDataOrder = dataOrder;

   _constants_table = new CONSTANTS_SELECT[_NumberConstantsEntries];

   switch( _RawConstantsSelector )
   {
   case 0:
      {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
      }
      break;

   case 1:
      {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
      }
      break;

   case 2:
      {
         for( index = 0; index < _NumberConstantsEntries; index++ )
         {
            //if( _constants_table != NULL )
           // {
               bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.multiplier, _NIFormat1, _lsbDataOrder );
               dataBlob += bytes;

               if( !_NoOffsetFlag )
               {
                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.offset, _NIFormat1, _lsbDataOrder );
                  dataBlob += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.offset = 0;
               }

               if( _SetOnePresentFlag )
               {
                  memcpy( (void *)&_constants_table[index].electric_constants.set1_constants.set_flags, dataBlob, sizeof( unsigned char ));
                  dataBlob += 1;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_f1, _NIFormat1, _lsbDataOrder );
                  dataBlob += bytes;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_p1, _NIFormat1, _lsbDataOrder );
                  dataBlob += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.set1_constants.ratio_f1 = 0;
                  _constants_table[index].electric_constants.set1_constants.ratio_p1 = 0;
                  _constants_table[index].electric_constants.set1_constants.set_flags.set_applied_flag = 0;
                  _constants_table[index].electric_constants.set1_constants.set_flags.filler = 0;
               }

               if( _SetTwoPresentFlag )
               {
                  memcpy( (void *)&_constants_table[index].electric_constants.set2_constants.set_flags, dataBlob, sizeof( unsigned char ));
                  dataBlob += 1;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_f1, _NIFormat1, _lsbDataOrder );
                  dataBlob += bytes;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_p1, _NIFormat1, _lsbDataOrder );
                  dataBlob += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.set2_constants.ratio_f1 = 0;
                  _constants_table[index].electric_constants.set2_constants.ratio_p1 = 0;
                  _constants_table[index].electric_constants.set2_constants.set_flags.set_applied_flag = 0;
                  _constants_table[index].electric_constants.set2_constants.set_flags.filler = 0;
               }
            //}
         }
      }
      break;

   default:
      {
         //3-255 are reserved
      }
      break;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable15::~CtiAnsiTable15()
{
    if (_constants_table != NULL)
    {
        delete[] _constants_table;
        _constants_table = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable15& CtiAnsiTable15::operator=(const CtiAnsiTable15& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;

}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getElecMultiplier( int index )
{
   return _constants_table[index].electric_constants.multiplier;
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getElecOffset( int index )
{
   return _constants_table[index].electric_constants.offset;
}

//=========================================================================================================================================
//=========================================================================================================================================

SET_APPLIED CtiAnsiTable15::getElecSetOneConstants( void )
{
   return _constants_table->electric_constants.set1_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

SET_APPLIED CtiAnsiTable15::getElecSetTwoConstants( void )
{
   return _constants_table->electric_constants.set2_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable15::getUseControl( void )
{
   if( 1 )
      return _constants_table->electric_constants.set1_constants.set_flags.set_applied_flag;
   else
      return _constants_table->electric_constants.set2_constants.set_flags.set_applied_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getRatioF1( void )
{
   return(1);
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getRatioP1( void )
{
   return(1);
}

//=========================================================================================================================================
//=========================================================================================================================================

ELECTRIC_CONSTANTS CtiAnsiTable15::getElecConstants( void )
{
   return _constants_table->electric_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable15::getSet1AppliedFlag(int index )
{
   return ((bool)_constants_table[index].electric_constants.set1_constants.set_flags.set_applied_flag);
}
bool CtiAnsiTable15::getSet2AppliedFlag(int index )
{
   return ((bool)_constants_table[index].electric_constants.set2_constants.set_flags.set_applied_flag);
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable15::printResult( const string& deviceName )
{
    int integer;
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "==================== "<<deviceName<<"  Std Table 15 ========================" << endl;
    }

    switch( _RawConstantsSelector )
   {
   case 0:
      {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
      }
      break;

   case 1:
      {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
      }
      break;

   case 2:
      {
          {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << "       Multiplier Offset ";
          }
          if ( _SetOnePresentFlag )
          {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << "set1_applied_flag  ratio1_f1  ratio1_p1 ";
          }
          if ( _SetTwoPresentFlag )
          {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << "set2_applied_flag  ratio2_f1  ratio2_p1 ";
          }
         for( int index = 0; index < _NumberConstantsEntries; index++ )
         {
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << endl << "ELTRC CONSTS RCD "<<index<<":";
                  dout << "  "<<_constants_table[index].electric_constants.multiplier;
              }
              if (!_NoOffsetFlag)
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << "   "<< _constants_table[index].electric_constants.offset;
              }
              if( _SetOnePresentFlag )
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << "   "<<getSet1AppliedFlag(index);
                  dout << "   "<< _constants_table[index].electric_constants.set1_constants.ratio_f1;
                  dout << "   "<< _constants_table[index].electric_constants.set1_constants.ratio_p1;
              }
              if( _SetTwoPresentFlag )
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << "   "<<getSet2AppliedFlag(index);
                  dout << "   "<< _constants_table[index].electric_constants.set2_constants.ratio_f1;
                  dout << "   "<< _constants_table[index].electric_constants.set2_constants.ratio_p1;
              }
          }
      }
      break;

   default:
      {
         //3-255 are reserved
      }
      break;
   }


}

