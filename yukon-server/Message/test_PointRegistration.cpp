#include <boost/test/unit_test.hpp>

#include "msg_ptreg.h"

BOOST_AUTO_TEST_SUITE( test_pointRegistration )

BOOST_AUTO_TEST_CASE(test_no_flags)
{
    CtiPointRegistrationMsg msg;

    BOOST_CHECK( ! (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( ! (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( ! msg.isAddingPoints() );
    BOOST_CHECK( ! msg.isDecliningUpload() );
    BOOST_CHECK( ! msg.isRemovingPoints() );
    BOOST_CHECK( ! msg.isRequestingAlarms() );
    BOOST_CHECK( ! msg.isRequestingAllPoints() );
    BOOST_CHECK( ! msg.isRequestingEvents() );
    BOOST_CHECK( ! msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_all_flags)
{
    CtiPointRegistrationMsg msg(REG_ADD_POINTS 
                                | REG_ALL_POINTS
                                | REG_REMOVE_POINTS
                                | REG_ALARMS 
                                | REG_EVENTS 
                                | REG_TAG_UPLOAD
                                | REG_NO_UPLOAD );

    BOOST_CHECK( (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( msg.isAddingPoints() );
    BOOST_CHECK( msg.isDecliningUpload() );
    BOOST_CHECK( msg.isRemovingPoints() );
    BOOST_CHECK( msg.isRequestingAlarms() );
    BOOST_CHECK( msg.isRequestingAllPoints() );
    BOOST_CHECK( msg.isRequestingEvents() );
    BOOST_CHECK( msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_adding_points)
{
    CtiPointRegistrationMsg msg(REG_ADD_POINTS);

    BOOST_CHECK(   (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( ! (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK(   msg.isAddingPoints() );
    BOOST_CHECK( ! msg.isDecliningUpload() );
    BOOST_CHECK( ! msg.isRemovingPoints() );
    BOOST_CHECK( ! msg.isRequestingAlarms() );
    BOOST_CHECK( ! msg.isRequestingAllPoints() );
    BOOST_CHECK( ! msg.isRequestingEvents() );
    BOOST_CHECK( ! msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_declining_upload)
{
    CtiPointRegistrationMsg msg(REG_NO_UPLOAD);

    BOOST_CHECK( ! (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK(   (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( ! msg.isAddingPoints() );
    BOOST_CHECK(   msg.isDecliningUpload() );
    BOOST_CHECK( ! msg.isRemovingPoints() );
    BOOST_CHECK( ! msg.isRequestingAlarms() );
    BOOST_CHECK( ! msg.isRequestingAllPoints() );
    BOOST_CHECK( ! msg.isRequestingEvents() );
    BOOST_CHECK( ! msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_removing_points)
{
    CtiPointRegistrationMsg msg(REG_REMOVE_POINTS);

    BOOST_CHECK( ! (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK(   (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( ! (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( ! msg.isAddingPoints() );
    BOOST_CHECK( ! msg.isDecliningUpload() );
    BOOST_CHECK(   msg.isRemovingPoints() );
    BOOST_CHECK( ! msg.isRequestingAlarms() );
    BOOST_CHECK( ! msg.isRequestingAllPoints() );
    BOOST_CHECK( ! msg.isRequestingEvents() );
    BOOST_CHECK( ! msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_requesting_alarms)
{
    CtiPointRegistrationMsg msg(REG_ALARMS);

    BOOST_CHECK( ! (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK(   (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( ! (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( ! msg.isAddingPoints() );
    BOOST_CHECK( ! msg.isDecliningUpload() );
    BOOST_CHECK( ! msg.isRemovingPoints() );
    BOOST_CHECK(   msg.isRequestingAlarms() );
    BOOST_CHECK( ! msg.isRequestingAllPoints() );
    BOOST_CHECK( ! msg.isRequestingEvents() );
    BOOST_CHECK( ! msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_requesting_all_points)
{
    CtiPointRegistrationMsg msg(REG_ALL_POINTS);

    BOOST_CHECK( ! (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK(   (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( ! (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( ! msg.isAddingPoints() );
    BOOST_CHECK( ! msg.isDecliningUpload() );
    BOOST_CHECK( ! msg.isRemovingPoints() );
    BOOST_CHECK( ! msg.isRequestingAlarms() );
    BOOST_CHECK(   msg.isRequestingAllPoints() );
    BOOST_CHECK( ! msg.isRequestingEvents() );
    BOOST_CHECK( ! msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_requesting_events)
{
    CtiPointRegistrationMsg msg(REG_EVENTS);

    BOOST_CHECK( ! (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK(   (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( ! (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( ! msg.isAddingPoints() );
    BOOST_CHECK( ! msg.isDecliningUpload() );
    BOOST_CHECK( ! msg.isRemovingPoints() );
    BOOST_CHECK( ! msg.isRequestingAlarms() );
    BOOST_CHECK( ! msg.isRequestingAllPoints() );
    BOOST_CHECK(   msg.isRequestingEvents() );
    BOOST_CHECK( ! msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_CASE(test_requesting_moa_tag)
{
    CtiPointRegistrationMsg msg(REG_TAG_UPLOAD);

    BOOST_CHECK( ! (msg.getFlags() & REG_ADD_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALL_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_REMOVE_POINTS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_ALARMS) );
    BOOST_CHECK( ! (msg.getFlags() & REG_EVENTS) );
    BOOST_CHECK(   (msg.getFlags() & REG_TAG_UPLOAD) );
    BOOST_CHECK( ! (msg.getFlags() & REG_NO_UPLOAD) );

    BOOST_CHECK( ! msg.isAddingPoints() );
    BOOST_CHECK( ! msg.isDecliningUpload() );
    BOOST_CHECK( ! msg.isRemovingPoints() );
    BOOST_CHECK( ! msg.isRequestingAlarms() );
    BOOST_CHECK( ! msg.isRequestingAllPoints() );
    BOOST_CHECK( ! msg.isRequestingEvents() );
    BOOST_CHECK(   msg.isRequestingUploadTag() );
}

BOOST_AUTO_TEST_SUITE_END()
