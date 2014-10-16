#include "precompiled.h"
#include <boost\regex.hpp>

#include "dbaccess.h"
#include "logger.h"
#include "tbl_paoexclusion.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

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

long CtiTablePaoExclusion::getPaoId() const
{
    return _paoId;
}

long CtiTablePaoExclusion::getExcludedPaoId() const
{
    return _excludedPaoId;
}

long CtiTablePaoExclusion::getPointId() const
{
    return _pointId;
}

double CtiTablePaoExclusion::getValue() const
{
    return _value;
}

long CtiTablePaoExclusion::getFunctionId() const
{
    return _functionId;
}

string CtiTablePaoExclusion::getFunctionName() const
{
    return _funcName;
}

string CtiTablePaoExclusion::getFunctionParams() const
{
    return _funcParams;
}

long CtiTablePaoExclusion::getFunctionRequeue() const
{
    return _funcRequeue;
}

string CtiTablePaoExclusion::getTableName()
{
    return "PaoExclusion";
}

string CtiTablePaoExclusion::getSQLCoreStatement(long id)
{
    static const string sqlNoID =  "SELECT PEX.exclusionid, PEX.paoid, PEX.excludedpaoid, PEX.pointid, PEX.value, "
                                     "PEX.functionid, PEX.funcname, PEX.funcrequeue, PEX.funcparams "
                                   "FROM PaoExclusion PEX "
                                   "WHERE PEX.functionid != 3";

    if(id > 0)
    {
        return string(sqlNoID + " AND PEX.paoid = ?");
    }
    else
    {
        return sqlNoID;
    }
}

void CtiTablePaoExclusion::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
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
        temp = what[0];
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _cycleTime = atoi(what[0].str().c_str());

        e1.assign("offset:[0-9]+");
        boost::regex_search(_funcParams, what, e1, boost::match_default);
        temp = what[0];
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _cycleOffset = atoi(what[0].str().c_str());


        e1.assign("transmittime:[0-9]+");
        boost::regex_search(_funcParams, what, e1, boost::match_default);
        temp = what[0];
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _transmitTime = atoi(what[0].str().c_str());


        e1.assign("maxtime:[0-9]+");
        boost::regex_search(_funcParams, what, e1, boost::match_default);
        temp = what[0];
        e1.assign("[0-9]+");
        boost::regex_search(temp, what, e1, boost::match_default);
        _maxTransmitTime = atoi(what[0].str().c_str());

    }

}

std::string CtiTablePaoExclusion::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTablePaoExclusion";
    itemList.add("exclusionid")   << _exclusionId;
    itemList.add("paoid")         << _paoId;
    itemList.add("excludedpaoid") << _excludedPaoId;
    itemList.add("pointid")       << _pointId;
    itemList.add("value")         << _value;
    itemList.add("functionid")    << _functionId;
    itemList.add("funcname")      << _funcName;
    itemList.add("funcrequeue")   << _funcRequeue;
    itemList.add("funcparams")    << _funcParams;
    itemList.add("cycletime")     << _cycleTime;
    itemList.add("cycleoffset")   << _cycleOffset;

    return itemList.toString();
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
        CTILOG_ERROR(dout, "cycleTime ("<< _cycleTime <<") < cycleOffset ("<< _cycleOffset <<")");
    }

    return *this;
}

CtiTablePaoExclusion &CtiTablePaoExclusion::setCycleOffset(int cycleoffset)
{
    _cycleOffset = cycleoffset;

    if( _cycleTime < _cycleOffset )
    {
        CTILOG_ERROR(dout, "cycleTime ("<< _cycleTime <<") < cycleOffset ("<< _cycleOffset <<")");
    }

    return *this;
}

CtiTablePaoExclusion &CtiTablePaoExclusion::setTransmitTime(int transmittime)
{
    _transmitTime = transmittime;
    return *this;
}




