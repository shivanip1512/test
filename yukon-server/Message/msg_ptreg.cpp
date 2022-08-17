#include "precompiled.h"

#include "collectable.h"
#include "logger.h"
#include "msg_ptreg.h"
#include "dlldefs.h"

#include "dllbase.h"

DEFINE_COLLECTABLE( CtiPointRegistrationMsg, MSG_POINTREGISTRATION );

// Return a new'ed copy of this message!
CtiMessage* CtiPointRegistrationMsg::replicateMessage() const
{
   return new CtiPointRegistrationMsg(*this);
}

std::string CtiPointRegistrationMsg::toString() const
{
   Cti::FormattedList itemList;

   itemList <<"CtiPointRegistrationMsg";
   itemList.add("Registration Flags") << RegFlags;

   std::vector<LONG>::const_iterator itr = PointList.begin();
   for( ; itr != PointList.end(); itr++)
   {
       itemList.add("Registering for Point") << *itr;
   }

   return (Inherited::toString() += itemList.toString());
}

CtiPointRegistrationMsg::CtiPointRegistrationMsg()
    :   CtiPointRegistrationMsg(REG_NOTHING)
{}

CtiPointRegistrationMsg::CtiPointRegistrationMsg(int Flag) 
    :   RegFlags{Flag},
        CtiMessage(14)
{}

// If list is empty, I assume you wanted them all!.
size_t CtiPointRegistrationMsg::getCount() const       { return PointList.size(); }

void CtiPointRegistrationMsg::insert(const long a)
{
   PointList.push_back(a);
}

int CtiPointRegistrationMsg::getFlags() const
{
    return RegFlags;
}

void CtiPointRegistrationMsg::setFlags(int flags)
{
    RegFlags = flags;
}

const std::vector<LONG>& CtiPointRegistrationMsg::getPointList() const
{
    return PointList;
}

void CtiPointRegistrationMsg::setPointList( const std::vector<LONG>& points )
{
    PointList.assign( points.begin(), points.end() );
}

bool CtiPointRegistrationMsg::isRequestingEvents()    const  {  return RegFlags & REG_EVENTS;         }
bool CtiPointRegistrationMsg::isRequestingAlarms()    const  {  return RegFlags & REG_ALARMS;         }
bool CtiPointRegistrationMsg::isDecliningUpload()     const  {  return RegFlags & REG_NO_UPLOAD;      }
bool CtiPointRegistrationMsg::isRequestingAllPoints() const  {  return RegFlags & REG_ALL_POINTS;     }
bool CtiPointRegistrationMsg::isAddingPoints()        const  {  return RegFlags & REG_ADD_POINTS;     }
bool CtiPointRegistrationMsg::isRemovingPoints()      const  {  return RegFlags & REG_REMOVE_POINTS;  }
bool CtiPointRegistrationMsg::isRequestingUploadTag() const  {  return RegFlags & REG_TAG_UPLOAD;     }
