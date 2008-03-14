/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_paoinfo
*
* Date:   2005-jan-17
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.27 $
* DATE         :  $Date: 2008/03/14 23:32:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "rwutil.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_paoinfo.h"

using namespace std;


const string CtiTableDynamicPaoInfo::_empty_string = "(empty)";

//  !!!  these strings MUST NOT CHANGE - this is what the DB keys on  !!!
const string CtiTableDynamicPaoInfo::_owner_dispatch       = "dispatch";
const string CtiTableDynamicPaoInfo::_owner_porter         = "porter";
const string CtiTableDynamicPaoInfo::_owner_scanner        = "scanner";
const string CtiTableDynamicPaoInfo::_owner_capcontrol     = "capacitor control";
const string CtiTableDynamicPaoInfo::_owner_loadmanagement = "load management";
const string CtiTableDynamicPaoInfo::_owner_calc           = "calc and logic";

//  !!!  these strings MUST NOT CHANGE - this is what the DB keys on  !!!
const string CtiTableDynamicPaoInfo::_key_mct_sspec                     = "mct sspec";
const string CtiTableDynamicPaoInfo::_key_mct_sspec_revision            = "mct sspec revision";
const string CtiTableDynamicPaoInfo::_key_mct_loadprofile_config        = "mct load profile config";
const string CtiTableDynamicPaoInfo::_key_mct_loadprofile_interval      = "mct load profile interval";
const string CtiTableDynamicPaoInfo::_key_mct_loadprofile_interval2     = "mct load profile interval 2";
const string CtiTableDynamicPaoInfo::_key_mct_ied_loadprofile_interval  = "mct ied load profile rate";

const string CtiTableDynamicPaoInfo::_key_freeze_counter                = "freeze counter";
const string CtiTableDynamicPaoInfo::_key_expected_freeze               = "expected freeze";
const string CtiTableDynamicPaoInfo::_key_verification_sequence         = "verification sequence";

const string CtiTableDynamicPaoInfo::_key_mct_time_adjust_tolerance     = "mct time adjust tolerance";
const string CtiTableDynamicPaoInfo::_key_frozen_demand_peak_timestamp  = "frozen demand peak timestamp";
const string CtiTableDynamicPaoInfo::_key_frozen_demand2_peak_timestamp = "frozen channel 2 demand peak timestamp";
const string CtiTableDynamicPaoInfo::_key_frozen_demand3_peak_timestamp = "frozen channel 3 demand peak timestamp";
const string CtiTableDynamicPaoInfo::_key_frozen_rate_a_peak_timestamp  = "frozen rate a peak timestamp";
const string CtiTableDynamicPaoInfo::_key_frozen_rate_b_peak_timestamp  = "frozen rate b peak timestamp";
const string CtiTableDynamicPaoInfo::_key_frozen_rate_c_peak_timestamp  = "frozen rate c peak timestamp";
const string CtiTableDynamicPaoInfo::_key_frozen_rate_d_peak_timestamp  = "frozen rate d peak timestamp";
const string CtiTableDynamicPaoInfo::_key_demand_freeze_timestamp       = "demand freeze timestamp";
const string CtiTableDynamicPaoInfo::_key_voltage_freeze_timestamp      = "voltage freeze timestamp";

const string CtiTableDynamicPaoInfo::_key_mct_dst_start_time            = "mct dst start time";
const string CtiTableDynamicPaoInfo::_key_mct_dst_end_time              = "mct dst end time";
const string CtiTableDynamicPaoInfo::_key_mct_time_zone_offset          = "mct time zone offset";
const string CtiTableDynamicPaoInfo::_key_mct_over_voltage_threshold    = "mct over voltage threshold";
const string CtiTableDynamicPaoInfo::_key_mct_under_voltage_threshold   = "mct under voltage threshold";
const string CtiTableDynamicPaoInfo::_key_mct_demand_interval           = "mct demand interval";
const string CtiTableDynamicPaoInfo::_key_mct_voltage_profile_interval  = "mct voltage profile interval";
const string CtiTableDynamicPaoInfo::_key_mct_voltage_demand_interval   = "mct voltage demand interval";

