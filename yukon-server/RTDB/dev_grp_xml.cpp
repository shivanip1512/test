#include "yukon.h"

#include "dev_grp_xml.h"
#include "prot_xml.h"
#include "prot_base.h"
#include "tbl_dv_expresscom.h"
#include "xfer.h"

using namespace Cti;

CtiDeviceGroupXml::CtiDeviceGroupXml()
{

}

CtiDeviceGroupXml::~CtiDeviceGroupXml()
{

}

CtiDeviceGroupXml& CtiDeviceGroupXml::operator=(const CtiDeviceGroupXml& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        //_parameters = aRef.getParameters();
    }

    return *this;
}

std::vector<std::pair<string,string> > CtiDeviceGroupXml::getParameters()
{
    return _parameters;
}

void CtiDeviceGroupXml::setParameters( std::vector<std::pair<string,string> >& parameters )
{
    _parameters = std::vector<std::pair<string,string> >(parameters);
}

void CtiDeviceGroupXml::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const
{
    Inherited2::getSQL(db, keyTable, selector);
    CtiTableExpresscomLoadGroup::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("XML GROUP") && selector.where() );
}

void CtiDeviceGroupXml::getParametersSelector(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const
{
    RWDBTable tbl = db.table("LMGroupXMLParameter");
    selector << tbl["LMGroupXMLParameterId"]
             << tbl["parametername"]
             << tbl["parametervalue"];
    selector.from(tbl);

    selector.where( rwdbUpper(tbl["LMGroupXMLParameterId"]) == RWDBExpr(getID()) && selector.where() );
}


void CtiDeviceGroupXml::clearParameters()
{
    _parameters.clear();
}

void CtiDeviceGroupXml::decodeParameters(RWDBReader &rdr)
{
    string param;
    string value;

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    while(rdr())
    {
        //Decode properties here.
        rdr["parametername"] >> param;
        rdr["parametervalue"] >> value;

        _parameters.push_back(std::make_pair(param,value));
    }
}
