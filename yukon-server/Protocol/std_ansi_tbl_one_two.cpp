#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_two
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_one_two.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/03/14 21:44:16 $
*    History: 
      $Log: std_ansi_tbl_one_two.cpp,v $
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

#include "logger.h"
#include "std_ansi_tbl_one_two.h"


const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_WATTS = "Watts";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_VARS = "Vars";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_VA = "VA";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_RMS_VOLTAGE = "RMS Voltage";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_RMS_VOLTAGE_SQUARED = "RMS Voltage Squared";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_INSTANTANEOUS_VOLTAGE = "Instantaneous Voltage";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_RMS_CURRENT = "RMS Current";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_RMS_CURRENT_SQUARED= "RMS Current Squared";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_INSTANTANEOUS_CURRENT = "Instantaneous Current";
const CHAR * CtiAnsiTableOneTwo::ANSI_UOM_NOT_SUPPORTED = "UOM Not Supported";

const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_DIAL_READING = "Dial reading (Energy)";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_INSTANTANEOUS = "Instantaneous";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_PERIOD_BASED = "Period based (Power,RMS)";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_SUB_BLOCK_AVERAGE = "Sub-block average (Demand)";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_BLOCK_AVERAGE = "Block average (Demand)";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_RELATIVE_DIAL_READING = "Relative dial reading (Energy)";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_THERMAL = "Thermal quantity (Demand)";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_EVENT = "Event quantity (# of occurrences)";
const CHAR * CtiAnsiTableOneTwo::ANSI_TIMEBASE_UNKNOWN = "Timebase not supported";

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneTwo::CtiAnsiTableOneTwo( int num_uom_entries )
{
   _numUomEntries = num_uom_entries;
   _uom_entries = new UOM_ENTRY_BFLD[num_uom_entries];
}

