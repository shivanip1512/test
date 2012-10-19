#include <boost/test/unit_test.hpp>

#include "ccUnitTestUtil.h"

#include "IVVCStrategy.h"
#include "ZoneManager.h"

using Cti::CapControl::Zone;
using Cti::CapControl::ZoneLoader;
using Cti::CapControl::ZoneManager;

extern unsigned long _MAX_KVAR;
extern unsigned long _SEND_TRIES;

BOOST_AUTO_TEST_SUITE( test_IVVCAlgorithm )

class StrategyUnitTestLoader : public StrategyLoader
{

public:

    StrategyUnitTestLoader()
    {
        requestFactory.reset(new PointDataRequestFactory);
    }

    virtual StrategyManager::StrategyMap load(const long ID)
    {
        StrategyManager::StrategyMap loaded;

        if (ID < 0)
        {
            long IDs[] = { 100 };

            for (int i = 0; i < sizeof(IDs)/ sizeof(*IDs); i++)
            {
                loadSingle(IDs[i], loaded);
            }
        }
        else
        {
            loadSingle(ID, loaded);
        }

        return loaded;
    }

private:

    PointDataRequestFactoryPtr requestFactory;

    void loadSingle(const long ID, StrategyManager::StrategyMap &strategies)
    {
        bool doInsertion = true;

        StrategyManager::SharedPtr  newStrategy;

        switch (ID)
        {
            case 100:
            {
                IVVCStrategy* strat = new IVVCStrategy(requestFactory);
                strat->setStrategyName("Test IVVC Strategy");
                newStrategy.reset(strat);
                break;
            }
            default:
            {
                doInsertion = false;
                break;
            }
        }

        if (doInsertion)
        {
            newStrategy->setStrategyId(ID);
            strategies[ID] = newStrategy;
        }
    }
};


class ZoneUnitTestLoader : public ZoneLoader
{

public:

    // default construction and destruction is OK

    virtual ZoneManager::ZoneMap load(const long Id)
    {
        ZoneManager::ZoneMap zones;

        if (Id < 0)
        {
            long Ids[] = { 101, 103, 104, 106, 107, 108, 109 };

            for (int i = 0; i < sizeof(Ids)/ sizeof(*Ids); i++)
            {
                loadSingle(Ids[i], zones);
            }
        }
        else
        {
            loadSingle(Id, zones);
        }

        return zones;
    }

private:

