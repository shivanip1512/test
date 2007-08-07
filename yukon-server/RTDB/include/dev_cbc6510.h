/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc6510
*
* Class:  CtiDeviceCBC6510
* Date:   5/22/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_cbc.h-arc  $
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2007/08/07 19:56:17 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_CBC6510_H__
#define __DEV_CBC6510_H__
#pragma warning( disable : 4786)


#include "dev_remote.h"
#include "dev_dnp.h"
#include "tbl_dv_idlcremote.h"

class IM_EX_DEVDB CtiDeviceCBC6510 : public Cti::Device::DNP
{
private:

    typedef Cti::Device::DNP Inherited;

    enum
    {
        PointOffset_Close = 1,
        PointOffset_Trip  = 2,

        PointOffset_TripClosePaired = 1
    };

    struct tripclose_info_t
    {
        double state;
        CtiTime time;
        short  millis;
    } _trip_info, _close_info;

protected:

    virtual void processPoints( Cti::Protocol::Interface::pointlist_t &points );

public:

    CtiDeviceCBC6510();
    CtiDeviceCBC6510(const CtiDeviceCBC6510& aRef);
    virtual ~CtiDeviceCBC6510();

    CtiDeviceCBC6510& operator=(const CtiDeviceCBC6510& aRef);

    virtual string getDescription(const CtiCommandParser & parse) const;

    //void processInboundPoints(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, RWTPtrSlist<CtiPointDataMsg> &points );
    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
};


#endif // #ifndef __DEV_CBC_H__
