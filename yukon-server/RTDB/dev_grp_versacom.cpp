/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_versacom
*
* Date:   6/28/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_grp_versacom.cpp-arc  $
* REVISION     :  $Revision: 1.25.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "pt_status.h"
#include "master.h"

#include "pointtypes.h"
#include "connection.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_grp_versacom.h"
#include "logger.h"
#include "utility.h"

#include "numstr.h"

using std::string;
using std::endl;
using std::list;

LONG CtiDeviceGroupVersacom::getRouteID()      // Must be defined!
{
    return VersacomGroup.getRouteID();
}

YukonError_t CtiDeviceGroupVersacom::ExecuteRequest(CtiRequestMsg     *pReq,
                                                    CtiCommandParser  &parse,
                                                    OUTMESS          *&OutMessage,
                                                    CtiMessageList    &vgList,
                                                    CtiMessageList    &retList,
                                                    OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

    CtiRouteSPtr Route;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    if(parse.getiValue("type") != ProtocolVersacomType)
    {
        parse.setValue("type", ProtocolVersacomType);
        parse.parse();  // reparse for vcom specific data items....  This is required in case we got here from a group macro.
    }

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        OutMessage->TargetID = getID();

        OutMessage->Buffer.VSt.Address      = VersacomGroup.getSerial();

        if( OutMessage->Buffer.VSt.Address == 0 )
        {
            if(VersacomGroup.useUtilityID())
                OutMessage->Buffer.VSt.UtilityID = VersacomGroup.getUtilityID();

            if(VersacomGroup.useSection())
                OutMessage->Buffer.VSt.Section   = VersacomGroup.getSection();

            if(VersacomGroup.useClass())
                OutMessage->Buffer.VSt.Class     = VersacomGroup.getClass();

            if(VersacomGroup.useDivision())
                OutMessage->Buffer.VSt.Division  = VersacomGroup.getDivision();
        }

        OutMessage->Buffer.VSt.RelayMask = VersacomGroup.getRelayMask();

        if( parse.isKeyValid("relaynext") )
        {
            if( OutMessage->Buffer.VSt.RelayMask == A_SHED_A )
                OutMessage->Buffer.VSt.RelayMask = A_SHED_B;
            else if( OutMessage->Buffer.VSt.RelayMask == A_SHED_B )
                OutMessage->Buffer.VSt.RelayMask = A_SHED_C;
            else if( OutMessage->Buffer.VSt.RelayMask == A_SHED_C )
                OutMessage->Buffer.VSt.RelayMask = A_SHED_A;
        }

        /*
         *  The VERSACOM tag is CRITICAL in that it indicates to the subsequent stages which
         *  control path to take with this OutMessage!
         */
        OutMessage->EventCode    = VERSACOM | NORESULT;
        OutMessage->Retry        = 2;                      // Default to two tries per route!

        reportActionItemsToDispatch(pReq, parse, vgList);

        /*
         *  Form up the reply here since the ExecuteRequest funciton will consume the
         *  OutMessage.
         */
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                              string(OutMessage->Request.CommandStr),
                                              Route->getName(),
                                              nRet,
                                              OutMessage->Request.RouteID,
                                              OutMessage->Request.RetryMacroOffset,
                                              OutMessage->Request.Attempt,
                                              OutMessage->Request.GrpMsgID,
                                              OutMessage->Request.UserID,
                                              OutMessage->Request.SOE,
                                              CtiMultiMsg_vec());

        if( parse.getControlled() )
        {
            OutMessage->ExpirationTime = CtiTime().seconds() + gConfigParms.getValueAsInt(GROUP_CONTROL_EXPIRATION, 1200);
        }

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
                reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, getLastCommand() );

            delete pRet;
        }
    }
    else
    {
        nRet = ClientErrors::NoRouteGroupDevice;

        resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();

        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                              string(OutMessage->Request.CommandStr),
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

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceGroupVersacom::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    INT   ser;
    INT   op;
    INT   flags = parse.getFlags();
    string tmpStr;


    if( INT_MIN != parse.getiValue("proptest")   ||
        INT_MIN != parse.getiValue("ovuv")       ||
        (INT_MIN != flags && (flags & (CMD_FLAG_CTL_OPEN | CMD_FLAG_CTL_CLOSE))))
    {
        tmpStr = "Group: " + getName();
    }
    else
    {
        tmpStr = "Group: " + getName() + " Relay:";

        for(int i = 0; i < 32; i++)
        {
            if(VersacomGroup.getRelayMask() & (mask << i))
            {
                tmpStr += " r" + CtiNumStr(i+1);
            }
        }
    }

    return tmpStr;
}

string CtiDeviceGroupVersacom::getPutConfigAssignment(UINT modifier)
{
    char assign[128];

    sprintf(assign, "versacom assign u%d s%d c0x%x d0x%x",
            VersacomGroup.getUtilityID(),
            VersacomGroup.getSection(),
            convertHumanFormAddressToVersacom(VersacomGroup.getClass()),
            convertHumanFormAddressToVersacom(VersacomGroup.getDivision()));

    return  string(assign);
}

CtiDeviceGroupVersacom::CtiDeviceGroupVersacom()
{}

string CtiDeviceGroupVersacom::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, LGV.serialaddress, LGV.utilityaddress, "
                                     "LGV.sectionaddress, LGV.classaddress, LGV.divisionaddress, LGV.addressusage, "
                                     "LGV.relayusage, LGV.routeid "
                                   "FROM YukonPAObject YP, Device DV, LMGroupVersacom LGV "
                                   "WHERE YP.paobjectid = LGV.deviceid AND YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceGroupVersacom::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    VersacomGroup.DecodeDatabaseReader(rdr);
}
