#pragma once

#include <map>
#include "smartmap.h"


#include "cparms.h"
#include "queue.h"
#include "thread.h"
#include "thread_register_data.h"

class IM_EX_CTIBASE CtiThreadMonitor : public CtiThread
{
public:

   typedef CtiSmartMap<CtiThreadRegData> ThreadData;
   typedef std::vector < int > PointIDList;

   CtiThreadMonitor();
   virtual ~CtiThreadMonitor();

//   void tickle( const CtiThreadRegData *in );
   void tickle( CtiThreadRegData *in );
//   void dump( void );

   enum State//These are considered internal states, who cares what the point value is, I dont deal with that!!!
   {
       Normal = 0,
       NonCriticalFailure,
       CriticalFailure,
       Dead
   };

   std::string getString();

   enum PointOffsets
   {
       FirstPoint = 1000,//This must always match the first offset
       Porter = 1000,    //and offsets must be sequential!
       Dispatch = 1001,
       Scanner = 1002,
       Calc = 1003,
       CapControl = 1004,
       FDR = 1005,
       Macs = 1006,
       LastPoint
   };

   enum TickleTiming
   {
       StandardTickleTime =  30,    //Time between responses
       StandardMonitorTime = 300,   //Time until dead
       ExtendedMonitorTime = 330
   };

   State getState(void);
   PointIDList getPointIDList();
   void recalculatePointIDList(void);
   int getPointIDFromOffset(int offset);

protected:

   virtual void run( void );

private:

   long checkForExpriration( void );
   void processQueue( void );
   void processExpired( void );
   void processExtraCommands( void );

   State _currentState;//status point!

   mutable CtiMutex                                              _monitorMux;
   mutable CtiMutex                                              _vectorMux;//for the pointID list
   CtiQueue < CtiThreadRegData, std::less< CtiThreadRegData > >  _queue;
   ThreadData                                                    _threadData;
   PointIDList                                                   _pointIDList;
   std::string                                                   _output;

};


//  included here instead of dllbase.h because ptimes are fat as of 2004-oct-22;
//    include thread_monitor.h if you want to use the thread monitor in your code
IM_EX_CTIBASE extern CtiThreadMonitor  ThreadMonitor;
