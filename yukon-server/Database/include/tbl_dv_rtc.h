
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_rtc
*
* Class:  CtiTableDeviceRTC
* Date:   3/18/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/03/18 19:50:34 $
* HISTORY      :
* $Log: tbl_dv_rtc.h,v $
* Revision 1.1  2004/03/18 19:50:34  cplender
* Initial Checkin
* Builds, but not too complete.
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_RTC_H__
#define __TBL_DV_RTC_H__

#include "dlldefs.h"

class IM_EX_CTIYUKONDB CtiTableDeviceRTC
{
public:

    enum
    {
        NoLBT = 0,
        WaitForNextSlot = 1,
        WaitForFreqClear = 2,
        OverrideAfterSlot = 3
    } CtiRTCLBTMode_t;

protected:

    LONG    _deviceID;
    int     _rtcAddress;
    bool    _responseBit;       // This will likely always be true.
    int     _lbtMode;           // This is 0-3: As defined in CtiRTCLBTMode_t.

private:

public:
    
    CtiTableDeviceRTC();
    virtual ~CtiTableDeviceRTC();

    LONG getDeviceID() const;

    int  getRTCAddress() const;
    bool getResponseBit() const;
    int  getLBTMode() const;

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    static RWCString getTableName();

    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_RTC_H__
