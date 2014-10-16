#include "precompiled.h"

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

const CtiTableSA305LoadGroup& CtiDeviceGroupSA305::getLoadGroup() const
{
    return _loadGroup;
}

LONG CtiDeviceGroupSA305::getRouteID()
{
    return _loadGroup.getRouteId();
}

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
std::string CtiDeviceGroupSA305::getDescription(const CtiCommandParser & parse) const
{
    return "Group: " + getName();
}

std::string CtiDeviceGroupSA305::getSQLCoreStatement() const
{
    static const std::string sqlCore =
        "SELECT"
            " YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag,"
            " DV.deviceid, DV.alarminhibit, DV.controlinhibit, SA3.groupid, SA3.routeid,"
            " SA3.addressusage, SA3.utilityaddress, SA3.groupaddress, SA3.divisionaddress,"
            " SA3.substationaddress, SA3.individualaddress, SA3.ratefamily, SA3.ratemember,"
            " SA3.ratehierarchy, SA3.loadnumber"
        " FROM"
            " YukonPAObject YP, Device DV, lmgroupsa305 SA3"
        " WHERE"
            " upper (YP.type) = 'SA-305 GROUP'"
            " AND YP.paobjectid = SA3.groupid"
            " AND YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceGroupSA305::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    _loadGroup.DecodeDatabaseReader(rdr);
}

YukonError_t CtiDeviceGroupSA305::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    ULONG etime = 0;
    std::string resultString;

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
            nRet = ClientErrors::BadParameter;

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

    std::string au = getLoadGroup().getAddressUsage();

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
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                     OutMessage->Request.CommandStr,
                                                     Route->getName(),
                                                     nRet,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            resultString = "ERROR " + CtiNumStr(nRet).spad(3) + " performing command on route " + Route->getName();
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
        nRet = ClientErrors::NoRouteGroupDevice;

        resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                     OutMessage->Request.CommandStr,
                                                     resultString,
                                                     nRet,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());
        retList.push_back( pRet );

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        CTILOG_ERROR(dout, resultString);
    }

    return nRet;
}

std::string CtiDeviceGroupSA305::getPutConfigAssignment(UINT level)
{
    std::string assign = std::string("sa305 assign") +
                       " U" + CtiNumStr(_loadGroup.getUtility()) +
                       " G" + CtiNumStr(_loadGroup.getGroup()) +
                       " D" + CtiNumStr(_loadGroup.getDivision()) +
                       " S" + CtiNumStr(_loadGroup.getSubstation()) +
                       " F" + CtiNumStr(_loadGroup.getRateFamily()) +
                       " M" + CtiNumStr(_loadGroup.getRateMember());

    return  assign;
}



