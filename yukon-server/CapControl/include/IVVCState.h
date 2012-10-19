#pragma once

#include "yukon.h"
#include "ctitime.h"
#include "cccapbank.h"
#include "PointDataRequest.h"

#include <boost/shared_ptr.hpp>

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
            IVVC_OPERATE_TAP
        };

//        typedef std::map<Zone::IdSet::value_type, int>  TapOperationZoneMap;
        typedef std::map<long, int>  TapOperationZoneMap;

        TapOperationZoneMap     _tapOps;
        CtiTime                 _tapOpDelay;

        IVVCState();

        State getState();
        void setState(State state);

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

        bool _commsLost;

        unsigned _consecutiveCapBankOps;

        unsigned long _commsRetryCount;

        std::set<long> _reportedControllers;
};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;

// use this guy for memoization during bus analysis
struct WattVArValues
{
    long    paoId;
    double  wattValue;
    double  varValue;
};

