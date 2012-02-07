/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_11
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_11.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_one.cpp,v $
      Revision 1.8  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.7  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.6  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_11.h"

using std::endl;
using std::string;

const CHAR * CtiAnsiTable11::SELECTOR_CONSTANTS_GAS_AGA3 = "GAS_CONSTANTS_AGA3";
const CHAR * CtiAnsiTable11::SELECTOR_CONSTANTS_GAS_AGA7 = "GAS_CONSTANTS_AGA7";
const CHAR * CtiAnsiTable11::SELECTOR_CONSTANTS_ELECTRIC = "ELECTRIC_CONSTANTS";
const CHAR * CtiAnsiTable11::SELECTOR_CONSTANTS_RESERVED = "CONSTANT_RESERVED_FOR_FUTURE_USE";

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable11::CtiAnsiTable11( BYTE *dataBlob )
{
   _source_record = new SOURCE_RCD;

   if( _source_record != NULL )
      dataBlob += toAnsiIntParser(dataBlob, _source_record, sizeof( SOURCE_RCD ));
}



//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable11::~CtiAnsiTable11()
{
    if (_source_record != NULL)
    {
        delete _source_record;
        _source_record = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable11& CtiAnsiTable11::operator=(const CtiAnsiTable11& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable11::printResult( const string& deviceName )
{
    int integer;
    string string1,string2;
    bool flag;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    flag = getRawPFExcludeFlag();
    string1 = getResolvedPFExcludeFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=================== "<<deviceName<<"  Std Table 11 ========================" << endl;
        dout << "   Power Fail Exclude Flag: " << string1 << " (" << flag <<")" << endl;
    }

    flag = getRawResetExcludeFlag();
    string1 = getResolvedResetExcludeFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Reset Exclude Flag: " << string1 << " (" << flag <<")" << endl;
    }

    flag = getRawBlockDemandFlag();
    string1 = getResolvedBlockDemandFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Block Demand Flag: " << string1 << " (" << flag <<")" << endl;
    }

    flag = getRawSlidingDemandFlag();
    string1 = getResolvedSlidingDemandFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Sliding Demand Flag: " << string1 << " (" << flag <<")" << endl;
    }

    flag = getRawThermalDemandFlag();
    string1 = getResolvedThermalDemandFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Thermal Demand Flag: " << string1 << " (" << flag <<")" << endl;
    }

    flag = getRawSetOnePresentFlag();
    string1 = getResolvedSetOnePresentFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Set1 Present Flag: " << string1 << " (" << flag <<")" << endl;
    }

    flag = getRawSetTwoPresentFlag();
    string1 = getResolvedSetTwoPresentFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Set2 Present Flag: " << string1 << " (" << flag <<")" << endl;
    }

    flag = getRawNoOffsetFlag();
    string1 = getResolvedNoOffsetFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   No Offset Flag: " << string1 << " (" << flag <<")" << endl;
    }

    integer = getNumberUOMEntries();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Number of UOM Entries: " << integer << endl;
    }

    integer = getNumberDemandControlEntries();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Number of Demand Control Entries: " << integer << endl;
    }

    integer =  getDataControlLength() ;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Data Control Length: " << integer << endl;
    }
    integer =  getNumberDataControlEntries();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Number of Data Control Entries: " << integer << endl;
    }

    integer = getNumberConstantsEntries();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Number of Constants Entries: " << integer << endl;
    }
    string1 = getResolvedConstantsSelector();
    integer = getRawConstantsSelector();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Constants Selectors: " << string1 << " (" << integer <<")" << endl;
    }

    integer = getNumberSources();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Number of Sources: " << integer << endl;
    }
}

//=========================================================================================================================================
//this particular entry appears to exist in all the end devices... some of the entries in this table tell you if they exist in the end
//device
//=========================================================================================================================================

int CtiAnsiTable11::getNumberUOMEntries( void )
{
   return ((int)_source_record->nbr_uom_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable11::getNumberDemandControlEntries( void )
{
   return ((int)_source_record->nbr_demand_ctrl_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable11::getDataControlLength( void )
{
   return ((int)_source_record->data_ctrl_length);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable11::getNumberDataControlEntries( void )
{
   return ((int)_source_record->nbr_data_ctrl_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable11::getNumberConstantsEntries( void )
{
   return ((int)_source_record->nbr_constants_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable11::getNumberSources( void )
{
   return ((int)_source_record->nbr_sources);
}


//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable11::getRawConstantsSelector( void )
{
   return ((int)_source_record->constants_selector);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedConstantsSelector( void )
{
    string ret;
    if ((int)_source_record->constants_selector == gasConstantsAGA3)
        ret = string (SELECTOR_CONSTANTS_GAS_AGA3);
    else if ((int)_source_record->constants_selector == gasConstantsAGA7)
        ret = string (SELECTOR_CONSTANTS_GAS_AGA7);
    else if ((int)_source_record->constants_selector == electricConstants)
        ret = string (SELECTOR_CONSTANTS_ELECTRIC);
    else
        ret= string (SELECTOR_CONSTANTS_RESERVED);
    return ret;
}
//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawNoOffsetFlag( void )
{
   return ((bool)_source_record->source_flags.no_offset_flag);
}

//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedNoOffsetFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.no_offset_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawSetOnePresentFlag( void )
{
   return ((bool)_source_record->source_flags.set1_preset_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedSetOnePresentFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.set1_preset_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawSetTwoPresentFlag( void )
{
   return ((bool)_source_record->source_flags.set2_preset_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedSetTwoPresentFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.set2_preset_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}


//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawPFExcludeFlag( void )
{
   return ((bool)_source_record->source_flags.pf_exclude_flag);
}

//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedPFExcludeFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.pf_exclude_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawResetExcludeFlag( void )
{
   return ((bool)_source_record->source_flags.reset_exclude_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedResetExcludeFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.reset_exclude_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawBlockDemandFlag( void )
{
   return ((bool)_source_record->source_flags.block_demand_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedBlockDemandFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.block_demand_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}
//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawSlidingDemandFlag( void )
{
   return ((bool)_source_record->source_flags.sliding_demand_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedSlidingDemandFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.sliding_demand_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}
//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable11::getRawThermalDemandFlag( void )
{
   return ((bool)_source_record->source_flags.thermal_demand_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable11::getResolvedThermalDemandFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.thermal_demand_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}




