
#pragma warning( disable : 4786)
#ifndef __PT_DYN_BASE_H__
#define __PT_DYN_BASE_H__

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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/11/15 14:08:24 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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
