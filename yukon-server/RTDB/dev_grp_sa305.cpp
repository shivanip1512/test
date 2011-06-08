/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sa305
*
* Date:   3/15/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.26 $
* DATE         :  $Date: 2008/10/28 19:21:42 $
*
* HISTORY      :
* $Log: dev_grp_sa305.cpp,v $
* Revision 1.26  2008/10/28 19:21:42  mfisher
* YUK-6589 Scanner should not load non-scannable devices
* refreshList() now takes a list of paoids, which may be empty if it's a full reload
* Changed CtiDeviceBase, CtiPointBase, and CtiRouteBase::getSQL() (and all inheritors) to be const
* Removed a couple unused Porter functions
* Added logger unit test
* Simplified DebugTimer's variables
*
* Revision 1.25  2008/08/14 15:57:39  jotteson
* YUK-6333  Change naming in request message and change cancellation to use this new named field instead of user ID
* Cancellation now uses the new group message ID.
* Group Message ID name added to Request, Result, Out, and In messages.
*
* Revision 1.24  2007/08/27 18:27:10  jotteson
* YUK-4279
* Added function to remove the dynamic text from control command strings.
*
* Revision 1.23  2006/02/27 23:58:30  tspar
* Phase two of RWTPtrSlist replacement.
*
* Revision 1.22  2006/02/24 00:19:11  tspar
* First Series of replacements of RWTPtrSlist to std::list. Scanner, Pil, Porter.
*
* Revision 1.21  2006/02/17 17:04:34  tspar
* CtiMultiMsg:  replaced RWOrdered with vector<RWCollectable*> throughout the tree
*
* Revision 1.20  2006/01/16 20:46:04  mfisher
* Message Flags naming change
*
* Revision 1.19  2005/12/20 17:20:22  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.18  2005/10/25 14:36:22  cplender
* Have reportControlStart use the command sent as the last command... it is for these guys.
*
* Revision 1.17  2005/09/02 16:19:46  cplender
* Modified the getPutConfigAssignment() method to allow modifier parameters.
*
* Revision 1.16  2005/08/24 20:49:00  cplender
* Restore commands were expiring inappropriately.
*
* Revision 1.15  2005/06/13 19:10:21  cplender
* Working to get the correct messages sent for control history to work right.
*
* Revision 1.14  2005/05/31 21:05:55  cplender
* the cycle "count" is now one based to match versacom and expresscom parse syntax.
* Control history was off by one repeat prior to this checkin.
*
* Revision 1.13  2005/04/15 19:04:10  mfisher
* got rid of magic number debuglevel checks
*
* Revision 1.12  2005/02/17 19:02:58  mfisher
* Removed space before CVS comment header, moved #include "yukon.h" after CVS header
*
* Revision 1.11  2005/02/10 23:23:59  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.10  2005/01/27 17:50:44  cplender
* Added method reportActionItemsToDispatch()
*
* Revision 1.9  2004/12/01 20:12:50  cplender
* Default "control_reduction" is 100, not -1.
*
* Revision 1.8  2004/11/24 17:11:17  cplender
* Working on the configuration of SA receivers.
*
* Revision 1.7  2004/11/05 17:25:59  cplender
*
* Getting 305s to work
*
* Revision 1.6  2004/06/28 16:40:40  cplender
* Added toUpper on the string responses to FORCE case insensitivity.
*
* Revision 1.5  2004/06/23 18:44:13  cplender
* Try that last checkin again... it builds right now.
*
* Revision 1.4  2004/06/23 18:36:56  cplender
* Added control_interval and control_reduction to the grp so the protocol doesn't need to set it.
*
* Revision 1.3  2004/05/24 13:49:46  cplender
* Set retries to 0 for all but 205 commands.
*
* Revision 1.2  2004/05/10 22:35:28  cplender
* Controls require
* OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC
*
* Revision 1.1  2004/03/18 19:46:43  cplender
* Added code to support the SA305 protocol and load group
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "cparms.h"
#include "cmdparse.h"
#include "dev_grp_sa305.h"
#include "expresscom.h"
#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "utility.h"

using std::endl;
using std::list;

CtiDeviceGroupSA305::CtiDeviceGroupSA305() :
_lastSACommandType(SA305_DI_Control)
{
}

CtiDeviceGroupSA305::CtiDeviceGroupSA305(const CtiDeviceGroupSA305& aRef)
{
    *this = aRef;
}

CtiDeviceGroupSA305::~CtiDeviceGroupSA305()
{
}

CtiDeviceGroupSA305& CtiDeviceGroupSA305::operator=(const CtiDeviceGroupSA305& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _loadGroup = aRef.getLoadGroup();
        _lastSACommandType = aRef.getLastSACommandType();
    }

    return *this;
}


CtiTableSA305LoadGroup CtiDeviceGroupSA305::getLoadGroup() const
{
    return _loadGroup;
}
CtiTableSA305LoadGroup& CtiDeviceGroupSA305::getLoadGroup()
{
    return _loadGroup;
}
CtiDeviceGroupSA305& CtiDeviceGroupSA305::setLoadGroup(const CtiTableSA305LoadGroup& aRef)
{
    _loadGroup = aRef;
    return *this;
}

