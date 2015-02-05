#pragma once

#include "dev_remote.h"
#include "queue.h"
#include "tbl_dv_rtc.h"

#include <list>

class IM_EX_DEVDB CtiDeviceRTC : public CtiDeviceRemote
{
    typedef CtiDeviceRemote Inherited;

    LONG _millis;

    static ULONG messageDuration(const int groupType);

    std::queue< CtiVerificationBase * > _verification_objects;

protected:

    typedef std::list< std::pair< CtiTime, CtiOutMessage* > > CtiRepeatCol;

    CtiTableDeviceRTC _rtcTable;

    CtiQueue< CtiOutMessage, std::greater<CtiOutMessage> > _workQueue;

    CtiTime _repeatTime;                                             // This is the time assigned to any OM placed on the list!
    CtiRepeatCol _repeatList;

    INT queueRepeatToDevice(OUTMESS *&OutMessage);
    void addVerificationForOutMessage(CtiOutMessage &OutMessage);
    void writeCodeToSimulatorLog(const CtiSAData &SASt) const;

public:

    CtiDeviceRTC();
    virtual ~CtiDeviceRTC();

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getAddress() const;
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    unsigned queuedWorkCount() const;
    virtual bool hasQueuedWork() const;
    virtual YukonError_t queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    virtual bool getOutMessage(CtiOutMessage *&OutMessage);
    virtual LONG deviceQueueCommunicationTime() const;
    virtual LONG deviceMaxCommunicationTime() const;

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t IntegrityScan (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);
    YukonError_t GeneralScan   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);

    YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList);

    INT prepareOutMessageForComms(CtiOutMessage *&OutMessage);
    void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);
};
