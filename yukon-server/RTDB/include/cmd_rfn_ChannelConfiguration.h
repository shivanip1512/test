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

    typedef std::set<unsigned> MetricIds;

    MetricIds getMetricsReceived() const;

protected:

    RfnChannelConfigurationCommand()
    {};

    typedef std::vector<TypeLengthValue> TlvList;

    // contains list of metrics for channel selection tlv type 1 or interval recording tlv type 1
    MetricIds _metricsReceived;

    virtual TlvList       getTlvsToSend() const;
    virtual unsigned char getResponseCommandCode() const = 0;

    void decodeHeader             ( const Bytes &response, RfnCommandResult &result );
    void decodeMetricsIds         ( const Bytes &response, RfnCommandResult &result );
    void decodeChannelDescriptors ( const Bytes &response, RfnCommandResult &result );
};

/**
 * Channel Selection Configuration Command
 */
class IM_EX_DEVDB RfnChannelSelectionCommand : public RfnChannelConfigurationCommand,
       InvokerFor<RfnChannelSelectionCommand>
{
    enum
    {
        CommandCode_Request  = 0x78,
        CommandCode_Response = 0x79,
    };

    static const LongTlvList longTlvs;

    unsigned char getCommandCode() const;
    unsigned char getResponseCommandCode() const;

    void decodeTlvs( const TlvList& tlvs, RfnCommandResult &result, const unsigned char tlv_expected );

    void decodeTlvChannelSelection( const Bytes &data, RfnCommandResult &result );

protected:
    enum
    {
        Operation_SetChannelSelectionConfiguration  = 0x00,
        Operation_GetChannelSelectionConfiguration  = 0x01,
        Operation_GetChannelSelectionActiveChannels = 0x02
    };

    enum
    {
        TlvType_ChannelSelection_Configuration  = 0x01,
        TlvType_ChannelSelection_ActiveChannels = 0x02
    };

    RfnChannelSelectionCommand() = default;

    virtual unsigned char getExpectedTlvType() const = 0;

public:
    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response );
};

/**
 * Set Channel Selection Configuration Command
 */
class IM_EX_DEVDB RfnSetChannelSelectionCommand : public RfnChannelSelectionCommand
{
    Bytes _setChannelSelectionTlvPayload;

    TlvList       getTlvsToSend() const;
    unsigned char getOperation() const;

    virtual unsigned char getExpectedTlvType() const;

public:
    RfnSetChannelSelectionCommand( const MetricIds& metrics );

};

/**
 * Get Channel Selection Configuration Command
 */
class IM_EX_DEVDB RfnGetChannelSelectionCommand : public RfnChannelSelectionCommand
{
    unsigned char getOperation() const;

    virtual unsigned char getExpectedTlvType() const;
};

/**
 * Get Full Channel Registration Description Command
 */
class IM_EX_DEVDB RfnGetChannelSelectionFullDescriptionCommand : public RfnChannelSelectionCommand
{
    unsigned char getOperation() const;

    virtual unsigned char getExpectedTlvType() const;
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

    unsigned char getCommandCode() const;
    unsigned char getResponseCommandCode() const;

    virtual void decodeTlv( const TypeLengthValue& tlv, RfnCommandResult &result ) = 0;

protected:
    enum
    {
        Operation_SetChannelIntervalRecordingConfiguration  = 0x00,
        Operation_GetChannelIntervalRecordingConfiguration  = 0x01,
        Operation_GetChannelIntervalRecordingActiveConfiguration = 0x02
    };

    enum
    {
        TlvType_ChannelIntervalRecording_Configuration       = 0x01,
        TlvType_ChannelIntervalRecording_ActiveChannels      = 0x02,
        TlvType_ChannelIntervalRecording_ActiveConfiguration = 0x03
    };

    RfnChannelIntervalRecordingCommand()
    {};

public:
    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response );
};

namespace RfnChannelIntervalRecording {

/**
 * Set channel interval recording channels and intervals
 */
class IM_EX_DEVDB SetConfigurationCommand : public RfnChannelIntervalRecordingCommand,
       InvokerFor<SetConfigurationCommand>
{
    Bytes _setIntervalRecordingTlvPayload;

    TlvList getTlvsToSend() const;

    unsigned char getOperation() const;

    virtual void decodeTlv( const TypeLengthValue& tlv, RfnCommandResult &result );

    const unsigned _intervalRecordingSeconds,
                   _intervalReportingSeconds;

public:
    SetConfigurationCommand( const MetricIds& metrics,
                             unsigned intervalRecordingSeconds,
                             unsigned intervalReportingSeconds );

    unsigned getIntervalRecordingSeconds() const;
    unsigned getIntervalReportingSeconds() const;
};

/**
 * Get channel interval recording channel and interval
 * configuration
 */
class IM_EX_DEVDB GetConfigurationCommand : public RfnChannelIntervalRecordingCommand,
       InvokerFor<GetConfigurationCommand>
{
    unsigned char getOperation() const;

    virtual void decodeTlv( const TypeLengthValue& tlv, RfnCommandResult &result );

    void decodeChannelIntervalRecording( const Bytes &response, RfnCommandResult &result );

    unsigned _intervalRecordingSecondsReceived;
    unsigned _intervalReportingSecondsReceived;

public:
    unsigned getIntervalRecordingSecondsReceived() const;
    unsigned getIntervalReportingSecondsReceived() const;
};

/**
 * Get actual active channels
 */
class IM_EX_DEVDB GetActiveConfigurationCommand : public RfnChannelIntervalRecordingCommand,
       InvokerFor<GetActiveConfigurationCommand>
{
    unsigned char getOperation() const;

    virtual void decodeTlv( const TypeLengthValue& tlv, RfnCommandResult &result );

    void decodeActiveConfiguration( const Bytes &response, RfnCommandResult &result );

    unsigned _intervalRecordingSecondsReceived;
    unsigned _intervalReportingSecondsReceived;

public:
    unsigned getIntervalRecordingSecondsReceived() const;
    unsigned getIntervalReportingSecondsReceived() const;
};

}

}
}
}
