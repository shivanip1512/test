/*-----------------------------------------------------------------------------*
*
* File:   pt_dyn_dispatch
*
* Class:  CtiDynamicPointDispatch
* Date:   6/19/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_dyn_dispatch.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/11/17 23:39:41 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_DYN_DISPATCH_H__
#define __PT_DYN_DISPATCH_H__
#pragma warning( disable : 4786)

#include <windows.h>

#include "pt_dyn_base.h"
#include "tbl_pt_alarm.h"
#include "tbl_ptdispatch.h"

class IM_EX_PNTDB CtiDynamicPointDispatch : public CtiDynamicPointBase
{
protected:

   // Attributes found from the previous execution..
   CtiTablePointDispatch      _dispatch;

   // This is a little kludge to attach additional functionality to a point.
   VOID                       *_attachment;

   BOOL                       _archivePending;
   INT                        _lastSignal;

   bool                       _inDelayedData;

private:

public:

   typedef CtiDynamicPointBase Inherited;

   CtiDynamicPointDispatch(LONG id,  double initialValue = 0.0, INT qual = UnintializedQuality);

   CtiDynamicPointDispatch(const CtiDynamicPointDispatch& aRef);
   virtual ~CtiDynamicPointDispatch();

   CtiDynamicPointDispatch& operator=(const CtiDynamicPointDispatch& aRef);
   virtual CtiDynamicPointBase* replicate() const;

   const CtiTablePointDispatch& getDispatch() const;
   CtiTablePointDispatch& getDispatch();

   double         getValue() const;
   UINT           getQuality() const;

   RWTime         getTimeStamp() const;
   UINT           getTimeStampMillis() const;

   BOOL           getArchivePending() const;
   BOOL           isArchivePending() const;
   CtiDynamicPointDispatch&  setArchivePending(BOOL b = TRUE);
   CtiDynamicPointDispatch&  setPoint(const RWTime &NewTime, UINT millis, double Val, int Qual, UINT tag_mask);

   VOID*          getAttachment();
   void           setAttachment(VOID *aptr);


   RWTime         getNextArchiveTime() const;
   // RWTime&        getNextArchiveTime();
   CtiDynamicPointDispatch&  setNextArchiveTime(const RWTime &aTime);

   bool inDelayedData() const;
   CtiDynamicPointDispatch&  setInDelayedData(const bool in = true);

};
#endif // #ifndef __PT_DYN_DISPATCH_H__
