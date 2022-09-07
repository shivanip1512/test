#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_OvUvConfiguration.h"


namespace Cti::Devices {

class IM_EX_DEVDB RfnResidentialVoltageDevice
    :   public RfnResidentialDevice
{
    void storeOvUvConfig( const Commands::RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration & config );
    void storeVoltageProfileConfig( const Commands::RfnVoltageProfileConfigurationCommand::Read & cmd );

protected:

    virtual ConfigMap getConfigMethods(InstallType installType);

    YukonError_t executeGetConfigVoltageAveragingInterval( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests );
    YukonError_t executePutConfigVoltageAveragingInterval( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests );

    void handleCommandResult( const Commands::RfnVoltageProfileGetConfigurationCommand & cmd ) override;
    void handleCommandResult( const Commands::RfnVoltageProfileSetConfigurationCommand & cmd ) override;

    YukonError_t executeGetConfigOvUv( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests );
    YukonError_t executePutConfigOvUv( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests );

    void handleCommandResult( const Commands::RfnGetOvUvAlarmConfigurationCommand       & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmProcessingStateCommand     & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatCountCommand         & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatIntervalCommand      & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvNewAlarmReportIntervalCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvSetOverVoltageThresholdCommand  & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvSetUnderVoltageThresholdCommand & cmd ) override;

    YukonError_t executeGetConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executePutConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetValueVoltageProfile            (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetConfigPermanentVoltageProfile  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executePutConfigPermanentVoltageProfile  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnConfigNotificationCommand               & cmd ) override;
    void handleCommandResult( const Commands::RfnLoadProfileGetRecordingCommand          & cmd ) override;
    void handleCommandResult( const Commands::RfnLoadProfileSetTemporaryRecordingCommand & cmd ) override;
    void handleCommandResult( const Commands::RfnLoadProfileSetPermanentRecordingCommand & cmd ) override;
};


typedef RfnResidentialVoltageDevice Rfn420fxDevice;
typedef RfnResidentialVoltageDevice Rfn420fdDevice;
typedef RfnResidentialVoltageDevice Rfn420frxDevice;
typedef RfnResidentialVoltageDevice Rfn420frdDevice;
//  RFN-500 Focus AX (gen 1)
typedef RfnResidentialVoltageDevice Rfn520faxDevice;
typedef RfnResidentialVoltageDevice Rfn520frxDevice;
typedef RfnResidentialVoltageDevice Rfn520faxdDevice;
typedef RfnResidentialVoltageDevice Rfn520frxdDevice;
typedef RfnResidentialVoltageDevice Rfn530faxDevice;
typedef RfnResidentialVoltageDevice Rfn530frxDevice;
//  RFN-500 Focus AXe (gen 2)
typedef RfnResidentialVoltageDevice Rfn520faxeDevice;
typedef RfnResidentialVoltageDevice Rfn520frxeDevice;
typedef RfnResidentialVoltageDevice Rfn520faxedDevice;
typedef RfnResidentialVoltageDevice Rfn520frxedDevice;
typedef RfnResidentialVoltageDevice Crl520faxeDevice;
typedef RfnResidentialVoltageDevice Crl520faxedDevice;
typedef RfnResidentialVoltageDevice Crl520frxeDevice;
typedef RfnResidentialVoltageDevice Crl520frxedDevice;
typedef RfnResidentialVoltageDevice Rfn530faxeDevice;
typedef RfnResidentialVoltageDevice Rfn530frxeDevice;

}