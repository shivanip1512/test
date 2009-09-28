/*---------------------------------------------------------------------------
        Filename:  test_ccapbank.cpp

        Programmer:  Jess Oteson

        Initial Date:  5/11/2007

        COPYRIGHT:  Copyright (C) Cannon Technologies 2007
---------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test CCCapBank"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>

#include "yukon.h"
#include "ctitime.h"
#include "ccsubstationbus.h"
#include "ccsubstation.h"
#include "ccarea.h"
#include "ccsubstationbusstore.h"

using boost::unit_test_framework::test_suite;
using namespace std;


BOOST_AUTO_TEST_CASE(test_cannot_control_bank_text)
{
    CtiCCSubstationBus bus;
    CtiCCSubstation station;
    CtiCCArea area;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance(true);
    area.setPAOId(3);
    station.setPAOId(2);
    bus.setPAOId(1);
    bus.setPAOName("Test SubBus");
    bus.setParentId(2);
    bus.setEventSequence(22);  
    bus.setCurrentVarLoadPointValue(55, CtiTime());
    station.setParentId(1);
    station.setSaEnabledFlag(FALSE);
    store->addAreaToPaoMap(&area);
    area.getSubStationList()->push_back(station.getPAOId());
    store->addSubstationToPaoMap(&station);
    station.getCCSubIds()->push_back(bus.getPAOId());
    store->addSubBusToPaoMap(&bus);

    bus.setCorrectionNeededNoBankAvailFlag(FALSE);
    CtiMultiMsg_vec ccEvents;
    bus.createCannotControlBankText("Increase Var", "Open", ccEvents);
    BOOST_CHECK_EQUAL(bus.getCorrectionNeededNoBankAvailFlag(), 1);
    BOOST_CHECK_EQUAL(ccEvents.size(), 1);
    bus.createCannotControlBankText("Increase Var", "Open", ccEvents);
    BOOST_CHECK_EQUAL(ccEvents.size(), 1);
    bus.setCorrectionNeededNoBankAvailFlag(FALSE);
    bus.createCannotControlBankText("Increase Var", "Open", ccEvents);
    BOOST_CHECK_EQUAL(ccEvents.size(), 2);
}
