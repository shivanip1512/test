#include "precompiled.h"

#include "dev_base_lite.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;

namespace Cti {

DeviceBaseLite::DeviceBaseLite(LONG id) :
    _deviceID(id),
    _portID(0),
    _disableFlag(false),
    _controlInhibitFlag(false)
{
}

DeviceBaseLite::DeviceBaseLite(const DeviceBaseLite& aRef)
{
    *this = aRef;
}

DeviceBaseLite::~DeviceBaseLite()
{
}

LONG DeviceBaseLite::getID() const
{
    return _deviceID;
}

LONG DeviceBaseLite::getPortID() const
{
    return _portID;
}

string DeviceBaseLite::getName() const
{
    return _name;
}

string DeviceBaseLite::getObjectType() const
{
    return _objectType;
}

void DeviceBaseLite::setDisableFlag( const bool flag )
{
    _disableFlag = flag;
}
void DeviceBaseLite::setControlInhibitFlag( const bool flag )
{
    _controlInhibitFlag = flag;
}


bool DeviceBaseLite::operator<( const DeviceBaseLite &rhs ) const
{
    return(getID() < rhs.getID() );
}
bool DeviceBaseLite::operator==( const DeviceBaseLite &rhs ) const
{
    return(getID() == rhs.getID() );
}
bool DeviceBaseLite::operator()(const DeviceBaseLite& aRef) const
{
    return operator<(aRef);
}

string DeviceBaseLite::getSQLCoreStatement(long paoid)
{
    static const string sqlNoID = "SELECT YP.paobjectid, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                    "DV.controlinhibit "
                                  "FROM YukonPAObject YP "
                                  "JOIN Device DV ON YP.paobjectid = DV.deviceid";

    if( paoid )
    {
        return string(sqlNoID + " WHERE YP.paobjectid = ?");
    }
    else
    {
        return sqlNoID;
    }
}

void DeviceBaseLite::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    rdr["paobjectid"]  >> _deviceID;
    rdr["paoname"]     >> _name;
    rdr["type"]        >> _objectType;

    std::string tmp;

    rdr["paoclass"]    >> tmp;
    _isGroup = ciStringEqual(tmp, "group");

    rdr["disableflag"] >> tmp;
    _disableFlag = ciStringEqual(tmp, "Y");

    rdr["controlinhibit"] >> tmp;
    _controlInhibitFlag = ciStringEqual(tmp, "Y");
}

string DeviceBaseLite::getTableName()
{
    return string("YukonPAObject");
}

bool DeviceBaseLite::Restore()
{
    {
        static const string sql =  "SELECT YP.paobjectid, YP.paoclass, YP.paoname, YP.type, YP.description, "
                                       "YP.disableflag, DV.controlinhibit "
                                   "FROM YukonPAObject YP "
                                   "JOIN Device DV ON YP.paobjectid = DV.deviceid "
                                   "WHERE YP.paobjectid = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr << getID();

        rdr.execute();

        if( rdr() )
        {
            DecodeDatabaseReader( rdr );
            return true;
        }
    }
    return false;
}

DeviceBaseLite& DeviceBaseLite::operator=(const DeviceBaseLite& aRef)
{
    if(this != &aRef)
    {
        _deviceID = aRef.getID();
        _portID = aRef.getPortID();
        _name = aRef.getName();
        _objectType = aRef.getObjectType();
        _disableFlag = aRef.isDisabled();
        _controlInhibitFlag = aRef.isControlInhibited();
        _isGroup = aRef.isGroup();
    }
    return *this;
}

bool DeviceBaseLite::isDisabled() const
{
    return _disableFlag;
}

bool DeviceBaseLite::isControlInhibited() const
{
    return _controlInhibitFlag;
}

bool DeviceBaseLite::isGroup() const
{
    return _isGroup;
}

}

