#pragma once

#include <dev_single.h>
#include "msg_rfnrequest.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfnDevice : public CtiDeviceSingle
{
public:

    typedef boost::ptr_vector<Messaging::RfnRequestMsg> RfnRequestMessages;

    virtual int ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests);

    struct RfnRequestExecuter : virtual RequestExecuter
    {
        RfnRequestMessages rfnRequests;

        RfnRequestExecuter(CtiRequestMsg *pReq_, CtiCommandParser &parse_) :
            RequestExecuter(pReq_, parse_)
        {}

        virtual int execute(RfnDevice &dev)
        {
            return dev.ExecuteRequest(pReq, parse, vgList, retList, rfnRequests);
        }
    };

    virtual int runExecuter(RequestExecuter &executer)
    {
        return executer.execute(*this);
    }

protected:

    virtual int executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests);
    virtual int executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests);
    virtual int executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests);
};

}
}