    void loadSingle(const long Id, ZoneManager::ZoneMap &zones)
    {
        bool doInsertion = true;
        ZoneManager::SharedPtr newZone;

        switch (Id)
        {
            case 101:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Root Zone" ) );
                break;
            }
            case 103:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Left Zone" ) );
                break;
            }
            case 104:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 103, 35, "The Left Right Zone" ) );
                break;
            }
            case 106:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 107, 35, "The Right Left Zone" ) );
                break;
            }
            case 107:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Right Zone" ) );
                break;
            }
            case 108:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 107, 35, "The Right Right Zone" ) );
                break;
            }
            case 109:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 103, 35, "The Left Left Zone" ) );
                break;
            }

            default:  doInsertion = false;
        }

        if (doInsertion)
        {
            zones[Id] = newZone;
        }
    }
};


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_voltage_flatness_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        double test_calculateVf(const PointValueMap &voltages)
        {
            return calculateVf(voltages);
        }
    };

    std::set<long> ignorePoints;
    test_IVVCAlgorithm  _algorithm;

    PointValue    _value;
    PointValueMap _voltages;

    _value.quality   = NormalQuality;
    _value.timestamp = CtiTime();

    _value.value    = 120.0;
    _voltages[1000] = _value;

    _value.value    = 121.2;
    _voltages[1001] = _value;

    _value.value    = 122.1;
    _voltages[1002] = _value;

    _value.value    = 119.6;
    _voltages[1003] = _value;

    _value.value    = 120.3;
    _voltages[1004] = _value;

    _value.value    = 119.8;
    _voltages[1005] = _value;

    _value.value    = 118.2;
    _voltages[1006] = _value;

    _value.value    = 121.9;
    _voltages[1007] = _value;

    /*
        The second and third parameter are point IDs to ignore in the flatness calculation.  Use this
         feature to generate different test values with the same set of data.
    */

    // check to 6 significant digits (rounded)

    // Don't exclude any points
    BOOST_CHECK_CLOSE( 2.187500 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    // Exclude a single point
    //ignorePoints.insert(1000);
    _voltages.erase(1000);
    BOOST_CHECK_CLOSE( 2.242857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 2.242857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    //ignorePoints.clear();
    _value.value    = 120.0;
    _voltages[1000] = _value;

    //ignorePoints.insert(1002);
    _voltages.erase(1002);
    BOOST_CHECK_CLOSE( 1.942857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 1.942857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    // Exclude two points
    //ignorePoints.insert(1000);
    _voltages.erase(1000);
    BOOST_CHECK_CLOSE( 1.966667 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 1.966667 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    //ignorePoints.clear();
    _value.value    = 120.0;
    _voltages[1000] = _value;
    _value.value    = 122.1;
    _voltages[1002] = _value;

    //ignorePoints.insert(1006);
    _voltages.erase(1006);
    //ignorePoints.insert(1007);
    _voltages.erase(1007);
    BOOST_CHECK_CLOSE( 0.900000 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.900000 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_target_power_factor_var_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        double test_calculateTargetPFVars(const double targetPF, const double wattValue)
        {
            return calculateTargetPFVars(targetPF, wattValue);
        }
    };

    test_IVVCAlgorithm  _algorithm;

    // check to 1 significant digits (rounded)

    BOOST_CHECK_CLOSE( -881.9 , _algorithm.test_calculateTargetPFVars( -75.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(  881.9 , _algorithm.test_calculateTargetPFVars(  75.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(  484.3 , _algorithm.test_calculateTargetPFVars(  90.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE( -484.3 , _algorithm.test_calculateTargetPFVars( -90.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(    0.0 , _algorithm.test_calculateTargetPFVars( 100.0, 1000.0) , 0.1 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_regulator_tap_operation_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        using IVVCAlgorithm::calculateVte;
    };

    test_IVVCAlgorithm  _algorithm;
    std::map<long, CtiCCMonitorPointPtr>    monitorMap;
    IVVCStrategy    strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    PointValue    _value;
    PointValueMap _voltages;

    _value.quality   = NormalQuality;
    _value.timestamp = CtiTime();

    _value.value    = 120.0;
    _voltages[1000] = _value;

    _value.value    = 121.2;
    _voltages[1001] = _value;

    _value.value    = 122.1;
    _voltages[1002] = _value;

    _value.value    = 119.6;
    _voltages[1003] = _value;

    _value.value    = 120.3;
    _voltages[1004] = _value;

    _value.value    = 119.8;
    _voltages[1005] = _value;

    _value.value    = 118.2;
    _voltages[1006] = _value;

    _value.value    = 121.9;
    _voltages[1007] = _value;

    strategy.restoreParameters("Lower Volt Limit", "PEAK", "115.0");
    strategy.restoreParameters("Voltage Regulation Margin", "PEAK", "4.0");
    strategy.restoreParameters("Upper Volt Limit", "PEAK", "125.0");
    /*
        The fifth and sixth parameter are point IDs to ignore in the tap operation calculation.  Use this
         feature to generate different test values with the same set of data.
    */

    // Don't exclude any points - all voltages within limits, on both sides of the margin - no operation

    BOOST_CHECK_EQUAL(  0 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the only point that is below the marginal voltage - should tap down
    _voltages.erase(1006);

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // Don't exclude any points - single voltage over limit - should tap down
    _value.value    = 118.2;
    _voltages[1006] = _value;
    strategy.restoreParameters("Upper Volt Limit", "PEAK", "122.0");

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the only point that is above the max voltage - no operation
    _voltages.erase(1002);

    BOOST_CHECK_EQUAL(  0 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // Don't exclude any points - single voltage over limit and single voltage under limit - should tap down
    _value.value    = 122.1;
    _voltages[1002] = _value;

    strategy.restoreParameters("Lower Volt Limit", "PEAK", "119.0");
    strategy.restoreParameters("Voltage Regulation Margin", "PEAK", "1.0");

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the point above the max voltage - should tap up
    _voltages.erase(1002);

    BOOST_CHECK_EQUAL(  1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the point below the min voltage - should tap down
    _value.value    = 122.1;
    _voltages[1002] = _value;

    _voltages.erase(1006);

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the points above the max voltage and below min - no operation
    _voltages.erase(1002);

    BOOST_CHECK_EQUAL(  0 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_zone_subsets_by_subbus)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    Zone::IdSet results;

    results.insert(101);
    results.insert(103);
    results.insert(104);
    results.insert(106);
    results.insert(107);
    results.insert(108);
    results.insert(109);

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus(35);

    BOOST_CHECK_EQUAL_COLLECTIONS( results.begin(), results.end(), subset.begin(), subset.end() );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_all_children_of_zone)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    // Get all children of ID 101

    Zone::IdSet results101;

    results101.insert(103);
    results101.insert(104);
    results101.insert(106);
    results101.insert(107);
    results101.insert(108);
    results101.insert(109);

    Zone::IdSet subset101 = zoneManager.getAllChildrenOfZone(101);

    BOOST_CHECK_EQUAL_COLLECTIONS( results101.begin(), results101.end(), subset101.begin(), subset101.end() );

    // Get all children of ID 103

    Zone::IdSet results103;

    results103.insert(104);
    results103.insert(109);

    Zone::IdSet subset103 = zoneManager.getAllChildrenOfZone(103);

    BOOST_CHECK_EQUAL_COLLECTIONS( results103.begin(), results103.end(), subset103.begin(), subset103.end() );

    // Get all children of ID 107

    Zone::IdSet results107;

    results107.insert(106);
    results107.insert(108);

    Zone::IdSet subset107 = zoneManager.getAllChildrenOfZone(107);

    BOOST_CHECK_EQUAL_COLLECTIONS( results107.begin(), results107.end(), subset107.begin(), subset107.end() );

    // Get all children of ID 104, 106, 108 and 109

    Zone::IdSet emptySet;

    Zone::IdSet subset104 = zoneManager.getAllChildrenOfZone(104);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset104.begin(), subset104.end() );

    Zone::IdSet subset106 = zoneManager.getAllChildrenOfZone(106);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset106.begin(), subset106.end() );

    Zone::IdSet subset108 = zoneManager.getAllChildrenOfZone(108);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset108.begin(), subset108.end() );

    Zone::IdSet subset109 = zoneManager.getAllChildrenOfZone(109);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset109.begin(), subset109.end() );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_all_children_of_zone_with_zone_reload_and_reconfiguration)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    Zone::IdSet results;

    results.insert(101);
    results.insert(103);
    results.insert(104);
    results.insert(106);
    results.insert(107);
    results.insert(108);
    results.insert(109);

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus(35);

    BOOST_CHECK_EQUAL_COLLECTIONS( results.begin(), results.end(), subset.begin(), subset.end() );

    results.erase(108);

    zoneManager.unload(108);

    subset = zoneManager.getZoneIdsBySubbus(35);

    BOOST_CHECK_EQUAL_COLLECTIONS( results.begin(), results.end(), subset.begin(), subset.end() );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_voltage_violation_component_defaults_and_assignments)
{
    IVVCStrategy        strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    // test the defaults
    //
    BOOST_CHECK_CLOSE(    3.0, strategy.getLowVoltageViolationBandwidth(), 1e-6 );
    BOOST_CHECK_CLOSE(    1.0, strategy.getHighVoltageViolationBandwidth(), 1e-6 );
    BOOST_CHECK_CLOSE( -150.0, strategy.getEmergencyLowVoltageViolationCost(), 1e-6 );
    BOOST_CHECK_CLOSE(  -10.0, strategy.getLowVoltageViolationCost(), 1e-6 );
    BOOST_CHECK_CLOSE(   70.0, strategy.getHighVoltageViolationCost(), 1e-6 );
    BOOST_CHECK_CLOSE(  300.0, strategy.getEmergencyHighVoltageViolationCost(), 1e-6 );

    // load in new values and check they reflect correctly
    //
    strategy.restoreParameters("Low Voltage Violation",  "BANDWIDTH",      "2.5");
    strategy.restoreParameters("Low Voltage Violation",  "COST",           "-31.2");
    strategy.restoreParameters("Low Voltage Violation",  "EMERGENCY_COST", "-123.45");
    strategy.restoreParameters("High Voltage Violation", "BANDWIDTH",      "5.75");
    strategy.restoreParameters("High Voltage Violation", "COST",           "95.9");
    strategy.restoreParameters("High Voltage Violation", "EMERGENCY_COST", "246.8");

    BOOST_CHECK_CLOSE(     2.5, strategy.getLowVoltageViolationBandwidth(), 1e-6 );
    BOOST_CHECK_CLOSE(    5.75, strategy.getHighVoltageViolationBandwidth(), 1e-6 );
    BOOST_CHECK_CLOSE( -123.45, strategy.getEmergencyLowVoltageViolationCost(), 1e-6 );
    BOOST_CHECK_CLOSE(   -31.2, strategy.getLowVoltageViolationCost(), 1e-6 );
    BOOST_CHECK_CLOSE(    95.9, strategy.getHighVoltageViolationCost(), 1e-6 );
    BOOST_CHECK_CLOSE(   246.8, strategy.getEmergencyHighVoltageViolationCost(), 1e-6 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_voltage_violation_component)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        using IVVCAlgorithm::voltageViolationCalculator;
    };

    test_IVVCAlgorithm  algorithm;
    IVVCStrategy        strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    strategy.restoreParameters("Lower Volt Limit", "PEAK", "110.0");
    strategy.restoreParameters("Upper Volt Limit", "PEAK", "130.0");

    BOOST_CHECK_CLOSE(  110.0, strategy.getLowerVoltLimit(true), 1e-6 );
    BOOST_CHECK_CLOSE(  130.0, strategy.getUpperVoltLimit(true), 1e-6 );

    // voltages between the lower and upper limit inclusive
    //  
    for ( double voltage = 110.0; voltage <= 130.0; voltage += 0.5 )
    {
        BOOST_CHECK_CLOSE(  0.0, algorithm.voltageViolationCalculator( voltage, &strategy, true ), 1e-6 );
    }

    // voltages less than lower limit but not emergency -- cost increase by 10 per volt under
    //
    BOOST_CHECK_CLOSE(   5.0, algorithm.voltageViolationCalculator( 109.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE(  10.0, algorithm.voltageViolationCalculator( 109.0, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE(  15.0, algorithm.voltageViolationCalculator( 108.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE(  20.0, algorithm.voltageViolationCalculator( 108.0, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE(  25.0, algorithm.voltageViolationCalculator( 107.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE(  30.0, algorithm.voltageViolationCalculator( 107.0, &strategy, true ), 1e-6 );

    // emergency voltages less than lower limit -- cost increase by 150 per volt under
    //
    BOOST_CHECK_CLOSE( 105.0, algorithm.voltageViolationCalculator( 106.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 180.0, algorithm.voltageViolationCalculator( 106.0, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 255.0, algorithm.voltageViolationCalculator( 105.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 330.0, algorithm.voltageViolationCalculator( 105.0, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 405.0, algorithm.voltageViolationCalculator( 104.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 480.0, algorithm.voltageViolationCalculator( 104.0, &strategy, true ), 1e-6 );

    // voltages greater than upper limit but not emergency -- cost increase by 70 per volt over
    //
    BOOST_CHECK_CLOSE(  35.0, algorithm.voltageViolationCalculator( 130.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE(  70.0, algorithm.voltageViolationCalculator( 131.0, &strategy, true ), 1e-6 );

    // emergency voltages greater than upper limit -- cost increase by 300 per volt over
    //
    BOOST_CHECK_CLOSE( 220.0, algorithm.voltageViolationCalculator( 131.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 370.0, algorithm.voltageViolationCalculator( 132.0, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 520.0, algorithm.voltageViolationCalculator( 132.5, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 670.0, algorithm.voltageViolationCalculator( 133.0, &strategy, true ), 1e-6 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_voltage_violation_component_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        using IVVCAlgorithm::calculateVoltageViolation;
    };

    test_IVVCAlgorithm  algorithm;
    IVVCStrategy        strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    PointValue    _value;
    PointValueMap _voltages;

    _value.quality   = NormalQuality;
    _value.timestamp = CtiTime();

    _value.value    = 120.0;
    _voltages[1000] = _value;

    _value.value    = 121.2;
    _voltages[1001] = _value;

    _value.value    = 122.1;
    _voltages[1002] = _value;

    _value.value    = 119.6;
    _voltages[1003] = _value;

    _value.value    = 120.3;
    _voltages[1004] = _value;

    _value.value    = 119.8;
    _voltages[1005] = _value;

    _value.value    = 118.2;
    _voltages[1006] = _value;

    _value.value    = 121.9;
    _voltages[1007] = _value;

    strategy.restoreParameters("Lower Volt Limit", "PEAK", "115.0");
    strategy.restoreParameters("Upper Volt Limit", "PEAK", "125.0");

    // all points within the limits...
    //
    BOOST_CHECK_CLOSE( 0.0, algorithm.calculateVoltageViolation( _voltages, &strategy, true ), 1e-6 );

    // add a low non-emergency low voltage
    //
    _value.value    = 114.5;
    _voltages[1008] = _value;

    BOOST_CHECK_CLOSE( 5.0, algorithm.calculateVoltageViolation( _voltages, &strategy, true ), 1e-6 );

    // make it an emergency low voltage
    //
    _value.value    = 111.0;
    _voltages[1008] = _value;

    BOOST_CHECK_CLOSE( 180.0, algorithm.calculateVoltageViolation( _voltages, &strategy, true ), 1e-6 );

    // add in a non-emergency high voltage
    //
    _value.value    = 126.0;
    _voltages[1009] = _value;

    BOOST_CHECK_CLOSE( 250.0, algorithm.calculateVoltageViolation( _voltages, &strategy, true ), 1e-6 );

    // make it an emergency high voltage
    //
    _value.value    = 127.0;
    _voltages[1009] = _value;

    BOOST_CHECK_CLOSE( 550.0, algorithm.calculateVoltageViolation( _voltages, &strategy, true ), 1e-6 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_power_factor_correction_component_calculation_defaults_and_assignments)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        using IVVCAlgorithm::feederPFCorrectionCalculator;
    };

    test_IVVCAlgorithm  algorithm;
    IVVCStrategy        strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    // defaults

    BOOST_CHECK_CLOSE( 0.02, strategy.getPowerFactorCorrectionBandwidth(), 1e-6 );
    BOOST_CHECK_CLOSE( 20.0, strategy.getPowerFactorCorrectionCost(),      1e-6 );
    BOOST_CHECK_CLOSE( 2.0,  strategy.getPowerFactorCorrectionMaxCost(),   1e-6 );

    // assignments

    strategy.restoreParameters("Power Factor Correction", "BANDWIDTH", "0.03");
    strategy.restoreParameters("Power Factor Correction", "COST",      "45.0");
    strategy.restoreParameters("Power Factor Correction", "MAX_COST",  "6.25");

    BOOST_CHECK_CLOSE( 0.03, strategy.getPowerFactorCorrectionBandwidth(), 1e-6 );
    BOOST_CHECK_CLOSE( 45.0, strategy.getPowerFactorCorrectionCost(),      1e-6 );
    BOOST_CHECK_CLOSE( 6.25, strategy.getPowerFactorCorrectionMaxCost(),   1e-6 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_power_factor_correction_component_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        using IVVCAlgorithm::feederPFCorrectionCalculator;
    };

    test_IVVCAlgorithm  algorithm;
    IVVCStrategy        strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    strategy.restoreParameters("Target PF", "PEAK", "80.0");

    // check against defaults for the power factor correction

    for ( double pf = 0.0; pf <= 0.680; pf += 0.020 )
    {
        BOOST_CHECK_CLOSE( 2.0, algorithm.feederPFCorrectionCalculator( pf, &strategy, true ), 1e-6 );
    }

    BOOST_CHECK_CLOSE( 1.80, algorithm.feederPFCorrectionCalculator( 0.690, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.60, algorithm.feederPFCorrectionCalculator( 0.700, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.40, algorithm.feederPFCorrectionCalculator( 0.710, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.20, algorithm.feederPFCorrectionCalculator( 0.720, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.00, algorithm.feederPFCorrectionCalculator( 0.730, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.80, algorithm.feederPFCorrectionCalculator( 0.740, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.60, algorithm.feederPFCorrectionCalculator( 0.750, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.40, algorithm.feederPFCorrectionCalculator( 0.760, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.20, algorithm.feederPFCorrectionCalculator( 0.770, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.02, algorithm.feederPFCorrectionCalculator( 0.779, &strategy, true ), 1e-6 );

    for ( double pf = 0.780; pf <= 0.820; pf += 0.002 )
    {
        BOOST_CHECK_CLOSE( 0.0, algorithm.feederPFCorrectionCalculator( pf, &strategy, true ), 1e-6 );
    }

    BOOST_CHECK_CLOSE( 0.02, algorithm.feederPFCorrectionCalculator( 0.821, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.20, algorithm.feederPFCorrectionCalculator( 0.830, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.40, algorithm.feederPFCorrectionCalculator( 0.840, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.60, algorithm.feederPFCorrectionCalculator( 0.850, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.80, algorithm.feederPFCorrectionCalculator( 0.860, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.00, algorithm.feederPFCorrectionCalculator( 0.870, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.20, algorithm.feederPFCorrectionCalculator( 0.880, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.40, algorithm.feederPFCorrectionCalculator( 0.890, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.60, algorithm.feederPFCorrectionCalculator( 0.900, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.80, algorithm.feederPFCorrectionCalculator( 0.910, &strategy, true ), 1e-6 );

    for ( double pf = 0.920; pf <= 2.000; pf += 0.020 )
    {
        BOOST_CHECK_CLOSE( 2.0, algorithm.feederPFCorrectionCalculator( pf, &strategy, true ), 1e-6 );
    }
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_power_factor_correction_component_calculation_2)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        using IVVCAlgorithm::feederPFCorrectionCalculator;
    };

    test_IVVCAlgorithm  algorithm;
    IVVCStrategy        strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    strategy.restoreParameters("Target PF", "PEAK", "-80.0");

    // check against defaults for the power factor correction

    for ( double pf = 0.0; pf <= 1.080; pf += 0.020 )
    {
        BOOST_CHECK_CLOSE( 2.0, algorithm.feederPFCorrectionCalculator( pf, &strategy, true ), 1e-6 );
    }

    BOOST_CHECK_CLOSE( 1.80, algorithm.feederPFCorrectionCalculator( 1.090, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.60, algorithm.feederPFCorrectionCalculator( 1.100, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.40, algorithm.feederPFCorrectionCalculator( 1.110, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.20, algorithm.feederPFCorrectionCalculator( 1.120, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.00, algorithm.feederPFCorrectionCalculator( 1.130, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.80, algorithm.feederPFCorrectionCalculator( 1.140, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.60, algorithm.feederPFCorrectionCalculator( 1.150, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.40, algorithm.feederPFCorrectionCalculator( 1.160, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.20, algorithm.feederPFCorrectionCalculator( 1.170, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.02, algorithm.feederPFCorrectionCalculator( 1.179, &strategy, true ), 1e-6 );

    for ( double pf = 1.180; pf <= 1.220; pf += 0.002 )
    {
        BOOST_CHECK_CLOSE( 0.0, algorithm.feederPFCorrectionCalculator( pf, &strategy, true ), 1e-6 );
    }

    BOOST_CHECK_CLOSE( 0.02, algorithm.feederPFCorrectionCalculator( 1.221, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.20, algorithm.feederPFCorrectionCalculator( 1.230, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.40, algorithm.feederPFCorrectionCalculator( 1.240, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.60, algorithm.feederPFCorrectionCalculator( 1.250, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 0.80, algorithm.feederPFCorrectionCalculator( 1.260, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.00, algorithm.feederPFCorrectionCalculator( 1.270, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.20, algorithm.feederPFCorrectionCalculator( 1.280, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.40, algorithm.feederPFCorrectionCalculator( 1.290, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.60, algorithm.feederPFCorrectionCalculator( 1.300, &strategy, true ), 1e-6 );
    BOOST_CHECK_CLOSE( 1.80, algorithm.feederPFCorrectionCalculator( 1.310, &strategy, true ), 1e-6 );

    for ( double pf = 1.320; pf <= 2.000; pf += 0.020 )
    {
        BOOST_CHECK_CLOSE( 2.0, algorithm.feederPFCorrectionCalculator( pf, &strategy, true ), 1e-6 );
    }
}


BOOST_AUTO_TEST_SUITE_END()
