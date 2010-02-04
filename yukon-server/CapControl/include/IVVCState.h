#pragma once

#include "yukon.h"
#include "ctitime.h"
#include "GroupPointDataRequest.h"

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

    private:
        CtiTime _timeStamp;
        CtiTime _nextControlTime;
        GroupRequestPtr _groupRequest;

        bool _scannedRequest;
        State _state;

};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;
