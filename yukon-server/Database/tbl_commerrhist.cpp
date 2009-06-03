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
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_commerrhist.h"
#include "logger.h"
#include "rwutil.h"

//CtiTableCommErrorHistory::CtiTableCommErrorHistory() {}

CtiTableCommErrorHistory::CtiTableCommErrorHistory(LONG paoid, const CtiTime& datetime,
                                                   LONG soe, LONG type, LONG number,
                                                   const string& cmd,
                                                   const string& out,
                                                   const string& in, LONG ceid) :
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

const CtiTime& CtiTableCommErrorHistory::getDateTime() const
{

    return _dateTime;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setDateTime( const CtiTime& dt )
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

const string& CtiTableCommErrorHistory::getCommand() const
{
    return _command;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setCommand( const string &str )
{

    _command = str;
    return *this;
}

const string& CtiTableCommErrorHistory::getOutMessage() const
{
    return _outMessage;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setOutMessage( const string &str )
{
    _outMessage = str;
    return *this;
}

const string& CtiTableCommErrorHistory::getInMessage() const
{
    return _inMessage;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setInMessage( const string &str )
{

    _inMessage = str;
    return *this;
}

string CtiTableCommErrorHistory::getTableName()
{
    return "CommErrorHistory";
}

void CtiTableCommErrorHistory::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("YukonPAObject");
    RWDBTable devTbl = db.table(getTableName().c_str());

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

    RWDBTable table = getDatabase().table( getTableName().c_str() );
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
    RWDBTable table = getDatabase().table( getTableName().c_str() );
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

    if(getCommand().empty()) setCommand("none");
    if(getOutMessage().empty()) setOutMessage("none");
    if(getInMessage().empty()) setInMessage("none");

    inserter <<
    getCommErrorID() <<
    getPAOID() <<
    CtiTime(getDateTime()) <<
    getSoeTag() <<
    getErrorType() <<
    getErrorNumber() <<
    getCommand() <<
    getOutMessage() <<
    getInMessage();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }
#if 1
    else
    {
        string loggedSQLstring = inserter.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Unable to insert Comm Error History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << loggedSQLstring << endl;
        }
    }
#else
    else
    {
        try
        {
            LONG newcid = CommErrorHistoryIdGen(true);

            if(newcid > 0 && newcid != getCommErrorID())
            {
                CtiTime Now;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << Now << " Insert collision occurred in table " << getTableName() << "." << endl;
                    dout << Now << "   CommErrorId has been re-initialized.  There may be two copies of dispatch inserting into this DB" << endl;
                }

                setCommErrorID( newcid );

                if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() != RWDBStatus::ok )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Unable to insert Comm Error History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "   " << inserter.asString() << endl;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getPAOID() );

    updater <<
    table["paobjectid"].assign(getPAOID() ) <<
    table["commerrorid"].assign(getCommErrorID() ) <<
    table["datetime"].assign(toRWDBDT(getDateTime()) ) <<
    table["soe_tag"].assign( getSoeTag() ) <<
    table["errortype"].assign( getErrorType() ) <<
    table["errornumber"].assign( getErrorNumber() ) <<
    table["command"].assign( getCommand().c_str() ) <<
    table["outmessage"].assign( getOutMessage().c_str() ) <<
    table["inmessage"].assign( getInMessage().c_str() );

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok )
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableCommErrorHistory::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["paobjectid"] == getPAOID() );
    deleter.execute( conn );
    return deleter.status();
}

RWDBStatus CtiTableCommErrorHistory::Prune(CtiDate &earliestDate)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["datetime"] < toRWDBDT(CtiTime( earliestDate )) );
    deleter.execute( conn );
    return deleter.status();
}
