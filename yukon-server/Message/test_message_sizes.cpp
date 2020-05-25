#include <boost/test/unit_test.hpp>

#include "utility.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_pdata.h"

#ifdef   _DEBUG
    #define CONSUMPTION_TEST    BOOST_CHECK_GE
#else
    #define CONSUMPTION_TEST    BOOST_CHECK_EQUAL
#endif


BOOST_AUTO_TEST_SUITE( test_message_sizes )

BOOST_AUTO_TEST_CASE( test_base_message_memory_footprint )
{
    CtiMessage  m;

    // An uninitialized CtiMessage is 80 bytes in release mode, bigger in debug - given from sizeof

    BOOST_CHECK_EQUAL(   0, m.getVariableSize() );
    CONSUMPTION_TEST( calculateMemoryConsumption( &m ), 80 );

    // The internal strings are subject to the small string optimization.  Anything less than 16
    //  bytes is stored in the object directly. Strings of 16 bytes or greater are allocated from
    //  the free store.

    m.setUser( "0123456789" );
    m.setSource( "0123456789" );

    BOOST_CHECK_EQUAL(   0, m.getVariableSize() );
    CONSUMPTION_TEST( calculateMemoryConsumption( &m ), 80 );

    m.setUser( "01234567890123456789" );    // at least 20 bytes from the free store

    BOOST_CHECK_GE( m.getVariableSize(), 20 );
    BOOST_CHECK_GE( calculateMemoryConsumption( &m ), 100 );

    m.setSource( "0123456789012345678901234567890123456789" );  // at least 40 more bytes from the free store

    BOOST_CHECK_GE( m.getVariableSize(), 60 );
    BOOST_CHECK_GE( calculateMemoryConsumption( &m ), 140 );
}

BOOST_AUTO_TEST_CASE( test_point_data_message_memory_footprint )
{
    CtiPointDataMsg m;

    // An uninitialized CtiPointDataMsg is 144 bytes in release mode, bigger in debug - given from sizeof

    BOOST_CHECK_EQUAL(   0, m.getVariableSize() );
    CONSUMPTION_TEST( calculateMemoryConsumption( &m ), 144 );

    // In addition to the base CtiMessage, a CtiPointDataMsg has an embedded string as well,
    //  which is also subject to the small string optimization.

    m.setUser( "0123456789" );
    m.setSource( "0123456789" );
    m.setString( "0123456789" );

    BOOST_CHECK_EQUAL(   0, m.getVariableSize() );
    CONSUMPTION_TEST( calculateMemoryConsumption( &m ), 144 );

    // at least 20 bytes from the free store
    m.setUser( "01234567890123456789" );

    BOOST_CHECK_GE( m.getVariableSize(), 20 );
    BOOST_CHECK_GE( calculateMemoryConsumption( &m ), 164 );

    // at least 40 more bytes from the free store
    m.setSource( "0123456789012345678901234567890123456789" );

    BOOST_CHECK_GE( m.getVariableSize(), 60 );
    BOOST_CHECK_GE( calculateMemoryConsumption( &m ), 204 );

    // at least 80 more bytes from the free store
    m.setString( "01234567890123456789012345678901234567890123456789012345678901234567890123456789" );

    BOOST_CHECK_GE( m.getVariableSize(), 140 );
    BOOST_CHECK_GE( calculateMemoryConsumption( &m ), 284 );
}

BOOST_AUTO_TEST_CASE( test_multi_message_memory_footprint )
{
    CtiMultiMsg m;

    // An uninitialized CtiPointDataMsg is 96 bytes in release mode, bigger in debug - given from sizeof

    BOOST_CHECK_EQUAL(   0, m.getVariableSize() );
    CONSUMPTION_TEST( calculateMemoryConsumption( &m ), 96 );

    {
        CtiMultiMsg_vec input;

        // Stuff it with point data messages - each one is at least 144 bytes...
        for ( int i = 0; i < 10; i++ )
        {
            input.push_back( new CtiPointDataMsg() );
        }

        m.setData( input );

        delete_container( input );
    }

    // 10 * 144 + map overhead, is at least 4 bytes per entry for the pointer == 1480

    BOOST_CHECK_GE( m.getVariableSize(), 1480 );
    BOOST_CHECK_GE( calculateMemoryConsumption( &m ), 1576 );
}

BOOST_AUTO_TEST_SUITE_END()
