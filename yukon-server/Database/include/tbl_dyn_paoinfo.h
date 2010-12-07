#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"
#include "database_connection.h"
#include "row_reader.h"

#include <string>
#include <map>

using std::map;
using std::string;

class IM_EX_CTIYUKONDB CtiTableDynamicPaoInfo : public CtiMemDBObject
{
public:

    enum PaoInfoKeys
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
        Key_MCT_ScheduledFreezeDay,
        Key_MCT_ScheduledFreezeConfigTimestamp,
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
        Key_MCT_DisconnectMinutes,
        Key_MCT_ConnectMinutes,
        Key_MCT_LLPChannel1Len,
        Key_MCT_LLPChannel2Len,
        Key_MCT_LLPChannel3Len,
        Key_MCT_LLPChannel4Len,
        Key_MCT_DisplayParameters,
        Key_MCT_TransformerRatio,
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
        Key_MCT_LoadProfileResolution,
        Key_MCT_RelayATimer,
        Key_MCT_RelayBTimer,
        Key_MCT_PrecannedTableReadInterval,
        Key_MCT_PrecannedMeterNumber,
        Key_MCT_PrecannedTableType,
        Key_MCT_DNP_RealTime1CRC,
        Key_MCT_DNP_RealTime2CRC,
        Key_MCT_DNP_BinaryCRC,
        Key_MCT_DNP_AnalogCRC1,
        Key_MCT_DNP_AnalogCRC2,
        Key_MCT_DNP_AnalogCRC3,
        Key_MCT_DNP_AnalogCRC4,
        Key_MCT_DNP_AnalogCRC5,
        Key_MCT_DNP_AccumulatorCRC1,
        Key_MCT_DNP_AccumulatorCRC2,
        Key_MCT_DNP_AccumulatorCRC3,
        Key_MCT_DNP_AccumulatorCRC4,
        Key_MCT_LcdMetric01,
        Key_MCT_LcdMetric02,
        Key_MCT_LcdMetric03,
        Key_MCT_LcdMetric04,
        Key_MCT_LcdMetric05,
        Key_MCT_LcdMetric06,
        Key_MCT_LcdMetric07,
        Key_MCT_LcdMetric08,
        Key_MCT_LcdMetric09,
        Key_MCT_LcdMetric10,
        Key_MCT_LcdMetric11,
        Key_MCT_LcdMetric12,
        Key_MCT_LcdMetric13,
        Key_MCT_LcdMetric14,
        Key_MCT_LcdMetric15,
        Key_MCT_LcdMetric16,
        Key_MCT_LcdMetric17,
        Key_MCT_LcdMetric18,
        Key_MCT_LcdMetric19,
        Key_MCT_LcdMetric20,
        Key_MCT_LcdMetric21,
        Key_MCT_LcdMetric22,
        Key_MCT_LcdMetric23,
        Key_MCT_LcdMetric24,
        Key_MCT_LcdMetric25,
        Key_MCT_LcdMetric26,

        //  run-time info
        Key_MCT_LLPInterest_Time,
        Key_MCT_LLPInterest_Channel,
        Key_MCT_LLPInterest_RequestBegin,
        Key_MCT_LLPInterest_RequestEnd,

        Key_FreezeCounter,
        Key_FreezeExpected,
        Key_VerificationSequence,
        Key_FrozenRateAPeakTimestamp,
        Key_FrozenRateBPeakTimestamp,
        Key_FrozenRateCPeakTimestamp,
        Key_FrozenRateDPeakTimestamp,
        Key_FrozenDemandPeakTimestamp,
        Key_FrozenDemand2PeakTimestamp,
        Key_FrozenDemand3PeakTimestamp,
        Key_DemandFreezeTimestamp,
        Key_VoltageFreezeTimestamp,

        Key_UDP_IP,
        Key_UDP_Port,
        Key_UDP_Sequence,

        Key_FMU_SoftSpecLSB,
        Key_FMU_SoftSpecMSB,
        Key_FMU_Revision,
        Key_FMU_DataTransferCommsType,
        Key_FMU_SerialNo,
        Key_FMU_ValidMessageType,
        Key_FMU_MonitorCommsType,
        Key_FMU_AttachedDeviceBaud,
        Key_FMU_AttachedDeviceProtocol,
        Key_FMU_AttachedDeviceRSSIAddress,
        Key_FMU_AttachedDeviceFlexCodeSet,
        Key_FMU_BluetoothSettings,
        Key_FMU_BluetoothPin,
        Key_FMU_DataLogBufferWrapped,
        Key_FMU_DataLogIndex,
        Key_FMU_DataLogMaxIndex,
        Key_FMU_DataLogEntries,
        Key_FMU_DataLogNumberUnreported,
        Key_FMU_DataLogStartIndex,
        Key_FMU_DataLogLastReportedIndex,
        Key_FMU_DataTransferPortSelectCommConfig,
        Key_FMU_MonitorPortSelectCommConfig,

