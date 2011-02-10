#include "yukon.h"

#include "dev_grp_xml.h"
#include "prot_xml.h"
#include "prot_base.h"
#include "tbl_dv_expresscom.h"
#include "xfer.h"
#include "amq_connection.h"

namespace Cti {
namespace Devices {


XmlGroupDevice::XmlGroupDevice()
{

}

XmlGroupDevice::~XmlGroupDevice()
{

}

string XmlGroupDevice::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, ECA.routeid, ECA.serialnumber, "
                                     "ECA.serviceaddress, ECA.geoaddress, ECA.substationaddress, ECA.feederaddress, "
                                     "ECA.zipcodeaddress, ECA.udaddress, ECA.programaddress, ECA.splinteraddress, "
                                     "ECA.addressusage, ECA.relayusage, ECA.protocolpriority "
                                   "FROM YukonPAObject YP, Device DV, ExpressComAddress_View ECA "
                                   "WHERE upper (YP.type) = 'INTEGRATION GROUP' AND YP.paobjectid = ECA.lmgroupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void XmlGroupDevice::clearParameters()
{
    _parameters.clear();
}

void XmlGroupDevice::decodeParameters(Cti::RowReader &rdr)
{
    string param;
    string value;

    rdr["parametername"] >> param;
    rdr["parametervalue"] >> value;

    _parameters.push_back(std::make_pair(param,value));
}

void XmlGroupDevice::sendMessage(const string &queueName, const CtiCommandParser &parse, const string &rawAscii, const std::vector<std::pair<string,string> > &params)
{
    string textMessage = Protocols::XmlProtocol::createMessage(parse, rawAscii, params);

    Messaging::gActiveMQConnection.enqueueMessage(queueName, textMessage);
}

INT XmlGroupDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int status = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);

    if( !status )
    {
        //  rob the outlist and do with the OMs what you will
        list< OUTMESS* >::iterator list_itr = outList.begin();

        for( ; list_itr != outList.end(); ++list_itr )
        {
            if( *list_itr )
            {
                const CtiOutMessage &om = **list_itr;

                //  there are two nulls in the buffer before position 300 - so we can make two strings
                if( validateBuffer(om.Buffer.OutMessage, 300) )
                {
                    string queueName((const char *)om.Buffer.OutMessage);

                    string xcomData ((const char *)om.Buffer.OutMessage + queueName.size() + 1);

                    //  send the command string and XML parameters
                    sendMessage(queueName, parse, xcomData, _parameters);
                }
            }
        }

        //  delete all of the OMs
        delete_container(outList);

        outList.clear();
    }

    return status;
}

bool XmlGroupDevice::validateBuffer(const unsigned char *buf, int max_pos)
{
    int pos = 0;

    //  exit the loop when we find the first null
    //    note that the postincrement on buf[pos++] will increment nullpos even when we exit
    while( pos < max_pos && buf[pos++] != 0 )
        ;

    //  find the second null - the previous loop left nullpos pointing one element beyond the first null
    while( pos < max_pos && buf[pos++] != 0 )
        ;

    return pos < max_pos;
}


}
}
