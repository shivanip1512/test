#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __QUEENT_H__
#define __QUEENT_H__

#include <windows.h>
// #include <iostream.h>

#include <rw\defs.h>
#include <rw\rwtime.h>
#include <rw/thr/threadid.h>

#include "dlldefs.h"

class CtiConnectionManager;
class CtiServer;

class IM_EX_CTIBASE CtiQueueEnt
{
private:
   int                     priority_;
   int                     tag_;
   RWTime                  entrytime_;

   RWThreadId              mycreator_;

   RWCString               DataString;
   CtiConnectionManager   *ReplyTo;            // Maintained only across queue calls...

public:
   CtiQueueEnt(int Priority = 0, int Tag = 0) :   // Defaults to a FIFO type queue
      priority_(Priority),
      tag_(Tag),
      mycreator_(rwThreadId())
   {}

   ~CtiQueueEnt()
   {
   }

   virtual void PreInsert()
   {
      entrytime_ = RWTime();
   }

   virtual int ExecuteQueueEntry(CtiServer *Svr) = 0;     // Pure Virtual;

   void setTag(int n) { tag_ = n; }
   void setPriority(int n) { priority_ = n; }

   RWBoolean virtual
      operator==(const CtiQueueEnt& aRef) const
   {
      RWBoolean bRet(FALSE);

      if(tag_ || aRef.getTag())
      {
         if(tag_ == aRef.getTag() && priority_ == aRef.getPriority())
         {
            bRet = RWBoolean(TRUE);
         }
      }
      else
      {
         if(priority_ == aRef.getPriority())
         {
            bRet = RWBoolean(TRUE);
         }
      }

      return bRet;
   }

   // Returns true if self compares lexicographically less than aRef, otherwise returns false.
   /*
    * objects are inserted such that objects "less than" themselves are before the
    * object, objects "greater than" themselves after the object.
    * An insertion sort is used. Duplicates are allowed.
    */
   RWBoolean virtual
      operator<(const CtiQueueEnt& aRef) const
   {
      RWBoolean bRet(TRUE);

      if(priority_ < aRef.getPriority())
      {
         bRet = RWBoolean(FALSE);
      }
      else if(priority_ == aRef.getPriority())
      {
         if(tag_ > aRef.getTag())
         {
            bRet = RWBoolean(FALSE);
         }
      }
      return bRet;
   }

   void                    setManager( CtiConnectionManager *  RT )  { ReplyTo = RT;}
   CtiConnectionManager*   getManager( )                             { return ReplyTo;}

   const RWCString &       getString()                   { return DataString; }
   void                    setString(RWCString &str)     { DataString = str;  }

   int      getTag() const { return tag_; }
   int      getPriority() const { return priority_; }
   RWTime   getTime() const { return entrytime_; }

   RWThreadId  getOrigin() const { return mycreator_; }

   void     Dump() const
   {
      cout <<
         "Created by " << mycreator_ <<
         " Tag " << tag_ <<
         " Priority " << priority_ << endl;
   }

   // const int MaxPriorty = 15;
};

RWBoolean TagMatch(const CtiQueueEnt *p, void *pTag);


#endif // #ifndef  __QUEENT_H__
