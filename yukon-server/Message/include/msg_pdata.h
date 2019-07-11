#pragma once

#include "yukon.h"
#include "dlldefs.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "message.h"


class IM_EX_MSG CtiPointDataMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiPointDataMsg )

protected:

   long           _id;
   CtiPointType_t _type;
   unsigned       _quality;            // see pointdefs.h
   unsigned       _tags;               // see pointdefs.h
   double         _value;

   CtiTime         _time;
   unsigned       _millis;

   std::string      _str;

private:

    static unsigned int _instanceCount;



public:

    CtiPointType_t resolveType(int type);

   typedef CtiMessage Inherited;

   CtiPointDataMsg(long      id          = 0,
                   double    value       = 0.0,
                   unsigned  quality     = NormalQuality,
                   CtiPointType_t type   = StatusPointType,
                   std::string     valReport  = std::string(),
                   unsigned  tags        = 0);

   CtiPointDataMsg(const CtiPointDataMsg &aRef);
   virtual ~CtiPointDataMsg();

   CtiPointDataMsg& operator=(const CtiPointDataMsg& aRef);

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
   bool isRegistrationUpload() const;
   bool isOldTimestamp() const;

   const CtiTime& getTime() const;
   CtiTime& getTime();
   CtiPointDataMsg& setTime(const CtiTime& aTime);

   unsigned  getMillis() const;
   CtiPointDataMsg& setMillis(unsigned millis);

   CtiPointDataMsg& setTimeWithMillis(const CtiTime& aTime, const unsigned millis);

   std::string getTrackingId() const;

   virtual std::string toString() const override;

   virtual bool isValid();
   static unsigned int getInstanceCount() { return _instanceCount; }
};

