/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_golay
*
* Date:   4/21/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.24 $
* DATE         :  $Date: 2008/10/28 19:21:42 $
*
* HISTORY      :
* $Log: dev_grp_golay.cpp,v $
* Revision 1.24  2008/10/28 19:21:42  mfisher
* YUK-6589 Scanner should not load non-scannable devices
* refreshList() now takes a list of paoids, which may be empty if it's a full reload
* Changed CtiDeviceBase, CtiPointBase, and CtiRouteBase::getSQL() (and all inheritors) to be const
* Removed a couple unused Porter functions
* Added logger unit test
* Simplified DebugTimer's variables
*
* Revision 1.23  2008/08/14 15:57:39  jotteson
* YUK-6333  Change naming in request message and change cancellation to use this new named field instead of user ID
* Cancellation now uses the new group message ID.
* Group Message ID name added to Request, Result, Out, and In messages.
*
* Revision 1.22  2007/08/27 18:27:10  jotteson
* YUK-4279
* Added function to remove the dynamic text from control command strings.
*
* Revision 1.21  2006/04/17 20:12:07  cplender
* Altered the processing of golay operational addresses to fully support F1,2,3 & 4
*
* Revision 1.20  2006/02/27 23:58:30  tspar
* Phase two of RWTPtrSlist replacement.
*
* Revision 1.19  2006/02/24 00:19:11  tspar
* First Series of replacements of RWTPtrSlist to std::list. Scanner, Pil, Porter.
*
* Revision 1.18  2006/02/17 17:04:34  tspar
* CtiMultiMsg:  replaced RWOrdered with vector<RWCollectable*> throughout the tree
*
* Revision 1.17  2006/01/16 20:48:25  mfisher
* Message Flags naming change
*
* Revision 1.16  2005/12/20 17:20:21  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.15  2005/10/25 14:36:22  cplender
* Have reportControlStart use the command sent as the last command... it is for these guys.
*
* Revision 1.14  2005/09/02 16:19:46  cplender
* Modified the getPutConfigAssignment() method to allow modifier parameters.
*
* Revision 1.13  2005/07/25 16:38:04  cplender
* Golay receivers cannot epire an OM in less than 15 minutes.
*
* Revision 1.12  2005/04/15 19:04:10  mfisher
* got rid of magic number debuglevel checks
*
* Revision 1.11  2005/02/17 23:22:31  cplender
* Removed deletes of OutMessage that are unnecessary
*
* Revision 1.10  2005/02/17 19:02:58  mfisher
* Removed space before CVS comment header, moved #include "precompiled.h"
//#include "yukon.h" after CVS header
*
* Revision 1.9  2005/02/10 23:23:59  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.8  2005/01/27 17:50:44  cplender
* Added method reportActionItemsToDispatch()
*
* Revision 1.7  2004/12/02 22:15:13  cplender
* Added OM-ExpirationTime to the device queue processing.
*
* Revision 1.6  2004/12/01 20:12:49  cplender
* Default "control_reduction" is 100, not -1.
*
* Revision 1.5  2004/06/28 16:40:40  cplender
* Added toUpper on the string responses to FORCE case insensitivity.
*
* Revision 1.4  2004/06/23 13:16:58  cplender
* Added control_interval and control_reduction to the grp so the protocol doesn't need to set it.
*
* Revision 1.3  2004/05/24 13:49:46  cplender
* Set retries to 0 for all but 205 commands.
*
* Revision 1.2  2004/05/10 22:35:28  cplender
* Controls require
* OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC
*
* Revision 1.1  2004/04/29 20:23:49  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "cmdparse.h"
#include "dev_grp_golay.h"
#include "expresscom.h"
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

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay::CtiDeviceGroupGolay()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay::CtiDeviceGroupGolay(const CtiDeviceGroupGolay& aRef)
{
    *this = aRef;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay::~CtiDeviceGroupGolay()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay& CtiDeviceGroupGolay::operator=(const CtiDeviceGroupGolay& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _loadGroup = aRef.getLoadGroup();
    }

    return *this;
}

//===================================================================================================================
//===================================================================================================================

CtiTableSASimpleGroup CtiDeviceGroupGolay::getLoadGroup() const
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiTableSASimpleGroup& CtiDeviceGroupGolay::getLoadGroup()
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay& CtiDeviceGroupGolay::setLoadGroup(const CtiTableSASimpleGroup& aRef)
{
    _loadGroup = aRef;
    return *this;
}

//===================================================================================================================
//===================================================================================================================

LONG CtiDeviceGroupGolay::getRouteID()
{
    return _loadGroup.getRouteId();
}

//===================================================================================================================
// This method determines what should be displayed in the "Description" column
// of the systemlog table when something happens to this device
//===================================================================================================================
string CtiDeviceGroupGolay::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    string tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

//===================================================================================================================
//===================================================================================================================

string CtiDeviceGroupGolay::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, LMS.groupid, LMS.routeid, "
                                     "LMS.operationaladdress, LMS.nominaltimeout, LMS.markindex, LMS.spaceindex "
                                   "FROM YukonPAObject YP, Device DV, LMGroupSASimple LMS "
                                   "WHERE upper (YP.type) = 'GOLAY GROUP' AND YP.paobjectid = LMS.groupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupGolay::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _loadGroup.DecodeDatabaseReader(rdr);
}

//===================================================================================================================
//===================================================================================================================

INT CtiDeviceGroupGolay::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   nRet = NoError;
    string resultString;

    CtiRouteSPtr Route;

    parse.setValue("type", ProtocolGolayType);
    parse.parse();

    bool control = (parse.getFlags() & CMD_FLAG_CTL_SHED);

    if(!control)
    {
        nRet = BADPARAM;

        resultString = " Cannot control Golay groups except with command \"control shed\"  :" + getName();
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());
        retList.push_back( pRet );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << resultString << endl;
        }
    }
    else
    {
        parse.setValue("shed", _loadGroup.getNominalTimeout());

        // Set these here in case they are not set elsewhere.
        parse.setValue("control_interval", _loadGroup.getNominalTimeout() );
        parse.setValue("control_reduction", 100 );

        parse.setValue("sa_codesimple", _loadGroup.getOperationalAddress());
        parse.setValue("sa_golaybase", _loadGroup.getGolayOperationalAddress());
        if(!parse.isKeyValid("sa_function"))
            parse.setValue("sa_function", _loadGroup.getFunction(control));

        if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
        {
            OutMessage->TargetID = getID();
            OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
            OutMessage->Retry = 0;
            OutMessage->ExpirationTime = CtiTime().seconds() + (_loadGroup.getNominalTimeout() >= 900 ? _loadGroup.getNominalTimeout() : 900); // Time this out in 15 minutes or the setting.

            reportActionItemsToDispatch(pReq, parse, vgList);

            //
            //  Form up the reply here since the ExecuteRequest function might consume the OutMessage.
            //
            CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), Route->getName(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

            // Start the control request on its route(s)
            if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
            {
                resultString = "ERROR " + CtiNumStr(nRet).spad(3) + string(" performing command on route ") + Route->getName();
                pRet->setStatus(nRet);
                pRet->setResultString(resultString);
                retList.push_back( pRet );
            }
            else
            {
                if(parse.getCommand() == ControlRequest)
                    reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, removeCommandDynamicText(parse.getCommandStr()) );

                delete pRet;
            }
        }
        else
        {
            nRet = NoRouteGroupDevice;

            resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();
            CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());
            retList.push_back( pRet );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << resultString << endl;
            }
        }
    }

    return nRet;
}



