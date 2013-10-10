#include "precompiled.h"

#include "std_helper.h"
#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {


void RfnCommand::invokeResultHandler(ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}

// Construct a byte vector request
RfnCommand::RfnRequestPayload RfnCommand::executeCommand(const CtiTime now)
{
    RfnRequestPayload req;

    prepareCommandData(now);

    Bytes header = getCommandHeader();

    req.insert(req.end(), header.begin(), header.end());

    Bytes data = getCommandData();

    req.insert(req.end(), data.begin(), data.end());

    return req;
}

// Return the error description
RfnCommandResult RfnCommand::error(const CtiTime now, const YukonError_t errorCode)
{
    RfnCommandResult result;

    result.description = GetErrorString(errorCode);

    return result;
}

// Default header for RFN messages
RfnCommand::Bytes RfnCommand::getCommandHeader()
{
    Bytes   header;

    header.push_back(getCommandCode());
    header.push_back(getOperation());

    return header;
}

// Defaults to Advanced Metrology
unsigned char RfnCommand::getApplicationServiceId() const
{
    return 0x09;
}

// Convert type-length-value vector to a byte vector
RfnCommand::Bytes RfnCommand::getBytesFromTlvs( const std::vector<TypeLengthValue> &tlvs )
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


std::vector<RfnCommand::TypeLengthValue> RfnCommand::getTlvsFromBytes( const Bytes &bytes )
{
    LongTlvList emptyList;

    return getTlvsFromBytes( bytes, emptyList );
}


std::vector<RfnCommand::TypeLengthValue> RfnCommand::getTlvsFromBytes( const Bytes &bytes, const LongTlvList &longTlvs )
{
    Bytes::const_iterator itr = bytes.begin();

    std::vector<TypeLengthValue> tlvs;

    if( itr == bytes.end() )
    {
        throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
    }

    unsigned count = *itr++;

    while( count-- )
    {
        if( itr == bytes.end() )
        {
            throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
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
            throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
        }

        unsigned tlv_length = *itr++; // byte default we expect the tlv length field to be on 1 byte

        if( longTlvs.find( tlv.type ) != longTlvs.end() )
        {
            if( itr == bytes.end() )
            {
                throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
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
                throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
            }

            tlv.value.push_back(*itr++);
        }

        tlvs.push_back(tlv);
    }

    if( itr != bytes.end() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " - TLVs contains residual bytes. " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
    }

    return tlvs;
}


}
}
}
