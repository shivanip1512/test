
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/08/29 16:31:11 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "master.h"

#include "pointtypes.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_dnp.h"
#include "device.h"
#include "yukon.h"
#include "logger.h"
#include "numstr.h"
#include "cparms.h"


CtiDeviceDNP::CtiDeviceDNP() {}

CtiDeviceDNP::CtiDeviceDNP(const CtiDeviceDNP &aRef)
{
   *this = aRef;
}

CtiDeviceDNP::~CtiDeviceDNP() {}

CtiDeviceDNP &CtiDeviceDNP::operator=(const CtiDeviceDNP &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


CtiProtocolDNP &CtiDeviceDNP::getProtocol( void )
{
    return _dnp;
}


INT CtiDeviceDNP::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    switch( parse.getCommand() )
    {
        case ControlRequest:
            {
                int offset;
                CtiDNPBinaryOutputControl::ControlCode controltype;

                if( parse.getFlags() & CMD_FLAG_OFFSET )
                {
                    offset = parse.getiValue("offset");

                    if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                    {
                        controltype = CtiDNPBinaryOutputControl::PulseOff;
                    }
                    else
                    {
                        controltype = CtiDNPBinaryOutputControl::PulseOn;
                    }

                    CtiProtocolDNP::dnp_output_point controlout;

                    controlout.type   = CtiProtocolDNP::DigitalOutput;
                    controlout.offset = offset;

                    controlout.dout.control    = controltype;
                    controlout.dout.trip_close = CtiDNPBinaryOutputControl::NUL;
                    controlout.dout.on_time    = 0;
                    controlout.dout.off_time   = 0;
                    controlout.dout.count      = 1;
                    controlout.dout.queue      = false;
                    controlout.dout.clear      = false;

                    _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut, &controlout, 1);

                    nRet = NoError;
                }
                //  implied
                /*else
                {
                    nRet = NoMethod;
                }*/

                break;
            }

        case ScanRequest:
            {
                switch( parse.getiValue("scantype") )
                {
                    case ScanRateStatus:
                        break;

                    case ScanRateGeneral:
                        _dnp.setCommand(CtiProtocolDNP::DNP_Class123Read);
                        break;

                    case ScanRateAccum:
                        break;

                    case ScanRateIntegrity:
                        _dnp.setCommand(CtiProtocolDNP::DNP_Class0Read);
                        break;
                }

                nRet = NoError;

                break;
            }

        case PutValueRequest:
            {
                int offset;

                if( parse.getFlags() & CMD_FLAG_PV_ANALOG )
                {
                    CtiProtocolDNP::dnp_output_point controlout;

                    controlout.type = CtiProtocolDNP::AnalogOutput;

                    controlout.aout.value = parse.getiValue("analogvalue");
                    controlout.offset     = parse.getiValue("analogoffset");

                    _dnp.setCommand(CtiProtocolDNP::DNP_SetAnalogOut, &controlout, 1);
                }

                nRet = NoError;

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
    }

    OutMessage->Port = getPortID();
    OutMessage->DeviceID = getID();
    OutMessage->TargetID = getID();

    _dnp.commOut( OutMessage, outList );

    return nRet;
}


INT CtiDeviceDNP::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> dnpPoints;

    _dnp.commIn(InMessage, outList);

    if( _dnp.hasInboundPoints() )
    {
        _dnp.getInboundPoints(dnpPoints);

        while( !dnpPoints.isEmpty() )
        {
            CtiPointDataMsg *tmpMsg = dnpPoints.removeFirst();
            CtiPointBase *point;

            //  !!! getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
            if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId(), tmpMsg->getType())) != NULL )
            {
                tmpMsg->setId(point->getID());

                retList.append(tmpMsg);
            }
        }
    }

    return ErrReturn;
}

INT CtiDeviceDNP::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = new CtiReturnMsg(getID(),
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



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceDNP::getDescription(const CtiCommandParser &parse) const
{
   RWCString tmp;

   //tmp = "CBC Device: " + getName() + " SN: " + CtiNumStr(_cbc.getSerial());

   return tmp;
}


void CtiDeviceDNP::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableDeviceDNP::getSQL(db, keyTable, selector);
}

void CtiDeviceDNP::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _dnpAddress.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _dnp.setSlaveAddress(_dnpAddress.getSlaveAddress());
   _dnp.setMasterAddress(_dnpAddress.getMasterAddress());
}