const string CtiTableDynamicPaoInfo::_key_mct_day_table                 = "mct day table";
const string CtiTableDynamicPaoInfo::_key_mct_day_schedule_1            = "mct day schedule 1";
const string CtiTableDynamicPaoInfo::_key_mct_day_schedule_2            = "mct day schedule 2";
const string CtiTableDynamicPaoInfo::_key_mct_day_schedule_3            = "mct day schedule 3";
const string CtiTableDynamicPaoInfo::_key_mct_day_schedule_4            = "mct day schedule 4";
const string CtiTableDynamicPaoInfo::_key_mct_default_tou_rate          = "mct default tou rate";

const string CtiTableDynamicPaoInfo::_key_mct_bronze_address            = "mct bronze address";
const string CtiTableDynamicPaoInfo::_key_mct_lead_address              = "mct lead address";
const string CtiTableDynamicPaoInfo::_key_mct_collection_address        = "mct collection address";
const string CtiTableDynamicPaoInfo::_key_mct_service_provider_id       = "mct service provider id";

const string CtiTableDynamicPaoInfo::_key_mct_configuration             = "mct configuration";
const string CtiTableDynamicPaoInfo::_key_mct_options                   = "mct options";
const string CtiTableDynamicPaoInfo::_key_mct_outage_cycles             = "mct outage cycles";
const string CtiTableDynamicPaoInfo::_key_mct_event_flags_mask_1        = "mct event flags mask 1";
const string CtiTableDynamicPaoInfo::_key_mct_event_flags_mask_2        = "mct event flags mask 2";
const string CtiTableDynamicPaoInfo::_key_mct_meter_alarm_mask          = "mct meter alarm mask";
const string CtiTableDynamicPaoInfo::_key_mct_demand_limit              = "mct demand limit";
const string CtiTableDynamicPaoInfo::_key_mct_connect_delay             = "mct connect delay";
const string CtiTableDynamicPaoInfo::_key_mct_disconnect_minutes        = "mct disconnect minutes";
const string CtiTableDynamicPaoInfo::_key_mct_connect_minutes           = "mct connect minutes";

const string CtiTableDynamicPaoInfo::_key_mct_holiday_1                 = "mct holiday 1";
const string CtiTableDynamicPaoInfo::_key_mct_holiday_3                 = "mct holiday 3";
const string CtiTableDynamicPaoInfo::_key_mct_holiday_2                 = "mct holiday 2";

const string CtiTableDynamicPaoInfo::_key_mct_llp_channel1_len          = "mct llp channel 1 length";
const string CtiTableDynamicPaoInfo::_key_mct_llp_channel2_len          = "mct llp channel 2 length";
const string CtiTableDynamicPaoInfo::_key_mct_llp_channel3_len          = "mct llp channel 3 length";
const string CtiTableDynamicPaoInfo::_key_mct_llp_channel4_len          = "mct llp channel 4 length";

const string CtiTableDynamicPaoInfo::_key_mct_load_profile_channel_1_config = "mct load profile channel 1 config";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_channel_2_config = "mct load profile channel 2 config";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_channel_3_config = "mct load profile channel 3 config";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_channel_4_config = "mct load profile channel 4 config";

const string CtiTableDynamicPaoInfo::_key_mct_load_profile_meter_ratio_1    = "mct load profile meter ratio 1";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_meter_ratio_2    = "mct load profile meter ratio 2";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_meter_ratio_3    = "mct load profile meter ratio 3";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_meter_ratio_4    = "mct load profile meter ratio 4";

const string CtiTableDynamicPaoInfo::_key_mct_load_profile_k_ratio_1        = "mct load profile k ratio 1";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_k_ratio_2        = "mct load profile k ratio 2";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_k_ratio_3        = "mct load profile k ratio 3";
const string CtiTableDynamicPaoInfo::_key_mct_load_profile_k_ratio_4        = "mct load profile k ratio 4";

