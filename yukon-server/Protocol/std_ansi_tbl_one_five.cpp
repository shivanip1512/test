/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_five
*
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:42 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "std_ansi_tbl_one_five.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneFive::CtiAnsiTableOneFive( BYTE *dataBlob, int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2 )
{
   int      index;
   BYTE     *tracker = dataBlob;

   char     data[200];
   double   temp1;
   bool     temp2;
   bool     temp3;


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
               memcpy( (void *)&(_constants_table[index].electric_constants.multiplier ), tracker, sizeof( _constants_table[index].electric_constants.multiplier));
               tracker += sizeof( _constants_table[index].electric_constants.multiplier);

               if( !noOffset )
               {
                  memcpy( (void *)&(_constants_table[index].electric_constants.offset ), tracker, sizeof( _constants_table[index].electric_constants.offset ));
                  tracker += sizeof( _constants_table[index].electric_constants.offset );
               }
               else
               {
                  _constants_table[index].electric_constants.offset = 0;
               }

               if( useSet1 )
               {
                  memcpy( (void *)&(_constants_table[index].electric_constants.set1_constants ), tracker, sizeof( _constants_table[index].electric_constants.set1_constants ));
                  tracker += sizeof( _constants_table[index].electric_constants.set1_constants );
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
                  memcpy( (void *)&(_constants_table[index].electric_constants.set2_constants ), tracker, sizeof( _constants_table[index].electric_constants.set2_constants ));
                  tracker += sizeof( _constants_table[index].electric_constants.set2_constants );
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

         //debug
         memcpy( data, dataBlob, 20 );

         for( index = 0; index < constants_entries; index++ )
         {
            temp1 = _constants_table[index].electric_constants.multiplier;
            temp2 = _constants_table[index].electric_constants.set1_constants.set_flags.set_applied_flag;
            temp3 = _constants_table[index].electric_constants.set2_constants.set_flags.set_applied_flag;
         }
         ///

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
