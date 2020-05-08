#pragma once

#include "pointdefs.h"
#include "message.h"
#include "yukon.h"


class IM_EX_MSG CtiLMControlHistoryMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMControlHistoryMsg )

public:

   long        _paoId;                  //
   long        _pointId;                // Controlled pointid, or zero if NA.
   int         _rawState;               // Targeted State.  Valid entries are INVALID, CONTROLLED, UNCONTROLLED.  Others are undefined.
   CtiTime      _startDateTime;          //
   int         _controlDuration;        // Length of control in seconds! Negative means LATCHED control.
   int         _reductionRatio;         // percentage of time the control is in effect. Defaults to 100% of controlDuration.
   std::string   _controlType;            //
   std::string   _activeRestore;          // Indicates whether group will time-in or a message is required.
   double      _reductionValue;         // kW reduction on this group.
   int         _controlPriority;        // 0=highest priority. higher = lower priority. In Expresscom, 3 = lowest priority
   int         _associationKey;

private:

    static unsigned int _instanceCount;

public:

   typedef CtiMessage Inherited;

   CtiLMControlHistoryMsg(long       paoid      = 0,
                          long       pointid    = 0,
                          int        raw        = STATE_INVALID,
                          CtiTime     start      = CtiTime(),
                          int        dur        = 0,
                          int        redrat     = 100,
                          int        ctrlPriority   = 0);

   CtiLMControlHistoryMsg(const CtiLMControlHistoryMsg& aRef);
   virtual ~CtiLMControlHistoryMsg();

   CtiLMControlHistoryMsg& operator=(const CtiLMControlHistoryMsg& aRef);

   long getPAOId() const;
   CtiLMControlHistoryMsg& setPAOId( const long a_id );

   long getPointId() const;
   CtiLMControlHistoryMsg& setPointId( const long a_id );

   int getControlPriority() const;
   CtiLMControlHistoryMsg& setControlPriority( const int priority );

   int getRawState() const;
   CtiLMControlHistoryMsg& setRawState(const int raw);

   const CtiTime& getStartDateTime() const;
   CtiLMControlHistoryMsg& setStartDateTime(const CtiTime& time);

   int getControlDuration() const;
   CtiLMControlHistoryMsg& setControlDuration(const int duration);

   int getReductionRatio() const;
   CtiLMControlHistoryMsg& setReductionRatio(const int redrat);

   const std::string& getControlType() const;
   CtiLMControlHistoryMsg& setControlType(const std::string& string);

   const std::string& getActiveRestore() const;
   CtiLMControlHistoryMsg& setActiveRestore(const std::string& string);

   double getReductionValue() const;
   CtiLMControlHistoryMsg& setReductionValue( const double value );

   int getAssociationKey() const;
   CtiLMControlHistoryMsg& setAssociationKey(const int key);

   virtual CtiMessage* replicateMessage() const;

   std::size_t getFixedSize() const override    { return sizeof( *this ); }
   std::size_t getVariableSize() const override;

   std::string toString() const override;

   virtual bool isValid();

   static unsigned int getInstanceCount() { return _instanceCount; }
};
