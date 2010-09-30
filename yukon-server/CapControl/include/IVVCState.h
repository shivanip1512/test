#pragma once

#include "yukon.h"
#include "ctitime.h"
#include "cccapbank.h"
#include "PointDataRequest.h"


#include <boost/shared_ptr.hpp>

class IVVCState
{
    public:

        enum State
        {
            IVVC_DISABLED,
            IVVC_WAIT,
            IVVC_PRESCAN_LOOP,
            IVVC_ANALYZE_DATA,
            IVVC_POST_CONTROL_WAIT,
            IVVC_CONTROLLED_LOOP,
            IVVC_VERIFY_CONTROL_LOOP,
            IVVC_POSTSCAN_LOOP,
            IVVC_COMMS_LOST,
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

        bool isRemoteMode();
        void setRemoteMode(bool remoteMode);

        bool isFirstPass();
        void setFirstPass(bool firstPass);

        CtiTime getNextHeartbeatTime();
        void setNextHeartbeatTime(const CtiTime& time);

        void setCommsRetryCount(const unsigned long retryCount);
        unsigned long getCommsRetryCount() const;

        void setShowVarCheckMsg(const bool flag);
        bool isShowVarCheckMsg() const;

        void setShowBusDisableMsg(const bool flag);
        bool isShowBusDisableMsg() const;

        void setShowLtcAutoModeMsg(const bool flag);
        bool isShowLtcAutoModeMsg() const;

        void setShowNoLtcAttachedMsg(const bool flag);
        bool isShowNoLtcAttachedMsg() const;

        void setCommsLost(const bool flag);
        bool isCommsLost() const;

        void setConsecutiveCapBankOps(const unsigned ops);
        const unsigned getConsecutiveCapBankOps() const;

        void setReportedControllers(const std::set<long>& reportedControllers);
        const std::set<long>& getReportedControllers();

        struct EstimatedData
        {
            bool            operated;
            double          flatness;
            double          powerFactor;
            double          busWeight;
            CtiCCCapBankPtr capbank;

            EstimatedData() : operated(false), flatness(0.0), powerFactor(0.0), busWeight(0.0), capbank(0) {  }
        };

        typedef std::map<long, EstimatedData> EstimatedDataMap;

        EstimatedDataMap    _estimated;

    private:

        CtiTime _timeStamp;
        CtiTime _nextControlTime;
        CtiTime _lastTapOpTime;
        CtiTime _nextHeartbeat;

        PointDataRequestPtr _groupRequest;

        bool _scannedRequest;
        State _state;

        long _controlledId;
        long _paoId;

        bool _remoteMode;
        bool _firstPass;

        bool _showVarCheckFailMsg;
        bool _showSubbusDisableMsg;
        bool _showLtcAutoModeMsg;
        bool _showNoLtcAttachedMsg;
        bool _commsLost;

        unsigned _consecutiveCapBankOps;

        unsigned long _commsRetryCount;
        std::set<long> _reportedControllers;
};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;
