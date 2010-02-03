#pragma once

#include "yukon.h"
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

        IVVCState() : _state(IVVC_WAIT) { }

        State getState() {return _state;}
        void setState(State state) {_state = state;}

    private:

        State _state;
};

typedef boost::shared_ptr<IVVCState> IVVCStatePtr;
