/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_pttag
*
* Class:  CtiTableDynamicPaoInfo
* Date:   12/22/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/09 16:41:58 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DYN_PAOINFO_H__
#define __TBL_DYN_PAOINFO_H__

#include <rw/db/db.h>
#include <rw/rwtime.h>

#include <string>
#include <map>
using namespace std;

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableDynamicPaoInfo : public CtiMemDBObject
{
public:

    enum Keys
    {
        //  this ordering can change without adverse effects - the strings are what the DB keys on
        Key_Invalid   =  -1,
        Key_MCT_SSpec = 100,
        Key_MCT_SSpecRevision,
        Key_MCT_LoadProfileConfig,
        Key_MCT_LoadProfileInterval,
        Key_MCT_LoadProfileInterval2,
        Key_MCT_IEDLoadProfileInterval,
        Key_MCT_TimeAdjustTolerance,
        Key_MCT_DSTStartTime,
        Key_MCT_DSTEndTime,
        Key_MCT_TimeZoneOffset,
        Key_MCT_UnderVoltageThreshold,
        Key_MCT_OverVoltageThreshold,
        Key_MCT_DemandInterval,
        Key_MCT_VoltageLPInterval,
        Key_MCT_VoltageDemandInterval,
        Key_MCT_DayTable,
        Key_MCT_DaySchedule1,
        Key_MCT_DaySchedule2,
        Key_MCT_DaySchedule3,
        Key_MCT_DaySchedule4,
        Key_MCT_DefaultTOURate,
        Key_MCT_AddressBronze,
        Key_MCT_AddressCollection,
        Key_MCT_AddressServiceProviderID,
        Key_MCT_AddressLead,
        Key_MCT_Configuration,
        Key_MCT_EventFlagsMask1,
        Key_MCT_EventFlagsMask2,
        Key_MCT_MeterAlarmMask,
        Key_MCT_Options,
        Key_MCT_OutageCycles,
        Key_MCT_DemandThreshold,
        Key_MCT_ConnectDelay,
        Key_MCT_LLPChannel1Len,
        Key_MCT_LLPChannel2Len,
        Key_MCT_LLPChannel3Len,
        Key_MCT_LLPChannel4Len,
        Key_MCT_Holiday1,
        Key_MCT_Holiday2,
        Key_MCT_Holiday3,
        Key_MCT_LoadProfileKRatio1,
        Key_MCT_LoadProfileKRatio2,
        Key_MCT_LoadProfileKRatio3,
        Key_MCT_LoadProfileKRatio4,
        Key_MCT_LoadProfileMeterRatio1,
        Key_MCT_LoadProfileMeterRatio2,
        Key_MCT_LoadProfileMeterRatio3,
        Key_MCT_LoadProfileMeterRatio4,
        Key_MCT_LoadProfileChannelConfig1,
        Key_MCT_LoadProfileChannelConfig2,
        Key_MCT_LoadProfileChannelConfig3,
        Key_MCT_LoadProfileChannelConfig4,
        Key_MCT_RelayATimer,
        Key_MCT_RelayBTimer,
        Key_MCT_PrecannedTableReadInterval,
        Key_MCT_PrecannedMeterNumber,
        Key_MCT_PrecannedTableType,

        Key_FreezeCounter,
        Key_ExpectedFreeze,
        Key_VerificationSequence,
        Key_FrozenRateAPeakTimestamp,
        Key_FrozenRateBPeakTimestamp,
        Key_FrozenRateCPeakTimestamp,
        Key_FrozenRateDPeakTimestamp,
        Key_FrozenDemandPeakTimestamp,
        Key_DemandFreezeTimestamp,
        Key_VoltageFreezeTimestamp,

        Key_UDP_IP,
        Key_UDP_Port,
        //  make sure to add any new enum values to the string map
    };

protected:

    static const string _owner_dispatch;
    static const string _owner_porter;
    static const string _owner_scanner;
    static const string _owner_capcontrol;
    static const string _owner_loadmanagement;
    static const string _owner_calc;

    static const string _key_mct_sspec;
    static const string _key_mct_sspec_revision;
    static const string _key_mct_loadprofile_config;
    static const string _key_mct_loadprofile_interval;
    static const string _key_mct_loadprofile_interval2;
    static const string _key_mct_ied_loadprofile_interval;
    static const string _key_freeze_counter;
    static const string _key_expected_freeze;
    static const string _key_verification_sequence;
    static const string _key_frozen_demand_peak_timestamp;
    static const string _key_frozen_rate_a_peak_timestamp;
    static const string _key_frozen_rate_b_peak_timestamp;
    static const string _key_frozen_rate_c_peak_timestamp;
    static const string _key_frozen_rate_d_peak_timestamp;
    static const string _key_demand_freeze_timestamp;
    static const string _key_voltage_freeze_timestamp;

    static const string _key_mct_time_adjust_tolerance;
    static const string _key_mct_dst_start_time;
    static const string _key_mct_dst_end_time;
    static const string _key_mct_time_zone_offset;
    static const string _key_mct_over_voltage_threshold;
    static const string _key_mct_under_voltage_threshold;
    static const string _key_mct_demand_interval;
    static const string _key_mct_voltage_profile_interval;
    static const string _key_mct_voltage_demand_interval;

    static const string _key_mct_day_table;
    static const string _key_mct_day_schedule_1;
    static const string _key_mct_day_schedule_2;
    static const string _key_mct_day_schedule_3;
    static const string _key_mct_day_schedule_4;
    static const string _key_mct_default_tou_rate;

    static const string _key_mct_bronze_address;
    static const string _key_mct_lead_address;
    static const string _key_mct_collection_address;
    static const string _key_mct_service_provider_id;

    static const string _key_mct_configuration;
    static const string _key_mct_options;
    static const string _key_mct_outage_cycles;
    static const string _key_mct_event_flags_mask_1;
    static const string _key_mct_event_flags_mask_2;
    static const string _key_mct_meter_alarm_mask;

    static const string _key_mct_demand_limit;
    static const string _key_mct_connect_delay;

    static const string _key_mct_llp_channel1_len;
    static const string _key_mct_llp_channel2_len;
    static const string _key_mct_llp_channel3_len;
    static const string _key_mct_llp_channel4_len;

    static const string _key_mct_holiday_1;
    static const string _key_mct_holiday_2;
    static const string _key_mct_holiday_3;

    static const string _key_mct_load_profile_channel_1_config;
    static const string _key_mct_load_profile_channel_2_config;
    static const string _key_mct_load_profile_channel_3_config;
    static const string _key_mct_load_profile_channel_4_config;

    static const string _key_mct_load_profile_meter_ratio_1;
    static const string _key_mct_load_profile_meter_ratio_2;
    static const string _key_mct_load_profile_meter_ratio_3;
    static const string _key_mct_load_profile_meter_ratio_4;

    static const string _key_mct_load_profile_k_ratio_1;
    static const string _key_mct_load_profile_k_ratio_2;
    static const string _key_mct_load_profile_k_ratio_3;
    static const string _key_mct_load_profile_k_ratio_4;

    static const string _key_mct_relay_a_timer;
    static const string _key_mct_relay_b_timer;

    static const string _key_mct_precanned_table_read_interval;
    static const string _key_mct_precanned_meter_number;
    static const string _key_mct_precanned_table_type;

    static const string _key_udp_ip;
    static const string _key_udp_port;

    typedef map<CtiApplication_t, const string *> owner_map_t;
    typedef map<Keys,             const string *> key_map_t;

    static const owner_map_t _owner_map;
    static const key_map_t   _key_map;

    static owner_map_t init_owner_map();
    static key_map_t   init_key_map();

    long             _entry_id;
    long             _pao_id;
    CtiApplication_t _owner_id;

    Keys   _key;
    string _value;

    static const string _empty_string;

private:

public:

    typedef CtiMemDBObject Inherited;

    CtiTableDynamicPaoInfo();
    CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo &aRef);
    CtiTableDynamicPaoInfo(long paoid, Keys k);  //  owner doesn't matter until the new row gets written to the DB

    virtual ~CtiTableDynamicPaoInfo();

    CtiTableDynamicPaoInfo& operator=(const CtiTableDynamicPaoInfo &aRef);
    bool                    operator<(const CtiTableDynamicPaoInfo &rhs) const;  //  this is for the set in dev_base

    static RWCString getTableName();

    bool hasRow() const;

    RWDBStatus Insert(RWDBConnection &conn);
    RWDBStatus Update(RWDBConnection &conn);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Delete();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector, CtiApplication_t app_id);
    void DecodeDatabaseReader(RWDBReader& rdr);

    long             getPaoID()   const;
    long             getEntryID() const;
    CtiApplication_t getOwner()   const;
    Keys             getKey()     const;
    string           getValue()   const;

    void             getValue(int           &destination) const;
    void             getValue(long          &destination) const;
    void             getValue(unsigned long &destination) const;
    void             getValue(double        &destination) const;
    void             getValue(string        &destination) const;

    CtiTableDynamicPaoInfo &setPaoID(long pao_id);
    CtiTableDynamicPaoInfo &setEntryID(long entry_id);
    CtiTableDynamicPaoInfo &setOwner(CtiApplication_t o);
    CtiTableDynamicPaoInfo &setKey(Keys k);

    //  we actually want to limit the input conversions into setValue, because we
    //    need to be able to convert them from string form in the getValue functions
    CtiTableDynamicPaoInfo &setValue(int i);
    CtiTableDynamicPaoInfo &setValue(long l);
    CtiTableDynamicPaoInfo &setValue(unsigned long l);
    CtiTableDynamicPaoInfo &setValue(double d);
    CtiTableDynamicPaoInfo &setValue(const string &s);

    //CtiTableDynamicPaoInfo &setDirty(bool dirty);

    virtual void dump();
};


#endif // #ifndef __TBL_DYN_PAOINFO_H__
