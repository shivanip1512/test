#pragma once

#include "dev_grp_expresscom.h"

#include "amq_connection.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB XmlGroupDevice : public CtiDeviceGroupExpresscom, boost::noncopyable
{
private:

    typedef CtiDeviceGroupExpresscom Inherited;

    void sendMessage(const string &queueName, const CtiCommandParser &parse, const string &rawAscii, const std::vector<std::pair<string,string> > &params);

    //ActiveMQConnectionManager *_amq;

    std::vector<std::pair<string,string> > _parameters;

protected:

    bool validateBuffer(const unsigned char *buf, int max_pos);

public:

    XmlGroupDevice();
    ~XmlGroupDevice();

    virtual string getSQLCoreStatement() const;

    virtual void decodeParameters(Cti::RowReader &rdr);

    virtual void clearParameters();

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
};

}
}

