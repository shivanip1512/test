#pragma once

#include "dev_rfn.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_DemandFreeze.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_OvUvConfiguration.h"


namespace Cti       {
namespace Devices   {


class IM_EX_DEVDB RfnResidentialDevice
    :   public RfnDevice
{
protected:

    virtual ConfigMap getConfigMethods(bool readOnly);

    //int executeLoadProfileRecording              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeImmediateDemandFreeze             (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeTouCriticalPeak                   (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executePutConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executeGetConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeGetConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executePutValueTouReset                  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutValueTouResetZero              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executePutConfigDemandFreezeDay          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutConfigOvUv                     (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executeReadDemandFreezeInfo              (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeGetStatusTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeGetConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeGetConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeGetConfigOvUv                     (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executeGetConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeGetValueVoltageProfile            (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnVoltageProfileGetConfigurationCommand  & cmd );
    void handleCommandResult( const Commands::RfnVoltageProfileSetConfigurationCommand  & cmd );
    void handleCommandResult( const Commands::RfnLoadProfileGetRecordingCommand         & cmd );
    void handleCommandResult( const Commands::RfnGetDemandFreezeInfoCommand             & cmd );
    void handleCommandResult( const Commands::RfnDemandFreezeConfigurationCommand       & cmd );
    void handleCommandResult( const Commands::RfnTouScheduleGetConfigurationCommand     & cmd );
    void handleCommandResult( const Commands::RfnTouScheduleSetConfigurationCommand     & cmd );
    void handleCommandResult( const Commands::RfnTouHolidayConfigurationCommand         & cmd );
    void handleCommandResult( const Commands::RfnGetOvUvAlarmConfigurationCommand       & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvAlarmProcessingStateCommand     & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatCountCommand         & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatIntervalCommand      & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvNewAlarmReportIntervalCommand   & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvSetOverVoltageThresholdCommand  & cmd );
    void handleCommandResult( const Commands::RfnSetOvUvSetUnderVoltageThresholdCommand & cmd );

private:

    bool isTouConfigCurrent( const Config::DeviceConfigSPtr &deviceConfig, std::map<std::string, std::string> &configMap );
};

typedef RfnResidentialDevice Rfn410fxDevice;
typedef RfnResidentialDevice Rfn410fdDevice;

typedef RfnResidentialDevice Rfn410clDevice;

}
}

