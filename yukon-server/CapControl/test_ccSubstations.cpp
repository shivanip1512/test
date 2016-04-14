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

    test_CtiCCSubstation * replicate() const
    {
        return new test_CtiCCSubstation( *this );
    }

    using CtiCCSubstation::isDirty;
    using CtiCCSubstation::setDirty;

protected:

    test_CtiCCSubstation( const test_CtiCCSubstation & other ) = default;
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
                                                 
    BOOST_CHECK_EQUAL(                    1, substations[  3 ].getPointIds()->size() );
                                                 
    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getConfirmationStats().getPAOId() );
                                                 
    // CtiCCSubstation                                     
    BOOST_CHECK_EQUAL(                  101, substations[  3 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getParentId() );
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
    BOOST_CHECK_EQUAL(                    0, substations[ 13 ].getParentId() );
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

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setPFactor( -1.0 );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setPFactor( 0.7 );

    BOOST_CHECK_EQUAL(                  0.7, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                  0.7, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setPFactor( -1.0 );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getPFactor() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
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
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setStationUpdatedFlag( false );
    substations[ 13 ].setDirty( false );

    BOOST_CHECK_EQUAL(                  0.7, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

    substations[ 13 ].setEstPFactor( -1.0 );

    BOOST_CHECK_EQUAL(                 -1.0, substations[ 13 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                 true, substations[ 13 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[ 13 ].isDirty() );

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

// Test and document our object copying behavior via replicate

    boost::scoped_ptr<test_CtiCCSubstation>     newSubstation( substations[ 3 ].replicate() );

    // Our new object is identical to the one it was replicated from.

    // CapControlPao
    BOOST_CHECK_EQUAL(                    3, newSubstation->getPaoId() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", newSubstation->getPaoCategory() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", newSubstation->getPaoClass() );
    BOOST_CHECK_EQUAL( "Groom Lake Station", newSubstation->getPaoName() );
    BOOST_CHECK_EQUAL(       "CCSUBSTATION", newSubstation->getPaoType() );
    BOOST_CHECK_EQUAL(         "Top Secret", newSubstation->getPaoDescription() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getDisableFlag() );
    BOOST_CHECK_EQUAL(                    0, newSubstation->getDisabledStatePointId() );

    BOOST_CHECK_EQUAL(                    1, newSubstation->getPointIds()->size() );

    BOOST_CHECK_EQUAL(                    3, newSubstation->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                    3, newSubstation->getConfirmationStats().getPAOId() );

    // CtiCCSubstation                                     
    BOOST_CHECK_EQUAL(                  101, newSubstation->getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    0, newSubstation->getParentId() );
    BOOST_CHECK_EQUAL(                 -1.0, newSubstation->getPFactor() );
    BOOST_CHECK_EQUAL(                 -1.0, newSubstation->getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    0, newSubstation->getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getChildVoltReductionFlag() );

    BOOST_CHECK_EQUAL(                false, newSubstation->isDirty() );

    // Mess with it -- make sure it is independent from its source

    newSubstation->setPaoName( "Mongo Fett" );
    newSubstation->setPaoDescription( "Failed Clone" );
    newSubstation->setDisableFlag( true );
    newSubstation->setDisabledStatePointId( 1234 );
    newSubstation->setVoltReductionControlId( 2345 );
    newSubstation->setOvUvDisabledFlag( true );
    newSubstation->setPFactor( 0.5 );
    newSubstation->setEstPFactor( 0.25 );
    newSubstation->setChildVoltReductionFlag( true );   // sets 'dirty' and 'updated' as well
    newSubstation->setSaEnabledId( 279 );
    newSubstation->setSaEnabledFlag( true );

    // validate our changes

    // CapControlPao
    BOOST_CHECK_EQUAL(                    3, newSubstation->getPaoId() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", newSubstation->getPaoCategory() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", newSubstation->getPaoClass() );
    BOOST_CHECK_EQUAL(         "Mongo Fett", newSubstation->getPaoName() );
    BOOST_CHECK_EQUAL(       "CCSUBSTATION", newSubstation->getPaoType() );
    BOOST_CHECK_EQUAL(       "Failed Clone", newSubstation->getPaoDescription() );
    BOOST_CHECK_EQUAL(                 true, newSubstation->getDisableFlag() );
    BOOST_CHECK_EQUAL(                 1234, newSubstation->getDisabledStatePointId() );

    BOOST_CHECK_EQUAL(                    1, newSubstation->getPointIds()->size() );

    BOOST_CHECK_EQUAL(                    3, newSubstation->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                    3, newSubstation->getConfirmationStats().getPAOId() );

    // CtiCCSubstation                                     
    BOOST_CHECK_EQUAL(                 2345, newSubstation->getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    0, newSubstation->getParentId() );
    BOOST_CHECK_EQUAL(                  0.5, newSubstation->getPFactor() );
    BOOST_CHECK_EQUAL(                 0.25, newSubstation->getEstPFactor() );
    BOOST_CHECK_EQUAL(                 true, newSubstation->getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                  279, newSubstation->getSaEnabledId() );
    BOOST_CHECK_EQUAL(                 true, newSubstation->getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, newSubstation->getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                 true, newSubstation->getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                 true, newSubstation->getChildVoltReductionFlag() );

    BOOST_CHECK_EQUAL(                 true, newSubstation->isDirty() );

    // Verify our original object we replicated from is unchanged

    // CapControlPao
    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getPaoId() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", substations[  3 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(         "CAPCONTROL", substations[  3 ].getPaoClass() );
    BOOST_CHECK_EQUAL( "Groom Lake Station", substations[  3 ].getPaoName() );
    BOOST_CHECK_EQUAL(       "CCSUBSTATION", substations[  3 ].getPaoType() );
    BOOST_CHECK_EQUAL(         "Top Secret", substations[  3 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getDisabledStatePointId() );

    BOOST_CHECK_EQUAL(                    1, substations[  3 ].getPointIds()->size() );

    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                    3, substations[  3 ].getConfirmationStats().getPAOId() );

    // CtiCCSubstation                                     
    BOOST_CHECK_EQUAL(                  101, substations[  3 ].getVoltReductionControlId() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getParentId() );
    BOOST_CHECK_EQUAL(                 -1.0, substations[  3 ].getPFactor() );
    BOOST_CHECK_EQUAL(                 -1.0, substations[  3 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    0, substations[  3 ].getSaEnabledId() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getSaEnabledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getStationUpdatedFlag() );
    BOOST_CHECK_EQUAL(                false, substations[  3 ].getChildVoltReductionFlag() );

    BOOST_CHECK_EQUAL(                false, substations[  3 ].isDirty() );

// Test operator== and operator!=

    BOOST_CHECK(     *newSubstation == substations[ 3 ] );
    BOOST_CHECK( ! ( *newSubstation != substations[ 3 ] ) );

    BOOST_CHECK_EQUAL(  3, newSubstation->getPaoId() );
    BOOST_CHECK_EQUAL(  3, newSubstation->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  3, newSubstation->getConfirmationStats().getPAOId() );

    BOOST_CHECK_EQUAL(  3, substations[ 3 ].getPaoId() );
    BOOST_CHECK_EQUAL(  3, substations[ 3 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  3, substations[ 3 ].getConfirmationStats().getPAOId() );

    newSubstation->setPaoId( 4 );

    BOOST_CHECK( ! ( *newSubstation == substations[ 3 ] ) );
    BOOST_CHECK(     *newSubstation != substations[ 3 ] );

    BOOST_CHECK_EQUAL(  4, newSubstation->getPaoId() );
    BOOST_CHECK_EQUAL(  4, newSubstation->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  4, newSubstation->getConfirmationStats().getPAOId() );

    BOOST_CHECK_EQUAL(  3, substations[ 3 ].getPaoId() );
    BOOST_CHECK_EQUAL(  3, substations[ 3 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  3, substations[ 3 ].getConfirmationStats().getPAOId() );
}

BOOST_AUTO_TEST_CASE( test_ccSubstation_point_assignment )
{
    boost::ptr_map< long, CtiCCSubstation >     substations;

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
            }
        };

        CCSubstationReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            substations.insert( paoID, new CtiCCSubstation( reader ) );
        }
    }

    // Validate the attached point IDs

    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(    1, substations[ 3 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(  101, substations[ 3 ].getVoltReductionControlId() );

    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getOperationStats().getUserDefOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getOperationStats().getDailyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getOperationStats().getWeeklyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getOperationStats().getMonthlyOpSuccessPercentId() );

    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getConfirmationStats().getUserDefCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getConfirmationStats().getDailyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getConfirmationStats().getWeeklyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, substations[ 3 ].getConfirmationStats().getMonthlyCommSuccessPercentId() );

    // Validate the contents of the point ID collection
    {
        std::vector<long>   expected
        {
            101
        };

        BOOST_CHECK_EQUAL_RANGES( expected, *substations[ 3 ].getPointIds() );
    }

    // Validate the points we register for
    {
        std::vector<long>   expected
        {
            101
        };

        std::set<long>  registeredPoints;

        substations[ 3 ].getPointRegistrationIds( registeredPoints );

        BOOST_CHECK_EQUAL_RANGES( expected, registeredPoints );
    }

    {   // Point initialization

        using CCSubstationPointRow     = Cti::Test::StringRow<3>;
        using CCSubstationPointReader  = Cti::Test::TestReader<CCSubstationPointRow>;

        CCSubstationPointRow columnNames =
        {
            "POINTID",
            "POINTTYPE",
            "POINTOFFSET"
        };

        std::vector<CCSubstationPointRow> rowVec
        {
            {   "300",  "Status",   "1001"  },      // not an actual point
            {   "671",  "Analog",   "10001" },      // daily ops
            {   "681",  "Analog",   "10002" },      // weekly ops
            {   "683",  "Analog",   "10003" },      // monthly ops
            {   "690",  "Analog",   "10000" },      // user def ops
            {   "691",  "Analog",   "10010" },      // user def conf
            {   "692",  "Analog",   "170"   },      // not an actual point
            {   "693",  "Analog",   "10013" },      // monthly conf
            {   "695",  "Analog",   "10012" },      // weekly conf
            {   "697",  "Analog",   "10011" },      // daily conf
            {  "1000",  "Status",   "500"   },      // disabled state
            {  "1005",  "Status",   "-1"    },      // tag point
            {  "1009",  "Analog",   "180"   }       // not an actual point
        };

        CCSubstationPointReader reader( columnNames, rowVec );

        while ( reader() )
        {
            substations[ 3 ].assignPoint( reader );
        }
    }

    // Validate the attached point IDs

    BOOST_CHECK_EQUAL( 1000, substations[ 3 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(   10, substations[ 3 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(  101, substations[ 3 ].getVoltReductionControlId() );

    BOOST_CHECK_EQUAL(  690, substations[ 3 ].getOperationStats().getUserDefOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  671, substations[ 3 ].getOperationStats().getDailyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  681, substations[ 3 ].getOperationStats().getWeeklyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  683, substations[ 3 ].getOperationStats().getMonthlyOpSuccessPercentId() );

    BOOST_CHECK_EQUAL(  691, substations[ 3 ].getConfirmationStats().getUserDefCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  697, substations[ 3 ].getConfirmationStats().getDailyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  695, substations[ 3 ].getConfirmationStats().getWeeklyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  693, substations[ 3 ].getConfirmationStats().getMonthlyCommSuccessPercentId() );

    // Validate the contents of the point ID collection
    {
        std::vector<long>   expected
        {
            101, 671, 681, 683, 690, 691, 693, 695, 697, 1000
        };

        BOOST_CHECK_EQUAL_RANGES( expected, *substations[ 3 ].getPointIds() );
    }

    // Validate the points we register for
    {
        std::vector<long>   expected
        {
            101, 671, 681, 683, 690, 1000
        };

        std::set<long>  registeredPoints;

        substations[ 3 ].getPointRegistrationIds( registeredPoints );

        BOOST_CHECK_EQUAL_RANGES( expected, registeredPoints );
    }
}

BOOST_AUTO_TEST_SUITE_END()

