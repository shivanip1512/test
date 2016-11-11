#include "precompiled.h"

#include "std_helper.h"
#include "RfnWaterNodeMessaging.h"



namespace Cti       {
namespace Messaging {
namespace Rfn       {

RfnSetChannelConfigRequestMessage::RfnSetChannelConfigRequestMessage()
{
    // empty
}

RfnSetChannelConfigReplyMessage::RfnSetChannelConfigReplyMessage( const int thriftCode )
    :   replyCode( thriftCode )
{
    // empty
}

std::string RfnSetChannelConfigReplyMessage::description() const
{
    static const std::map<int, std::string>  replyCodeLookup
    {
        { 0, "Success"          },
        { 1, "Invalid Device"   },
        { 2, "No Node"          },
        { 3, "No Gateway"       },
        { 4, "Failure"          }
    };

    return mapFindOrDefault( replyCodeLookup, replyCode, "Unknown Reply Code" );
}

RfnGetChannelConfigRequestMessage::RfnGetChannelConfigRequestMessage( const RfnIdentifier & rfnId )
    :   rfnIdentifier( rfnId )
{
    // empty
}

RfnGetChannelConfigReplyMessage::RfnGetChannelConfigReplyMessage()
{
    // empty
}

bool RfnGetChannelConfigReplyMessage::ChannelInfo::operator<( const RfnGetChannelConfigReplyMessage::ChannelInfo & rhs ) const
{
    if ( UOM < rhs.UOM )
        return true;
    if ( UOM > rhs.UOM )
        return false;
    if ( uomModifier < rhs.uomModifier )
        return true;
    if ( uomModifier > rhs.uomModifier )
        return false;
    if ( channelNumber < rhs.channelNumber )
        return true;
    if ( channelNumber > rhs.channelNumber )
        return false;

    return enabled;
}

std::string RfnGetChannelConfigReplyMessage::description() const
{
    static const std::map<int, std::string>  replyCodeLookup
    {
        { 0, "Success"          },
        { 1, "Invalid Device"   },
        { 2, "No Node"          },
        { 3, "Failure"          }
    };

    return mapFindOrDefault( replyCodeLookup, replyCode, "Unknown Reply Code" );
}

std::string RfnGetChannelConfigReplyMessage::to_string() const
{
    std::string output;

    output  +=    "RFN ID: "                + rfnIdentifier.toString()
            +   "\nReplyCode: ("            + std::to_string( replyCode ) + "): " + description()
            +   "\nTimestamp: "             + timestamp.asString()
            +   "\nRecording Interval: "    + std::to_string( recordingInterval )
            +   "\nReporting Interval: "    + std::to_string( reportingInterval );

    for ( const auto & info : channelInfo )
    {
        output  +=  "\n\tChannel Number: "  + std::to_string( info.channelNumber )
                +   "\n\tStatus: "          + ( info.enabled ? "Enabled" : "Disabled" )
                +   "\n\tUOM: "             + info.UOM
                +   "\n\tUOM Modifiers:";

        if ( info.uomModifier.empty() )
        {
            output  += "<empty>";
        }
        else
        {
            for ( const auto &modifier : info.uomModifier )
            {
                output  +=  "\n\t\t" + modifier;
            }
        }
    }

    return output;
}

}
}
}

