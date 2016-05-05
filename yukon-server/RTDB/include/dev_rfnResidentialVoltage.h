#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_OvUvConfiguration.h"


namespace Cti       {
namespace Devices   {

class IM_EX_DEVDB RfnResidentialVoltageDevice
    :   public RfnResidentialDevice
{
protected:

    virtual ConfigMap getConfigMethods(bool readOnly);

    YukonError_t executeGetConfigVoltageAveragingInterval( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests );
    YukonError_t executePutConfigVoltageAveragingInterval( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests );

    void handleCommandResult( const Commands::RfnVoltageProfileGetConfigurationCommand & cmd ) override;
    void handleCommandResult( const Commands::RfnVoltageProfileSetConfigurationCommand & cmd ) override;

    YukonError_t executeGetConfigOvUv( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests );
    YukonError_t executePutConfigOvUv( CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests );

    void handleCommandResult( const Commands::RfnGetOvUvAlarmConfigurationCommand       & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmProcessingStateCommand     & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatCountCommand         & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatIntervalCommand      & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvNewAlarmReportIntervalCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvSetOverVoltageThresholdCommand  & cmd ) override;
    void handleCommandResult( const Commands::RfnSetOvUvSetUnderVoltageThresholdCommand & cmd ) override;

    YukonError_t executeGetConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executePutConfigVoltageProfile           (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeGetValueVoltageProfile            (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeGetConfigPermanentVoltageProfile  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigPermanentVoltageProfile  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnLoadProfileGetRecordingCommand          & cmd ) override;
    void handleCommandResult( const Commands::RfnLoadProfileSetTemporaryRecordingCommand & cmd ) override;
    void handleCommandResult( const Commands::RfnLoadProfileSetPermanentRecordingCommand & cmd ) override;

    bool NmCompatibilityAtLeast( const double minimumVersion );
    virtual double getNmCompatibilityVersion() const;

public:

    RfnResidentialVoltageDevice() {};
};


typedef RfnResidentialVoltageDevice Rfn420fxDevice;
typedef RfnResidentialVoltageDevice Rfn420fdDevice;
typedef RfnResidentialVoltageDevice Rfn420frxDevice;
typedef RfnResidentialVoltageDevice Rfn420frdDevice;
typedef RfnResidentialVoltageDevice Rfn520faxDevice;
typedef RfnResidentialVoltageDevice Rfn520frxDevice;
typedef RfnResidentialVoltageDevice Rfn520faxdDevice;
typedef RfnResidentialVoltageDevice Rfn520frxdDevice;

}   // Devices
}   // Cti

