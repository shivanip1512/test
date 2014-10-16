#include "precompiled.h"

#include "database_connection.h"
#include "database_reader.h"
#include "dbaccess.h"
#include "logger.h"
#include "tbl_ci_cust.h"


using namespace std;

bool CtiTableCICustomerBase::operator<( const CtiTableCICustomerBase &rhs ) const
{
    return(getID() < rhs.getID());
}

bool CtiTableCICustomerBase::operator==( const CtiTableCICustomerBase &rhs ) const
{
    return(getID() == rhs.getID());
}

bool CtiTableCICustomerBase::operator()(const CtiTableCICustomerBase& aRef) const
{
    return operator<(aRef);
}

LONG CtiTableCICustomerBase::getID() const
{
    return _id;
}

CtiTableCICustomerBase& CtiTableCICustomerBase::setID( const LONG &aRef )
{
    _id = aRef;
    return *this;
}

string CtiTableCICustomerBase::getTableName()
{
    return string("CICustomerBase");
}

bool CtiTableCICustomerBase::Restore()
{
    int contactid;
    vector< int > contactIDs;

    bool retVal = true;

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection);

    {
        static const string sql =  "SELECT CMB.customerid "
                                   "FROM CICustomerBase CMB "
                                   "WHERE CMB.customerid = ?";

        reader.setCommandText(sql);

        reader << getID();

        reader.execute();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
        }
        else
        {
            retVal = false;
        }
    }

    //Must hit customer table now to get the primary contact id
    if( retVal )
    {
        static const string sql =  "SELECT CST.primarycontactid "
                                   "FROM Customer CST "
                                   "WHERE CST.customerid = ?";

        reader.setCommandText(sql);

        reader << getID();

        reader.execute();

        retVal = false;
        while( reader() )
        {
            retVal = true;
            reader["primarycontactid"] >> contactid;
            contactIDs.push_back(contactid);
        }
    }

    // Now hit the CustomerAdditionalContact table to get the rest of the contact ids
    if( retVal )
    {
        static const string sql =  "SELECT CAD.contactid "
                                   "FROM CustomerAdditionalContact CAD "
                                   "WHERE CAD.customerid = ?";

        reader.setCommandText(sql);

        reader << getID();

        reader.execute();

        retVal = false;
        while( reader() )
        {
            retVal = true;
            reader["contactid"] >> contactid;
            contactIDs.push_back(contactid);
        }
    }

    // Hit the ContactNotification table to get all the notification ids
    // thats what were are going to squirrel away
    if( retVal )
    {
        std::stringstream ss;

        static const string sql = "SELECT CNF.contactnotifid "
                                  "FROM ContactNotification CNF";

        ss << sql;

        if( !contactIDs.empty() )
        {
            ss << " WHERE";
        }

        for( vector< int >::iterator iter = contactIDs.begin(); iter != contactIDs.end(); iter++ )
        {
            if(iter != contactIDs.begin())
            {
                    ss << " OR";
            }
            ss << " CNF.contactid = " << *iter;
        }

        reader.setCommandText(ss.str());
        reader.execute();

        retVal = false;
        while( reader() )
        {
            retVal = true;
            reader["contactnotifid"] >> contactid;
            _contactNotificationIDs.insert(contactid);
        }
    }

    return retVal;
}

void CtiTableCICustomerBase::DecodeDatabaseReader(Cti::RowReader& rdr)
{
    string temp;

    {
        rdr["customerid"] >> _id;
    }

    return;
}

size_t CtiTableCICustomerBase::entries() const
{
  return _contactNotificationIDs.size();
}

std::string CtiTableCICustomerBase::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableCICustomerBase";
    itemList.add("customer id") << getID();

    return itemList.toString();
}

CtiTableCICustomerBase::INTSET CtiTableCICustomerBase::getContactNotificationSet() const
{
  return _contactNotificationIDs;
}

void CtiTableCICustomerBase::dumpContactNotifications() const
{
    try
    {
        Cti::FormattedList itemList;

        CtiTableCICustomerBase::CONST_INTSETITERATOR iter;

        for(iter = _contactNotificationIDs.begin(); iter != _contactNotificationIDs.end(); iter++)
        {
            itemList.add("ContactNotificationID") << *iter;
        }

        CTILOG_INFO(dout, itemList);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

CtiTableCICustomerBase& CtiTableCICustomerBase::setContactNotificationSet(const INTSET& rhs)
{
  _contactNotificationIDs = rhs;
  return *this;
}

vector<int> CtiTableCICustomerBase::getContactNotificationVector() const
{
    vector<int> n_vec;

    try
    {
        CtiTableCICustomerBase::CONST_INTSETITERATOR iter;

        for(iter = _contactNotificationIDs.begin(); iter != _contactNotificationIDs.end(); iter++)
        {
            n_vec.push_back(*iter);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return n_vec;
}
//            set<int>::const_reference locID = *iter;


CtiTableCICustomerBase::CtiTableCICustomerBase(LONG id) :
_id(id)
{}

CtiTableCICustomerBase::CtiTableCICustomerBase(const CtiTableCICustomerBase& aRef)
{
    if(this != &aRef)
    {
      *this = aRef;
    }
}

CtiTableCICustomerBase::~CtiTableCICustomerBase() {}

CtiTableCICustomerBase& CtiTableCICustomerBase::operator=(const CtiTableCICustomerBase& aRef)
{
    if(this != &aRef)
    {
        setID(aRef.getID());
    setContactNotificationSet(aRef.getContactNotificationSet());
    }
    return *this;
}


