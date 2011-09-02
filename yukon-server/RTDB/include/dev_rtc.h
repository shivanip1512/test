#pragma once

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
