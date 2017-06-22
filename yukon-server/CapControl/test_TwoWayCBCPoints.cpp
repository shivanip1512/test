#include <boost/test/unit_test.hpp>

#include "boost_test_helpers.h"

#include "cctwowaycbcpoints.h"
#include "ctidate.h"
#include "std_helper.h"


class test_LastControlReasonCbc802x : public LastControlReasonCbc802x
{
public:

    std::string lookupStateName( const long reason, const long stateGroup ) const override
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

        return "Unknown State. Value = " + std::to_string( reason );
    }
};

class test_IgnoredControlReasonCbc802x : public IgnoredControlReasonCbc802x
{
public:

    std::string lookupStateName( const long reason, const long stateGroup ) const override
    {
        static const std::map<long, std::string>    _lookup 
        {
            {  0, "Godzilla"                },
            {  1, "Ghidorah"                },
            {  2, "Mechagodzilla"           },
            {  3, "Biollante"               },
            {  4, "Mothra"                  },
            {  5, "Destoroyah"              },
            {  6, "Anguirus"                },
            {  7, "Orga"                    },
            {  8, "Hedorah"                 },
            {  9, "Rodan"                   },
            { 10, "Megaguirus"              },
            { 11, "Battra"                  },
            { 12, "Megalon"                 },
            { 13, "Baragon"                 },
            { 14, "Ebirah"                  },
            { 19, "Control Accepted"        }       // sigh...
        };

        if ( auto result = Cti::mapFind( _lookup, reason ) )
        {
            return *result;
        }

        return "Unknown State. Value = " + std::to_string( reason );
    }
};



BOOST_AUTO_TEST_SUITE( test_TwoWayCBCPoints )

BOOST_AUTO_TEST_CASE( test_TwoWayCBCPoints_CBC_DNP )
{
    std::unique_ptr<CtiCCTwoWayPoints>     points( CtiCCTwoWayPointsFactory::Create( 575, "CBC DNP" ) );

    LitePoint   databaseInput[] =
    {
        LitePoint( 761, StatusPointType, "foobar", 0, 1, "", "", 1.0, 0 )
    };

    for ( const auto & item : databaseInput )
    {
        points->assignTwoWayPoint( item );
    }

    std::set<long>
        registrationPoints,
        expected
        {
            761
        };

    points->addAllCBCPointsToRegMsg( registrationPoints );

    BOOST_CHECK_EQUAL_RANGES( registrationPoints, expected );

    BOOST_CHECK_EQUAL( "", points->getLastControlText() );

    BOOST_CHECK_EQUAL( "", points->getIgnoredControlText() );

    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );
}

