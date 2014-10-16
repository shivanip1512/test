/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_emetcon
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_grp_emetcon.cpp-arc  $
* REVISION     :  $Revision: 1.27.2.1 $
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

#include "connection.h"

#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_grp_emetcon.h"

#include "numstr.h"

using std::string;
using std::endl;
using std::list;

void ParserToAStruct(CtiCommandParser &parse, ASTRUCT *ASt)
{
    INT Flags = parse.getFlags();
    int shedtime  = 0;

    if(Flags & CMD_FLAG_CTL_SHED)
    {
        shedtime = parse.getiValue("shed", 0);
    }

    if(Flags & CMD_FLAG_CTL_RESTORE || shedtime == 0)
    {
        ASt->Function = A_RESTORE;
    }

    ASt->Time = resolveAWordTime( shedtime );

}

LONG CtiDeviceGroupEmetcon::getRouteID()      // Must be defined!
{
    return EmetconGroup.getRouteID();
}


YukonError_t CtiDeviceGroupEmetcon::ExecuteRequest(CtiRequestMsg     *pReq,
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

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        switch(parse.getCommand())
        {
        case ControlRequest:
            {
                OutMessage->TargetID            = getID();
                OutMessage->Retry               = 2;                      // Default to two tries per route!

                OutMessage->Buffer.ASt.Group    = EmetconGroup.getEmetconAddress();
                OutMessage->Buffer.ASt.Function = EmetconGroup.getRelay();

                if( parse.isKeyValid("relaynext") )
                {
                    if( OutMessage->Buffer.ASt.Function == A_SHED_A )
                        OutMessage->Buffer.ASt.Function = A_SHED_B;
                    else if( OutMessage->Buffer.ASt.Function == A_SHED_B )
                        OutMessage->Buffer.ASt.Function = A_SHED_C;
                    else if( OutMessage->Buffer.ASt.Function == A_SHED_C )
                        OutMessage->Buffer.ASt.Function = A_SHED_A;
                }

                ParserToAStruct(parse, &(OutMessage->Buffer.ASt));

                OutMessage->EventCode    = AWORD | ACTIN | NOWAIT | NORESULT;

                /*
                 * OK, these are the items we are about to set out to perform..  Any additional signals will
                 * be added into the list upon completion of the Execute!
                 */
                if(parse.getActionItems().size())
                {
                    for(std::list< string >::const_iterator itr = parse.getActionItems().begin();
                         itr != parse.getActionItems().end();
                         ++itr )
                    {
                        string actn = *itr;
                        string desc = getDescription(parse);

                        _lastCommand = generateCommandString(OutMessage);

                        CtiPointStatusSPtr pControlStatus = boost::static_pointer_cast<CtiPointStatus>(getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS ));
                        LONG pid = ( pControlStatus ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );

                        vgList.push_back(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, _lastCommand, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                    }
                }

                /*
                 *  Form up the reply here since the ExecuteRequest funciton will consume the
                 *  OutMessage.
                 */
                resultString = "Control command submitted on route " + CtiNumStr(Route->getRouteID()) + string(": ") + Route->getName();

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

                if( Route->getType() == RouteTypeVersacom )
                {
                    OutMessage->EventCode    |= ENCODED;    // Set this so that versacom works
                }

                if( parse.getControlled() )
                {
                    OutMessage->ExpirationTime = CtiTime().seconds() + gConfigParms.getValueAsInt(GROUP_CONTROL_EXPIRATION, 1200);
                }

                // Get a control request done maybe?
                if( !(nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
                {
                    if(parse.getCommand() == ControlRequest)
                        reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, getLastCommand() );

                    pRet->setStatus(ClientErrors::None);
                }
                else     // An error occured in the processing/communication
                {
                    resultString = "ERROR " + CtiNumStr(nRet).spad(3) + string(" performing control on route ") + CtiNumStr(Route->getRouteID());
                    pRet->setStatus(nRet);
                    pRet->setResultString(resultString);
                }

                retList.push_back( pRet );

                break;
            }
        case GetStatusRequest:
        case LoopbackRequest:
        case GetValueRequest:
        case PutValueRequest:
        case PutStatusRequest:
        case GetConfigRequest:
        case PutConfigRequest:
        default:
            {
                nRet = ClientErrors::NoMethodForExecuteRequest;

                CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                      string(OutMessage->Request.CommandStr),
                                                      string("EMETCON GROUP Class devices do not support this command (yet?)"),
                                                      nRet,
                                                      OutMessage->Request.RouteID,
                                                      OutMessage->Request.RetryMacroOffset,
                                                      OutMessage->Request.Attempt,
                                                      OutMessage->Request.GrpMsgID,
                                                      OutMessage->Request.UserID,
                                                      OutMessage->Request.SOE,
                                                      CtiMultiMsg_vec());

                retList.push_back( pRet );

                vgList.push_back( CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, OutMessage->Request.SOE, "Unsupported command", "ERROR", GeneralLogType, SignalEvent, pReq->getUser()));


                if(OutMessage)
                {
                    delete OutMessage;
                    OutMessage = NULL;
                }

                CTILOG_ERROR(dout, "Unsupported command (" << parse.getCommand() << ") in Emetcon group \"" << getName() << "\"");

                break;
            }
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

string CtiDeviceGroupEmetcon::getDescription(const CtiCommandParser & parse) const
{
    string  ch_relay("Group: " + getName());

    if(EmetconGroup.getRelay() == A_SHED_A) // 0)
    {
        ch_relay += " Relay: A";
    }
    else if(EmetconGroup.getRelay() == A_SHED_B) // 1)
    {
        ch_relay += " Relay: B";
    }
    else if(EmetconGroup.getRelay() == A_SHED_C)
    {
        ch_relay += " Relay: C";
    }
    else if(EmetconGroup.getRelay() == A_SHED_D)
    {
        ch_relay += " Relay: D";
    }
    else if(EmetconGroup.getRelay() == A_RESTORE)
    {
        ch_relay += " Restore";
    }
    else if(EmetconGroup.getRelay() == A_SCRAM)
    {
        ch_relay += " Scram";
    }
    else if(EmetconGroup.getRelay() == A_LATCH_OPEN)
    {
        ch_relay += " Latch Open";
    }
    else if(EmetconGroup.getRelay() == A_LATCH_CLOSE)
    {
        ch_relay += " Latch Close";
    }
    else
    {
        ch_relay += " DB Error. Invalid Emetcon entry.";

        CTILOG_ERROR(dout, ch_relay);
    }

    return ch_relay;
}

CtiDeviceGroupEmetcon::CtiDeviceGroupEmetcon()
{
}

string CtiDeviceGroupEmetcon::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, LMG.goldaddress, LMG.silveraddress, "
                                     "LMG.addressusage, LMG.relayusage, LMG.routeid "
                                   "FROM YukonPAObject YP, Device DV, LMGroupEmetcon LMG "
                                   "WHERE YP.paobjectid = LMG.deviceid AND YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceGroupEmetcon::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    EmetconGroup.DecodeDatabaseReader(rdr);
}

