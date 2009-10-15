#pragma once

#include <string>
#include <boost/shared_ptr.hpp>
#include "EncodingFilter.h"
#include "port_udp.h"

namespace Cti {

class EncodingFilterFactory
{
private:
    static const std::string NoFilterType;
    static const std::string LantronrixUdpAES;

public:
    typedef boost::shared_ptr<EncodingFilter> EncodingFilterSPtr;

    static EncodingFilterSPtr getEncodingFilter(const Ports::UdpPortSPtr &port);
};

}