BOOST_AUTO_TEST_CASE( test_TwoWayCBCPoints_CBC_702X )
{
    std::unique_ptr<CtiCCTwoWayPoints>     points( CtiCCTwoWayPointsFactory::Create( 545, "CBC 7022" ) );

    LitePoint   databaseInput[] =
    {
        // These type/offsets are defined in the 2-way point code for CBC 702X devices
        LitePoint( 715, AnalogPointType,            "", 0,     3, "", "", 1.0, 0 ),
        LitePoint( 739, AnalogPointType,            "", 0,     5, "", "", 1.0, 0 ),
        LitePoint( 691, AnalogPointType,            "", 0,     6, "", "", 1.0, 0 ),
        LitePoint( 732, AnalogPointType,            "", 0,     7, "", "", 1.0, 0 ),
        LitePoint( 706, AnalogPointType,            "", 0,     8, "", "", 1.0, 0 ),
        LitePoint( 712, AnalogPointType,            "", 0,     9, "", "", 1.0, 0 ),
        LitePoint( 723, AnalogPointType,            "", 0,    10, "", "", 1.0, 0 ),
        LitePoint( 671, AnalogPointType,            "", 0,    13, "", "", 1.0, 0 ),
        LitePoint( 698, AnalogPointType,            "", 0,    14, "", "", 1.0, 0 ),
        LitePoint( 708, AnalogPointType,            "", 0, 10001, "", "", 1.0, 0 ),
        LitePoint( 733, AnalogPointType,            "", 0, 10002, "", "", 1.0, 0 ),
        LitePoint( 720, AnalogPointType,            "", 0, 10003, "", "", 1.0, 0 ),
        LitePoint( 721, AnalogPointType,            "", 0, 10004, "", "", 1.0, 0 ),
        LitePoint( 697, AnalogPointType,            "", 0, 10010, "", "", 1.0, 0 ),
        LitePoint( 729, AnalogPointType,            "", 0, 10011, "", "", 1.0, 0 ),
        LitePoint( 684, AnalogPointType,            "", 0, 10026, "", "", 1.0, 0 ),
        LitePoint( 672, AnalogPointType,            "", 0, 10042, "", "", 1.0, 0 ),
        LitePoint( 722, AnalogPointType,            "", 0, 10068, "", "", 1.0, 0 ),
        LitePoint( 719, AnalogPointType,            "", 0, 20001, "", "", 1.0, 0 ),
        LitePoint( 688, AnalogPointType,            "", 0, 20002, "", "", 1.0, 0 ),
        LitePoint( 711, PulseAccumulatorPointType,  "", 0,     1, "", "", 1.0, 0 ),
        LitePoint( 727, PulseAccumulatorPointType,  "", 0,     2, "", "", 1.0, 0 ),
        LitePoint( 687, PulseAccumulatorPointType,  "", 0,     3, "", "", 1.0, 0 ),
        LitePoint( 675, StatusPointType,            "", 0,     1, "", "", 1.0, 0 ),
        LitePoint( 682, StatusPointType,            "", 0,     2, "", "", 1.0, 0 ),
        LitePoint( 694, StatusPointType,            "", 0,     3, "", "", 1.0, 0 ),
        LitePoint( 707, StatusPointType,            "", 0,     4, "", "", 1.0, 0 ),
        LitePoint( 679, StatusPointType,            "", 0,     5, "", "", 1.0, 0 ),
        LitePoint( 700, StatusPointType,            "", 0,     6, "", "", 1.0, 0 ),
        LitePoint( 690, StatusPointType,            "", 0,     7, "", "", 1.0, 0 ),
        LitePoint( 693, StatusPointType,            "", 0,     8, "", "", 1.0, 0 ),
        LitePoint( 676, StatusPointType,            "", 0,     9, "", "", 1.0, 0 ),
        LitePoint( 734, StatusPointType,            "", 0,    10, "", "", 1.0, 0 ),
        LitePoint( 674, StatusPointType,            "", 0,    11, "", "", 1.0, 0 ),
        LitePoint( 701, StatusPointType,            "", 0,    12, "", "", 1.0, 0 ),
        LitePoint( 702, StatusPointType,            "", 0,    13, "", "", 1.0, 0 ),
        LitePoint( 714, StatusPointType,            "", 0,    14, "", "", 1.0, 0 ),
        LitePoint( 716, StatusPointType,            "", 0,    15, "", "", 1.0, 0 ),
        LitePoint( 704, StatusPointType,            "", 0,    16, "", "", 1.0, 0 ),
        LitePoint( 695, StatusPointType,            "", 0,    24, "", "", 1.0, 0 ),
        LitePoint( 689, StatusPointType,            "", 0,    25, "", "", 1.0, 0 ),
        LitePoint( 705, StatusPointType,            "", 0,    26, "", "", 1.0, 0 ),
        LitePoint( 731, StatusPointType,            "", 0,    27, "", "", 1.0, 0 ),
        LitePoint( 683, StatusPointType,            "", 0,    28, "", "", 1.0, 0 ),
        LitePoint( 717, StatusPointType,            "", 0,    29, "", "", 1.0, 0 ),
        LitePoint( 726, StatusPointType,            "", 0,    34, "", "", 1.0, 0 ),
        // The following are not...
        LitePoint( 718, AnalogPointType,            "", 0,    15, "", "", 1.0, 0 ),
        LitePoint( 738, AnalogPointType,            "", 0,    16, "", "", 1.0, 0 ),
        LitePoint( 724, AnalogPointType,            "", 0,    17, "", "", 1.0, 0 ),
        LitePoint( 686, AnalogPointType,            "", 0,    18, "", "", 1.0, 0 ),
        LitePoint( 696, AnalogPointType,            "", 0,    19, "", "", 1.0, 0 ),
        LitePoint( 728, AnalogPointType,            "", 0,    20, "", "", 1.0, 0 ),
        LitePoint( 703, AnalogPointType,            "", 0,    21, "", "", 1.0, 0 ),
        LitePoint( 677, AnalogPointType,            "", 0,    22, "", "", 1.0, 0 ),
        LitePoint( 710, AnalogPointType,            "", 0, 10006, "", "", 1.0, 0 ),
        LitePoint( 709, AnalogPointType,            "", 0, 10007, "", "", 1.0, 0 ),
        LitePoint( 737, AnalogPointType,            "", 0, 10008, "", "", 1.0, 0 ),
        LitePoint( 685, AnalogPointType,            "", 0, 10009, "", "", 1.0, 0 ),
        LitePoint( 680, AnalogPointType,            "", 0, 10015, "", "", 1.0, 0 ),
        LitePoint( 736, AnalogPointType,            "", 0, 10016, "", "", 1.0, 0 ),
        LitePoint( 678, AnalogPointType,            "", 0, 10017, "", "", 1.0, 0 ),
        LitePoint( 673, AnalogPointType,            "", 0, 10018, "", "", 1.0, 0 ),
        LitePoint( 713, AnalogPointType,            "", 0, 10057, "", "", 1.0, 0 ),
        LitePoint( 735, AnalogPointType,            "", 0, 10110, "", "", 1.0, 0 ),
        LitePoint( 730, AnalogPointType,            "", 0, 10111, "", "", 1.0, 0 ),
        LitePoint( 725, AnalogPointType,            "", 0, 10113, "", "", 1.0, 0 ),
        LitePoint( 681, AnalogPointType,            "", 0, 10114, "", "", 1.0, 0 ),
        LitePoint( 692, StatusPointType,            "", 0,    17, "", "", 1.0, 0 ),
        LitePoint( 699, StatusPointType,            "", 0,    30, "", "", 1.0, 0 ),
        LitePoint( 740, StatusPointType,            "", 0,  2001, "", "", 1.0, 0 )
    };

    for ( const auto & item : databaseInput )
    {
        points->assignTwoWayPoint( item );
    }

    std::set<long>
        registrationPoints,
        expected
        {
            715,
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
                    points->getPointIdByAttribute( Attribute::TotalOperationCount ),    10, now ) );

    // Do it right!
    BOOST_CHECK( points->setTwoWayPulseAccumulatorPointValue(
                    points->getPointIdByAttribute( Attribute::TotalOperationCount ),    10, now ) );

    // See what comes out...
    BOOST_CHECK_EQUAL(  711, points->getPointIdByAttribute( Attribute::TotalOperationCount ) );
    BOOST_CHECK_EQUAL( 10.0, points->getPointValueByAttribute( Attribute::TotalOperationCount ) );

    LitePoint lp = points->getPointByAttribute( Attribute::TotalOperationCount );

    BOOST_CHECK_EQUAL(  PulseAccumulatorPointType, lp.getPointType() );

    BOOST_CHECK_EQUAL(  711, lp.getPointId() );
    BOOST_CHECK_EQUAL(    1, lp.getPointOffset() );

