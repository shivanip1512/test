#pragma once

#include "dev_remote.h"
#include "queue.h"
#include "tbl_dv_rtc.h"

#include <list>

class IM_EX_DEVDB CtiDeviceRTC : public CtiDeviceRemote
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceRTC(const CtiDeviceRTC&);
    CtiDeviceRTC& operator=(const CtiDeviceRTC&);

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
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    unsigned queuedWorkCount() const;
    virtual bool hasQueuedWork() const;
    virtual YukonError_t queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    virtual bool getOutMessage(CtiOutMessage *&OutMessage);
    virtual LONG deviceQueueCommunicationTime() const;
    virtual LONG deviceMaxCommunicationTime() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(const INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, std::list< CtiMessage* > &retList);

    INT prepareOutMessageForComms(CtiOutMessage *&OutMessage);
    void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);
};
