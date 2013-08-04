#pragma once

#include "cmd_rfn.h"



namespace Cti        {
namespace Devices    {
namespace Commands   {



class IM_EX_DEVDB RfnDemandFreezeCommand : public RfnCommand
{
protected:

    enum CommandCode
    {
        CommandCode_Request                     = 0x55,
        CommandCode_Response                    = 0x56
    };

    enum Operation
    {
        Operation_ImmediateDemandFreeze         = 0x01,
        Operation_SetDayOfDemandFreeze          = 0x02,
        Operation_GetDemandFreezeInfo           = 0x03
    };

    enum TlvType
    {
        TlvType_ScheduledDayOfDemandFreeze      = 0x01,
        TlvType_TimeLastDemandFreezeOccured     = 0x02,
        TlvType_FrozenPeakDemandTotal           = 0x03,
        TlvType_FrozenPeakDemandTime            = 0x04,
        TlvType_FrozenPeakDemandRateA           = 0x05,
        TlvType_FrozenPeakDemandRateATime       = 0x06,
        TlvType_FrozenPeakDemandRateB           = 0x07,
        TlvType_FrozenPeakDemandRateBTime       = 0x08,
        TlvType_FrozenPeakDemandRateC           = 0x09,
        TlvType_FrozenPeakDemandRateCTime       = 0x0a,
        TlvType_FrozenPeakDemandRateD           = 0x0b,
        TlvType_FrozenPeakDemandRateDTime       = 0x0c,
        TlvType_FrozenPeakDemandRateE           = 0x0d,
        TlvType_FrozenPeakDemandRateETime       = 0x0e
    };

    RfnDemandFreezeCommand( const Operation operation );

    virtual unsigned char getCommandCode() const;

    virtual unsigned char getOperation() const;

    virtual Bytes getData();

    const Operation _operation;

    RfnResult decodeResponseHeader( const CtiTime now,
                                    const RfnResponse & response );

public:

    virtual RfnResult decode( const CtiTime now,
                              const RfnResponse & response ) = 0;

    virtual RfnResult error( const CtiTime now,
                             const YukonError_t error_code );
};


////


class IM_EX_DEVDB RfnDemandFreezeConfigurationCommand : public RfnDemandFreezeCommand
{
    unsigned char _freezeDay;

protected:

    virtual Bytes getData();

public:

    RfnDemandFreezeConfigurationCommand( const unsigned day_of_freeze );

    virtual RfnResult decode( const CtiTime now,
                              const RfnResponse & response );

};


}
}
}

