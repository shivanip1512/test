#include "precompiled.h"

#include "tbl_pt_status_control.h"
#include "resolvers.h"
#include "logger.h"

using std::string;
using std::endl;

void CtiTablePointStatusControl::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from PointStatusControl");
    }

    string rwsTemp;
    rdr["controltype"] >> rwsTemp;
    _controlType = resolveControlType(rwsTemp);

    rdr["closetime1"] >> _closeTime1;
    rdr["closetime2"] >> _closeTime2;
    rdr["statezerocontrol"] >> _stateZeroControl;
    rdr["stateonecontrol"] >> _stateOneControl;
    rdr["commandtimeout"] >> _commandTimeout;
}


void CtiTablePointStatusControl::setControlType(CtiControlType_t t)
{
    _controlType = t;
}

CtiTablePointStatusControl::CtiTablePointStatusControl() :
    CtiTablePointControl(),
    _controlType(ControlType_Invalid),
    _closeTime1    (0),
    _closeTime2    (0),
    _commandTimeout(0)
{
}


int CtiTablePointStatusControl::getCommandTimeout() const
{
    return _commandTimeout > 0
            ? _commandTimeout
            : DefaultControlExpirationTime;
}

