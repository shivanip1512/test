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
    Cti::FormattedList itemList;

    itemList.add("Power Fail Exclude Flag")          << getResolvedPFExcludeFlag()     <<" ("<< getRawPFExcludeFlag()     <<")";
    itemList.add("Reset Exclude Flag")               << getResolvedResetExcludeFlag()  <<" ("<< getRawResetExcludeFlag()  <<")";
    itemList.add("Block Demand Flag")                << getResolvedBlockDemandFlag()   <<" ("<< getRawBlockDemandFlag()   <<")";
    itemList.add("Sliding Demand Flag")              << getResolvedSlidingDemandFlag() <<" ("<< getRawSlidingDemandFlag() <<")";
    itemList.add("Thermal Demand Flag")              << getResolvedThermalDemandFlag() <<" ("<< getRawThermalDemandFlag() <<")";
    itemList.add("Set1 Present Flag")                << getResolvedSetOnePresentFlag() <<" ("<< getRawSetOnePresentFlag() <<")";
    itemList.add("Set2 Present Flag")                << getResolvedSetTwoPresentFlag() <<" ("<< getRawSetTwoPresentFlag() <<")";
    itemList.add("No Offset Flag")                   << getResolvedNoOffsetFlag()      <<" ("<< getRawNoOffsetFlag()      <<")";
    itemList.add("Number of UOM Entries")            << getNumberUOMEntries();
    itemList.add("Number of Demand Control Entries") << getNumberDemandControlEntries();
    itemList.add("Data Control Length")              << getDataControlLength();
    itemList.add("Number of Data Control Entries")   << getNumberDataControlEntries();
    itemList.add("Number of Constants Entries")      << getNumberConstantsEntries();
    itemList.add("Constants Selectors")              << getResolvedConstantsSelector() <<" ("<< getRawConstantsSelector() <<")";
    itemList.add("Number of Sources")                << getNumberSources();

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 11") <<
            itemList
            );
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




