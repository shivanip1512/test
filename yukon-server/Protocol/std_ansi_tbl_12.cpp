/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_12
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_12.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_two.cpp,v $
      Revision 1.10  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.9  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.8  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.7  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.6  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2004/04/22 21:12:53  dsutton
      Last known revision DLS

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_12.h"

using std::endl;
using std::string;

const CHAR * CtiAnsiTable12::ANSI_UOM_WATTS = "Watts";
const CHAR * CtiAnsiTable12::ANSI_UOM_VARS = "Vars";
const CHAR * CtiAnsiTable12::ANSI_UOM_VA = "VA";
const CHAR * CtiAnsiTable12::ANSI_UOM_RMS_VOLTAGE = "RMS Voltage";
const CHAR * CtiAnsiTable12::ANSI_UOM_RMS_VOLTAGE_SQUARED = "RMS Voltage Squared";
const CHAR * CtiAnsiTable12::ANSI_UOM_INSTANTANEOUS_VOLTAGE = "Instantaneous Voltage";
const CHAR * CtiAnsiTable12::ANSI_UOM_RMS_CURRENT = "RMS Current";
const CHAR * CtiAnsiTable12::ANSI_UOM_RMS_CURRENT_SQUARED= "RMS Current Squared";
const CHAR * CtiAnsiTable12::ANSI_UOM_INSTANTANEOUS_CURRENT = "Instantaneous Current";
const CHAR * CtiAnsiTable12::ANSI_UOM_NOT_SUPPORTED = "UOM Not Supported";

