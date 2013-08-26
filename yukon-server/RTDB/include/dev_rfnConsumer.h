#pragma once

#include "dev_rfn.h"
#include "cmd_rfn_LoadProfile.h"


namespace Cti       {
namespace Devices   {


class IM_EX_DEVDB RfnConsumerDevice
    :   public RfnDevice,
        public Commands::RfnVoltageProfileConfigurationCommand::ResultHandler,
        public Commands::RfnLoadProfileRecordingCommand::ResultHandler
{
    int executePutConfigVoltageAveragingInterval( CtiRequestMsg     * pReq,
                                                  CtiCommandParser  & parse,
                                                  CtiMessageList    & retList,
                                                  RfnCommandList    & rfnRequests );

    int executeLoadProfileRecording( CtiRequestMsg     * pReq,
                                     CtiCommandParser  & parse,
                                     CtiMessageList    & retList,
                                     RfnCommandList    & rfnRequests );

    void handleResult( const Commands::RfnVoltageProfileConfigurationCommand & cmd );
    void handleResult( const Commands::RfnLoadProfileRecordingCommand & cmd );
};


}
}

