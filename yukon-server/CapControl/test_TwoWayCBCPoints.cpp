#include <boost/test/unit_test.hpp>

#include "boost_test_helpers.h"

#include "cctwowaycbcpoints.h"
#include "ctidate.h"
#include "std_helper.h"


struct PointInitializer
{
    CtiPointType_t  type;
    int             offset;
    long            pointID;
};


class test_LastControlReasonCbc802x : public LastControlReason
{
public:

    std::string getText( const long reason, const long stateGroup ) override
    {
        // state of the world as of 2015.04.29...

        static const std::map<long, std::string>    _lookup 
        {
            {  0, "Manual"                  },
            {  1, "SCADA Override"          },
            {  2, "Fault Current"           },
            {  3, "Emergency Voltage"       },
            {  4, "Time ONOFF"              },
            {  5, "OVUV Control"            },
            {  6, "VAR"                     },
            {  7, "Va"                      },
            {  8, "Vb"                      },
            {  9, "Vc"                      },
            { 10, "Ia"                      },
            { 11, "Ib"                      },
            { 12, "Ic"                      },
            { 13, "Temp"                    },
            { 14, "N/A"                     },
            { 15, "Time"                    },
            { 16, "N/A"                     },
            { 17, "Bad Active Relay"        },
            { 18, "NC Lockout"              },
            { 19, "Control Accepted"        },
            { 20, "Auto Mode"               },
            { 21, "Reclose Block"           }
        };

        if ( auto result = Cti::mapFind( _lookup, reason ) )
        {
            return *result;
        }

        return "Uninitialized";
    }
};



BOOST_AUTO_TEST_SUITE( test_TwoWayCBCPoints )

BOOST_AUTO_TEST_CASE( test_TwoWayCBCPoints_CBC_DNP )
{
    std::unique_ptr<CtiCCTwoWayPoints>     points( CtiCCTwoWayPointsFactory::Create( 575, "CBC DNP" ) );

    PointInitializer    databaseInput[] =
    {
        { StatusPointType,                  1,      761 }
    };

    for ( const PointInitializer & item : databaseInput )
    {
        points->setTwoWayPointId( item.type, item.offset, item.pointID, 0 );
    }

    std::set<long>
        registrationPoints,
        expected
        {
            761
        };

    points->addAllCBCPointsToRegMsg( registrationPoints );

    BOOST_CHECK_EQUAL_RANGES( registrationPoints, expected );

    BOOST_CHECK_EQUAL( "Uninitialized", points->getLastControlText() );
}