LONG CtiDeviceGroupSA305::getRouteID()
{
    return _loadGroup.getRouteId();
}

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceGroupSA305::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    string tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

string CtiDeviceGroupSA305::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, SA3.groupid, SA3.routeid, "
                                     "SA3.addressusage, SA3.utilityaddress, SA3.groupaddress, SA3.divisionaddress, "
                                     "SA3.substationaddress, SA3.individualaddress, SA3.ratefamily, SA3.ratemember, "
                                     "SA3.ratehierarchy, SA3.loadnumber "
                                   "FROM YukonPAObject YP, Device DV, lmgroupsa305 SA3 "
                                   "WHERE upper (YP.type) = 'SA-305 GROUP' AND YP.paobjectid = SA3.groupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceGroupSA305::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _loadGroup.DecodeDatabaseReader(rdr);
}

INT CtiDeviceGroupSA305::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   nRet = NoError;
    ULONG etime = 0;
    string resultString;

    CtiRouteSPtr Route;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */
    parse.setValue("type", ProtocolSA305Type);
    bool control = (parse.getFlags() & (CMD_FLAG_CTL_SHED | CMD_FLAG_CTL_CYCLE));

    if(control)
    {
        _lastSACommandType =  ( parse.getiValue("sa_f0bit", 1) ? SA305_DI_Control: SA305_DLC_Control );
    }

    if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_SHED)
    {
        int shed_seconds = parse.getiValue("shed",86400);
        if(shed_seconds >= 0)
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_interval", parse.getiValue("shed"));
            parse.setValue("control_reduction", 100 );
            etime = CtiTime().seconds() + shed_seconds;
        }
        else
            nRet = BADPARAM;

    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_CYCLE)
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8);   repeat = repeat > 14 ? 14 : repeat;

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * (repeat+1));

        etime = CtiTime().seconds() + (60 * period * (repeat+1));
    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) & (CMD_FLAG_CTL_RESTORE | CMD_FLAG_CTL_TERMINATE))
    {
        if(!(findStringIgnoreCase(parse.getCommandStr()," dlc") || findStringIgnoreCase(parse.getCommandStr()," di")))
        {
            // We were not explicitly told how to do it.  Must restore the same as was sent last.
            parse.setValue("sa_f0bit", (_lastSACommandType == SA305_DI_Control ? 1 : 0));
        }
        parse.setValue("control_reduction", 0 );
        parse.setValue("control_interval", 0);
    }


    int serial = 0;
    int group = 0;
    int division = 0;
    int sub = 0;

    serial = (int)(getLoadGroup().getIndividual());

    /*  These elements are mandatory for any communicatino */
    parse.setValue("sa_utility", getLoadGroup().getUtility());
    parse.setValue("sa_ratefamily", getLoadGroup().getRateFamily());
    parse.setValue("sa_ratemember", getLoadGroup().getRateMember());
    parse.setValue("sa_hierarchy", getLoadGroup().getHierarchy());
    parse.setValue("serial", serial);

    string au = getLoadGroup().getAddressUsage();

    if(findStringIgnoreCase(au,"R"))     // This is a group addressed command
    {
        parse.setValue("sa_addressusage", TRUE);
    }

    if(findStringIgnoreCase(au,"G"))          group = (int)(getLoadGroup().getGroup());
    if(findStringIgnoreCase(au,"D"))          division = (int)(getLoadGroup().getDivision());
    if(findStringIgnoreCase(au,"S"))          sub = (int)(getLoadGroup().getSubstation());

    // These elements are gravy
    parse.setValue("sa_group", group);
    parse.setValue("sa_division", division);
    parse.setValue("sa_substation", sub);

    parse.setValue("sa_function", getLoadGroup().getFunction());

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        OutMessage->TargetID = getID();
        OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
        // OutMessage->Retry = 0;
        OutMessage->Retry = gConfigParms.getValueAsInt("PORTER_SA_REPEATS", 1);
        OutMessage->ExpirationTime = etime;

        reportActionItemsToDispatch(pReq, parse, vgList);

        /*
         *  Form up the reply here since the ExecuteRequest function will consume the
         *  OutMessage.
         */
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

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << resultString << endl;
        }
    }

    return nRet;
}

string CtiDeviceGroupSA305::getPutConfigAssignment(UINT level)
{
    string assign = string("sa305 assign") +
                       " U" + CtiNumStr(_loadGroup.getUtility()) +
                       " G" + CtiNumStr(_loadGroup.getGroup()) +
                       " D" + CtiNumStr(_loadGroup.getDivision()) +
                       " S" + CtiNumStr(_loadGroup.getSubstation()) +
                       " F" + CtiNumStr(_loadGroup.getRateFamily()) +
                       " M" + CtiNumStr(_loadGroup.getRateMember());

    return  assign;
}



