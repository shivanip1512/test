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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
* HISTORY      :
* $Log: dev_rtc.h,v $
* Revision 1.16  2008/10/28 19:21:44  mfisher
* YUK-6589 Scanner should not load non-scannable devices
* refreshList() now takes a list of paoids, which may be empty if it's a full reload
* Changed CtiDeviceBase, CtiPointBase, and CtiRouteBase::getSQL() (and all inheritors) to be const
* Removed a couple unused Porter functions
* Added logger unit test
* Simplified DebugTimer's variables
*
* Revision 1.15  2008/06/06 20:28:44  jotteson
* YUK-6005 Porter LLP expect more set incorrectly
* Added an option to override expect more in the error decode call.
* Made LLP retry 3 times before failing.
*
* Revision 1.14  2007/12/10 23:02:57  jotteson
* YUK-4788 Point data ordering issue
* Fix to queues that were not ordering incoming data the way we wanted.
* CtiMessage's ordering operators now do what you would expect and queue
* now uses the ordering operators.
*
* Revision 1.13  2006/09/21 21:31:39  mfisher
* privatized Inherited typedef
*
* Revision 1.12  2006/02/27 23:58:32  tspar
* Phase two of RWTPtrSlist replacement.
*
* Revision 1.11  2006/02/24 00:19:13  tspar
* First Series of replacements of RWTPtrSlist to std::list. Scanner, Pil, Porter.
*
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
#include "dev_remote.h"
#include "queue.h"
#include "tbl_dv_rtc.h"

class IM_EX_DEVDB CtiDeviceRTC : public CtiDeviceRemote
{
private:

    typedef CtiDeviceRemote Inherited;

    LONG _millis;

    static ULONG messageDuration(int groupType);

    std::queue< CtiVerificationBase * > _verification_objects;

public:

    typedef std::list< std::pair< CtiTime, CtiOutMessage* > > CtiRepeatCol;

protected:

    CtiTableDeviceRTC _rtcTable;

    CtiQueue< CtiOutMessage, std::greater<CtiOutMessage> > _workQueue;

    CtiTime _repeatTime;                                             // This is the time assigned to any OM placed on the list!
    CtiRepeatCol _repeatList;

public:

    CtiDeviceRTC();
    CtiDeviceRTC(const CtiDeviceRTC& aRef);
    virtual ~CtiDeviceRTC();

    CtiDeviceRTC& operator=(const CtiDeviceRTC& aRef);
    CtiDeviceRTC& setRepeatTime(const CtiTime& aRef);

    const CtiTableDeviceRTC& getRTCTable() const;

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getAddress() const;
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    unsigned queuedWorkCount() const;
    virtual bool hasQueuedWork() const;
    virtual INT queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    virtual bool getOutMessage(CtiOutMessage *&OutMessage);
    virtual LONG deviceQueueCommunicationTime() const;
    virtual LONG deviceMaxCommunicationTime() const;

    INT queueRepeatToDevice(OUTMESS *&OutMessage, UINT *dqcnt);

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, std::list< CtiMessage* > &retList);

    INT prepareOutMessageForComms(CtiOutMessage *&OutMessage);
    void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);

};
#endif // #ifndef __DEV_RTC_H__
