/*-----------------------------------------------------------------------------*
*
* File:   dev_mct4xx
*
* Class:  CtiDeviceMCT4xx
* Date:   10/5/2005
*
* Author: Jess M. Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct4xx.h-arc  $
* REVISION     :  $Revision: 1.28 $
* DATE         :  $Date: 2007/03/08 22:43:34 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT4XX_H__
#define __DEV_MCT4XX_H__
#pragma warning( disable : 4786)
#include "yukon.h"

#include "dev_mct.h"
#include <vector>
#include "config_base.h"
#include "config_device.h"
#include "config_parts.h"


class IM_EX_DEVDB CtiDeviceMCT4xx : public CtiDeviceMCT
{
protected:

    typedef vector<const char *> ConfigPartsList;

private:

    typedef CtiDeviceMCT Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    static const ConfigPartsList _config_parts;
    static ConfigPartsList initConfigParts();

    int executePutConfigSingle( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    void createTOUDayScheduleString(string &schedule, long (&times)[5], long (&rates)[6]);

protected:

    string printable_time(unsigned long seconds) const;
    string printable_date(unsigned long seconds) const;

    bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    enum ValueType
    {
        ValueType_Voltage,
        ValueType_Demand,
        ValueType_DynamicDemand,
        ValueType_TOUDemand,
        ValueType_TOUFrozenDemand,
        ValueType_LoadProfile_Voltage,
        ValueType_LoadProfile_Demand,
        ValueType_LoadProfile_DynamicDemand,
        ValueType_Accumulator,
        ValueType_AccumulatorDelta,
        ValueType_FrozenAccumulator,
        ValueType_IED,
        ValueType_Raw,
    };

    enum Errors
    {
        Error_MeterCommunicationProblem = 0xfffffffe,
        Error_NoDataYetAvailable        = 0xfffffffc,
        Error_IntervalOutsideValidRange = 0xfffffffa,
        Error_DeviceFiller              = 0xfffffff8,
        Error_PowerFailureThisInterval  = 0xfffffff6,
        Error_PowerRestoredThisInterval = 0xfffffff4,
        Error_Overflow                  = 0xffffffe0,
    };

    enum ErrorClasses
    {
        EC_MeterReading    = 1 << 0,
        EC_DemandReading   = 1 << 1,
        EC_TOUDemand       = 1 << 2,
        EC_TOUFrozenDemand = 1 << 3,
        EC_LoadProfile     = 1 << 4,
    };

    struct error_info
    {
        int error;
        int error_classes;
        PointQuality_t quality;
        string description;

        error_info(int e, string d = "", PointQuality_t q = NormalQuality, int ec = 0) :
            error(e),
            description(d),
            quality(q),
            error_classes(ec)
        {
        }

        operator<(const error_info &rhs) const
        {
            return error < rhs.error;
        }
    };

    typedef set<error_info> error_set;

    static const error_set _errorInfo;

    enum Commands
    {
        Command_TOUDisable   = 0x55,
        Command_TOUEnable    = 0x56,

        Command_TOUResetZero = 0x5e,
        Command_TOUReset     = 0x5f,
    };

    enum MemoryMap
    {
        Memory_OptionsPos         = 0x02,
        Memory_OptionsLen         =    1,

        Memory_ConfigurationPos   = 0x03,
        Memory_ConfigurationLen   =    1,

        Memory_TOUDayTablePos     = 0x50,
        Memory_TOUDayTableLen     =    2,

        Memory_TOUDailySched1Pos  = 0x52,
        Memory_TOUDailySched1Len  =    7,

        Memory_TOUDailySched2Pos  = 0x59,
        Memory_TOUDailySched2Len  =    7,

        Memory_TOUDailySched3Pos  = 0x60,
        Memory_TOUDailySched3Len  =    7,

        Memory_TOUDailySched4Pos  = 0x67,
        Memory_TOUDailySched4Len  =    7,

        Memory_TOUDefaultRatePos    = 0x6e,
        Memory_TOUDefaultRateLen    =    1,
    };

    enum Functions
    {
        FuncWrite_ConfigAlarmMaskPos = 0x01,

        FuncWrite_LLPStoragePos      = 0x04,
        FuncWrite_LLPStorageLen      =    5,

        FuncWrite_LLPInterestPos     = 0x05,
        FuncWrite_LLPInterestLen     =    6,

        FuncWrite_LLPPeakInterestPos = 0x06,
        FuncWrite_LLPPeakInterestLen =    7,

        FuncWrite_TOUSchedule1Pos    = 0x30,
        FuncWrite_TOUSchedule1Len    =   15,

        FuncWrite_TOUSchedule2Pos    = 0x31,
        FuncWrite_TOUSchedule2Len    =   15,

        FuncRead_MReadPos            = 0x90,

        FuncRead_DemandPos           = 0x92,

        FuncRead_PeakDemandPos       = 0x93,

        FuncRead_LLPStatusPos        = 0x9d,
        FuncRead_LLPStatusLen        =    8,

        FuncRead_LLPPeakDayPos       = 0xa0,
        FuncRead_LLPPeakHourPos      = 0xa1,
        FuncRead_LLPPeakIntervalPos  = 0xa2,
        FuncRead_LLPPeakLen          =   13,

        FuncRead_TOUBasePos          = 0xb0,
        FuncRead_TOULen              =    9,
        FuncRead_TOUFrozenOffset     =    4,

        FuncRead_TOUDaySchedulePos   = 0xad,
        FuncRead_TOUDayScheduleLen   =   11,

        FuncRead_TOUStatusPos           = 0xad,
        FuncRead_TOUStatusLen           =   11,
        FuncRead_TOUSwitchSchedule12Pos = 0xae,
        FuncRead_TOUSwitchSchedule12Len =   13,
        FuncRead_TOUSwitchSchedule34Pos = 0xaf,
        FuncRead_TOUSwitchSchedule34Len =   13,
    };

    enum
    {
        LPChannels       =    4,
        LPRecentBlocks   =   16,

        DawnOfTime       = 0x386d4380,  //  jan 1, 2000, in UTC seconds

        PointOffset_RateOffset  =  20,   //  gets added for rate B, C, D

        PointOffset_PeakOffset  =  10,

        PointOffset_TOUBase     = 100,  //  this is okay because TOU only has peak and frozen demand - it must start at 111 anyway
    };

    struct lp_info_t
    {
        unsigned long archived_reading;
        unsigned long current_request;
        unsigned long current_schedule;
    } _lp_info[LPChannels];

    struct llp_interest_t
    {
        unsigned long time;
        unsigned long time_end;

        unsigned offset;
        unsigned channel;

        long in_progress;

        bool retry;
        bool failed;
    } _llpInterest;

    struct llp_peak_report_interest_t
    {
        unsigned long time;
        int channel;
        int period;
        int command;
        bool in_progress;
    } _llpPeakInterest;

    //  this is more extensible than a pair
    struct point_info
    {
        double         value;
        PointQuality_t quality;
        int            freeze_bit;
        string         description;
    };

    static error_set initErrorInfo( void );

    unsigned char crc8(const unsigned char *buf, unsigned int len) const;
    point_info getData(unsigned char *buf, int len, ValueType vt=ValueType_Demand) const;

    int makeDynamicDemand(double input) const;

    string valueReport(const CtiPointSPtr p,    const point_info &pi, const CtiTime &t = YUKONEOT) const;
    string valueReport(const string &pointname, const point_info &pi, const CtiTime &t = YUKONEOT, bool undefined = true) const;

    bool insertPointDataReport(CtiPointType_t type, int offset, CtiReturnMsg *rm, point_info pi, const string &default_pointname="", const CtiTime &timestamp=0UL, double default_multiplier=1.0, int tags=0);

    virtual long getLoadProfileInterval(unsigned channel) = 0;
    virtual point_info getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len) = 0;

    virtual ConfigPartsList getPartsList();

    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual INT executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);


    virtual int executePutConfigDst               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigVThreshold        (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigDemandLP          (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigAddressing        (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigTOU               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigDisconnect        (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigOptions           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigHoliday           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigUsage             (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigLongLoadProfile   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigLoadProfileChannel(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigRelays            (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigPrecannedTable    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigCentron           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual int executePutConfigDNP               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);

    INT decodeGetConfigTime      (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeGetConfigTOU       (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodePutConfig          (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeGetValuePeakDemand (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeGetValueLoadProfile(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeScanLoadProfile    (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);

    static const char *PutConfigPart_all;
    static const char *PutConfigPart_tou;
    static const char *PutConfigPart_dst;
    static const char *PutConfigPart_vthreshold;
    static const char *PutConfigPart_demand_lp;
    static const char *PutConfigPart_options;
    static const char *PutConfigPart_addressing;
    static const char *PutConfigPart_disconnect;
    static const char *PutConfigPart_holiday;
    static const char *PutConfigPart_usage;
    static const char *PutConfigPart_llp;
    static const char *PutConfigPart_lpchannel;
    static const char *PutConfigPart_relays;
    static const char *PutConfigPart_precanned_table;
    static const char *PutConfigPart_dnp;
    static const char *PutConfigPart_centron;

public:

    CtiDeviceMCT4xx();
    CtiDeviceMCT4xx(const CtiDeviceMCT4xx &aRef);

    virtual ~CtiDeviceMCT4xx();

    CtiDeviceMCT4xx &operator=(const CtiDeviceMCT4xx &aRef);

    enum
    {
        UniversalAddress = 4194012,  //  0x3FFEDC

        Command_FreezeVoltageOne = 0x59,
        Command_FreezeVoltageTwo = 0x5A,

        Command_PowerfailReset   = 0x89,
        Command_Reset            = 0x8A,

        FuncWrite_Command        = 0x00,

        FuncWrite_TSyncPos       = 0xf0,
        FuncWrite_TSyncLen       =    6
    };

    INT ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);

};

#endif // #ifndef __DEV_MCT4xx_H__
