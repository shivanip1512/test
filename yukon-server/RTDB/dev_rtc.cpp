
/*-----------------------------------------------------------------------------*
*
* File:   dev_rtc
*
* Date:   3/18/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/03/19 15:56:16 $
*
* HISTORY      :
* $Log: dev_rtc.cpp,v $
* Revision 1.2  2004/03/19 15:56:16  cplender
* Adding the RTC and non-305 SA protocols.
*
* Revision 1.1  2004/03/18 19:50:34  cplender
* Initial Checkin
* Builds, but not too complete.
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "dev_rtc.h"

#include "msg_cmd.h"
#include "msg_lmcontrolhistory.h"
#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "pt_accum.h"


CtiDeviceRTC::CtiDeviceRTC()
{
}

CtiDeviceRTC::CtiDeviceRTC(const CtiDeviceRTC &aRef)
{
    *this = aRef;
}

CtiDeviceRTC::~CtiDeviceRTC()
{
}

CtiDeviceRTC &CtiDeviceRTC::operator=(const CtiDeviceRTC &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}

INT CtiDeviceRTC::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


INT CtiDeviceRTC::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


INT CtiDeviceRTC::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    OutMessage->Port     = getPortID();
    OutMessage->DeviceID = getID();
    OutMessage->TargetID = getID();

    OutMessage->Remote   = _rtcTable.getRTCAddress();
    OutMessage->Retry    = 2;

    switch( parse.getCommand() )
    {
    case ControlRequest:
        {
            int offset, on_time, off_time;
            CtiPointBase   *point   = NULL;
            CtiPointStatus *control = NULL;

            if( parse.getiValue("point") > 0 )
            {
                if( (point = getDevicePointEqual(parse.getiValue("point"))) != NULL )
                {
                    if( point->isStatus() )
                    {
                        control = (CtiPointStatus *)point;
                    }
                }
            }
            else if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                offset = parse.getiValue("offset");

                control = (CtiPointStatus*)getDeviceControlPointOffsetEqual(offset);
            }

            if( control != NULL )
            {
                if( control->getPointStatus().getControlType() > NoneControlType &&
                    control->getPointStatus().getControlType() < InvalidControlType )
                {
                    //  NOTE - the control duration is completely arbitrary here.  Fix sometime if necessary
                    //           (i.e. customer doing sheds/restores that need to be accurately LMHist'd)
                    CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg(getID(), control->getPointID(), 0, RWTime(), 86400, 100);

                    //  if the control is latched
                    if( control->getPointStatus().getControlType() == LatchControlType ||
                        control->getPointStatus().getControlType() == SBOLatchControlType )
                    {
                        if( parse.getCommandStr().contains(control->getPointStatus().getStateZeroControl(), RWCString::ignoreCase) )      //  CMD_FLAG_CTL_OPEN
                        {
                            hist->setRawState(STATEZERO);
                        }
                        else if( parse.getCommandStr().contains(control->getPointStatus().getStateOneControl(), RWCString::ignoreCase) )  //  CMD_FLAG_CTL_CLOSE
                        {
                            hist->setRawState(STATEONE);
                        }

                        offset      = control->getPointStatus().getControlOffset();
                        on_time     = 0;
                        off_time    = 0;
                    }
                    else  //  assume pulsed
                    {
                        if( parse.getCommandStr().contains(control->getPointStatus().getStateZeroControl(), RWCString::ignoreCase) )      //  CMD_FLAG_CTL_OPEN
                        {
                            on_time     = control->getPointStatus().getCloseTime1();
                            hist->setRawState(STATEZERO);
                        }
                        else if( parse.getCommandStr().contains(control->getPointStatus().getStateOneControl(), RWCString::ignoreCase) )  //  CMD_FLAG_CTL_CLOSE
                        {
                            on_time     = control->getPointStatus().getCloseTime2();
                            hist->setRawState(STATEONE);
                        }

                        offset      = control->getPointStatus().getControlOffset();
                        off_time    = 0;
                    }


                    hist->setMessagePriority(MAXPRIORITY - 1);
                    vgList.insert(hist);

                    if(control->isPseudoPoint() )
                    {
                        if( (control->getPointStatus().getControlType() == SBOPulseControlType ||
                             control->getPointStatus().getControlType() == SBOLatchControlType) && !parse.isKeyValid("sbo_operate") )
                        {
                            //  we have to wait until we're sending the operate to send the pseudo data
                        }
                        else
                        {
                            // There is no physical point to observe and respect.  We lie to the control point.
                            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(control->getID(),
                                                                                (DOUBLE)hist->getRawState(),
                                                                                NormalQuality,
                                                                                StatusPointType,
                                                                                RWCString("This point has been controlled"));
                            pData->setUser(pReq->getUser());
                            vgList.insert(pData);
                        }

                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                OutMessage->ExpirationTime = RWTime().seconds() + control->getControlExpirationTime();
            }
            else
            {
                if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                on_time  = 0;
                off_time = 0;
            }

            if( (control != NULL) && control->getPointStatus().getControlInhibit() )
            {
                nRet = NoMethod;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                nRet = NoError;
            }

            break;
        }
    case ScanRequest:
        {
            switch( parse.getiValue("scantype") )
            {
            case ScanRateStatus:
                {
                    nRet = NoMethod;
                    break;
                }
            case ScanRateGeneral:
                {
                    nRet = NoError;
                    break;
                }
            case ScanRateAccum:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "Accumulator scanrates not defined for DNP devices - check the DeviceScanRate table" << endl;
                }
            case ScanRateIntegrity:
                {
                    nRet = NoError;
                    break;
                }
            }


            break;
        }
    case PutConfigRequest:
        {
            if(parse.isKeyValid("timesync"))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                nRet = NoError;
            }

            break;
        }

    case GetConfigRequest:
    case PutValueRequest:
    case GetValueRequest:
    case GetStatusRequest:
    case PutStatusRequest:
    case LoopbackRequest:
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    if( nRet == NoError )
    {
        if( OutMessage != NULL )
        {
            outList.insert(OutMessage);
            OutMessage = NULL;
        }
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


INT CtiDeviceRTC::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    RWCString resultString;
    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       RWCString(InMessage->Return.CommandStr),
                                                       getName() + " / operation failed",
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID);

        retList.append(retMsg);
    }

    return ErrReturn;
}


INT CtiDeviceRTC::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                                     RWCString(InMessage->Return.CommandStr),
                                                     RWCString(),
                                                     InMessage->EventCode & 0x7fff,
                                                     InMessage->Return.RouteID,
                                                     InMessage->Return.MacroOffset,
                                                     InMessage->Return.Attempt,
                                                     InMessage->Return.TrxID,
                                                     InMessage->Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointBase     *commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    if( pPIL != NULL )
    {
        CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if(pMsg != NULL)
        {
            pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
            pMsg->insert(OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(getID());          // The id (device or point which failed)
            pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            if(InMessage->EventCode != 0)
            {
                pMsg->insert(InMessage->EventCode);
            }
            else
            {
                pMsg->insert(GeneralScanAborted);
            }

            retList.insert( pMsg );
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceRTC::getDescription(const CtiCommandParser &parse) const
{
    return getName();
}


void CtiDeviceRTC::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceRTC::getSQL(db, keyTable, selector);
}

void CtiDeviceRTC::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    _rtcTable.DecodeDatabaseReader(rdr);

    if( getDebugLevel() & 0x0800 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


