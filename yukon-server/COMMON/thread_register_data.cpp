
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/10/07 16:58:31 $
*
* Copyright (c) 1999, 2000, 2001, 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*---------------------------------------------------------------------------------------------*/

#include "thread_register_data.h"

//===========================================================================================================
// the first 4 args are required 
//===========================================================================================================

CtiThreadRegData::CtiThreadRegData( int id,
                                     string name,
                                     Behaviours type,
                                     int tickle_freq,
                                     behaviourFuncPtr ptr1,
                                     void *args1,
                                     behaviourFuncPtr ptr2,
                                     void *args2 ) :
   _tickledTime( second_clock::local_time() )
{
   _id = id;
   _name = name;
   _behaviourType = type;
   _tickleFreq = tickle_freq;
   _action_one = ptr1;
   _action_one_args = args1;
   _action_two = ptr2;
   _action_two_args = args2;

   if(( !_id ) || ( _name == "default" ))
      setReported(  false );
}

//===========================================================================================================
//===========================================================================================================

CtiThreadRegData::~CtiThreadRegData()
{
}

//===========================================================================================================
//===========================================================================================================

bool CtiThreadRegData::operator<( const CtiThreadRegData& y ) const
{
   return( true );
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

CtiThreadRegData::behaviourFuncPtr CtiThreadRegData::getShutdownFunc( void )
{
   return( _action_one );
}

//===========================================================================================================
//===========================================================================================================

void* CtiThreadRegData::getShutdownArgs( void )
{
   return( _action_one_args );
}

//===========================================================================================================
//===========================================================================================================

CtiThreadRegData::behaviourFuncPtr CtiThreadRegData::getAlternateFunc( void )
{
   return( _action_two );
}

//===========================================================================================================
//===========================================================================================================

void* CtiThreadRegData::getAlternateArgs( void )
{
   return( _action_two_args );
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

void CtiThreadRegData::setAlternateArgs( void* args )
{
   _action_one_args = args;
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setTickleFreq( ULONG seconds )
{
   _tickleFreq = seconds;
}

//===========================================================================================================
// this could be a restart function or whatever else seems like a good idea
//===========================================================================================================

void CtiThreadRegData::setAlternateFunc( behaviourFuncPtr in )
{
   _action_two = in;
}
   
//===========================================================================================================
//===========================================================================================================

void CtiThreadRegData::setShutdownArgs( void* args )
{
   _action_one_args = args;
}

//===========================================================================================================
// this should be a function that causes the death of whatever thread has gone awry
//===========================================================================================================

void CtiThreadRegData::setShutdownFunc( behaviourFuncPtr in )
{
   _action_one = in; 
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
