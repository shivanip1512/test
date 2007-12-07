/*-----------------------------------------------------------------------------*
*
* File:   test_ccusim.cpp
*
* Date:   11/30/2007
*
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test CCUSim"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

#include <string>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <list>

#include "yukon.h"
#include "ctitime.h"
#include "ctidate.h"
#include "ctistring.h"
#include "CCU711.h"

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST( test_something )
{

    int portNumber = 2000;
    cout<<"Port: "<<portNumber<<endl;
    int strategy=0;

    CCU711 aCCU711(2000);

    int Strategy = 0;
    aCCU711.setStrategy(Strategy);

    unsigned char ReadBuffer[100];
    unsigned char test = ReadBuffer[0];
    BOOST_CHECK(ReadBuffer[0]==test);
    int ccuNumber = 0;
    aCCU711.ReceiveMsg(ReadBuffer, ccuNumber);

    //aCCU711.getNeededAddresses(mctAddressArray);  // ask the CCU711 which mct addresses it needs values from the db for

    int counter = 0;
    //aCCU711.ReceiveMore(ReadBuffer, counter, structArray);

    //aCCU711.CreateMsg(ccuNumber);

    //aCCU711.SendMsg(SendData);


/////////////  TESTS   /////////////
   BOOST_CHECK(ReadBuffer[0]==test);
   // BOOST_CHECK_EQUAL(1,1);


}

