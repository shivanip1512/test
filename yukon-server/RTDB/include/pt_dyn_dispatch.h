
#pragma warning( disable : 4786)
#ifndef __PT_DYN_DISPATCH_H__
#define __PT_DYN_DISPATCH_H__

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
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

   BOOL           getArchivePending() const;
   BOOL           isArchivePending() const;
   CtiDynamicPointDispatch&  setArchivePending(BOOL b = TRUE);
   CtiDynamicPointDispatch& setPoint(const RWTime &NewTime, double Val, int Qual, UINT tag_mask);

   VOID*          getAttachment();
   void           setAttachment(VOID *aptr);


   RWTime         getNextArchiveTime() const;
   // RWTime&        getNextArchiveTime();
   CtiDynamicPointDispatch&  setNextArchiveTime(const RWTime &aTime);

   INT getLastSignal() const;
   CtiDynamicPointDispatch& setLastSignal( const INT &aInt );
};
#endif // #ifndef __PT_DYN_DISPATCH_H__
