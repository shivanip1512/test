
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/30 21:37:18 $
*    History: 
      $Log: std_ansi_tbl_one_four.cpp,v $
      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_one_four.h"

//=========================================================================================================================================
//wow... what a mess this is
//allocate however many data_records we have (from table 11)
//then, for each one, allocate howevermany source id's (again from table 11)
//=========================================================================================================================================

CtiAnsiTableOneFour::CtiAnsiTableOneFour( int dataCtrlLen, int numDataCtrlEntries )
{
    _controlLength = dataCtrlLen;
    _controlEntries = numDataCtrlEntries;
}
CtiAnsiTableOneFour::CtiAnsiTableOneFour( BYTE *dataBlob, int dataCtrlLen, int numDataCtrlEntries )
{
   int   index;
   int   cnt;

   //we're saving these so we can delete the memory later
   _controlLength = dataCtrlLen;
   _controlEntries = numDataCtrlEntries;

   if( _controlEntries != 0 )
   {
      _data_control_record.data_rcd = new DATA_RCD[_controlEntries];

      for( index = 0; index < _controlEntries; index++ )
      {
         if( _controlLength != 0 )
         {
            _data_control_record.data_rcd[index].source_id = new unsigned char[_controlLength];

            for( cnt = 0; cnt < _controlLength; cnt++ )
            {
               memcpy( (void *)&_data_control_record.data_rcd[index].source_id[cnt], dataBlob, sizeof( unsigned char ));
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
   //int count;

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
//=========================================================================================================================================
//=========================================================================================================================================


void CtiAnsiTableOneFour::decodeResultPiece( BYTE **dataBlob )
{
   
   if( _controlEntries != 0 )
   {
      _data_control_record.data_rcd = new DATA_RCD[_controlEntries];

      for(int index = 0; index < _controlEntries; index++ )
      {
         if( _controlLength != 0 )
         {
            _data_control_record.data_rcd[index].source_id = new unsigned char[_controlLength];

            for(int cnt = 0; cnt < _controlLength; cnt++ )
            {
               memcpy( (void *)&_data_control_record.data_rcd[index].source_id[cnt], *dataBlob, sizeof( unsigned char ));
               *dataBlob += sizeof( unsigned char );
            }
         }
      }
   } 
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneFour::generateResultPiece( BYTE **dataBlob )
{
   //if( _controlEntries != 0 )
   //{
   //   _data_control_record.data_rcd = new DATA_RCD[_controlEntries];

      for( int index = 0; index < _controlEntries; index++ )
      {
         if( _controlLength != 0 )
         {
           // _data_control_record.data_rcd[index].source_id = new unsigned char[_controlLength];

            for( int cnt = 0; cnt < _controlLength; cnt++ )
            {
               memcpy( *dataBlob, (void *)&_data_control_record.data_rcd[index].source_id[cnt], sizeof( unsigned char ));
               *dataBlob += sizeof( unsigned char );
            }
         }
      }
   //}
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneFour::printResult(  )
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
        dout << endl << "=======================  Std Table 14 ========================" << endl;
    }

    for( int index = 0; index < _controlEntries; index++ )
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "  DATA_RCD " <<index<<" : ";
        }
        for( int cnt = 0; cnt < _controlLength; cnt++ )
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "   "<<(int)_data_control_record.data_rcd[index].source_id[cnt];
        }
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout <<endl;
        }
    }

}

