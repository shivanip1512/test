#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_one
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_one_one.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:19:57 $
*    History: 
      $Log: std_ansi_tbl_one_one.cpp,v $
      Revision 1.7  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

<<<<<<< std_ansi_tbl_one_one.cpp
      Revision 1.5.2.2  2005/07/14 22:27:02  jliu
      RWCStringRemoved

      Revision 1.5.2.1  2005/07/12 21:08:42  jliu
      rpStringWithoutCmpParser

=======
      Revision 1.6  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5.4.1  2005/12/12 19:50:39  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

>>>>>>> 1.6
      Revision 1.5  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_one_one.h"

using std::endl;

const CHAR * CtiAnsiTableOneOne::SELECTOR_CONSTANTS_GAS_AGA3 = "GAS_CONSTANTS_AGA3";
const CHAR * CtiAnsiTableOneOne::SELECTOR_CONSTANTS_GAS_AGA7 = "GAS_CONSTANTS_AGA7";
const CHAR * CtiAnsiTableOneOne::SELECTOR_CONSTANTS_ELECTRIC = "ELECTRIC_CONSTANTS";
const CHAR * CtiAnsiTableOneOne::SELECTOR_CONSTANTS_RESERVED = "CONSTANT_RESERVED_FOR_FUTURE_USE";

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneOne::CtiAnsiTableOneOne( BYTE *dataBlob )
{
   int temp = sizeof( SOURCE_RCD );

   _source_record = new SOURCE_RCD;

   if( _source_record != NULL )
      memcpy( _source_record, dataBlob, sizeof( SOURCE_RCD ));
}


CtiAnsiTableOneOne::CtiAnsiTableOneOne( )
{
    _source_record = new SOURCE_RCD;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneOne::~CtiAnsiTableOneOne()
{
    if (_source_record != NULL)
    {
        delete _source_record;
        _source_record = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneOne& CtiAnsiTableOneOne::operator=(const CtiAnsiTableOneOne& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


void CtiAnsiTableOneOne::generateResultPiece( BYTE **dataBlob )
{
    memcpy(*dataBlob, _source_record, sizeof( SOURCE_RCD ));
    *dataBlob += sizeof (SOURCE_RCD);
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneOne::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( _source_record, *dataBlob, sizeof( SOURCE_RCD ));
    *dataBlob += sizeof (SOURCE_RCD);
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneOne::printResult( const string& deviceName )
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

int CtiAnsiTableOneOne::getNumberUOMEntries( void )
{
   return ((int)_source_record->nbr_uom_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableOneOne::getNumberDemandControlEntries( void )
{
   return ((int)_source_record->nbr_demand_ctrl_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableOneOne::getDataControlLength( void )
{
   return ((int)_source_record->data_ctrl_length);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableOneOne::getNumberDataControlEntries( void )
{
   return ((int)_source_record->nbr_data_ctrl_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableOneOne::getNumberConstantsEntries( void )
{
   return ((int)_source_record->nbr_constants_entries);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableOneOne::getNumberSources( void )
{
   return ((int)_source_record->nbr_sources);
}


//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableOneOne::getRawConstantsSelector( void )
{
   return ((int)_source_record->constants_selector);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedConstantsSelector( void )
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

bool CtiAnsiTableOneOne::getRawNoOffsetFlag( void )
{
   return ((bool)_source_record->source_flags.no_offset_flag);
}

//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedNoOffsetFlag( void )
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

bool CtiAnsiTableOneOne::getRawSetOnePresentFlag( void )
{
   return ((bool)_source_record->source_flags.set1_preset_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedSetOnePresentFlag( void )
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

bool CtiAnsiTableOneOne::getRawSetTwoPresentFlag( void )
{
   return ((bool)_source_record->source_flags.set2_preset_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedSetTwoPresentFlag( void )
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

bool CtiAnsiTableOneOne::getRawPFExcludeFlag( void )
{
   return ((bool)_source_record->source_flags.pf_exclude_flag);
}

//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedPFExcludeFlag( void )
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

bool CtiAnsiTableOneOne::getRawResetExcludeFlag( void )
{
   return ((bool)_source_record->source_flags.reset_exclude_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedResetExcludeFlag( void )
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

bool CtiAnsiTableOneOne::getRawBlockDemandFlag( void )
{
   return ((bool)_source_record->source_flags.block_demand_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedBlockDemandFlag( void )
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

bool CtiAnsiTableOneOne::getRawSlidingDemandFlag( void )
{
   return ((bool)_source_record->source_flags.sliding_demand_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedSlidingDemandFlag( void )
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

bool CtiAnsiTableOneOne::getRawThermalDemandFlag( void )
{
   return ((bool)_source_record->source_flags.thermal_demand_flag);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTableOneOne::getResolvedThermalDemandFlag( void )
{
    string ret;
    if ((bool)_source_record->source_flags.thermal_demand_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}