string CtiDeviceGroupEmetcon::generateCommandString(OUTMESS *&OutMessage)
{
    string str("SHED ");

    string tmstr;

    switch(OutMessage->Buffer.ASt.Time)
    {
    case TIME_7_5:
        {
            tmstr = string(" 7.5M");
            break;
        }
    case TIME_15:
        {
            tmstr = string(" 15M");
            break;
        }
    case TIME_30:
        {
            tmstr = string(" 30M");
            break;
        }
    case TIME_60:
        {
            tmstr = string(" 60M");
            break;
        }
    default:
        {
            tmstr = string("RESTORE");
            break;
        }
    }

    switch(OutMessage->Buffer.ASt.Function)
    {
    case A_SHED_A:
        {
            str += string("Relay A") + tmstr;
            break;
        }
    case A_SHED_B:
        {
            str += string("Relay B") + tmstr;
            break;
        }
    case A_SHED_C:
        {
            str += string("Relay C") + tmstr;
            break;
        }
    case A_SHED_D:
        {
            str += string("Relay D") + tmstr;
            break;
        }
    case A_LATCH_OPEN:
        {
            str += string("OPEN");
            break;
        }
    case A_LATCH_CLOSE:
        {
            str += string("CLOSE ");
            break;
        }
    case A_SCRAM:
        {
            str += string("SCRAM ");
            break;
        }
    case A_RESTORE:
    default:
        {
            str = string("RESTORE");
            break;
        }
    }

    return str;
}
