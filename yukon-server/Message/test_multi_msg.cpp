#include <boost/test/unit_test.hpp>

#include "msg_multi.h"
#include "msg_pdata.h"

#include "utility.h"

BOOST_AUTO_TEST_SUITE( test_multi_msg )

BOOST_AUTO_TEST_CASE(test_msg_set_data)
{
    CtiMultiMsg_vec pointChanges;
    pointChanges.push_back(new CtiPointDataMsg(123,0,NormalQuality,StatusPointType, "testDeviceName"));
    pointChanges.push_back(new CtiPointDataMsg(124,0,NormalQuality,StatusPointType, "testDeviceName"));
    pointChanges.push_back(new CtiPointDataMsg(125,0,NormalQuality,StatusPointType, "testDeviceName"));

    CtiMultiMsg* pointChangeMsg = new CtiMultiMsg();
    pointChangeMsg->setData(pointChanges);

    delete_container(pointChanges);

    BOOST_CHECK_EQUAL(((CtiPointDataMsg*)pointChangeMsg->getData()[0])->getId(), 123);
    BOOST_CHECK_EQUAL(((CtiPointDataMsg*)pointChangeMsg->getData()[1])->getId(), 124);
    BOOST_CHECK_EQUAL(((CtiPointDataMsg*)pointChangeMsg->getData()[2])->getId(), 125);

    delete pointChangeMsg;
}

BOOST_AUTO_TEST_SUITE_END()
