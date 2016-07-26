#include <boost/test/unit_test.hpp>

#include "dev_grp.h"
#include "dev_grp_expresscom.h"
#include "devicetypes.h"
#include "mgr_point.h"
#include "expresscom.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_dev_grp )

BOOST_AUTO_TEST_CASE(test_dev_group_dynamic_text)
{
    CtiDeviceGroupExpresscom group; // CtiDeviceGroupBase can't be instantiated

    std::string input1 = "control xcom cycle 50 count 8 period 30 truecycle";
    std::string input2 = "control xcom cycle 50 count 7 period 30 truecycle";
    std::string input3 = "control xcom cycle 50 count 1234 period 30 truecycle";
    std::string output = "control xcom cycle 50 period 30 truecycle";

    BOOST_CHECK_EQUAL( group.removeCommandDynamicText(input1), output );
    BOOST_CHECK_EQUAL( group.removeCommandDynamicText(input2), output );
    BOOST_CHECK_EQUAL( group.removeCommandDynamicText(input3), output );

}

BOOST_AUTO_TEST_CASE(test_dev_group_parent_child)
{
    CtiDeviceGroupBaseSPtr group(CTIDBG_new CtiDeviceGroupExpresscom()); // Expresscom is the only one who does this right now.
    CtiDeviceGroupBaseSPtr child1(CTIDBG_new CtiDeviceGroupExpresscom());
    CtiDeviceGroupBaseSPtr child2(CTIDBG_new CtiDeviceGroupExpresscom());
    child2->setID(2, test_tag);
    long child1ID = 1;
    child1->setID(child1ID, test_tag);

    BOOST_CHECK(!group->isAParent());
    group->addChild(child1);
    BOOST_CHECK(group->isAParent());
    group->addChild(child2);
    group->removeChild(child1ID);
    BOOST_CHECK(group->isAParent());
    group->clearChildren();
    BOOST_CHECK(!group->isAParent());
    group->addChild(child1);
    BOOST_CHECK(group->isAParent());
    group->removeChild(child1ID);
    BOOST_CHECK(!group->isAParent());
    group->addChild(child1);
    group->addChild(child1);
    group->addChild(child1);
    BOOST_CHECK(group->isAParent());
    group->removeChild(child1ID);
    BOOST_CHECK(!group->isAParent());
}

BOOST_AUTO_TEST_CASE(test_expresscom_contol_notification)
{
    CtiDeviceGroupExpresscom* expresscomGrpPtr;
    CtiDeviceGroupBaseSPtr parent = CtiDeviceGroupBaseSPtr(CTIDBG_new CtiDeviceGroupExpresscom());
    CtiDeviceGroupBaseSPtr child = CtiDeviceGroupBaseSPtr(CTIDBG_new CtiDeviceGroupExpresscom());
    parent->setType(TYPE_LMGROUP_EXPRESSCOM);
    child->setType(TYPE_LMGROUP_EXPRESSCOM);

    std::list<CtiMessage *> vgList;
    int count = 0;

    parent->reportControlStart(true, 1, 0, vgList, "test");
    count++;
    BOOST_CHECK_EQUAL(count, vgList.size());
    parent->reportControlStart(true, 1, 0, vgList, "test");
    count++;
    BOOST_CHECK_EQUAL(count, vgList.size());
    child->reportControlStart(true, 1, 0, vgList, "test");
    count++;
    BOOST_CHECK_EQUAL(count, vgList.size());

    //naughty cast
    expresscomGrpPtr = (CtiDeviceGroupExpresscom*) parent.get();
    expresscomGrpPtr->addChild(child);
    BOOST_CHECK_EQUAL(true,parent->isAParent());

    parent->reportControlStart(true, 1, 0, vgList, "test");
    count+=2;
    BOOST_CHECK_EQUAL(count, vgList.size());
    expresscomGrpPtr->removeChild(child->getID());
    parent->reportControlStart(true, 1, 0, vgList, "test");
    count+=1;
    BOOST_CHECK_EQUAL(count, vgList.size());

    delete_container(vgList);
    vgList.clear();
}

