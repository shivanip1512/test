#pragma once

#include "dev_rfnMeter.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_DemandFreeze.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_OvUvConfiguration.h"
#include "cmd_rfn_RemoteDisconnect.h"


namespace Cti       {
namespace Devices   {


class IM_EX_DEVDB RfnResidentialDevice
    :   public RfnMeterDevice
{
protected:

    virtual ConfigMap getConfigMethods(bool readOnly);

    //int executeLoadProfileRecording              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeImmediateDemandFreeze             (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeTouCriticalPeak                   (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;

    YukonError_t executePutConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executePutConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;

    YukonError_t executeGetConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeGetConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;

    YukonError_t executePutValueTouReset                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executePutValueTouResetZero              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;

    YukonError_t executePutConfigDemandFreezeDay          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigOvUv                     (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executeReadDemandFreezeInfo              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetStatusTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeGetConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigOvUv                     (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executeGetConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executePutConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeGetValueVoltageProfile            (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;

    YukonError_t executePutConfigDisconnect               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeGetConfigDisconnect               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;

    void handleCommandResult( const Commands::RfnVoltageProfileGetConfigurationCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnVoltageProfileSetConfigurationCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnLoadProfileGetRecordingCommand          & cmd ) override;
    void handleCommandResult( const Commands::RfnGetDemandFreezeInfoCommand              & cmd ) override;
    void handleCommandResult( const Commands::RfnDemandFreezeConfigurationCommand        & cmd ) override;
    void handleCommandResult( const Commands::RfnTouScheduleGetConfigurationCommand      & cmd ) override;
    void handleCommandResult( const Commands::RfnTouScheduleSetConfigurationCommand      & cmd ) override;
    void handleCommandResult( const Commands::RfnTouStateConfigurationCommand            & cmd ) override;
    void handleCommandResult( const Commands::RfnTouHolidayConfigurationCommand          & cmd ) override;
    void handleCommandResult( const Commands::RfnGetOvUvAlarmConfigurationCommand        & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmProcessingStateCommand      & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatCountCommand          & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatIntervalCommand       & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvNewAlarmReportIntervalCommand    & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvSetOverVoltageThresholdCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvSetUnderVoltageThresholdCommand  & cmd ) override;
    void handleCommandResult( const Commands::RfnRemoteDisconnectConfigurationCommand    & cmd ) override;

private:

    bool disconnectConfigSupported() const;

    std::string getDisconnectModeString( Commands::RfnRemoteDisconnectConfigurationCommand::DisconnectMode disconnectMode );
    std::string getReconnectParamString( Commands::RfnRemoteDisconnectConfigurationCommand::Reconnect reconnectParam );

    bool isTouConfigCurrent( const Config::DeviceConfigSPtr &deviceConfig, std::map<std::string, std::string> &configMap );

public:
    RfnResidentialDevice() {};
};

typedef RfnResidentialDevice Rfn410fxDevice;
typedef RfnResidentialDevice Rfn410fdDevice;

typedef RfnResidentialDevice Rfn410clDevice;

}
}

