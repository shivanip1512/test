#pragma once

#include "ctitime.h"
#include "ctidbgmem.h" // defines CTIDBG_new
#include "collectable.h"
#include "loggable.h"
#include "dlldefs.h"

#include <string>

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
    int          _token;
    std::string  _src;

   /*
    *  Allows a message to mark its return path.. This is a bread crumb.
    *   This pointer is never worried about by this class, it is just a carrier
    *   location as the message goes through the machinery on the server....
    */
   void  *ConnectionHandle;

   static std::string formatMessageName(const std::string &name);

public:

   CtiMessage(int Pri = 7);

   CtiMessage(const CtiMessage& aRef);

   virtual ~CtiMessage();

   CtiMessage& CtiMessage::operator=(const CtiMessage& aRef);
   bool operator==(const CtiMessage &aRef) const;
   bool virtual operator<(const CtiMessage& aRef) const;
   bool virtual operator>(const CtiMessage& aRef) const;
   virtual CtiMessage& setConnectionHandle(void *p);
   virtual void* getConnectionHandle() const;
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




