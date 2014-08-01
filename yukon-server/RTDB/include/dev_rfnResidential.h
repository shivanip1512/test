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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    RfnResidentialDevice(const RfnResidentialDevice&);
    RfnResidentialDevice& operator=(const RfnResidentialDevice&);

protected:

    virtual ConfigMap getConfigMethods(bool readOnly);

    //int executeLoadProfileRecording              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeImmediateDemandFreeze             (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeTouCriticalPeak                   (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executePutConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executeGetConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executePutValueTouReset                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutValueTouResetZero              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executePutConfigDemandFreezeDay          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigOvUv                     (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executeReadDemandFreezeInfo              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetStatusTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigOvUv                     (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executeGetConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetValueVoltageProfile            (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executePutConfigDisconnect               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigDisconnect               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnVoltageProfileGetConfigurationCommand            & cmd );
    void handleCommandResult( const Commands::RfnVoltageProfileSetConfigurationCommand            & cmd );
    void handleCommandResult( const Commands::RfnLoadProfileGetRecordingCommand                   & cmd );
    void handleCommandResult( const Commands::RfnGetDemandFreezeInfoCommand                       & cmd );
    void handleCommandResult( const Commands::RfnDemandFreezeConfigurationCommand                 & cmd );
    void handleCommandResult( const Commands::RfnTouScheduleGetConfigurationCommand               & cmd );
    void handleCommandResult( const Commands::RfnTouScheduleSetConfigurationCommand               & cmd );
    void handleCommandResult( const Commands::RfnTouStateConfigurationCommand                     & cmd );
    void handleCommandResult( const Commands::RfnTouHolidayConfigurationCommand                   & cmd );
    void handleCommandResult( const Commands::RfnGetOvUvAlarmConfigurationCommand                 & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvAlarmProcessingStateCommand               & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatCountCommand                   & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatIntervalCommand                & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvNewAlarmReportIntervalCommand             & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvSetOverVoltageThresholdCommand            & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvSetUnderVoltageThresholdCommand           & cmd );
    void handleCommandResult( const Commands::RfnRemoteDisconnectConfigurationCommand                          & cmd );

private:

    bool disconnectConfigSupported() const;

    std::string getDisconnectModeString( Commands::RfnRemoteDisconnectConfigurationCommand::DisconnectMode disconnectMode );
    std::string getReconnectParamString( Commands::RfnRemoteDisconnectConfigurationCommand::Reconnect reconnectParam );

    bool isTouConfigCurrent( const Config::DeviceConfigSPtr &deviceConfig, std::map<std::string, std::string> &configMap );

public:
    RfnResidentialDevice() {};
    virtual ~RfnResidentialDevice() {};
};

typedef RfnResidentialDevice Rfn410fxDevice;
typedef RfnResidentialDevice Rfn410fdDevice;

typedef RfnResidentialDevice Rfn410clDevice;

}
}

