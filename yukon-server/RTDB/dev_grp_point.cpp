#include "precompiled.h"
#include "cmdparse.h"
#include "cparms.h"
#include "dev_grp_point.h"
#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "utility.h"

using std::string;
using std::endl;
using std::list;

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupPoint::CtiDeviceGroupPoint()
{

}

//====================================================================================================================
//====================================================================================================================

string CtiDeviceGroupPoint::getDescription(const CtiCommandParser & parse) const
{
    string tmpStr = "Group: " + getName();
    return tmpStr;
}

//====================================================================================================================
//====================================================================================================================

string CtiDeviceGroupPoint::getSQLCoreStatement() const
{
    static const string sqlCore =
        "SELECT"
            " YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag,"
            " DV.deviceid, DV.alarminhibit, DV.controlinhibit,"
            " LGP.deviceid, LGP.deviceidusage, LGP.pointidusage, LGP.startcontrolrawstate,"
            " PSC.statezerocontrol, PSC.stateonecontrol"
        " FROM"
            " YukonPAObject YP"
            " JOIN Device DV on YP.paobjectid = DV.deviceid"
            " JOIN LMGroupPoint LGP on YP.paobjectid = LGP.deviceid"
            " JOIN PointStatusControl PSC on LGP.pointidusage = PSC.pointid"
        " WHERE"
            " upper (YP.type) = 'POINT GROUP'";

    return sqlCore;
}

//====================================================================================================================
//====================================================================================================================

void CtiDeviceGroupPoint::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    _loadGroup.DecodeDatabaseReader(rdr);

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }
}

//====================================================================================================================
//====================================================================================================================

YukonError_t CtiDeviceGroupPoint::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool control = parse.getControlled();   // Is is controlled?

    // Add these two items to the list for control accounting!
    if(control)
    {
        CTILOG_INFO(dout, "CONTROL START: "<< _loadGroup.getControlStartString());

        parse.setValue("control_interval", 86400);  // I do not have any idea how long this will control!
        parse.setValue("control_reduction", 100 );
    }
    else
    {
        CTILOG_INFO(dout, "CONTROL STOP: "<< _loadGroup.getControlStopString());

        parse.setValue("control_reduction", 0 );
        parse.setValue("control_interval", 0);
    }

    if(parse.getCommand() == ControlRequest)
    {
        reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, removeCommandDynamicText(parse.getCommandStr()) );
    }

    return ClientErrors::None;
}

INT CtiDeviceGroupPoint::generateRequest(CtiRequestMsg *pReq, CtiCommandParser &parse)
{
    bool control = parse.getControlled();   // Is is controlled?

    // Add these two items to the list for control accounting!
    if(control)
    {
        CTILOG_INFO(dout, "CONTROL START: "<< _loadGroup.getControlStartString());

        pReq->setDeviceId( _loadGroup.getControlDevice() );
        pReq->setCommandString( _loadGroup.getControlStartString().c_str() + string(" select pointid " + CtiNumStr(_loadGroup.getControlPoint())));
        pReq->setMessagePriority( MAXPRIORITY - 1 );    // Make it sing!
    }
    else
    {
        CTILOG_INFO(dout, "CONTROL STOP: "<< _loadGroup.getControlStopString());

        pReq->setDeviceId( _loadGroup.getControlDevice() );
        pReq->setCommandString( _loadGroup.getControlStopString().c_str() + string(" select pointid " + CtiNumStr(_loadGroup.getControlPoint())));
        pReq->setMessagePriority( MAXPRIORITY - 1 );    // Make it sing!
    }

    return ClientErrors::None;
}

LONG CtiDeviceGroupPoint::getRouteID()
{
    return 0L;
}

