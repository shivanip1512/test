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

    enum SspecInformation
    {
        Sspec = 1030
    };

protected:

    enum PointOffsets
    {
        PointOffset_LineVoltagePhaseA        = 0,
        PointOffset_LineVoltagePhaseB        = 0,
        PointOffset_LineVoltagePhaseC        = 0,

        PointOffset_LineCurrentPhaseA        = 0,
        PointOffset_LineCurrentPhaseB        = 0,
        PointOffset_LineCurrentPhaseC        = 0,

        PointOffset_LinePowFactorPhaseA      = 0,
        PointOffset_LinePowFactorPhaseB      = 0,
        PointOffset_LinePowFactorPhaseC      = 0,

        PointOffset_TOUBaseForward           = 100,
        PointOffset_TOUBaseReverse           = 200,
    };


    enum Functions
    {
        FuncWrite_TOUSchedule3Pos            = 0x33,
        FuncWrite_TOUSchedule3Len            =   15,
        FuncWrite_TOUSchedule4Pos            = 0x34,
        FuncWrite_TOUSchedule4Len            =   15,

        FuncRead_TOUSwitchSchedule12Part2Pos = 0xb8,
        FuncRead_TOUSwitchSchedule12Part2Len =   13,
        FuncRead_TOUSwitchSchedule34Part2Pos = 0xb9,
        FuncRead_TOUSwitchSchedule34Part2Len =   13,
    };


    // overrite the MCT-420's function read definition

    virtual const ReadDescriptor getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const;

    virtual bool getOperation(const UINT &cmd,  BSTRUCT &bst ) const;

    virtual bool isSupported(const Mct410Device::Features feature) const;

    virtual INT executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual bool sspecValid(const unsigned sspec, const unsigned rev) const;

    virtual INT ModelDecode(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetValueInstantLineData(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT decodeGetValueTOUkWh(INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual int executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly);

    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual long resolveScheduleName(const string & scheduleName);

    virtual void createTOUDayScheduleString(string &schedule, long (&times)[10], long (&rates)[11]);
};


}
}
