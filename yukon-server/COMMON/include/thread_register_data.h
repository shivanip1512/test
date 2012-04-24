#pragma once

#include <string>


#include "boost_time.h"
#include "cticalls.h"
#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include <functional>

class IM_EX_CTIBASE CtiThreadRegData
{
public:

   enum Behaviors    //absence detect behavior type
   {
      None,
      Action,        //Function call
      LogOut
   };

   typedef void (*BehaviorFunction)( const std::string & p );

   CtiThreadRegData( const int id,
                     const std::string & name,
                     const Behaviors type,
                     const int tickle_freq_sec,
                     const BehaviorFunction ptr,
                     const std::string & args );

   CtiThreadRegData( const int id,
                     const std::string & name,
                     const Behaviors type );

   virtual ~CtiThreadRegData();

   bool operator<( const CtiThreadRegData& y ) const;   //just for the queue, me thinks

   std::string getName( void );
   void setName( const std::string in );

   int getId( void );
   void setId( const int &in );

   int getUnreportedCount(void);
   void setUnreportedCount(int count);

   Behaviors getBehavior( void );
   void setBehavior( Behaviors in );

   ULONG getTickleFreq( void );
   void setTickleFreq( ULONG seconds_between_tickles );

   boost::posix_time::ptime getTickledTime( void );
   void setTickledTime( boost::posix_time::ptime in );

   BehaviorFunction getActionFunc( void );
   void setActionFunc( BehaviorFunction in );

   std::string getActionArgs();
   void setActionArgs( const std::string & args );

   bool getReported( void );
   void setReported( const bool in );

   bool getCritical( void );
   void setCritical( const bool in );

   void setActionTaken(const bool in);
   bool getActionTaken(void);

   void resetUnreportedFilter();
   bool testUnreportedFilter();

protected:

private:

   bool                     _reported;
   bool                     _critical;//is it critical or not (default true)
   bool                     _actionTaken;//clear until action is taken (makes sure we dont take action twice!)
   boost::posix_time::ptime _tickledTime;
   int                      _unreportedCount;
   int                      _unreportedFilter;

   //
   //registration: must haves
   //
   std::string          _name;
   int                  _id;
   Behaviors            _behaviorType;
   ULONG                _tickleFreq;

   //
   //registration: optionals
   //
   BehaviorFunction     _action;
   std::string          _action_args;
};

// This will be used to sort these things in a CtiQueue.
namespace std
{
  template <> struct greater<CtiThreadRegData*>
  {
    bool operator()(CtiThreadRegData const* p1, CtiThreadRegData const* p2)
    {
      //Defined like less for ascending sorting
      if(!p1)
        return true;
      if(!p2)
        return false;
      return *p2 < *p1;
    }
  };
};

typedef boost::shared_ptr< CtiThreadRegData > CtiThreadRegDataSPtr;
