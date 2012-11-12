#pragma once

#include "dev_mct420.h"

namespace Cti {
namespace Devices {


class IM_EX_DEVDB Mct440_213xBDevice : public Mct420Device
{
    typedef Mct420Device Inherited;

    static const FunctionReadValueMappings _readValueMaps;
    static FunctionReadValueMappings       initReadValueMaps();

    static const CommandSet _commandStore;
    static       CommandSet initCommandStore();

    static const ConfigPartsList _config_parts;
    static       ConfigPartsList initConfigParts();

    static string describeStatusAndEvents(unsigned char *buf);

    enum SspecInformation
    {
        Sspec                                   = 1030,
    };

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

        PointOffset_PulseAcc_TOUBaseForward     = 100,
        PointOffset_PulseAcc_TOUBaseReverse     = 200,

        PointOffset_PulseAcc_RecentkWhForward   = 181,
        PointOffset_PulseAcc_RecentkWhReverse   = 281,
    };

    enum Functions
    {
        FuncWrite_TOUSchedule3Pos               = 0x33,
        FuncWrite_TOUSchedule3Len               = 15,
        FuncWrite_TOUSchedule4Pos               = 0x34,
        FuncWrite_TOUSchedule4Len               = 15,

        FuncRead_TOUSwitchSchedule12Part2Pos    = 0xb8,
        FuncRead_TOUSwitchSchedule12Part2Len    = 13,
        FuncRead_TOUSwitchSchedule34Part2Pos    = 0xb9,
        FuncRead_TOUSwitchSchedule34Part2Len    = 13,

        FuncRead_Channel1SingleDayBasePos       = 0x30,
        FuncRead_Channel1SingleDayLen           = 10,
    };

    enum MemoryMap
    {
        Memory_Holiday1_6Pos                    = 0xd0,
        Memory_Holiday1_6Len                    = 12,

        Memory_Holiday7_12Pos                   = 0xd1,
        Memory_Holiday7_12Len                   = 12,

        Memory_Holiday13_18Pos                  = 0xd2,
        Memory_Holiday13_18Len                  = 12,

        Memory_Holiday19_24Pos                  = 0xd3,
        Memory_Holiday19_24Len                  = 12,

        Memory_Holiday25_28Pos                  = 0xd4,
        Memory_Holiday25_28Len                  = 8,
    };


    // overrite the MCT-420's function read definition

    virtual const FunctionReadValueMappings *getReadValueMaps() const;

    virtual bool getOperation(const UINT &cmd,  BSTRUCT &bst ) const;

    virtual ConfigPartsList getPartsList();

    virtual bool isSupported(const Mct410Device::Features feature) const;

    virtual INT executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual bool sspecValid(const unsigned sspec, const unsigned rev) const;

    virtual INT ModelDecode(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetValueInstantLineData(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetValueTOUkWh(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetValueDailyReadRecent(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly);

    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual long resolveScheduleName(const string & scheduleName);

    virtual void createTOUDayScheduleString(string &schedule, long (&times)[10], long (&rates)[11]);

    virtual void parseTOUDayScheduleString(string &schedule, long (&times)[10], long (&rates)[11]);

    virtual int getPhaseCount() = 0;

    virtual INT decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual int executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int executePutConfigTOUDays(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetConfigPhaseLossThreshold(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int executePutConfigPhaseLossThreshold(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetConfigTOU(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual int decodeGetConfigHoliday(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetStatusFreeze(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList  &retList, OutMessageList &outList);

    virtual int executePutConfigAlarmMask(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetConfigIntervals(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList  &retList, OutMessageList  &outList);

    virtual INT executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int decodeGetConfigAlarmMask(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList  &retList, OutMessageList  &outList);

    virtual void createTOUScheduleConfig(long (&daySchedule)[8], long (&times)[4][10], long (&rates)[4][11], long defaultRate, OUTMESS *&OutMessage, OutMessageList &outList);

};


}
}
