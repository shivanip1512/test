/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc6510
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2005/03/17 05:14:14 $
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
        int offset = 0;

        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
        {
            offset = 2;
        }
        else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
        {
            offset = 1;
        }

        pReq->setCommandString("control close offset " + CtiNumStr(offset));

        CtiCommandParser new_parse(pReq->CommandString());

        //  NOTE the new parser I'm passing in - i've already mangled the pReq string, so
        //    i need to seal the deal with a new parse
        nRet = Inherited::ExecuteRequest(pReq, new_parse, OutMessage, vgList, retList, outList);
    }
    else
    {
        nRet = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


//  This overrides the processPoints function in dev_dnp, but calls it afterward to do the real processing
void CtiDeviceCBC6510::processPoints( Cti::Protocol::Interface::pointlist_t &points )
{
    Cti::Protocol::Interface::pointlist_t::iterator pt_itr, last_pos;
    int last_offset;

    CtiPointDataMsg *pt_msg;

    last_pos    = points.end();
    last_offset = -1;

    //  we need to find any status values for offsets 1 and 2
    for( pt_itr = points.begin(); pt_itr != points.end(); pt_itr++ )
    {
        pt_msg = *pt_itr;

        //  is it a trip/close message?
        if( pt_msg &&
            (pt_msg->getType() == StatusPointType) &&
            (pt_msg->getId() == PointOffset_Trip || pt_msg->getId() == PointOffset_Close) )
        {
            if( pt_msg->getId() == PointOffset_Trip )
            {
                _trip_info.time    = pt_msg->getTime();
                _trip_info.millis  = pt_msg->getMillis();
                _trip_info.state   = pt_msg->getValue();
            }
            else //  if( pt_msg->getId() == PointOffset_Close )
            {
                _close_info.time   = pt_msg->getTime();
                _close_info.millis = pt_msg->getMillis();
                _close_info.state  = pt_msg->getValue();
            }

            //  check to see if the last two messages were a pair...
            if( last_pos != points.end() )
            {
                //  make sure it was the opposite offset...
                if( last_offset != pt_msg->getId() )
                {
                    //  ... and that the times match exactly
                    if( _trip_info.time == _close_info.time && _trip_info.millis == _close_info.millis )
                    {
                        *last_pos = 0;

                        last_pos    = points.end();
                        last_offset = -1;
                    }
                }
            }

            //  grab the info before we overwrite it
            last_pos    = pt_itr;
            last_offset = pt_msg->getId();


            //  replace the old message with the calculated tristate message
            if( _trip_info.state == 1.0 && _close_info.state == 0.0 )
            {
                pt_msg->setValue(0.0);
            }
            else if( _trip_info.state == 0.0 && _close_info.state == 1.0 )
            {
                pt_msg->setValue(1.0);
            }
            else
            {
                pt_msg->setValue(2.0);
            }

            pt_msg->setId(PointOffset_TripClosePaired);
        }
    }

    //  do the final processing
    Inherited::processPoints(points);
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

