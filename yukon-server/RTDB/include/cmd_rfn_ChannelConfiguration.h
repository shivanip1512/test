#pragma once

#include <boost/container/flat_map.hpp>
#include "ctidate.h"
#include "cmd_rfn.h"

namespace Cti        {
namespace Devices    {
namespace Commands   {

/**
 * Channel Configuration Command
 */
class IM_EX_DEVDB RfnChannelConfigurationCommand : public RfnCommand
{
    Bytes getCommandData();

public:
    virtual ~RfnChannelConfigurationCommand()
    {};

    typedef std::set<std::string> MetricList;
    typedef std::set<unsigned>    MetricIdList;

    MetricList   getMetricsReceived() const;
    MetricIdList getMetricsIdsReceived() const;

    static MetricIdList resolveMetrics( const MetricList& metrics );

protected:
    RfnChannelConfigurationCommand()
    {};

    typedef std::vector<TypeLengthValue> TlvList;

    // contains list of metric ids for channel selection tlv type 1 or interval recording tlv type 1
    MetricList   _metricsReceived;
    MetricIdList _metricsIdsReceived;

    virtual TlvList       getTlvsToSend() const;
    virtual unsigned char getResponseCommandCode() const = 0;

    void decodeHeader             ( const Bytes &response, RfnCommandResult &result );
    void decodeMetricsIds         ( const Bytes &response, RfnCommandResult &result );
    void decodeChannelDescriptors ( const Bytes &response, RfnCommandResult &result );
};

/**
 * Channel Selection Configuration Command
 */
class IM_EX_DEVDB RfnChannelSelectionCommand : public RfnChannelConfigurationCommand
{
    enum
    {
        CommandCode_Request  = 0x78,
        CommandCode_Response = 0x79,
    };

    static const LongTlvList longTlvs;

    unsigned char getCommandCode() const;
    unsigned char getResponseCommandCode() const;

    void decodeTlvs( const TlvList& tlvs, RfnCommandResult &result );

    void decodeTlvChannelSelection( const Bytes &data, RfnCommandResult &result );

protected:
    enum
    {
        Operation_SetChannelSelectionConfiguration   = 0x00,
        Operation_GetChannelSelectionConfiguration   = 0x01,
        Operation_GetChannelSelectionFullDescription = 0x02
    };

    enum
    {
        TlvType_ChannelSelectionConfiguration   = 0x01,
        TlvType_ChannelSelectionFullDescription = 0x02
    };

    RfnChannelSelectionCommand()
    {};

public:
    void invokeResultHandler(RfnCommand::ResultHandler &rh) const;

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response );

    virtual ~RfnChannelSelectionCommand()
    {};
};

/**
 * Set Channel Selection Configuration Command
 */
class IM_EX_DEVDB RfnSetChannelSelectionCommand : public RfnChannelSelectionCommand
{
    Bytes _setChannelSelectionTlvPayload;

    TlvList       getTlvsToSend() const;
    unsigned char getOperation() const;

public:
    RfnSetChannelSelectionCommand( const MetricList& metrics );

};

/**
 * Get Channel Selection Configuration Command
 */
class IM_EX_DEVDB RfnGetChannelSelectionCommand : public RfnChannelSelectionCommand
{
    unsigned char getOperation() const;
};

/**
 * Get Full Channel Registration Description Command
 */
class IM_EX_DEVDB RfnGetChannelSelectionFullDescriptionCommand : public RfnChannelSelectionCommand
{
    unsigned char getOperation() const;
};

/**
 * Channel interval recording
 */
class IM_EX_DEVDB RfnChannelIntervalRecordingCommand : public RfnChannelConfigurationCommand
{
    enum
    {
        CommandCode_Request  = 0x7a,
        CommandCode_Response = 0x7b
    };

    unsigned _intervalRecordingSecondsReceived;
    unsigned _intervalReportingSecondsReceived;

    unsigned char getCommandCode() const;
    unsigned char getResponseCommandCode() const;

    void decodeTlvs( const TlvList& tlvs, RfnCommandResult &result );
    void decodeChannelIntervalRecording( const Bytes &response, RfnCommandResult &result );

protected:
    enum
    {
        Operation_SetChannelIntervalRecordingConfiguration = 0x00,
        Operation_GetChannelIntervalRecordingConfiguration = 0x01
    };

    enum
    {
        TlvType_ChannelIntervalRecordingConfiguration   = 0x01,
        TlvType_ChannelIntervalRecordingFullDescription = 0x02
    };

    RfnChannelIntervalRecordingCommand()
    {};

public:
    void invokeResultHandler(RfnCommand::ResultHandler &rh) const;

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response );

    virtual ~RfnChannelIntervalRecordingCommand()
    {};

    unsigned getIntervalRecordingSecondsReceived() const;
    unsigned getIntervalReportingSecondsReceived() const;
};

/**
 * Set channel interval recording
 */
class IM_EX_DEVDB RfnSetChannelIntervalRecordingCommand : public RfnChannelIntervalRecordingCommand
{
    Bytes _setIntervalRecordingTlvPayload;

    TlvList getTlvsToSend() const;

    unsigned char getOperation() const;

public:
    RfnSetChannelIntervalRecordingCommand( const MetricList& metrics,
                                           unsigned intervalRecordingSeconds,
                                           unsigned intervalReportingSeconds );
};

/**
 * Get channel interval recording
 */
class IM_EX_DEVDB RfnGetChannelIntervalRecordingCommand : public RfnChannelIntervalRecordingCommand
{
    unsigned char getOperation() const;
};


}
}
}
