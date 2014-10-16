/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_16
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_16.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_six.cpp,v $
      Revision 1.9  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.8  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.7  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.6  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2004/04/22 21:12:53  dsutton
      Last known revision DLS

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_16.h"

using std::endl;
using std::string;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable16::CtiAnsiTable16( BYTE *dataBlob, int nbr_constants )
{
   int   index;
   _numberOfConstants = nbr_constants;

   _source_link = new SOURCE_LINK_BFLD[_numberOfConstants];

   for( index = 0; index < _numberOfConstants; index++ )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_source_link[index], sizeof( SOURCE_LINK_BFLD ));
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable16::~CtiAnsiTable16()
{
   delete []_source_link;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable16& CtiAnsiTable16::operator=(const CtiAnsiTable16& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
SOURCE_LINK_BFLD CtiAnsiTable16::getSourceLink(int aOffset)
{
    SOURCE_LINK_BFLD ret;

    ret.constant_to_be_applied =0;
    ret.constants_flag = 0;
    ret.data_ctrl_flag =0;
    ret.demand_ctrl_flag=0;
    ret.filler=0;
    ret.pulse_engr_flag=0;
    ret.uom_entry_flag=0;

    if (aOffset < _numberOfConstants)
    {
        ret = _source_link[aOffset];
    }

    return (ret);
}

bool CtiAnsiTable16::getUOMEntryFlag( int index )
{
   return (bool)_source_link[index].uom_entry_flag;
}
bool CtiAnsiTable16::getDemandCtrlFlag( int index )
{
   return (bool)_source_link[index].demand_ctrl_flag;
}
bool CtiAnsiTable16::getDataCtrlFlag( int index )
{
   return (bool)_source_link[index].data_ctrl_flag;
}
bool CtiAnsiTable16::getConstantsFlag( int index )
{
   return (bool)_source_link[index].constants_flag;
}
bool CtiAnsiTable16::getPulseEngrFlag( int index )
{
   return (bool)_source_link[index].pulse_engr_flag;
}
bool CtiAnsiTable16::getConstToBeAppliedFlag( int index )
{
   return (bool)_source_link[index].constant_to_be_applied;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable16::printResult( const string& deviceName )
{
    Cti::FormattedTable table;

    table.setCell(0, 0) << "idx";
    table.setCell(0, 1) << "uomEntryFlg";
    table.setCell(0, 2) << "dmndCtrlFlg";
    table.setCell(0, 3) << "dataCtrlFlg";
    table.setCell(0, 4) << "constantsFlg";
    table.setCell(0, 5) << "pulseEngrFlg";
    table.setCell(0, 6) << "constToBeApp";

    for( int index = 0; index < _numberOfConstants; index++ )
    {
        const unsigned row = index + 1;

        table.setCell(row, 0) << index;
        table.setCell(row, 1) << getUOMEntryFlag(index);
        table.setCell(row, 2) << getDemandCtrlFlag(index);
        table.setCell(row, 3) << getDataCtrlFlag(index);
        table.setCell(row, 4) << getConstantsFlag(index);
        table.setCell(row, 5) << getPulseEngrFlag(index);
        table.setCell(row, 6) << getConstToBeAppliedFlag(index);
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 16") <<
            endl <<"** SourceLink **"<<
            table
            );
}

