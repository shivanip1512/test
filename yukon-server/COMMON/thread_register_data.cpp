/*--------------------------------------------------------------------------------------------*
*
* File:   thread_register_data
*
* Date:   9/2/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2006/02/15 18:42:38 $
*
* Copyright (c) 1999, 2000, 2001, 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*---------------------------------------------------------------------------------------------*/
#include "yukon.h"

#include "thread_register_data.h"

//===========================================================================================================
// the first 4 args are required
//===========================================================================================================

CtiThreadRegData::CtiThreadRegData( int id,
                                     string name,
                                     Behaviours type,
                                     int tickle_freq_sec,
                                     behaviourFuncPtr ptr,
                                     void *args ) :
    _tickledTime( second_clock::local_time() ),
    _critical(true),
    _actionTaken(false),
   _unreportedCount(0),
   _unreportedFilter(0),
    _reported(true)
{
   _id = id;
   _name = name;
   _behaviourType = type;
   _tickleFreq = tickle_freq_sec;
   _action = ptr;
   _action_args = args;

   if(( !_id ) || ( _name == "default" ))
      setReported(  false );
}

//===========================================================================================================
//===========================================================================================================

CtiThreadRegData::~CtiThreadRegData()
{
    if( _action_args )
    {
        delete _action_args;
        _action_args = NULL;
    }
}

//===========================================================================================================
//===========================================================================================================

bool CtiThreadRegData::operator<( const CtiThreadRegData& y ) const
{
   return( _id < y._id );
}

//===========================================================================================================
//===========================================================================================================

string CtiThreadRegData::getName( void )
{
   return( _name );
}

//===========================================================================================================
//===========================================================================================================

int CtiThreadRegData::getId( void )
{
   return( _id );
}

//===========================================================================================================
//===========================================================================================================

CtiThreadRegData::Behaviours CtiThreadRegData::getBehaviour( void )
{
   return( _behaviourType );
}

//===========================================================================================================
//===========================================================================================================

ptime CtiThreadRegData::getTickledTime( void )
{
   return( _tickledTime );
}

//===========================================================================================================
//===========================================================================================================

CtiThreadRegData::behaviourFuncPtr CtiThreadRegData::getActionFunc( void )
{
   return( _action );
}

//===========================================================================================================
// this should be a function that causes the death of whatever thread has gone awry
//===========================================================================================================

void CtiThreadRegData::setActionFunc( behaviourFuncPtr in )
{
   _action = in;
}

//===========================================================================================================
//===========================================================================================================

void* CtiThreadRegData::getActionArgs( void )
{
   return( _action_args );
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setActionArgs( void* args )
{
    if( _action_args )
    {
        delete _action_args;
    }
   _action_args = args;
}

//===========================================================================================================
// this returns seconds
//===========================================================================================================

ULONG CtiThreadRegData::getTickleFreq( void )
{
   return( _tickleFreq );
}

//===========================================================================================================
//===========================================================================================================

bool CtiThreadRegData::getReported( void )
{
   return( _reported );
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setCritical( const bool in )
{
   _critical = in;
}

//===========================================================================================================
//===========================================================================================================

bool CtiThreadRegData::getCritical( void )
{
   return( _critical );
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setActionTaken (const bool in )
{
    _actionTaken = in;
}

//===========================================================================================================
//===========================================================================================================

bool CtiThreadRegData::getActionTaken (void )
{
    return ( _actionTaken );
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setReported( const bool in )
{
   _reported = in;
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setTickledTime( ptime in )
{
   _tickledTime = in;
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setTickleFreq( ULONG seconds_between_tickles )
{
   _tickleFreq = seconds_between_tickles;
}

//===========================================================================================================
// this setting tells the monitor what type of action the registered thread would like us to take if there
// is a 'out-to-lunch' episode
//===========================================================================================================

void CtiThreadRegData::setBehaviour( CtiThreadRegData::Behaviours in )
{
   _behaviourType = in;
}


//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setName( const string in )
{
   _name = in;
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setId( const int &in )
{
   _id = in;
}


//===========================================================================================================
//===========================================================================================================

int CtiThreadRegData::getUnreportedCount(void)
{
   return _unreportedCount;
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setUnreportedCount(int count)
{
   _unreportedCount = count;
}


/* 
   The thread monitor is hiccuping when running on a virtual machine.  If the VM takes a long enough nap
   and then recovers, all of the threads will go unreported and then immediately re-report.  We want to
   filter out this case.
*/
void CtiThreadRegData::resetUnreportedFilter()
{
   _unreportedFilter = 0;
}


bool CtiThreadRegData::testUnreportedFilter()
{
   if ( _unreportedFilter < 3 )
   {
      _unreportedFilter++;
      return false;
   }

   return true;
}

