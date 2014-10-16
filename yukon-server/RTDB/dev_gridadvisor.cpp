/*-----------------------------------------------------------------------------*
 *
 * File:   dev_gridadvisor.cpp
 *
 * Class:  CtiDeviceGridAdvisor
 * Date:   07/02/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "porter.h"

#include "tbl_ptdispatch.h"
#include "dev_gridadvisor.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "msg_lmcontrolhistory.h"
#include "cmdparse.h"

#include "dlldefs.h"

#include "logger.h"
#include "guard.h"

#include "utility.h"

#include "dllyukon.h"
#include "cparms.h"
#include "numstr.h"

using std::string;
using std::endl;
using std::list;

CtiDeviceGridAdvisor::CtiDeviceGridAdvisor()
{
}

YukonError_t CtiDeviceGridAdvisor::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

    bool found = false;

    switch( parse.getCommand() )
    {
        case ScanRequest:
        {
            switch(parse.getiValue("scantype"))
            {
                case ScanRateStatus:
                case ScanRateGeneral:
                {
                    break;
                }

                case ScanRateAccum:
                {
                    break;
                }

                case ScanRateIntegrity:
                {
                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "Invalid scan type \""<< parse.getiValue("scantype") <<"\" for device \""<< getName() <<"\"");

                    break;
                }
            }

            break;
        }

        case GetStatusRequest:
        {
            break;
        }

        case PutConfigRequest:
        {
            break;
        }

        case ControlRequest:
        {
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unsupported command. Command = "<< parse.getCommand());

            break;
        }
    }


    if( found )
    {
        OutMessage->Port         = getPortID();
        OutMessage->DeviceID     = getID();
        OutMessage->TargetID     = getID();

        CtiReturnMsg *retmsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       string(OutMessage->Request.CommandStr),
                                                       getName() + " / command submitted",
                                                       nRet,
                                                       OutMessage->Request.RouteID,
                                                       OutMessage->Request.RetryMacroOffset,
                                                       OutMessage->Request.Attempt,
                                                       OutMessage->Request.GrpMsgID,
                                                       OutMessage->Request.UserID,
                                                       OutMessage->Request.SOE,
                                                       CtiMultiMsg_vec());

        retmsg->setExpectMore(true);

        retList.push_back(retmsg);
    }
    else
    {
        nRet = ClientErrors::NoMethod;

        CTILOG_ERROR(dout, "Couldn't come up with an operation for device "<< getName() <<". Command: " << pReq->CommandString());

        resultString = "NoMethod or invalid command.";
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                        string(OutMessage->Request.CommandStr),
                                        resultString,
                                        nRet,
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.RetryMacroOffset,
                                        OutMessage->Request.Attempt,
                                        OutMessage->Request.GrpMsgID,
                                        OutMessage->Request.UserID,
                                        OutMessage->Request.SOE,
                                        CtiMultiMsg_vec()));

        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}



YukonError_t CtiDeviceGridAdvisor::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority )
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "GeneralScan for \"" << getName() << "\"");
    }

    pReq->setCommandString("scan general");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}



YukonError_t CtiDeviceGridAdvisor::IntegrityScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority )
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "IntegrityScan for \""<< getName() <<"\"");
    }

    pReq->setCommandString("scan integrity");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}

LONG CtiDeviceGridAdvisor::getPortID() const
{
    return _commport.getPortID();
}

YukonError_t CtiDeviceGridAdvisor::AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority )
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan accumulator");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "Accumulator (EventLog) Scan for \""<< getName() <<"\"");
    }

    pReq->setCommandString("scan accumulator");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}

string CtiDeviceGridAdvisor::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, "
                                     "AD.masteraddress, AD.slaveaddress, AD.postcommwait, CS.portid "
                                   "FROM YukonPAObject YP, Device DV, DeviceAddress AD, DeviceDirectCommSettings CS "
                                   "WHERE upper (YP.paoclass) = 'GRIDADVISOR' AND YP.paobjectid = AD.deviceid AND "
                                     "YP.paobjectid = DV.deviceid AND YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void CtiDeviceGridAdvisor::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

   _address.DecodeDatabaseReader(rdr);
   _commport.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & DEBUGLEVEL_DATABASE )
   {
       CTILOG_DEBUG(dout, "Decoding DB reader");
   }
}

LONG CtiDeviceGridAdvisor::getAddress() const
{
    return _address.getMasterAddress();
}

LONG CtiDeviceGridAdvisor::getMasterAddress() const
{
    return _address.getMasterAddress();
}