const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_DIAL_READING = "Dial reading (Energy)";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_INSTANTANEOUS = "Instantaneous";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_PERIOD_BASED = "Period based (Power,RMS)";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_SUB_BLOCK_AVERAGE = "Sub-block average (Demand)";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_BLOCK_AVERAGE = "Block average (Demand)";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_RELATIVE_DIAL_READING = "Relative dial reading (Energy)";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_THERMAL = "Thermal quantity (Demand)";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_EVENT = "Event quantity (# of occurrences)";
const CHAR * CtiAnsiTable12::ANSI_TIMEBASE_UNKNOWN = "Timebase not supported";

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable12::CtiAnsiTable12( BYTE *dataBlob, int num_uom_entries, DataOrder dataOrder )
{
   int index;

   _numUomEntries = num_uom_entries;
   _uom_entries = new UOM_ENTRY_BFLD[num_uom_entries];
   

   for( index = 0; index < num_uom_entries; index++ )
   {
       dataBlob += toAnsiIntParser(dataBlob, &_uom_entries[index], sizeof( UOM_ENTRY_BFLD ), dataOrder );
   }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable12::~CtiAnsiTable12()
{
    if (_uom_entries != NULL)
    {
        delete []_uom_entries;
        _uom_entries = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable12& CtiAnsiTable12::operator=(const CtiAnsiTable12& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable12::getRawIDCode( int aOffset )
{
    int ret;

    if (aOffset < _numUomEntries)
    {
        ret=(int)_uom_entries[aOffset].id_code;
    }
    else
    {
        ret=-1;
    }

   return (ret);
}

//=========================================================================================================================================
//=========================================================================================================================================
bool CtiAnsiTable12::isCorrectData( int aOffset, int aUOM)
{
    bool ret=false;

    if (aOffset < _numUomEntries)
    {
        if (_uom_entries[aOffset].id_code == aUOM)
            ret=true;
    }
   return (ret);
}

//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable12::getResolvedIDCode( int aOffset )
{
    string ret=string (ANSI_UOM_NOT_SUPPORTED);
    if (aOffset < _numUomEntries)
    {
        switch (getRawIDCode(aOffset))
        {
            case uom_watts:
                ret=string (ANSI_UOM_WATTS);
                break;
            case uom_vars:
                ret=string (ANSI_UOM_VARS);
                break;
            case uom_va:
                ret=string (ANSI_UOM_VA);
                break;
            case uom_rms_volts:
                ret=string (ANSI_UOM_RMS_VOLTAGE);
                break;
            case uom_rms_volts_squared:
                ret=string (ANSI_UOM_RMS_VOLTAGE_SQUARED);
                break;
            case uom_instantaneous_volts:
                ret=string (ANSI_UOM_INSTANTANEOUS_VOLTAGE);
                break;
            case uom_rms_amps:
                ret=string (ANSI_UOM_RMS_CURRENT);
                break;
            case uom_rms_amps_squared:
                ret=string (ANSI_UOM_RMS_CURRENT_SQUARED);
                break;
            case uom_instantaneous_amps:
                ret=string (ANSI_UOM_INSTANTANEOUS_CURRENT);
                break;
            default:
                ret=string (ANSI_UOM_NOT_SUPPORTED);
                break;
        }
    }
    return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable12::getRawTimeBase( int aOffset )
{
    int ret;

    if (aOffset < _numUomEntries)
    {
        ret=(int)_uom_entries[aOffset].time_base;
    }
    else
    {
        ret=-1;
    }

   return (ret);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable12::getResolvedTimeBase( int aOffset )
{
    string ret=string (ANSI_TIMEBASE_UNKNOWN);
    if (aOffset < _numUomEntries)
    {
        switch (getRawTimeBase(aOffset))
        {
            case timebase_dial_reading:
                ret=string (ANSI_TIMEBASE_DIAL_READING);
                break;
            case timebase_instantaneous:
                ret=string (ANSI_TIMEBASE_INSTANTANEOUS);
                break;
            case timebase_sub_block_average:
                ret=string (ANSI_TIMEBASE_SUB_BLOCK_AVERAGE);
                break;
            case timebase_block_average:
                ret=string (ANSI_TIMEBASE_BLOCK_AVERAGE);
                break;
            case timebase_relative_dial_reading:
                ret=string (ANSI_TIMEBASE_RELATIVE_DIAL_READING);
                break;
            case timebase_thermal:
                ret=string (ANSI_TIMEBASE_THERMAL);
                break;
            case timebase_event:
                ret=string (ANSI_TIMEBASE_EVENT);
                break;
            case timebase_unknown:
            default:
                ret=string (ANSI_TIMEBASE_UNKNOWN);
                break;
        }
    }
    return ret;
}
//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable12::getRawMultiplier( int aOffset )
{
    int ret;

    if (aOffset < _numUomEntries)
    {
        ret=(int)_uom_entries[aOffset].multiplier;
    }
    else
    {
        ret=-1;
    }

   return (ret);
}
//=========================================================================================================================================
//=========================================================================================================================================
DOUBLE CtiAnsiTable12::getResolvedMultiplier( int aOffset )
{
    DOUBLE ret=1;
    if (aOffset < _numUomEntries)
    {
        switch (getRawMultiplier(aOffset))
        {
            case multiplier_10_to_0:
                ret= pow(10.0,0);
                break;
            case multiplier_10_to_2:
                ret= pow(10.0,2);
                break;
            case multiplier_10_to_3:
                ret= pow(10.0,3);
                break;
            case multiplier_10_to_6:
                ret= pow(10.0,6);
                break;
            case multiplier_10_to_9:
                ret= pow(10.0,9);
                break;
            case multiplier_10_to_minus_2:
                ret= pow(10.0,-2);
                break;
            case multiplier_10_to_minus_3:
                ret= pow(10.0,-3);
                break;
            case multiplier_10_to_minus_6:
                ret= pow(10.0,-6);
                break;
            case multiplier_unknown:
            default:
                ret=1;
                break;
        }
    }
    return ret;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable12::printResult( const string& deviceName )
{
    Cti::FormattedTable table;
    table.setCell(0, 0) << "UOM Offset";
    table.setCell(0, 1) << "Id Code";
    table.setCell(0, 2) << "Time Base";
    table.setCell(0, 3) << "Multiplier";
    table.setCell(0, 4) << "Q's";
    table.setCell(0, 5) << "netFlow";
    table.setCell(0, 6) << "Seg";
    table.setCell(0, 7) << "Harm";
    table.setCell(0, 8) << "nfs";

    for (int x=0; x < _numUomEntries; x++)
    {
        const unsigned row = x+1;
        table.setCell(row, 0) << x;
        table.setCell(row, 1) << getResolvedIDCode(x)     <<" ("<< getRawIDCode(x)     <<")";
        table.setCell(row, 2) << getResolvedTimeBase(x)   <<" ("<< getRawTimeBase(x)   <<")";
        table.setCell(row, 3) << getResolvedMultiplier(x) <<" ("<< getRawMultiplier(x) <<")";

        std::string qs;
        for (int q = 1; q <= 4; q++)
        {
            if( getQuadrantAccountabilityFlag(q, x) )
            {
                qs += " Q" + boost::lexical_cast<string>(q);
            }
        }
        table.setCell(row, 4) << qs;
        table.setCell(row, 5) << (bool)_uom_entries[x].net_flow_accountablility;
        table.setCell(row, 6) <<       _uom_entries[x].segmentation;
        table.setCell(row, 7) << (bool)_uom_entries[x].harmonic;
        table.setCell(row, 8) <<"("<< _uom_entries[x].nfs <<")";
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 12") <<
            table
            );
}

bool  CtiAnsiTable12::getQuadrantAccountabilityFlag(int quadrant, int index)
{
    bool retVal = false;
    switch (quadrant)
    {
        case 1:
        {
            retVal = (bool) _uom_entries[index].q1_accountablility;
            break;
        }
        case 2:
        {
            retVal = (bool) _uom_entries[index].q2_accountablility;
            break;
        }
        case 3:
        {
            retVal = (bool) _uom_entries[index].q3_accountablility;
            break;
        }
        case 4:
        {
            retVal = (bool) _uom_entries[index].q4_accountablility;
            break;
        }
        default:
            break;
    }
    return retVal;
}

bool CtiAnsiTable12::getNetFlowAccountabilityFlag(int index)
{
    return (bool) _uom_entries[index].net_flow_accountablility;
}

int CtiAnsiTable12::getSegmentation(int index)
{
    return (int) _uom_entries[index].segmentation;
}
bool CtiAnsiTable12::getHarmonicFlag(int index)
{
    return (bool) _uom_entries[index].harmonic;
}