const string CtiTableDynamicPaoInfo::_key_mct_relay_a_timer                 = "mct relay a timer";
const string CtiTableDynamicPaoInfo::_key_mct_relay_b_timer                 = "mct relay b timer";

const string CtiTableDynamicPaoInfo::_key_mct_dnp_realtime1_crc             = "mct dnp realtime1 crc";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_realtime2_crc             = "mct dnp realtime2 crc";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_binary_crc                = "mct dnp binary crc";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_analog_crc1               = "mct dnp analog crc1";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_analog_crc2               = "mct dnp analog crc2";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_analog_crc3               = "mct dnp analog crc3";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_analog_crc4               = "mct dnp analog crc4";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_analog_crc5               = "mct dnp analog crc5";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_accumulator_crc1          = "mct dnp accumulator crc1";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_accumulator_crc2          = "mct dnp accumulator crc2";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_accumulator_crc3          = "mct dnp accumulator crc3";
const string CtiTableDynamicPaoInfo::_key_mct_dnp_accumulator_crc4          = "mct dnp accumulator crc4";

const string CtiTableDynamicPaoInfo::_key_mct_centron_parameters            = "mct centron parameters";
const string CtiTableDynamicPaoInfo::_key_mct_centron_ratio                 = "mct centron ratio";

const string CtiTableDynamicPaoInfo::_key_mct_precanned_table_read_interval = "mct precanned table read interval";
const string CtiTableDynamicPaoInfo::_key_mct_precanned_meter_number        = "mct precanned meter number";
const string CtiTableDynamicPaoInfo::_key_mct_precanned_table_type          = "mct precanned table type";

const string CtiTableDynamicPaoInfo::_key_mct_llp_interest_time             = "mct llp interest time";
const string CtiTableDynamicPaoInfo::_key_mct_llp_interest_channel          = "mct llp interest channel";
const string CtiTableDynamicPaoInfo::_key_mct_llp_interest_request_begin    = "mct llp interest request begin";
const string CtiTableDynamicPaoInfo::_key_mct_llp_interest_request_end      = "mct llp interest request end";

const string CtiTableDynamicPaoInfo::_key_udp_ip       = "udp ip";
const string CtiTableDynamicPaoInfo::_key_udp_port     = "udp port";
const string CtiTableDynamicPaoInfo::_key_udp_sequence = "udp sequence";

const CtiTableDynamicPaoInfo::owner_map_t CtiTableDynamicPaoInfo::_owner_map = CtiTableDynamicPaoInfo::init_owner_map();
const CtiTableDynamicPaoInfo::key_map_t   CtiTableDynamicPaoInfo::_key_map   = CtiTableDynamicPaoInfo::init_key_map();


CtiTableDynamicPaoInfo::owner_map_t CtiTableDynamicPaoInfo::init_owner_map()
{
    owner_map_t retval;

    retval.insert(make_pair(Application_Dispatch, &_owner_dispatch));
    retval.insert(make_pair(Application_Porter,   &_owner_porter));
    retval.insert(make_pair(Application_Scanner,  &_owner_scanner));

    return retval;
}


