
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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2006/08/08 13:36:10 $
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
public:

    typedef vector<const char *> ConfigPartsList;

private:

    static ConfigPartsList initConfigParts();
    static const ConfigPartsList _config_parts;
    int executePutConfigSingle( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    typedef CtiDeviceMCT Inherited;

protected:

    enum ValueType
    {
        ValueType_Voltage,
        ValueType_KW,
        ValueType_LoadProfile_Voltage,
        ValueType_LoadProfile_KW,
        ValueType_Accumulator,
        ValueType_FrozenAccumulator,
        ValueType_Raw
    };

    enum ErrorClasses
    {
        EC_MeterReading    = 0x0001,
        EC_DemandReading   = 0x0002,
        EC_TOUDemand       = 0x0004,
        EC_TOUFrozenDemand = 0x0008,
        EC_LoadProfile     = 0x0010
    };

    typedef map<unsigned long, pair<PointQuality_t, int> > QualityMap;  //  the int will hold ErrorClasses OR'd together

    static const QualityMap _errorQualities;

    enum Functions
    {
        FuncWrite_LLPInterestPos     = 0x05,
        FuncWrite_LLPInterestLen     =    6,
    };

    enum
    {
        MCT4XX_LPChannels       =    4,
        MCT4XX_LPRecentBlocks   =   16,

        MCT4XX_DawnOfTime       = 0x386d4380,  //  jan 1, 2000, in UTC seconds

        MCT4XX_PointOffset_RateOffset  = 20,   //  gets added for rate B, C, D

        MCT4XX_PointOffset_PeakOffset  = 10,
    };

    struct lp_info_t
    {
        unsigned long archived_reading;
        unsigned long current_request;
        unsigned long current_schedule;
    } _lp_info[MCT4XX_LPChannels];

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

        MCT4XX_Command_FreezeVoltageOne = 0x59,
        MCT4XX_Command_FreezeVoltageTwo = 0x5A,

        MCT4XX_Command_PowerfailReset   = 0x89,
        MCT4XX_Command_Reset            = 0x8A,

        MCT4XX_FuncWrite_Command        = 0x00,

        MCT4XX_FuncWrite_TSyncPos       = 0xf0,
        MCT4XX_FuncWrite_TSyncLen       =    6
    };

    INT ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);

};

#endif // #ifndef __DEV_MCT4xx_H__
