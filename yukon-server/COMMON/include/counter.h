#pragma warning( disable : 4786 )
#ifndef __COUNTER_H__
#define __COUNTER_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   counter.h
 *
 * Class:  CtiCounter, CtiTXCounter
 * Date:   2001-oct-02
 *
 * Author: Matthew Fisher
 *
 * PVCS KEYWORDS:
 * ARCHIVE      :  $Archive:     $
 * REVISION     :  $Revision: 1.3 $
 * DATE         :  $Date: 2002/04/16 15:57:25 $
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <map>
using namespace std;
#include "guard.h"
#include "mutex.h"

class IM_EX_CTIBASE CtiCounter
{
public:

    CtiCounter( )    
        { };

    void inc( int index );
    void dec( int index );
    int  get( int index );
    void reset( int index );

private:
    
    map< int, int > _counterMap;
    CtiMutex _counterMapMux;
};



class IM_EX_CTIBASE CtiTXCounter : protected CtiCounter
{
public:

    CtiTXCounter( )
        { };

    enum txCountTypes
    {
        Try,
        Fail,
        Success
    };
    
    int getTries( void );
    int getSuccesses( void );
    int getFails( void );

    void incSuccess( void );
    void incFail( void );

    void resetTXCounts( void );
};

#endif  //  __COUNTERS_H__


