#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __QUEENT_H__
#define __QUEENT_H__

#include <windows.h>


#include <rw\defs.h>
#include <rw/thr/threadid.h>

#include "dlldefs.h"

using std::cout;

class CtiConnectionManager;
class CtiServer;

class IM_EX_CTIBASE CtiQueueEnt
{
private:
   int                     priority_;
   int                     tag_;   
   CtiTime                  entrytime_;

   RWThreadId              mycreator_;

   string               DataString;
   CtiConnectionManager   *ReplyTo;            // Maintained only across queue calls...

public:
   CtiQueueEnt(int Priority = 0, int Tag = 0);   // Defaults to a FIFO type queue

   ~CtiQueueEnt()
   {
   }

   virtual void PreInsert();

   virtual int ExecuteQueueEntry(CtiServer *Svr) = 0;     // Pure Virtual;

   void setTag(int n);
   void setPriority(int n);
   RWBoolean virtual
      operator==(const CtiQueueEnt& aRef) const;

   // Returns true if self compares lexicographically less than aRef, otherwise returns false.
   /*
    * objects are inserted such that objects "less than" themselves are before the
    * object, objects "greater than" themselves after the object.
    * An insertion sort is used. Duplicates are allowed.
    */
   RWBoolean virtual
      operator<(const CtiQueueEnt& aRef) const;

   void                    setManager( CtiConnectionManager *  RT );
   CtiConnectionManager*   getManager( );

   const string &       getString();
   void                 setString(const string &str);

   int      getTag() const;
   int      getPriority() const;   
   CtiTime   getTime() const;
   RWThreadId  getOrigin() const;
   void     Dump() const;

   // const int MaxPriorty = 15;
};

RWBoolean TagMatch(const CtiQueueEnt *p, void *pTag);


#endif // #ifndef  __QUEENT_H__
