#pragma once

#include "cmd_rfn.h"

#include <boost/optional.hpp>

namespace Cti        {
namespace Devices    {
namespace Commands   {

//------------------------------------------------------------
// Remote disconnect command base class
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectConfigurationCommand : public RfnCommand
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

    boost::optional<DisconnectMode> getDisconnectMode() const;
    boost::optional<Reconnect>      getReconnectParam() const;

    boost::optional<unsigned>  getDemandInterval()    const;
    boost::optional<double>    getDemandThreshold()   const;
    boost::optional<unsigned>  getConnectDelay()      const;
    boost::optional<unsigned>  getMaxDisconnects()    const;
    boost::optional<unsigned>  getDisconnectMinutes() const;
    boost::optional<unsigned>  getConnectMinutes()    const;

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

    std::string decodeDisconnectConfigTlv( TypeLengthValue tlv );

    virtual Bytes getCommandData();

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const;

    typedef std::vector<TypeLengthValue> TlvList;
    virtual TlvList getTlvs();

    TlvList getTlvsFromPayload( const RfnResponsePayload & response );

    RfnRemoteDisconnectConfigurationCommand( const Operation operation );

private:

    boost::optional<DisconnectMode> _disconnectMode;

    // Shared parameters
    boost::optional<Reconnect> _reconnectParam;

    // Unique parameters
    boost::optional<unsigned>  _demandInterval;
    boost::optional<double>    _demandThreshold;
    boost::optional<unsigned>  _connectDelay;
    boost::optional<unsigned>  _maxDisconnects;
    boost::optional<unsigned>  _disconnectMinutes;
    boost::optional<unsigned>  _connectMinutes;
};

//------------------------------------------------------------
// Base class for remote disconnect set configuration commands
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetConfigurationCommand : public RfnRemoteDisconnectConfigurationCommand
{
public:

    unsigned char currentDisconnectMode;

    virtual RfnCommandResult decodeCommand( const CtiTime now, const RfnResponsePayload &response );

protected:

    RfnRemoteDisconnectSetConfigurationCommand();

    virtual TlvList getTlvs();

    virtual Bytes getData() = 0;

    virtual DisconnectMode getConfigurationDisconnectMode() const = 0;
};

//------------------------------------------------------------
// On-Demand disconnect set configuration command
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetOnDemandConfigurationCommand : public RfnRemoteDisconnectSetConfigurationCommand,
       InvokerFor<RfnRemoteDisconnectSetOnDemandConfigurationCommand>
{
public:

    RfnRemoteDisconnectSetOnDemandConfigurationCommand( const Reconnect reconnect_param );

    Reconnect reconnectParam;

protected:

    virtual Bytes   getData();

    virtual DisconnectMode getConfigurationDisconnectMode() const;
};

//------------------------------------------------------------
// Demand threshold disconnect set configuration command
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetThresholdConfigurationCommand : public RfnRemoteDisconnectSetConfigurationCommand,
       InvokerFor<RfnRemoteDisconnectSetThresholdConfigurationCommand>
{
public:

    RfnRemoteDisconnectSetThresholdConfigurationCommand( const Reconnect      reconnect_param,
                                                         const DemandInterval demand_interval,
                                                         const double         demand_threshold,
                                                         const unsigned       connect_delay,
                                                         const unsigned       max_disconnects );

    Reconnect reconnectParam;
    DemandInterval demandInterval;
    double demandThreshold;
    unsigned connectDelay;
    unsigned maxDisconnects;

protected:

    virtual Bytes   getData();

    virtual DisconnectMode getConfigurationDisconnectMode() const;
};

//------------------------------------------------------------
// Cycling disconnect set configuration command
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectSetCyclingConfigurationCommand : public RfnRemoteDisconnectSetConfigurationCommand,
       InvokerFor<RfnRemoteDisconnectSetCyclingConfigurationCommand>
{
public:

    RfnRemoteDisconnectSetCyclingConfigurationCommand( const unsigned disconnect_minutes,
                                                       const unsigned connect_minutes );

    unsigned disconnectMinutes;
    unsigned connectMinutes;

protected:

    virtual Bytes   getData();

    virtual DisconnectMode getConfigurationDisconnectMode() const;
};

//------------------------------------------------------------
// Base class for remote disconnect get configuration commands
//------------------------------------------------------------
class IM_EX_DEVDB RfnRemoteDisconnectGetConfigurationCommand : public RfnRemoteDisconnectConfigurationCommand,
       InvokerFor<RfnRemoteDisconnectGetConfigurationCommand>
{
public:

    RfnRemoteDisconnectGetConfigurationCommand();

    virtual RfnCommandResult decodeCommand( const CtiTime now, const RfnResponsePayload & response );
};

}
}
}
