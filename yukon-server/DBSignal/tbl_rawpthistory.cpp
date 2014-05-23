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

void CtiTableRawPointHistory::fillInserter(Cti::RowWriter &inserter, const __int64 changeId)
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setMillis(), millis = " << millis << " < 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return 0;
    }

    if( millis > 999 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return millis % 1000;
    }

    return millis;
}


