#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_21.h"

using std::string;
using std::endl;
//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable21::CtiAnsiTable21( BYTE *dataBlob ) :
    _tier(0)
{
   dataBlob += toAnsiIntParser(dataBlob, &_reg_func1_flags, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_reg_func2_flags, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_nbr_self_reads, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_nbr_summations, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_nbr_demands, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_nbr_coin_values, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_occur, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_tiers, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_nbr_present_demands, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_nbr_present_values, sizeof( unsigned char ));
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable21::~CtiAnsiTable21()
{

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable21& CtiAnsiTable21::operator=(const CtiAnsiTable21& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}

//=========================================================================================================================================
//used in table 22
//=========================================================================================================================================

int CtiAnsiTable21::getNumberSummations( void )
{
   return _nbr_summations;
}

//=========================================================================================================================================
//used in table 22
//=========================================================================================================================================

int CtiAnsiTable21::getNumberDemands( void )
{
   return _nbr_demands;
}

//=========================================================================================================================================
//used in table 22
//=========================================================================================================================================

int CtiAnsiTable21::getCoinValues( void )
{
   return _nbr_coin_values;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable21::getOccur( void )
{
   return _occur;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable21::getTiers( void )
{
   return _tiers;
}

int CtiAnsiTable21::getNbrPresentDemands(void)
{
    return _nbr_present_demands;
}

int CtiAnsiTable21::getNbrPresentValues(void)
{
    return _nbr_present_values;
}

bool CtiAnsiTable21::getTimeRemainingFlag(void)
{
    if( _reg_func1_flags.time_remaining_flag  & 0x01 )
      return true;
   else
      return false;
}



//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable21::getDemandResetCtrFlag( void )
{
   if( _reg_func1_flags.demand_reset_ctr_flag & 0x01 )
      return true;
   else
      return false;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable21::getTimeDateFieldFlag( void )
{
   if( _reg_func1_flags.date_time_field_flag & 0x01 )
      return true;
   else
      return false;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable21::getSeasonInfoFieldFlag( void )
{
   if( _reg_func1_flags.season_info_field_flag & 0x01 )
      return true;
   else
      return false;
}


//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable21::getCumDemandFlag( void )
{
   if( _reg_func1_flags.cum_demand_flag & 0x01 )
      return true;
   else
      return false;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable21::getContCumDemandFlag( void )
{
   if( _reg_func1_flags.cont_cum_demand_flag & 0x01 )
      return true;
   else
      return false;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable21::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList << "** Reg_Func1_Flags **";
    itemList.add("Season_Info_Field_Flag") << (bool)_reg_func1_flags.season_info_field_flag;
    itemList.add("Date_Time_Field_Flag")   << (bool)_reg_func1_flags.date_time_field_flag;
    itemList.add("Demand_Reset_Ctr_Flag")  << (bool)_reg_func1_flags.demand_reset_ctr_flag;
    itemList.add("Demand_Reset_Lock_Flag") << (bool)_reg_func1_flags.demand_reset_lock_flag;
    itemList.add("Cum_Demand_Flag")        << (bool)_reg_func1_flags.cum_demand_flag;
    itemList.add("Cont_Cum_Demand_Flag")   << (bool)_reg_func1_flags.cont_cum_demand_flag;
    itemList.add("Time_Remaining_Flag")    << (bool)_reg_func1_flags.time_remaining_flag;

    itemList <<"** Reg_Func2_Flags **";
    itemList.add("Self_Read_Inhibit_Overflow_Flag") << (bool)_reg_func2_flags.self_read_inhibit_overflow_flag;
    itemList.add("Self_Read_Seq_Nbr_Flag")          << (bool)_reg_func2_flags.self_read_seq_nbr_flag;
    itemList.add("Daily_Self_Read_Flag")            << (bool)_reg_func2_flags.daily_self_read_flag;
    itemList.add("Weekly_Self_Read_Flag")           << (bool)_reg_func2_flags.weekly_self_read_flag;
    itemList.add("Self_Read_Demand_Reset")          <<       _reg_func2_flags.self_read_demand_reset;

    itemList <<"** Stats **";
    itemList.add("Nbr_Self_Reads")      << _nbr_self_reads;
    itemList.add("Nbr_Summations")      << _nbr_summations;
    itemList.add("Nbr_Demands")         << _nbr_demands;
    itemList.add("Nbr_Coin_Values")     << _nbr_coin_values;
    itemList.add("Nbr_Occur")           << _occur;
    itemList.add("Nbr_Tiers")           << _tiers;
    itemList.add("Nbr_Present_Demands") << _nbr_present_demands;
    itemList.add("Nbr_Present_Values")  << _nbr_present_values;

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 21") <<
            itemList
            );
}
