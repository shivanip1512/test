#pragma warning( disable : 4786 )

#ifndef __DEV_SINGLE_H__
#define __DEV_SINGLE_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_single
*
* Class:  CtiDeviceBase
* Date:   8/19/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_single.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw\cstring.h>
#include <rw\thr\mutex.h>
#include <vector>


#include "dsm2.h"

#include "dev_base.h"
#include "tbl_base.h"
#include "tbl_2way.h"
#include "tbl_stats.h"
#include "tbl_scanrate.h"
// #include "rtdb.h"
#include "yukon.h"
#include "connection.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "tbl_dv_scandata.h"
#include "tbl_dv_wnd.h"

/*
 *  A Single (as opposed to group) device entity which is physical device!
 */
class IM_EX_DEVDB CtiDeviceSingle : public CtiDeviceBase
{
protected:

    CtiTableDevice2Way           _twoWay;
    CtiTableDeviceStatistics     *_statistics[StatTypeInvalid];
    CtiTableDeviceScanRate       *_scanRateTbl[ScanRateInvalid];    // Multiple Scan Rates

    vector<CtiTableDeviceWindow> _windowVector;

    /*
     *  Dynamic data used by scannables...
     */

    BOOL                       _useScanFlags;          // Do we really need to deal with the ScanData?
    CtiTableDeviceScanData     *_scanData;             // Dynamic data only loaded if needed.

    bool validatePendingStatus(bool status, int scantype, RWTime &now = RWTime());

private:

    ULONG getTardyTime(int scantype) const;

public:

    typedef CtiDeviceBase Inherited;

    CtiDeviceSingle();
    CtiDeviceSingle(const CtiDeviceSingle& aRef);

    virtual ~CtiDeviceSingle();

    CtiDeviceSingle& operator=(const CtiDeviceSingle& aRef);

    CtiTableDevice2Way    getTwoWay() const;
    CtiTableDevice2Way&   getTwoWay();

    CtiDeviceSingle& setTwoWay( const CtiTableDevice2Way & aTwoWay );

    BOOL isStatValid(const INT stat) const;

    CtiTableDeviceStatistics getStatistics(const INT i) const;

    CtiTableDeviceStatistics&  getStatistics(const INT i);

    CtiDeviceSingle&  setStatistics(const INT i, CtiTableDeviceStatistics *aStatistics );

    BOOL isRateValid(const INT i) const;

    virtual LONG getScanRate(int a) const;

    virtual void setScanRate(int a, LONG b);

    CtiTableDeviceScanRate  getRateTable(const INT i) const;

    CtiTableDeviceScanRate& getRateTable(const INT i);

    CtiDeviceSingle&     setRateTables(const INT i, const CtiTableDeviceScanRate* aScanRate);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual void DecodeStatisticsDatabaseReader(RWDBReader &rdr);

    virtual void DecodeScanRateDatabaseReader(RWDBReader &rdr);
    virtual void DecodeDeviceWindowDatabaseReader(RWDBReader &rdr);
    void applySignaledRateChange(LONG aOpen, LONG aDuration);

    virtual void DumpData();

    /*
     *  Things which make me into a scannable object.
     */
    INT  resetScanFlags(INT i = 0);
    BOOL useScanFlags() const;

    CtiTableDeviceScanData& getScanData();

    const CtiTableDeviceScanData* getScanData() const;

    BOOL     setUseScanFlags(BOOL b = TRUE);
    BOOL     resetUseScanFlags(BOOL b = FALSE);


    virtual INT ProcessResult(INMESS*,
                              RWTime&,
                              RWTPtrSlist< CtiMessage > &vgList,
                              RWTPtrSlist< CtiMessage > &retList,
                              RWTPtrSlist< OUTMESS > &outList);

    virtual RWTime adjustNextScanTime(const INT scanType = ScanRateGeneral);
    RWTime      firstScan( const RWTime &When, INT rate );
    void        validateScanTimes(bool force = false);

    INT         doDeviceInit(void);
    INT         initiateGeneralScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 11);
    INT         initiateIntegrityScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 11);
    INT         initiateAccumulatorScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 12);
    //  Load Profile gets a low priority so it doesn't butt heads so hard with other reads
    INT         initiateLoadProfileScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 8);

    bool isScanDataValid() const;
    BOOL isScanWindowOpen(RWTime &aNow=RWTime().now()) const;
    BOOL isAlternateRateActive(RWTime &aNow=RWTime().now()) const;
    void checkSignaledAlternateRateForExpiration();

    INT validateScanData();

    BOOL     isScanStarting() const;
    BOOL     setScanStarting(BOOL b = TRUE);
    BOOL     resetScanStarting(BOOL b = FALSE);

    BOOL     isScanIntegrity() const;
    BOOL     setScanIntegrity(BOOL b = TRUE);
    BOOL     resetScanIntegrity(BOOL b = FALSE);

    BOOL     isScanFrozen() const;
    BOOL     setScanFrozen(BOOL b = TRUE);
    BOOL     resetScanFrozen(BOOL b = FALSE);

    BOOL     isScanFreezePending() const;
    BOOL     setScanFreezePending(BOOL b = TRUE);
    BOOL     resetScanFreezePending(BOOL b = FALSE);

    BOOL     isScanPending() const;
    BOOL     setScanPending(BOOL b = TRUE);
    BOOL     resetScanPending(BOOL b = FALSE);

    BOOL     isScanFreezeFailed() const;
    BOOL     setScanFreezeFailed(BOOL b = TRUE);
    BOOL     resetScanFreezeFailed(BOOL b = FALSE);

    BOOL     isScanResetting() const;
    BOOL     setScanResetting(BOOL b = TRUE);
    BOOL     resetScanResetting(BOOL b = FALSE);

    BOOL     isScanResetFailed() const;
    BOOL     setScanResetFailed(BOOL b = TRUE);
    BOOL     resetScanResetFailed(BOOL b = FALSE);

    BOOL     isScanForced() const;
    BOOL     setScanForced(BOOL b = TRUE);
    BOOL     resetScanForced(BOOL b = FALSE);

    BOOL     isScanException() const;
    BOOL     setScanException(BOOL b = TRUE);
    BOOL     resetScanException(BOOL b = FALSE);


    LONG  getLastFreezeNumber() const;
    LONG& getLastFreezeNumber();
    LONG  getPrevFreezeNumber() const;
    LONG& getPrevFreezeNumber();
    RWTime  getLastFreezeTime() const;
    RWTime  getPrevFreezeTime() const;
    RWTime  getLastLPTime();

    CtiDeviceSingle& setLastFreezeNumber( const LONG aLastFreezeNumber );
    CtiDeviceSingle& setLastFreezeTime( const RWTime& aLastFreezeTime );
    CtiDeviceSingle& setPrevFreezeNumber( const LONG aPrevFreezeNumber );
    CtiDeviceSingle& setPrevFreezeTime( const RWTime& aPrevFreezeTime );
    CtiDeviceSingle& setLastLPTime( const RWTime& aLastFreezeTime );

    RWTime nextNearestTime() const;
    RWTime getNextScan(INT a);
    CtiDeviceSingle& setNextScan(INT a, const RWTime &b);

    virtual void invalidateScanRates();
    virtual void deleteNonUpdatedScanRates();
    virtual INT RefreshDevicePoints();

    virtual bool clearedForScan(int scantype);
    virtual void resetForScan(int scantype);
    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;

    RWTime peekDispatchTime() const;

};

#endif // #ifndef __DEV_SINGLE_H__