CtiTableDynamicPaoInfo::key_map_t CtiTableDynamicPaoInfo::init_key_map()
{
    key_map_t retval;

    retval.insert(make_pair(Key_MCT_SSpec,                  &_key_mct_sspec));
    retval.insert(make_pair(Key_MCT_SSpecRevision,          &_key_mct_sspec_revision));
    retval.insert(make_pair(Key_MCT_LoadProfileConfig,      &_key_mct_loadprofile_config));
    retval.insert(make_pair(Key_MCT_LoadProfileInterval,    &_key_mct_loadprofile_interval));
    retval.insert(make_pair(Key_MCT_LoadProfileInterval2,   &_key_mct_loadprofile_interval2));
    retval.insert(make_pair(Key_MCT_IEDLoadProfileInterval, &_key_mct_ied_loadprofile_interval));

    retval.insert(make_pair(Key_FreezeCounter,              &_key_freeze_counter));
    retval.insert(make_pair(Key_FreezeExpected,             &_key_expected_freeze));
    retval.insert(make_pair(Key_VerificationSequence,       &_key_verification_sequence));

    retval.insert(make_pair(Key_MCT_TimeAdjustTolerance,        &_key_mct_time_adjust_tolerance));
    retval.insert(make_pair(Key_MCT_DSTStartTime,               &_key_mct_dst_start_time));
    retval.insert(make_pair(Key_MCT_DSTEndTime,                 &_key_mct_dst_end_time));
    retval.insert(make_pair(Key_MCT_TimeZoneOffset,             &_key_mct_time_zone_offset));
    retval.insert(make_pair(Key_MCT_OverVoltageThreshold,       &_key_mct_over_voltage_threshold));
    retval.insert(make_pair(Key_MCT_UnderVoltageThreshold,      &_key_mct_under_voltage_threshold));
    retval.insert(make_pair(Key_MCT_DemandInterval,             &_key_mct_demand_interval));
    retval.insert(make_pair(Key_MCT_VoltageLPInterval,          &_key_mct_voltage_profile_interval));
    retval.insert(make_pair(Key_MCT_VoltageDemandInterval,      &_key_mct_voltage_demand_interval));
    retval.insert(make_pair(Key_MCT_DayTable,                   &_key_mct_day_table));
    retval.insert(make_pair(Key_MCT_DaySchedule1,               &_key_mct_day_schedule_1));
    retval.insert(make_pair(Key_MCT_DaySchedule2,               &_key_mct_day_schedule_2));
    retval.insert(make_pair(Key_MCT_DaySchedule3,               &_key_mct_day_schedule_3));
    retval.insert(make_pair(Key_MCT_DaySchedule4,               &_key_mct_day_schedule_4));
    retval.insert(make_pair(Key_MCT_DefaultTOURate,             &_key_mct_default_tou_rate));
    retval.insert(make_pair(Key_MCT_AddressBronze,              &_key_mct_bronze_address));
    retval.insert(make_pair(Key_MCT_AddressLead,                &_key_mct_lead_address));
    retval.insert(make_pair(Key_MCT_AddressCollection,          &_key_mct_collection_address));
    retval.insert(make_pair(Key_MCT_AddressServiceProviderID,   &_key_mct_service_provider_id));
    retval.insert(make_pair(Key_MCT_Configuration,              &_key_mct_configuration));
    retval.insert(make_pair(Key_MCT_Options,                    &_key_mct_options));
    retval.insert(make_pair(Key_MCT_EventFlagsMask1,            &_key_mct_event_flags_mask_1));
    retval.insert(make_pair(Key_MCT_EventFlagsMask2,            &_key_mct_event_flags_mask_2));
    retval.insert(make_pair(Key_MCT_MeterAlarmMask,             &_key_mct_meter_alarm_mask));
    retval.insert(make_pair(Key_MCT_OutageCycles,               &_key_mct_outage_cycles));
    retval.insert(make_pair(Key_MCT_DemandThreshold,            &_key_mct_demand_limit));
    retval.insert(make_pair(Key_MCT_ConnectDelay,               &_key_mct_connect_delay));
    retval.insert(make_pair(Key_MCT_DisconnectMinutes,          &_key_mct_disconnect_minutes));
    retval.insert(make_pair(Key_MCT_ConnectMinutes,             &_key_mct_connect_minutes));
    retval.insert(make_pair(Key_MCT_Holiday1,                   &_key_mct_holiday_1));
    retval.insert(make_pair(Key_MCT_Holiday2,                   &_key_mct_holiday_3));
    retval.insert(make_pair(Key_MCT_Holiday3,                   &_key_mct_holiday_2));
    retval.insert(make_pair(Key_MCT_LLPChannel1Len,             &_key_mct_llp_channel1_len));
    retval.insert(make_pair(Key_MCT_LLPChannel2Len,             &_key_mct_llp_channel2_len));
    retval.insert(make_pair(Key_MCT_LLPChannel3Len,             &_key_mct_llp_channel3_len));
    retval.insert(make_pair(Key_MCT_LLPChannel4Len,             &_key_mct_llp_channel4_len));

    retval.insert(make_pair(Key_MCT_LoadProfileChannelConfig1,  &_key_mct_load_profile_channel_1_config));
    retval.insert(make_pair(Key_MCT_LoadProfileChannelConfig2,  &_key_mct_load_profile_channel_2_config));
    retval.insert(make_pair(Key_MCT_LoadProfileChannelConfig3,  &_key_mct_load_profile_channel_3_config));
    retval.insert(make_pair(Key_MCT_LoadProfileChannelConfig4,  &_key_mct_load_profile_channel_4_config));
    retval.insert(make_pair(Key_MCT_LoadProfileMeterRatio1,     &_key_mct_load_profile_meter_ratio_1));
    retval.insert(make_pair(Key_MCT_LoadProfileMeterRatio2,     &_key_mct_load_profile_meter_ratio_2));
    retval.insert(make_pair(Key_MCT_LoadProfileMeterRatio3,     &_key_mct_load_profile_meter_ratio_3));
    retval.insert(make_pair(Key_MCT_LoadProfileMeterRatio4,     &_key_mct_load_profile_meter_ratio_4));
    retval.insert(make_pair(Key_MCT_LoadProfileKRatio1,         &_key_mct_load_profile_k_ratio_1));
    retval.insert(make_pair(Key_MCT_LoadProfileKRatio2,         &_key_mct_load_profile_k_ratio_2));
    retval.insert(make_pair(Key_MCT_LoadProfileKRatio3,         &_key_mct_load_profile_k_ratio_3));
    retval.insert(make_pair(Key_MCT_LoadProfileKRatio4,         &_key_mct_load_profile_k_ratio_4));

    retval.insert(make_pair(Key_MCT_RelayATimer,                &_key_mct_relay_a_timer));
    retval.insert(make_pair(Key_MCT_RelayBTimer,                &_key_mct_relay_b_timer));

    retval.insert(make_pair(Key_MCT_CentronParameters,          &_key_mct_centron_parameters));
    retval.insert(make_pair(Key_MCT_CentronRatio,               &_key_mct_centron_ratio));

    retval.insert(make_pair(Key_MCT_PrecannedTableReadInterval, &_key_mct_precanned_table_read_interval));
    retval.insert(make_pair(Key_MCT_PrecannedMeterNumber,       &_key_mct_precanned_meter_number));
    retval.insert(make_pair(Key_MCT_PrecannedTableType,         &_key_mct_precanned_table_type));

    retval.insert(make_pair(Key_MCT_LLPInterest_Time,           &_key_mct_llp_interest_time));
    retval.insert(make_pair(Key_MCT_LLPInterest_Channel,        &_key_mct_llp_interest_channel));
    retval.insert(make_pair(Key_MCT_LLPInterest_RequestBegin,   &_key_mct_llp_interest_request_begin));
    retval.insert(make_pair(Key_MCT_LLPInterest_RequestEnd,     &_key_mct_llp_interest_request_end));

    retval.insert(make_pair(Key_MCT_DNP_AccumulatorCRC1,        &_key_mct_dnp_accumulator_crc1));
    retval.insert(make_pair(Key_MCT_DNP_AccumulatorCRC2,        &_key_mct_dnp_accumulator_crc2));
    retval.insert(make_pair(Key_MCT_DNP_AccumulatorCRC3,        &_key_mct_dnp_accumulator_crc3));
    retval.insert(make_pair(Key_MCT_DNP_AccumulatorCRC4,        &_key_mct_dnp_accumulator_crc4));
    retval.insert(make_pair(Key_MCT_DNP_AnalogCRC1,             &_key_mct_dnp_analog_crc1));
    retval.insert(make_pair(Key_MCT_DNP_AnalogCRC2,             &_key_mct_dnp_analog_crc2));
    retval.insert(make_pair(Key_MCT_DNP_AnalogCRC3,             &_key_mct_dnp_analog_crc3));
    retval.insert(make_pair(Key_MCT_DNP_AnalogCRC4,             &_key_mct_dnp_analog_crc4));
    retval.insert(make_pair(Key_MCT_DNP_AnalogCRC5,             &_key_mct_dnp_analog_crc5));
    retval.insert(make_pair(Key_MCT_DNP_RealTime1CRC,           &_key_mct_dnp_realtime1_crc));
    retval.insert(make_pair(Key_MCT_DNP_RealTime2CRC,           &_key_mct_dnp_realtime2_crc));
    retval.insert(make_pair(Key_MCT_DNP_BinaryCRC,              &_key_mct_dnp_binary_crc));

    retval.insert(make_pair(Key_FrozenRateAPeakTimestamp,       &_key_frozen_rate_a_peak_timestamp));
    retval.insert(make_pair(Key_FrozenRateBPeakTimestamp,       &_key_frozen_rate_b_peak_timestamp));
    retval.insert(make_pair(Key_FrozenRateCPeakTimestamp,       &_key_frozen_rate_c_peak_timestamp));
    retval.insert(make_pair(Key_FrozenRateDPeakTimestamp,       &_key_frozen_rate_d_peak_timestamp));
    retval.insert(make_pair(Key_FrozenDemandPeakTimestamp,      &_key_frozen_demand_peak_timestamp));
    retval.insert(make_pair(Key_FrozenDemand2PeakTimestamp,     &_key_frozen_demand2_peak_timestamp));
    retval.insert(make_pair(Key_FrozenDemand3PeakTimestamp,     &_key_frozen_demand3_peak_timestamp));
    retval.insert(make_pair(Key_DemandFreezeTimestamp,          &_key_demand_freeze_timestamp));
    retval.insert(make_pair(Key_VoltageFreezeTimestamp,         &_key_voltage_freeze_timestamp));

    retval.insert(make_pair(Key_UDP_IP,       &_key_udp_ip));
    retval.insert(make_pair(Key_UDP_Port,     &_key_udp_port));
    retval.insert(make_pair(Key_UDP_Sequence, &_key_udp_sequence));

    return retval;
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo() :
    _entry_id(-1),
    _pao_id(-1),
    _owner_id(Application_Invalid),
    _key(Key_Invalid),
    _value("")
{
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo& aRef) :
    _entry_id(-1),
    _pao_id(-1),
    _owner_id(Application_Invalid),
    _key(Key_Invalid),
    _value("")
{
    *this = aRef;
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, Keys k) :
    _entry_id(-1),
    _pao_id(paoid),
    _owner_id(Application_Invalid),
    _key(k),
    _value("")
{
}


CtiTableDynamicPaoInfo::~CtiTableDynamicPaoInfo()
{
}


CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::operator=(const CtiTableDynamicPaoInfo& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _entry_id = aRef.getEntryID();
        _pao_id   = aRef.getPaoID();
        _owner_id = aRef.getOwner();
        _key      = aRef.getKey();
        _value    = aRef.getValue();
    }

    return *this;
}