BOOST_AUTO_TEST_CASE( test_TwoWayCBCPoints_CBC_702X )
{
    std::unique_ptr<CtiCCTwoWayPoints>     points( CtiCCTwoWayPointsFactory::Create( 545, "CBC 7022" ) );

    PointInitializer    databaseInput[] =
    {
        // These type/offsets are defined in the 2-way point code for CBC 702X devices
        { AnalogPointType,                  5,      739 },
        { AnalogPointType,                  6,      691 },
        { AnalogPointType,                  7,      732 },
        { AnalogPointType,                  8,      706 },
        { AnalogPointType,                  9,      712 },
        { AnalogPointType,                 10,      723 },
        { AnalogPointType,                 13,      671 },
        { AnalogPointType,                 14,      698 },
        { AnalogPointType,              10001,      708 },
        { AnalogPointType,              10002,      733 },
        { AnalogPointType,              10003,      720 },
        { AnalogPointType,              10004,      721 },
        { AnalogPointType,              10010,      697 },
        { AnalogPointType,              10011,      729 },
        { AnalogPointType,              10026,      684 },
        { AnalogPointType,              10042,      672 },
        { AnalogPointType,              10068,      722 },
        { AnalogPointType,              20001,      719 },
        { AnalogPointType,              20002,      688 },
        { PulseAccumulatorPointType,        1,      711 },
        { PulseAccumulatorPointType,        2,      727 },
        { PulseAccumulatorPointType,        3,      687 },
        { StatusPointType,                  1,      675 },
        { StatusPointType,                  2,      682 },
        { StatusPointType,                  3,      694 },
        { StatusPointType,                  4,      707 },
        { StatusPointType,                  5,      679 },
        { StatusPointType,                  6,      700 },
        { StatusPointType,                  7,      690 },
        { StatusPointType,                  8,      693 },
        { StatusPointType,                  9,      676 },
        { StatusPointType,                 10,      734 },
        { StatusPointType,                 11,      674 },
        { StatusPointType,                 12,      701 },
        { StatusPointType,                 13,      702 },
        { StatusPointType,                 14,      714 },
        { StatusPointType,                 15,      716 },
        { StatusPointType,                 16,      704 },
        { StatusPointType,                 24,      695 },
        { StatusPointType,                 25,      689 },
        { StatusPointType,                 26,      705 },
        { StatusPointType,                 27,      731 },
        { StatusPointType,                 28,      683 },
        { StatusPointType,                 29,      717 },
        { StatusPointType,                 34,      726 },
        // The following are not...
        { AnalogPointType,                  3,      715 },
        { AnalogPointType,                 15,      718 },
        { AnalogPointType,                 16,      738 },
        { AnalogPointType,                 17,      724 },
        { AnalogPointType,                 18,      686 },
        { AnalogPointType,                 19,      696 },
        { AnalogPointType,                 20,      728 },
        { AnalogPointType,                 21,      703 },
        { AnalogPointType,                 22,      677 },
        { AnalogPointType,              10006,      710 },
        { AnalogPointType,              10007,      709 },
        { AnalogPointType,              10008,      737 },
        { AnalogPointType,              10009,      685 },
        { AnalogPointType,              10015,      680 },
        { AnalogPointType,              10016,      736 },
        { AnalogPointType,              10017,      678 },
        { AnalogPointType,              10018,      673 },
        { AnalogPointType,              10057,      713 },
        { AnalogPointType,              10110,      735 },
        { AnalogPointType,              10111,      730 },
        { AnalogPointType,              10113,      725 },
        { AnalogPointType,              10114,      681 },
        { StatusPointType,                 17,      692 },
        { StatusPointType,                 30,      699 },
        { StatusPointType,               2001,      740 }
    };

    for ( const PointInitializer & item : databaseInput )
    {
        points->setTwoWayPointId( item.type, item.offset, item.pointID, 0 );
    }

    std::set<long>
        registrationPoints,
        expected
        {
            739, 691, 732, 706, 712, 723, 671, 698, 708,
            733, 720, 721, 697, 729, 684, 672, 722, 719,
            688, 711, 727, 687, 675, 682, 694, 707, 679,
            700, 690, 693, 676, 734, 674, 701, 702, 714,
            716, 704, 695, 689, 705, 731, 683, 717, 726
        };

    points->addAllCBCPointsToRegMsg( registrationPoints );

    BOOST_CHECK_EQUAL_RANGES( registrationPoints, expected );

/// -----------

    CtiTime     now( CtiDate( 22, 8, 2014 ), 9, 0, 0 );

/// ----------- general interface testing

    // Try to update a status point value with a non-status point attribute
    now += 1;
    BOOST_CHECK( ! points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::TotalOpCount ),      10, now ) );

    // Do it right!
    BOOST_CHECK( points->setTwoWayPulseAccumulatorPointValue(
                    points->getPointIdByAttribute( PointAttribute::TotalOpCount ),      10, now ) );

    // See what comes out...
    BOOST_CHECK_EQUAL(  711, points->getPointIdByAttribute( PointAttribute::TotalOpCount ) );
    BOOST_CHECK_EQUAL( 10.0, points->getPointValueByAttribute( PointAttribute::TotalOpCount ) );

    LitePoint lp = points->getPointByAttribute( PointAttribute::TotalOpCount );

    BOOST_CHECK_EQUAL(  PulseAccumulatorPointType, lp.getPointType() );

    BOOST_CHECK_EQUAL(  711, lp.getPointId() );
    BOOST_CHECK_EQUAL(    1, lp.getPointOffset() );

