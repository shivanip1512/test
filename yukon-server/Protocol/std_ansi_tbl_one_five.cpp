
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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:53 $
*    History: 
      $Log: std_ansi_tbl_one_five.cpp,v $
      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_one_five.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneFive::CtiAnsiTableOneFive( BYTE *dataBlob, int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2,
                                          int format1, int format2 )
{
   int      index;
   int      offset = 0;
   int      bytes;

   _constants_table = new CONSTANTS_SELECT[constants_entries];

   switch( selector )
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
         for( index = 0; index < constants_entries; index++ )
         {
            if( _constants_table != NULL )
            {
               bytes = toDoubleParser( dataBlob + offset, _constants_table[index].electric_constants.multiplier, format1 );
               offset += bytes;

               if( !noOffset )
               {
                  bytes = toDoubleParser( dataBlob + offset, _constants_table[index].electric_constants.offset, format1 );
                  offset += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.offset = 0;
               }

               if( useSet1 )
               {
                  memcpy( (void *)&_constants_table[index].electric_constants.set1_constants.set_flags, dataBlob + offset, sizeof( unsigned char ));
                  offset += 1;

                  bytes = toDoubleParser( dataBlob + offset, _constants_table[index].electric_constants.set1_constants.ratio_f1, format1 );
                  offset += bytes;

                  bytes = toDoubleParser( dataBlob + offset, _constants_table[index].electric_constants.set1_constants.ratio_p1, format1 );
                  offset += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.set1_constants.ratio_f1 = 0;
                  _constants_table[index].electric_constants.set1_constants.ratio_p1 = 0;
                  _constants_table[index].electric_constants.set1_constants.set_flags.set_applied_flag = 0;
                  _constants_table[index].electric_constants.set1_constants.set_flags.filler = 0;
               }

               if( useSet2 )
               {
                  memcpy( (void *)&_constants_table[index].electric_constants.set2_constants.set_flags, dataBlob + offset, sizeof( unsigned char ));
                  offset += 1;

                  bytes = toDoubleParser( dataBlob + offset, _constants_table[index].electric_constants.set2_constants.ratio_f1, format1 );
                  offset += bytes;

                  bytes = toDoubleParser( dataBlob + offset, _constants_table[index].electric_constants.set2_constants.ratio_p1, format1 );
                  offset += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.set2_constants.ratio_f1 = 0;
                  _constants_table[index].electric_constants.set2_constants.ratio_p1 = 0;
                  _constants_table[index].electric_constants.set2_constants.set_flags.set_applied_flag = 0;
                  _constants_table[index].electric_constants.set2_constants.set_flags.filler = 0;
               }
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

double CtiAnsiTableOneFive::getElecMultiplier( void )
{
   _constants_table->electric_constants.multiplier;
      return 1;
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTableOneFive::getElecOffset( void )
{
   return _constants_table->electric_constants.offset;
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