CtiAnsiTableOneTwo::CtiAnsiTableOneTwo( BYTE *dataBlob, int num_uom_entries )
{
   int index;

   _numUomEntries = num_uom_entries;
   _uom_entries = new UOM_ENTRY_BFLD[num_uom_entries];

   for( index = 0; index < num_uom_entries; index++ )
   {
      memcpy( (void *)&_uom_entries[index], dataBlob, sizeof( UOM_ENTRY_BFLD ));
      dataBlob += sizeof( UOM_ENTRY_BFLD );
   }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneTwo::~CtiAnsiTableOneTwo()
{
    if (_uom_entries != NULL)
    {
        delete []_uom_entries;
        _uom_entries = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneTwo& CtiAnsiTableOneTwo::operator=(const CtiAnsiTableOneTwo& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneTwo::generateResultPiece( BYTE **dataBlob )
{
    memcpy(*dataBlob, (void*)_uom_entries, sizeof( UOM_ENTRY_BFLD ) * _numUomEntries);
    //memcpy(*dataBlob, _uom_entries, sizeof( UOM_ENTRY_BFLD ) * _numUomEntries);
    *dataBlob += (sizeof( UOM_ENTRY_BFLD ) * _numUomEntries);

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneTwo::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( (void*)_uom_entries, *dataBlob, sizeof( UOM_ENTRY_BFLD ) * _numUomEntries);
    *dataBlob += (sizeof( UOM_ENTRY_BFLD ) * _numUomEntries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableOneTwo::getRawIDCode( int aOffset )
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
bool CtiAnsiTableOneTwo::isCorrectData( int aOffset, int aUOM)
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
RWCString CtiAnsiTableOneTwo::getResolvedIDCode( int aOffset )
{
    RWCString ret=RWCString (ANSI_UOM_NOT_SUPPORTED);
    if (aOffset < _numUomEntries)
    {
        switch (getRawIDCode(aOffset))
        {
            case uom_watts:
                ret=RWCString (ANSI_UOM_WATTS);
                break;
            case uom_vars:
                ret=RWCString (ANSI_UOM_VARS);
                break;
            case uom_va:
                ret=RWCString (ANSI_UOM_VA);
                break;
            case uom_rms_volts:
                ret=RWCString (ANSI_UOM_RMS_VOLTAGE);
                break;
            case uom_rms_volts_squared:
                ret=RWCString (ANSI_UOM_RMS_VOLTAGE_SQUARED);
                break;
            case uom_instantaneous_volts:
                ret=RWCString (ANSI_UOM_INSTANTANEOUS_VOLTAGE);
                break;
            case uom_rms_amps:
                ret=RWCString (ANSI_UOM_RMS_CURRENT);
                break;
            case uom_rms_amps_squared:
                ret=RWCString (ANSI_UOM_RMS_CURRENT_SQUARED);
                break;
            case uom_instantaneous_amps:
                ret=RWCString (ANSI_UOM_INSTANTANEOUS_CURRENT);
                break;
            default:
                ret=RWCString (ANSI_UOM_NOT_SUPPORTED);
                break;
        }
    }
    return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableOneTwo::getRawTimeBase( int aOffset )
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
RWCString CtiAnsiTableOneTwo::getResolvedTimeBase( int aOffset )
{
    RWCString ret=RWCString (ANSI_TIMEBASE_UNKNOWN);
    if (aOffset < _numUomEntries)
    {
        switch (getRawTimeBase(aOffset))
        {
            case timebase_dial_reading:
                ret=RWCString (ANSI_TIMEBASE_DIAL_READING);
                break;
            case timebase_instantaneous:
                ret=RWCString (ANSI_TIMEBASE_INSTANTANEOUS);
                break;
            case timebase_sub_block_average:
                ret=RWCString (ANSI_TIMEBASE_SUB_BLOCK_AVERAGE);
                break;
            case timebase_block_average:
                ret=RWCString (ANSI_TIMEBASE_BLOCK_AVERAGE);
                break;
            case timebase_relative_dial_reading:
                ret=RWCString (ANSI_TIMEBASE_RELATIVE_DIAL_READING);
                break;
            case timebase_thermal:
                ret=RWCString (ANSI_TIMEBASE_THERMAL);
                break;
            case timebase_event:
                ret=RWCString (ANSI_TIMEBASE_EVENT);
                break;
            case timebase_unknown:
            default:
                ret=RWCString (ANSI_TIMEBASE_UNKNOWN);
                break;
        }
    }
    return ret;
}
//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableOneTwo::getRawMultiplier( int aOffset )
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
DOUBLE CtiAnsiTableOneTwo::getResolvedMultiplier( int aOffset )
{
    DOUBLE ret=1;
    if (aOffset < _numUomEntries)
    {
        switch (getRawMultiplier(aOffset))
        {
            case multiplier_10_to_0:
                ret= pow(10,0);
                break;
            case multiplier_10_to_2:
                ret= pow(10,2);
                break;
            case multiplier_10_to_3:
                ret= pow(10,3);
                break;
            case multiplier_10_to_6:
                ret= pow(10,6);
                break;
            case multiplier_10_to_9:
                ret= pow(10,9);
                break;
            case multiplier_10_to_minus_2:
                ret= pow(10,-2);
                break;
            case multiplier_10_to_minus_3:
                ret= pow(10,-3);
                break;
            case multiplier_10_to_minus_6:
                ret= pow(10,-6);
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
void CtiAnsiTableOneTwo::printResult(  )
{
    int integer;
    RWCString string1,string2;
    double double1;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 12 ========================" << endl;
        dout << "UOM Offset  Id Code     Time Base           Multiplier  Q's netFlow Seg Harm  nfs" << endl;
    }

    for (int x=0;x < _numUomEntries; x++)
    {
        integer = getRawIDCode(x);
        string1 = getResolvedIDCode(x);
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " " << x << "   " << string1 << " (" << integer <<")  ";
        }

        integer = getRawTimeBase(x);
        string1 = getResolvedTimeBase(x);
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << string1 << " (" << integer <<")      ";
        }

        integer = getRawMultiplier(x);
        double1 = getResolvedMultiplier(x);
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << double1 << " (" << integer <<")" ;
        }
        for (int q = 1; q <= 4; q++) 
        {
            if (getQuadrantAccountabilityFlag(q, x)) 
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout <<" Q" <<q;
            }
        }
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout <<"  " << (bool)_uom_entries[x].net_flow_accountablility;
            dout <<"  " << (int)_uom_entries[x].segmentation;
            dout <<"  " << (bool)_uom_entries[x].harmonic;
            dout <<" (" << (int)_uom_entries[x].nfs <<")" << endl;
        }
        //Sleep(50);
    }
}

bool  CtiAnsiTableOneTwo::getQuadrantAccountabilityFlag(int quadrant, int index)
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

bool CtiAnsiTableOneTwo::getNetFlowAccountabilityFlag(int index)
{
    return (bool) _uom_entries[index].net_flow_accountablility;
}

int CtiAnsiTableOneTwo::getSegmentation(int index)
{
    return (int) _uom_entries[index].segmentation;
}
bool CtiAnsiTableOneTwo::getHarmonicFlag(int index)
{
    return (bool) _uom_entries[index].harmonic;
}



