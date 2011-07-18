#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


#include "queent.h"



CtiQueueEnt(int Priority = 0, int Tag = 0) :   // Defaults to a FIFO type queue
  priority_(Priority),
  tag_(Tag),
  mycreator_(rwThreadId())
{}
virtual void PreInsert()
{
  entrytime_ = CtiTime();
}
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
void setManager( CtiConnectionManager *  RT )
{
   ReplyTo = RT;
}

CtiConnectionManager*   getManager( )
{
   return ReplyTo;
}

const string & getString()
{
   return DataString;
}
void setString(const string &str)
{
   DataString = str;
}

int      getTag() const
{
   return tag_;
}
int      getPriority() const
{
   return priority_;
}
CtiTime   getTime() const
{
   return entrytime_;
}

RWThreadId  getOrigin() const
{
   return mycreator_;
}

void     Dump() const
{
  std::cout <<
     "Created by " << mycreator_ <<
     " Tag " << tag_ <<
     " Priority " << priority_ << endl;
}


// How about that crap.