bool CtiTableDynamicPaoInfo::operator<(const CtiTableDynamicPaoInfo &rhs) const
{
    //  there should not be more than one of these in any device's collection of table entries, so this is safe for a total ordering
    //    it makes set-based lookups possible, as well - i didn't want to use a map in the device
    return getKey() < rhs.getKey();
}


string CtiTableDynamicPaoInfo::getTableName()
{
    return string("DynamicPaoInfo");
}


bool CtiTableDynamicPaoInfo::hasRow() const
{
    return (_entry_id > 0);
}


RWDBStatus CtiTableDynamicPaoInfo::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}


RWDBStatus CtiTableDynamicPaoInfo::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    long rowsAffected = 0;

    return Update(conn, rowsAffected);
}

RWDBStatus CtiTableDynamicPaoInfo::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();
    RWDBStatus retval(RWDBStatus::ok);

    const string *tmp_owner = 0, *tmp_key = 0;
    string tmp_value;

    if( _owner_map.find(getOwner()) != _owner_map.end() )
    {
        tmp_owner = (_owner_map.find(getOwner()))->second;
    }

    if( _key_map.find(getKey()) != _key_map.end() )
    {
        tmp_key = (_key_map.find(getKey()))->second;
    }

    getValue(tmp_value);
    if( tmp_value.empty() )
    {
        tmp_value = _empty_string;
    }

    if( (getPaoID() >= 0) && tmp_owner && tmp_key )
    {
        inserter <<  getEntryID()  //  MUST be set before we try to insert
                 <<  getPaoID()
                 << *tmp_owner
                 << *tmp_key
                 <<  tmp_value
                 <<  CtiTime();

        if(DebugLevel & DEBUGLEVEL_LUDICROUS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << CtiTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl << endl;
        }

        ExecuteInserter(conn,inserter,__FILE__,__LINE__);

        if(inserter.status().errorCode() != RWDBStatus::ok)    // error occured!
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** SQL FAILED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << inserter.asString() << endl;
            }
        }
        else
        {
            resetDirty(FALSE);
        }

        retval = inserter.status();
    }
    else
    {
        if( !tmp_owner ) tmp_owner = &_empty_string;
        if( !tmp_key   ) tmp_key   = &_empty_string;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid attempt to insert into " << getTableName() << " - paoid = " << getPaoID() << ", tmp_owner = \"" << *tmp_owner << "\", and tmp_key = \"" << *tmp_key << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return retval;
}

