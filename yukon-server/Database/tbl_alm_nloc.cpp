#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_nloc
*
* Date:   6/26/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_alm_nloc.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:47 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dbaccess.h"
#include "dllbase.h"
#include "tbl_alm_nloc.h"
#include "logger.h"


LONG CtiTableGroupRecipient::getRecipientID() const
{

    return _recipientID;
}

const RWCString& CtiTableGroupRecipient::getRecipientName() const
{

    return _recipientName;
}

const RWCString& CtiTableGroupRecipient::getEmailAddress() const
{

    return _emailAddress;
}

INT CtiTableGroupRecipient::getEmailSendType() const
{

    return _emailSendType;
}

RWCString CtiTableGroupRecipient::getRecipientType() const
{

    return _recipientType;
}

const RWCString& CtiTableGroupRecipient::getPagerNumber() const
{

    return _pagerNumber;
}

bool CtiTableGroupRecipient::isDisabled() const
{

    return _disabled;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setRecipientID(LONG id)
{

    _recipientID = id;
    return *this;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setRecipientName(const RWCString &str)
{

    _recipientName = str;
    return *this;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setEmailAddress(const RWCString &str)
{

    _emailAddress = str;
    return *this;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setEmailSendType(INT type)
{

    _emailSendType = type;
    return *this;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setRecipientType(RWCString type)
{

    _recipientType = type;
    return *this;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setPagerNumber(const RWCString &str)
{

    _pagerNumber = str;
    return *this;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setDisabled(bool b)
{

    _disabled = b;
    return *this;
}

CtiTableGroupRecipient& CtiTableGroupRecipient::setDirty( bool dirt )
{

    _isDirty = dirt;
    return *this;
}

bool CtiTableGroupRecipient::isDirty() const
{

    return _isDirty;
}

void CtiTableGroupRecipient::dump() const
{

    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << "Recipient ID: " << getRecipientID() << endl;
    dout << "Recipient Name: " << getRecipientName() << endl;
    dout << "Email Addr: " << getEmailAddress() << endl;
    dout << "Email Type: " << getEmailSendType() << endl;
    dout << "Pager Number: " << getPagerNumber() << endl;
    dout << "Disabled: " << isDisabled() << endl;
    dout << "Recipient Type: " << getRecipientType() << endl;

}

void CtiTableGroupRecipient::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table( getTableName() );

    selector <<
    keyTable["recipientid"] <<
    keyTable["recipientname"] <<
    keyTable["emailaddress"] <<
    keyTable["emailsendtype"] <<
    keyTable["pagernumber"] <<
    keyTable["disableflag"] <<
    keyTable["recipienttype"];

    selector.from(keyTable);
}

RWCString CtiTableGroupRecipient::getTableName()
{
    return RWCString("NotificationRecipient");
}
RWDBStatus CtiTableGroupRecipient::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();



    inserter <<
    getRecipientID() <<
    getRecipientName() <<
    getEmailAddress() <<
    getEmailSendType() <<
    getPagerNumber() <<
    RWCString( ( isDisabled() ? 'Y': 'N' ) ) <<
    getRecipientType();

    inserter.execute( conn );

    return inserter.status();
}
RWDBStatus CtiTableGroupRecipient::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();



    updater.where( table["recipientid"] == getRecipientID() );

    updater <<
    table["recipientname"].assign( getRecipientName() ) <<
    table["emailaddress"].assign( getEmailAddress() ) <<
    table["emailsendtype"].assign( getEmailSendType() ) <<
    table["pagernumber"].assign( getPagerNumber() ) <<
    table["disableflag"].assign( RWCString( ( isDisabled() ? 'Y': 'N' ) ) ) <<
    table["recipienttype"].assign( getRecipientType() );

    updater.execute( conn );

    return updater.status();
}

RWDBStatus CtiTableGroupRecipient::Restore()
{

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBStatus dbstat;

    {
        RWDBTable table = getDatabase().table( getTableName() );
        RWDBSelector selector = getDatabase().selector();

        selector <<
        table["recipientid"] <<
        table["recipientname"] <<
        table["emailaddress"] <<
        table["emailsendtype"] <<
        table["pagernumber"] <<
        table["disableflag"] <<
        table["recipienttype"];

        selector.where( table["recipientid"] == getRecipientID() );

        RWDBReader reader = selector.reader( conn );

        dbstat = selector.status();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
        }
    }

    return dbstat;
}

RWDBStatus CtiTableGroupRecipient::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["recipientid"] == getRecipientID() );

    return deleter.execute( conn ).status();
}


void CtiTableGroupRecipient::DecodeDatabaseReader(RWDBReader& rdr)
{

    RWCString rwstemp;

    rdr["recipientid"] >> _recipientID;
    rdr["recipientname"] >> _recipientName;
    rdr["emailaddress"] >> _emailAddress;
    rdr["emailsendtype"] >> _emailSendType;
    rdr["pagernumber"] >> _pagerNumber;
    rdr["disableflag"] >> rwstemp;
    rdr["recipienttype"] >> _recipientType;

    rwstemp.toLower();
    setDisabled(rwstemp[(size_t)0] == 'y');

    return;

}

bool CtiTableGroupRecipient::operator<( const CtiTableGroupRecipient &rhs ) const
{
    return(getRecipientID() < rhs.getRecipientID());
}
bool CtiTableGroupRecipient::operator==( const CtiTableGroupRecipient &rhs ) const
{
    return(getRecipientID() == rhs.getRecipientID());
}
bool CtiTableGroupRecipient::operator()(const CtiTableGroupRecipient& aRef) const
{
    return operator<(aRef);
}

CtiTableGroupRecipient::CtiTableGroupRecipient(LONG id) :
_recipientID( id ),
_isDirty(true),
_disabled(true)
{}

CtiTableGroupRecipient::CtiTableGroupRecipient(const CtiTableGroupRecipient& aRef)
{
    *this = aRef;
}

CtiTableGroupRecipient::~CtiTableGroupRecipient() {}

CtiTableGroupRecipient& CtiTableGroupRecipient::operator=(const CtiTableGroupRecipient& aRef)
{
    if(this != &aRef)
    {


        _recipientID    = aRef.getRecipientID();
        _recipientName = aRef.getRecipientName();
        _emailAddress  = aRef.getEmailAddress();
        _emailSendType = aRef.getEmailSendType();
        _pagerNumber   = aRef.getPagerNumber();
        _recipientType = aRef.getRecipientType();
        _disabled      = aRef.isDisabled();

        setDirty( aRef.isDirty() );
    }
    return *this;
}

