#pragma once

#include <string>

#include "cmdparse.h"

namespace Cti {
namespace Protocols {

class IM_EX_PROT XmlProtocol : public boost::noncopyable
{
public:

    XmlProtocol();

    static std::string createMessage(const CtiCommandParser &parse, const string &rawAscii, const std::vector<std::pair<string,string> > &params);

};

}
}

