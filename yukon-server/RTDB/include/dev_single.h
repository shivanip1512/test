#pragma once

#include <vector>
#include <queue>

#include "dsm2.h"

#include "dev_base.h"
#include "tbl_base.h"
#include "tbl_scanrate.h"
#include "yukon.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "tbl_dv_scandata.h"
#include "tbl_dv_wnd.h"
#include "connection.h"
#include "xfer.h"
#include "config_exceptions.h"

#include "exceptions.h"

#include <boost/algorithm/string/join.hpp>
#include <boost/range/adaptor/transformed.hpp>

class CtiVerificationBase;  //  this is so boost_time.h isn't included via verification_objects.h - for now

namespace Cti::Protocols {
    class Interface;
}

/*
 *  A Single (as opposed to group) device entity which is physical device!
 */
class IM_EX_DEVDB CtiDeviceSingle : public CtiDeviceBase
{
    typedef CtiDeviceBase Inherited;

public:
    using ReturnMsgList = std::vector<std::unique_ptr<CtiReturnMsg>>;

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
        ScanDataValid
    };

    enum PointOffsets
    {
        PointOffset_Analog_IPAddress = 20001,
        PointOffset_Analog_Port      = 20002,
    };

    //  this is more extensible than a pair
    struct point_info
    {
        double         value;
        PointQuality_t quality;
        std::string         description;
    };

    typedef std::pair<OUTMESS *, INMESS *> queued_result_t;

