
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_three
*
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:36:20 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_one_three.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneThree::CtiAnsiTableOneThree( BYTE *dataBlob, int nbr_demand_cntl_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude )
{
   int      index;

   _demand_control_record = new DEMAND_CONTROL_RCD;

   if( reset_exclude != false )
   {
      memcpy( (void *)&_demand_control_record->reset_exclusion, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
   }
   else
      _demand_control_record->reset_exclusion = 500;      //invalid value

   if( pf_exclude != false )
   {
      memcpy( (void *)&_demand_control_record->excludes.p_fail_recogntn_tm, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );

      memcpy( (void *)&_demand_control_record->excludes.p_fail_exclusion, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );

      memcpy( (void *)&_demand_control_record->excludes.cold_load_pickup, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
   }
   else
   {
      //default invalid vals
      _demand_control_record->excludes.p_fail_recogntn_tm = 500;
      _demand_control_record->excludes.p_fail_exclusion = 500;
      _demand_control_record->excludes.cold_load_pickup = 500;
   }

   _demand_control_record->_int_control_rec = new INT_CONTROL_RCD[nbr_demand_cntl_entries];

   for( index = 0; index < nbr_demand_cntl_entries; index++ )
   {
      if( sliding_demand != false )
      {
         memcpy( (void *)&_demand_control_record->_int_control_rec->cntl_rec.sub_int, dataBlob, sizeof( unsigned char ));
         dataBlob += sizeof( unsigned char );

         memcpy( (void *)&_demand_control_record->_int_control_rec->cntl_rec.int_mulitplier, dataBlob, sizeof( unsigned char ));
         dataBlob += sizeof( unsigned char );
      }
      else
      {
         memcpy( (void *)&_demand_control_record->_int_control_rec->int_length, dataBlob, sizeof( unsigned char) *2 );
         dataBlob += ( sizeof( unsigned char) *2 );
      }
   }

   _numberEntries = nbr_demand_cntl_entries;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneThree::~CtiAnsiTableOneThree()
{
/*   int   index;

   for( index = 0; index < _numberEntries; index++ )
   {
      delete _demand_control_record->_int_control_rec;
   }
*/
   delete []_demand_control_record->_int_control_rec;
   delete _demand_control_record;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneThree& CtiAnsiTableOneThree::operator=(const CtiAnsiTableOneThree& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