        Key_LCR_SSpec,
        Key_LCR_SSpecRevision,
        Key_LCR_SerialAddress,
        Key_LCR_Spid,
        Key_LCR_GeoAddress,
        Key_LCR_Substation,
        Key_LCR_Feeder,
        Key_LCR_ZipCode,
        Key_LCR_Uda,
        Key_LCR_ProgramAddressRelay1,
        Key_LCR_ProgramAddressRelay2,
        Key_LCR_ProgramAddressRelay3,
        Key_LCR_ProgramAddressRelay4,
        Key_LCR_SplinterAddressRelay1,
        Key_LCR_SplinterAddressRelay2,
        Key_LCR_SplinterAddressRelay3,
        Key_LCR_SplinterAddressRelay4,

        Key_RPT_SSpec,
        Key_RPT_SSpecRevision,
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
    static const string _key_frozen_demand2_peak_timestamp;
    static const string _key_frozen_demand3_peak_timestamp;
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

    static const string _key_mct_scheduled_freeze_day;
    static const string _key_mct_scheduled_freeze_config_timestamp;

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
    static const string _key_mct_disconnect_minutes;
    static const string _key_mct_connect_minutes;

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

    static const string _key_mct_display_parameters;
    static const string _key_mct_transformer_ratio;

    static const string _key_mct_precanned_table_read_interval;
    static const string _key_mct_precanned_meter_number;
    static const string _key_mct_precanned_table_type;

    static const string _key_mct_llp_interest_time;
    static const string _key_mct_llp_interest_channel;
    static const string _key_mct_llp_interest_request_begin;
    static const string _key_mct_llp_interest_request_end;

    static const string _key_mct_dnp_realtime1_crc;
    static const string _key_mct_dnp_realtime2_crc;
    static const string _key_mct_dnp_binary_crc;
    static const string _key_mct_dnp_analog_crc1;
    static const string _key_mct_dnp_analog_crc2;
    static const string _key_mct_dnp_analog_crc3;
    static const string _key_mct_dnp_analog_crc4;
    static const string _key_mct_dnp_analog_crc5;
    static const string _key_mct_dnp_accumulator_crc1;
    static const string _key_mct_dnp_accumulator_crc2;
    static const string _key_mct_dnp_accumulator_crc3;
    static const string _key_mct_dnp_accumulator_crc4;

    static const string _key_mct_lcd_metric_01;
    static const string _key_mct_lcd_metric_02;
    static const string _key_mct_lcd_metric_03;
    static const string _key_mct_lcd_metric_04;
    static const string _key_mct_lcd_metric_05;
    static const string _key_mct_lcd_metric_06;
    static const string _key_mct_lcd_metric_07;
    static const string _key_mct_lcd_metric_08;
    static const string _key_mct_lcd_metric_09;
    static const string _key_mct_lcd_metric_10;
    static const string _key_mct_lcd_metric_11;
    static const string _key_mct_lcd_metric_12;
    static const string _key_mct_lcd_metric_13;
    static const string _key_mct_lcd_metric_14;
    static const string _key_mct_lcd_metric_15;
    static const string _key_mct_lcd_metric_16;
    static const string _key_mct_lcd_metric_17;
    static const string _key_mct_lcd_metric_18;
    static const string _key_mct_lcd_metric_19;
    static const string _key_mct_lcd_metric_20;
    static const string _key_mct_lcd_metric_21;
    static const string _key_mct_lcd_metric_22;
    static const string _key_mct_lcd_metric_23;
    static const string _key_mct_lcd_metric_24;
    static const string _key_mct_lcd_metric_25;
    static const string _key_mct_lcd_metric_26;

    static const string _key_udp_ip;
    static const string _key_udp_port;
    static const string _key_udp_sequence;