RWDBStatus CtiTableDynamicPaoInfo::Update(RWDBConnection &conn, long &rowsAffected)
{
    RWDBStatus  stat(RWDBStatus::ok);
    RWDBTable   table = getDatabase().table( getTableName().c_str() );

    const string *tmp_owner = 0, *tmp_key = 0;
    string tmp_value;

    if( _owner_map.find(getOwner()) != _owner_map.end() )
    {
        tmp_owner = (_owner_map.find(getOwner()))->second;
    }

    if( _key_map.find(getKey()) != _key_map.end() )
    {
        tmp_key = (_key_map.find(getKey()))->second;
    }


    getValue(tmp_value);
    if( tmp_value.empty() )
    {
        tmp_value = _empty_string;
    }

    if( getEntryID() && tmp_owner && tmp_key )
    {
        RWDBUpdater updater = table.updater();

        updater.where(table["paobjectid"] == getPaoID() &&
                      table["owner"]      == tmp_owner->data() &&
                      table["infokey"]    == tmp_key->data());

        updater << table["value"].assign(tmp_value.c_str())
                << table["updatetime"].assign(toRWDBDT(CtiTime::now()));

        stat = ExecuteUpdater(conn, updater, __FILE__, __LINE__, &rowsAffected);

        if( stat.errorCode() == RWDBStatus::ok && rowsAffected > 0)
        {
            setDirty(false);
        }
        //  we'll be doing this in mgr_device, because we need to assign a new entryid on insert
/*        else
        {
            stat = Insert(conn);        // Try a vanilla insert if the update failed!
        }*/
    }

    return stat;
}

