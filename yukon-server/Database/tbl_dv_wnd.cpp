/*-----------------------------------------------------------------------------*
*
*    FILE NAME: tbl_scanwindow.cpp
*
*    DATE: 10/03/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_wnd.cpp-arc  $
*    REVISION     :  $Revision: 1.11 $
*    DATE         :  $Date: 2005/04/15 18:28:40 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Memory image of scan window table
*
*    DESCRIPTION: This class implements a a window that some function is valid for a device
*
*    ---------------------------------------------------
*    History:
      $Log: tbl_dv_wnd.cpp,v $
      Revision 1.11  2005/04/15 18:28:40  mfisher
      got rid of magic number debuglevel checks

      Revision 1.10  2005/02/10 23:23:48  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.9  2004/06/28 16:40:40  cplender
      Added toUpper on the string responses to FORCE case insensitivity.

      Revision 1.8  2003/05/23 22:12:10  cplender
      Workning on making the alternatescan rate logic scan immediately and then at the alternate interval.

      Revision 1.7  2002/11/15 14:07:51  cplender
      CRTDBG_new is being used in place of new

      Revision 1.6  2002/09/09 21:44:07  cplender
      These guys have multiple entries per pao.  Brain cramp on last change.  undid it.

      Revision 1.4  2002/05/02 17:02:34  cplender
      DBAccess no longer uses connection Lockguard to limit the number of connections
      It uses the CTIDBG_new CtiSemaphore object to limit them to 5 (default)

      Revision 1.3  2002/04/16 15:58:01  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:32  cplender

      This is an update due to the freezing of PVCS on 4/13/2002


      Rev 2.3   28 Feb 2002 11:52:18   cplender
   DBMemObject no longer inherits RWMonitor, removed table monitors.

      Rev 2.2   19 Oct 2001 09:55:24   dsutton
   updated the way we handle the windows when open and close are equal

      Rev 2.1   17 Oct 2001 13:45:30   dsutton
   initial revision
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"

#include "tbl_dv_wnd.h"

CtiTableDeviceWindow::CtiTableDeviceWindow() :
_ID(-1),
_type(DeviceWindowInvalid),
_open(-1L),
_duration(-1L),
_alternateOpen(-1L),
_alternateDuration(-1L),
_updated(FALSE)
{}

CtiTableDeviceWindow::~CtiTableDeviceWindow()
{
}

CtiTableDeviceWindow::CtiTableDeviceWindow(const CtiTableDeviceWindow &aRef)
{
    *this = aRef;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::operator=(const CtiTableDeviceWindow &aRef)
{
    if(this != &aRef)
    {
        _ID                 = aRef.getID();
        _type                     = aRef.getType();
        _open                     = aRef.getOpen();
        _duration                 = aRef.getDuration();
        _alternateOpen            = aRef.getAlternateOpen();
        _alternateDuration        = aRef.getAlternateDuration();

        _updated = FALSE;
    }

    return *this;
}

LONG CtiTableDeviceWindow::getType() const
{

    return _type;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setType( const LONG aType )
{

    _type = aType;
    return *this;
}

LONG CtiTableDeviceWindow::getOpen() const
{

    return _open;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setOpen( const LONG aWindow )
{

    _open = aWindow;
    return *this;
}

LONG CtiTableDeviceWindow::getDuration() const
{

    return _duration;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setDuration( const LONG aDuration )
{

    _duration = aDuration;
    return *this;
}

LONG CtiTableDeviceWindow::getAlternateOpen() const
{

    return _alternateOpen;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setAlternateOpen( const LONG aWindow )
{

    _alternateOpen = aWindow;
    return *this;
}

LONG CtiTableDeviceWindow::getAlternateDuration() const
{

    return _alternateDuration;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setAlternateDuration( const LONG aDuration )
{

    _alternateDuration = aDuration;
    return *this;
}

BOOL CtiTableDeviceWindow::getUpdated() const
{

    return _updated;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setUpdated( const BOOL aBool )
{

    _updated = aBool;
    return *this;
}

LONG CtiTableDeviceWindow::getID() const
{

    return _ID;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setID( const LONG aID )
{

    _ID = aID;
    return *this;
}


RWCString CtiTableDeviceWindow::getTableName()
{
    return "DeviceWindow";
}


LONG CtiTableDeviceWindow::calculateClose(LONG aOpen, LONG aDuration) const
{
    LONG close;

    close = aOpen + aDuration;
    if(close > 86400)
    {
        close -= 86400;
    }
    return close;
}

void CtiTableDeviceWindow::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("Device");
    RWDBTable devTbl = db.table(getTableName());

    selector <<
    keyTable["deviceid"] <<
    devTbl["type"] <<
    devTbl["winopen"] <<
    devTbl["winclose"] <<
    devTbl["alternateopen"] <<
    devTbl["alternateclose"];

    selector.from(keyTable);
    selector.from(devTbl);

//   selector.where( selector.where() && keyTable["deviceid"] == getDeviceID() );

    selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] ); //should I change this to getDeviceID() ??
}

void CtiTableDeviceWindow::DecodeDatabaseReader(RWDBReader &aRdr)
{
    LONG close,alternateClose;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    RWCString rwstemp;

    aRdr["type"]  >> rwstemp;

    _type = resolveDeviceWindowType( rwstemp );
    aRdr["winopen"] >> _open;
    aRdr["winclose"] >> close;
    aRdr["alternateopen"] >> _alternateOpen;
    aRdr["alternateclose"] >> alternateClose;

    // see if we pass over midnight
    if(close < _open)
    {
        // we are seconds in a day minus open + seconds into the day
        _duration = 86400 - _open + close;
    }
    else
    {
        _duration = close - _open;
    }

    // see if we pass over midnight
    if(close < _open)
    {
        // we are seconds in a day minus open + seconds into the day
        _alternateDuration = 86400 - _alternateOpen + alternateClose;
    }
    else
    {
        _alternateDuration = alternateClose - _alternateOpen;
    }

    _updated = TRUE;                    // _ONLY_ _ONLY_ place this is set.
}

void CtiTableDeviceWindow::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << "DumpData" << endl;
    dout << " Window Type                                 : " << desolveDeviceWindowType( _type) << endl;
    dout << " Open                                        : " << _open  << endl;
    dout << " Duration                                    : " << _duration << endl;
    dout << " Alternate Open                              : " << _alternateOpen  << endl;
    dout << " Alternate Duration                          : " << _alternateDuration << endl;
}


RWDBStatus CtiTableDeviceWindow::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["type"] <<
    table["winopen"] <<
    table["winclose"] <<
    table["alternateopen"] <<
    table["alternateclose"];

//   selector.where( (table["deviceid"] == getDeviceID())) ;
    selector.where( (table["deviceid"] == getID() ) && (rwdbLower(table["type"]) == desolveDeviceWindowType (getType())));

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableDeviceWindow::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getID() <<
    desolveDeviceWindowType(getType() ) <<
    getOpen () <<
    calculateClose (getOpen(),getDuration()) <<
    getAlternateOpen () <<
    calculateClose (getAlternateOpen(),getAlternateDuration());

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceWindow::Update()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getID() );

    updater <<
    table["type"].assign(desolveDeviceWindowType(getType() ) ) <<
    table["winopen"].assign(getOpen() ) <<
    table["winclose"].assign(calculateClose (getOpen(),getDuration())) <<
    table["alternateopen"].assign(getAlternateOpen() ) <<
    table["alternateclose"].assign(calculateClose (getAlternateOpen(),getAlternateDuration()));
    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceWindow::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getID() && (rwdbLower(table["type"]) == desolveDeviceWindowType(getType())));
    deleter.execute( conn );
    return deleter.status();
}

bool CtiTableDeviceWindow::addSignaledRateActive(int rate) const
{
    pair< CtiWindowSet_t::iterator, bool> ipair = _signaledRateActive.insert( rate );
    return ipair.second;
}

bool CtiTableDeviceWindow::addSignaledRateSent(int rate) const
{
    pair< CtiWindowSet_t::iterator, bool> ipair = _signaledRateSent.insert( rate );
    return ipair.second;
}

bool CtiTableDeviceWindow::isSignaledRateScheduled(int rate) const
{
    return (_signaledRateSent.find( rate ) != _signaledRateSent.end()); // Found it in there!
}

bool CtiTableDeviceWindow::verifyWindowMatch() const
{
    bool bmatch = false;

    if(_signaledRateSent == _signaledRateActive)
    {
        bmatch = true;
    }

    return bmatch;
}




