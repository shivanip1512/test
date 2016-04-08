#include <boost/test/unit_test.hpp>

#include <boost/ptr_container/ptr_map.hpp>

#include "boost_test_helpers.h"
#include "test_reader.h"
#include "ccsubstation.h"


struct test_CtiCCSubstation : CtiCCSubstation
{
    test_CtiCCSubstation()
        :   CtiCCSubstation()
    {

    }

    test_CtiCCSubstation( Cti::RowReader & rdr )
        :   CtiCCSubstation( rdr )
    {

    }

    using CtiCCSubstation::isDirty;
    using CtiCCSubstation::setDirty;
};


BOOST_AUTO_TEST_SUITE( test_Substation )

BOOST_AUTO_TEST_CASE( test_Substation_construction )
{
    boost::ptr_map< long, test_CtiCCSubstation >    substations;

    {   // Core area object initialization

        using CCSubstationRow     = Cti::Test::StringRow<10>;
        using CCSubstationReader  = Cti::Test::TestReader<CCSubstationRow>;

        CCSubstationRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "VoltReductionPointID",
            "AdditionalFlags",
            "SAEnabledID"
        };

        std::vector<CCSubstationRow> rowVec
        {
            {
                "3",
                "CAPCONTROL",
                "CAPCONTROL",
                "Groom Lake Station",
                "CCSUBSTATION",
                "Top Secret",
                "N",
                "101",
                CCSubstationReader::getNullString(),
                CCSubstationReader::getNullString()
            },
            {
                "13",
                "CAPCONTROL",
                "CAPCONTROL",
                "Station 2_0002",
                "CCSUBSTATION",
                "(none)",
                "Y",
                "0",
                "NYNYNYNNNNNNNNNNNNNN",
                "200"
            }
        };

        CCSubstationReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            substations.insert( paoID, new test_CtiCCSubstation( reader ) );
        }
    }

// First entry

    // CapControlPao
    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getPaoId() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", substations[  3 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", substations[  3 ].getPaoClass() );
    BOOST_CHECK_EQUAL( "Groom Lake Station", substations[  3 ].getPaoName() );
    BOOST_CHECK_EQUAL(       "CCSUBSTATION", substations[  3 ].getPaoType() );
    BOOST_CHECK_EQUAL(         "Top Secret", substations[  3 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getDisabledStatePointId() );
                                                 
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getPointIds()->size() );
                                                 
    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getConfirmationStats().getPAOId() );
                                                 
    // CtiCCSubstation                                     
    BOOST_CHECK_EQUAL(                  101, substations[  3 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                   "", substations[  3 ].getParentName() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getParentId() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getDisplayOrder() );
    BOOST_CHECK_EQUAL(                 -1.0, substations[  3 ].getPFactor() );
    BOOST_CHECK_EQUAL(                 -1.0, substations[  3 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getChildVoltReductionFlag() );
                          
    BOOST_CHECK_EQUAL(                false, substations[  3 ].isDirty() );

// Second entry        
                       
    // CapControlPao   
    BOOST_CHECK_EQUAL(                   13, substations[ 13 ].getPaoId() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", substations[ 13 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", substations[ 13 ].getPaoClass() );
    BOOST_CHECK_EQUAL(     "Station 2_0002", substations[ 13 ].getPaoName() );
    BOOST_CHECK_EQUAL(       "CCSUBSTATION", substations[ 13 ].getPaoType() );
    BOOST_CHECK_EQUAL(             "(none)", substations[ 13 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getDisabledStatePointId() );

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getPointIds()->size() );

    BOOST_CHECK_EQUAL(                   13, substations[ 13 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                   13, substations[ 13 ].getConfirmationStats().getPAOId() );

    // CtiCCSubstation                                         
    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getVoltReductionFlag() );        // flags[ 2 ] == 'N' -- false
    BOOST_CHECK_EQUAL(                   "", substations[ 13 ].getParentName() );
    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getParentId() );
    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getDisplayOrder() );
    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getOvUvDisabledFlag() );         // flags[ 0 ] == 'N' -- false
    BOOST_CHECK_EQUAL(                  200, substations[ 13 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getSaEnabledFlag() );            // flags[ 1 ] == 'Y' -- true
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getRecentlyControlledFlag() );   // flags[ 3 ] == 'Y' -- true
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );       // flags[ 4 ] == 'N' -- false
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getChildVoltReductionFlag() );   // flags[ 5 ] == 'Y' -- true

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

// Test dirty and updated flags behavior via toggling the setters for each data member

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setVoltReductionControlId( 0 );

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setVoltReductionControlId( 1234 );

    BOOST_CHECK_EQUAL(                 1234, substations[ 13 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setVoltReductionControlId( 0 );

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setVoltReductionFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setVoltReductionFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setVoltReductionFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                   "", substations[ 13 ].getParentName() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setParentName( "" );

    BOOST_CHECK_EQUAL(                   "", substations[ 13 ].getParentName() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setParentName( "Alpha" );

    BOOST_CHECK_EQUAL(              "Alpha", substations[ 13 ].getParentName() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(              "Alpha", substations[ 13 ].getParentName() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setParentName( "" );

    BOOST_CHECK_EQUAL(                   "", substations[ 13 ].getParentName() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                   "", substations[ 13 ].getParentName() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getParentId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setParentId( 0 );

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getParentId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setParentId( 110 );

    BOOST_CHECK_EQUAL(                  110, substations[ 13 ].getParentId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setParentId( 0 );

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getParentId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getDisplayOrder() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setDisplayOrder( 0 );

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getDisplayOrder() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setDisplayOrder( 2 );

    BOOST_CHECK_EQUAL(                    2, substations[ 13 ].getDisplayOrder() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setDisplayOrder( 0 );

    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getDisplayOrder() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setPFactor( -1.0 );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setPFactor( 0.7 );

    BOOST_CHECK_EQUAL(                  0.7, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                  0.7, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setPFactor( -1.0 );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setEstPFactor( -1.0 );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setEstPFactor( 0.7 );

    BOOST_CHECK_EQUAL(                  0.7, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                  0.7, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setEstPFactor( -1.0 );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setOvUvDisabledFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setOvUvDisabledFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setOvUvDisabledFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                  200, substations[ 13 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setSaEnabledId( 200 );

    BOOST_CHECK_EQUAL(                  200, substations[ 13 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setSaEnabledId( 201 );

    BOOST_CHECK_EQUAL(                  201, substations[ 13 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                  201, substations[ 13 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setSaEnabledId( 200 );

    BOOST_CHECK_EQUAL(                  200, substations[ 13 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                  200, substations[ 13 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setSaEnabledFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setSaEnabledFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setSaEnabledFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setRecentlyControlledFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setRecentlyControlledFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setRecentlyControlledFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    //

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setChildVoltReductionFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setChildVoltReductionFlag( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setChildVoltReductionFlag( true );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );
}

BOOST_AUTO_TEST_SUITE_END()

