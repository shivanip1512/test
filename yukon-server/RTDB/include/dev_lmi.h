#pragma once

#include "dev_remote.h"
#include "tbl_dv_address.h"
#include "tbl_dv_seriesv.h"
#include "prot_lmi.h"

class IM_EX_DEVDB CtiDeviceLMI : public CtiDeviceRemote
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceLMI(const CtiDeviceLMI&);
    CtiDeviceLMI& operator=(const CtiDeviceLMI&);

    CtiTableDeviceAddress _address;
    CtiTableDeviceSeriesV _seriesv;

    typedef CtiDeviceRemote Inherited;

    CtiDeviceExclusion _lmi_exclusion;
    CtiTime _lastPreload;

protected:

    CtiProtocolLMI _lmi;
    Cti::Protocol::Interface *getProtocol();

public:

    CtiDeviceLMI();

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);

    int decode(CtiXfer &xfer, int status);
    void sendDispatchResults(CtiConnection &vg_connection);
    void getVerificationObjects(std::queue< CtiVerificationBase * > &vq);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 3);
    INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(const INMESS *InMessage, CtiTime &Now, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    void processInboundData(const INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, std::list<CtiPointDataMsg*> &points, std::string &info );

    bool hasExclusions() const;
    void addExclusion(CtiTablePaoExclusion &paox);
    void clearExclusions();
    CtiDeviceExclusion& getExclusion();
    exclusions getExclusions() const;
    CtiTime selectCompletionTime() const;
    bool   isDeviceExcluded(long id) const;
    bool   isExecuting() const;
    void   setExecuting(bool set = true, CtiTime when = CtiTime(YUKONEOT));
    bool   isExecutionProhibited(const CtiTime &now = CtiTime(), LONG did = 0);
    size_t setExecutionProhibited(unsigned long id, CtiTime& releaseTime = CtiTime(YUKONEOT));
    bool   removeInfiniteProhibit(unsigned long id);

    unsigned queuedWorkCount() const;
    bool    hasQueuedWork() const;
    bool    hasPreloadWork() const;
    CtiTime getPreloadEndTime() const;
    LONG    getPreloadBytes() const;
    LONG    getCycleTime() const;
    LONG    getCycleOffset() const;

    INT  queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    bool getOutMessage(CtiOutMessage *&OutMessage);
    LONG deviceQueueCommunicationTime() const;
    LONG deviceMaxCommunicationTime() const;
};

