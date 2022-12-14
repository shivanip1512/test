#include "precompiled.h"

#include "tbl_pt_control.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePointControl::CtiTablePointControl() :
    _controlInhibit(false),
    _controlOffset(0)
{
}

void CtiTablePointControl::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from PointControl");
    }

    string tempControlInhibit;

    rdr["controloffset"] >> _controlOffset;
    rdr["controlinhibit"] >> tempControlInhibit;

    _controlInhibit = ciStringEqual(tempControlInhibit, "y");
}

int CtiTablePointControl::getControlOffset() const
{
    return _controlOffset;
}

void CtiTablePointControl::setControlOffset(int i)
{
    _controlOffset = i;
}

bool CtiTablePointControl::isControlInhibited() const
{
    return _controlInhibit;
}

