#pragma once

#include "DispatchConnection.h"
#include "guard.h"
#include "mutex.h"



class DispatchPointRegistry
{
public:

    template<typename Container>
    void addPoints( const Container & collection )
    {
        CTILOCKGUARD( CtiMutex, guard, _mutex );

        for ( auto pointID : collection )
        {
            ++_pending[ pointID ];
        }
    }

    template<typename Container>
    void removePoints( const Container & collection )
    {
        CTILOCKGUARD( CtiMutex, guard, _mutex );

        for ( auto pointID : collection )
        {
            --_pending[ pointID ];
        }
    }

    void updateRegistry( DispatchConnectionPtr dispatchConnection );

private:

    using EnrollmentCounts = std::map<long, long>;  // { point ID --> Count }

    EnrollmentCounts    _pending;
    EnrollmentCounts    _registered;

    CtiMutex            _mutex;
};

