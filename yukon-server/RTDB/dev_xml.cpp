
#include "yukon.h"

#include "dev_xml.h"
#include "prot_xml.h"
#include "prot_base.h"
#include "tbl_dv_expresscom.h"
#include "xfer.h"

using namespace Cti;

CtiDeviceXml::CtiDeviceXml()
{
	xmlProtocol = NULL;
}

CtiDeviceXml::~CtiDeviceXml()
{
	if (xmlProtocol != NULL)
	{
		delete xmlProtocol;
		xmlProtocol = NULL;
	}
}

Cti::Protocol::Interface * CtiDeviceXml::getProtocol()
{
	return static_cast<Cti::Protocol::Interface*>(xmlProtocol);
}

/**
 * 
 * 
 * @param OutMessage 
 * 
 * @return int 
 */
int CtiDeviceXml::recvCommRequest(OUTMESS *OutMessage)
{
	int retVal = NoError;
	
	if (xmlProtocol != NULL)
	{
		delete xmlProtocol;
		xmlProtocol = NULL;
	}

	xmlProtocol = new Cti::Protocol::XmlProtocol();
	xmlProtocol->recvCommRequest(OutMessage);

	return retVal;
}

void CtiDeviceXml::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const
{
    Inherited::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("XML") && selector.where() );
}

void CtiDeviceXml::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

/*
int ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{

}
*/

