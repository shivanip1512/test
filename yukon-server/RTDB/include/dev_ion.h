#pragma once

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"

#include "tbl_dv_address.h"
#include "prot_ion.h"

class IM_EX_DEVDB CtiDeviceION : public CtiDeviceRemote
{
    typedef CtiDeviceRemote Inherited;

    unsigned long _lastLPTime;

    int _postControlScanCount;

    CtiProtocolION    _ion;
    CtiTableDeviceAddress _address;

    std::string _collectionGroup,
                _testCollectionGroup,
                _meterNumber,
                _billingGroup;


protected:

    CtiTableDeviceMeterGroup MeterGroup;

    void initEventLogPosition(void);

    enum IONConstants
    {
        IONRetries = 2,
        IONPostControlScanMax = 5
    };

public:

    CtiDeviceION();

    void setMeterGroupData( const std::string &meterNumber);

    virtual std::string getSQLCoreStatement() const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    virtual std::string getDescription(const CtiCommandParser & parse) const;
    Cti::Protocols::Interface *getProtocol() override;

    //  virtual in case different ION devices need to form up alternate requests for the same command
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    YukonError_t AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;

    YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList& retList);

    virtual void processInboundData(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, std::list<CtiPointDataMsg*> &pointData, std::list<CtiSignalMsg*> &eventData, std::string &returnInfo, bool expectMore = false );
};

typedef boost::shared_ptr<CtiDeviceION> CtiDeviceIONSPtr;
