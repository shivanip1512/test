/*-----------------------------------------------------------------------------*
*
* File:   tbl_paoexclusion
*
* Date:   5/14/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include <boost\regex.hpp>
#include <rw\re.h>
#undef mask_

#include "dbaccess.h"
#include "logger.h"
#include "tbl_paoexclusion.h"
#include "utility.h"
#include "rwutil.h"


CtiTablePaoExclusion::CtiTablePaoExclusion(long xid,
                                           long paoid,
                                           long excludedpaoid,
                                           long pointid,
                                           double value,
                                           long function,
                                           string str,
                                           long funcrequeue) :
_exclusionId(xid),
_paoId(paoid),
_excludedPaoId(excludedpaoid),
_pointId(pointid),
_value(value),
_functionId(function),
_funcName(str),
_funcRequeue(funcrequeue),
_cycleTime(0),
_cycleOffset(0),
_transmitTime(0),
_maxTransmitTime(0)

{

}

CtiTablePaoExclusion::CtiTablePaoExclusion(const CtiTablePaoExclusion& aRef)
{
    *this = aRef;
}

bool CtiTablePaoExclusion::operator<(const CtiTablePaoExclusion &rhs) const
{
    return (_exclusionId < rhs.getExclusionId());
}

long CtiTablePaoExclusion::getExclusionId() const
{
    return _exclusionId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setExclusionId(long xid)
{
    _exclusionId = xid;
    return *this;
}

long CtiTablePaoExclusion::getPaoId() const
{
    return _paoId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setPaoId(long val)
{
    _paoId = val;
    return *this;
}

long CtiTablePaoExclusion::getExcludedPaoId() const
{
    return _excludedPaoId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setExcludedPaoId(long val)
{
    _excludedPaoId = val;
    return *this;
}

long CtiTablePaoExclusion::getPointId() const
{
    return _pointId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setPointId(long val)
{
    _pointId = val;
    return *this;
}

double CtiTablePaoExclusion::getValue() const
{
    return _value;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setValue(double val)
{
    _value = val;
    return *this;
}

long CtiTablePaoExclusion::getFunctionId() const
{
    return _functionId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setFunctionId(long val)
{
    _functionId = val;
    return *this;
}

string CtiTablePaoExclusion::getFunctionName() const
{
    return _funcName;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setFunctionName(string val)
{
    _funcName = val;
    return *this;
}

string CtiTablePaoExclusion::getFunctionParams() const
{
    return _funcParams;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setFunctionParams(string val)
{
    _funcParams = val;
    return *this;
}

long CtiTablePaoExclusion::getFunctionRequeue() const
{
    return _funcRequeue;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setFunctionRequeue(long val)
{
    _funcRequeue = val;
    return *this;
}

string CtiTablePaoExclusion::getTableName()
{
    return "PaoExclusion";
}

void CtiTablePaoExclusion::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName().c_str());

    selector <<
    keyTable["exclusionid"] <<
    keyTable["paoid"] <<
    keyTable["excludedpaoid"] <<
    keyTable["pointid"] <<
    keyTable["value"] <<
    keyTable["functionid"] <<
    keyTable["funcname"] <<
    keyTable["funcrequeue"] <<
    keyTable["funcparams"];

    selector.from(keyTable);
}

void CtiTablePaoExclusion::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["exclusionid"]      >> _exclusionId;
    rdr["paoid"]            >> _paoId;
    rdr["excludedpaoid"]    >> _excludedPaoId;
    rdr["pointid"]          >> _pointId;
    rdr["value"]            >> _value;
    rdr["functionid"]       >> _functionId;
    rdr["funcname"]         >> _funcName;
    rdr["funcrequeue"]      >> _funcRequeue;
    rdr["funcparams"]       >> _funcParams;

    std::transform(_funcParams.begin(), _funcParams.end(), _funcParams.begin(), ::tolower);

    if(_funcParams[(size_t)0] != '#')
    {
        string temp;
        boost::regex e1("cycletime:[0-9]+");
        boost::match_results<std::string::const_iterator> what;
        boost::regex_search(_funcParams, what, e1, boost::match_default);
        temp = what[0].matched;
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _cycleTime = atoi(temp.c_str());

        e1.assign("offset:[0-9]+");
        boost::regex_search(_funcParams, what, e1, boost::match_default);
        temp = what[0].matched;
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _cycleOffset = atoi(temp.c_str());


        e1.assign("transmittime:[0-9]+");
        boost::regex_search(_funcParams, what, e1, boost::match_default);
        temp = what[0].matched;
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _transmitTime = atoi(temp.c_str());


        e1.assign("maxtime:[0-9]+");
        boost::regex_search(_funcParams, what, e1, boost::match_default);
        temp = what[0].matched;
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _maxTransmitTime = atoi(temp.c_str());

    }

}

RWDBStatus CtiTablePaoExclusion::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["exclusionid"] <<
    table["paoid"] <<
    table["excludedpaoid"] <<
    table["pointid"] <<
    table["value"] <<
    table["functionid"] <<
    table["funcname"] <<
    table["funcrequeue"] <<
    table["funcparams"];

    selector.where( table["exclusionid"] == getExclusionId() );  //??

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }

    return reader.status();
}

RWDBStatus CtiTablePaoExclusion::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTablePaoExclusion::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return inserter.status();
}


RWDBStatus CtiTablePaoExclusion::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return updater.status();
}

RWDBStatus CtiTablePaoExclusion::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return deleter.status();
}

void CtiTablePaoExclusion::dump() const
{

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

        dout << "exclusionid    " <<  _exclusionId << endl;
        dout << "paoid          " <<  _paoId << endl;
        dout << "excludedpaoid  " <<  _excludedPaoId << endl;
        dout << "pointid        " <<  _pointId << endl;
        dout << "value          " <<  _value << endl;
        dout << "functionid     " <<  _functionId << endl;
        dout << "funcname       " <<  _funcName << endl;
        dout << "funcrequeue    " <<  _funcRequeue << endl;
        dout << "funcparams     " <<  _funcParams << endl;
        dout << "cycletime      " <<  _cycleTime << endl;
        dout << "cycleoffset    " <<  _cycleOffset << endl;

    }
}

int CtiTablePaoExclusion::getCycleTime() const
{
    return _cycleTime;
}

int CtiTablePaoExclusion::getCycleOffset() const
{
    return _cycleOffset;
}

int CtiTablePaoExclusion::getTransmitTime() const
{
    return _transmitTime;
}

int CtiTablePaoExclusion::getMaxTransmitTime() const
{
    return _maxTransmitTime;
}


CtiTablePaoExclusion &CtiTablePaoExclusion::setCycleTime(int cycletime)
{
    _cycleTime = cycletime;

    if( _cycleTime < _cycleOffset )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - cycleTime (" << _cycleTime << ") < cycleOffset (" << _cycleOffset << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}

CtiTablePaoExclusion &CtiTablePaoExclusion::setCycleOffset(int cycleoffset)
{
    _cycleOffset = cycleoffset;

    if( _cycleTime < _cycleOffset )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - cycleTime (" << _cycleTime << ") < cycleOffset (" << _cycleOffset << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}

CtiTablePaoExclusion &CtiTablePaoExclusion::setTransmitTime(int transmittime)
{
    _transmitTime = transmittime;
    return *this;
}

CtiTablePaoExclusion &CtiTablePaoExclusion::setMaxTransmitTime(int maxtransmittime)
{
    _maxTransmitTime = maxtransmittime;
    return *this;
}



