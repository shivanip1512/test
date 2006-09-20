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
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2006/09/20 20:21:04 $
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

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    static const ConfigPartsList _config_parts;
    static ConfigPartsList initConfigParts();

    int executePutConfigSingle( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    typedef CtiDeviceMCT Inherited;

protected:

    bool getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io );

    enum ValueType
    {
        ValueType_Voltage,
        ValueType_KW,
        ValueType_LoadProfile_Voltage,
        ValueType_LoadProfile_KW,
        ValueType_Accumulator,
        ValueType_FrozenAccumulator,
        ValueType_IED,
        ValueType_Raw,
    };

    enum ErrorClasses
    {
        EC_MeterReading    = 1 << 0,
        EC_DemandReading   = 1 << 1,
        EC_TOUDemand       = 1 << 2,
        EC_TOUFrozenDemand = 1 << 3,
        EC_LoadProfile     = 1 << 4,
    };

    typedef map<unsigned long, pair<PointQuality_t, int> > QualityMap;  //  the int will hold ErrorClasses OR'd together

    static const QualityMap _errorQualities;

    enum Commands
    {
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

        FuncRead_MReadPos            = 0x90,

        FuncRead_DemandPos           = 0x92,

        FuncRead_PeakDemandPos       = 0x93,

        FuncRead_LLPStatusPos        = 0x9d,
        FuncRead_LLPStatusLen        =    8,

        FuncRead_LLPPeakDayPos       = 0xa0,
        FuncRead_LLPPeakHourPos      = 0xa1,
        FuncRead_LLPPeakIntervalPos  = 0xa2,
        FuncRead_LLPPeakLen          =   13,
    };

    enum
    {
        LPChannels       =    4,
        LPRecentBlocks   =   16,

        DawnOfTime       = 0x386d4380,  //  jan 1, 2000, in UTC seconds

        PointOffset_RateOffset  = 20,   //  gets added for rate B, C, D

        PointOffset_PeakOffset  = 10,
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
        bool retry;
        bool failed;
    } _llpInterest;

    struct llp_peak_report_interest_t
    {
        unsigned long time;
        int channel;
        int period;
        int command;
    } _llpPeakInterest;

    //  this is more extensible than a pair
    struct point_info_t
    {
        double value;
        PointQuality_t quality;
        int freeze_bit;
        //  this could hold a timestamp someday if i get really adventurous
    };

    static QualityMap initErrorQualities( void );

    unsigned char crc8(const unsigned char *buf, unsigned int len);
    point_info_t  getData(unsigned char *buf, int len, ValueType vt=ValueType_KW);

     CtiPointDataMsg *makePointDataMsg(CtiPointSPtr p, const point_info_t &pi, const string &pointString);

    virtual long getLoadProfileInterval(unsigned channel) = 0;
    virtual point_info_t getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len);

    virtual ConfigPartsList getPartsList();

    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
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
    INT decodePutConfig          (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
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
        UniversalAddress = 4194012,

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
