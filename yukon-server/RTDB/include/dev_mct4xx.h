#pragma once

#include "dev_mct.h"
#include "config_device.h"
#include "config_data_mct.h"
#include "ctidate.h"

#include <vector>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct4xxDevice : public MctDevice
{
protected:

    typedef vector<const char *> ConfigPartsList;

private:

    typedef MctDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    int executePutConfigSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool readsOnly);
    int executePutConfigMultiple(ConfigPartsList & partsList, CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool readsOnly);

protected:

    static string printable_time(unsigned long seconds);
    static string printable_date(unsigned long seconds);
    static string printable_date(const CtiDate &dt);

    bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;
    void createTOUDayScheduleString(string &schedule, long (&times)[5], long (&rates)[6]);

    enum ValueType4xx
    {
        ValueType_Accumulator,
        ValueType_FrozenAccumulator,
        ValueType_Raw,
    };

    struct error_info
    {
        int error;
        PointQuality_t quality;
        string description;

        error_info(int e, string d = "", PointQuality_t q = NormalQuality) :
            error(e),
            description(d),
            quality(q)
        {
        }

        bool operator<(const error_info &rhs) const
        {
            return error < rhs.error;
        }
    };

    typedef set<error_info> error_set;

    static const error_set _mct_error_info;

    static error_set initErrorInfo( void );

    enum Commands
    {
        Command_TOUDisable   = 0x55,
        Command_TOUEnable    = 0x56,

        Command_TOUResetZero = 0x5e,
        Command_TOUReset     = 0x5f,
    };

    enum MemoryMap
    {
        Memory_OptionsPos              = 0x02,
        Memory_OptionsLen              =    1,

        Memory_ConfigurationPos        = 0x03,
        Memory_ConfigurationLen        =    1,

        Memory_DayOfScheduledFreezePos = 0x4f,
        Memory_DayOfScheduledFreezeLen =    1,

        Memory_TOUDayTablePos          = 0x50,
        Memory_TOUDayTableLen          =    2,

        Memory_TOUDailySched1Pos       = 0x52,
        Memory_TOUDailySched1Len       =    7,

        Memory_TOUDailySched2Pos       = 0x59,
        Memory_TOUDailySched2Len       =    7,

        Memory_TOUDailySched3Pos       = 0x60,
        Memory_TOUDailySched3Len       =    7,

        Memory_TOUDailySched4Pos       = 0x67,
        Memory_TOUDailySched4Len       =    7,

        Memory_TOUDefaultRatePos       = 0x6e,
        Memory_TOUDefaultRateLen       =    1,
    };

    enum Functions
    {
        FuncWrite_ConfigAlarmMaskPos = 0x01,

        FuncWrite_LLPStoragePos      = 0x04,
        FuncWrite_LLPStorageLen      =    5,

        FuncWrite_LLPInterestPos     = 0x05,
        FuncWrite_LLPInterestLen     =    6,

        FuncWrite_LLPPeakInterestPos = 0x06,
        FuncWrite_LLPPeakInterestLen =    9,

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
        FuncRead_TOULen              =   10,
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

    enum Features
    {
        Feature_LoadProfilePeakReport,
        Feature_TouPeaks
    };

    static const CtiDate DawnOfTime_Date;

    enum
    {
        LPChannels       =    4,
        LPRetries        =    3,
        LPRecentBlocks   =   16,

        DawnOfTime_UtcSeconds   = 0x386d4380,  //  jan 1, 2000, in UTC seconds

        PointOffset_RateOffset  =  20,   //  gets added for rate B, C, D

        PointOffset_PeakOffset  =  10,

        PointOffset_TOUBase     = 100,  //  this is okay because TOU only has peak and frozen demand - it must start at 111 anyway
    };

    struct lp_info_t
    {
        unsigned long collection_point;
        unsigned long current_schedule;
    } _lp_info[LPChannels];

    struct llp_interest_t
    {
        unsigned long time;
        unsigned long time_end;
        unsigned long user_id;

        unsigned offset;
        unsigned channel;

        volatile long in_progress;

        unsigned retry;
        bool failed;
    } _llpInterest;

    struct llp_peak_report_interest_t
    {
        unsigned long time;
        int channel;
        int period;
        int command;
        long in_progress;
    } _llpPeakInterest;

    virtual CtiTime getDeviceDawnOfTime() const      { return DawnOfTime_UtcSeconds; }
    virtual bool is_valid_time(const CtiTime) const;

    bool sspecAtLeast(const int rev_desired) const;

    virtual bool isSupported(const Features feature) const = 0;
    virtual bool sspecValid(const unsigned sspec, const unsigned rev) const = 0;

    static unsigned char crc8(const unsigned char *buf, unsigned int len);

    //  force it pure virtual so it must be overridden by the 410 and 470
    virtual const read_key_store_t &getReadKeyStore(void) const = 0;

    //  overridden by the 410 and 470 so they can use the same peak/TOU decode function
    virtual point_info getDemandData(unsigned char *buf, int len, bool is_frozen_data) const = 0;
    point_info getAccumulatorData(unsigned char *buf, int len, bool is_frozen_data) const;

    point_info getData(const unsigned char *buf, int len, ValueType4xx vt) const;

    virtual long getLoadProfileInterval(unsigned channel) = 0;
    virtual point_info getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len) = 0;

    virtual ConfigPartsList getPartsList() = 0;

    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual INT executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    virtual INT executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);

    virtual int executePutConfigLoadProfileChannel (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigConfigurationByte  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigTimeAdjustTolerance(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigTimezone           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigSpid               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigDemandLP           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigTOU                (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigPrecannedTable     (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList, bool readsOnly = false);
    virtual int executePutConfigRelays            (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool readsOnly = false);

    //virtual int executePutConfigDisconnect        (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    //virtual int executePutConfigDNP               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);

    INT decodeGetConfigTime      (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeGetConfigTOU       (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodePutConfig          (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeGetValuePeakDemand (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeGetValueLoadProfile(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT decodeScanLoadProfile    (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);

    virtual INT decodeGetStatusFreeze( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList ) = 0; // pure virtual, hence this is an abstract class

    static const char *PutConfigPart_basic;
    static const char *PutConfigPart_all;
    static const char *PutConfigPart_tou;
    static const char *PutConfigPart_dst;
    static const char *PutConfigPart_timezone;
    static const char *PutConfigPart_vthreshold;
    static const char *PutConfigPart_demand_lp;
    static const char *PutConfigPart_options;
    static const char *PutConfigPart_configbyte;
    static const char *PutConfigPart_time_adjust_tolerance;
    static const char *PutConfigPart_addressing;
    static const char *PutConfigPart_disconnect;
    static const char *PutConfigPart_holiday;
    static const char *PutConfigPart_usage;
    static const char *PutConfigPart_llp;
    static const char *PutConfigPart_lpchannel;
    static const char *PutConfigPart_relays;
    static const char *PutConfigPart_precanned_table;
    static const char *PutConfidPart_spid;
    static const char *PutConfigPart_dnp;
    static const char *PutConfigPart_centron;

public:

    Mct4xxDevice();
    Mct4xxDevice(const Mct4xxDevice &aRef);

    virtual ~Mct4xxDevice();

    static bool isKwhDataRead(Mct4xxDevice::ValueType4xx vt);

    Mct4xxDevice &operator=(const Mct4xxDevice &aRef);

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

    //  will start up any outstanding LLP requests
    void deviceInitialization(list< CtiRequestMsg * > &request_list);

    static unsigned loadTimeSync(unsigned char *buf);

    INT ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    INT ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList, bool &overrideExpectMore);

};

}
}

