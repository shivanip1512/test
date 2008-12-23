
#include "yukon.h"

#include "dev_xml.h"
#include "prot_xml.h"
#include "prot_base.h"
#include "tbl_dv_expresscom.h"
#include "xfer.h"

using namespace Cti;

CtiDeviceXml::CtiDeviceXml()
{
    _xmlProtocol.reset();
}

CtiDeviceXml::~CtiDeviceXml()
{
}

Cti::Protocol::Interface * CtiDeviceXml::getProtocol()
{
    return &_xmlProtocol;
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

    _xmlProtocol.reset();
    retVal = _xmlProtocol.recvCommRequest(OutMessage);

    return retVal;
}

void CtiDeviceXml::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const
{
    Inherited::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("XML") && selector.where() );

	std::cout << selector.asString();
}

void CtiDeviceXml::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    if ( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiDeviceXml::setParameters( std::vector< std::vector<string> >& params)
{
	_xmlProtocol.setParameters(params);
}
