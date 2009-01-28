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
*    REVISION     :  $Revision: 1.4.2.2 $
*    DATE         :  $Date: 2008/11/18 20:11:30 $
      $Log: fdrpointlist.h,v $
      Revision 1.4.2.2  2008/11/18 20:11:30  jmarks
      [YUKRV-525] Comment: YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      * Responded to reviewer comments
      * Changed monitor's version to MUTEX version
      * Other changes for compilation

      Revision 1.4.2.1  2008/11/13 17:23:46  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.4  2008/09/15 21:09:16  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.3  2002/04/16 15:58:45  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:19:01  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

 * 
 *    Rev 1.0   14 Dec 2001 17:13:48   dsutton
 * Initial revision.
****************************************************************************
*/

#ifndef __FDRPOINTLIST_H__
#define __FDRPOINTLIST_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

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

#endif
