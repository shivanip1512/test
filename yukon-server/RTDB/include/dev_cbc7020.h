#pragma once

#include "dev_dnp.h"
#include "config_data_cbc.h"
#include "config_device.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc7020Device : public DnpDevice
{
public:

    typedef std::vector<const char *> ConfigPartsList;

    //  we need to transform "control open" and "control close" into "control open offset 1" and "control close offset 1"
    //  also, we now need to allow configs to be sent out.
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    static void initOffsetAttributeMaps( std::map <int, PointAttribute> &analogOffsetAttribute,
                                          std::map <int, PointAttribute> &statusOffsetAttribute,
                                          std::map <int, PointAttribute> &accumulatorOffsetAttribute);
    //virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* >&vgList, std::list< CtiMessage* >&retList, std::list< OUTMESS* >&outList);

protected:

    enum
    {
        PointOffset_FirmwareRevision = 3,
    };

    virtual void processPoints( Cti::Protocol::Interface::pointlist_t &points );
    static void processFirmwarePoint( Cti::Protocol::Interface::pointlist_t &points );

private:

    typedef DnpDevice Inherited;

    static const ConfigPartsList _config_parts;
    static ConfigPartsList initConfigParts();

    int executePutConfigSingle        (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigVoltage       (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigCommsLost     (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigNeutralCurrent(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigFaultDetection(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigTimeAndTemp1  (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigTimeAndTemp2  (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigControlTimes  (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigDataLogging   (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    //int executePutConfigAddressing    (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigDNP           (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int executePutConfigUDP           (CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,std::list< CtiMessage* >&vgList,std::list< CtiMessage* >&retList,std::list< OUTMESS* >   &outList);
    int sendPutValueAnalog(int outputPt, double value, CtiRequestMsg *pReq, OUTMESS *&OutMessage, std::list< CtiMessage* >&vgList, std::list< CtiMessage* >&retList, std::list< OUTMESS* >   &outList);

    static const char *PutConfigPart_all;
    static const char *PutConfigPart_comms_lost;
    static const char *PutConfigPart_control_times;
    static const char *PutConfigPart_data_logging;
    static const char *PutConfigPart_dnp;
    static const char *PutConfigPart_fault_detection;
    static const char *PutConfigPart_neutral_current;
    static const char *PutConfigPart_time_temp_1;
    static const char *PutConfigPart_time_temp_2;
    static const char *PutConfigPart_udp;
    static const char *PutConfigPart_voltage;

public:
    enum OutputPoints
    {
        ActiveSettingsOffset            =  1,
        UVClosePointOffset              =  2,
        OVTripPointOffset               =  3,
        OVUVControlTriggerTimeOffset    =  4,
        AdaptiveVoltageHysteresisOffset = 20,
        AdaptiveVoltageFlagOffset       = 19,
        EmergencyUVPointOffset          =  7,
        EmergencyOVPointOffset          =  8,
        EmergencyVoltageTimeOffset      =  9,

        CommsLostUVClosePointOffset     = 58,
        CommsLostOVTripPointOffset      = 59,
        CommsLostTimeOffset             = 57,
        CommsLostActionOffset           = 56,

        FaultCurrentSetPointOffset      = 11,
        StateChangeSetPointOffset       = 14,
        NeutralCurrentRetryCountOffset  = -1,

        AI1AverageTimeOffset    =  85,
        AI2AverageTimeOffset    =  95,
        AI3AverageTimeOffset    = 105,
        AI1PeakSamplesOffset    = 112,
        AI2PeakSamplesOffset    = 114,
        AI3PeakSamplesOffset    = 116,
        AI1RatioThresholdOffset = 113,
        AI2RatioThresholdOffset = 115,
        AI3RatioThresholdOffset = 117,
        BatteryOnTimeOffset     = 118,

        Season1StartOffset              = 24,
        WeekdayTimedControlClose1Offset = 27,
        WeekendTimedControlClose1Offset = 30,
        WeekdayTimedControlTrip1Offset  = 28,
        WeekendTimedControlTrip1Offset  = 31,
        OffTimeState1Offset             = 29,
        TempMinThreshold1Offset         = 32,
        TempMinThresholdAction1Offset   = 33,
        TempMinHysterisis1Offset        = 34,
        TempMinThresholdTrigTime1Offset = 35,
        TempMaxThreshold1Offset         = 36,
        TempMaxThresholdAction1Offset   = 37,
        TempMaxHysterisis1Offset        = 38,
        TempMaxThresholdTrigTime1Offset = 39,

        Season2StartOffset              = 40,
        WeekdayTimedControlClose2Offset = 43,
        WeekendTimedControlClose2Offset = 45,
        WeekdayTimedControlTrip2Offset  = 44,
        WeekendTimedControlTrip2Offset  = 46,
        OffTimeState2Offset             = 47,
        TempMinThreshold2Offset         = 48,
        TempMinThresholdAction2Offset   = 49,
        TempMinHysterisis2Offset        = 50,
        TempMinThresholdTrigTime2Offset = 51,
        TempMaxThreshold2Offset         = 52,
        TempMaxThresholdAction2Offset   = 53,
        TempMaxHysterisis2Offset        = 54,
        TempMaxThresholdTrigTime2Offset = 55,

        ContactClosureTimeOffset        = 17,
        ManualControlDelayTripOffset    = 15,
        ManualControlDelayCloseOffset   = 16,
        RecloseDelayTimeOffset          = 18,

        DataLogFlagsOffset    = 22,
        LogTimeIntervalOffset = 23,

        LineVoltageDeadBandOffset  = 69,
        DeltaVoltageDeadBandOffset = 72,
        AnalogDeadBandOffset       = 73,

        RetryDelayOffset  = 110,
        PollTimeoutOffset = 111,
    };
};

}
}
