/*-----------------------------------------------------------------------------*
 *
 * File:   counter.cpp
 *
 * Class:  CtiCounter, CtiTXCounter
 * Date:   2001-oct-02
 *
 * Author: Matthew Fisher
 *
 * PVCS KEYWORDS:
 * ARCHIVE      :  $Archive:     $
 * REVISION     :  $Revision: 1.7 $
 * DATE         :  $Date: 2004/12/14 22:21:44 $
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "counter.h"

void CtiCounter::inc( int index, int bump )
{
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    if( _counterMap.find(index) != _counterMap.end() )
        _counterMap[index] += bump;
    else
        _counterMap[index] = bump;
}



void CtiCounter::dec( int index, int bump )
{
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    if( _counterMap.find(index) != _counterMap.end() )
        _counterMap[index] -= bump;
    else
        _counterMap[index] = -bump;
}



int CtiCounter::get( int index ) const
{
    int retVal;
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    if( _counterMap.find(index) != _counterMap.end() )
        retVal = (*_counterMap.find(index)).second;       // Use this goofy access to keep the const-ness.
    else
        retVal = 0;

    return retVal;
}

void CtiCounter::set( int index, int val )
{
    CtiLockGuard<CtiMutex> guard(_counterMapMux);
    _counterMap[index] = val;
}

void CtiCounter::reset( int index )
{
    int retVal;
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    _counterMap[index] = 0;
}

void CtiCounter::resetAll()
{
    int retVal;
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    if(!_counterMap.empty()) _counterMap.clear();
}

CtiCounter& CtiCounter::operator=(const CtiCounter& aRef)
{
    if(this != &aRef)
    {
        _counterMap = aRef._counterMap;
    }
    return *this;
}


int CtiTXCounter::getTries( void )       {  return get( Try );      };
int CtiTXCounter::getSuccesses( void )   {  return get( Success );  };
int CtiTXCounter::getFails( void )       {  return get( Fail );     };

void CtiTXCounter::incSuccess( void )    {  inc( Success );
                                            inc( Try );      };

void CtiTXCounter::incFail( void )       {  inc( Fail );
                                            inc( Try );      };

void CtiTXCounter::resetTXCounts( void ) {  reset( Try );
                                            reset( Fail );
                                            reset( Success );  };


