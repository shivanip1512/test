#include "precompiled.h"

#include "dev_rfn.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Devices {

int RfnDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    typedef int (RfnDevice::*RfnExecuteMethod)(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests);

    typedef std::map<int, RfnExecuteMethod> ExecuteLookup;

    const ExecuteLookup executeMethods = boost::assign::map_list_of
        (PutConfigRequest, &RfnDevice::executePutConfig)
        (GetConfigRequest, &RfnDevice::executeGetConfig)
        (GetValueRequest,  &RfnDevice::executeGetValue);

    ExecuteLookup::const_iterator itr = executeMethods.find(parse.getCommand());

    if( itr == executeMethods.end() )
    {
        return NoMethod;
    }

    const RfnExecuteMethod executeMethod = itr->second;

    return (this->*executeMethod)(pReq, parse, vgList, retList, rfnRequests);
}

int RfnDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    if( const boost::optional<std::string> installValue = parse.findStringForKey("installvalue") )
    {
        typedef int (RfnDevice::*PutConfigMethod)(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests);

        typedef std::map<std::string, PutConfigMethod> PutConfigLookup;

        const PutConfigLookup putConfigMethods = boost::assign::map_list_of
            ("display",          &RfnDevice::executePutConfigDisplay)
            ("freezeday",        &RfnDevice::executePutConfigFreezeDay)
            ("voltageaveraging", &RfnDevice::executePutConfigVoltageAveragingInterval)
            ("tou",              &RfnDevice::executePutConfigTou);

        PutConfigLookup::const_iterator itr = putConfigMethods.find(*installValue);

        if( itr != putConfigMethods.end() )
        {
            PutConfigMethod method = itr->second;

            return (this->*method)(pReq, parse, vgList, retList, rfnRequests);
        }
    }

    return NoMethod;
}

int RfnDevice::executePutConfigFreezeDay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigVoltageAveragingInterval(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}


}
}
