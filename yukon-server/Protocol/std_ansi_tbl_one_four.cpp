
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_four
*
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_one_four.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:53 $
*    History: 
      $Log: std_ansi_tbl_one_four.cpp,v $
      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_one_four.h"

//=========================================================================================================================================
//wow... what a mess this is
//allocate however many data_records we have (from table 11)
//then, for each one, allocate howevermany source id's (again from table 11)
//=========================================================================================================================================

CtiAnsiTableOneFour::CtiAnsiTableOneFour( BYTE *dataBlob, int dataCtrlLen, int numDataCtrlEntries )
{
   int   index;
   int   count;

   //we're saving these so we can delete the memory later
   _controlLength = dataCtrlLen;
   _controlEntries = numDataCtrlEntries;

   if( numDataCtrlEntries != 0 )
   {
      _data_control_record.data_rcd = new DATA_RCD[numDataCtrlEntries];

      for( index = 0; index < numDataCtrlEntries; index++ )
      {
         if( dataCtrlLen != 0 )
         {
            _data_control_record.data_rcd[index].source_id = new unsigned char[dataCtrlLen];

            for( count = 0; count < dataCtrlLen; count++ )
            {
               memcpy( _data_control_record.data_rcd[index].source_id, dataBlob, sizeof( unsigned char ));
               dataBlob += sizeof( unsigned char );
            }
         }
      }
   }
}

//=========================================================================================================================================
//the most massive memory management problem ... ick!
//hahaha... I really thought that when I wrote it, then I did table 23!
//=========================================================================================================================================

CtiAnsiTableOneFour::~CtiAnsiTableOneFour()
{
   int index;
   int count;

   for( index = 0; index < _controlEntries; index++ )
   {
      delete[] _data_control_record.data_rcd[index].source_id;
   }

   delete[] _data_control_record.data_rcd;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneFour& CtiAnsiTableOneFour::operator=(const CtiAnsiTableOneFour& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}



