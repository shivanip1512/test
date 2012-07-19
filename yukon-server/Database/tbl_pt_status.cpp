#include "precompiled.h"

#include "tbl_pt_status.h"
#include "dllbase.h"
#include "logger.h"

using std::endl;

CtiTablePointStatus::CtiTablePointStatus() :
    _initialState(0)
{
}

void CtiTablePointStatus::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["initialstate"] >> _initialState;
}


int CtiTablePointStatus::getInitialState() const
{
    return _initialState;
}

