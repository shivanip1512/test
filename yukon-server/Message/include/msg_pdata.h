#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include "yukon.h"
#include "dlldefs.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "message.h"

#include <windows.h>
#include <rw/rwset.h>

class IM_EX_MSG CtiPointDataMsg : public CtiMessage
{
protected:

   long           _id;
   CtiPointType_t _type;
   unsigned       _quality;            // see pointdefs.h
   unsigned       _tags;               // see pointdefs.h
   unsigned       _attrib;             // see pointdefs.h
   unsigned       _limit;              // set iff ExceedsHigh or Low.
   double         _value;

   CtiTime         _time;
   unsigned       _millis;

   std::string      _str;

private:

    static unsigned int _instanceCount;

    CtiPointType_t resolveType(int type);

public:

   RWDECLARE_COLLECTABLE( CtiPointDataMsg );

   typedef CtiMessage Inherited;

   CtiPointDataMsg(long      id          = 0,
                   double    value       = 0.0,
                   unsigned  quality     = NormalQuality,
                   CtiPointType_t type   = StatusPointType,
                   std::string     valReport  = std::string(),
                   unsigned  tags        = 0,
                   unsigned  attrib      = 0,
                   unsigned  limit       = 0,
                   int       pri         = 7
                   );

   CtiPointDataMsg(const CtiPointDataMsg &aRef);
   virtual ~CtiPointDataMsg();

   CtiPointDataMsg& operator=(const CtiPointDataMsg& aRef);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;


   long  getId() const;
   CtiPointDataMsg& setId( const long a_id );

   const std::string& getString() const;
   CtiPointDataMsg& setString(const std::string& string_value);

   CtiPointType_t getType() const;
   CtiPointDataMsg& setType(CtiPointType_t type);

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

   const CtiTime& getTime() const;
   CtiTime& getTime();
   CtiPointDataMsg& setTime(const CtiTime& aTime);

   unsigned  getMillis() const;
   CtiPointDataMsg& setMillis(unsigned millis);

   CtiPointDataMsg& setTimeWithMillis(const CtiTime& aTime, const unsigned millis);

   virtual void dump() const;

   virtual bool isValid();
   static unsigned int getInstanceCount() { return _instanceCount; }
};

