#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc6510
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2002/08/29 16:46:07 $
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


INT CtiDeviceCBC6510::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    if( parse.getCommand() == ControlRequest  && !(parse.getFlags() & CMD_FLAG_OFFSET) )
    {
        int offset;
        CtiDNPBinaryOutputControl::ControlCode controltype;

        offset = (parse.getFlags() & CMD_FLAG_CTL_OPEN) ? 2 : 1;

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

        _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut, &controlout, 1);

        OutMessage->Port = getPortID();
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();

        _dnp.commOut( OutMessage, outList );

        nRet = NoError;
    }
    else
    {
        nRet = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
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

