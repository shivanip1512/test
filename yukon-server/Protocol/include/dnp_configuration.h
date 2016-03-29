#pragma once

namespace Cti {
namespace Protocols {
namespace DNP {

enum class TimeOffset {
    Utc,
    Local,
    LocalStandard
};

struct config_data
{
    config_data( const unsigned internalRetries_,
                 const TimeOffset timeOffset_,
                 const bool       enableDnpTimesyncs_,
                 const bool       omitTimeRequest_,
                 const bool       enableUnsolicitedClass1_,
                 const bool       enableUnsolicitedClass2_,
                 const bool       enableUnsolicitedClass3_ )
    :
        internalRetries( internalRetries_ ),
        timeOffset( timeOffset_ ),
        enableDnpTimesyncs( enableDnpTimesyncs_ ),
        omitTimeRequest( omitTimeRequest_ ),
        enableUnsolicitedClass1( enableUnsolicitedClass1_ ),
        enableUnsolicitedClass2( enableUnsolicitedClass2_ ),
        enableUnsolicitedClass3( enableUnsolicitedClass3_ )
    {
    }

    bool isAnyUnsolicitedEnabled() const
    {
        return enableUnsolicitedClass1
                || enableUnsolicitedClass2
                || enableUnsolicitedClass3;
    }

    const unsigned   internalRetries;
    const TimeOffset timeOffset;
    const bool       enableDnpTimesyncs;
    const bool       omitTimeRequest;
    const bool       enableUnsolicitedClass1;
    const bool       enableUnsolicitedClass2;
    const bool       enableUnsolicitedClass3;

private:
    config_data();
};

}
}
}