/// ----------- last control reason testing

    // Initialize all LastControl... attributes to 0
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlLocal ),          0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlRemote ),         0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlOvUv ),           0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlNeutralFault ),   0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlScheduled ),      0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlDigital ),        0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlAnalog ),         0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlTemperature ),    0, now ) );

    BOOST_CHECK_EQUAL( "Uninitialized", points->getLastControlText() );

    // Try to update LastControlLocal with same timestamp
    BOOST_CHECK( ! points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlLocal ),          1, now ) );

    BOOST_CHECK_EQUAL( "Uninitialized", points->getLastControlText() );

    // Test different LastControl... attributes
    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlLocal ),          1, now ) );

    BOOST_CHECK_EQUAL( "Local", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlLocal ),          0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlRemote ),         1, now ) );

    BOOST_CHECK_EQUAL( "Remote", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlRemote ),         0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlOvUv ),           1, now ) );

    BOOST_CHECK_EQUAL( "OvUv", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlOvUv ),           0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlNeutralFault ),   1, now ) );

    BOOST_CHECK_EQUAL( "NeutralFault", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlNeutralFault ),   0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlScheduled ),      1, now ) );

    BOOST_CHECK_EQUAL( "Schedule", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlScheduled ),      0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlDigital ),        1, now ) );

    BOOST_CHECK_EQUAL( "Digital", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlDigital ),        0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlAnalog ),         1, now ) );

    BOOST_CHECK_EQUAL( "Analog", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlAnalog ),         0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlTemperature ),    1, now ) );

    BOOST_CHECK_EQUAL( "Temp", points->getLastControlText() );
}

