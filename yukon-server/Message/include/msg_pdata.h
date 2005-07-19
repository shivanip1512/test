  #pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   msg_pdata
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_pdata.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/07/19 22:48:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __MSG_PDATA_H__
#define __MSG_PDATA_H__

#include <windows.h>
#include <rw/cstring.h>
#include <rw/rwtime.h>
#include <rw/rwset.h>

#include "dlldefs.h"
#include "pointdefs.h"
#include "message.h"

#include "yukon.h"

class IM_EX_MSG CtiPointDataMsg : public CtiMessage
{
protected:

   long           _id;
   int            _type;
   unsigned       _quality;            // see pointdefs.h
   unsigned       _tags;               // see pointdefs.h
   unsigned       _attrib;             // see pointdefs.h
   unsigned       _limit;              // set iff ExceedsHigh or Low.
   double         _value;

   RWTime         _time;
   unsigned       _millis;

   RWCString      _str;

private:

    static unsigned int _instanceCount;

public:

   RWDECLARE_COLLECTABLE( CtiPointDataMsg );

   typedef CtiMessage Inherited;

   CtiPointDataMsg(long       id          = 0,
                   double     value       = 0.0,
                   unsigned   quality     = NormalQuality,
                   int        type        = 0,
                   RWCString  valReport   = RWCString(),
                   unsigned   tags        = 0,
                   unsigned   attrib      = 0,
                   unsigned   limit       = 0,
                   int        pri         = 7,
                   unsigned   millis      = 0
                   );

   CtiPointDataMsg(const CtiPointDataMsg &aRef);
   virtual ~CtiPointDataMsg();

   CtiPointDataMsg& operator=(const CtiPointDataMsg& aRef);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;


   long  getId() const;
   CtiPointDataMsg& setId( const long a_id );

   const RWCString& getString() const;
   CtiPointDataMsg& setString(const RWCString& string_value);

   int getType() const;
   CtiPointDataMsg& setType(int type);

   double getValue() const;
   CtiPointDataMsg& setValue(double value);

   unsigned  getQuality() const;
   CtiPointDataMsg& setQuality( const unsigned a_quality );

   unsigned  getTags() const;
   CtiPointDataMsg& setTags( const unsigned a_tags );
   CtiPointDataMsg& resetTags( const unsigned tag_mask = 0xffffffff );

   unsigned  getAttributes() const;
   CtiPointDataMsg& setAttributes( const unsigned at );

   unsigned  getLimit() const;
   CtiPointDataMsg& setLimit( const unsigned a_limit );

   /* A point data which isExemptable can be _not_ sent by dispatch if its value has not changed */
   unsigned  isExemptable() const;
   CtiPointDataMsg& setExemptionStatus( const unsigned a_ex = 1 );

   const RWTime& getTime() const;
   RWTime& getTime();
   CtiPointDataMsg& setTime(const RWTime& aTime);

   unsigned  getMillis() const;
   CtiPointDataMsg& setMillis(unsigned millis);

   virtual void dump() const;

   virtual bool isValid();
   static unsigned int getInstanceCount() { return _instanceCount; }
};
#endif
