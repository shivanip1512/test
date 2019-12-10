#pragma once

#include "cmd_rfn_Individual.h"
#include <boost/optional.hpp>
#include <map>


namespace Cti        {
namespace Devices    {
namespace Commands   {



class IM_EX_DEVDB RfnDemandFreezeCommand : public RfnTwoWayCommand
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

public:

    enum TlvType
    {
        TlvType_ScheduledDayOfDemandFreeze  = 0x01,
        TlvType_TimeLastDemandFreezeOccurred = 0x02,

        TlvType_FrozenDeliveredPeakDemandTotal      = 0x03,
        TlvType_FrozenDeliveredPeakDemandTime       = 0x04,
        TlvType_FrozenDeliveredPeakDemandRateA      = 0x05,
        TlvType_FrozenDeliveredPeakDemandRateATime  = 0x06,
        TlvType_FrozenDeliveredPeakDemandRateB      = 0x07,
        TlvType_FrozenDeliveredPeakDemandRateBTime  = 0x08,
        TlvType_FrozenDeliveredPeakDemandRateC      = 0x09,
        TlvType_FrozenDeliveredPeakDemandRateCTime  = 0x0a,
        TlvType_FrozenDeliveredPeakDemandRateD      = 0x0b,
        TlvType_FrozenDeliveredPeakDemandRateDTime  = 0x0c,
        TlvType_FrozenDeliveredPeakDemandRateE      = 0x0d,
        TlvType_FrozenDeliveredPeakDemandRateETime  = 0x0e,

        TlvType_FrozenReceivedPeakDemandTotal       = 0x20,
        TlvType_FrozenReceivedPeakDemandTime        = 0x21,
        TlvType_FrozenReceivedPeakDemandRateA       = 0x22,
        TlvType_FrozenReceivedPeakDemandRateATime   = 0x23,
        TlvType_FrozenReceivedPeakDemandRateB       = 0x24,
        TlvType_FrozenReceivedPeakDemandRateBTime   = 0x25,
        TlvType_FrozenReceivedPeakDemandRateC       = 0x26,
        TlvType_FrozenReceivedPeakDemandRateCTime   = 0x27,
        TlvType_FrozenReceivedPeakDemandRateD       = 0x28,
        TlvType_FrozenReceivedPeakDemandRateDTime   = 0x29,
        TlvType_FrozenReceivedPeakDemandRateE       = 0x2a,
        TlvType_FrozenReceivedPeakDemandRateETime   = 0x2b,

        TlvType_FrozenDeliveredPeakVarTotal         = 0x30,
        TlvType_FrozenDeliveredPeakVarTime          = 0x31,
        TlvType_FrozenDeliveredPeakVarRateA         = 0x32,
        TlvType_FrozenDeliveredPeakVarRateATime     = 0x33,
        TlvType_FrozenDeliveredPeakVarRateB         = 0x34,
        TlvType_FrozenDeliveredPeakVarRateBTime     = 0x35,
        TlvType_FrozenDeliveredPeakVarRateC         = 0x36,
        TlvType_FrozenDeliveredPeakVarRateCTime     = 0x37,
        TlvType_FrozenDeliveredPeakVarRateD         = 0x38,
        TlvType_FrozenDeliveredPeakVarRateDTime     = 0x39,
        TlvType_FrozenDeliveredPeakVarRateE         = 0x3a,
        TlvType_FrozenDeliveredPeakVarRateETime     = 0x3b,

        TlvType_FrozenReceivedPeakVarTotal          = 0x40,
        TlvType_FrozenReceivedPeakVarTime           = 0x41,
        TlvType_FrozenReceivedPeakVarRateA          = 0x42,
        TlvType_FrozenReceivedPeakVarRateATime      = 0x43,
        TlvType_FrozenReceivedPeakVarRateB          = 0x44,
        TlvType_FrozenReceivedPeakVarRateBTime      = 0x45,
        TlvType_FrozenReceivedPeakVarRateC          = 0x46,
        TlvType_FrozenReceivedPeakVarRateCTime      = 0x47,
        TlvType_FrozenReceivedPeakVarRateD          = 0x48,
        TlvType_FrozenReceivedPeakVarRateDTime      = 0x49,
        TlvType_FrozenReceivedPeakVarRateE          = 0x4a,
        TlvType_FrozenReceivedPeakVarRateETime      = 0x4b
    };

protected:

    RfnDemandFreezeCommand( const Operation operation );

    virtual unsigned char getCommandCode() const;

    virtual unsigned char getOperation() const;

    virtual Bytes getCommandData();

    const Operation _operation;

    RfnCommandResult decodeResponseHeader( const CtiTime now,
                                           const RfnResponsePayload & response );

    virtual void validateStatus( const unsigned char status,
                                 const unsigned char asc,
                                 const unsigned char ascq,
                                 const std::string &description );

public:

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response ) = 0;
};


////


class IM_EX_DEVDB RfnDemandFreezeConfigurationCommand : public RfnDemandFreezeCommand,
       InvokerFor<RfnDemandFreezeConfigurationCommand>
{
protected:

    virtual Bytes getCommandData();

public:

    RfnDemandFreezeConfigurationCommand( const unsigned char day_of_freeze );

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    const unsigned char freezeDay;
};


////


class IM_EX_DEVDB RfnImmediateDemandFreezeCommand : public RfnDemandFreezeCommand, public NoResultHandler
{
public:

    RfnImmediateDemandFreezeCommand();

    virtual void validateStatus( const unsigned char status,
                                 const unsigned char asc,
                                 const unsigned char ascq,
                                 const std::string &description );

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

};


////


class IM_EX_DEVDB RfnGetDemandFreezeInfoCommand : public RfnDemandFreezeCommand,
       InvokerFor<RfnGetDemandFreezeInfoCommand>
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

        enum MetricTypes
        {
            Metric_FrozenPeak_Demand_Delivered,
            Metric_FrozenPeak_Demand_Received,
            Metric_FrozenPeak_Vars_Delivered,
            Metric_FrozenPeak_Vars_Received,
        };

        struct PeakRecord
        {
            boost::optional<unsigned int>   value;
            boost::optional<unsigned int>   timestamp;
        };

        boost::optional<unsigned char>      dayOfFreeze;
        boost::optional<unsigned int>       lastFreezeTime;

        typedef std::map<MetricTypes, PeakRecord> QuadrantPeakValues;

        typedef std::map<DemandRates, QuadrantPeakValues> PeaksPerRate;

        PeaksPerRate peakValues;
    };

    RfnGetDemandFreezeInfoCommand();

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    DemandFreezeData getDemandFreezeData() const;

private:

    DemandFreezeData _freezeData;
};


}
}
}

