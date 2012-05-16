#include "precompiled.h"

#include "logger.h"
#include "pt_status.h"
#include "tbl_pt_alarm.h"

using namespace std;

const CtiTablePointStatus &CtiPointStatus::getPointStatus() const
{
    return _pointStatus;
}

string CtiPointStatus::getSQLCoreStatement()
{
    static const string sql =  "SELECT PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, "
                                   "PT.pointoffset, PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, "
                                   "PT.archiveinterval, STS.initialstate, STS.controlinhibit, STS.controltype, "
                                   "STS.controloffset, STS.closetime1, STS.closetime2, STS.statezerocontrol, "
                                   "STS.stateonecontrol, STS.commandtimeout "
                               "FROM Point PT, PointStatus STS "
                               "WHERE PT.pointid = STS.pointid";

    return sql;
}

void CtiPointStatus::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);          // get the base class data out!

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _pointStatus.DecodeDatabaseReader(rdr);
}

UINT CtiPointStatus::adjustStaticTags(UINT &tag) const
{
    Inherited::adjustStaticTags(tag);
    return _pointStatus.adjustStaticTags(tag);
}

UINT CtiPointStatus::getStaticTags()
{
   return Inherited::getStaticTags() | _pointStatus.getStaticTags();
}


double CtiPointStatus::getDefaultValue( ) const
{
   return _pointStatus.getInitialState();
}

int CtiPointStatus::getControlExpirationTime() const
{
    int ct = _pointStatus.getCommandTimeout();

    if(ct <= 0)
    {
        ct = Inherited::getControlExpirationTime();
    }

    return ct;
}

int CtiPointStatus::getControlOffset() const
{
    int pOff = 0;
    if(_pointStatus.getControlType() != NoneControlType && _pointStatus.getControlType() != InvalidControlType)
        pOff = _pointStatus.getControlOffset();

    return pOff;
}

