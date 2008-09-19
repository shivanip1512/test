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
* REVISION     :  $Revision: 1.33 $
* DATE         :  $Date: 2008/09/19 11:40:41 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SINGLE_H__
#define __DEV_SINGLE_H__
#pragma warning( disable : 4786 )


#include <rw\thr\mutex.h>
#include <vector>
#include <queue>

#include "dsm2.h"

#include "dev_base.h"
#include "tbl_base.h"
#include "tbl_2way.h"
#include "tbl_stats.h"
#include "tbl_scanrate.h"
// #include "rtdb.h"
#include "yukon.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "tbl_dv_scandata.h"
#include "tbl_dv_wnd.h"
#include "connection.h"
#include "prot_base.h"

//#include "verification_objects.h"
class CtiVerificationBase;  //  this is so boost_time.h isn't included via verification_objects.h - for now

/*
 *  A Single (as opposed to group) device entity which is physical device!
 */
class IM_EX_DEVDB CtiDeviceSingle : public CtiDeviceBase
{
private:

    typedef CtiDeviceBase Inherited;

public:
    enum ScanFlags
    {
        ScanStarting = ScanRateInvalid + 1,     // This allows us to be stored in the same set as ScanRateGeneral - ScanRateLoadProfile
        ScanFrozen,                             // This flag is used to indicate a device state.  The device has had its accums frozen.
        ScanFreezePending,                      // This flag is used to indicate device state.  The device has been asked to freeze accumulators.
        ScanFreezeFailed,                       // This flag is used to indicate device state.  The device failed to complete when asked to freeze accumulators.
        ScanResetting,
        ScanResetFailed,
        ScanForced,
        ScanException,
        ScanMeterRead,
        ScanDataValid
    };

    enum PointOffsets
    {
        PointOffset_Analog_IPAddress = 20001,
        PointOffset_Analog_Port      = 20002,
    };

    struct channelWithID  //  This is used for tracking return messages to commander based on channel and id
    {
        int channel;
        int identifier;
        CtiTime creationTime;

        bool channelWithID::operator<(const channelWithID &rhs) const
        {
            if( identifier < rhs.identifier )  return true;
            if( identifier > rhs.identifier )  return false;

            return channel < rhs.channel;
        }
    };


protected:

    enum ScanPriorities
    {
        ScanPriority_General     = 11,
        ScanPriority_Integrity   = 11,
        ScanPriority_Accumulator = 12,
        ScanPriority_LoadProfile = 6
    };

    CtiTableDevice2Way           _twoWay;

    CtiTableDeviceScanRate       *_scanRateTbl[ScanRateInvalid];    // Multiple Scan Rates

    vector<CtiTableDeviceWindow> _windowVector;

    /*
     *  Dynamic data used by scannables...
     */

    BOOL                       _useScanFlags;          // Do we really need to deal with the ScanData?
    CtiTableDeviceScanData     _scanData;

    bool validatePendingStatus(bool status, int scantype, CtiTime &now = CtiTime());

    virtual Cti::Protocol::Interface *getProtocol();

private:

    typedef map< int, bool > ScanFlagsPending_t;
    mutable ScanFlagsPending_t _pending_map;       // Scantype, bool pendingState

    ULONG getTardyTime(int scantype) const;
    bool hasRateOrClockChanged(int rate, CtiTime &Now);
    BOOL isAlternateRateActive(bool &bScanIsScheduled, CtiTime &aNow=CtiTime(), int rate = ScanRateInvalid) const;
    BOOL scheduleSignaledAlternateScan( int rate ) const;

    typedef map<channelWithID, int > MessageCount_t;
    MessageCount_t _messageCount;
    CtiTime _lastExpirationCheckTime;

public:

    CtiDeviceSingle();
    CtiDeviceSingle(const CtiDeviceSingle& aRef);

    virtual ~CtiDeviceSingle();

    CtiDeviceSingle& operator=(const CtiDeviceSingle& aRef);

    CtiTableDevice2Way    getTwoWay() const;
    CtiTableDevice2Way&   getTwoWay();

    CtiDeviceSingle& setTwoWay( const CtiTableDevice2Way & aTwoWay );

    BOOL isStatValid(const INT stat) const;

    BOOL isRateValid(const INT i) const;

    virtual LONG getScanRate(int a) const;

    virtual void setScanRate(int a, LONG b);

    CtiTableDeviceScanRate  getRateTable(const INT i) const;

    CtiTableDeviceScanRate& getRateTable(const INT i);

