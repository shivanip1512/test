#pragma once

#include "yukon.h"
#include "ctitime.h"
#include "GroupPointDataRequest.h"
#include "cccapbank.h"

#include <boost/shared_ptr.hpp>

class IVVCState
{
    public:

        enum State
        {
            IVVC_WAIT,
            IVVC_PRESCAN_LOOP,
            IVVC_ANALYZE_DATA,
            IVVC_POST_CONTROL_WAIT,
            IVVC_CONTROLLED_LOOP,
            IVVC_VERIFY_CONTROL_LOOP,
            IVVC_POSTSCAN_LOOP
        };

        IVVCState();

        State getState();
        void setState(State state);

        bool isScannedRequest();
        void setScannedRequest(bool scannedRequest);

        const CtiTime& getTimeStamp();
        void setTimeStamp(const CtiTime& time);

        const CtiTime& getNextControlTime();
        void setNextControlTime(const CtiTime& time);

        const GroupRequestPtr& getGroupRequest();
        void setGroupRequest(const GroupRequestPtr& groupRequest);

        const CtiTime& getLastTapOpTime();
        void setLastTapOpTime(const CtiTime& opTime);

        void setControlledBankId(long bankId);
        long getControlledBankId() const;

        void setPaoId(long paoId);
        long getPaoId() const;

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

        GroupRequestPtr _groupRequest;

        bool _scannedRequest;
        State _state;

        long _controlledId;
        long _paoId;

};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;
