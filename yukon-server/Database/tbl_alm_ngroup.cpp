#include "precompiled.h"

#include "row_reader.h"
#include "dbaccess.h"
#include "tbl_alm_ngroup.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"

using namespace std;

bool CtiTableNotificationGroup::operator<( const CtiTableNotificationGroup &rhs ) const
{
    return(getGroupID() < rhs.getGroupID());
}
bool CtiTableNotificationGroup::operator==( const CtiTableNotificationGroup &rhs ) const
{
    return(getGroupID() == rhs.getGroupID());
}
bool CtiTableNotificationGroup::operator()(const CtiTableNotificationGroup& aRef) const
{
    return operator<(aRef);
}

LONG CtiTableNotificationGroup::getGroupID() const
{

    return _notificationGroupID;
}
string CtiTableNotificationGroup::getGroupName() const
{

    return _groupName;
}

BOOL CtiTableNotificationGroup::isDisabled() const
{

    return _groupDisabled;
}

CtiTableNotificationGroup& CtiTableNotificationGroup::setGroupID( const LONG &aRef )
{

    _notificationGroupID = aRef;
    return *this;
}
CtiTableNotificationGroup& CtiTableNotificationGroup::setGroupName( const string &aStr )
{

    _groupName = aStr;
    return *this;
}

CtiTableNotificationGroup& CtiTableNotificationGroup::setDisabled( const BOOL b )
{

    _groupDisabled = b;
    return *this;
}

string CtiTableNotificationGroup::getTableName()
{
    return string("NotificationGroup");
}

bool CtiTableNotificationGroup::Restore()
{
    {
        static const string sql = "SELECT NG.notificationgroupid, NG.groupname, NG.disableflag "
                                  "FROM NotificationGroup NG "
                                  "WHERE NG.notificationgroupid = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader reader(connection, sql);

        reader << getGroupID();

        reader.execute();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
            return true;
        }
    }

    return false;
}

void CtiTableNotificationGroup::DecodeDatabaseReader(Cti::RowReader& rdr)
{
    string temp;

{

    rdr["notificationgroupid"] >> _notificationGroupID;
    rdr["groupname"]           >> _groupName;
    rdr["disableflag"]         >> temp;
}

std::transform(temp.begin(), temp.end(), temp.begin(), ::tolower);
setDisabled( (temp[0] == 'y') ? TRUE : FALSE );

setDirty(false);  // Not dirty anymore

return;
}

std::string CtiTableNotificationGroup::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableNotificationGroup";
    itemList.add("Group ID")    << getGroupID();
    itemList.add("Group Name")  << getGroupName();
    itemList.add("is Disabled") << isDisabled();

    return itemList.toString();
}

CtiTableNotificationGroup& CtiTableNotificationGroup::setDirty( bool dirt )
{
    _isDirty = dirt;
    return *this;
}

bool CtiTableNotificationGroup::isDirty() const
{


    return _isDirty;
}

CtiTableNotificationGroup::CtiTableNotificationGroup( LONG gid) :
    _isDirty(true),
    _notificationGroupID(gid),
    _groupDisabled(FALSE)
{}

CtiTableNotificationGroup::CtiTableNotificationGroup(const CtiTableNotificationGroup& aRef)
{
    *this = aRef;
}

CtiTableNotificationGroup::~CtiTableNotificationGroup() {}

CtiTableNotificationGroup& CtiTableNotificationGroup::operator=(const CtiTableNotificationGroup& aRef)
{
    if(this != &aRef)
    {
        setGroupID( aRef.getGroupID() );
        setGroupName( aRef.getGroupName() );
        setDisabled( aRef.isDisabled() );

        setDirty(aRef.isDirty());
    }

    return *this;
}