/// ----------- last control reason testing

    // Initialize all LastControl... attributes to 0
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonLocal ),         0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonRemote ),        0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonOvUv ),          0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonNeutralFault ),  0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonScheduled ),     0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonDigital ),       0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonAnalog ),        0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonTemperature ),   0, now ) );

    BOOST_CHECK_EQUAL( "Uninitialized", points->getLastControlText() );

    // Try to update LastControlReasonLocal with same timestamp
    BOOST_CHECK( ! points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonLocal ),         1, now ) );

    BOOST_CHECK_EQUAL( "Uninitialized", points->getLastControlText() );

    // Test different LastControlReason... attributes
    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonLocal ),         1, now ) );

    BOOST_CHECK_EQUAL( "Local", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonLocal ),         0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonRemote ),        1, now ) );

    BOOST_CHECK_EQUAL( "Remote", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonRemote ),        0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonOvUv ),          1, now ) );

    BOOST_CHECK_EQUAL( "OvUv", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonOvUv ),          0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonNeutralFault ),  1, now ) );

    BOOST_CHECK_EQUAL( "NeutralFault", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonNeutralFault ),  0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonScheduled ),     1, now ) );

    BOOST_CHECK_EQUAL( "Schedule", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonScheduled ),     0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonDigital ),       1, now ) );

    BOOST_CHECK_EQUAL( "Digital", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonDigital ),       0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonAnalog ),        1, now ) );

    BOOST_CHECK_EQUAL( "Analog", points->getLastControlText() );

    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonAnalog ),        0, now ) );
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonTemperature ),   1, now ) );

    BOOST_CHECK_EQUAL( "Temp", points->getLastControlText() );

    // LastControl... points are mutually exclusive - set another and validate the error message.
    now += 1;
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::LastControlReasonAnalog ),        1, now ) );
    BOOST_CHECK_EQUAL( "Unknown State. Value = 192", points->getLastControlText() );