BOOST_AUTO_TEST_CASE(test_expresscom_address_comparison)
{
    CtiDeviceGroupExpresscom* parentGrpPtr;
    CtiDeviceGroupExpresscom* childGrpPtr;
    CtiDeviceGroupBaseSPtr parent = CtiDeviceGroupBaseSPtr(CTIDBG_new CtiDeviceGroupExpresscom());
    CtiDeviceGroupBaseSPtr child = CtiDeviceGroupBaseSPtr(CTIDBG_new CtiDeviceGroupExpresscom());
    parent->setType(TYPE_LMGROUP_EXPRESSCOM);
    child->setType(TYPE_LMGROUP_EXPRESSCOM);

    parentGrpPtr = (CtiDeviceGroupExpresscom *)parent.get();
    childGrpPtr = (CtiDeviceGroupExpresscom *)child.get();

    parentGrpPtr->getExpresscomGroup().setAddressUsage(0xFF);
    childGrpPtr->getExpresscomGroup().setAddressUsage(0xFF);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, child->compareAddressing(parent));

    //short test to ensure that RFN Expresscom is keeping separate from Expresscom.
    parent->setType(TYPE_LMGROUP_RFN_EXPRESSCOM);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, child->compareAddressing(parent));

    child->setType(TYPE_LMGROUP_RFN_EXPRESSCOM);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setFeeder(10);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, child->compareAddressing(parent));

    //Note here the feeders are still different.
    parentGrpPtr->getExpresscomGroup().setAddressUsage(0xFF & !CtiProtocolExpresscom::atFeeder);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::THIS_IS_PARENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::OPERAND_IS_PARENT, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setFeeder(10);
    parentGrpPtr->getExpresscomGroup().setId(9);
    parentGrpPtr->getExpresscomGroup().setRouteId(8);
    parentGrpPtr->getExpresscomGroup().setSerial(0);
    parentGrpPtr->getExpresscomGroup().setServiceProvider(6);
    parentGrpPtr->getExpresscomGroup().setGeo(5);
    parentGrpPtr->getExpresscomGroup().setSubstation(4);
    parentGrpPtr->getExpresscomGroup().setZip(3);
    parentGrpPtr->getExpresscomGroup().setUda(2);
    parentGrpPtr->getExpresscomGroup().setProgram(1);
    parentGrpPtr->getExpresscomGroup().setSplinter(13);

    childGrpPtr->getExpresscomGroup().setFeeder(10);
    childGrpPtr->getExpresscomGroup().setId(9);
    childGrpPtr->getExpresscomGroup().setRouteId(8);
    childGrpPtr->getExpresscomGroup().setSerial(0);
    childGrpPtr->getExpresscomGroup().setServiceProvider(6);
    childGrpPtr->getExpresscomGroup().setGeo(5);
    childGrpPtr->getExpresscomGroup().setSubstation(4);
    childGrpPtr->getExpresscomGroup().setZip(3);
    childGrpPtr->getExpresscomGroup().setUda(2);
    childGrpPtr->getExpresscomGroup().setProgram(1);
    childGrpPtr->getExpresscomGroup().setSplinter(13);


    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::THIS_IS_PARENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::OPERAND_IS_PARENT, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setAddressUsage(0xFF);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setAddressUsage(CtiProtocolExpresscom::atIndividual);
    childGrpPtr->getExpresscomGroup().setAddressUsage(CtiProtocolExpresscom::atIndividual);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setSerial(7);
    childGrpPtr->getExpresscomGroup().setSerial(8);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setSerial(8);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, child->compareAddressing(parent));

    //Very bad stuff below. In current code, SERIAL overrides everything! As of this writing,
    //the dbeditor assigns a spid when serial is chosen, and addressUsage says "spid" even
    //though it is really serial. This test is not "correct" but shows current behavior as a
    //warning to anyone who changes this behavior.

    parentGrpPtr->getExpresscomGroup().setAddressUsage(CtiProtocolExpresscom::atSpid);
    childGrpPtr->getExpresscomGroup().setAddressUsage(CtiProtocolExpresscom::atSpid);
    parentGrpPtr->getExpresscomGroup().setServiceProvider(7);
    childGrpPtr->getExpresscomGroup().setServiceProvider(8);
    // We have a spid, they are different, but we also have a serial so we ignore spid!
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setSerial(7);
    childGrpPtr->getExpresscomGroup().setSerial(8);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setSerial(0);
    childGrpPtr->getExpresscomGroup().setSerial(0);
    parentGrpPtr->getExpresscomGroup().setServiceProvider(7);
    childGrpPtr->getExpresscomGroup().setServiceProvider(8);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::NO_RELATIONSHIP, child->compareAddressing(parent));

    parentGrpPtr->getExpresscomGroup().setServiceProvider(8);
    childGrpPtr->getExpresscomGroup().setServiceProvider(8);
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, parent->compareAddressing(child));
    BOOST_CHECK_EQUAL(CtiDeviceGroupBase::ADDRESSING_EQUIVALENT, child->compareAddressing(parent));
}

BOOST_AUTO_TEST_SUITE_END()
