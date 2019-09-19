#pragma once

#include "cmd_device.h"

#include "rfn_asid.h"

#include <boost/shared_ptr.hpp>
#include <boost/cstdint.hpp>

namespace Cti::Devices::Commands {

class RfDaReadDnpSlaveAddressCommand;
class RfnAggregateCommand;
class RfnCentronSetLcdConfigurationCommand;
class RfnCentronGetLcdConfigurationCommand;
class RfnConfigNotificationCommand;
class RfnFocusAlLcdConfigurationReadCommand;
class RfnFocusAlLcdConfigurationWriteCommand;
class RfnDemandFreezeConfigurationCommand;
class RfnGetDemandFreezeInfoCommand;
class RfnDemandIntervalSetConfigurationCommand;
class RfnDemandIntervalGetConfigurationCommand;
class RfnLoadProfileGetRecordingCommand;
class RfnLoadProfileSetTemporaryRecordingCommand;
class RfnLoadProfileSetPermanentRecordingCommand;
class RfnVoltageProfileGetRecordingCommand;
class RfnVoltageProfileGetConfigurationCommand;
class RfnVoltageProfileSetConfigurationCommand;
class RfnTouConfigurationCommand;
class RfnTouScheduleGetConfigurationCommand;
class RfnTouScheduleSetConfigurationCommand;
class RfnTouStateConfigurationCommand;
class RfnTouHolidayConfigurationCommand;
class RfnGetOvUvAlarmConfigurationCommand;
class RfnSetOvUvAlarmProcessingStateCommand;
class RfnSetOvUvAlarmRepeatCountCommand;
class RfnSetOvUvAlarmRepeatIntervalCommand;
class RfnSetOvUvNewAlarmReportIntervalCommand;
class RfnSetOvUvSetOverVoltageThresholdCommand;
class RfnSetOvUvSetUnderVoltageThresholdCommand;
class RfnRemoteDisconnectGetConfigurationCommand;
class RfnRemoteDisconnectSetConfigurationCommand;
class RfnChannelSelectionCommand;
namespace RfnChannelIntervalRecording {
class GetConfigurationCommand;
class GetActiveConfigurationCommand;
class SetConfigurationCommand;
}
class RfnTemperatureAlarmCommand;

struct RfnCommandResult
{
    RfnCommandResult(const std::string &desc) 
        :   RfnCommandResult(desc, ClientErrors::None)
    {}
    RfnCommandResult(const std::string &desc, YukonError_t error) 
        :   description(desc)
        ,   status(error) 
    {}