/// ----------- ignored control reason testing

    BOOST_CHECK_EQUAL(  726, points->getPointIdByAttribute( Attribute::IgnoredIndicator ) );
    BOOST_CHECK_EQUAL(  698, points->getPointIdByAttribute( Attribute::IgnoredControlReason ) );

    // Initialize all IgnoredControl... attributes to 0
    BOOST_CHECK( points->setTwoWayStatusPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredIndicator ),           0, now ) );
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       0, now ) );

    BOOST_CHECK_EQUAL(  0.0, points->getPointValueByAttribute( Attribute::IgnoredIndicator ) );
    BOOST_CHECK_EQUAL(  0.0, points->getPointValueByAttribute( Attribute::IgnoredControlReason ) );

    BOOST_CHECK_EQUAL( "Local", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       1, now ) );

    BOOST_CHECK_EQUAL( "FaultCurrent", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       2, now ) );

    BOOST_CHECK_EQUAL( "EmVolt", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       3, now ) );

    BOOST_CHECK_EQUAL( "Time", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       4, now ) );

    BOOST_CHECK_EQUAL( "Voltage", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( true, points->controlRejectedByVoltageLimits() );    // <--- yeah, this guy...
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    {   // Check delta voltage related math
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::UnderVoltageThreshold ),  115.0, now ) );
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::OverVoltageThreshold ),   125.0, now ) );

        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::Voltage ),    114.0, now ) );

        BOOST_CHECK_EQUAL( false, points->checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::Voltage ),    115.0, now ) );

        BOOST_CHECK_EQUAL( false, points->checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::Voltage ),    115.1, now ) );

        BOOST_CHECK_EQUAL( true, points->checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::Voltage ),    120.0, now ) );

        BOOST_CHECK_EQUAL( true, points->checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::Voltage ),    124.9, now ) );

        BOOST_CHECK_EQUAL( true, points->checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::Voltage ),    125.0, now ) );

        BOOST_CHECK_EQUAL( false, points->checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points->setTwoWayAnalogPointValue(
                        points->getPointIdByAttribute( Attribute::Voltage ),    126.0, now ) );

        BOOST_CHECK_EQUAL( false, points->checkDeltaVoltageRejection() );
    }

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       5, now ) );

    BOOST_CHECK_EQUAL( "Digital1", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       6, now ) );

    BOOST_CHECK_EQUAL( "Analog1", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       7, now ) );

    BOOST_CHECK_EQUAL( "Digital2", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       8, now ) );

    BOOST_CHECK_EQUAL( "Analog2", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),       9, now ) );

    BOOST_CHECK_EQUAL( "Digital3", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      10, now ) );

    BOOST_CHECK_EQUAL( "Analog3", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      11, now ) );

    BOOST_CHECK_EQUAL( "Digital4", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      12, now ) );

    BOOST_CHECK_EQUAL( "Temp", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      13, now ) );

    BOOST_CHECK_EQUAL( "Remote", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      14, now ) );

    BOOST_CHECK_EQUAL( "NtrlLockOut", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      15, now ) );

    BOOST_CHECK_EQUAL( "BrownOut", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      16, now ) );

    BOOST_CHECK_EQUAL( "BadActRelay", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );

    now += 1;
    BOOST_CHECK( points->setTwoWayAnalogPointValue(
                    points->getPointIdByAttribute( Attribute::IgnoredControlReason ),      17, now ) );

    BOOST_CHECK_EQUAL( "unknown", points->getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points->controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points->isControlAccepted() );
}

