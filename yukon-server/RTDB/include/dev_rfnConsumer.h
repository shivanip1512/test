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
        public Commands::RfnGetDemandFreezeInfoCommand::ResultHandler,
        public Commands::RfnTouScheduleConfigurationCommand::ResultHandler,
        public Commands::RfnTouHolidayConfigurationCommand::ResultHandler
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

    int executePutConfigTou( CtiRequestMsg     * pReq,
                             CtiCommandParser  & parse,
                             CtiMessageList    & retList,
                             RfnCommandList    & rfnRequests );

    int executeGetConfigTou( CtiRequestMsg     * pReq,
                             CtiCommandParser  & parse,
                             CtiMessageList    & retList,
                             RfnCommandList    & rfnRequests );

    int executePutConfigHoliday( CtiRequestMsg     * pReq,
                                 CtiCommandParser  & parse,
                                 CtiMessageList    & retList,
                                 RfnCommandList    & rfnRequests );

    int executeGetConfigHoliday( CtiRequestMsg     * pReq,
                                 CtiCommandParser  & parse,
                                 CtiMessageList    & retList,
                                 RfnCommandList    & rfnRequests );

    static const InstallMap _putConfigInstallMap;
    static const InstallMap _getConfigInstallMap;

    static const InstallMap initPutConfigInstallMap();
    static const InstallMap initGetConfigInstallMap();

    virtual InstallMap getPutConfigInstallMap() const;
    virtual InstallMap getGetConfigInstallMap() const;

    // execute putconfig install
    virtual int executePutConfigInstallFreezeDay                (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutConfigInstallVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutConfigInstallTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutConfigInstallDisplay                  (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    void handleResult( const Commands::RfnVoltageProfileConfigurationCommand & cmd );
    void handleResult( const Commands::RfnLoadProfileRecordingCommand & cmd );

    void handleResult( const Commands::RfnGetDemandFreezeInfoCommand & cmd );

    void handleResult( const Commands::RfnTouScheduleConfigurationCommand & cmd );
    void handleResult( const Commands::RfnTouHolidayConfigurationCommand & cmd );

private:

    bool isTouConfigCurrent();
};


}
}

