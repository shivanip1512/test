#pragma once

#include "cmd_rfn.h"

#include <boost/optional.hpp>

namespace Cti        {
namespace Devices    {
namespace Commands   {

//------------------------------------------------------------
// Remote disconnect command base class
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectCommand : public RfnCommand
{
public: 

    enum DemandInterval
    {
        DemandInterval_Five    =  5,
        DemandInterval_Ten     = 10,
        DemandInterval_Fifteen = 15
    };

    enum Reconnect
    {
        Reconnect_Arm       = 0,
        Reconnect_Immediate = 1
    };

    enum DisconnectMode
    {
        DisconnectMode_OnDemand        = 0x01,
        DisconnectMode_DemandThreshold = 0x02,
        DisconnectMode_Cycling         = 0x03
    };

protected:

    enum CommandCode
    {
        CommandCode_Request            = 0x82,
        CommandCode_Response           = 0x83
    };

    enum Operation
    {
        Operation_SetConfiguration     = 0x00,
        Operation_GetConfiguration     = 0x01
    };

    const Operation _operation;

    RfnCommandResult decodeResponseHeader( const CtiTime now, const RfnResponsePayload & response );

    virtual Bytes getCommandData();

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const;

    typedef std::vector<TypeLengthValue> TlvList;
    virtual TlvList getTlvs();

    TlvList getTlvsFromPayload( const RfnResponsePayload & response );

    virtual DisconnectMode getDisconnectMode() const = 0;

    RfnRemoteDisconnectCommand( const Operation operation );
};

//------------------------------------------------------------
// Base class for remote disconnect set configuration commands
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetConfigurationCommand : public RfnRemoteDisconnectCommand
{
public:

    unsigned char currentDisconnectMode;

    virtual RfnCommandResult decodeCommand( const CtiTime now, const RfnResponsePayload &response );

    virtual DisconnectMode getDisconnectMode() const = 0;

protected:

    RfnRemoteDisconnectSetConfigurationCommand();

    virtual TlvList getTlvs();

    virtual Bytes getData() = 0;
};

//------------------------------------------------------------
// On-Demand disconnect set configuration command
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetOnDemandConfigurationCommand : public RfnRemoteDisconnectSetConfigurationCommand
{
public:

    virtual void invokeResultHandler( RfnCommand::ResultHandler &rh ) const;

    RfnRemoteDisconnectSetOnDemandConfigurationCommand( const Reconnect reconnect_param );

    virtual DisconnectMode getDisconnectMode() const;

    Reconnect reconnectParam;

protected:

    virtual Bytes   getData();
};

//------------------------------------------------------------
// Demand threshold disconnect set configuration command
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetThresholdConfigurationCommand : public RfnRemoteDisconnectSetConfigurationCommand
{
public:

    virtual void invokeResultHandler( RfnCommand::ResultHandler &rh ) const;

    RfnRemoteDisconnectSetThresholdConfigurationCommand( const Reconnect      reconnect_param,
                                                         const DemandInterval demand_interval,
                                                         const double         demand_threshold,
                                                         const unsigned       connect_delay,
                                                         const unsigned       max_disconnects );

    virtual DisconnectMode getDisconnectMode() const;

    Reconnect reconnectParam;
    DemandInterval demandInterval;
    double demandThreshold;
    unsigned connectDelay;
    unsigned maxDisconnects;

protected:

    virtual Bytes   getData();
};

//------------------------------------------------------------
// Cycling disconnect set configuration command
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetCyclingConfigurationCommand : public RfnRemoteDisconnectSetConfigurationCommand
{
public:

    virtual void invokeResultHandler( RfnCommand::ResultHandler &rh ) const;

    RfnRemoteDisconnectSetCyclingConfigurationCommand( const unsigned disconnect_minutes,
                                                       const unsigned connect_minutes );

    virtual DisconnectMode getDisconnectMode() const;

    unsigned disconnectMinutes;
    unsigned connectMinutes;

protected:

    virtual Bytes   getData();
};

//------------------------------------------------------------
// Base class for remote disconnect get configuration commands
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectGetConfigurationCommand : public RfnRemoteDisconnectCommand
{
public:

    virtual void invokeResultHandler( RfnCommand::ResultHandler &rh ) const;

    RfnRemoteDisconnectGetConfigurationCommand();

    virtual RfnCommandResult decodeCommand( const CtiTime now, const RfnResponsePayload & response );

    virtual DisconnectMode getDisconnectMode() const;

    Reconnect      getReconnectParam() const;

    boost::optional<DemandInterval> getDemandInterval() const;
    boost::optional<double>         getDemandThreshold() const;
    boost::optional<unsigned>       getConnectDelay() const;
    boost::optional<unsigned>       getMaxDisconnects() const;
    boost::optional<unsigned>       getDisconnectMinutes() const;
    boost::optional<unsigned>       getConnectMinutes() const;

private:

    DisconnectMode _disconnectMode;

    // Shared parameters
    Reconnect _reconnectParam;

    // Unique parameters
    boost::optional<DemandInterval> _demandInterval;
    boost::optional<double>         _demandThreshold;
    boost::optional<unsigned>       _connectDelay;
    boost::optional<unsigned>       _maxDisconnects;
    boost::optional<unsigned>       _disconnectMinutes;
    boost::optional<unsigned>       _connectMinutes;
};

}
}
}
