/*-----------------------------------------------------------------------------*
*
* File:   tbl_commerrhist
*
* Date:   9/21/2001
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_commerrhist.cpp-arc  $
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_commerrhist.h"
#include "logger.h"

//CtiTableCommErrorHistory::CtiTableCommErrorHistory() {}

CtiTableCommErrorHistory::CtiTableCommErrorHistory(LONG paoid, const RWTime& datetime,
                                                   LONG soe, LONG type, LONG number,
                                                   const RWCString& cmd,
                                                   const RWCString& out,
                                                   const RWCString& in, LONG ceid) :
_commErrorID(ceid), _paoID(paoid), _dateTime(datetime), _soeTag(soe), _errorType(type),
_errorNumber(number), _command(cmd), _outMessage(out), _inMessage(in)
{
}

CtiTableCommErrorHistory::CtiTableCommErrorHistory(const CtiTableCommErrorHistory& aRef)
{
    *this = aRef;
}

CtiTableCommErrorHistory::~CtiTableCommErrorHistory()
{
}

bool CtiTableCommErrorHistory::operator<(const CtiTableCommErrorHistory& aRef) const
{
    return(getCommErrorID() < aRef.getCommErrorID());
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::operator=(const CtiTableCommErrorHistory &aRef)
{
    if(this != &aRef)
    {
        _commErrorID   = aRef.getCommErrorID();
        _paoID         = aRef.getPAOID();
        _dateTime      = aRef.getDateTime();
        _soeTag        = aRef.getSoeTag();
        _errorType     = aRef.getErrorType();
        _errorNumber   = aRef.getErrorNumber();
        _command       = aRef.getCommand();
        _outMessage    = aRef.getOutMessage();
        _inMessage     = aRef.getInMessage();
    }
    return *this;
}

LONG CtiTableCommErrorHistory::getCommErrorID() const
{

    return _commErrorID;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setCommErrorID( const LONG ce )
{

    _commErrorID = ce;
    return *this;
}

LONG CtiTableCommErrorHistory::getPAOID() const
{

    return _paoID;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setPAOID( const LONG pao )
{

    _paoID = pao;
    return *this;
}

const RWTime& CtiTableCommErrorHistory::getDateTime() const
{

    return _dateTime;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setDateTime( const RWTime& dt )
{

    _dateTime = dt;
    return *this;
}

LONG CtiTableCommErrorHistory::getSoeTag() const
{

    return _soeTag;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setSoeTag( const LONG st )
{

    _soeTag = st;
    return *this;
}

LONG CtiTableCommErrorHistory::getErrorType() const
{

    return _errorType;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setErrorType( const LONG et )
{

    _errorType = et;
    return *this;
}

LONG CtiTableCommErrorHistory::getErrorNumber() const
{

    return _errorNumber;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setErrorNumber( const LONG en )
{

    _errorNumber = en;
    return *this;
}

const RWCString& CtiTableCommErrorHistory::getCommand() const
{
    return _command;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setCommand( const RWCString &str )
{

    _command = str;
    return *this;
}

const RWCString& CtiTableCommErrorHistory::getOutMessage() const
{
    return _outMessage;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setOutMessage( const RWCString &str )
{
    _outMessage = str;
    return *this;
}

const RWCString& CtiTableCommErrorHistory::getInMessage() const
{
    return _inMessage;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setInMessage( const RWCString &str )
{

    _inMessage = str;
    return *this;
}

RWCString CtiTableCommErrorHistory::getTableName()
{
    return "CommErrorHistory";
}

void CtiTableCommErrorHistory::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("YukonPAObject");
    RWDBTable devTbl = db.table(getTableName());

    selector <<
    keyTable["paobjectid"] <<
    devTbl["commerrorid"] <<
    devTbl["datetime"] <<
    devTbl["soe_tag"] <<
    devTbl["errortype"] <<
    devTbl["errornumber"] <<
    devTbl["command"] <<
    devTbl["outmessage"] <<
    devTbl["inmessage"];

    selector.from(keyTable);
    selector.from(devTbl);

    selector.where( selector.where() && keyTable["paobjectid"] == devTbl["paobjectid"] );
}

void CtiTableCommErrorHistory::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"]          >> _paoID;
    rdr["commerrorid"]         >> _commErrorID;
    rdr["datetime"]            >> _dateTime;
    rdr["soe_tag"]             >> _soeTag;
    rdr["errortype"]           >> _errorType;
    rdr["errornumber"]         >> _errorNumber;
    rdr["command"]             >> _command;
    rdr["outmessage"]          >> _outMessage;
    rdr["inmessage"]           >> _inMessage;
}

RWDBStatus CtiTableCommErrorHistory::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["paobjectid"] <<
    table["commerrorid"] <<
    table["datetime"] <<
    table["soe_tag"] <<
    table["errortype"] <<
    table["errornumber"] <<
    table["command"] <<
    table["outmessage"] <<
    table["inmessage"];

    selector.where( table["paobjectid"] == getPAOID() );  //??

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

RWDBStatus CtiTableCommErrorHistory::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTableCommErrorHistory::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();


    if(getCommand().length() > MAX_COMMAND_LENGTH)
    {
        _command.resize(MAX_COMMAND_LENGTH-1);
    }

    if(getOutMessage().length() > MAX_OUTMESS_LENGTH)
    {
        _outMessage.resize(MAX_OUTMESS_LENGTH - 1);
    }

    if(getInMessage().length() > MAX_INMESS_LENGTH)
    {
        _inMessage.resize(MAX_INMESS_LENGTH - 1);
    }

    if(getCommand().isNull()) setCommand("none");
    if(getOutMessage().isNull()) setOutMessage("none");
    if(getInMessage().isNull()) setInMessage("none");

    inserter <<
    getCommErrorID() <<
    getPAOID() <<
    RWDBDateTime(getDateTime()) <<
    getSoeTag() <<
    getErrorType() <<
    getErrorNumber() <<
    getCommand() <<
    getOutMessage() <<
    getInMessage();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }
#if 1
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Unable to insert Comm Error History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
    }
#else
    else
    {
        try
        {
            LONG newcid = CommErrorHistoryIdGen(true);

            if(newcid > 0 && newcid != getCommErrorID())
            {
                RWTime Now;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << Now << " Insert collision occurred in table " << getTableName() << "." << endl;
                    dout << Now << "   CommErrorId has been re-initialized.  There may be two copies of dispatch inserting into this DB" << endl;
                }

                setCommErrorID( newcid );

                if( inserter.execute( conn ).status().errorCode() != RWDBStatus::ok )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Unable to insert Comm Error History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "   " << inserter.asString() << endl;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
#endif
    return inserter.status();
}


RWDBStatus CtiTableCommErrorHistory::Update()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getPAOID() );

    updater <<
    table["paobjectid"].assign(getPAOID() ) <<
    table["commerrorid"].assign(getCommErrorID() ) <<
    table["datetime"].assign(getDateTime() ) <<
    table["soe_tag"].assign( getSoeTag() ) <<
    table["errortype"].assign( getErrorType() ) <<
    table["errornumber"].assign( getErrorNumber() ) <<
    table["command"].assign( getCommand() ) <<
    table["outmessage"].assign( getOutMessage() ) <<
    table["inmessage"].assign( getInMessage() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableCommErrorHistory::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["paobjectid"] == getPAOID() );
    deleter.execute( conn );
    return deleter.status();
}

RWDBStatus CtiTableCommErrorHistory::Prune(RWDate &earliestDate)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["datetime"] < RWDBDateTime( earliestDate ) );
    deleter.execute( conn );
    return deleter.status();
}
