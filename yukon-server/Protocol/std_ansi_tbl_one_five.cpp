
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_five
*
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_tbl_one_five.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/30 21:37:17 $
*    History: 
      $Log: std_ansi_tbl_one_five.cpp,v $
      Revision 1.4  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_one_five.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableOneFive::CtiAnsiTableOneFive( int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2,
                                          int format1, int format2 )
{
    _RawConstantsSelector = selector;
    _NumberConstantsEntries = constants_entries;
    _NoOffsetFlag = noOffset;
    _SetOnePresentFlag = useSet1;
    _SetTwoPresentFlag = useSet2;
    _NIFormat1 = format1;
    _NIFormat2 = format2;

}
CtiAnsiTableOneFive::CtiAnsiTableOneFive( BYTE *dataBlob, int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2,
                                          int format1, int format2 )
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
               bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.multiplier, _NIFormat1 );
               dataBlob += bytes;

               if( !_NoOffsetFlag )
               {
                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.offset, _NIFormat1 );
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

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_f1, _NIFormat1 );
                  dataBlob += bytes;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_p1, _NIFormat1 );
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

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_f1, _NIFormat1 );
                  dataBlob += bytes;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_p1, _NIFormat1 );
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

CtiAnsiTableOneFive::~CtiAnsiTableOneFive()
{
   delete[] _constants_table;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneFive& CtiAnsiTableOneFive::operator=(const CtiAnsiTableOneFive& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;

}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTableOneFive::getElecMultiplier( int index )
{
   return _constants_table[index].electric_constants.multiplier;
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTableOneFive::getElecOffset( int index )
{
   return _constants_table[index].electric_constants.offset;
}

//=========================================================================================================================================
//=========================================================================================================================================

SET_APPLIED CtiAnsiTableOneFive::getElecSetOneConstants( void )
{
   return _constants_table->electric_constants.set1_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

SET_APPLIED CtiAnsiTableOneFive::getElecSetTwoConstants( void )
{
   return _constants_table->electric_constants.set2_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableOneFive::getUseControl( void )
{
   if( 1 )
      return _constants_table->electric_constants.set1_constants.set_flags.set_applied_flag;
   else
      return _constants_table->electric_constants.set2_constants.set_flags.set_applied_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTableOneFive::getRatioF1( void )
{
   return(1);
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTableOneFive::getRatioP1( void )
{
   return(1);
}

//=========================================================================================================================================
//=========================================================================================================================================

ELECTRIC_CONSTANTS CtiAnsiTableOneFive::getElecConstants( void )
{
   return _constants_table->electric_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableOneFive::getSet1AppliedFlag(int index )
{
   return ((bool)_constants_table[index].electric_constants.set1_constants.set_flags.set_applied_flag);
}
bool CtiAnsiTableOneFive::getSet2AppliedFlag(int index )
{
   return ((bool)_constants_table[index].electric_constants.set2_constants.set_flags.set_applied_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================


void CtiAnsiTableOneFive::decodeResultPiece( BYTE **dataBlob )
{
    int      index;
    int      bytes;

    {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << endl << "decode RESULT PIECE!! "<<endl;
    }
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
               bytes = toDoubleParser( *dataBlob, _constants_table[index].electric_constants.multiplier, _NIFormat1 );
               *dataBlob += bytes;

               if( !_NoOffsetFlag )
               {
                  bytes = toDoubleParser( *dataBlob, _constants_table[index].electric_constants.offset, _NIFormat1 );
                  *dataBlob += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.offset = 0;
               }

               if( _SetOnePresentFlag )
               {
                  memcpy( (void *)&_constants_table[index].electric_constants.set1_constants.set_flags, *dataBlob, sizeof( unsigned char ));
                  *dataBlob += 1;

                  bytes = toDoubleParser( *dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_f1, _NIFormat1 );
                  *dataBlob += bytes;

                  bytes = toDoubleParser( *dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_p1, _NIFormat1 );
                  *dataBlob += bytes;
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
                  memcpy( (void *)&_constants_table[index].electric_constants.set2_constants.set_flags, *dataBlob, sizeof( unsigned char ));
                  *dataBlob += 1;

                  bytes = toDoubleParser( *dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_f1, _NIFormat1 );
                  *dataBlob += bytes;

                  bytes = toDoubleParser( *dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_p1, _NIFormat1 );
                  *dataBlob += bytes;
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
void CtiAnsiTableOneFive::generateResultPiece( BYTE **dataBlob )
{
    int      index;
     int      bytes;

  //   _constants_table = new CONSTANTS_SELECT[_NumberConstantsEntries];

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
             bytes = fromDoubleParser( _constants_table[index].electric_constants.multiplier, *dataBlob, _NIFormat1 );
            *dataBlob += bytes;

               if( !_NoOffsetFlag )
               {
                   bytes = fromDoubleParser( _constants_table[index].electric_constants.offset, *dataBlob, _NIFormat1 );
                  *dataBlob += bytes;
               }
               if( _SetOnePresentFlag )
               {
                  memcpy( *dataBlob, (void *)&_constants_table[index].electric_constants.set1_constants.set_flags, sizeof( unsigned char ));
                  *dataBlob += 1;
                  bytes = fromDoubleParser( _constants_table[index].electric_constants.set1_constants.ratio_f1, *dataBlob, _NIFormat1 );
                  *dataBlob += bytes;

                  bytes = fromDoubleParser( _constants_table[index].electric_constants.set1_constants.ratio_p1, *dataBlob, _NIFormat1 );
                  *dataBlob += bytes;
               }
               if( _SetTwoPresentFlag )
               {
                  memcpy( *dataBlob, (void *)&_constants_table[index].electric_constants.set2_constants.set_flags, sizeof( unsigned char ));
                  *dataBlob += 1;
                  bytes = fromDoubleParser( _constants_table[index].electric_constants.set2_constants.ratio_f1, *dataBlob, _NIFormat1 );
                  *dataBlob += bytes;

                  bytes = fromDoubleParser( _constants_table[index].electric_constants.set2_constants.ratio_p1, *dataBlob, _NIFormat1 );
                  *dataBlob += bytes;
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

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneFive::printResult(  )
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
        dout << endl << "=======================  Std Table 15 ========================" << endl;
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

