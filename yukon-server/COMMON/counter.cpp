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
 * REVISION     :  $Revision: 1.4 $
 * DATE         :  $Date: 2002/05/21 23:10:42 $
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "counter.h"

void CtiCounter::inc( int index )
{
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    if( _counterMap.find(index) != _counterMap.end() )
        _counterMap[index] += 1;
    else
        _counterMap[index] = 1;
}



void CtiCounter::dec( int index )
{
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    if( _counterMap.find(index) != _counterMap.end() )
        _counterMap[index] -= 1;
    else
        _counterMap[index] = -1;
}



int CtiCounter::get( int index )
{
    int retVal;
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    if( _counterMap.find(index) != _counterMap.end() )
        retVal = _counterMap[index];
    else
        retVal = 0;

    return retVal;
}


void CtiCounter::reset( int index )
{
    int retVal;
    CtiLockGuard<CtiMutex> guard(_counterMapMux);

    _counterMap[index] = 0;
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

