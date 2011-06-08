#include "yukon.h"

#include "EncodingFilterFactory.h"
#include "encryption_lantronix.h"
#include "encryption_noop.h"

#include "port_udp.h"

using std::string;

namespace Cti {

//These need to change if the database change. Match the client
const string EncodingFilterFactory::NoFilterType = "none";
const string EncodingFilterFactory::LantronrixUdpAES = "AES";

/**
 *  Returns an Encoding Filter for the given port.
 */
EncodingFilterFactory::EncodingFilterSPtr EncodingFilterFactory::getEncodingFilter(const Ports::UdpPortSPtr &port)
{
    string encodingType = port->getEncodingType();

    if (!LantronrixUdpAES.compare(encodingType))
    {
        LantronixEncryptionImpl* filter = new LantronixEncryptionImpl();

        filter->setKey(port->getEncodingKey());

        return EncodingFilterSPtr(filter);
    }
    else
    {
        //return a no-op filter
        return EncodingFilterSPtr(new NoOpEncryption());
    }

}

}

