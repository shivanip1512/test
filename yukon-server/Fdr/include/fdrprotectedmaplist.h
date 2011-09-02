#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    

#include "dlldefs.h"
#include "queues.h"

// forward class declarations

typedef std::list<CtiFDRPoint*>  FDRIdMapList;
class IM_EX_FDRBASE CtiFDRProtectedIdMapList 
{                                    

    public:

        CtiFDRProtectedIdMapList();

        ~CtiFDRProtectedIdMapList();

    CtiFDRProtectedIdMapList& operator=( const CtiFDRProtectedIdMapList &other );

    CtiMutex& getMutex (void);

    FDRIdMapList getIdMapList (void) const;
    FDRIdMapList& getIdMapList (void);

    CtiFDRProtectedIdMapList& setIdMapList (CtiFDRProtectedIdMapList &aList);


    private:

        FDRIdMapList        iPointList;
        CtiMutex            iMux;
};
