/*-----------------------------------------------------------------------------*
*
* File:   pt_dyn_base
*
* Class:  CtiDynamicPointBase
* Date:   6/19/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_dyn_base.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/03/13 19:36:17 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_DYN_BASE_H__
#define __PT_DYN_BASE_H__
#pragma warning( disable : 4786)


#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>

#include "ctidbgmem.h"  // CTIDBG_new
#include "dlldefs.h"

class IM_EX_PNTDB CtiDynamicPointBase : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

private:

public:
   CtiDynamicPointBase() {}

   CtiDynamicPointBase(const CtiDynamicPointBase& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiDynamicPointBase() {}

   CtiDynamicPointBase& operator=(const CtiDynamicPointBase& aRef)
   {
      if(this != &aRef)
      {
      }
      return *this;
   }

   virtual CtiDynamicPointBase*  replicate() const
   {
      return (CTIDBG_new CtiDynamicPointBase(*this));
   }

};
#endif // #ifndef __PT_DYN_BASE_H__