BOOST_AUTO_TEST_CASE( test_TwoWayCBCPoints_CBC_802X )
{
    struct test_CtiCCTwoWayPointsCbc802x: public CtiCCTwoWayPointsCbc802x
    {
        test_CtiCCTwoWayPointsCbc802x( const long paoid, const std::string & paotype,
                                       std::unique_ptr<LastControlReason>    lastControlReason,
                                       std::unique_ptr<IgnoredControlReason> ignoredControlReason )
            :   CtiCCTwoWayPointsCbc802x( paoid, paotype,
                                          std::move( lastControlReason ),
                                          std::move( ignoredControlReason ) )
        {
            // empty...
        }
    }
    points( 545, "CBC 8020",
            std::make_unique<test_LastControlReasonCbc802x>(),
            std::make_unique<test_IgnoredControlReasonCbc802x>() );

    LitePoint   databaseInput[] =
    {
        // These type/offsets are defined in the 2-way point code for CBC 802X devices
        LitePoint( 342, AnalogPointType,            "", 0,     1, "", "", 1.0, -17 ),
        LitePoint( 335, AnalogPointType,            "", 0,     2, "", "", 1.0, -17 ),
        LitePoint( 340, AnalogPointType,            "", 0,    12, "", "", 1.0, -17 ),
        LitePoint( 352, AnalogPointType,            "", 0,   114, "", "", 1.0, -17 ),
        LitePoint( 325, AnalogPointType,            "", 0,  9999, "", "", 1.0, -17 ),
        LitePoint( 331, AnalogPointType,            "", 0, 10001, "", "", 1.0, -17 ),
        LitePoint( 359, AnalogPointType,            "", 0, 10002, "", "", 1.0, -17 ),
        LitePoint( 313, AnalogPointType,            "", 0, 10318, "", "", 1.0, -17 ),
        LitePoint( 323, PulseAccumulatorPointType,  "", 0,     1, "", "", 1.0, -17 ),
        LitePoint( 338, PulseAccumulatorPointType,  "", 0,     2, "", "", 1.0, -17 ),
        LitePoint( 362, PulseAccumulatorPointType,  "", 0,     3, "", "", 1.0, -17 ),
        LitePoint( 330, PulseAccumulatorPointType,  "", 0,     4, "", "", 1.0, -17 ),
        LitePoint( 350, PulseAccumulatorPointType,  "", 0,     5, "", "", 1.0, -17 ),
        LitePoint( 318, StatusPointType,            "", 0,     1, "", "", 1.0, -17 ),
        LitePoint( 345, StatusPointType,            "", 0,     2, "", "", 1.0, -17 ),
        LitePoint( 334, StatusPointType,            "", 0,     3, "", "", 1.0, -17 ),
        LitePoint( 311, StatusPointType,            "", 0,    72, "", "", 1.0, -17 ),
        LitePoint( 354, StatusPointType,            "", 0,    84, "", "", 1.0, -17 ),
        LitePoint( 321, StatusPointType,            "", 0,    86, "", "", 1.0, -17 ),
        LitePoint( 336, StatusPointType,            "", 0,    89, "", "", 1.0, -17 ),
        // The following are not...
        LitePoint( 355, AnalogPointType,            "", 0,     5, "", "", 1.0, -17 ),
        LitePoint( 357, AnalogPointType,            "", 0,     6, "", "", 1.0, -17 ),
        LitePoint( 320, AnalogPointType,            "", 0,     7, "", "", 1.0, -17 ),
        LitePoint( 351, AnalogPointType,            "", 0,     8, "", "", 1.0, -17 ),
        LitePoint( 361, AnalogPointType,            "", 0,    21, "", "", 1.0, -17 ),
        LitePoint( 324, AnalogPointType,            "", 0, 10005, "", "", 1.0, -17 ),
        LitePoint( 329, AnalogPointType,            "", 0, 10006, "", "", 1.0, -17 ),
        LitePoint( 339, AnalogPointType,            "", 0, 10007, "", "", 1.0, -17 ),
        LitePoint( 312, AnalogPointType,            "", 0, 10008, "", "", 1.0, -17 ),
        LitePoint( 314, AnalogPointType,            "", 0, 10009, "", "", 1.0, -17 ),
        LitePoint( 326, AnalogPointType,            "", 0, 10010, "", "", 1.0, -17 ),
        LitePoint( 316, AnalogPointType,            "", 0, 10011, "", "", 1.0, -17 ),
        LitePoint( 337, AnalogPointType,            "", 0, 10012, "", "", 1.0, -17 ),
        LitePoint( 341, AnalogPointType,            "", 0, 10013, "", "", 1.0, -17 ),
        LitePoint( 328, AnalogPointType,            "", 0, 10015, "", "", 1.0, -17 ),
        LitePoint( 332, AnalogPointType,            "", 0, 10016, "", "", 1.0, -17 ),
        LitePoint( 343, AnalogPointType,            "", 0, 10018, "", "", 1.0, -17 ),
        LitePoint( 315, AnalogPointType,            "", 0, 10019, "", "", 1.0, -17 ),
        LitePoint( 333, AnalogPointType,            "", 0, 10020, "", "", 1.0, -17 ),
        LitePoint( 347, StatusPointType,            "", 0,     4, "", "", 1.0, -17 ),
        LitePoint( 344, StatusPointType,            "", 0,     5, "", "", 1.0, -17 ),
        LitePoint( 349, StatusPointType,            "", 0,     6, "", "", 1.0, -17 ),
        LitePoint( 353, StatusPointType,            "", 0,    14, "", "", 1.0, -17 ),
        LitePoint( 319, StatusPointType,            "", 0,    15, "", "", 1.0, -17 ),
        LitePoint( 322, StatusPointType,            "", 0,    68, "", "", 1.0, -17 ),
        LitePoint( 360, StatusPointType,            "", 0,    69, "", "", 1.0, -17 ),
        LitePoint( 348, StatusPointType,            "", 0,    70, "", "", 1.0, -17 ),
        LitePoint( 356, StatusPointType,            "", 0,    71, "", "", 1.0, -17 ),
        LitePoint( 317, StatusPointType,            "", 0,    83, "", "", 1.0, -17 ),
        LitePoint( 327, StatusPointType,            "", 0,    87, "", "", 1.0, -17 ),
        LitePoint( 358, StatusPointType,            "", 0,    88, "", "", 1.0, -17 ),
        LitePoint( 552, StatusPointType,            "", 0,  2000, "", "", 1.0, -17 ),
        LitePoint( 346, StatusPointType,            "", 0,  2001, "", "", 1.0, -17 )
    };

    for ( const auto & item : databaseInput )
    {
        points.assignTwoWayPoint( item );
    }

    std::set<long>
        registrationPoints,
        expected
        {
            325, 342, 345,
            340, 331, 359, 313, 323, 338, 362, 330, 350,
            318, 334, 311, 352, 354, 321, 336, 335
        };

    points.addAllCBCPointsToRegMsg( registrationPoints );

    BOOST_CHECK_EQUAL_RANGES( registrationPoints, expected );

/// -----------

    CtiTime     now( CtiDate( 22, 8, 2014 ), 9, 0, 0 );

/// ----------- last control reason testing

    BOOST_CHECK_EQUAL( "Uninitialized", points.getLastControlText() );

    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       0, now ) );

    BOOST_CHECK_EQUAL( "Manual", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       1, now ) );

    BOOST_CHECK_EQUAL( "SCADA Override", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       2, now ) );

    BOOST_CHECK_EQUAL( "Fault Current", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       3, now ) );

    BOOST_CHECK_EQUAL( "Emergency Voltage", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       4, now ) );

    BOOST_CHECK_EQUAL( "Time ONOFF", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       5, now ) );

    BOOST_CHECK_EQUAL( "OVUV Control", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       6, now ) );

    BOOST_CHECK_EQUAL( "VAR", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       7, now ) );

    BOOST_CHECK_EQUAL( "Va", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       8, now ) );

    BOOST_CHECK_EQUAL( "Vb", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),       9, now ) );

    BOOST_CHECK_EQUAL( "Vc", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      10, now ) );

    BOOST_CHECK_EQUAL( "Ia", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      11, now ) );

    BOOST_CHECK_EQUAL( "Ib", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      12, now ) );

    BOOST_CHECK_EQUAL( "Ic", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      13, now ) );

    BOOST_CHECK_EQUAL( "Temp", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      14, now ) );

    BOOST_CHECK_EQUAL( "N/A", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      15, now ) );

    BOOST_CHECK_EQUAL( "Time", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      16, now ) );

    BOOST_CHECK_EQUAL( "N/A", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      17, now ) );

    BOOST_CHECK_EQUAL( "Bad Active Relay", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      18, now ) );

    BOOST_CHECK_EQUAL( "NC Lockout", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      19, now ) );

    BOOST_CHECK_EQUAL( "Control Accepted", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      20, now ) );

    BOOST_CHECK_EQUAL( "Auto Mode", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      21, now ) );

    BOOST_CHECK_EQUAL( "Reclose Block", points.getLastControlText() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::LastControlReason ),      22, now ) );

    BOOST_CHECK_EQUAL( "Unknown State. Value = 22", points.getLastControlText() );

