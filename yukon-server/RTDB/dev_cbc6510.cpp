
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc6510
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/07/19 13:41:54 $
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
#include "dev_cbc6510.h"
#include "device.h"
#include "yukon.h"
#include "logger.h"
#include "numstr.h"
#include "cparms.h"


CtiDeviceCBC6510::CtiDeviceCBC6510() {}

CtiDeviceCBC6510::CtiDeviceCBC6510(const CtiDeviceCBC6510 &aRef)
{
   *this = aRef;
}

CtiDeviceCBC6510::~CtiDeviceCBC6510() {}

CtiDeviceCBC6510 &CtiDeviceCBC6510::operator=(const CtiDeviceCBC6510 &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


CtiProtocolDNP &CtiDeviceCBC6510::getProtocol( void )
{
    return _dnp;
}


INT CtiDeviceCBC6510::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    switch( parse.getCommand() )
    {
        case ControlRequest:
            {
                int offset = (parse.getFlags() & CMD_FLAG_CTL_OPEN) ? 2 : 1;

                CtiProtocolDNP::XferPoint controlout;
                controlout.type   = StatusOutputPointType;
                controlout.offset = offset;
                controlout.value  = 1;

                _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut, &controlout, 1);

                nRet = NoError;

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
                CtiProtocolDNP::XferPoint analogout;
                analogout.type   = AnalogOutputPointType;
                analogout.offset = parse.getiValue("offset");
                analogout.value  = parse.getdValue("dial");

                _dnp.setCommand(CtiProtocolDNP::DNP_SetAnalogOut, &analogout, 1);

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


INT CtiDeviceCBC6510::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist<CtiMessage>&vgList, RWTPtrSlist<CtiMessage>&retList, RWTPtrSlist<OUTMESS>&outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiMessage> dnpPoints;

    _dnp.commIn(InMessage, outList);

    if( _dnp.hasInboundPoints() )
    {
        _dnp.getInboundPoints(dnpPoints);
    }

    //  ACH: filter points

    return ErrReturn;
}

INT CtiDeviceCBC6510::ErrorDecode(INMESS *InMessage, RWTime& Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
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
RWCString CtiDeviceCBC6510::getDescription(const CtiCommandParser &parse) const
{
   RWCString tmp;

   //tmp = "CBC Device: " + getName() + " SN: " + CtiNumStr(_cbc.getSerial());

   return tmp;
}


void CtiDeviceCBC6510::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableDeviceIDLC::getSQL(db, keyTable, selector);
}

void CtiDeviceCBC6510::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _address.DecodeDatabaseReader(rdr);

   if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

   _dnp.setSlaveAddress(_address.getAddress());
}









