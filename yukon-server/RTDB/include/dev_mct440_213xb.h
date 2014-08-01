#pragma once

#include "dev_mct420.h"

namespace Cti {
namespace Devices {


class IM_EX_DEVDB Mct440_213xBDevice : public Mct420Device
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Mct440_213xBDevice(const Mct440_213xBDevice&);
    Mct440_213xBDevice& operator=(const Mct440_213xBDevice&);

    typedef Mct420Device Inherited;

    static const FunctionReadValueMappings  _readValueMaps;
    static FunctionReadValueMappings        initReadValueMaps();

    static const CommandSet                 _commandStore;
    static       CommandSet                 initCommandStore();

    static const ConfigPartsList            _config_parts;
    static       ConfigPartsList            initConfigParts();

    static const std::set<UINT>             _excludedCommands;
    static       std::set<UINT>             initExcludedCommands();

    static const CtiDate                    holidayBaseDate;

    static std::string describeStatusAndEvents(const unsigned char *buf);

    enum SspecInformation
    {
        Sspec                                   = 10300,
    };

    struct PutConfigPending_t
    {
        bool is_pending, force;
        PutConfigPending_t()
        {
            is_pending = false;
            force      = false;
        }
    };

    PutConfigPending_t InstallDstPending;

protected:

    enum PointOffsets
    {
        PointOffset_Analog_LineVoltagePhaseA    = 801,
        PointOffset_Analog_LineVoltagePhaseB    = 802,
        PointOffset_Analog_LineVoltagePhaseC    = 803,

        PointOffset_Analog_LineCurrentPhaseA    = 811,
        PointOffset_Analog_LineCurrentPhaseB    = 812,
        PointOffset_Analog_LineCurrentPhaseC    = 813,

        PointOffset_Analog_LinePowFactPhaseA    = 821,
        PointOffset_Analog_LinePowFactPhaseB    = 822,
        PointOffset_Analog_LinePowFactPhaseC    = 823,

        PointOffset_PulseAcc_TOUBaseFwd         = 100,
        PointOffset_PulseAcc_TOUBaseRev         = 200,

        PointOffset_PulseAcc_TOUBaseFwdFrozen   = 600,
        PointOffset_PulseAcc_TOUBaseRevFrozen   = 700,

        PointOffset_PulseAcc_BaseMRead          = 1,
        PointOffset_PulseAcc_BaseMReadFrozen    = 501,

        PointOffset_PulseAcc_RecentkWhForward   = 181,
        PointOffset_PulseAcc_RecentkWhReverse   = 281,
    };

    enum Functions
    {
        FuncWrite_TOUSchedule1Pos               = 0x30,
        FuncWrite_TOUSchedule1Len               = 15,
        FuncWrite_TOUSchedule2Pos               = 0x31,
        FuncWrite_TOUSchedule2Len               = 14,
        FuncWrite_TOUSchedule3Pos               = 0x33,
        FuncWrite_TOUSchedule3Len               = 10,
        FuncWrite_TOUSchedule4Pos               = 0x34,
        FuncWrite_TOUSchedule4Len               = 10,

        FuncRead_TOUSwitchSchedule12Part2Pos    = 0xb8,
        FuncRead_TOUSwitchSchedule12Part2Len    = 13,
        FuncRead_TOUSwitchSchedule34Part2Pos    = 0xb9,
        FuncRead_TOUSwitchSchedule34Part2Len    = 13,

        FuncRead_Channel1SingleDayBasePos       = 0x30,
        FuncRead_Channel1SingleDayLen           = 10,
    };

    enum MemoryMap
    {
        // 0x050 to 0x059 – Read Meter Parameters Change Register
        Memory_EventLogBasePos                  = 0x50,
        Memory_EventLogLen                      = 10,
        Memory_EventLogMaxOffset                = 9,
    };

    Mct440_213xBDevice() {};

    virtual const FunctionReadValueMappings  *getReadValueMaps() const;
    virtual const std::set<UINT>             *getExcludedCommands() const;
    virtual ConfigPartsList                   getPartsList();

    virtual bool getOperation                      (const UINT &cmd,  BSTRUCT &bst) const;
    virtual bool isSupported                       (const Mct410Device::Features feature) const;
    virtual bool isCommandExcluded                 (const UINT &cmd) const;
    virtual bool sspecValid                        (const unsigned sspec, const unsigned rev) const;

    virtual int getPhaseCount() = 0;

    virtual INT ModelDecode                        (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetValueInstantLineData      (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetValueKWH                  (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetValueTOUkWh               (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetValueDailyReadRecent      (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetConfigTOU                 (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int decodeGetConfigModel               (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetConfigPhaseLossThreshold  (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetConfigIntervals           (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int decodeGetConfigAlarmMask           (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetConfigDisconnect          (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int decodeGetConfigAddressing          (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int decodeGetConfigTimeAdjustTolerance (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int decodeGetConfigOptions             (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetStatusFreeze              (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetStatusInternal            (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetStatusDisconnect          (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodeGetStatusEventLog            (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT decodePutConfig                    (const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT executeGetValue                    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executeGetConfig                   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executePutConfig                   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executeGetStatus                   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executePutStatus                   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int executePutConfigTOUDays            (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int executePutConfigHoliday            (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int executePutConfigPhaseLossThreshold (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int executePutConfigAlarmMask          (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int executePutConfigTOU                (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly);
    virtual int executePutConfigInstallPhaseLoss   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly);
    virtual int executePutConfigInstallAddressing  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly);
    virtual int executePutConfigInstallDST         (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly);
    virtual int executePutConfigTimezone           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly);
    virtual int executePutConfigTimeAdjustTolerance(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly);

    virtual long        resolveScheduleName           (const std::string & scheduleName);
    virtual std::string decodeDisconnectStatus        (const DSTRUCT &DSt) const;
    virtual unsigned    getDisconnectReadDelay        () const;

    virtual void createTOUDayScheduleString        (std::string &schedule, long (&times)[9], long (&rates)[10]);
    virtual void parseTOUDayScheduleString         (std::string &schedule, long (&times)[9], long (&rates)[10]);

    virtual void createTOUScheduleConfig           (const long (&daySchedule)[8], const long (&times)[4][9], const long (&rates)[4][10], OUTMESS *&OutMessage, OutMessageList &outList);
};


}
}
