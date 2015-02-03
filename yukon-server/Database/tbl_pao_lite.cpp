#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"
#include "tbl_pao_lite.h"
#include "database_connection.h"
#include "database_reader.h"

using std::transform;
using std::string;
using std::endl;

CtiTblPAOLite::CtiTblPAOLite() :
    _paObjectID(-1),
    _class(0),
    _type(0),
    _disableFlag(false)
{}

CtiTblPAOLite::~CtiTblPAOLite() {}

LONG CtiTblPAOLite::getID() const
{
    return _paObjectID;
}

CtiTblPAOLite& CtiTblPAOLite::setID( LONG paObjectID )
{
    _paObjectID = paObjectID;
    return *this;
}

INT CtiTblPAOLite::getClass() const
{
    return _class;
}

string CtiTblPAOLite::getName() const
{
    return _name;
}

INT CtiTblPAOLite::getType() const
{
    return _type;
}

CtiTblPAOLite& CtiTblPAOLite::setType( const INT &type )
{
    _type = type;
    return *this;
}

string CtiTblPAOLite::getTableName()
{
    return "YukonPAObject";
}

void CtiTblPAOLite::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;
    string tmpStr, category;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["paobjectid"] >> _paObjectID;
    rdr >> category;
    rdr >> tmpStr;

    _class = resolvePAOClass(tmpStr);

    rdr >> _name;
    rdr >> tmpStr; // type

    _type = resolvePAOType(category, tmpStr);

    rdr >> tmpStr;
    transform(tmpStr.begin(), tmpStr.end(), tmpStr.begin(), tolower);
    _disableFlag = ((tmpStr == "y") ? true : false);
}

std::string CtiTblPAOLite::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTblPAOLite";
    itemList.add("PAO Id") << _paObjectID;
    itemList.add("Name")   << _name;
    itemList.add("Class")  << _class;
    itemList.add("Type")   << _type;

    return itemList.toString();
}
