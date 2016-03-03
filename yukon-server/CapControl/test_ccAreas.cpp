#include <boost/test/unit_test.hpp>

#include <boost/ptr_container/ptr_map.hpp>

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
                                                     
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getPointIds()->size() );
                                                     
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getConfirmationStats().getPAOId() );
                                                     
    // Controllable                                  
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getStrategyId() );
                                                     
    // CtiCCAreaBase                                 
    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );  // no dynamic data -- default = false
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );           // no dynamic data -- default = false
    BOOST_CHECK_EQUAL(                       "", areas[  2 ].getAdditionalFlags() );
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
    BOOST_CHECK_EQUAL(                       "", areas[ 22 ].getAdditionalFlags() );
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
    BOOST_CHECK_EQUAL(   "ynynnnnnnnnnnnnnnnnn", areas[ 42 ].getAdditionalFlags() );
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
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getConfirmationStats().getPAOId() );
                          
    // Controllable       
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getStrategyId() );
                          
    // CtiCCAreaBase      
    BOOST_CHECK_EQUAL(                      651, areas[ 52 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getVoltReductionControlValue() );  // dynamic data == 1 with pointID > 0 -- true
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(   "nynynnnnnnnnnnnnnnnn", areas[ 52 ].getAdditionalFlags() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].isDirty() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].isSpecial() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getAreaUpdatedFlag() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getSubstationIds().size() );
                          
    // CtiCCArea          
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getReEnableAreaFlag() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getChildVoltReductionFlag() );
}

BOOST_AUTO_TEST_CASE( test_ccSpecialArea_construction )
{
/*
    Bugs or general weirdness
        Objects are marked as 'dirty' on completion if they don't have dynamic data.
        We aren't serializing the areaUpdated flag like we do for CtiCCArea
*/
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
                                                     
    BOOST_CHECK_EQUAL(                        0, areas[  2 ].getPointIds()->size() );
                                                     
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                        2, areas[  2 ].getConfirmationStats().getPAOId() );
                                                     
    // Controllable                                  
    BOOST_CHECK_EQUAL(                       -1, areas[  2 ].getStrategyId() );
                                                     
    // CtiCCAreaBase                                 
    BOOST_CHECK_EQUAL(                      551, areas[  2 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[  2 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(                       "", areas[  2 ].getAdditionalFlags() );
    BOOST_CHECK_EQUAL(                     true, areas[  2 ].isDirty() );               // why true? shouldn't be - we were just created
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
    BOOST_CHECK_EQUAL(                       "", areas[ 22 ].getAdditionalFlags() );
    BOOST_CHECK_EQUAL(                     true, areas[ 22 ].isDirty() );               // why true? shouldn't be - we were just created
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
    BOOST_CHECK_EQUAL(   "ynynnnnnnnnnnnnnnnnn", areas[ 42 ].getAdditionalFlags() );
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
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getPointIds()->size() );
                          
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getOperationStats().getPAOId() );
    BOOST_CHECK_EQUAL(                       52, areas[ 52 ].getConfirmationStats().getPAOId() );
                          
    // Controllable       
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getStrategyId() );
                          
    // CtiCCAreaBase      
    BOOST_CHECK_EQUAL(                      651, areas[ 52 ].getVoltReductionControlPointId() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].getVoltReductionControlValue() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(   "nynynnnnnnnnnnnnnnnn", areas[ 52 ].getAdditionalFlags() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].isDirty() );
    BOOST_CHECK_EQUAL(                     true, areas[ 52 ].isSpecial() );
    BOOST_CHECK_EQUAL(                    false, areas[ 52 ].getAreaUpdatedFlag() );    // additionalflags[ 3 ] == 'y' is unserialized -- false
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getPFactor() );
    BOOST_CHECK_EQUAL(                       -1, areas[ 52 ].getEstPFactor() );
                          
    BOOST_CHECK_EQUAL(                        0, areas[ 52 ].getSubstationIds().size() );
                          
    // CtiCCSpecial                                         
}

BOOST_AUTO_TEST_SUITE_END()

