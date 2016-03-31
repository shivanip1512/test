#include <boost/test/unit_test.hpp>

#include <boost/ptr_container/ptr_map.hpp>

#include "boost_test_helpers.h"
#include "test_reader.h"
#include "ccarea.h"
#include "ccsparea.h"


BOOST_AUTO_TEST_SUITE( test_Area )

BOOST_AUTO_TEST_CASE( test_ccArea_construction )
{
    boost::ptr_map< long, CtiCCArea >    areas;

    {   // Core area object initialization

        using CCAreaRow     = Cti::Test::StringRow<10>;
        using CCAreaReader  = Cti::Test::TestReader<CCAreaRow>;

        CCAreaRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "VoltReductionPointID",
            "additionalflags",
            "ControlValue"
        };

        std::vector<CCAreaRow> rowVec
        {
            {
                "2",
                "CAPCONTROL",
                "CAPCONTROL",
                "Area 51",
                "CCAREA",
                "Top Secret",
                "N",
                "551",
                CCAreaReader::getNullString(),
                CCAreaReader::getNullString()
            },
            {
                "22",
                "CAPCONTROL",
                "CAPCONTROL",
                "Area 2_0002",
                "CCAREA",
                "(none)",
                "Y",
                "0",
                CCAreaReader::getNullString(),
                CCAreaReader::getNullString()
            },
            {
                "42",
                "CAPCONTROL",
                "CAPCONTROL",
                "Level 42",
                "CCAREA",
                "Something About You",
                "N",
                "0",
                "YNYNNNNNNNNNNNNNNNNN",
                "1"
            },
            {
                "52",
                "CAPCONTROL",
                "CAPCONTROL",
                "Empty Area",
                "CCAREA",
                "",
                "N",
                "651",
                "NYNYNNNNNNNNNNNNNNNN",
                "1"
            }
        };

        CCAreaReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            areas.insert( paoID, new CtiCCArea( reader, nullptr ) );
        }
    }

// First entry

    // CapControlPao
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoClass() );
    BOOST_CHECK_EQUAL(                "Area 51", areas[  2 ].getPaoName() );
    BOOST_CHECK_EQUAL(                 "CCAREA", areas[  2 ].getPaoType() );
    BOOST_CHECK_EQUAL(             "Top Secret", areas[  2 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getDisabledStatePointId() );
                                                     
    BOOST_CHECK_EQUAL(                        1, areas[  2 ].getPointIds()->size() );
                                                     
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getConfirmationStats().getPAOId() );
                                                     
    // Controllable                                  
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getStrategyId() );
                                                     
    // CtiCCAreaBase                                 
    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );  // no dynamic data -- default = false
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );           // no dynamic data -- default = false
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );            // no dynamic data -- default = false
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
                                                         
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getSubstationIds().size() );
                                                         
    // CtiCCArea                                         
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getReEnableAreaFlag() );           // no dynamic data -- default = false
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getChildVoltReductionFlag() );     // no dynamic data -- default = false
                          
// Second entry           
                          
    // CapControlPao      
    BOOST_CHECK_EQUAL(                       22, areas[ 22 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 22 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 22 ].getPaoClass() );
    BOOST_CHECK_EQUAL(            "Area 2_0002", areas[ 22 ].getPaoName() );
    BOOST_CHECK_EQUAL(                 "CCAREA", areas[ 22 ].getPaoType() );
    BOOST_CHECK_EQUAL(                 "(none)", areas[ 22 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                     true, areas[ 22 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getDisabledStatePointId() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       22, areas[ 22 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       22, areas[ 22 ].getConfirmationStats().getPAOId() );
                          
    // Controllable                                      
    BOOST_CHECK_EQUAL(                       -1, areas[ 22 ].getStrategyId() );
                          
    // CtiCCAreaBase                                     
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 22 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 22 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getSubstationIds().size() );
                          
    // CtiCCArea                                         
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getChildVoltReductionFlag() );
                          
// Third entry            
                          
    // CapControlPao      
    BOOST_CHECK_EQUAL(                       42, areas[ 42 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 42 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 42 ].getPaoClass() );
    BOOST_CHECK_EQUAL(               "Level 42", areas[ 42 ].getPaoName() );
    BOOST_CHECK_EQUAL(                 "CCAREA", areas[ 42 ].getPaoType() );
    BOOST_CHECK_EQUAL(    "Something About You", areas[ 42 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getDisabledStatePointId() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       42, areas[ 42 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       42, areas[ 42 ].getConfirmationStats().getPAOId() );
                          
    // Controllable                                      
    BOOST_CHECK_EQUAL(                       -1, areas[ 42 ].getStrategyId() );
                          
    // CtiCCAreaBase                                     
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].getVoltReductionControlValue() );  // dynamic data == 1 but since pointID == 0 -- false
    BOOST_CHECK_EQUAL(                     true, areas[ 42 ].getOvUvDisabledFlag() );           // additionalflags[ 0 ] == 'y' -- true
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].getAreaUpdatedFlag() );            // additionalflags[ 3 ] == 'n' -- false
    BOOST_CHECK_EQUAL(                       -1, areas[ 42 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 42 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getSubstationIds().size() );
                          
    // CtiCCArea                                         
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].getReEnableAreaFlag() );           // additionalflags[ 1 ] == 'n' -- false
    BOOST_CHECK_EQUAL(                     true, areas[ 42 ].getChildVoltReductionFlag() );     // additionalflags[ 2 ] == 'y' -- true
                          
