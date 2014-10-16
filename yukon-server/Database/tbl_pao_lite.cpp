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
    string rwsTemp, category;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["paobjectid"] >> _paObjectID;
    rdr >> category;
    rdr >> rwsTemp;

    _class = resolvePAOClass(rwsTemp);

    rdr >> _name;
    rdr >> rwsTemp; // type

    _type = resolvePAOType(category, rwsTemp);

    rdr >> rwsTemp;
    transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    _disableFlag = ((rwsTemp == "y") ? true : false);
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
