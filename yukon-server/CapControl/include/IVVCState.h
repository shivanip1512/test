#pragma once

#include "yukon.h"
#include "ctitime.h"
#include "cccapbank.h"
#include "PointDataRequest.h"
#include "ccutil.h"

#include <boost/shared_ptr.hpp>

struct DmvTestData
{
    std::string
        TestName;

    long
        TestId,
        DataArchivingInterval,
        IntervalDataGatheringDuration,
        CommSuccessPercentage,
        ExecutionID;

    double
        StepSize;
};


struct KeepAliveHelper
{
    std::size_t phaseIndex;
    CtiTime     nextSendTime;

    Cti::CapControl::Phase  getCurrentPhase();

    KeepAliveHelper()
        :   phaseIndex { 0 }
    {
        // empty...
    }
};

struct PowerFlowHelper
{
    bool    valid;
    CtiTime nextCheckTime;

    PowerFlowHelper()
        :   valid { false }
    {
        // empty...
    }
};

class IVVCState
{
    public:
        struct VerificationHelper
        {
            long verificationBankId;
            int  successCount;
            int  failureCount;
            VerificationHelper(long bankId = -1) : verificationBankId(bankId), successCount(0), failureCount(0) { }
        };
        enum State
        {
            IVVC_WAIT,
            IVVC_PRESCAN_LOOP,
            IVVC_ANALYZE_DATA,
            IVVC_POST_CONTROL_WAIT,
            IVVC_CONTROLLED_LOOP,
            IVVC_VERIFY_CONTROL_LOOP,
            IVVC_POSTSCAN_LOOP,
            IVVC_OPERATE_TAP,

            DMV_TEST_SETUP,
            DMV_TEST_DATA_GATHERING_START,
            DMV_TEST_PRESCAN,
            DMV_TEST_PRESCAN_LOOP,
            DMV_TEST_POSTSCAN_PROCESSING,
            DMV_TEST_DECIDE_BUMP_DIRECTION,
            DMV_TEST_CALCULATE_BUMPS,
            DMV_TEST_ISSUE_CONTROLS,
            DMV_POST_BUMP_TEST_DATA_GATHERING_START,
            DMV_POST_BUMP_TEST_SCAN,
            DMV_POST_BUMP_TEST_SCAN_LOOP,
            DMV_POST_BUMP_TEST_SCAN_PROCESSING,
            DMV_TEST_RETURN_BUMP_ISSUE_CONTROLS,
            DMV_RETURN_BUMP_TEST_DATA_GATHERING_START,
            DMV_RETURN_BUMP_TEST_SCAN,
            DMV_RETURN_BUMP_TEST_SCAN_LOOP,
            DMV_RETURN_BUMP_TEST_SCAN_PROCESSING,
            DMV_TEST_END_TEST
        };

        CtiTime dataGatheringEndTime;

        std::map< long, std::pair< double, double > >   feasibilityData;    // pointID -> { min, max } voltages

        std::map<long, double>  storedSetPoints;

        std::string dmvTestStatusMessage;

        std::set<long>  dmvWattVarPointIDs;

        bool dmvRegulatorInAutoMode;

        enum
        {
            Down,
            Up
        }
        bumpDirection;

        KeepAliveHelper keepAlives;

        PowerFlowHelper powerFlow;

        typedef std::map<long, double>  TapOperationZoneMap;

        TapOperationZoneMap     _tapOps;
        CtiTime                 _tapOpDelay;
        TapOperationZoneMap     _undoTapOps;    // keeps track of bump restoration to original state

        bool isIvvcOnline() const;

        IVVCState();

        State getState();
        void setState(State state);

        bool hasDmvTestState();
        void setDmvTestState( std::unique_ptr<DmvTestData> testData );
        void deleteDmvState();
        DmvTestData & getDmvTestData();

        bool isScannedRequest();
        void setScannedRequest(bool scannedRequest);

        const CtiTime& getTimeStamp();
        void setTimeStamp(const CtiTime& time);

        const CtiTime& getNextControlTime();
        void setNextControlTime(const CtiTime& time);

        const PointDataRequestPtr& getGroupRequest();
        void setGroupRequest(const PointDataRequestPtr& groupRequest);

        const CtiTime& getLastTapOpTime();
        void setLastTapOpTime(const CtiTime& opTime);

        void setControlledBankId(long bankId);
        long getControlledBankId() const;

        void setPaoId(long paoId);
        long getPaoId() const;

        bool isFirstPass();
        void setFirstPass(bool firstPass);

        void setShowVarCheckMsg(const bool flag);
        bool isShowVarCheckMsg() const;

        void setShowBusDisableMsg(const bool flag);
        bool isShowBusDisableMsg() const;

        void setShowNoRegulatorAttachedMsg(const bool flag);
        bool isShowNoRegulatorAttachedMsg() const;

        void setShowNoZonesOnBusMsg(const bool flag);
        bool isShowNoZonesOnBusMsg() const;

        bool isCommsLost() const;
        void setCommsLost(const bool flag);

        void setCommsRetryCount(const unsigned long retryCount);
        unsigned long getCommsRetryCount() const;

        void setConsecutiveCapBankOps(const unsigned ops);
        const unsigned getConsecutiveCapBankOps() const;

        void setReportedControllers(const std::set<long>& reportedControllers);
        const std::set<long>& getReportedControllers();

        struct EstimatedData
        {
            bool            operated;
            double          flatness;
            double          powerFactor;
            double          voltViolation;
            double          busWeight;
            CtiCCCapBankPtr capbank;

            EstimatedData()
                : operated(false), flatness(0.0), powerFactor(0.0), voltViolation(0.0), busWeight(0.0), capbank(0)
            {
                // empty
            }
        };

        typedef std::map<long, EstimatedData> EstimatedDataMap;

        EstimatedDataMap    _estimated;

        VerificationHelper _verification;

        bool showZoneRegulatorConfigMsg;

    private:

        CtiTime _timeStamp;
        CtiTime _nextControlTime;
        CtiTime _lastTapOpTime;

        PointDataRequestPtr _groupRequest;

        bool _scannedRequest;
        State _state;

        long _controlledId;
        long _paoId;

        bool _firstPass;

        bool _showVarCheckFailMsg;
        bool _showSubbusDisableMsg;
        bool _showNoRegulatorAttachedMsg;
        bool _showNoZonesOnBusMsg;

        bool _commsLost;

        unsigned _consecutiveCapBankOps;

        unsigned long _commsRetryCount;

        std::set<long> _reportedControllers;

        std::unique_ptr<DmvTestData> _dmvTestData;
};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;

// use this guy for memoization during bus analysis
struct WattVArValues
{
    long    paoId;
    double  wattValue;
    double  varValue;
};

