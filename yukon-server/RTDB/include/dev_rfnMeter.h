#pragma once

#include "dev_rfn.h"
#include "cmd_rfn_ChannelConfiguration.h"
#include "cmd_rfn_TemperatureAlarm.h"
#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/container/flat_map.hpp>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfnMeterDevice :
    public RfnDevice
{
protected:

    struct ConfigPart
    {
        static const std::string all;
        static const std::string freezeday;
        static const std::string tou;
        static const std::string voltageaveraging;
        static const std::string ovuv;
        static const std::string display;
        static const std::string disconnect;
        static const std::string temperaturealarm;
        static const std::string channelconfig;
        static const std::string voltageprofile;
        static const std::string demand;
    };

    typedef boost::container::flat_map<std::string, ConfigMethod> ConfigMap;

    enum class InstallType {
        PutConfig,
        GetConfig
    };

    /// Returns a map that contains the configuration methods.
    virtual ConfigMap getConfigMethods(InstallType installType);

    YukonError_t executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    virtual YukonError_t executeGetStatusWifi(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    virtual YukonError_t executeGetStatusTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    virtual YukonError_t executePutConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    virtual YukonError_t executePutValueTouReset    (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executePutValueTouResetZero(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    virtual YukonError_t executeGetConfigVoltageProfile (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executePutConfigVoltageProfile (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetValueVoltageProfile  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    virtual YukonError_t executePutConfigDisconnect (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetConfigDisconnect (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    virtual YukonError_t executePutConfigTemperatureAlarm(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetConfigTemperatureAlarm(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    YukonError_t executePutConfigInstallChannels          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executeGetConfigInstallChannels          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnConfigNotificationCommand & cmd ) override;
    void handleCommandResult( const Commands::RfnTemperatureAlarmCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnChannelSelectionCommand   & cmd ) override;
    void handleCommandResult( const Commands::RfnChannelIntervalRecording::GetConfigurationCommand       & cmd ) override;
    void handleCommandResult( const Commands::RfnChannelIntervalRecording::GetActiveConfigurationCommand & cmd ) override;
    void handleCommandResult( const Commands::RfnChannelIntervalRecording::SetConfigurationCommand       & cmd ) override;

private:
    typedef Commands::RfnChannelConfigurationCommand::MetricIds MetricIds;
    typedef std::vector<unsigned long> PaoMetricIds;

    YukonError_t executeGetConfigBehavior(const CtiRequestMsg& req, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executePutConfigBehavior(const CtiRequestMsg& req, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executePutConfigBehaviorRfnDataStreaming(const CtiRequestMsg& req, ReturnMsgList& returnMsgs, RfnIndividualCommandList& rfnRequests);

    YukonError_t executeConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests, InstallType installType);
    YukonError_t compareChannels(
        CtiRequestMsg    * pReq,
        CtiCommandParser & parse,
        ReturnMsgList    & returnMsgs,
        std::string prefix, 
        const PaoMetricIds &cfgMidnightMetrics, 
        const boost::optional<PaoMetricIds> &paoMidnightMetrics);

    void storeTemperatureConfig( const Commands::RfnTemperatureAlarmCommand::AlarmConfiguration & configuration );
    void storeChannelSelections( const Commands::RfnChannelConfigurationCommand::MetricIds & metricsReceived );
    void storeIntervalRecordingActiveConfiguration( const Commands::RfnChannelIntervalRecording::ActiveConfiguration & cmd );
};

typedef RfnMeterDevice Rfn410flDevice;  //  kWh only

}
}
