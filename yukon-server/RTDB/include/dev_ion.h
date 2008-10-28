/*-----------------------------------------------------------------------------*
 *
 * File:   dev_ion.h
 *
 * Class:  CtiDeviceION
 * Date:   06/05/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __DEV_ION_H__
#define __DEV_ION_H__
#pragma warning( disable : 4786 )


#include <windows.h>
#include "ctitypes.h"
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

    string _collectionGroup,
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
    void setMeterGroupData( const string &meterNumber);

    //  getSQL has been modified to left-outer-join the metergroup table so's ION meters can be selected
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual string getDescription(const CtiCommandParser & parse) const;
    Cti::Protocol::Interface *getProtocol( void );

    //  virtual in case different ION devices need to form up alternate requests for the same command
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT ErrorDecode (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore);

    virtual void processInboundData(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, list<CtiPointDataMsg*> &pointData, list<CtiSignalMsg*> &eventData, string &returnInfo, bool expectMore = false );
};

typedef boost::shared_ptr<CtiDeviceION> CtiDeviceIONSPtr;

#endif //  #ifndef __DEV_ION_H__

