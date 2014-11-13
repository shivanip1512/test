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
    Cti::Protocols::Interface *getProtocol() override;

public:

    CtiDeviceLMI();

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    YukonError_t decode(CtiXfer &xfer, YukonError_t status);
    void sendDispatchResults(CtiConnection &vg_connection);
    void getVerificationObjects(std::queue< CtiVerificationBase * > &vq);

    YukonError_t ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    YukonError_t GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);
    YukonError_t AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 3);
    YukonError_t IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);

    YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime Now, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    void processInboundData(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, std::list<CtiPointDataMsg*> &points, std::string &info );

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

    YukonError_t queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    bool getOutMessage(CtiOutMessage *&OutMessage);
    LONG deviceQueueCommunicationTime() const;
    LONG deviceMaxCommunicationTime() const;
};

