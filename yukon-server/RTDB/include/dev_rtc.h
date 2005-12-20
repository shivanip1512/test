
/*-----------------------------------------------------------------------------*
*
* File:   dev_rtc
*
* Class:  CtiDeviceRTC
* Date:   3/18/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:20:30 $
* HISTORY      :
* $Log: dev_rtc.h,v $
* Revision 1.10  2005/12/20 17:20:30  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.9  2004/11/08 16:24:58  mfisher
* implemented getVerificationObjects() instead of just thinking about it
*
* Revision 1.8  2004/10/29 20:02:11  mfisher
* added verification support
*
* Revision 1.7  2004/10/08 20:32:08  cplender
* Added method queuedWorkCount()
*
* Revision 1.6  2004/05/20 22:43:12  cplender
* Support for repeating 205 messages after n minutes.
*
* Revision 1.5  2004/05/19 14:48:53  cplender
* Exclusion changes
*
* Revision 1.4  2004/05/10 21:35:51  cplender
* Exclusions a'la GRE are a bit closer here.  The proximity exclusions should work ok now.
*
* Revision 1.3  2004/04/29 19:59:10  cplender
* Initial sa protocol/load group support
*
* Revision 1.2  2004/03/19 15:56:16  cplender
* Adding the RTC and non-305 SA protocols.
*
* Revision 1.1  2004/03/18 19:50:34  cplender
* Initial Checkin
* Builds, but not too complete.
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_RTC_H__
#define __DEV_RTC_H__

#include <list>
using std::list;

#include "dev_remote.h"
#include "queue.h"
#include "tbl_dv_rtc.h"

class IM_EX_DEVDB CtiDeviceRTC : public CtiDeviceRemote
{
public:

    typedef list< pair< CtiTime, CtiOutMessage* > > CtiRepeatCol;

protected:

    CtiTableDeviceRTC _rtcTable;

    CtiQueue< CtiOutMessage, less<CtiOutMessage> > _workQueue;

    CtiTime _repeatTime;                                             // This is the time assigned to any OM placed on the list!
    CtiRepeatCol _repeatList;

private:

    LONG _millis;

    static ULONG messageDuration(int groupType);

    queue< CtiVerificationBase * > _verification_objects;

public:

    typedef CtiDeviceRemote Inherited;

    CtiDeviceRTC();
    CtiDeviceRTC(const CtiDeviceRTC& aRef);
    virtual ~CtiDeviceRTC();

    CtiDeviceRTC& operator=(const CtiDeviceRTC& aRef);
    CtiDeviceRTC& setRepeatTime(const CtiTime& aRef);

    const CtiTableDeviceRTC& getRTCTable() const;

    virtual LONG getAddress() const;
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    INT queuedWorkCount() const;
    virtual bool hasQueuedWork() const;
    virtual INT queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    virtual bool getOutMessage(CtiOutMessage *&OutMessage);
    virtual LONG deviceQueueCommunicationTime() const;
    virtual LONG deviceMaxCommunicationTime() const;

    INT queueRepeatToDevice(OUTMESS *&OutMessage, UINT *dqcnt);

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT ErrorDecode (INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    INT prepareOutMessageForComms(CtiOutMessage *&OutMessage);
    void getVerificationObjects(queue< CtiVerificationBase * > &work_queue);

};
#endif // #ifndef __DEV_RTC_H__
