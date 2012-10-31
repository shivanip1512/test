#pragma once

#include "dev_mct420.h"

namespace Cti {
namespace Devices {


class IM_EX_DEVDB Mct440_213xBDevice : public Mct420Device
{
    typedef Mct420Device Inherited;

    static const FunctionReadValueMappings _readValueMaps;

    static const CommandSet _commandStore;

    static FunctionReadValueMappings initReadValueMaps();

    static CommandSet initCommandStore();

    static string describeStatusAndEvents(unsigned char *buf);

    enum SspecInformation
    {
        Sspec                                   = 1030,
        SspecRev_DailyRead                      = 21,  //  rev  2.1
    };

protected:

    enum PointOffsets
    {
        PointOffset_PulseAcc_LineVoltagePhaseA  = 801,
        PointOffset_PulseAcc_LineVoltagePhaseB  = 802,
        PointOffset_PulseAcc_LineVoltagePhaseC  = 803,

        PointOffset_PulseAcc_LineCurrentPhaseA  = 811,
        PointOffset_PulseAcc_LineCurrentPhaseB  = 812,
        PointOffset_PulseAcc_LineCurrentPhaseC  = 813,

        PointOffset_PulseAcc_LinePowFactPhaseA  = 821,
        PointOffset_PulseAcc_LinePowFactPhaseB  = 822,
        PointOffset_PulseAcc_LinePowFactPhaseC  = 823,

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


    // overrite the MCT-420's function read definition

    virtual const FunctionReadValueMappings *getReadValueMaps() const;

    virtual bool getOperation(const UINT &cmd,  BSTRUCT &bst ) const;

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

    virtual int getPhaseCount() = 0;

    virtual INT decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual int executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int executePutConfigTOUDays(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetConfigThresholds( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual int executePutConfigThresholds(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

};


}
}
