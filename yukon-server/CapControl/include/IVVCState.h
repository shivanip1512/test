#pragma once

#include "yukon.h"
#include "ctitime.h"
#include "GroupPointDataRequest.h"

#include <boost/shared_ptr.hpp>

/**
 * Holds the current state and needed information for the object
 * being controlled.
 *
 */
class IVVCState
{
    public:

        enum State
        {
            IVVC_WAIT,
            IVVC_PRESCAN_LOOP,
            IVVC_ANALYZE_DATA,
            IVVC_POST_CONTROL_WAIT,
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

        int getControlledBankId();
        void setControlledBankId(int bankId);

        const CtiTime& getLastTapOpTime();
        void setLastTapOpTime(const CtiTime& opTime);

    private:

        CtiTime _timeStamp;
        CtiTime _nextControlTime;
        CtiTime _lastTapOpTime;

        GroupRequestPtr _groupRequest;

        int _controlledBankId;
        bool _scannedRequest;
        State _state;

};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;
