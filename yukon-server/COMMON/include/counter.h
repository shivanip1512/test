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
 * REVISION     :  $Revision: 1.6 $
 * DATE         :  $Date: 2003/03/13 19:35:26 $
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __COUNTER_H__
#define __COUNTER_H__
#pragma warning( disable : 4786 )


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
    int  get( int index ) const;
    void set( int index, int val );
    void reset( int index );
    void resetAll();

    CtiCounter& operator=(const CtiCounter& aRef);

private:

    map< int, int > _counterMap;
    mutable CtiMutex _counterMapMux;
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


