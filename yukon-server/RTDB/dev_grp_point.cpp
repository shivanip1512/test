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

CtiDeviceGroupPoint::CtiDeviceGroupPoint(const CtiDeviceGroupPoint& aRef)
{
   *this = aRef;
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupPoint::~CtiDeviceGroupPoint()
{
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupPoint& CtiDeviceGroupPoint::operator=(const CtiDeviceGroupPoint& aRef)
{
   if( this != &aRef )
   {
       Inherited::operator=( aRef );

       {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
       }
   }

   return *this;
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
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, LGP.deviceid, LGP.deviceidusage, "
                                     "LGP.pointidusage, LGP.startcontrolrawstate, PTS.statezerocontrol, PTS.stateonecontrol "
                                   "FROM YukonPAObject YP, Device DV, LMGroupPoint LGP, PointStatus PTS "
                                   "WHERE upper (YP.type) = 'POINT GROUP' AND PTS.pointid = LGP.pointidusage AND "
                                     "LGP.deviceid = YP.paobjectid AND YP.paobjectid = DV.deviceid";

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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

//====================================================================================================================
//====================================================================================================================

INT CtiDeviceGroupPoint::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;

    bool control = parse.getControlled();   // Is is controlled?

    // Add these two items to the list for control accounting!
    if(control)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** CONTROL START! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << _loadGroup.getControlStartString() << endl;
        }
        parse.setValue("control_interval", 86400);  // I do not have any idea how long this will control!
        parse.setValue("control_reduction", 100 );
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** CONTROL STOP!! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << _loadGroup.getControlStopString() << endl;
        }

        parse.setValue("control_reduction", 0 );
        parse.setValue("control_interval", 0);
    }

    if(parse.getCommand() == ControlRequest)
    {
        reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, removeCommandDynamicText(parse.getCommandStr()) );
    }

    return nRet;
}

INT CtiDeviceGroupPoint::generateRequest(CtiRequestMsg *pReq, CtiCommandParser &parse)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;

    bool control = parse.getControlled();   // Is is controlled?

    // Add these two items to the list for control accounting!
    if(control)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** CONTROL START! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << _loadGroup.getControlStartString() << endl;
        }
        pReq->setDeviceId( _loadGroup.getControlDevice() );
        pReq->setCommandString( _loadGroup.getControlStartString().c_str() + string(" select pointid " + CtiNumStr(_loadGroup.getControlPoint())));
        pReq->setMessagePriority( MAXPRIORITY - 1 );    // Make it sing!
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** CONTROL STOP!! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << _loadGroup.getControlStopString() << endl;
        }
        pReq->setDeviceId( _loadGroup.getControlDevice() );
        pReq->setCommandString( _loadGroup.getControlStopString().c_str() + string(" select pointid " + CtiNumStr(_loadGroup.getControlPoint())));
        pReq->setMessagePriority( MAXPRIORITY - 1 );    // Make it sing!
    }

    return nRet;
}

LONG CtiDeviceGroupPoint::getRouteID()
{
    return 0L;
}

