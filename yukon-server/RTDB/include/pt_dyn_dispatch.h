#pragma once

#include "tbl_pt_alarm.h"
#include "tbl_ptdispatch.h"

#include <boost/shared_ptr.hpp>

class IM_EX_PNTDB CtiDynamicPointDispatch : boost::noncopyable
{
   // Attributes found from the previous execution..
   CtiTablePointDispatch _dispatch;

   bool _archivePending;
   bool _inDelayedData;
   bool _wasArchived;

public:

   CtiDynamicPointDispatch(LONG id,  double initialValue = 0.0, INT qual = UnintializedQuality);

   virtual ~CtiDynamicPointDispatch()  {};

   const CtiTablePointDispatch& getDispatch() const;
   CtiTablePointDispatch& getDispatch();

   double         getValue() const;
   unsigned       getQuality() const;
   CtiTime        getTimeStamp() const;
   unsigned       getTimeStampMillis() const;

   bool isArchivePending() const;
   void setArchivePending(bool pending);

   void setPoint(const CtiTime &NewTime, unsigned millis, double Val, int Qual, unsigned tag_mask);

   bool wasArchived() const;
   void setWasArchived(bool archived);

   CtiTime getNextArchiveTime() const;
   void    setNextArchiveTime(const CtiTime &aTime);

   bool inDelayedData() const;
   void setInDelayedData(bool delayed);
};

typedef boost::shared_ptr< CtiDynamicPointDispatch > CtiDynamicPointDispatchSPtr;

