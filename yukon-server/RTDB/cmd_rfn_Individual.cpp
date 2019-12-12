#include "precompiled.h"

#include "cmd_rfn_Individual.h"

namespace Cti::Devices::Commands {


//  Delegate directly to the individual command decode method
RfnCommandResultList RfnTwoWayCommand::handleResponse(const CtiTime now, const RfnResponsePayload &response)
{
    try
    {
        RfnCommandResult result = decodeCommand(now, response);
        result.description = getCommandName() + ":\n" + result.description;
        return { result };
    }
    catch (YukonErrorException yee)
    {
        yee.error_description = getCommandName() + ":\n" + yee.error_description;
        throw yee;
    }
}

bool RfnTwoWayCommand::isOneWay() const
{
    return false;
}

RfnCommandResultList RfnOneWayCommand::handleResponse(const CtiTime now, const RfnResponsePayload &response)
{
    CTILOG_WARN(dout, "handleResponse called for one-way command: " << getCommandName());
    return {};
}

RfnCommandResult RfnOneWayCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    CTILOG_WARN(dout, "decodeCommand called for one-way command: " << getCommandName());
    return { "decodeCommand not valid for one-way command " + getCommandName(), ClientErrors::NoMethodForResultDecode };
}

bool RfnOneWayCommand::isOneWay() const
{
    return true;
}


//  Delegate directly to the individual command error method
RfnCommandResultList RfnIndividualCommand::handleError(const CtiTime now, const YukonError_t errorCode)
{
    return { error(now, errorCode) };
}

RfnCommandResult RfnIndividualCommand::error(const CtiTime now, const YukonError_t errorCode)
{
    return { getCommandName() + ":\n" + CtiError::GetErrorString(errorCode), errorCode };
}

double RfnIndividualCommand::adjustByScalingFactor(double value, double scalingFactor) const
{
    //  Temporary workaround for YUK-12814.
    //  The voltage UOM sent to C++ (deci, 1e-1) differs from that sent to Java (milli, 1e-3), resulting in ever-so-slightly different values for the same timestamp.
    //  The following code reenacts the 1e-3 UOM calc in order to make the bits match exactly.
    if ( scalingFactor == 1e-1 )
    {
        double temp = value * 1e2;

        return temp * 1e-3;
    }

    return value * scalingFactor;
}

// Convert type-length-value vector to a byte vector
RfnCommand::Bytes RfnIndividualCommand::getBytesFromTlvs( const std::vector<TypeLengthValue> &tlvs )
{
    Bytes tlvs_bytes;

    // the first byte correspond to the number of tlv items
    tlvs_bytes.push_back( tlvs.size() );

    for each( const TypeLengthValue &tlv in tlvs )
    {
        tlvs_bytes.push_back( tlv.type );

        const unsigned tlv_length  = tlv.value.size();

        if( tlv.isLongTlv )
        {
            tlvs_bytes.push_back( tlv_length >> 8 );
            tlvs_bytes.push_back( tlv_length & 0xff );
        }
        else
        {
            tlvs_bytes.push_back( tlv_length );
        }

        tlvs_bytes.insert( tlvs_bytes.end(), tlv.value.begin(), tlv.value.end() );
    }

    return tlvs_bytes;
}


std::vector<RfnIndividualCommand::TypeLengthValue> RfnIndividualCommand::getTlvsFromBytes( const Bytes &bytes )
{
    LongTlvList emptyList;

    return getTlvsFromBytes( bytes, emptyList );
}


std::vector<RfnIndividualCommand::TypeLengthValue> RfnIndividualCommand::getTlvsFromBytes( const Bytes &bytes, const LongTlvList &longTlvs )
{
    Bytes::const_iterator itr = bytes.begin();

    std::vector<TypeLengthValue> tlvs;

    if( itr == bytes.end() )
    {
        throw CommandException(ClientErrors::InvalidData, "Incomplete data for TLV");
    }

    unsigned count = *itr++;

    while( count-- )
    {
        if( itr == bytes.end() )
        {
            throw CommandException(ClientErrors::InvalidData, "Incomplete data for TLV");
        }

        //
        // TLV type
        //

        TypeLengthValue tlv(*itr++);

        //
        // TLV length
        //

        if( itr == bytes.end() )
        {
            throw CommandException(ClientErrors::InvalidData, "Incomplete data for TLV");
        }

        unsigned tlv_length = *itr++; // byte default we expect the tlv length field to be on 1 byte

        if( longTlvs.find( tlv.type ) != longTlvs.end() )
        {
            if( itr == bytes.end() )
            {
                throw CommandException(ClientErrors::InvalidData, "Incomplete data for TLV");
            }

            tlv.isLongTlv = true;

            tlv_length <<= 8;
            tlv_length |= *itr++;
        }

        //
        //  TLV data
        //

        while( tlv_length-- )
        {
            if( itr == bytes.end() )
            {
                throw CommandException(ClientErrors::InvalidData, "Incomplete data for TLV");
            }

            tlv.value.push_back(*itr++);
        }

        tlvs.push_back(tlv);
    }

    if( itr != bytes.end() )
    {
        CTILOG_WARN(dout, "TLVs contains residual bytes.");
    }

    return tlvs;
}


}