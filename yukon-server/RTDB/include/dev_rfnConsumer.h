#pragma once

#include "dev_rfn.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_DemandFreeze.h"
#include "cmd_rfn_TouConfiguration.h"


namespace Cti       {
namespace Devices   {


class IM_EX_DEVDB RfnConsumerDevice
    :   public RfnDevice,
        public Commands::RfnVoltageProfileConfigurationCommand::ResultHandler,
        public Commands::RfnLoadProfileRecordingCommand::ResultHandler,
        public Commands::RfnGetDemandFreezeInfoCommand::ResultHandler

{
protected:

    int executePutConfigVoltageAveragingInterval( CtiRequestMsg     * pReq,
                                                  CtiCommandParser  & parse,
                                                  CtiMessageList    & retList,
                                                  RfnCommandList    & rfnRequests );

    int executeLoadProfileRecording( CtiRequestMsg     * pReq,
                                     CtiCommandParser  & parse,
                                     CtiMessageList    & retList,
                                     RfnCommandList    & rfnRequests );

    int executePutConfigDemandFreezeDay( CtiRequestMsg     * pReq,
                                         CtiCommandParser  & parse,
                                         CtiMessageList    & retList,
                                         RfnCommandList    & rfnRequests );

    int executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                      CtiCommandParser  & parse,
                                      CtiMessageList    & retList,
                                      RfnCommandList    & rfnRequests );

    int executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                     CtiCommandParser  & parse,
                                     CtiMessageList    & retList,
                                     RfnCommandList    & rfnRequests );

    int executeTouCriticalPeak( CtiRequestMsg     * pReq,
                                CtiCommandParser  & parse,
                                CtiMessageList    & retList,
                                RfnCommandList    & rfnRequests );


    void handleResult( const Commands::RfnVoltageProfileConfigurationCommand & cmd );
    void handleResult( const Commands::RfnLoadProfileRecordingCommand & cmd );

    void handleResult( const Commands::RfnGetDemandFreezeInfoCommand & cmd );
};


}
}

