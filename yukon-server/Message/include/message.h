#pragma once

#include "ctitime.h"
#include "ctidbgmem.h" // defines CTIDBG_new
#include "collectable.h"
#include "loggable.h"
#include "dlldefs.h"
#include "connectionHandle.h"

#include <string>
#include <vector>

class CtiMessage;    // Forward reference...

typedef std::vector<CtiMessage*> CtiMultiMsg_vec;

/******************************************************************************
 *  This is a base class which comprises an executable (Dispatch-able) message
 *  type.
 *
 *  DERIVED classes MUST DECLARE AND DEFINE THEMSELVES COLLECTIBLE!
 *
 ******************************************************************************/
class IM_EX_MSG CtiMessage : public Cti::Loggable
{
public:
    DECLARE_COLLECTABLE( CtiMessage )

protected:

    CtiTime      MessageTime;         // set to current during construction.
    INT          MessagePriority;
    int          _soe;             // An ID to group events.. Default to zero if not used
    std::string  _usr;
    std::string  _src;

    Cti::ConnectionHandle _connectionHandle;

   static std::string formatMessageName(const std::string &name);

public:

   CtiMessage(int Pri = 7);

   CtiMessage(const CtiMessage& aRef);

   virtual ~CtiMessage() = default;

   CtiMessage& operator=(const CtiMessage& aRef);
   bool operator==(const CtiMessage &aRef) const;
   bool virtual operator<(const CtiMessage& aRef) const;
   bool virtual operator>(const CtiMessage& aRef) const;
   virtual void setConnectionHandle(const Cti::ConnectionHandle handle);
   Cti::ConnectionHandle getConnectionHandle() const;
   void setMessagePriority(INT n);
   INT  getMessagePriority() const;
   INT  getSOE() const;
   CtiMessage& setSOE( const INT & soe );


   CtiTime getMessageTime() const;
   CtiMessage&   setMessageTime(const CtiTime &mTime);
   const std::string& getUser() const;
   CtiMessage&   setUser(const std::string& usr);

   int getToken() const;
   CtiMessage& setToken(const int& tok);

   const std::string& getSource() const;
   CtiMessage&   setSource(const std::string& src);

   virtual CtiMessage* replicateMessage() const;

   // Adjust the time to now.
   void resetTime();

   virtual std::string toString() const override;
   std::string typeString() const;

   virtual bool isValid();
};


// This will be used to sort these things in a CtiQueue.
namespace std
{
  template <> struct greater<CtiMessage*>
  {
    bool operator()(CtiMessage const* p1, CtiMessage const* p2)
    {
      if(!p1)
        return true;
      if(!p2)
        return false;
      return *p1 > *p2;
    }
  };
};




