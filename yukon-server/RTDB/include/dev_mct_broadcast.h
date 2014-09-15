#pragma once

#include "dev_dlcbase.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB MctBroadcastDevice : public DlcBaseDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    MctBroadcastDevice(const MctBroadcastDevice&);
    MctBroadcastDevice& operator=(const MctBroadcastDevice&);

    typedef DlcBaseDevice Inherited;

    int _last_freeze;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    bool getOperation( const UINT &cmdType, USHORT &function, USHORT &length, USHORT &io );

    enum
    {
        MCTBCAST_ResetPF            =    0x50,
        MCTBCAST_ResetPFLen         =       0,

        MCTBCAST_LeadMeterOffset    = 4186111  //  4186112 - 1
    };

    YukonError_t executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t executePutValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

public:

    MctBroadcastDevice();

    YukonError_t ResultDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual LONG getAddress() const;
};

}
}

