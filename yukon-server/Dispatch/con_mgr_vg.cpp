#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\mutex.h>

#include "con_mgr_vg.h"
#include "dlldefs.h"
#include "logger.h"
#include "pointtypes.h"


UINT  CtiVanGoghConnectionManager::getLoadProfile() const
{
   return _loadProfile;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setLoadProfile( const UINT a_lp )
{
   _loadProfile = a_lp;
   return *this;
}

UINT  CtiVanGoghConnectionManager::getStatus() const
{
   return _status;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setStatus( const UINT a_status )
{
   _status = a_status;
   return *this;
}

UINT  CtiVanGoghConnectionManager::getAnalog() const
{
   return _analog;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setAnalog( const UINT a_analog )
{
   _analog = a_analog;
   return *this;
}

UINT  CtiVanGoghConnectionManager::getAccumulator() const
{
   return _accum;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setAccumulator( const UINT a_accum )
{
   _accum = a_accum;
   return *this;
}

UINT  CtiVanGoghConnectionManager::getCalculated() const
{
   return _calc;
}
CtiVanGoghConnectionManager& CtiVanGoghConnectionManager::setCalculated( const UINT a_calc )
{
   _calc = a_calc;
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
    return (getStatus() && getAnalog() && getAccumulator() && getCalculated());
}

bool CtiVanGoghConnectionManager::isRegForAnyType() const
{
    return (getStatus() || getAnalog() || getAccumulator() || getCalculated());
}

BOOL  CtiVanGoghConnectionManager::isRegForChangeType(int type) const
{
   BOOL result = FALSE;

   switch( type )
   {
   case StatusPointType:
      {
         if(getStatus())
         {
            result = TRUE;
         }
         break;
      }
   case AnalogPointType:
      {
         if(getAnalog())
         {
            result = TRUE;
         }
         break;
      }
   case PulseAccumulatorPointType:
   case DemandAccumulatorPointType:
      {
         if(getAccumulator())
         {
            result = TRUE;
         }
         break;
      }
   case CalculatedPointType:
   case CalculatedStatusPointType:
      {
         if(getCalculated())
         {
            result = TRUE;
         }
         break;
      }
   }

   return result;
}

void CtiVanGoghConnectionManager::reportRegistration() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << "Status      " << (getStatus() ? "YES" : "NO ") << endl;
   dout << "Analog      " << (getAnalog() ? "YES" : "NO ") << endl;
   dout << "Accums      " << (getAccumulator() ? "YES" : "NO ") << endl;
   dout << "Calc        " << (getCalculated() ? "YES" : "NO ") << endl;
   dout << "Event       " << (getEvent() ? "YES" : "NO ") << endl;
   dout << "Alarm       " << (getAlarm() ? "YES" : "NO ") << endl;
}

CtiVanGoghConnectionManager::CtiVanGoghConnectionManager( CtiListenerConnection &listenerConn, Que_t *MainQueue_) :
   _blank(0),
   CtiConnectionManager(listenerConn, MainQueue_)
{
   // cout << "**** Connection Manager!!! *****" << endl;
}

CtiVanGoghConnectionManager::~CtiVanGoghConnectionManager()
{ }

//static unsigned CtiVanGoghConnectionManager::hash(const CtiVanGoghConnectionManager& aRef)
unsigned CtiVanGoghConnectionManager::hash(const CtiVanGoghConnectionManager& aRef)
{
   return (unsigned)&aRef;            // The address of the Object?
}