/// ----------- ignored control reason testing

    // doesn't have this
    BOOST_CHECK_EQUAL(    0, points.getPointIdByAttribute( Attribute::IgnoredIndicator ) );

    BOOST_CHECK_EQUAL(  352, points.getPointIdByAttribute( Attribute::IgnoredControlReason ) );

    // Initialize all IgnoredControl... attributes to 0
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       0, now ) );

    BOOST_CHECK_EQUAL(  0.0, points.getPointValueByAttribute( Attribute::IgnoredControlReason ) );

    BOOST_CHECK_EQUAL( "Godzilla", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       1, now ) );

    BOOST_CHECK_EQUAL( "Ghidorah", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       2, now ) );

    BOOST_CHECK_EQUAL( "Mechagodzilla", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       3, now ) );

    BOOST_CHECK_EQUAL( "Biollante", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       4, now ) );

    BOOST_CHECK_EQUAL( "Mothra", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       5, now ) );

    BOOST_CHECK_EQUAL( "Destoroyah", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( true, points.controlRejectedByVoltageLimits() );    // <--- yeah, this guy...
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    {   // Check delta voltage related math
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::UnderVoltageThreshold ),   115.0, now ) );
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::OverVoltageThreshold ),    125.0, now ) );

        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::Voltage ),     114.0, now ) );

        BOOST_CHECK_EQUAL( false, points.checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::Voltage ),     115.0, now ) );

        BOOST_CHECK_EQUAL( false, points.checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::Voltage ),     115.1, now ) );

        BOOST_CHECK_EQUAL( true, points.checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::Voltage ),     120.0, now ) );

        BOOST_CHECK_EQUAL( true, points.checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::Voltage ),     124.9, now ) );

        BOOST_CHECK_EQUAL( true, points.checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::Voltage ),     125.0, now ) );

        BOOST_CHECK_EQUAL( false, points.checkDeltaVoltageRejection() );

        now += 1;
        BOOST_CHECK( points.setTwoWayAnalogPointValue(
                        points.getPointIdByAttribute( Attribute::Voltage ),     126.0, now ) );

        BOOST_CHECK_EQUAL( false, points.checkDeltaVoltageRejection() );
    }

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       6, now ) );

    BOOST_CHECK_EQUAL( "Anguirus", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       7, now ) );

    BOOST_CHECK_EQUAL( "Orga", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       8, now ) );

    BOOST_CHECK_EQUAL( "Hedorah", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),       9, now ) );

    BOOST_CHECK_EQUAL( "Rodan", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),      10, now ) );

    BOOST_CHECK_EQUAL( "Megaguirus", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),      11, now ) );

    BOOST_CHECK_EQUAL( "Battra", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),      12, now ) );

    BOOST_CHECK_EQUAL( "Megalon", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),      13, now ) );

    BOOST_CHECK_EQUAL( "Baragon", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),      14, now ) );

    BOOST_CHECK_EQUAL( "Ebirah", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),      15, now ) );

    BOOST_CHECK_EQUAL( "Unknown State. Value = 15", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( false, points.isControlAccepted() );

    now += 1;
    BOOST_CHECK( points.setTwoWayAnalogPointValue(
                    points.getPointIdByAttribute( Attribute::IgnoredControlReason ),      19, now ) );

    BOOST_CHECK_EQUAL( "Control Accepted", points.getIgnoredControlText() );
    BOOST_CHECK_EQUAL( false, points.controlRejectedByVoltageLimits() );
    BOOST_CHECK_EQUAL( true,  points.isControlAccepted() );      // <-- the special case
}

BOOST_AUTO_TEST_SUITE_END()