    static const string _key_fmu_softspec_lsb;
    static const string _key_fmu_softspec_msb;
    static const string _key_fmu_revision;
    static const string _key_fmu_datatransfer_comms_type;
    static const string _key_fmu_serial_no;
    static const string _key_fmu_valid_message_type;
    static const string _key_fmu_monitor_comms_type;
    static const string _key_fmu_attached_device_baud;
    static const string _key_fmu_attached_device_protocol;
    static const string _key_fmu_attached_device_rssi_address;
    static const string _key_fmu_attached_device_flex_code_set;
    static const string _key_fmu_bluetooth_settings;
    static const string _key_fmu_bluetooth_pin;
    static const string _key_fmu_datalog_buffer_wrapped;
    static const string _key_fmu_datalog_index;
    static const string _key_fmu_datalog_max_index;
    static const string _key_fmu_datalog_entries;
    static const string _key_fmu_datalog_number_unreported;
    static const string _key_fmu_datalog_start_index;
    static const string _key_fmu_datalog_last_reported_index;
    static const string _key_fmu_datatransfer_port_select_comm_config;
    static const string _key_fmu_monitor_port_select_comm_config;

    static const string _key_lcr_sspec;
    static const string _key_lcr_sspec_revision;
    static const string _key_lcr_sspec_serial;
    static const string _key_lcr_sspec_spid;
    static const string _key_lcr_sspec_geo_address;
    static const string _key_lcr_relay1_program_address;
    static const string _key_lcr_relay2_program_address;
    static const string _key_lcr_relay3_program_address;
    static const string _key_lcr_relay4_program_address;
    static const string _key_lcr_relay1_splinter_address;
    static const string _key_lcr_relay2_splinter_address;
    static const string _key_lcr_relay3_splinter_address;
    static const string _key_lcr_relay4_splinter_address;
    static const string _key_lcr_substation;
    static const string _key_lcr_feeder;
    static const string _key_lcr_zipcode;
    static const string _key_lcr_uda;

    static const string _key_rpt_sspec;
    static const string _key_rpt_sspec_revision;


    typedef map<CtiApplication_t, const string *> owner_map_t;
    typedef map<PaoInfoKeys,             const string *> key_map_t;

    static const owner_map_t _owner_map;
    static const key_map_t   _key_map;

    static owner_map_t init_owner_map();
    static key_map_t   init_key_map();

    long             _entry_id;
    long             _pao_id;
    CtiApplication_t _owner_id;

    PaoInfoKeys   _key;
    string _value;

    static const string _empty_string;

public:

    typedef CtiMemDBObject Inherited;

    CtiTableDynamicPaoInfo();
    CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo &aRef);
    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k);  //  owner doesn't matter until the new row gets written to the DB

    virtual ~CtiTableDynamicPaoInfo();

    CtiTableDynamicPaoInfo& operator=(const CtiTableDynamicPaoInfo &aRef);
    bool                    operator<(const CtiTableDynamicPaoInfo &rhs) const;  //  this is for the set in dev_base

    static string getTableName();

    bool hasRow() const;

    bool Insert(Cti::Database::DatabaseConnection &conn);
    bool Update(Cti::Database::DatabaseConnection &conn, long &rowsAffected);

    static string getSQLCoreStatement(CtiApplication_t _app_id);

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    long             getPaoID()       const;
    long             getEntryID()     const;
    CtiApplication_t getOwnerID()     const;
    string           getOwnerString() const;
    PaoInfoKeys      getKey()         const;
    string           getKeyString()   const;
    string           getValue()       const;

    void             getValue(int           &destination) const;
    void             getValue(long          &destination) const;
    void             getValue(unsigned long &destination) const;
    void             getValue(double        &destination) const;
    void             getValue(string        &destination) const;

    CtiTableDynamicPaoInfo &setPaoID(long pao_id);
    CtiTableDynamicPaoInfo &setEntryID(long entry_id);
    CtiTableDynamicPaoInfo &setOwner(CtiApplication_t o);
    CtiTableDynamicPaoInfo &setKey(PaoInfoKeys k);

    //  we actually want to limit the input conversions into setValue, because we
    //    need to be able to convert them from string form in the getValue functions
    CtiTableDynamicPaoInfo &setValue(int i);
    CtiTableDynamicPaoInfo &setValue(unsigned int i);
    CtiTableDynamicPaoInfo &setValue(long l);
    CtiTableDynamicPaoInfo &setValue(unsigned long l);
    CtiTableDynamicPaoInfo &setValue(double d);
    CtiTableDynamicPaoInfo &setValue(const string &s);

    //CtiTableDynamicPaoInfo &setDirty(bool dirty);

    virtual void dump();
};

