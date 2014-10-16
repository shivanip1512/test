#include "precompiled.h"

#include "logger.h"
#include "pt_status.h"
#include "tbl_pt_alarm.h"

using namespace std;

const boost::optional<CtiTablePointStatusControl> CtiPointStatus::getControlParameters() const
{
    return _pointStatusControl;
}

//  NOTE - no WHERE clause, you will need to add your own!
string CtiPointStatus::getSQLCoreStatement()
{
    static const string sql =
        "SELECT"
            " PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid,"
            " PT.pointoffset, PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, PT.archiveinterval,"
            " STS.initialstate,"
            " PSC.controltype, PSC.closetime1, PSC.closetime2, PSC.statezerocontrol, PSC.stateonecontrol, PSC.commandtimeout,"
            " PC.controlinhibit, PC.controloffset"
        " FROM"
            " Point PT"
            " JOIN PointStatus STS on PT.pointid = STS.pointid"
            " LEFT OUTER JOIN PointStatusControl PSC on STS.pointid = PSC.pointid"
            " LEFT OUTER JOIN PointControl PC on PSC.pointid = PC.pointid";

    return sql;
}

void CtiPointStatus::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);          // get the base class data out!

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    _pointStatus.DecodeDatabaseReader(rdr);

    if( ! rdr["controloffset"].isNull() )
    {
        _pointStatusControl = CtiTablePointStatusControl();

        _pointStatusControl->DecodeDatabaseReader(rdr);
    }
    else
    {
        _pointStatusControl.reset();
    }
}

UINT CtiPointStatus::adjustStaticTags(UINT &tag) const
{
    Inherited::adjustStaticTags(tag);

    if( _pointStatusControl &&
        _pointStatusControl->getControlType() != ControlType_Invalid )
    {
        tag |= TAG_ATTRIB_CONTROL_AVAILABLE;
    }
    else
    {
        tag &= ~TAG_ATTRIB_CONTROL_AVAILABLE;
    }

    if( _pointStatusControl &&
        _pointStatusControl->isControlInhibited() )
    {
        tag |= TAG_DISABLE_CONTROL_BY_POINT;
    }
    else
    {
        tag &= ~TAG_DISABLE_CONTROL_BY_POINT;
    }

    return tag;
}

double CtiPointStatus::getDefaultValue( ) const
{
   return _pointStatus.getInitialState();
}

