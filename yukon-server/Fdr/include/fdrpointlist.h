#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrplist.h
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
*
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/INCLUDE/fdrpointlist.h-arc  $
*    REVISION     :  $Revision: 1.1.1.1 $
*    DATE         :  $Date: 2002/04/12 13:59:40 $
      $Log: fdrpointlist.h,v $
      Revision 1.1.1.1  2002/04/12 13:59:40  cplender
      Imported sources 4/12/01

 * 
 *    Rev 1.0   14 Dec 2001 17:13:48   dsutton
 * Initial revision.
****************************************************************************
*/

#ifndef __FDRPOINTLIST_H__
#define __FDRPOINTLIST_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

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

#endif
