#pragma once

#include "message.h"
#include "yukon.h"

class CtiPointDataMsg;

class IM_EX_MSG CtiSignalMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiSignalMsg )

protected:

   long        _id;                 // System point or point id this message is associated with.
   int         _logType;            // Identifies the signal type.
   unsigned    _signalCategory;     // Which alarm category should be addressed. Category one is an event class and not sent.
   std::string   _text;               // What happened
   std::string   _additional;         // Additional Info.  What is special about this happening?
   unsigned    _tags;               // Alarm states;.. Bit field frome pointdefs.h
   std::string   _user;               // Who caused this to happen?
   unsigned    _signalMillis;       // Milliseconds for high-precision/SOE events

   int         _condition;          // This is the alarm condition represented by this message


   unsigned    _logid;              // LogID in the systemlog... Zero when not in use or unknown.  No streaming.

   double      _point_value;

private:

    static unsigned int _instanceCount;

public:

   typedef  CtiMessage  Inherited;


   CtiSignalMsg(long       pid    = 0,
                int        soe    = 0,
                std::string  text = "(none)",
                std::string  addl= "(none)",
                int        lt     = GeneralLogType,
                unsigned   cls    = SignalEvent,
                std::string  usr = "(none)",
                unsigned   tag    = 0,
                int        pri    = 7,
                unsigned   millis = 0,
                double     ptvalue = 0.0);

   CtiSignalMsg(const CtiSignalMsg& aRef);

   virtual ~CtiSignalMsg();

   CtiSignalMsg& operator=(const CtiSignalMsg& aRef);
   long  getId() const;
   CtiSignalMsg& setId( const long a_id );

   const std::string& getText() const;
   CtiSignalMsg& setText(const std::string& string);

   const std::string& getAdditionalInfo() const;
   CtiSignalMsg& setAdditionalInfo(const std::string& string);

   unsigned getSignalCategory() const;
   CtiSignalMsg& setSignalCategory(const unsigned cls);

   int getCondition() const;
   CtiSignalMsg& setCondition(const int cls);

   unsigned getTags() const;
   CtiSignalMsg& setTags(const unsigned s);
   CtiSignalMsg& resetTags(const unsigned s = 0xffffffff);

   int getLogType() const;
   CtiSignalMsg& setLogType(const int soe);

   unsigned getSignalMillis() const;
   CtiSignalMsg& setSignalMillis(unsigned millis);

   double getPointValue() const;
   CtiSignalMsg& setPointValue(double pval);

   CtiMessage* replicateMessage() const override;

   std::string toString() const override;

   BOOL isAlarm() const;
   BOOL isEvent() const;

   unsigned getLogID() const;
   CtiSignalMsg& setLogID(const unsigned lid);

   static unsigned int getInstanceCount() { return _instanceCount; }
};