    std::string description;
    YukonError_t status;
    std::vector<DeviceCommand::point_data> points;
};

using RfnCommandResultList = std::vector<RfnCommandResult>;

struct RfnResultHandlerInvoker
{
    struct ResultHandler
    {
        virtual void handleCommandResult(const RfDaReadDnpSlaveAddressCommand &)                      {}
        virtual void handleCommandResult(const RfnAggregateCommand &)                                 {}
        virtual void handleCommandResult(const RfnCentronSetLcdConfigurationCommand &)                {}
        virtual void handleCommandResult(const RfnCentronGetLcdConfigurationCommand &)                {}
        virtual void handleCommandResult(const RfnConfigNotificationCommand &)                        {}
        virtual void handleCommandResult(const RfnFocusAlLcdConfigurationReadCommand &)               {}
        virtual void handleCommandResult(const RfnFocusAlLcdConfigurationWriteCommand &)              {}
        virtual void handleCommandResult(const RfnDemandFreezeConfigurationCommand &)                 {}
        virtual void handleCommandResult(const RfnGetDemandFreezeInfoCommand &)                       {}
        virtual void handleCommandResult(const RfnDemandIntervalSetConfigurationCommand &)            {}
        virtual void handleCommandResult(const RfnDemandIntervalGetConfigurationCommand &)            {}
        virtual void handleCommandResult(const RfnLoadProfileGetRecordingCommand &)                   {}
        virtual void handleCommandResult(const RfnLoadProfileSetTemporaryRecordingCommand &)          {}
        virtual void handleCommandResult(const RfnLoadProfileSetPermanentRecordingCommand &)          {}
        virtual void handleCommandResult(const RfnVoltageProfileGetRecordingCommand &)                {}
        virtual void handleCommandResult(const RfnVoltageProfileGetConfigurationCommand &)            {}
        virtual void handleCommandResult(const RfnVoltageProfileSetConfigurationCommand &)            {}
        virtual void handleCommandResult(const RfnTouConfigurationCommand &)                          {}
        virtual void handleCommandResult(const RfnTouScheduleGetConfigurationCommand &)               {}
        virtual void handleCommandResult(const RfnTouScheduleSetConfigurationCommand &)               {}
        virtual void handleCommandResult(const RfnTouStateConfigurationCommand &)                     {}
        virtual void handleCommandResult(const RfnTouHolidayConfigurationCommand &)                   {}
        virtual void handleCommandResult(const RfnGetOvUvAlarmConfigurationCommand &)                 {}
        virtual void handleCommandResult(const RfnSetOvUvAlarmProcessingStateCommand &)               {}
        virtual void handleCommandResult(const RfnSetOvUvAlarmRepeatCountCommand &)                   {}
        virtual void handleCommandResult(const RfnSetOvUvAlarmRepeatIntervalCommand &)                {}
        virtual void handleCommandResult(const RfnSetOvUvNewAlarmReportIntervalCommand &)             {}
        virtual void handleCommandResult(const RfnSetOvUvSetOverVoltageThresholdCommand &)            {}
        virtual void handleCommandResult(const RfnSetOvUvSetUnderVoltageThresholdCommand &)           {}
        virtual void handleCommandResult(const RfnRemoteDisconnectGetConfigurationCommand &)          {}
        virtual void handleCommandResult(const RfnRemoteDisconnectSetConfigurationCommand &)          {}
        virtual void handleCommandResult(const RfnChannelSelectionCommand &)                          {}
        virtual void handleCommandResult(const RfnChannelIntervalRecording::SetConfigurationCommand &)        {}
        virtual void handleCommandResult(const RfnChannelIntervalRecording::GetConfigurationCommand &)        {}
        virtual void handleCommandResult(const RfnChannelIntervalRecording::GetActiveConfigurationCommand &)  {}
        virtual void handleCommandResult(const RfnTemperatureAlarmCommand &)                          {}
    };

    //  to be overridden by children that require a result handler
    virtual void invokeResultHandler(ResultHandler &rh) const = 0;
};

struct NoResultHandler : virtual RfnResultHandlerInvoker
{
    void invokeResultHandler(ResultHandler &rh) const final {}
};

template<class CommandType>
struct InvokerFor : virtual RfnResultHandlerInvoker
{
    void invokeResultHandler(ResultHandler &rh) const override final
    {
        //  Verify that we're actually the command type we were templated with
        static_assert(std::is_base_of_v<InvokerFor<CommandType>, CommandType>, "CommandType is not derived from InvokerFor<CommandType>");

        rh.handleCommandResult(static_cast<const CommandType &>(*this));
    }
};

class IM_EX_DEVDB RfnCommand : public DeviceCommand, public virtual RfnResultHandlerInvoker
{
public:

    using RfnRequestPayload  = Bytes;
    using RfnResponsePayload = Bytes;
    using RfnCommandPtr = std::unique_ptr<RfnCommand>;

    RfnRequestPayload executeCommand(const CtiTime now);

    virtual RfnCommandResultList handleResponse(const CtiTime now, const RfnResponsePayload &response) = 0;
    virtual RfnCommandResultList handleError(const CtiTime now, const YukonError_t errorCode) = 0;

    static std::unique_ptr<RfnConfigNotificationCommand> handleNodeOriginated(const CtiTime now, RfnResponsePayload payload);

    using ASID = Messaging::Rfn::ApplicationServiceIdentifiers;

    virtual ASID getApplicationServiceId() const;

protected:

    //
    // Functions called by execute() to create a request command
    //
    // Request command format :
    // 1-byte - Command Code
    // 1-byte - Operation
    // N-byte - Data
    //
    virtual unsigned char getCommandCode() const = 0;
    virtual unsigned char getOperation() const = 0;
    virtual Bytes         getCommandData() = 0;

    virtual Bytes         getCommandHeader();

    virtual void prepareCommandData(const CtiTime now) { }

};

using RfnCommandPtr = RfnCommand::RfnCommandPtr;
using RfnCommandList = std::vector<RfnCommandPtr>;

}