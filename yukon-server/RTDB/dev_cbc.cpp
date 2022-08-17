/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.26.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "master.h"

#include "pointtypes.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_cbc.h"
#include "logger.h"
#include "numstr.h"
#include "cparms.h"

using std::list;
using std::string;
using std::endl;

int CtiDeviceCBC::_cbcTries;

CtiDeviceCBC::CtiDeviceCBC()
{
}


LONG CtiDeviceCBC::getRouteID()      // Must be defined!
{
    return _cbc.getRouteID();
}


int CtiDeviceCBC::getCBCRetries( void )
{
    //  check if it's been initialized
    if( _cbcTries <= 0 )
    {
        string retryStr = gConfigParms.getValueAsString("CBC_RETRIES");
        int            tmp = atol( retryStr.c_str() );

        if( tmp > 0  )
        {
            _cbcTries = tmp + 1;
        }
        else
        {
            //  default to 3 attempts (2 retries)
            _cbcTries = 3;

            if( isDebugLudicrous() )
            {
                CTILOG_DEBUG(dout, "CBC_RETRIES cparm not found - defaulting CBC retry count to 2 (3 attempts)");
            }
        }
    }

    return _cbcTries - 1;
}


YukonError_t CtiDeviceCBC::ExecuteRequest(CtiRequestMsg     *pReq,
                                 CtiCommandParser  &parse,
                                 OUTMESS          *&OutMessage,
                                 CtiMessageList    &vgList,
                                 CtiMessageList    &retList,
                                 OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;

    //  make sure we keep trying
    OutMessage->Retry = getCBCRetries();

    switch(getType())
    {
    case TYPE_VERSACOMCBC:
        {
            nRet = executeVersacomCBC(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    case TYPE_CBC7010:
    case TYPE_EXPRESSCOMCBC:
        {
            nRet = executeExpresscomCBC(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    case TYPE_FISHERPCBC:
        {
            nRet = executeFisherPierceCBC(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    }

    return nRet;
}

YukonError_t CtiDeviceCBC::executeFisherPierceCBC(CtiRequestMsg     *pReq,
                                                  CtiCommandParser  &parse,
                                                  OUTMESS          *&OutMessage,
                                                  CtiMessageList    &vgList,
                                                  CtiMessageList    &retList,
                                                  OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;
    int   address;

    CtiRouteSPtr Route;
    CtiPointSPtr pPoint;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        memset(&(OutMessage->Buffer.FPSt), 0, sizeof(FPSTRUCT));

        if( (address = getCBC().getSerial()) < 0 )
        {
            //  group addressing is specified by a negative serial number
            address *= -1;

            if( address > 9999 )
            {
                CTILOG_ERROR(dout, "group addressing number > 9999, truncating");

                address %= 10000;
            }

            sprintf((CHAR*)OutMessage->Buffer.FPSt.u.PCC.ADDRS, "0000000" );
            sprintf((CHAR*)OutMessage->Buffer.FPSt.u.PCC.GRP, "%04d", address );
        }
        else
        {
            //  otherwise, just address by serial number
            sprintf((CHAR*)OutMessage->Buffer.FPSt.u.PCC.ADDRS, "%07d", address );
            sprintf((CHAR*)OutMessage->Buffer.FPSt.u.PCC.GRP, "0000");
        }


        /*
         *  The FISHERPIERCE tag is CRITICAL in that it indicates to the subsequent stages which
         *  control path to take with this OutMessage!
         */
        OutMessage->EventCode = ENCODED | FISHERPIERCE | NORESULT;

        /*
         * OK, these are the items we are about to set out to perform..  Any additional signals will
         * be added into the list upon completion of the Execute!
         */
        if(parse.getActionItems().size() == 1)
        {
            pPoint = getDevicePointOffsetTypeEqual(1, StatusPointType);

            if(pPoint)
            {
                double val = (parse.getFlags() & CMD_FLAG_CTL_OPEN) ? (double)STATE_OPENED : (double)STATE_CLOSED;

                resultString = "CBC Control ";

                if( val == ((double)STATE_OPENED) )
                    resultString += "OPENED";
                else
                    resultString += "CLOSED";

                vgList.push_back(CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, StatusPointType, resultString));
            }
            else
            {
                string actn = *(parse.getActionItems().begin());
                string desc = getDescription(parse);

                vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_CAPCONTROL, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unexpected action items is != 1 ("<< parse.getActionItems().size() <<")");
        }

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

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            resultString = "ERROR " + CtiNumStr(nRet).spad(3);
            resultString += " performing command on route " + Route->getName();
            pRet->setStatus(nRet);
            pRet->setResultString(resultString);
            retList.push_back( pRet );
        }
        else
        {
            delete pRet;
        }
    }
    else
    {
        nRet = ClientErrors::BadRoute;

        resultString = " ERROR: Route or Route Transmitter not available for CBC device " + getName();

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


YukonError_t CtiDeviceCBC::executeVersacomCBC(CtiRequestMsg     *pReq,
                                              CtiCommandParser  &parse,
                                              OUTMESS          *&OutMessage,
                                              CtiMessageList    &vgList,
                                              CtiMessageList    &retList,
                                              OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

    CtiRouteSPtr Route;
    CtiPointSPtr pPoint;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        memset(&(OutMessage->Buffer.VSt), 0, sizeof(VSTRUCT));

        OutMessage->Buffer.VSt.Address       = _cbc.getSerial();

        /*
         *  The VERSACOM tag is CRITICAL in that it indicates to the subsequent stages which
         *  control path to take with this OutMessage!
         */
        OutMessage->EventCode    = VERSACOM | NORESULT;

        /*
         * OK, these are the items we are about to set out to perform..  Any additional signals will
         * be added into the list upon completion of the Execute!
         */
        if(parse.getActionItems().size() == 1)
        {
            pPoint = getDevicePointOffsetTypeEqual(1, StatusPointType);

            if(pPoint)
            {
                string controlState;
                double val = (parse.getFlags() & CMD_FLAG_CTL_OPEN) ? (double)STATE_OPENED : (double)STATE_CLOSED;

                controlState = "CBC Control ";
                if( val == ((double)STATE_OPENED) )
                    controlState += "OPENED";
                else
                    controlState += "CLOSED";

                vgList.push_back(CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, StatusPointType, controlState));
            }
            else
            {
                string actn = *(parse.getActionItems().begin());
                string desc = getDescription(parse);

                vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_CAPCONTROL, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unexpected action items is != 1 ("<< parse.getActionItems().size() <<")");
        }

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

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            resultString = "ERROR " + CtiNumStr(nRet).spad(3);
            resultString += " performing command on route " + Route->getName();

            pRet->setStatus(nRet);
            pRet->setResultString(resultString);
            retList.push_back( pRet );
        }
        else
        {
            delete pRet;
        }
    }
    else
    {
        nRet = ClientErrors::BadRoute;

        resultString = " ERROR: Route or Route Transmitter not available for CBC device " + getName();

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
string CtiDeviceCBC::getDescription(const CtiCommandParser & parse) const
{
    string tmp;

    tmp = "CBC Device: " + getName();
    tmp += " SN: " + CtiNumStr(_cbc.getSerial());

    return tmp;
}

const CtiTableDeviceCBC& CtiDeviceCBC::getCBC() const
{
    return _cbc;
}

string CtiDeviceCBC::getSQLCoreStatement() const
{
    static const string sqlCore =   "SELECT "
                                        "YP.paobjectid, "
                                        "YP.category, "
                                        "YP.paoclass, "
                                        "YP.paoname, "
                                        "YP.type, "
                                        "YP.disableflag, "
                                        "DV.deviceid, "
                                        "DV.alarminhibit, "
                                        "DV.controlinhibit, "
                                        "CBC.serialnumber, "
                                        "CBC.routeid "
                                    "FROM "
                                        "YukonPAObject YP "
                                    "JOIN "
                                        "Device DV ON YP.paobjectid = DV.deviceid "
                                    "JOIN "
                                        "DeviceCBC CBC ON YP.PAObjectID = CBC.DEVICEID "
                                    "WHERE " 
                                        "UPPER(YP.Type) NOT IN( "
                                            "'CBC 7020', 'CBC 7022', 'CBC 7023', 'CBC 7024', "
                                            "'CBC DNP',  'CBC 8020', 'CBC 8024', 'CBC LOGICAL')";

    return sqlCore;
}

void CtiDeviceCBC::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    _cbc.DecodeDatabaseReader(rdr);
}


YukonError_t CtiDeviceCBC::executeExpresscomCBC(CtiRequestMsg     *pReq,
                                                CtiCommandParser  &parse,
                                                OUTMESS          *&OutMessage,
                                                CtiMessageList    &vgList,
                                                CtiMessageList    &retList,
                                                OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

    CtiRouteSPtr Route;
    CtiPointSPtr pPoint;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        parse.setValue("type", ProtocolExpresscomType);
        parse.setValue("xc_serial", _cbc.getSerial());

        memset(&(OutMessage->Buffer.VSt), 0, sizeof(VSTRUCT));
        OutMessage->EventCode = NORESULT;

        /*
         * OK, these are the items we are about to set out to perform..  Any additional signals will
         * be added into the list upon completion of the Execute!
         */

        if((pPoint = getDevicePointOffsetTypeEqual(1, StatusPointType)))
        {
            if( parse.getFlags() & (CMD_FLAG_CTL_OPEN | CMD_FLAG_CTL_CLOSE) )
            {
                string controlState;
                double val = (parse.getFlags() & CMD_FLAG_CTL_OPEN) ? (double)STATE_OPENED : (double)STATE_CLOSED;

                controlState = "CBC Control ";
                if( val == ((double)STATE_OPENED) ) controlState += "OPENED";
                else controlState += "CLOSED";

                vgList.push_back(CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, StatusPointType, controlState));
            }
            else if( parse.isKeyValid("xcflip") )
            {

                string actn;
                string desc("CBC Flip Command Executed");

                vgList.push_back(CTIDBG_new CtiSignalMsg(pPoint->getPointID(), pReq->getSOE(), desc, actn, CapControlLogType, SignalEvent, pReq->getUser()));
            }
        }
        else if(parse.getActionItems().size() == 1)
        {
            string actn = *(parse.getActionItems().begin());
            string desc = getDescription(parse);

            vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_CAPCONTROL, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
        }

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

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            resultString = "ERROR " + CtiNumStr(nRet).spad(3);
            resultString += " performing command on route " + Route->getName();

            pRet->setStatus(nRet);
            pRet->setResultString(resultString);
            retList.push_back( pRet );
        }
        else
        {
            delete pRet;
        }
    }
    else
    {
        nRet = ClientErrors::BadRoute;

        resultString = " ERROR: Route or Route Transmitter not available for CBC device " + getName();

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







