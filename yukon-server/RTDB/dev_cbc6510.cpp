/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc6510
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2005/02/10 23:23:59 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "pt_numeric.h"
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


INT CtiDeviceCBC6510::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    if( parse.getCommand() == ControlRequest && !(parse.getFlags() & CMD_FLAG_OFFSET) )
    {
        int offset;
        CtiDNPBinaryOutputControl::ControlCode controltype;

        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
        {
            offset = 2;
        }
        else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
        {
            offset = 1;
        }
        else
        {
            offset = 0;
        }

        CtiProtocolDNP::dnp_output_point controlout;

        controlout.type   = CtiProtocolDNP::DigitalOutput;
        controlout.offset = offset;

        controlout.dout.control    = CtiDNPBinaryOutputControl::PulseOn;
        controlout.dout.trip_close = CtiDNPBinaryOutputControl::NUL;
        controlout.dout.on_time    = 0;
        controlout.dout.off_time   = 0;
        controlout.dout.count      = 1;
        controlout.dout.queue      = false;
        controlout.dout.clear      = false;

        _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut_Direct, &controlout, 1);

        OutMessage->Port = getPortID();
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();

        _dnp.sendCommRequest( OutMessage, outList );

        nRet = NoError;
    }
    else
    {
        nRet = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


void CtiDeviceCBC6510::processInboundPoints(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &points )
{
    CtiPointDataMsg *tmpMsg;
    CtiPointBase    *point;
    CtiPointNumeric *pNumeric;

    double tmpValue;

    int tripped, closed;

    tripped = -1;
    closed  = -1;

    while( !points.isEmpty() )
    {
        tmpMsg = points.removeFirst();

        if( tmpMsg->getId() == 1 && tmpMsg->getType() == StatusPointType )
        {
            closed  = tmpMsg->getValue();

            delete tmpMsg;
            tmpMsg = NULL;
        }
        else if( tmpMsg->getId() == 2 && tmpMsg->getType() == StatusPointType )
        {
            tripped = tmpMsg->getValue();

            delete tmpMsg;
            tmpMsg = NULL;
        }

        if( tmpMsg != NULL )
        {
            //  !!! getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
            if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId(), tmpMsg->getType())) != NULL )
            {
                tmpMsg->setId(point->getID());

                if( point->isNumeric() )
                {
                    pNumeric = (CtiPointNumeric *)point;

                    tmpValue = pNumeric->computeValueForUOM(tmpMsg->getValue());

                    tmpMsg->setValue(tmpValue);
                }

                retList.append(tmpMsg);
            }
            else
            {
                delete tmpMsg;
            }
        }
    }

    if( (point = getDevicePointOffsetTypeEqual(1, StatusPointType)) != NULL )
    {
        if( tripped >= 0 || closed >= 0 )
        {
            tmpMsg = CTIDBG_new CtiPointDataMsg(point->getID());

            if( tripped == 1 && closed == 0 )
            {
                tmpMsg->setValue( 0.0 );
            }
            else if( tripped == 0 && closed == 1 )
            {
                tmpMsg->setValue( 1.0 );
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                tmpMsg->setValue( 2.0 );
            }

            retList.append(tmpMsg);
        }
    }
}


/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceCBC6510::getDescription(const CtiCommandParser &parse) const
{
   RWCString tmp;

   tmp = "CBC Device: " + getName();

   return tmp;
}

