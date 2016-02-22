#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "mgr_fdrpoint.h"
#include "mutex.h"

class IM_EX_FDRBASE CtiFDRPointList
{

    public:
        DEBUG_INSTRUMENTATION

        CtiFDRPointList();

        ~CtiFDRPointList();

        CtiFDRPointList& operator=( CtiFDRPointList &other );

        CtiMutex& getMutex (void);

        CtiFDRManager *getPointList (void) const;
        CtiFDRPointList& setPointList (CtiFDRPointList &aList);
        CtiFDRPointList& setPointList (CtiFDRManager *aList);

        void deletePointList ();

    private:

        CtiFDRManager       *iPointList;
        CtiMutex            iMux;
};
