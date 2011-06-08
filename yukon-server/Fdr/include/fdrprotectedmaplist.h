#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrprotectedmaplist.h
*
*    DATE: 04/26/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: The point lists for the interfaces with mutex protection
*
*    DESCRIPTION: This class implements an object that contains a list of 
*                    fdr points for each interface and a mutex to protect them
*                    when they are being accessed and reloaded
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRPROTECTEDMAPLIST_H__
#define __FDRPROTECTEDMAPLIST_H__


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

#endif
