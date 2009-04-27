/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test ObjectPool"

#include <boost/test/unit_test.hpp>

#include <cstring>
#include <vector>

#include "ObjectPool.h"

using boost::unit_test_framework::test_suite;


// Some 'object' to test the pool with
class ObPool_TestObj
{
    char buffer[32];
    int  length;

public:

    ObPool_TestObj()
    {
        strcpy(buffer, "<uninitialised>");
        length = strlen(buffer);
    }

    int getLength() const
    {
        return length;
    }

    const char *getBuffer() const
    {
        return buffer;
    }
};

typedef Cti::ObjectPool<ObPool_TestObj, 4>  ThePool;
typedef boost::shared_ptr<ObPool_TestObj>   ObjPtr;
typedef std::vector<ObjPtr>                 Container;


BOOST_AUTO_TEST_CASE(test_objectpool_memory_alignment)
{
    // Fill a vector with objects and then check their memory alignment.

    Container container;

    for (int ii = 0; ii < 15; ii++)
    {
        ObjPtr ptr( ThePool::Create() );

        container.push_back( ptr );
    }

    // Since we supplied the parameter 4 to the template definition of the pool
    //  it will grow by 4, then 8, then 16 etc.  We expect the first 4 objects in
    //  the container to be sequential in memory, followed by the next 8, etc.

    // Test this by comparing addresses...

    // elements 0 - 3 are offset by sizeof(ObPool_TestObj) which in the
    //  pointer arithmetic below is 1.
    BOOST_CHECK_EQUAL( container[1].get() , container[0].get() + 1 );
    BOOST_CHECK_EQUAL( container[2].get() , container[1].get() + 1 );
    BOOST_CHECK_EQUAL( container[3].get() , container[2].get() + 1 );

    // Since elements 3 and 4 are in different allocations of the pool they will
    //  NOT be sequential.
    BOOST_CHECK_NE( container[4].get() , container[3].get() + 1 );

    // elements 4 - 11 are again sequential..
    BOOST_CHECK_EQUAL( container[5].get() , container[4].get() + 1 );
    BOOST_CHECK_EQUAL( container[6].get() , container[5].get() + 1 );
    BOOST_CHECK_EQUAL( container[7].get() , container[6].get() + 1 );
    BOOST_CHECK_EQUAL( container[8].get() , container[7].get() + 1 );
    BOOST_CHECK_EQUAL( container[9].get() , container[8].get() + 1 );
    BOOST_CHECK_EQUAL( container[10].get() , container[9].get() + 1 );
    BOOST_CHECK_EQUAL( container[11].get() , container[10].get() + 1 );

    // Since elements 11 and 12 are in different allocations of the pool they will
    //  NOT be sequential.
    BOOST_CHECK_NE( container[12].get() , container[11].get() + 1 );

    // elements 12 + are again sequential..
    BOOST_CHECK_EQUAL( container[13].get() , container[12].get() + 1 );
    BOOST_CHECK_EQUAL( container[14].get() , container[13].get() + 1 );
}

BOOST_AUTO_TEST_CASE(test_objectpool_default_construction)
{
    // The Pool will call the objects default constructor.

    ObjPtr ptr( ThePool::Create() );

    BOOST_CHECK_EQUAL( ptr.get()->getBuffer() , "<uninitialised>" );
    BOOST_CHECK_EQUAL( ptr.get()->getLength() , 15 );
}

BOOST_AUTO_TEST_CASE(test_objectpool_default_destruction)
{
    // The Pool stores a custom deallocator for the object when it goes out of scope.

    // I have NO idea how to test this...

    BOOST_CHECK( true );
}

