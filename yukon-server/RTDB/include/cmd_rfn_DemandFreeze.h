#pragma once

#include "cmd_rfn.h"
#include <boost/optional.hpp>
#include <map>


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

    virtual Bytes getCommandData();

    const Operation _operation;

    RfnCommandResult decodeResponseHeader( const CtiTime now,
                                           const RfnResponsePayload & response );

public:

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response ) = 0;
};


////


class IM_EX_DEVDB RfnDemandFreezeConfigurationCommand : public RfnDemandFreezeCommand
{
    unsigned char _freezeDay;

protected:

    virtual Bytes getCommandData();

public:

    RfnDemandFreezeConfigurationCommand( const unsigned char day_of_freeze );

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );

};


////


class IM_EX_DEVDB RfnImmediateDemandFreezeCommand : public RfnDemandFreezeCommand
{
public:

    RfnImmediateDemandFreezeCommand();

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );

};


////


class IM_EX_DEVDB RfnGetDemandFreezeInfoCommand : public RfnDemandFreezeCommand
{
public:

    struct DemandFreezeData
    {
        enum DemandRates
        {
            DemandRates_Base,
            DemandRates_Rate_A,
            DemandRates_Rate_B,
            DemandRates_Rate_C,
            DemandRates_Rate_D,
            DemandRates_Rate_E,
            Rate_Count
        };

        struct RateInfo
        {
            boost::optional<unsigned int>   rate;
            boost::optional<unsigned int>   timestamp;
        };

        boost::optional<unsigned char>      dayOfFreeze;
        boost::optional<unsigned int>       lastFreezeTime;
        std::map<DemandRates, RateInfo>     demandInfo;
    };

    virtual void invokeResultHandler(RfnCommand::ResultHandler &rh) const;

    RfnGetDemandFreezeInfoCommand();

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );

    DemandFreezeData getDemandFreezeData() const;

private:

    DemandFreezeData _freezeData;
};


}
}
}

