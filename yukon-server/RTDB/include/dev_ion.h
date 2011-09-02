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
private:

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
    CtiDeviceION(const CtiDeviceION& aRef);

    virtual ~CtiDeviceION();

    CtiDeviceION& operator=(const CtiDeviceION& aRef);

    //-------  these functions are copied from dev_meter to prevent nasty inheritance/decode problems.
    CtiTableDeviceMeterGroup  getMeterGroup() const;
    CtiTableDeviceMeterGroup& getMeterGroup();
    CtiDeviceION& setMeterGroup( const CtiTableDeviceMeterGroup & aMeterGroup );
    //----
    void setMeterGroupData( const std::string &meterNumber);

    virtual std::string getSQLCoreStatement() const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual std::string getDescription(const CtiCommandParser & parse) const;
    Cti::Protocol::Interface *getProtocol( void );

    //  virtual in case different ION devices need to form up alternate requests for the same command
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, std::list<CtiMessage*>& retList);

    virtual void processInboundData(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, std::list<CtiPointDataMsg*> &pointData, std::list<CtiSignalMsg*> &eventData, std::string &returnInfo, bool expectMore = false );
};

typedef boost::shared_ptr<CtiDeviceION> CtiDeviceIONSPtr;
