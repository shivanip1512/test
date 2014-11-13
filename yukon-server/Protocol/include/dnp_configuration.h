#pragma once

namespace Cti {
namespace Protocols {
namespace DNP {

struct config_data
{
    config_data( const unsigned internalRetries_,
                 const bool     useLocalTime_,
                 const bool     enableDnpTimesyncs_,
                 const bool     omitTimeRequest_,
                 const bool     enableUnsolicitedClass1_,
                 const bool     enableUnsolicitedClass2_,
                 const bool     enableUnsolicitedClass3_ )
    :
        internalRetries( internalRetries_ ),
        useLocalTime( useLocalTime_ ),
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

    const unsigned internalRetries;
    const bool     useLocalTime;
    const bool     enableDnpTimesyncs;
    const bool     omitTimeRequest;
    const bool     enableUnsolicitedClass1;
    const bool     enableUnsolicitedClass2;
    const bool     enableUnsolicitedClass3;

private:
    config_data();
};

}
}
}