// Fourth entry           
                          
    // CapControlPao      
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 52 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 52 ].getPaoClass() );
    BOOST_CHECK_EQUAL(             "Empty Area", areas[ 52 ].getPaoName() );
    BOOST_CHECK_EQUAL(                 "CCAREA", areas[ 52 ].getPaoType() );
    BOOST_CHECK_EQUAL(                       "", areas[ 52 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getDisabledStatePointId() );
                          
    BOOST_CHECK_EQUAL(                        1, areas[ 52 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getConfirmationStats().getPAOId() );
                          
    // Controllable       
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getStrategyId() );
                          
    // CtiCCAreaBase      
    BOOST_CHECK_EQUAL(                      651, areas[ 52 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getVoltReductionControlValue() );  // dynamic data == 1 with pointID > 0 -- true
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].isSpecial() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getSubstationIds().size() );
                          
    // CtiCCArea          
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getChildVoltReductionFlag() );

// Test dirty and updated flags behavior via toggling the setters for each data member

    // CtiCCAreaBase contained data members

    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlPointId( 551 );

    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlPointId( 552 );

    BOOST_CHECK_EQUAL(                      552, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlPointId( 551 );

    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlValue( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlValue( true );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlValue( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setOvUvDisabledFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setOvUvDisabledFlag( true );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setOvUvDisabledFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setPFactor( 128 );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setEstPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setEstPFactor( 128 );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setEstPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    // CtiCCArea specific data members

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setReEnableAreaFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setReEnableAreaFlag( true );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setReEnableAreaFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setChildVoltReductionFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setChildVoltReductionFlag( true );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setChildVoltReductionFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getChildVoltReductionFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

// Test and document our object copying behavior via replicate

    boost::scoped_ptr<CtiCCArea>    newArea( areas[ 2 ].replicate() );

    // Our new object is identical to the one it was replicated from.

    BOOST_CHECK_EQUAL(                        2, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoClass() );
    BOOST_CHECK_EQUAL(                "Area 51", newArea->getPaoName() );
    BOOST_CHECK_EQUAL(                 "CCAREA", newArea->getPaoType() );
    BOOST_CHECK_EQUAL(             "Top Secret", newArea->getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, newArea->getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, newArea->getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(                        1, newArea->getPointIds()->size() );
    BOOST_CHECK_EQUAL(                        2, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, newArea->getConfirmationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getStrategyId() );
    BOOST_CHECK_EQUAL(                      551, newArea->getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, newArea->getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, newArea->getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, newArea->isDirty() );
    BOOST_CHECK_EQUAL(                    false, newArea->isSpecial() );
    BOOST_CHECK_EQUAL(                    false, newArea->getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getEstPFactor() );
    BOOST_CHECK_EQUAL(                        0, newArea->getSubstationIds().size() );
    BOOST_CHECK_EQUAL(                    false, newArea->getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, newArea->getChildVoltReductionFlag() );

    // Mess with it -- make sure it is independent from its source

    newArea->setPaoName( "Mongo Fett" );
    newArea->setPaoDescription( "Failed Clone" );
    newArea->setDisableFlag( true );
    newArea->setDisabledStatePointId( 1234 );
    newArea->setVoltReductionControlPointId( 2345 );
    newArea->setOvUvDisabledFlag( true );   // sets 'dirty' and 'updated' as well
    newArea->setPFactor( 0.5 );
    newArea->setEstPFactor( 0.25 );
    newArea->setReEnableAreaFlag( true );
    newArea->setChildVoltReductionFlag( true );

    // validate our changes

    BOOST_CHECK_EQUAL(                        2, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoClass() );
    BOOST_CHECK_EQUAL(             "Mongo Fett", newArea->getPaoName() );
    BOOST_CHECK_EQUAL(                 "CCAREA", newArea->getPaoType() );
    BOOST_CHECK_EQUAL(           "Failed Clone", newArea->getPaoDescription() );
    BOOST_CHECK_EQUAL(                     true, newArea->getDisableFlag() );
    BOOST_CHECK_EQUAL(                     1234, newArea->getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(                        1, newArea->getPointIds()->size() );
    BOOST_CHECK_EQUAL(                        2, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, newArea->getConfirmationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getStrategyId() );
    BOOST_CHECK_EQUAL(                     2345, newArea->getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, newArea->getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                     true, newArea->getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                     true, newArea->isDirty() );
    BOOST_CHECK_EQUAL(                    false, newArea->isSpecial() );
    BOOST_CHECK_EQUAL(                     true, newArea->getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                      0.5, newArea->getPFactor() );
    BOOST_CHECK_EQUAL(                     0.25, newArea->getEstPFactor() );
    BOOST_CHECK_EQUAL(                        0, newArea->getSubstationIds().size() );
    BOOST_CHECK_EQUAL(                     true, newArea->getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                     true, newArea->getChildVoltReductionFlag() );

    // Verify our original object we replicated from is unchanged

    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoClass() );
    BOOST_CHECK_EQUAL(                "Area 51", areas[  2 ].getPaoName() );
    BOOST_CHECK_EQUAL(                 "CCAREA", areas[  2 ].getPaoType() );
    BOOST_CHECK_EQUAL(             "Top Secret", areas[  2 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(                        1, areas[  2 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getConfirmationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getStrategyId() );
    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getSubstationIds().size() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getChildVoltReductionFlag() );

// Test operator== and operator!=

    BOOST_CHECK(     *newArea == areas[ 2 ] );
    BOOST_CHECK( ! ( *newArea != areas[ 2 ] ) );

    BOOST_CHECK_EQUAL(  2, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(  2, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  2, newArea->getConfirmationStats().getPAOId() );

    BOOST_CHECK_EQUAL(  2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getConfirmationStats().getPAOId() );

    newArea->setPaoId( 4 );

    BOOST_CHECK( ! ( *newArea == areas[ 2 ] ) );
    BOOST_CHECK(     *newArea != areas[ 2 ] );

    BOOST_CHECK_EQUAL(  4, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(  4, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  4, newArea->getConfirmationStats().getPAOId() );

    BOOST_CHECK_EQUAL(  2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getConfirmationStats().getPAOId() );
}

BOOST_AUTO_TEST_CASE( test_ccSpecialArea_construction )
{
    boost::ptr_map< long, CtiCCSpecial >    areas;

    {   // Core area object initialization

        using CCAreaRow     = Cti::Test::StringRow<10>;
        using CCAreaReader  = Cti::Test::TestReader<CCAreaRow>;

        CCAreaRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "VoltReductionPointID",
            "additionalflags",
            "ControlValue"
        };

        std::vector<CCAreaRow> rowVec
        {
            {
                "2",
                "CAPCONTROL",
                "CAPCONTROL",
                "Area 51",
                "CCSPECIALAREA",
                "Top Secret",
                "N",
                "551",
                CCAreaReader::getNullString(),
                CCAreaReader::getNullString()
            },
            {
                "22",
                "CAPCONTROL",
                "CAPCONTROL",
                "Area 2_0002",
                "CCSPECIALAREA",
                "(none)",
                "Y",
                "0",
                CCAreaReader::getNullString(),
                CCAreaReader::getNullString()
            },
            {
                "42",
                "CAPCONTROL",
                "CAPCONTROL",
                "Level 42",
                "CCSPECIALAREA",
                "Something About You",
                "N",
                "0",
                "YNYNNNNNNNNNNNNNNNNN",
                "1"
            },
            {
                "52",
                "CAPCONTROL",
                "CAPCONTROL",
                "Empty Area",
                "CCSPECIALAREA",
                "",
                "N",
                "651",
                "NYNYNNNNNNNNNNNNNNNN",
                "1"
            }
        };

        CCAreaReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            areas.insert( paoID, new CtiCCSpecial( reader, nullptr ) );
        }
    }

// First entry

    // CapControlPao
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoClass() );
    BOOST_CHECK_EQUAL(                "Area 51", areas[  2 ].getPaoName() );
    BOOST_CHECK_EQUAL(          "CCSPECIALAREA", areas[  2 ].getPaoType() );
    BOOST_CHECK_EQUAL(             "Top Secret", areas[  2 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getDisabledStatePointId() );
                                                     
    BOOST_CHECK_EQUAL(                        1, areas[  2 ].getPointIds()->size() );
                                                     
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getConfirmationStats().getPAOId() );
                                                     
    // Controllable                                  
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getStrategyId() );
                                                     
    // CtiCCAreaBase                                 
    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
                                                         
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getSubstationIds().size() );
                                                         
    // CtiCCSpecial                                         
                          
// Second entry           
                          
    // CapControlPao      
    BOOST_CHECK_EQUAL(                       22, areas[ 22 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 22 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 22 ].getPaoClass() );
    BOOST_CHECK_EQUAL(            "Area 2_0002", areas[ 22 ].getPaoName() );
    BOOST_CHECK_EQUAL(          "CCSPECIALAREA", areas[ 22 ].getPaoType() );
    BOOST_CHECK_EQUAL(                 "(none)", areas[ 22 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                     true, areas[ 22 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getDisabledStatePointId() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       22, areas[ 22 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       22, areas[ 22 ].getConfirmationStats().getPAOId() );
                          
    // Controllable                                      
    BOOST_CHECK_EQUAL(                       -1, areas[ 22 ].getStrategyId() );
                          
    // CtiCCAreaBase                                     
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[ 22 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[ 22 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 22 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 22 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 22 ].getSubstationIds().size() );
                          
    // CtiCCSpecial                                         
                          
// Third entry            
                          
    // CapControlPao      
    BOOST_CHECK_EQUAL(                       42, areas[ 42 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 42 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 42 ].getPaoClass() );
    BOOST_CHECK_EQUAL(               "Level 42", areas[ 42 ].getPaoName() );
    BOOST_CHECK_EQUAL(          "CCSPECIALAREA", areas[ 42 ].getPaoType() );
    BOOST_CHECK_EQUAL(    "Something About You", areas[ 42 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getDisabledStatePointId() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       42, areas[ 42 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       42, areas[ 42 ].getConfirmationStats().getPAOId() );
                          
    // Controllable                                      
    BOOST_CHECK_EQUAL(                       -1, areas[ 42 ].getStrategyId() );
                          
    // CtiCCAreaBase                                     
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                     true, areas[ 42 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[ 42 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[ 42 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 42 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 42 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 42 ].getSubstationIds().size() );
                          
    // CtiCCSpecial                                         
                          
// Fourth entry           
                          
    // CapControlPao      
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 52 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[ 52 ].getPaoClass() );
    BOOST_CHECK_EQUAL(             "Empty Area", areas[ 52 ].getPaoName() );
    BOOST_CHECK_EQUAL(          "CCSPECIALAREA", areas[ 52 ].getPaoType() );
    BOOST_CHECK_EQUAL(                       "", areas[ 52 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getDisabledStatePointId() );
                          
    BOOST_CHECK_EQUAL(                        1, areas[ 52 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getConfirmationStats().getPAOId() );
                          
    // Controllable       
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getStrategyId() );
                          
    // CtiCCAreaBase      
    BOOST_CHECK_EQUAL(                      651, areas[ 52 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].isSpecial() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getSubstationIds().size() );
                          
    // CtiCCSpecial                                         

// Test dirty and updated flags behavior via toggling the setters for each data member

    // CtiCCAreaBase contained data members

    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlPointId( 551 );

    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlPointId( 552 );

    BOOST_CHECK_EQUAL(                      552, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlPointId( 551 );

    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlValue( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlValue( true );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setVoltReductionControlValue( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setOvUvDisabledFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setOvUvDisabledFlag( true );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setOvUvDisabledFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setDirty( false );
    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setPFactor( 128 );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    //

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setEstPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setEstPFactor( 128 );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                      128, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setEstPFactor( -1 );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].getAreaUpdatedFlag() );

    areas[  2 ].setAreaUpdatedFlag( false );

    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );

    // CtiCCSpecial specific data members

// Test and document our object copying behavior via replicate

    boost::scoped_ptr<CtiCCSpecial>     newArea( areas[ 2 ].replicate() );

    // Our new object is identical to the one it was replicated from.

    BOOST_CHECK_EQUAL(                        2, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoClass() );
    BOOST_CHECK_EQUAL(                "Area 51", newArea->getPaoName() );
    BOOST_CHECK_EQUAL(          "CCSPECIALAREA", newArea->getPaoType() );
    BOOST_CHECK_EQUAL(             "Top Secret", newArea->getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, newArea->getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, newArea->getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(                        1, newArea->getPointIds()->size() );
    BOOST_CHECK_EQUAL(                        2, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, newArea->getConfirmationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getStrategyId() );
    BOOST_CHECK_EQUAL(                      551, newArea->getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, newArea->getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, newArea->getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, newArea->isDirty() );
    BOOST_CHECK_EQUAL(                     true, newArea->isSpecial() );
    BOOST_CHECK_EQUAL(                    false, newArea->getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getEstPFactor() );
    BOOST_CHECK_EQUAL(                        0, newArea->getSubstationIds().size() );

    // Mess with it -- make sure it is independent from its source

    newArea->setPaoName( "Mongo Fett" );
    newArea->setPaoDescription( "Failed Clone" );
    newArea->setDisableFlag( true );
    newArea->setDisabledStatePointId( 1234 );
    newArea->setVoltReductionControlPointId( 2345 );
    newArea->setOvUvDisabledFlag( true );   // sets 'dirty' and 'updated' as well
    newArea->setPFactor( 0.5 );
    newArea->setEstPFactor( 0.25 );

    // validate our changes

    BOOST_CHECK_EQUAL(                        2, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", newArea->getPaoClass() );
    BOOST_CHECK_EQUAL(             "Mongo Fett", newArea->getPaoName() );
    BOOST_CHECK_EQUAL(          "CCSPECIALAREA", newArea->getPaoType() );
    BOOST_CHECK_EQUAL(           "Failed Clone", newArea->getPaoDescription() );
    BOOST_CHECK_EQUAL(                     true, newArea->getDisableFlag() );
    BOOST_CHECK_EQUAL(                     1234, newArea->getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(                        1, newArea->getPointIds()->size() );
    BOOST_CHECK_EQUAL(                        2, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, newArea->getConfirmationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       -1, newArea->getStrategyId() );
    BOOST_CHECK_EQUAL(                     2345, newArea->getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, newArea->getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                     true, newArea->getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                     true, newArea->isDirty() );
    BOOST_CHECK_EQUAL(                     true, newArea->isSpecial() );
    BOOST_CHECK_EQUAL(                     true, newArea->getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                      0.5, newArea->getPFactor() );
    BOOST_CHECK_EQUAL(                     0.25, newArea->getEstPFactor() );
    BOOST_CHECK_EQUAL(                        0, newArea->getSubstationIds().size() );

    // Verify our original object we replicated from is unchanged

    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoCategory() );
    BOOST_CHECK_EQUAL(             "CAPCONTROL", areas[  2 ].getPaoClass() );
    BOOST_CHECK_EQUAL(                "Area 51", areas[  2 ].getPaoName() );
    BOOST_CHECK_EQUAL(          "CCSPECIALAREA", areas[  2 ].getPaoType() );
    BOOST_CHECK_EQUAL(             "Top Secret", areas[  2 ].getPaoDescription() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getDisableFlag() );
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(                        1, areas[  2 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getConfirmationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getStrategyId() );
    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getEstPFactor() );
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getSubstationIds().size() );

// Test operator== and operator!=

    BOOST_CHECK(     *newArea == areas[ 2 ] );
    BOOST_CHECK( ! ( *newArea != areas[ 2 ] ) );

    BOOST_CHECK_EQUAL(  2, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(  2, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  2, newArea->getConfirmationStats().getPAOId() );

    BOOST_CHECK_EQUAL(  2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getConfirmationStats().getPAOId() );

    newArea->setPaoId( 4 );

    BOOST_CHECK( ! ( *newArea == areas[ 2 ] ) );
    BOOST_CHECK(     *newArea != areas[ 2 ] );

    BOOST_CHECK_EQUAL(  4, newArea->getPaoId() );
    BOOST_CHECK_EQUAL(  4, newArea->getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  4, newArea->getConfirmationStats().getPAOId() );

    BOOST_CHECK_EQUAL(  2, areas[  2 ].getPaoId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(  2, areas[  2 ].getConfirmationStats().getPAOId() );
}

BOOST_AUTO_TEST_CASE( test_ccArea_point_assignment )
{
    boost::ptr_map< long, CtiCCArea >    areas;

    {   // Core area object initialization

        using CCAreaRow     = Cti::Test::StringRow<10>;
        using CCAreaReader  = Cti::Test::TestReader<CCAreaRow>;

        CCAreaRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "VoltReductionPointID",
            "additionalflags",
            "ControlValue"
        };

        std::vector<CCAreaRow> rowVec
        {
            {
                "2",
                "CAPCONTROL",
                "CAPCONTROL",
                "Area 51",
                "CCAREA",
                "Top Secret",
                "N",
                "551",
                CCAreaReader::getNullString(),
                CCAreaReader::getNullString()
            },
        };

        CCAreaReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            areas.insert( paoID, new CtiCCArea( reader, nullptr ) );
        }
    }

    // Validate the attached point IDs

    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(    1, areas[ 2 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(  551, areas[ 2 ].getVoltReductionControlPointId() );

    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getUserDefOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getDailyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getWeeklyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getMonthlyOpSuccessPercentId() );

    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getUserDefCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getDailyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getWeeklyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getMonthlyCommSuccessPercentId() );

    // Validate the contents of the point ID collection
    {
        std::vector<long>   expected
        {
            551
        };

        BOOST_CHECK_EQUAL_RANGES( expected, *areas[ 2 ].getPointIds() );
    }

    // Validate the points we register for
    {
        std::vector<long>   expected
        {
            551
        };

        std::set<long>  registeredPoints;

        areas[ 2 ].getPointRegistrationIds( registeredPoints );

        BOOST_CHECK_EQUAL_RANGES( expected, registeredPoints );
    }

    {   // Point initialization

        using CCAreaPointRow     = Cti::Test::StringRow<3>;
        using CCAreaPointReader  = Cti::Test::TestReader<CCAreaPointRow>;

        CCAreaPointRow columnNames =
        {
            "POINTID",
            "POINTTYPE",
            "POINTOFFSET"
        };

        std::vector<CCAreaPointRow> rowVec
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

        CCAreaPointReader reader( columnNames, rowVec );

        while ( reader() )
        {
            areas[ 2 ].assignPoint( reader );
        }
    }

    // Validate the attached point IDs

    BOOST_CHECK_EQUAL( 1000, areas[ 2 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(   10, areas[ 2 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(  551, areas[ 2 ].getVoltReductionControlPointId() );

    BOOST_CHECK_EQUAL(  690, areas[ 2 ].getOperationStats().getUserDefOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  671, areas[ 2 ].getOperationStats().getDailyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  681, areas[ 2 ].getOperationStats().getWeeklyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  683, areas[ 2 ].getOperationStats().getMonthlyOpSuccessPercentId() );

    BOOST_CHECK_EQUAL(  691, areas[ 2 ].getConfirmationStats().getUserDefCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  697, areas[ 2 ].getConfirmationStats().getDailyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  695, areas[ 2 ].getConfirmationStats().getWeeklyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  693, areas[ 2 ].getConfirmationStats().getMonthlyCommSuccessPercentId() );

    // Validate the contents of the point ID collection
    {
        std::vector<long>   expected
        {
            551, 671, 681, 683, 690, 691, 693, 695, 697, 1000
        };

        BOOST_CHECK_EQUAL_RANGES( expected, *areas[ 2 ].getPointIds() );
    }

    // Validate the points we register for
    {
        std::vector<long>   expected
        {
            551, 671, 681, 683, 690, 1000
        };

        std::set<long>  registeredPoints;

        areas[ 2 ].getPointRegistrationIds( registeredPoints );

        BOOST_CHECK_EQUAL_RANGES( expected, registeredPoints );
    }
}

BOOST_AUTO_TEST_CASE( test_ccSpecialArea_point_assignment )
{
    boost::ptr_map< long, CtiCCSpecial >    areas;

    {   // Core area object initialization

        using CCAreaRow     = Cti::Test::StringRow<10>;
        using CCAreaReader  = Cti::Test::TestReader<CCAreaRow>;

        CCAreaRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "VoltReductionPointID",
            "additionalflags",
            "ControlValue"
        };

        std::vector<CCAreaRow> rowVec
        {
            {
                "2",
                "CAPCONTROL",
                "CAPCONTROL",
                "Area 51",
                "CCSPECIALAREA",
                "Top Secret",
                "N",
                "551",
                CCAreaReader::getNullString(),
                CCAreaReader::getNullString()
            },
        };

        CCAreaReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            areas.insert( paoID, new CtiCCSpecial( reader, nullptr ) );
        }
    }

    // Validate the attached point IDs

    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(    1, areas[ 2 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(  551, areas[ 2 ].getVoltReductionControlPointId() );

    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getUserDefOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getDailyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getWeeklyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getOperationStats().getMonthlyOpSuccessPercentId() );

    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getUserDefCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getDailyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getWeeklyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(    0, areas[ 2 ].getConfirmationStats().getMonthlyCommSuccessPercentId() );

    // Validate the contents of the point ID collection
    {
        std::vector<long>   expected
        {
            551
        };

        BOOST_CHECK_EQUAL_RANGES( expected, *areas[ 2 ].getPointIds() );
    }

    // Validate the points we register for
    {
        std::vector<long>   expected
        {
            551
        };

        std::set<long>  registeredPoints;

        areas[ 2 ].getPointRegistrationIds( registeredPoints );

        BOOST_CHECK_EQUAL_RANGES( expected, registeredPoints );
    }

    {   // Point initialization

        using CCAreaPointRow     = Cti::Test::StringRow<3>;
        using CCAreaPointReader  = Cti::Test::TestReader<CCAreaPointRow>;

        CCAreaPointRow columnNames =
        {
            "POINTID",
            "POINTTYPE",
            "POINTOFFSET"
        };

        std::vector<CCAreaPointRow> rowVec
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

        CCAreaPointReader reader( columnNames, rowVec );

        while ( reader() )
        {
            areas[ 2 ].assignPoint( reader );
        }
    }

    // Validate the attached point IDs

    BOOST_CHECK_EQUAL( 1000, areas[ 2 ].getDisabledStatePointId() );
    BOOST_CHECK_EQUAL(   10, areas[ 2 ].getPointIds()->size() );
    BOOST_CHECK_EQUAL(  551, areas[ 2 ].getVoltReductionControlPointId() );

    BOOST_CHECK_EQUAL(  690, areas[ 2 ].getOperationStats().getUserDefOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  671, areas[ 2 ].getOperationStats().getDailyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  681, areas[ 2 ].getOperationStats().getWeeklyOpSuccessPercentId() );
    BOOST_CHECK_EQUAL(  683, areas[ 2 ].getOperationStats().getMonthlyOpSuccessPercentId() );

    BOOST_CHECK_EQUAL(  691, areas[ 2 ].getConfirmationStats().getUserDefCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  697, areas[ 2 ].getConfirmationStats().getDailyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  695, areas[ 2 ].getConfirmationStats().getWeeklyCommSuccessPercentId() );
    BOOST_CHECK_EQUAL(  693, areas[ 2 ].getConfirmationStats().getMonthlyCommSuccessPercentId() );

    // Validate the contents of the point ID collection
    {
        std::vector<long>   expected
        {
            551, 671, 681, 683, 690, 691, 693, 695, 697, 1000
        };

        BOOST_CHECK_EQUAL_RANGES( expected, *areas[ 2 ].getPointIds() );
    }

    // Validate the points we register for
    {
        std::vector<long>   expected
        {
            551, 671, 681, 683, 690, 1000
        };

        std::set<long>  registeredPoints;

        areas[ 2 ].getPointRegistrationIds( registeredPoints );

        BOOST_CHECK_EQUAL_RANGES( expected, registeredPoints );
    }
}

BOOST_AUTO_TEST_SUITE_END()

