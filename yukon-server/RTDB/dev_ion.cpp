#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
 *
 * File:   dev_ion.cpp
 *
 * Class:  CtiDeviceION
 * Date:   07/02/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "yukon.h"
#include "porter.h"

#include "dev_ion.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "cmdparse.h"

#include "dlldefs.h"

#include "dupreq.h"
#include "dialup.h"

#include "logger.h"
#include "guard.h"

#include "utility.h"



CtiDeviceION::CtiDeviceION() {}

CtiDeviceION::CtiDeviceION(const CtiDeviceION &aRef)
{
   *this = aRef;
}

CtiDeviceION::~CtiDeviceION() {}

CtiDeviceION &CtiDeviceION::operator=(const CtiDeviceION &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


CtiProtocolBase *CtiDeviceION::getProtocol( void ) const
{
    return (CtiProtocolBase *)&_ion;
}


/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceION::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}


INT CtiDeviceION::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    bool found = false;

    switch( parse.getCommand() )
    {
        case ScanRequest:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Picked ScanRequest" << endl;
            }

            switch(parse.getiValue("scantype"))
            {
            case ScanRateStatus:
            case ScanRateGeneral:
               {
                   _ion.setCommand(CtiProtocolION::ION_ExceptionScan);
                   found = true;
                   break;
               }
            case ScanRateAccum:
            case ScanRateIntegrity:
            default:
               {
                   _ion.setCommand(CtiProtocolION::ION_IntegrityScan);
                   found = true;
                   break;
               }
            }

            break;
        }

        /*
        case ControlRequest:
        {
            int offset;
            CtiIONBinaryOutputControl::ControlCode controltype;

            if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                offset = parse.getiValue("offset");

                if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                {
                    controltype = CtiIONBinaryOutputControl::PulseOff;
                }
                else
                {
                    controltype = CtiIONBinaryOutputControl::PulseOn;
                }

                CtiProtocolION::ion_output_point controlout;

                controlout.type   = CtiProtocolION::DigitalOutput;
                controlout.offset = offset;

                controlout.dout.control    = controltype;
                controlout.dout.trip_close = CtiIONBinaryOutputControl::NUL;
                controlout.dout.on_time    = 0;
                controlout.dout.off_time   = 0;
                controlout.dout.count      = 1;
                controlout.dout.queue      = false;
                controlout.dout.clear      = false;

                _ion.setCommand(CtiProtocolION::ION_SetDigitalOut, &controlout, 1);

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
                    _ion.setCommand(CtiProtocolION::ION_Class123Read);
                    nRet = NoError;
                    break;
                }

                case ScanRateAccum:
                {
                    nRet = NoMethod;
                    break;
                }

                case ScanRateIntegrity:
                {
                    _ion.setCommand(CtiProtocolION::ION_Class0Read);
                    nRet = NoError;
                    break;
                }
            }


            break;
        }

        case PutValueRequest:
        {
            int offset;

            if( parse.getFlags() & CMD_FLAG_PV_ANALOG )
            {
                CtiProtocolION::ion_output_point controlout;

                controlout.type = CtiProtocolION::AnalogOutput;

                controlout.aout.value = parse.getiValue("analogvalue");
                controlout.offset     = parse.getiValue("analogoffset");

                _ion.setCommand(CtiProtocolION::ION_SetAnalogOut, &controlout, 1);

                nRet = NoError;
            }

            break;
        }

        case GetValueRequest:
        case GetStatusRequest:
        {

        }
        case PutStatusRequest:
        case GetConfigRequest:
        case PutConfigRequest:
        case LoopbackRequest:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

    */

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command. Command = " << parse.getCommand() << endl;
            }
            nRet = NoMethod;

            break;
        }
    }


    if( nRet == NoError )
    {
        OutMessage->Port = getPortID();
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();
        _ion.sendCommRequest( OutMessage, outList );
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Couldn't come up with an operation for device " << getName() << endl;
            dout << RWTime() << "   Command: " << pReq->CommandString() << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.insert(CTIDBG_new CtiReturnMsg(getID(),
                                        RWCString(OutMessage->Request.CommandStr),
                                        resultString,
                                        nRet,
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.MacroOffset,
                                        OutMessage->Request.Attempt,
                                        OutMessage->Request.TrxID,
                                        OutMessage->Request.UserID,
                                        OutMessage->Request.SOE,
                                        RWOrdered()));
    }

    return nRet;
}



INT CtiDeviceION::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser CTIDBG_CTIDBG_newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan general");

    status = ExecuteRequest(pReq,CTIDBG_CTIDBG_newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}



INT CtiDeviceION::IntegrityScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser CTIDBG_CTIDBG_newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan integrity");

    status = ExecuteRequest(pReq,CTIDBG_CTIDBG_newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


int CtiDeviceION::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> ionPoints;

    _ion.recvCommResult(InMessage, outList);

    resetScanPending();

    if( _ion.hasInboundPoints() )
    {
        _ion.getInboundPoints(ionPoints);

        processInboundPoints(ionPoints, TimeNow, vgList, retList, outList);
    }

    return ErrReturn;
}


void CtiDeviceION::processInboundPoints(RWTPtrSlist<CtiPointDataMsg> &points, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    while( !points.isEmpty() )
    {
        CtiPointDataMsg *tmpMsg = points.removeFirst();
        CtiPointBase *point;

        //  !!! tmpMsg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
        if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId(), tmpMsg->getType())) != NULL )
        {
            tmpMsg->setId(point->getID());

            retList.append(tmpMsg);
        }
        else
        {
            delete tmpMsg;
        }
    }
}


INT CtiDeviceION::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
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
        //  insert "Sky is falling" messages into pPIL here

        // send the whole mess to dispatch
        if (pPIL->PointData().entries() > 0)
        {
            retList.insert( pPIL );
        }
        else
        {
            delete pPIL;
        }
        pPIL = NULL;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}


void CtiDeviceION::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableDeviceDNP::getSQL(db, keyTable, selector);
}


void CtiDeviceION::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _address.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _ion.setAddresses(_address.getMasterAddress(), _address.getSlaveAddress());
}

