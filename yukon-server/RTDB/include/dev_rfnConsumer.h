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
    static const InstallMap _putConfigInstallMap;
    static const InstallMap initPutConfigInstallMap();

    static const InstallMap _getConfigInstallMap;
    static const InstallMap initGetConfigInstallMap();

protected:

    virtual const InstallMap & getPutConfigInstallMap() const;
    virtual const InstallMap & getGetConfigInstallMap() const;

    int executeLoadProfileRecording              (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executeImmediateDemandFreeze             (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executeTouCriticalPeak                   (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    int executePutConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executePutConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    int executeGetConfigTou                      (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executeGetConfigHoliday                  (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    int executePutConfigDemandFreezeDay          (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executePutConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executePutConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    int executeReadDemandFreezeInfo              (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executeGetConfigInstallTou               (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    int executeGetConfigVoltageAveragingInterval (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    void handleResult( const Commands::RfnVoltageProfileConfigurationCommand & cmd );
    void handleResult( const Commands::RfnLoadProfileRecordingCommand & cmd );
    void handleResult( const Commands::RfnGetDemandFreezeInfoCommand & cmd );
    void handleResult( const Commands::RfnTouScheduleConfigurationCommand & cmd );
    void handleResult( const Commands::RfnTouHolidayConfigurationCommand & cmd );

private:

    bool isTouConfigCurrent( const Config::DeviceConfigSPtr &deviceConfig, std::map<std::string, std::string> &configMap );
};


}
}