RWDBStatus CtiTableDynamicPaoInfo::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector << table["entryid"]
             << table["paobjectid"]
             << table["owner"]
             << table["infokey"]
             << table["value"];

    selector.where( table["entryid"] == getEntryID() );

    RWDBReader reader = selector.reader( conn );

    /*
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are dirty and need to be
     *  written into the database
     */
    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
    else
    {
        setDirty( TRUE );
    }

    return reader.status();
}

RWDBStatus CtiTableDynamicPaoInfo::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where(table["entryid"] == getEntryID());

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute(conn).status();
}

void CtiTableDynamicPaoInfo::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector, CtiApplication_t app_id)
{
    keyTable = db.table(CtiTableDynamicPaoInfo::getTableName().c_str());
    owner_map_t::const_iterator o_itr;

    selector << keyTable["entryid"]
             << keyTable["paobjectid"]
             << keyTable["owner"]
             << keyTable["infokey"]
             << keyTable["value"];

    selector.from(keyTable);

    o_itr = _owner_map.find(app_id);
    if( o_itr != _owner_map.end() && o_itr->second )
    {
        selector.where(keyTable["owner"] == o_itr->second->data());
    }
}

void CtiTableDynamicPaoInfo::DecodeDatabaseReader(RWDBReader& rdr)
{
    string tmp_owner, tmp_key, tmp_value;
    long tmp_entryid, tmp_paoid;

    key_map_t::const_iterator k_itr;
    owner_map_t::const_iterator o_itr;

    rdr["entryid"]    >> tmp_entryid;
    rdr["paobjectid"] >> tmp_paoid;
    rdr["owner"]      >> tmp_owner;
    rdr["infokey"]    >> tmp_key;
    rdr["value"]      >> tmp_value;

    setEntryID(tmp_entryid);
    setPaoID(tmp_paoid);

    o_itr = _owner_map.begin();
    while( o_itr != _owner_map.end() )
    {
        if( !tmp_owner.compare(*(o_itr->second)) )
        {
            setOwner(o_itr->first);
            o_itr = _owner_map.end();
        }
        else
        {
            o_itr++;
        }
    }

    k_itr = _key_map.begin();
    while( k_itr != _key_map.end() )
    {
        if( !tmp_key.compare(*(k_itr->second)) )
        {
            setKey(k_itr->first);
            k_itr = _key_map.end();
        }
        else
        {
            k_itr++;
        }
    }

    //  should we turn _empty_string into ""?
    setValue(tmp_value);

    //  make sure this happens at the end, so we reset the dirty bit AFTER all of those above calls set it dirty
    resetDirty();
}


