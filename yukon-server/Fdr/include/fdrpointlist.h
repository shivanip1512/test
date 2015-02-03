#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "mgr_fdrpoint.h"

class IM_EX_FDRBASE CtiFDRPointList
{

    public:

        CtiFDRPointList();

        ~CtiFDRPointList();

        CtiFDRPointList& operator=( CtiFDRPointList &other );

        CtiMutex& getMutex (void);

        CtiFDRManager *getPointList (void);
        CtiFDRPointList& setPointList (CtiFDRPointList &aList);
        CtiFDRPointList& setPointList (CtiFDRManager *aList);

        void deletePointList ();

    private:

        CtiFDRManager       *iPointList;
        CtiMutex            iMux;
};