    CtiDeviceSingle&     setRateTables(const INT i, const CtiTableDeviceScanRate* aScanRate);

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual void DecodeScanRateDatabaseReader(RWDBReader &rdr);
    virtual void DecodeDeviceWindowDatabaseReader(RWDBReader &rdr);
    void applySignaledRateChange(LONG aOpen, LONG aDuration);

    virtual void DumpData();

    /*
     *  Things which make me into a scannable object.
     */
    BOOL useScanFlags() const;

    CtiTableDeviceScanData& getScanData();

    const CtiTableDeviceScanData* getScanData() const;

    BOOL     setUseScanFlags(BOOL b = TRUE);
    BOOL     resetUseScanFlags(BOOL b = FALSE);

    virtual int  generate(CtiXfer &xfer);
    virtual int  decode(CtiXfer &xfer, int status);
    virtual int  recvCommRequest(OUTMESS *OutMessage);
    virtual int  sendCommResult(INMESS *InMessage);
    virtual bool isTransactionComplete();
    virtual void sendDispatchResults(CtiConnection &vg_connection);

    virtual void getVerificationObjects(queue< CtiVerificationBase * > &work_queue);
    virtual void getTargetDeviceStatistics(vector< OUTMESS > &om_statistics);

    virtual INT  ProcessResult(INMESS*,
                               CtiTime&,
                               list< CtiMessage* > &vgList,
                               list< CtiMessage* > &retList,
                               list< OUTMESS* > &outList);

    virtual CtiTime adjustNextScanTime(const INT scanType = ScanRateGeneral);
    CtiTime         firstScan( const CtiTime &When, INT rate );
    void           validateScanTimes(bool force = false);

    INT         doDeviceInit(void);
    INT         initiateGeneralScan    (list< OUTMESS* > &outList, INT ScanPriority = ScanPriority_General);
    INT         initiateIntegrityScan  (list< OUTMESS* > &outList, INT ScanPriority = ScanPriority_Integrity);
    INT         initiateAccumulatorScan(list< OUTMESS* > &outList, INT ScanPriority = ScanPriority_Accumulator);
    //  Load Profile gets a low priority so it doesn't butt heads so hard with other reads
    INT         initiateLoadProfileScan(list< OUTMESS* > &outList, INT ScanPriority = ScanPriority_LoadProfile);

    bool isScanDataValid() const;
    BOOL isWindowOpen(CtiTime &aNow=CtiTime(), CtiTime &opensAt = CtiTime(), CtiDeviceWindow_t windowType = DeviceWindowScan) const;
    void checkSignaledAlternateRateForExpiration();

    INT validateScanData();

    BOOL     isScanFlagSet(int scantype = -1) const;
    BOOL     setScanFlag(int scantype = -1, BOOL b = TRUE);
    BOOL     resetScanFlag(int scantype = -1);

    LONG  getLastFreezeNumber() const;
    LONG& getLastFreezeNumber();
    LONG  getPrevFreezeNumber() const;
    LONG& getPrevFreezeNumber();
    CtiTime  getLastFreezeTime() const;
    CtiTime  getPrevFreezeTime() const;
    CtiTime  getLastLPTime();

    CtiDeviceSingle& setLastFreezeNumber( const LONG aLastFreezeNumber );
    CtiDeviceSingle& setLastFreezeTime( const CtiTime& aLastFreezeTime );
    CtiDeviceSingle& setPrevFreezeNumber( const LONG aPrevFreezeNumber );
    CtiDeviceSingle& setPrevFreezeTime( const CtiTime& aPrevFreezeTime );
    CtiDeviceSingle& setLastLPTime( const CtiTime& aLastFreezeTime );

    CtiTime nextRemoteScan() const;
    CtiTime getNextScan(INT a);
    CtiDeviceSingle& setNextScan(INT a, const CtiTime &b);

    virtual void invalidateScanRates();
    virtual void deleteNonUpdatedScanRates();

    virtual bool clearedForScan(int scantype);
    virtual void resetForScan(int scantype);
    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;
    virtual bool hasLongScanRate(const string &cmd) const;

    CtiTime peekDispatchTime() const;

    CtiTime getNextWindowOpen() const;
    static int desolveScanRateType( const string &cmd );
    bool removeWindowType( int window_type = -1 );              // Default Argument removes ALL windows.

    int getGroupMessageCount(long userID, long comID);
    incrementGroupMessageCount(long userID, long comID, int entries = 1);
    decrementGroupMessageCount(long userID, long comID, int entries = 1);

};


typedef shared_ptr<CtiDeviceSingle> CtiDeviceSingleSPtr;

#endif // #ifndef __DEV_SINGLE_H__
