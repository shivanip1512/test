#pragma once

#include "dev_rfnMeter.h"
#include "cmd_rfn_DemandFreeze.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_RemoteDisconnect.h"


namespace Cti::Devices {


class IM_EX_DEVDB RfnResidentialDevice
    :   public RfnMeterDevice
{
protected:

    virtual ConfigMap getConfigMethods(InstallType installType);

    YukonError_t executeImmediateDemandFreeze             (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeTouCriticalPeak                   (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executePutConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executePutConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executeGetConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executePutValueTouReset                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executePutValueTouResetZero              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executePutConfigDemandFreezeDay          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executePutConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    YukonError_t executeReadDemandFreezeInfo              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executeGetStatusTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    YukonError_t executePutConfigDisconnect               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetConfigDisconnect               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executePutConfigDemandInterval           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executeGetConfigDemandInterval           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnConfigNotificationCommand               & cmd ) override;
    void handleCommandResult( const Commands::RfnGetDemandFreezeInfoCommand              & cmd ) override;
    void handleCommandResult( const Commands::RfnDemandFreezeConfigurationCommand        & cmd ) override;
    void handleCommandResult( const Commands::RfnDemandIntervalGetConfigurationCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnDemandIntervalSetConfigurationCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnTouScheduleGetConfigurationCommand      & cmd ) override;
    void handleCommandResult( const Commands::RfnTouScheduleSetConfigurationCommand      & cmd ) override;
    void handleCommandResult( const Commands::RfnTouStateConfigurationCommand            & cmd ) override;
    void handleCommandResult( const Commands::RfnTouHolidayConfigurationCommand          & cmd ) override;
    void handleCommandResult( const Commands::RfnRemoteDisconnectGetConfigurationCommand & cmd ) override;
    void handleCommandResult( const Commands::RfnRemoteDisconnectSetConfigurationCommand & cmd ) override;

protected:

    static std::optional<bool> isDisconnectType(DeviceTypes t);
    static bool isDisconnectConfigSupported(DeviceTypes t);
    bool isDisconnectConfigSupported() const;

    virtual bool isDemandIntervalConfigSupported() const;

private:

    std::string getDisconnectModeString( Commands::RfnRemoteDisconnectConfigurationCommand::DisconnectMode disconnectMode );
    std::string getReconnectParamString( Commands::RfnRemoteDisconnectConfigurationCommand::Reconnect reconnectParam );

    bool isTouConfigCurrent( const Config::DeviceConfigSPtr &deviceConfig, std::map<std::string, std::string> &configMap );

    void storeTouState(
        const boost::optional<Commands::RfnTouScheduleConfigurationCommand::TouState> & touState,
        const boost::optional<Commands::RfnTouScheduleConfigurationCommand::Schedule> & schedule_received);

    void storeTouHolidays(const Commands::RfnTouHolidayConfigurationCommand::Holidays holidays);
    void storeDemandFreezeDay(const uint8_t demandFreezeDay);
    void storeDemandInterval(const std::chrono::minutes demandInterval);
    void storeDisconnect(const Commands::RfnRemoteDisconnectConfigurationCommand::Read & cmd);
};

typedef RfnResidentialDevice Rfn410fxDevice;
typedef RfnResidentialDevice Rfn410fdDevice;

}