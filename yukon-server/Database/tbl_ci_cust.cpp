#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_ci_cust.cpp
*
* Date:   4/2/2001
*
* Author: Corey G. Plender
*         Aaron Lauinger
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_ci_cust.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/02/10 23:23:47 $
*
* Copyright (c) 1999-2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dbaccess.h"
#include "logger.h"
#include "tbl_ci_cust.h"

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

RWCString CtiTableCICustomerBase::getTableName()
{
    return RWCString("CICustomerBase");
}

RWDBStatus CtiTableCICustomerBase::Restore()
{
    int contactid;
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    vector< int > contactIDs;

    RWDBStatus dbstat;

    {
        RWDBTable table = getDatabase().table( getTableName() );
        RWDBSelector selector = getDatabase().selector();

        selector << table["customerid"];

        selector.where( table["customerid"] == getID() );

        RWDBReader reader = selector.reader( conn );

        dbstat = selector.status();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
        }
    }

    //Must hit customer table now to get the primary contact id
    if(dbstat.errorCode() == RWDBStatus::ok)
      {
	RWDBDatabase db = getDatabase();
	RWDBTable table = db.table("Customer");
	RWDBSelector selector = db.selector();

	selector << table["primarycontactid"];
	selector.from(table);
	selector.where(table["customerid"] == getID());
	
	RWDBReader reader = selector.execute(conn).table().reader();
	dbstat = selector.status();
	
	while(reader())
	  {
	    reader["primarycontactid"] >> contactid;
	    contactIDs.push_back(contactid);
	  }
      }

    // Now hit the CustomerAdditionalContact table to get the rest of the contact ids
    if(dbstat.errorCode() == RWDBStatus::ok)
      {
	RWDBDatabase db = getDatabase();
	RWDBTable table = db.table("CustomerAdditionalContact");
	RWDBSelector selector = db.selector();

	selector << table["contactid"];
	selector.from(table);
	selector.where(table["customerid"] == getID());

	RWDBReader reader = selector.execute(conn).table().reader();
	dbstat = selector.status();

	while(reader()) 
	  {
	    reader["contactid"] >> contactid;
	    contactIDs.push_back(contactid);
	  }
      }

    // Hit the ContactNotification table to get all the notification ids
    // thats what were are going to squirrel away
    if(dbstat.errorCode() == RWDBStatus::ok) 
      {
	RWDBDatabase db = getDatabase();
	RWDBTable table = db.table("ContactNotification");
	RWDBSelector selector = db.selector();

	selector << table["contactnotifid"];
	selector.from(table);
	for(vector< int >::iterator iter = contactIDs.begin();
	    iter != contactIDs.end();
	    iter++ )
	  {
	    selector.where(selector.where() || table["contactid"] == *iter);
	  }

	RWDBReader reader = selector.execute(conn).table().reader();
	dbstat = selector.status();
	
	while(reader())
	  {
	    reader["contactnotifid"] >> contactid;
	    _contactNotificationIDs.insert(contactid);
	  }
      }

    return dbstat;
}

void CtiTableCICustomerBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table( getTableName() );

    selector <<
    keyTable["customerid"];

    selector.from(keyTable);
}

void CtiTableCICustomerBase::DecodeDatabaseReader(RWDBReader& rdr)
{
    RWCString temp;

    {
        rdr["customerid"] >> _id;
    }

    return;
}

size_t CtiTableCICustomerBase::entries() const
{
  return _contactNotificationIDs.size();
}

void CtiTableCICustomerBase::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << getID() << endl;
}

CtiTableCICustomerBase::INTSET CtiTableCICustomerBase::getContactNotificationSet() const 
{
  return _contactNotificationIDs;
}

void CtiTableCICustomerBase::dumpContactNotifications() const 
{
  try
    {
      CtiTableCICustomerBase::CONST_INTSETITERATOR iter;
      CtiLockGuard<CtiLogger> guard(dout);
      for(iter = _contactNotificationIDs.begin(); iter != _contactNotificationIDs.end(); iter++)
	{
	  dout << " ContactNotificationID " << *iter << endl;
	}
    }
  catch(...)
    {
      CtiLockGuard<CtiLogger> guard(dout);
      dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
      CtiLockGuard<CtiLogger> guard(dout);
      dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