BOOST_AUTO_TEST_CASE( test_TwoWayCBCPoints_CBC_802X )
{
    std::unique_ptr<CtiCCTwoWayPoints>     points( CtiCCTwoWayPointsFactory::Create( 545, "CBC 8020" ) );

    points->setLastControlReasonDecoder( std::make_unique<test_LastControlReasonCbc802x>() );

    PointInitializer    databaseInput[] =
    {
        // These type/offsets are defined in the 2-way point code for CBC 802X devices
        { AnalogPointType,                  2,      335 },
        { AnalogPointType,                 12,      340 },
        { AnalogPointType,              10001,      331 },
        { AnalogPointType,              10002,      359 },
        { AnalogPointType,              10318,      313 },
        { PulseAccumulatorPointType,        1,      323 },
        { PulseAccumulatorPointType,        2,      338 },
        { PulseAccumulatorPointType,        3,      362 },
        { StatusPointType,                  1,      318 },
        { StatusPointType,                  3,      334 },
        { StatusPointType,                 72,      311 },
        { StatusPointType,                 84,      354 },
        { StatusPointType,                 86,      321 },
        { StatusPointType,                 89,      336 },
        { PulseAccumulatorPointType,        4,      330 },
        { PulseAccumulatorPointType,        5,      350 },
        // The following are not...
        { AnalogPointType,                  1,      342 },
        { AnalogPointType,                  5,      355 },
        { AnalogPointType,                  6,      357 },
        { AnalogPointType,                  7,      320 },
        { AnalogPointType,                  8,      351 },
        { AnalogPointType,                 21,      361 },
        { AnalogPointType,                114,      352 },
        { AnalogPointType,               9999,      325 },
        { AnalogPointType,              10005,      324 },
        { AnalogPointType,              10006,      329 },
        { AnalogPointType,              10007,      339 },
        { AnalogPointType,              10008,      312 },
        { AnalogPointType,              10009,      314 },
        { AnalogPointType,              10010,      326 },
        { AnalogPointType,              10011,      316 },
        { AnalogPointType,              10012,      337 },
        { AnalogPointType,              10013,      341 },
        { AnalogPointType,              10015,      328 },
        { AnalogPointType,              10016,      332 },
        { AnalogPointType,              10018,      343 },
        { AnalogPointType,              10019,      315 },
        { AnalogPointType,              10020,      333 },
        { StatusPointType,                  2,      345 },
        { StatusPointType,                  4,      347 },
        { StatusPointType,                  5,      344 },
        { StatusPointType,                  6,      349 },
        { StatusPointType,                 14,      353 },
        { StatusPointType,                 15,      319 },
        { StatusPointType,                 68,      322 },
        { StatusPointType,                 69,      360 },
        { StatusPointType,                 70,      348 },
        { StatusPointType,                 71,      356 },
        { StatusPointType,                 83,      317 },
        { StatusPointType,                 87,      327 },
        { StatusPointType,                 88,      358 },
        { StatusPointType,               2000,      552 },
        { StatusPointType,               2001,      346 }
    };

    for ( const PointInitializer & item : databaseInput )
    {
        points->setTwoWayPointId( item.type, item.offset, item.pointID, -17 );
    }

    std::set<long>
        registrationPoints,
        expected
        {
            340, 331, 359, 313, 323, 338, 362, 330, 350,
            318, 334, 311, 354, 321, 336, 335
        };

    points->addAllCBCPointsToRegMsg( registrationPoints );

    BOOST_CHECK_EQUAL_RANGES( registrationPoints, expected );

/// -----------

    CtiTime     now( CtiDate( 22, 8, 2014 ), 9, 0, 0 );

/// ----------- last control reason testing

    BOOST_CHECK_EQUAL( "Uninitialized", points->getLastControlText() );

    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     0, now ) );

    BOOST_CHECK_EQUAL( "Manual", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     1, now ) );

    BOOST_CHECK_EQUAL( "SCADA Override", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     2, now ) );

    BOOST_CHECK_EQUAL( "Fault Current", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     3, now ) );

    BOOST_CHECK_EQUAL( "Emergency Voltage", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     4, now ) );

    BOOST_CHECK_EQUAL( "Time ONOFF", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     5, now ) );

    BOOST_CHECK_EQUAL( "OVUV Control", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     6, now ) );

    BOOST_CHECK_EQUAL( "VAR", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     7, now ) );

    BOOST_CHECK_EQUAL( "Va", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     8, now ) );

    BOOST_CHECK_EQUAL( "Vb", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),     9, now ) );

    BOOST_CHECK_EQUAL( "Vc", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    10, now ) );

    BOOST_CHECK_EQUAL( "Ia", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    11, now ) );

    BOOST_CHECK_EQUAL( "Ib", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    12, now ) );

    BOOST_CHECK_EQUAL( "Ic", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    13, now ) );

    BOOST_CHECK_EQUAL( "Temp", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    14, now ) );

    BOOST_CHECK_EQUAL( "N/A", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    15, now ) );

    BOOST_CHECK_EQUAL( "Time", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    16, now ) );

    BOOST_CHECK_EQUAL( "N/A", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    17, now ) );

    BOOST_CHECK_EQUAL( "Bad Active Relay", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    18, now ) );

    BOOST_CHECK_EQUAL( "NC Lockout", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    19, now ) );

    BOOST_CHECK_EQUAL( "Control Accepted", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    20, now ) );

    BOOST_CHECK_EQUAL( "Auto Mode", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    21, now ) );

    BOOST_CHECK_EQUAL( "Reclose Block", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( PointAttribute::LastControlReason ),    22, now ) );

    BOOST_CHECK_EQUAL( "Uninitialized", points->getLastControlText() );
}

BOOST_AUTO_TEST_SUITE_END()

