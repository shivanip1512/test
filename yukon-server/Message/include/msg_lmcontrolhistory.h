/*-----------------------------------------------------------------------------*
*
* File:   msg_lmcontrolhistory
*
* Date:   9/24/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_lmcontrolhistory.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/10/19 20:18:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __MSG_LMCONTROLHISTORY_H__
#define __MSG_LMCONTROLHISTORY_H__

#include <rw/cstring.h>
#include <rw/rwtime.h>
#include "pointdefs.h"
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiLMControlHistoryMsg : public CtiMessage
{
protected:

   long        _paoId;                  //
   long        _pointId;                // Controlled pointid, or zero if NA.
   int         _rawState;               // Targeted State.  Valid entries are INVALID, CONTROLLED, UNCONTROLLED.  Others are undefined.
   RWTime      _startDateTime;          //
   int         _controlDuration;        // Length of control in seconds! Negative means LATCHED control.
   int         _reductionRatio;         // percentage of time the control is in effect. Defaults to 100% of controlDuration.
   RWCString   _controlType;            //
   RWCString   _activeRestore;          // Indicates whether group will time-in or a message is required.
   double      _reductionValue;         // kW reduction on this group.

private:

    static unsigned int _instanceCount;

public:

   RWDECLARE_COLLECTABLE( CtiLMControlHistoryMsg );

   typedef CtiMessage Inherited;

   CtiLMControlHistoryMsg(long       paoid      = 0,
                          long       pointid    = 0,
                          int        raw        = INVALID,
                          RWTime     start      = RWTime(),
                          int        dur        = 0,
                          int        redrat     = 100,
                          RWCString  type       = RWCString("N/A"),
                          RWCString  restore    = RWCString('N'),
                          double     reduce     = 0.0,
                          int        pri        = 7);

   CtiLMControlHistoryMsg(const CtiLMControlHistoryMsg& aRef);
   virtual ~CtiLMControlHistoryMsg();

   CtiLMControlHistoryMsg& operator=(const CtiLMControlHistoryMsg& aRef);

   long getPAOId() const;
   CtiLMControlHistoryMsg& setPAOId( const long a_id );

   long getPointId() const;
   CtiLMControlHistoryMsg& setPointId( const long a_id );

   int getRawState() const;
   CtiLMControlHistoryMsg& setRawState(const int raw);

   const RWTime& getStartDateTime() const;
   CtiLMControlHistoryMsg& setStartDateTime(const RWTime& time);

   int getControlDuration() const;
   CtiLMControlHistoryMsg& setControlDuration(const int duration);

   int getReductionRatio() const;
   CtiLMControlHistoryMsg& setReductionRatio(const int redrat);

   const RWCString& getControlType() const;
   CtiLMControlHistoryMsg& setControlType(const RWCString& string);

   const RWCString& getActiveRestore() const;
   CtiLMControlHistoryMsg& setActiveRestore(const RWCString& string);

   double getReductionValue() const;
   CtiLMControlHistoryMsg& setReductionValue( const double value );

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   virtual void dump() const;

   virtual bool isValid();

   static unsigned int getInstanceCount() { return _instanceCount; }
};
#endif // #ifndef __MSG_LMCONTROLHISTORY_H__
