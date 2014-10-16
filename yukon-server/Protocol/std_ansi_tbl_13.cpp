/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_13
*
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_13.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_three.cpp,v $
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

      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_13.h"

using std::endl;
using std::string;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable13::CtiAnsiTable13( BYTE *dataBlob, int nbr_demand_cntl_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude, DataOrder dataOrder)
{
   int      index;

   _pfExcludeFlag = pf_exclude;
   _slidingDemandFlag = sliding_demand;
   _resetExcludeFlag = reset_exclude;

   if( reset_exclude != false )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_demand_control_record.reset_exclusion, sizeof( unsigned char )); //1 byte
   }

   if( pf_exclude != false )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_demand_control_record.excludes.p_fail_recogntn_tm, sizeof( unsigned char )); //1 byte
      dataBlob += toAnsiIntParser(dataBlob, &_demand_control_record.excludes.p_fail_exclusion, sizeof( unsigned char )); //1 byte
      dataBlob += toAnsiIntParser(dataBlob, &_demand_control_record.excludes.cold_load_pickup, sizeof( unsigned char )); //1 byte
   }

   _numberDemandCtrlEntries = nbr_demand_cntl_entries;
   _demand_control_record._int_control_rec = new INT_CONTROL_RCD[nbr_demand_cntl_entries];

   for( index = 0; index < _numberDemandCtrlEntries; index++ )
   {
      if( sliding_demand != false )
      {
         dataBlob += toAnsiIntParser(dataBlob, &_demand_control_record._int_control_rec[index].cntl_rec.sub_int, sizeof( unsigned char )); //1 byte
         dataBlob += toAnsiIntParser(dataBlob, &_demand_control_record._int_control_rec[index].cntl_rec.int_mulitplier, sizeof( unsigned char )); //1 byte
      }
      else
      {
          dataBlob += toAnsiIntParser(dataBlob, &_demand_control_record._int_control_rec[index].int_length,  sizeof( unsigned char) *2, dataOrder ); // 2 bytes
      }
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable13::~CtiAnsiTable13()
{
    if (_demand_control_record._int_control_rec != NULL)
    {
        delete []_demand_control_record._int_control_rec;
        _demand_control_record._int_control_rec = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable13& CtiAnsiTable13::operator=(const CtiAnsiTable13& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================
bool CtiAnsiTable13::getPFExcludeFlag()
{
    return _pfExcludeFlag;
}
bool CtiAnsiTable13::getSlidingDemandFlag()
{
    return _slidingDemandFlag;
}
bool CtiAnsiTable13::getResetExcludeFlag()
{
    return _resetExcludeFlag;
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable13::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    if (_resetExcludeFlag)
    {
        itemList.add("RESET_EXCLUSION")    << _demand_control_record.reset_exclusion;
    }

    if (_pfExcludeFlag)
    {
        itemList.add("P_FAIL_RECOGNTN_TM") << _demand_control_record.excludes.p_fail_recogntn_tm;
        itemList.add("P_FAIL_EXCLUSION")   << _demand_control_record.excludes.p_fail_exclusion;
        itemList.add("COLD_LOAD_PICKUP")   << _demand_control_record.excludes.cold_load_pickup;
    }

    Cti::FormattedTable table;

    if (_slidingDemandFlag)
    {
        table.setCell(0, 0) << "INDEX";
        table.setCell(0, 1) << "SUB_INT";
        table.setCell(0, 2) << "INT_MULTIPLIER";

        for( int x = 0; x < _numberDemandCtrlEntries; x++ )
        {
            const unsigned row = x+1;
            table.setCell(row, 0) << x;
            table.setCell(row, 1) << _demand_control_record._int_control_rec[x].cntl_rec.sub_int;
            table.setCell(row, 2) << _demand_control_record._int_control_rec[x].cntl_rec.int_mulitplier;
        }
    }
    else
    {
        table.setCell(0, 0) << "INDEX";
        table.setCell(1, 0) << "INT_LENGTH";

        for( int x = 0; x < _numberDemandCtrlEntries; x++ )
        {
            const unsigned row = x+1;
            table.setCell(row, 0) << x;
            table.setCell(row, 1) << _demand_control_record._int_control_rec[x].int_length;
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 13") <<
            itemList <<
            table
            );

}
