#include "precompiled.h"

#include "tbl_rawpthistory.h"
#include "logger.h"

using std::endl;

CtiTableRawPointHistory::CtiTableRawPointHistory(long pid, int qual, double val, const CtiTime tme, int millis) :
    _pointId(pid),
    _quality(qual),
    _value(val),
    _time(tme),
    _millis(validateMillis(millis))
{
}

std::string CtiTableRawPointHistory::getInsertSql()
{
    return "INSERT INTO RawPointHistory VALUES (?,?,?,?,?,?)";
}

void CtiTableRawPointHistory::fillInserter(Cti::RowWriter &inserter, const long long changeId)
{
    inserter
        << changeId
        << _pointId
        << _time
        << _quality
        << _value
        << _millis;
}

int CtiTableRawPointHistory::validateMillis(int millis)
{
    if( millis < 0 )
    {
        CTILOG_ERROR(dout, "millis = "<< millis <<" < 0 - returning 0");

        return 0;
    }

    if( millis > 999 )
    {
        CTILOG_ERROR(dout, "millis = "<< millis <<" > 999 - returning % 1000");

        return millis % 1000;
    }

    return millis;
}


