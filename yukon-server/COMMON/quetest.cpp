#include "yukon.h"
#include <windows.h>
#include <iostream>
#include <functional>

using namespace std;

#include <rw\cstring.h>


#include "queue.h"

#include <rw/thr/condtion.h> // for RWCondition
#include <rw/thr/monitor.h>  // for RWMonitor<T>
#include <rw/thr/mutex.h>    // for RWMutexLock
#include <rw/tvslist.h>      // for RWTValSlist<T>

// Producer Consumer queue example
template <class T>
class PCBuffer : RWMonitor<RWMutexLock>
{
private:
   RWCondition roomInBuffer_;
   RWCondition elementAvailable_;
   RWTValSlist<T> buffer_;
   size_t maxEntries_;
public:
   PCBuffer(size_t maxEntries)
      : maxEntries_(maxEntries),
        roomInBuffer_(mutex()),    // init with monitor mutex
        elementAvailable_(mutex()) // init with monitor mutex
   {}

   void put(T t) {
      LockGuard lock(monitor()); // acquire monitor mutex
      while (!(buffer_.entries() < maxEntries_)) {
         roomInBuffer_.wait();   // mutex released automatically
         // thread must have been signaled AND
         // mutex reacquired to reach here
      }
      buffer_.append(t);
      elementAvailable_.signal();
      // mutex automatically released in LockGuard destructor
   }

   T get(void) {
      LockGuard lock(monitor());   // acquire monitor mutex
      while (!(buffer_.entries() > 0)) {
         elementAvailable_.wait(); // mutex released
                                   //automatically
         // thread must have been signalled AND
         // mutex reacquired to reach here
      }
      T val = buffer_.removeFirst();
      roomInBuffer_.signal();
      return val;
      // mutex automatically released in LockGuard destructor
   }

};

void main(int argc, char **argv)
{
   CtiQueue<CtiExecutorQueueEnt, less<CtiExecutorQueueEnt> > Q;

#if 0

   RWCString Temp;

   PCBuffer<RWCString> Buf(10);

   cout << "Trying to find instance 1" << endl;

   Buf.put(RWCString("One"));

   Buf.put(RWCString("Three"));
   Buf.put(RWCString("Two"));

   Temp = Buf.get();
   cout << Temp << endl;
   Temp = Buf.get();
   cout << Temp << endl;
   Temp = Buf.get();
//   cout << Temp << endl;
//   Temp = Buf.get();
   // cout << Temp << endl;

   #else

   cout << "Trying to find instance 1" << endl;
   CtiExecutorQueueEnt *qPtr;

   Q.putQueue(new CtiExecutorQueueEnt(1, 2));
   qPtr = Q.getQueue(5000);
   qPtr = Q.getQueue(5000);

   Q.putQueue(new CtiExecutorQueueEnt(2, 2));
   Q.putQueue(new CtiExecutorQueueEnt(3, 2));
   Q.putQueue(new CtiExecutorQueueEnt(2, 2));
   Q.putQueue(new CtiExecutorQueueEnt(1, 2));
   cout << "Trying to find instance 1" << endl;


   while(Q.entries())
   {
      qPtr = Q.getQueue(5);

      cout << qPtr->getPriority() << endl;

      delete qPtr;
   }
   #endif


}
