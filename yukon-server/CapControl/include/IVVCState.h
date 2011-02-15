#pragma once

#include "yukon.h"
#include "ctitime.h"
#include "cccapbank.h"
#include "PointDataRequest.h"

#include <boost/shared_ptr.hpp>

class IVVCState
{
    public:
        
        struct CommsStatus
        {
            bool cbcsLost;
            bool regulatorsLost;
            bool voltagesLost;

            CommsStatus() : cbcsLost(false), regulatorsLost(false), voltagesLost(false) {  }
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

        bool isRemoteMode();
        void setRemoteMode(bool remoteMode);

        bool isFirstPass();
        void setFirstPass(bool firstPass);

        CtiTime getNextHeartbeatTime();
        void setNextHeartbeatTime(const CtiTime& time);

        void setShowVarCheckMsg(const bool flag);
        bool isShowVarCheckMsg() const;

        void setShowBusDisableMsg(const bool flag);
        bool isShowBusDisableMsg() const;

        void setShowRegulatorAutoModeMsg(const bool flag);
        bool isShowRegulatorAutoModeMsg() const;

        void setShowNoRegulatorAttachedMsg(const bool flag);
        bool isShowNoRegulatorAttachedMsg() const;


        bool isCbcCommsLost() const;
        void setCbcCommsLost(const bool flag);

        bool isRegulatorCommsLost() const;
        void setRegulatorCommsLost(const bool flag);

        bool isVoltageCommsLost() const;
        void setVoltageCommsLost(const bool flag);

        void setCbcCommsRetryCount(const unsigned long retryCount);
        unsigned long getCbcCommsRetryCount() const;

        void setRegulatorCommsRetryCount(const unsigned long retryCount);
        unsigned long getRegulatorCommsRetryCount() const;

        void setVoltageCommsRetryCount(const unsigned long retryCount);
        unsigned long getVoltageCommsRetryCount() const;

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
        bool _showRegulatorAutoModeMsg;
        bool _showNoRegulatorAttachedMsg;

        bool _cbcCommsLost;
        bool _regulatorCommsLost;
        bool _voltageCommsLost;

        unsigned _consecutiveCapBankOps;

        unsigned long _cbcCommsRetryCount;
        unsigned long _regulatorCommsRetryCount;
        unsigned long _voltageCommsRetryCount;

        std::set<long> _reportedControllers;
};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;
