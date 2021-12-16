#include "precompiled.h"

#include "con_mgr_vg.h"
#include "logger.h"



UINT  CtiVanGoghConnectionManager::getAllPoints() const
{
   return _allPoints;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setAllPoints( const UINT a_allPoints )
{
   _allPoints = a_allPoints;
   return *this;
}

UINT CtiVanGoghConnectionManager::getEvent() const
{
   return _event;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setEvent( const UINT a_event )
{
   _event = a_event;
   return *this;
}

UINT  CtiVanGoghConnectionManager::getAlarm() const
{
   return _alarm;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setAlarm( const UINT a_alarm )
{
   _alarm = a_alarm;
   return *this;
}

bool CtiVanGoghConnectionManager::isRegForAll() const
{
    return _allPoints;
}

void CtiVanGoghConnectionManager::reportRegistration() const
{
   Cti::FormattedList itemList;

   itemList.add("All Points") << (bool)getAllPoints();
   itemList.add("Event")  << (bool)getEvent();
   itemList.add("Alarm")  << (bool)getAlarm();

   CTILOG_INFO(dout, itemList);
}

CtiVanGoghConnectionManager::CtiVanGoghConnectionManager( const std::string& replyToName, const std::string& serverQueueName, Que_t *MainQueue_ ) :
   _blank(0),
   CtiConnectionManager( replyToName, serverQueueName, MainQueue_ )
{
}

CtiVanGoghConnectionManager::~CtiVanGoghConnectionManager()
{ }

unsigned CtiVanGoghConnectionManager::hash(const CtiVanGoghConnectionManager& aRef)
{
   return (unsigned)&aRef;            // The address of the Object?
}