protected:

    enum ScanPriorities
    {
        ScanPriority_General     = 11,
        ScanPriority_Integrity   = 11,
        ScanPriority_Accumulator = 12,
        ScanPriority_LoadProfile = 6
    };

    // Return value to allow non error returns
    // where error was handled in code.
    static const YukonError_t ExecutionComplete = ClientErrors::None;

    CtiTableDeviceScanRate       *_scanRateTbl[ScanRateInvalid];    // Multiple Scan Rates

    std::vector<CtiTableDeviceWindow> _windowVector;

    /*
     *  Dynamic data used by scannables...
     */

    BOOL                       _useScanFlags;          // Do we really need to deal with the ScanData?
    CtiTableDeviceScanData     _scanData;

    bool validateClearedForScan(bool clearedForScan, const CtiScanRate_t scantype);

    virtual Cti::Protocols::Interface *getProtocol();

    static unsigned intervalsPerDay(unsigned intervalLength);

    bool isDeviceAddressGlobal();

    virtual CtiTime getDeviceDawnOfTime()          const   { return CtiTime(CtiTime::neg_infin); }
    virtual bool is_valid_time(const CtiTime time) const   { return time.isValid() && !time.is_special(); }

    std::string valueReport(const CtiPointSPtr p,         const point_info &pi, const CtiTime &t = YUKONEOT) const;
    std::string valueReport(const std::string &pointname, const point_info &pi, const CtiTime &t) const;

    virtual std::string resolveStateName(long groupId, long rawValue) const;
    virtual void insertPointDataReport(CtiPointType_t type, int offset, CtiReturnMsg *rm, point_info pi, const std::string &default_pointname="", const CtiTime &timestamp=CtiTime(), double default_multiplier=1.0, int tags=0);

    enum class ExpectMore
    {
        True,
        False
    };

    virtual YukonError_t SubmitRetry(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    YukonError_t reportConfigErrorDetails( 
        const YukonError_t errcode,
        std::string msg, 
        CtiRequestMsg *pReq, 
        ReturnMsgList &returnMsgs );

    YukonError_t reportConfigErrorDetails( 
        const Cti::Devices::InvalidConfigDataException &ex, 
        CtiRequestMsg *pReq, 
        ReturnMsgList &returnMsgs );

    YukonError_t reportConfigErrorDetails( 
        const Cti::Devices::MissingConfigDataException &ex, 
        CtiRequestMsg *pReq, 
        ReturnMsgList &returnMsgs );

    /**
    * Add a configuration mismatch detail to message list.
    *
    * @param msg the message to send.
    * @param pReg pointer to Request Message
    * @param returnMsgs list of messages to return to the client.
    */
    void CtiDeviceSingle::reportConfigDetails(
        std::string &msg,
        CtiRequestMsg *pReq,
        CtiDeviceSingle::ReturnMsgList &returnMsgs );

    /**
    * Report a configuration mismatch to the client.
    *
    * @param setting Setting title
    * @param deviceSetting Device setting value
    * @param configSetting Config setting value
    * @param pReg pointer to Request Message
    * @param returnMsgs list of messages to return to the client.
    */
    void reportConfigMismatchDetails(
        std::string setting,
        CtiRequestMsg *pReq,
        CtiDeviceSingle::ReturnMsgList &returnMsgs );

    template <typename T>
    void reportConfigMismatchDetails(
        std::string setting,
        const T configSetting,
        const boost::optional<T> deviceSetting,
        CtiRequestMsg *pReq,
        CtiDeviceSingle::ReturnMsgList &returnMsgs );


private:

    typedef std::map< int, bool > ScanFlagsPending_t;
    mutable ScanFlagsPending_t _pending_map;       // Scantype, bool pendingState

    virtual void setScanRate(int a, LONG b);

    bool hasRateOrClockChanged(int rate, CtiTime &Now);
    BOOL isAlternateRateActive(bool &bScanIsScheduled, CtiTime &aNow=CtiTime(), int rate = ScanRateInvalid) const;
    BOOL scheduleSignaledAlternateScan( int rate ) const;

    CtiTime peekDispatchTime() const;

    std::string eWordReport(const ESTRUCT &ESt, Cti::Optional<repeater_info> repeater_details) const;

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

    typedef std::map<channelWithID, int > MessageCount_t;
    MessageCount_t _messageCount;
    CtiTime _lastExpirationCheckTime;

public:

    CtiDeviceSingle();
    virtual ~CtiDeviceSingle();

    BOOL isRateValid(const INT i) const;

    virtual LONG getScanRate(int a) const;

    long getTardyInterval(int scantype) const;

    void DecodeDatabaseReader(Cti::RowReader &rdr) override;
    virtual void DecodeScanRateDatabaseReader(Cti::RowReader &rdr);
    virtual void DecodeDeviceWindowDatabaseReader(Cti::RowReader &rdr);
    void applySignaledRateChange(LONG aOpen, LONG aDuration);

    virtual std::string toString() const override;

    /*
     *  Things which make me into a scannable object.
     */
    BOOL useScanFlags() const;

    CtiTableDeviceScanData& getScanData();

    const CtiTableDeviceScanData* getScanData() const;

    BOOL     setUseScanFlags(BOOL b = TRUE);
    BOOL     resetUseScanFlags(BOOL b = FALSE);

    virtual YukonError_t generate(CtiXfer &xfer);
    virtual YukonError_t decode  (CtiXfer &xfer, YukonError_t status);
    virtual YukonError_t recvCommRequest(OUTMESS *OutMessage);
    virtual YukonError_t sendCommResult(INMESS &InMessage);
    virtual bool isTransactionComplete();
    virtual void sendDispatchResults(CtiConnection &vg_connection);

    virtual void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);
    virtual std::vector<queued_result_t> getQueuedResults();

    virtual YukonError_t ProcessResult(const INMESS&, const CtiTime, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual CtiTime adjustNextScanTime(const INT scanType = ScanRateGeneral);
    CtiTime         firstScan( const CtiTime &When, INT rate );
    void           validateScanTimes(bool force = false);

    INT doDeviceInit(void);
    YukonError_t initiateGeneralScan    (OutMessageList &outList, INT ScanPriority = ScanPriority_General);
    YukonError_t initiateIntegrityScan  (OutMessageList &outList, INT ScanPriority = ScanPriority_Integrity);
    YukonError_t initiateAccumulatorScan(OutMessageList &outList, INT ScanPriority = ScanPriority_Accumulator);
    //  Load Profile gets a low priority so it doesn't butt heads so hard with other reads
    YukonError_t initiateLoadProfileScan(OutMessageList &outList, INT ScanPriority = ScanPriority_LoadProfile);

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

    virtual bool clearedForScan(const CtiScanRate_t scantype);
    virtual void resetForScan  (const CtiScanRate_t scantype);
    virtual bool processAdditionalRoutes( const INMESS &InMessage, int nRet ) const;
    virtual bool hasLongScanRate(const std::string &cmd) const;

    CtiTime getNextWindowOpen() const;
    static CtiScanRate_t desolveScanRateType( const std::string &cmd );
    bool removeWindowType( int window_type = -1 );              // Default Argument removes ALL windows.

    int getGroupMessageCount(long userID, Cti::ConnectionHandle client);
    void incrementGroupMessageCount(long userID, Cti::ConnectionHandle client, int entries = 1);
    void decrementGroupMessageCount(long userID, Cti::ConnectionHandle client, int entries = 1);

    virtual boost::optional<std::string> getTransactionReport();
    virtual void clearTransactionReport();

};


typedef boost::shared_ptr<CtiDeviceSingle> CtiDeviceSingleSPtr;

