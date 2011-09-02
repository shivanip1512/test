#pragma once

#include <map>
#include "guard.h"
#include "mutex.h"

class IM_EX_CTIBASE CtiCounter
{
public:

    CtiCounter( )
        { };

    void inc( int index, int bump = 1 );        // Increment by bump.
    void dec( int index, int bump = 1 );        // Decrement by bump.
    int  get( int index ) const;
    void set( int index, int val );
    void reset( int index );
    void resetAll();

    CtiCounter& operator=(const CtiCounter& aRef);

private:

    std::map< int, int > _counterMap;
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