long CtiTableDynamicPaoInfo::getEntryID() const
{
    return _entry_id;
}
long CtiTableDynamicPaoInfo::getPaoID() const
{
    return _pao_id;
}
CtiApplication_t CtiTableDynamicPaoInfo::getOwner() const
{
    return _owner_id;
}
CtiTableDynamicPaoInfo::Keys CtiTableDynamicPaoInfo::getKey() const
{
    return _key;
}


string CtiTableDynamicPaoInfo::getValue() const
{
    return _value;
}

//  these may need to become individually named get functions, if the assignment idiom doesn't work out
void CtiTableDynamicPaoInfo::getValue(string &destination) const
{
    destination = _value;
}
void CtiTableDynamicPaoInfo::getValue(int &destination) const
{
    destination = atoi(_value.c_str());
}

void CtiTableDynamicPaoInfo::getValue(long &destination) const
{
    destination = atol(_value.c_str());
}
void CtiTableDynamicPaoInfo::getValue(unsigned long &destination) const
{
    double tmp;
    getValue(tmp);

    if( tmp >= 0 )
    {
        destination = (unsigned long)tmp;
    }
    else
    {
        destination = 0UL;
    }
}
void CtiTableDynamicPaoInfo::getValue(double &destination) const
{
    destination = atof(_value.c_str());
}


CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setEntryID(long entry_id)
{
    _entry_id = entry_id;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setPaoID(long pao_id)
{
    _pao_id = pao_id;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setOwner(CtiApplication_t owner_id)
{
    _owner_id = owner_id;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setKey(Keys k)
{
    _key = k;

    setDirty();
    return *this;
}

CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(const string &s)
{
    //  maybe put in a null check, and assign "(empty)"
    _value = s;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(double d)
{
    _value = CtiNumStr(d);

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(int i)
{
    _value = CtiNumStr(i);

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(unsigned int i)
{
    _value = CtiNumStr(i);

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(long l)
{
    _value = CtiNumStr(l);

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(unsigned long ul)
{
    _value = CtiNumStr(ul);

    setDirty();
    return *this;
}


void CtiTableDynamicPaoInfo::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "getEntryID() " << getEntryID() << endl;
        dout << "getPaoID()   " << getPaoID() << endl;
        dout << "getOwner()   " << getOwner() << endl;
        dout << "getKey()     " << getKey() << endl;
        dout << "getValue()   " << getValue() << endl;
    }
}

