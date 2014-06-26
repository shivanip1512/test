#pragma once

#include "dev_rfn.h"

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
    };

    typedef boost::function<int (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)> ConfigMethod;
    typedef boost::container::flat_map<std::string, ConfigMethod> ConfigMap;

    /// Helper function to bind configuration method.
    template<typename Function, typename Caller>
    inline static ConfigMethod bindConfigMethod(Function f, Caller c)
    {
        return boost::bind( f, c, _1, _2, _3, _4 );
    }

    /// Returns a map that contains the configuration methods.
    virtual ConfigMap getConfigMethods(bool readOnly);

    virtual int executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual int executeGetStatusTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual int executePutConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual int executePutValueTouReset    (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executePutValueTouResetZero(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual int executeGetConfigVoltageProfile (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executePutConfigVoltageProfile (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetValueVoltageProfile  (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual int executePutConfigDisconnect (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetConfigDisconnect (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual int executePutConfigTemperatureAlarm(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual int executeGetConfigTemperatureAlarm(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executePutConfigInstallChannels          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executeGetConfigInstallChannels          (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnSetTemperatureAlarmConfigurationCommand  & cmd );
    void handleCommandResult( const Commands::RfnGetTemperatureAlarmConfigurationCommand  & cmd );
    void handleCommandResult( const Commands::RfnChannelSelectionCommand  & cmd );
    void handleCommandResult( const Commands::RfnChannelIntervalRecording::GetConfigurationCommand       & cmd );
    void handleCommandResult( const Commands::RfnChannelIntervalRecording::GetActiveConfigurationCommand & cmd );
    void handleCommandResult( const Commands::RfnChannelIntervalRecording::SetConfigurationCommand       & cmd );

private:

    int  executeConfigInstall       (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, bool readOnly );
    void executeConfigInstallSingle (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, const std::string &configPart, const ConfigMethod &configMethod );
};

typedef RfnMeterDevice Rfn410flDevice;  //  kWh only

}
}
