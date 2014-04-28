#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "std_helper.h"
#include "boost_test_helpers.h"
#include "cmd_rfn_ChannelConfiguration.h"

using boost::assign::list_of;

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnChannelConfigurationCommand;
using Cti::Devices::Commands::RfnSetChannelSelectionCommand;
using Cti::Devices::Commands::RfnGetChannelSelectionCommand;
using Cti::Devices::Commands::RfnGetChannelSelectionFullDescriptionCommand;
using Cti::Devices::Commands::RfnSetChannelIntervalRecordingCommand;
using Cti::Devices::Commands::RfnGetChannelIntervalRecordingCommand;

namespace boost      {
namespace test_tools {
// define in test_main
bool operator!=( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs );

bool operator==( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs )
{
    return !(lhs != rhs);
}

}
}

namespace std {

ostream & operator<<( ostream & os, const RfnChannelConfigurationCommand::MetricList& metrics )
{
    RfnChannelConfigurationCommand::MetricList::const_iterator itr = metrics.begin();

    if( itr != metrics.end() )
    {
        os << *(itr++);
        while( itr != metrics.end() )
        {
            os << ", " << *(itr++);
        }
    }
    return os;
}

ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex );

}

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_ChannelConfiguration )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

const RfnChannelConfigurationCommand::MetricList allMetrics = list_of
        ( "WattHourDel"              )
        ( "WattHourRec"              )
        ( "WattHourTotal"            )
        ( "WattHourNet"              )
        ( "WattsDelCurrentDemand"    )
        ( "WattsRecCurrentDemand"    )
        ( "WattsDelPeakDemand"       )
        ( "WattsRecPeakDemand"       )
        ( "WattsDelPeakDemandFrozen" )
        ( "WattsRecPeakDemandFrozen" )
        ( "WattHourDelFrozen"        )
        ( "WattHourRecFrozen"        )

        ( "VarHourDel"               )
        ( "VarHourRec"               )
        ( "VarHourTotal"             )
        ( "VarHourNet"               )
        ( "VarHourQ1"                )
        ( "VarHourQ2"                )
        ( "VarHourQ3"                )
        ( "VarHourQ4"                )
        ( "VarHourQ1+Q4"             )
        ( "VarHourQ2+Q3"             )
        ( "VarHourQ1-Q4"             )
        ( "VarDelCurrentDemand"      )
        ( "VarRecCurrentDemand"      )
        ( "VarDelPeakDemand"         )
        ( "VarRecPeakDemand"         )
        ( "VarDelPeakDemandCoinci"   )
        ( "VarRecPeakDemandCoinci"   )

        ( "VaHourDel"                )
        ( "VaHourRec"                )
        ( "VaHourTotal"              )
        ( "VaHourNet"                )
        ( "VaHourQ1"                 )
        ( "VaHourQ2"                 )
        ( "VaHourQ3"                 )
        ( "VaHourQ4"                 )
        ( "VaDelCurrentDemand"       )
        ( "VaRecCurrentDemand"       )
        ( "VaRecPeakDemand"          )
        ( "VaDelPeakDemand"          )

        ( "QHourDel"                 )
        ( "QHourRec"                 )
        ( "QHourTotal"               )
        ( "QHourNet"                 )
        ( "QDelCurrentDemand"        )
        ( "QRecCurrentDemand"        )
        ( "QDelPeakDemand"           )
        ( "QRecPeakDemand"           )
        ( "QDelPeakDemandCoinci"     )
        ( "QRecPeakDemandCoinci"     )

        ( "PfKWhDel"                 )
        ( "PfKWhRec"                 )
        ( "PfKWhTotal"               )

        ( "VoltagePhaseA"            )
        ( "VoltagePhaseB"            )
        ( "VoltagePhaseC"            )
        ( "CurrentPhaseA"            )
        ( "CurrentPhaseB"            )
        ( "CurrentPhaseC"            )
        ( "VoltageAnglePhaseA"       )
        ( "VoltageAnglePhaseB"       )
        ( "VoltageAnglePhaseC"       )
        ( "CurrentAnglePhaseA"       )
        ( "CurrentAnglePhaseB"       )
        ( "CurrentAnglePhaseC"       )
        ( "VoltageMin"               )
        ( "VoltageAvg"               )
        ( "VoltageMax"               )
        ( "Voltage"                  )
        ( "VoltageMinPhaseA"         )
        ( "VoltageMinPhaseB"         )
        ( "VoltageMinPhaseC"         )
        ( "VoltageAvgPhaseA"         )
        ( "VoltageAvgPhaseB"         )
        ( "VoltageAvgPhaseC"         )
        ( "VoltageMaxPhaseA"         )
        ( "VoltageMaxPhaseB"         )
        ( "VoltageMaxPhaseC"         )

        ( "WattsPhaseA"              )
        ( "WattsPhaseB"              )
        ( "WattsPhaseC"              )
        ( "VarPhaseA"                )
        ( "VarPhaseB"                )
        ( "VarPhaseC"                )
        ( "VaPhaseA"                 )
        ( "VaPhaseB"                 )
        ( "VaPhaseC"                 )
        ( "PfDegreePhaseA"           )
        ( "PfDegreePhaseB"           )
        ( "PfDegreePhaseC"           )
        ( "PfPhaseA"                 )
        ( "PfPhaseB"                 )
        ( "PfPhaseC"                 )

        ( "TimeSeconds"              )
        ( "TemperatureCentigrade"    );

BOOST_AUTO_TEST_CASE( test_RfnSetChannelSelectionCommand_allMetrics )
{
    RfnChannelConfigurationCommand::MetricList metrics = allMetrics;

    RfnSetChannelSelectionCommand cmd( metrics );

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        RfnCommand::RfnRequestPayload exp = list_of
                (0x78)(0x00)(0x01)  // command code + operation + 1 tlv
                (0x01)              // tlv type
                (0x00)(0xc1)        // tlv size (2-bytes)
                (0x60)              // number of metrics
                (0x00)(0x01)  (0x00)(0x02)  (0x00)(0x03)  (0x00)(0x04)
                (0x00)(0x05)  (0x00)(0x06)  (0x00)(0x07)  (0x00)(0x08)
                (0x00)(0x09)  (0x00)(0x0a)  (0x00)(0x0b)  (0x00)(0x0c)
                (0x00)(0x15)  (0x00)(0x16)  (0x00)(0x17)  (0x00)(0x18)
                (0x00)(0x19)  (0x00)(0x1a)  (0x00)(0x1b)  (0x00)(0x1c)
                (0x00)(0x1d)  (0x00)(0x1e)  (0x00)(0x1f)  (0x00)(0x20)
                (0x00)(0x21)  (0x00)(0x22)  (0x00)(0x23)  (0x00)(0x24)
                (0x00)(0x25)  (0x00)(0x29)  (0x00)(0x2a)  (0x00)(0x2b)
                (0x00)(0x2c)  (0x00)(0x2d)  (0x00)(0x2e)  (0x00)(0x2f)
                (0x00)(0x30)  (0x00)(0x31)  (0x00)(0x32)  (0x00)(0x33)
                (0x00)(0x34)  (0x00)(0x3d)  (0x00)(0x3e)  (0x00)(0x3f)
                (0x00)(0x40)  (0x00)(0x41)  (0x00)(0x42)  (0x00)(0x43)
                (0x00)(0x44)  (0x00)(0x45)  (0x00)(0x46)  (0x00)(0x51)
                (0x00)(0x52)  (0x00)(0x53)  (0x00)(0x64)  (0x00)(0x65)
                (0x00)(0x66)  (0x00)(0x67)  (0x00)(0x68)  (0x00)(0x69)
                (0x00)(0x6a)  (0x00)(0x6b)  (0x00)(0x6c)  (0x00)(0x6d)
                (0x00)(0x6e)  (0x00)(0x6f)  (0x00)(0x70)  (0x00)(0x71)
                (0x00)(0x72)  (0x00)(0x73)  (0x00)(0x74)  (0x00)(0x75)
                (0x00)(0x76)  (0x00)(0x77)  (0x00)(0x78)  (0x00)(0x79)
                (0x00)(0x7a)  (0x00)(0x7b)  (0x00)(0x7c)  (0x00)(0x96)
                (0x00)(0x97)  (0x00)(0x98)  (0x00)(0x99)  (0x00)(0x9a)
                (0x00)(0x9b)  (0x00)(0x9c)  (0x00)(0x9d)  (0x00)(0x9e)
                (0x00)(0x9f)  (0x00)(0xa0)  (0x00)(0xa1)  (0x00)(0xa2)
                (0x00)(0xa3)  (0x00)(0xa4)  (0x01)(0x00)  (0x01)(0x01);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const std::vector<unsigned char> response = list_of
                (0x79)(0x00)(0X00)(0x02)    // command code + operation + status + 2 tlv
                (0x01)                      // tlv type 1
                (0x00)(0xc1)                // tlv size
                (0x60)                      // number of metrics
                (0x00)(0x01)  (0x00)(0x02)  (0x00)(0x03)  (0x00)(0x04)
                (0x00)(0x05)  (0x00)(0x06)  (0x00)(0x07)  (0x00)(0x08)
                (0x00)(0x09)  (0x00)(0x0a)  (0x00)(0x0b)  (0x00)(0x0c)
                (0x00)(0x15)  (0x00)(0x16)  (0x00)(0x17)  (0x00)(0x18)
                (0x00)(0x19)  (0x00)(0x1a)  (0x00)(0x1b)  (0x00)(0x1c)
                (0x00)(0x1d)  (0x00)(0x1e)  (0x00)(0x1f)  (0x00)(0x20)
                (0x00)(0x21)  (0x00)(0x22)  (0x00)(0x23)  (0x00)(0x24)
                (0x00)(0x25)  (0x00)(0x29)  (0x00)(0x2a)  (0x00)(0x2b)
                (0x00)(0x2c)  (0x00)(0x2d)  (0x00)(0x2e)  (0x00)(0x2f)
                (0x00)(0x30)  (0x00)(0x31)  (0x00)(0x32)  (0x00)(0x33)
                (0x00)(0x34)  (0x00)(0x3d)  (0x00)(0x3e)  (0x00)(0x3f)
                (0x00)(0x40)  (0x00)(0x41)  (0x00)(0x42)  (0x00)(0x43)
                (0x00)(0x44)  (0x00)(0x45)  (0x00)(0x46)  (0x00)(0x51)
                (0x00)(0x52)  (0x00)(0x53)  (0x00)(0x64)  (0x00)(0x65)
                (0x00)(0x66)  (0x00)(0x67)  (0x00)(0x68)  (0x00)(0x69)
                (0x00)(0x6a)  (0x00)(0x6b)  (0x00)(0x6c)  (0x00)(0x6d)
                (0x00)(0x6e)  (0x00)(0x6f)  (0x00)(0x70)  (0x00)(0x71)
                (0x00)(0x72)  (0x00)(0x73)  (0x00)(0x74)  (0x00)(0x75)
                (0x00)(0x76)  (0x00)(0x77)  (0x00)(0x78)  (0x00)(0x79)
                (0x00)(0x7a)  (0x00)(0x7b)  (0x00)(0x7c)  (0x00)(0x96)
                (0x00)(0x97)  (0x00)(0x98)  (0x00)(0x99)  (0x00)(0x9a)
                (0x00)(0x9b)  (0x00)(0x9c)  (0x00)(0x9d)  (0x00)(0x9e)
                (0x00)(0x9f)  (0x00)(0xa0)  (0x00)(0xa1)  (0x00)(0xa2)
                (0x00)(0xa3)  (0x00)(0xa4)  (0x01)(0x00)  (0x01)(0x01)
                (0x02)                      // tlv type 2
                (0x01)(0x81)                // tlv size (2-bytes)
                (0x60)                      // number of metrics descriptor
                (0x00)(0x01)(0x00)(0x00)  (0x00)(0x02)(0x00)(0x00)  (0x00)(0x03)(0x00)(0x00)  (0x00)(0x04)(0x00)(0x00)
                (0x00)(0x05)(0x00)(0x00)  (0x00)(0x06)(0x00)(0x00)  (0x00)(0x07)(0x00)(0x00)  (0x00)(0x08)(0x00)(0x00)
                (0x00)(0x09)(0x00)(0x00)  (0x00)(0x0a)(0x00)(0x00)  (0x00)(0x0b)(0x00)(0x00)  (0x00)(0x0c)(0x00)(0x00)
                (0x00)(0x15)(0x00)(0x00)  (0x00)(0x16)(0x00)(0x00)  (0x00)(0x17)(0x00)(0x00)  (0x00)(0x18)(0x00)(0x00)
                (0x00)(0x19)(0x00)(0x00)  (0x00)(0x1a)(0x00)(0x00)  (0x00)(0x1b)(0x00)(0x00)  (0x00)(0x1c)(0x00)(0x00)
                (0x00)(0x1d)(0x00)(0x00)  (0x00)(0x1e)(0x00)(0x00)  (0x00)(0x1f)(0x00)(0x00)  (0x00)(0x20)(0x00)(0x00)
                (0x00)(0x21)(0x00)(0x00)  (0x00)(0x22)(0x00)(0x00)  (0x00)(0x23)(0x00)(0x00)  (0x00)(0x24)(0x00)(0x00)
                (0x00)(0x25)(0x00)(0x00)  (0x00)(0x29)(0x00)(0x00)  (0x00)(0x2a)(0x00)(0x00)  (0x00)(0x2b)(0x00)(0x00)
                (0x00)(0x2c)(0x00)(0x00)  (0x00)(0x2d)(0x00)(0x00)  (0x00)(0x2e)(0x00)(0x00)  (0x00)(0x2f)(0x00)(0x00)
                (0x00)(0x30)(0x00)(0x00)  (0x00)(0x31)(0x00)(0x00)  (0x00)(0x32)(0x00)(0x00)  (0x00)(0x33)(0x00)(0x00)
                (0x00)(0x34)(0x00)(0x00)  (0x00)(0x3d)(0x00)(0x00)  (0x00)(0x3e)(0x00)(0x00)  (0x00)(0x3f)(0x00)(0x00)
                (0x00)(0x40)(0x00)(0x00)  (0x00)(0x41)(0x00)(0x00)  (0x00)(0x42)(0x00)(0x00)  (0x00)(0x43)(0x00)(0x00)
                (0x00)(0x44)(0x00)(0x00)  (0x00)(0x45)(0x00)(0x00)  (0x00)(0x46)(0x00)(0x00)  (0x00)(0x51)(0x00)(0x00)
                (0x00)(0x52)(0x00)(0x00)  (0x00)(0x53)(0x00)(0x00)  (0x00)(0x64)(0x00)(0x00)  (0x00)(0x65)(0x00)(0x00)
                (0x00)(0x66)(0x00)(0x00)  (0x00)(0x67)(0x00)(0x00)  (0x00)(0x68)(0x00)(0x00)  (0x00)(0x69)(0x00)(0x00)
                (0x00)(0x6a)(0x00)(0x00)  (0x00)(0x6b)(0x00)(0x00)  (0x00)(0x6c)(0x00)(0x00)  (0x00)(0x6d)(0x00)(0x00)
                (0x00)(0x6e)(0x00)(0x00)  (0x00)(0x6f)(0x00)(0x00)  (0x00)(0x70)(0x00)(0x00)  (0x00)(0x71)(0x00)(0x00)
                (0x00)(0x72)(0x00)(0x00)  (0x00)(0x73)(0x00)(0x00)  (0x00)(0x74)(0x00)(0x00)  (0x00)(0x75)(0x00)(0x00)
                (0x00)(0x76)(0x00)(0x00)  (0x00)(0x77)(0x00)(0x00)  (0x00)(0x78)(0x00)(0x00)  (0x00)(0x79)(0x00)(0x00)
                (0x00)(0x7a)(0x00)(0x00)  (0x00)(0x7b)(0x00)(0x00)  (0x00)(0x7c)(0x00)(0x00)  (0x00)(0x96)(0x00)(0x00)
                (0x00)(0x97)(0x00)(0x00)  (0x00)(0x98)(0x00)(0x00)  (0x00)(0x99)(0x00)(0x00)  (0x00)(0x9a)(0x00)(0x00)
                (0x00)(0x9b)(0x00)(0x00)  (0x00)(0x9c)(0x00)(0x00)  (0x00)(0x9d)(0x00)(0x00)  (0x00)(0x9e)(0x00)(0x00)
                (0x00)(0x9f)(0x00)(0x00)  (0x00)(0xa0)(0x00)(0x00)  (0x00)(0xa1)(0x00)(0x00)  (0x00)(0xa2)(0x00)(0x00)
                (0x00)(0xa3)(0x00)(0x00)  (0x00)(0xa4)(0x00)(0x00)  (0x01)(0x00)(0x00)(0x00)  (0x01)(0x01)(0x00)(0x00);

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Selection Configuration:\n"
                "Metric(s) list:\n"
                "Watt hour delivered (1)\n"
                "Watt hour received (2)\n"
                "Watt hour total/sum (3)\n"
                "Watt hour net (4)\n"
                "Watts delivered, current demand (5)\n"
                "Watts received, current demand (6)\n"
                "Watts delivered, peak demand (7)\n"
                "Watts received, peak demand (8)\n"
                "Watts delivered, peak demand (Frozen) (9)\n"
                "Watts received, peak demand (Frozen) (10)\n"
                "Watt hour delivered (Frozen) (11)\n"
                "Watt hour received (Frozen) (12)\n"
                "Var hour delivered (21)\n"
                "Var hour received (22)\n"
                "Var hour total/sum (23)\n"
                "Var hour net (24)\n"
                "Var hour Q1 (25)\n"
                "Var hour Q2 (26)\n"
                "Var hour Q3 (27)\n"
                "Var hour Q4 (28)\n"
                "Var hour Q1 + Q4 (29)\n"
                "Var hour Q2 + Q3 (30)\n"
                "Var hour Q1 - Q4 (31)\n"
                "Var delivered, current demand (32)\n"
                "Var received, current demand (33)\n"
                "Var delivered, peak demand (34)\n"
                "Var received, peak demand (35)\n"
                "Var delivered, peak demand coincident (36)\n"
                "Var received, peak demand coincident (37)\n"
                "VA hour delivered (41)\n"
                "VA hour received (42)\n"
                "VA hour total/sum (43)\n"
                "VA hour net (44)\n"
                "VA hour Q1 (45)\n"
                "VA hour Q2 (46)\n"
                "VA hour Q3 (47)\n"
                "VA hour Q4 (48)\n"
                "VA delivered, current demand (49)\n"
                "VA received, current demand (50)\n"
                "VA received, peak demand (51)\n"
                "VA delivered, peak demand (52)\n"
                "Q hour delivered (61)\n"
                "Q hour received (62)\n"
                "Q hour total/sum (63)\n"
                "Q hour net (64)\n"
                "Q delivered, current demand (65)\n"
                "Q received, current demand (66)\n"
                "Q delivered, peak demand (67)\n"
                "Q received, peak demand (68)\n"
                "Q delivered, peak demand coincident (69)\n"
                "Q received, peak demand coincident (70)\n"
                "Power Factor kWh(del)/kVar(del) (81)\n"
                "Power Factor kWh(rec)/kVar(rec) (82)\n"
                "Power Factor kWh(total)/kVar(total) (83)\n"
                "Voltage Phase A (100)\n"
                "Voltage Phase B (101)\n"
                "Voltage Phase C (102)\n"
                "Current Phase A (103)\n"
                "Current Phase B (104)\n"
                "Current Phase C (105)\n"
                "Voltage Angle Phase A (106)\n"
                "Voltage Angle Phase B (107)\n"
                "Voltage Angle Phase C (108)\n"
                "Current Angle Phase A (109)\n"
                "Current Angle Phase B (110)\n"
                "Current Angle Phase C (111)\n"
                "Voltage Min (112)\n"
                "Voltage Average (113)\n"
                "Voltage Max (114)\n"
                "Voltage (115)\n"
                "Voltage Min Phase A (116)\n"
                "Voltage Min Phase B (117)\n"
                "Voltage Min Phase C (118)\n"
                "Voltage Average Phase A (119)\n"
                "Voltage Average Phase B (120)\n"
                "Voltage Average Phase C (121)\n"
                "Voltage Max Phase A (122)\n"
                "Voltage Max Phase B (123)\n"
                "Voltage Max Phase C (124)\n"
                "Watts Phase A (150)\n"
                "Watts Phase B (151)\n"
                "Watts Phase C (152)\n"
                "Var Phase A (153)\n"
                "Var Phase B (154)\n"
                "Var Phase C (155)\n"
                "VA Phase A (156)\n"
                "VA Phase B (157)\n"
                "VA Phase C (158)\n"
                "PF degree Phase A (159)\n"
                "PF degree Phase A (160)\n"
                "PF degree Phase C (161)\n"
                "PF Phase A (162)\n"
                "PF Phase B (163)\n"
                "PF Phase C (164)\n"
                "Time in Seconds (256)\n"
                "Temperature in Centigrade (257)\n"
                "Channel Registration Full Description:\n"
                "Metric(s) descriptors:\n"
                "Watt hour delivered (1): Scaling Factor: 1\n"
                "Watt hour received (2): Scaling Factor: 1\n"
                "Watt hour total/sum (3): Scaling Factor: 1\n"
                "Watt hour net (4): Scaling Factor: 1\n"
                "Watts delivered, current demand (5): Scaling Factor: 1\n"
                "Watts received, current demand (6): Scaling Factor: 1\n"
                "Watts delivered, peak demand (7): Scaling Factor: 1\n"
                "Watts received, peak demand (8): Scaling Factor: 1\n"
                "Watts delivered, peak demand (Frozen) (9): Scaling Factor: 1\n"
                "Watts received, peak demand (Frozen) (10): Scaling Factor: 1\n"
                "Watt hour delivered (Frozen) (11): Scaling Factor: 1\n"
                "Watt hour received (Frozen) (12): Scaling Factor: 1\n"
                "Var hour delivered (21): Scaling Factor: 1\n"
                "Var hour received (22): Scaling Factor: 1\n"
                "Var hour total/sum (23): Scaling Factor: 1\n"
                "Var hour net (24): Scaling Factor: 1\n"
                "Var hour Q1 (25): Scaling Factor: 1\n"
                "Var hour Q2 (26): Scaling Factor: 1\n"
                "Var hour Q3 (27): Scaling Factor: 1\n"
                "Var hour Q4 (28): Scaling Factor: 1\n"
                "Var hour Q1 + Q4 (29): Scaling Factor: 1\n"
                "Var hour Q2 + Q3 (30): Scaling Factor: 1\n"
                "Var hour Q1 - Q4 (31): Scaling Factor: 1\n"
                "Var delivered, current demand (32): Scaling Factor: 1\n"
                "Var received, current demand (33): Scaling Factor: 1\n"
                "Var delivered, peak demand (34): Scaling Factor: 1\n"
                "Var received, peak demand (35): Scaling Factor: 1\n"
                "Var delivered, peak demand coincident (36): Scaling Factor: 1\n"
                "Var received, peak demand coincident (37): Scaling Factor: 1\n"
                "VA hour delivered (41): Scaling Factor: 1\n"
                "VA hour received (42): Scaling Factor: 1\n"
                "VA hour total/sum (43): Scaling Factor: 1\n"
                "VA hour net (44): Scaling Factor: 1\n"
                "VA hour Q1 (45): Scaling Factor: 1\n"
                "VA hour Q2 (46): Scaling Factor: 1\n"
                "VA hour Q3 (47): Scaling Factor: 1\n"
                "VA hour Q4 (48): Scaling Factor: 1\n"
                "VA delivered, current demand (49): Scaling Factor: 1\n"
                "VA received, current demand (50): Scaling Factor: 1\n"
                "VA received, peak demand (51): Scaling Factor: 1\n"
                "VA delivered, peak demand (52): Scaling Factor: 1\n"
                "Q hour delivered (61): Scaling Factor: 1\n"
                "Q hour received (62): Scaling Factor: 1\n"
                "Q hour total/sum (63): Scaling Factor: 1\n"
                "Q hour net (64): Scaling Factor: 1\n"
                "Q delivered, current demand (65): Scaling Factor: 1\n"
                "Q received, current demand (66): Scaling Factor: 1\n"
                "Q delivered, peak demand (67): Scaling Factor: 1\n"
                "Q received, peak demand (68): Scaling Factor: 1\n"
                "Q delivered, peak demand coincident (69): Scaling Factor: 1\n"
                "Q received, peak demand coincident (70): Scaling Factor: 1\n"
                "Power Factor kWh(del)/kVar(del) (81): Scaling Factor: 1\n"
                "Power Factor kWh(rec)/kVar(rec) (82): Scaling Factor: 1\n"
                "Power Factor kWh(total)/kVar(total) (83): Scaling Factor: 1\n"
                "Voltage Phase A (100): Scaling Factor: 1\n"
                "Voltage Phase B (101): Scaling Factor: 1\n"
                "Voltage Phase C (102): Scaling Factor: 1\n"
                "Current Phase A (103): Scaling Factor: 1\n"
                "Current Phase B (104): Scaling Factor: 1\n"
                "Current Phase C (105): Scaling Factor: 1\n"
                "Voltage Angle Phase A (106): Scaling Factor: 1\n"
                "Voltage Angle Phase B (107): Scaling Factor: 1\n"
                "Voltage Angle Phase C (108): Scaling Factor: 1\n"
                "Current Angle Phase A (109): Scaling Factor: 1\n"
                "Current Angle Phase B (110): Scaling Factor: 1\n"
                "Current Angle Phase C (111): Scaling Factor: 1\n"
                "Voltage Min (112): Scaling Factor: 1\n"
                "Voltage Average (113): Scaling Factor: 1\n"
                "Voltage Max (114): Scaling Factor: 1\n"
                "Voltage (115): Scaling Factor: 1\n"
                "Voltage Min Phase A (116): Scaling Factor: 1\n"
                "Voltage Min Phase B (117): Scaling Factor: 1\n"
                "Voltage Min Phase C (118): Scaling Factor: 1\n"
                "Voltage Average Phase A (119): Scaling Factor: 1\n"
                "Voltage Average Phase B (120): Scaling Factor: 1\n"
                "Voltage Average Phase C (121): Scaling Factor: 1\n"
                "Voltage Max Phase A (122): Scaling Factor: 1\n"
                "Voltage Max Phase B (123): Scaling Factor: 1\n"
                "Voltage Max Phase C (124): Scaling Factor: 1\n"
                "Watts Phase A (150): Scaling Factor: 1\n"
                "Watts Phase B (151): Scaling Factor: 1\n"
                "Watts Phase C (152): Scaling Factor: 1\n"
                "Var Phase A (153): Scaling Factor: 1\n"
                "Var Phase B (154): Scaling Factor: 1\n"
                "Var Phase C (155): Scaling Factor: 1\n"
                "VA Phase A (156): Scaling Factor: 1\n"
                "VA Phase B (157): Scaling Factor: 1\n"
                "VA Phase C (158): Scaling Factor: 1\n"
                "PF degree Phase A (159): Scaling Factor: 1\n"
                "PF degree Phase A (160): Scaling Factor: 1\n"
                "PF degree Phase C (161): Scaling Factor: 1\n"
                "PF Phase A (162): Scaling Factor: 1\n"
                "PF Phase B (163): Scaling Factor: 1\n"
                "PF Phase C (164): Scaling Factor: 1\n"
                "Time in Seconds (256): Scaling Factor: 1\n"
                "Temperature in Centigrade (257): Scaling Factor: 1\n";


        BOOST_CHECK_EQUAL( rcv.description, desc_exp );
        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), metrics );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnGetChannelSelectionCommand )
{
    RfnChannelConfigurationCommand::MetricList metrics = allMetrics;

    RfnGetChannelSelectionCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        RfnCommand::RfnRequestPayload exp = list_of
                (0x78)(0x01)(0x00);  // command code + operation + 0 tlv

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const std::vector<unsigned char> response = list_of
                (0x79)(0x01)(0X00)(0x01)    // command code + operation + status + 1 tlv
                (0x01)                      // tlv type 1
                (0x00)(0xc1)                // tlv size (2-bytes)
                (0x60)                      // number of metrics
                (0x00)(0x01)  (0x00)(0x02)  (0x00)(0x03)  (0x00)(0x04)
                (0x00)(0x05)  (0x00)(0x06)  (0x00)(0x07)  (0x00)(0x08)
                (0x00)(0x09)  (0x00)(0x0a)  (0x00)(0x0b)  (0x00)(0x0c)
                (0x00)(0x15)  (0x00)(0x16)  (0x00)(0x17)  (0x00)(0x18)
                (0x00)(0x19)  (0x00)(0x1a)  (0x00)(0x1b)  (0x00)(0x1c)
                (0x00)(0x1d)  (0x00)(0x1e)  (0x00)(0x1f)  (0x00)(0x20)
                (0x00)(0x21)  (0x00)(0x22)  (0x00)(0x23)  (0x00)(0x24)
                (0x00)(0x25)  (0x00)(0x29)  (0x00)(0x2a)  (0x00)(0x2b)
                (0x00)(0x2c)  (0x00)(0x2d)  (0x00)(0x2e)  (0x00)(0x2f)
                (0x00)(0x30)  (0x00)(0x31)  (0x00)(0x32)  (0x00)(0x33)
                (0x00)(0x34)  (0x00)(0x3d)  (0x00)(0x3e)  (0x00)(0x3f)
                (0x00)(0x40)  (0x00)(0x41)  (0x00)(0x42)  (0x00)(0x43)
                (0x00)(0x44)  (0x00)(0x45)  (0x00)(0x46)  (0x00)(0x51)
                (0x00)(0x52)  (0x00)(0x53)  (0x00)(0x64)  (0x00)(0x65)
                (0x00)(0x66)  (0x00)(0x67)  (0x00)(0x68)  (0x00)(0x69)
                (0x00)(0x6a)  (0x00)(0x6b)  (0x00)(0x6c)  (0x00)(0x6d)
                (0x00)(0x6e)  (0x00)(0x6f)  (0x00)(0x70)  (0x00)(0x71)
                (0x00)(0x72)  (0x00)(0x73)  (0x00)(0x74)  (0x00)(0x75)
                (0x00)(0x76)  (0x00)(0x77)  (0x00)(0x78)  (0x00)(0x79)
                (0x00)(0x7a)  (0x00)(0x7b)  (0x00)(0x7c)  (0x00)(0x96)
                (0x00)(0x97)  (0x00)(0x98)  (0x00)(0x99)  (0x00)(0x9a)
                (0x00)(0x9b)  (0x00)(0x9c)  (0x00)(0x9d)  (0x00)(0x9e)
                (0x00)(0x9f)  (0x00)(0xa0)  (0x00)(0xa1)  (0x00)(0xa2)
                (0x00)(0xa3)  (0x00)(0xa4)  (0x01)(0x00)  (0x01)(0x01);

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Selection Configuration:\n"
                "Metric(s) list:\n"
                "Watt hour delivered (1)\n"
                "Watt hour received (2)\n"
                "Watt hour total/sum (3)\n"
                "Watt hour net (4)\n"
                "Watts delivered, current demand (5)\n"
                "Watts received, current demand (6)\n"
                "Watts delivered, peak demand (7)\n"
                "Watts received, peak demand (8)\n"
                "Watts delivered, peak demand (Frozen) (9)\n"
                "Watts received, peak demand (Frozen) (10)\n"
                "Watt hour delivered (Frozen) (11)\n"
                "Watt hour received (Frozen) (12)\n"
                "Var hour delivered (21)\n"
                "Var hour received (22)\n"
                "Var hour total/sum (23)\n"
                "Var hour net (24)\n"
                "Var hour Q1 (25)\n"
                "Var hour Q2 (26)\n"
                "Var hour Q3 (27)\n"
                "Var hour Q4 (28)\n"
                "Var hour Q1 + Q4 (29)\n"
                "Var hour Q2 + Q3 (30)\n"
                "Var hour Q1 - Q4 (31)\n"
                "Var delivered, current demand (32)\n"
                "Var received, current demand (33)\n"
                "Var delivered, peak demand (34)\n"
                "Var received, peak demand (35)\n"
                "Var delivered, peak demand coincident (36)\n"
                "Var received, peak demand coincident (37)\n"
                "VA hour delivered (41)\n"
                "VA hour received (42)\n"
                "VA hour total/sum (43)\n"
                "VA hour net (44)\n"
                "VA hour Q1 (45)\n"
                "VA hour Q2 (46)\n"
                "VA hour Q3 (47)\n"
                "VA hour Q4 (48)\n"
                "VA delivered, current demand (49)\n"
                "VA received, current demand (50)\n"
                "VA received, peak demand (51)\n"
                "VA delivered, peak demand (52)\n"
                "Q hour delivered (61)\n"
                "Q hour received (62)\n"
                "Q hour total/sum (63)\n"
                "Q hour net (64)\n"
                "Q delivered, current demand (65)\n"
                "Q received, current demand (66)\n"
                "Q delivered, peak demand (67)\n"
                "Q received, peak demand (68)\n"
                "Q delivered, peak demand coincident (69)\n"
                "Q received, peak demand coincident (70)\n"
                "Power Factor kWh(del)/kVar(del) (81)\n"
                "Power Factor kWh(rec)/kVar(rec) (82)\n"
                "Power Factor kWh(total)/kVar(total) (83)\n"
                "Voltage Phase A (100)\n"
                "Voltage Phase B (101)\n"
                "Voltage Phase C (102)\n"
                "Current Phase A (103)\n"
                "Current Phase B (104)\n"
                "Current Phase C (105)\n"
                "Voltage Angle Phase A (106)\n"
                "Voltage Angle Phase B (107)\n"
                "Voltage Angle Phase C (108)\n"
                "Current Angle Phase A (109)\n"
                "Current Angle Phase B (110)\n"
                "Current Angle Phase C (111)\n"
                "Voltage Min (112)\n"
                "Voltage Average (113)\n"
                "Voltage Max (114)\n"
                "Voltage (115)\n"
                "Voltage Min Phase A (116)\n"
                "Voltage Min Phase B (117)\n"
                "Voltage Min Phase C (118)\n"
                "Voltage Average Phase A (119)\n"
                "Voltage Average Phase B (120)\n"
                "Voltage Average Phase C (121)\n"
                "Voltage Max Phase A (122)\n"
                "Voltage Max Phase B (123)\n"
                "Voltage Max Phase C (124)\n"
                "Watts Phase A (150)\n"
                "Watts Phase B (151)\n"
                "Watts Phase C (152)\n"
                "Var Phase A (153)\n"
                "Var Phase B (154)\n"
                "Var Phase C (155)\n"
                "VA Phase A (156)\n"
                "VA Phase B (157)\n"
                "VA Phase C (158)\n"
                "PF degree Phase A (159)\n"
                "PF degree Phase A (160)\n"
                "PF degree Phase C (161)\n"
                "PF Phase A (162)\n"
                "PF Phase B (163)\n"
                "PF Phase C (164)\n"
                "Time in Seconds (256)\n"
                "Temperature in Centigrade (257)\n";

        BOOST_CHECK_EQUAL( rcv.description, desc_exp );
        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), metrics );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnGetChannelSelectionFullDescriptionCommand )
{
    RfnChannelConfigurationCommand::MetricList zeroMetrics;

    RfnGetChannelSelectionFullDescriptionCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        //debugPrint( rcv );

        RfnCommand::RfnRequestPayload exp = list_of
                (0x78)(0x02)(0x00);  // command code + operation + 1 tlv

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const std::vector<unsigned char> response = list_of
                (0x79)(0x02)(0X00)(0x02)  // command code + operation + status + 2 tlv
                (0x01)                    // tlv type 1
                (0x00)(0x01)              // tlv size (2-bytes)
                (0x00)                    // number of metrics
                (0x02)                    // tlv type 2
                (0x00)(0x71)              // tlv size (2-bytes)
                (0x1c)                    // number of metrics descriptor
                (0x00)(0x01)(0x00)(0x00)  // nothing specified
                (0x00)(0x02)(0x20)(0x00)  // Fund/Harmonic - Fundamental
                (0x00)(0x03)(0x40)(0x00)  // Fund/Harmonic - Harmonic
                (0x00)(0x05)(0x08)(0x00)  // Primary/Secondary - Primary
                (0x00)(0x06)(0x10)(0x00)  // Primary/Secondary - Secondary
                (0x00)(0x07)(0x01)(0x00)  // Segmentation - 1 - A to B
                (0x00)(0x08)(0x02)(0x00)  // Segmentation - 2 - B to C
                (0x00)(0x09)(0x03)(0x00)  // Segmentation - 3 - C to A
                (0x00)(0x0a)(0x04)(0x00)  // Segmentation - 4 - Neutral to Ground
                (0x00)(0x0b)(0x05)(0x00)  // Segmentation - 5 - A to Neutral
                (0x00)(0x0c)(0x06)(0x00)  // Segmentation - 6 - B to Neutral
                (0x00)(0x15)(0x07)(0x00)  // Segmentation - 7 - C to Neutral
                (0x00)(0x16)(0x00)(0x80)  // Continuous Cumulative
                (0x00)(0x17)(0x00)(0x40)  // Cumulative
                (0x00)(0x18)(0x00)(0x08)  // Coincident Value - 1
                (0x00)(0x19)(0x00)(0x10)  // Coincident Value - 2
                (0x00)(0x1a)(0x00)(0x18)  // Coincident Value - 3
                (0x00)(0x1b)(0x00)(0x20)  // Coincident Value - 4
                (0x00)(0x1c)(0x00)(0x28)  // Coincident Value - 5
                (0x00)(0x1d)(0x00)(0x30)  // Coincident Value - 6
                (0x00)(0x1e)(0x00)(0x38)  // Coincident Value - 7
                (0x00)(0x1f)(0x00)(0x03)  // Scaling Factor 10^9
                (0x00)(0x20)(0x00)(0x02)  // Scaling Factor 10^6
                (0x00)(0x21)(0x00)(0x01)  // Scaling Factor 10^3
                (0x00)(0x22)(0x00)(0x00)  // Scaling Factor 10^0
                (0x00)(0x23)(0x00)(0x07)  // Scaling Factor 10^-3
                (0x00)(0x29)(0x00)(0x06)  // Scaling Factor 10^-6
                (0x00)(0x2a)(0x00)(0x05); // Scaling Factor 1/10ths

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Selection Configuration:\n"
                "Metric(s) list:\n"
                "none\n"
                "Channel Registration Full Description:\n"
                "Metric(s) descriptors:\n"
                "Watt hour delivered (1): Scaling Factor: 1\n"
                "Watt hour received (2): Primary, Scaling Factor: 1\n"
                "Watt hour total/sum (3): Secondary, Scaling Factor: 1\n"
                "Watts delivered, current demand (5): Fundamental, Scaling Factor: 1\n"
                "Watts received, current demand (6): Harmonic, Scaling Factor: 1\n"
                "Watts delivered, peak demand (7): Segmentation: A to B, Scaling Factor: 1\n"
                "Watts received, peak demand (8): Segmentation: B to C, Scaling Factor: 1\n"
                "Watts delivered, peak demand (Frozen) (9): Segmentation: C to A, Scaling Factor: 1\n"
                "Watts received, peak demand (Frozen) (10): Segmentation: Neutral to Ground, Scaling Factor: 1\n"
                "Watt hour delivered (Frozen) (11): Segmentation: A to Neutral, Scaling Factor: 1\n"
                "Watt hour received (Frozen) (12): Segmentation: B to Neutral, Scaling Factor: 1\n"
                "Var hour delivered (21): Segmentation: C to Neutral, Scaling Factor: 1\n"
                "Var hour received (22): Continuous Cumulative, Scaling Factor: 1\n"
                "Var hour total/sum (23): Cumulative, Scaling Factor: 1\n"
                "Var hour net (24): Coincident Value 1, Scaling Factor: 1\n"
                "Var hour Q1 (25): Coincident Value 2, Scaling Factor: 1\n"
                "Var hour Q2 (26): Coincident Value 3, Scaling Factor: 1\n"
                "Var hour Q3 (27): Coincident Value 4, Scaling Factor: 1\n"
                "Var hour Q4 (28): Coincident Value 5, Scaling Factor: 1\n"
                "Var hour Q1 + Q4 (29): Coincident Value 6, Scaling Factor: 1\n"
                "Var hour Q2 + Q3 (30): Coincident Value 7, Scaling Factor: 1\n"
                "Var hour Q1 - Q4 (31): Scaling Factor: 10e9 (giga)\n"
                "Var delivered, current demand (32): Scaling Factor: 10e6 (mega)\n"
                "Var received, current demand (33): Scaling Factor: 10e3 (kilo)\n"
                "Var delivered, peak demand (34): Scaling Factor: 1\n"
                "Var received, peak demand (35): Scaling Factor: 10e-3 (milli)\n"
                "VA hour delivered (41): Scaling Factor: 10e-6 (micro)\n"
                "VA hour received (42): Scaling Factor: 10e-1 (deci)\n";

        BOOST_CHECK_EQUAL( rcv.description, desc_exp );
        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), zeroMetrics );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnSetChannelSelectionCommand_exceptions )
{
    // execute exceptions
    {
        const RfnChannelConfigurationCommand::MetricList metrics = list_of
                ( "WattHourDel"              )
                ( "WattHourRec"              )
                ( "WattHourTotal"            )
                ( "WALDO"                    ) // where's Waldo??
                ( "WattHourNet"              )
                ( "WattsDelCurrentDemand"    );

        const RfnCommand::CommandException expected( BADPARAM, "Unknown metric \"WALDO\"" );

        boost::optional<RfnCommand::CommandException> actual;

        try
        {
            RfnSetChannelSelectionCommand cmd( metrics );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual = ex;
        }

        BOOST_REQUIRE( actual );
        BOOST_CHECK_EQUAL( *actual, expected );
    }

    // decode exceptions
    {
        const std::vector<RfnCommand::RfnResponsePayload> responses = list_of
                ( list_of(0x79)(0x00)(0X00) )
                ( list_of(0x7a)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x01)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x00)(0X03)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                // tlv types
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x07)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x01)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x01)(0x00)(0x01)(0x00)                           )
                // tlv channel selection configuration
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x00)                                (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x03)                          (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x03)(0x01)(0xff)(0xff)              (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x05)(0x02)(0x00)(0x01)(0x00)(0x01)  (0x02)(0x00)(0x01)(0x00) )
                // tlv full description
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x03) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0xff)(0xff)(0x00)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x09)(0x02)(0x00)(0x01)(0x00)(0x00)(0x00)(0x01)(0x00)(0x00) )
                // tlv full description - metric qualifier
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x80)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x60)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x18)(0x00) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x7a)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected duplicated TLV of type (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received TLV of type(s): (1), expected: (1, 2)" ) )
                // tlv channel selection configuration
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 1, expected 7" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 1, expected 13" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description - metric qualifier
                ( RfnCommand::CommandException( ErrorInvalidData, "Metric qualifier expected extension bit to be zero" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Primary/Secondary\" (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Fund/Harmonic\" (3)" ) );


        const RfnChannelConfigurationCommand::MetricList metrics;
        RfnSetChannelSelectionCommand cmd( metrics );

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                actual.push_back( ex );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                       expected.begin(), expected.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnGetChannelSelectionCommand_exceptions )
{
    // decode exceptions
    {
        const std::vector<RfnCommand::RfnResponsePayload> responses = list_of
                ( list_of(0x79)(0x01)(0X00) )
                ( list_of(0x7a)(0x01)(0X00)(0x01)  (0x01)(0x00)(0x01)(0x00)  )
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x01)(0x00)(0x01)(0x00)  )
                ( list_of(0x79)(0x01)(0X03)(0x01)  (0x01)(0x00)(0x01)(0x00)  )
                // tlv types
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x07)(0x01)(0x00) )
                ( list_of(0x79)(0x01)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x01)(0x00)(0x01)(0x00))
                ( list_of(0x79)(0x01)(0X00)(0x00) )
                // tlv channel selection configuration
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x01)(0x00)(0x00) )
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x01)(0x00)(0x01)(0x03) )
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x01)(0x00)(0x03)(0x01)(0xff)(0xff) )
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x01)(0x00)(0x05)(0x02)(0x00)(0x01)(0x00)(0x01) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x7a)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x00)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected duplicated TLV of type (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received TLV of type(s): (), expected: (1)" ) )
                // tlv channel selection configuration
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 1, expected 7" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) );

        RfnGetChannelSelectionCommand cmd;

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                actual.push_back( ex );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                       expected.begin(), expected.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnGetChannelSelectionFullDescriptionCommand_exceptions )
{
    // decode exceptions
    {
        const std::vector<RfnCommand::RfnResponsePayload> responses = list_of
                ( list_of(0x79)(0x02)(0X00) )
                ( list_of(0x7a)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x01)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x02)(0X03)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                // tlv types
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x07)(0x01)(0x00)        (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x01)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x01)(0x00)(0x01)(0x00)                           )
                // tlv channel selection configuration
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x00)                                (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x03)                          (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x03)(0x01)(0xff)(0xff)              (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x05)(0x02)(0x00)(0x01)(0x00)(0x01)  (0x02)(0x00)(0x01)(0x00) )
                // tlv full description
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x03) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0xff)(0xff)(0x00)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x09)(0x02)(0x00)(0x01)(0x00)(0x00)(0x00)(0x01)(0x00)(0x00) )
                // tlv full description - metric qualifier
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x80)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x60)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x18)(0x00) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x7a)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected duplicated TLV of type (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received TLV of type(s): (1), expected: (1, 2)" ) )
                // tlv channel selection configuration
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 1, expected 7" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 1, expected 13" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description - metric qualifier
                ( RfnCommand::CommandException( ErrorInvalidData, "Metric qualifier expected extension bit to be zero" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Primary/Secondary\" (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Fund/Harmonic\" (3)" ) );

        RfnGetChannelSelectionFullDescriptionCommand cmd;

        std::vector<RfnCommand::CommandException> actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                actual.push_back( ex );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                       expected.begin(), expected.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_RfnSetChannelIntervalRecordingCommand )
{
    RfnChannelConfigurationCommand::MetricList metrics = list_of
            ( "WattHourDel"              )
            ( "WattHourRec"              )
            ( "WattHourTotal"            )
            ( "WattHourNet"              )
            ( "WattsDelCurrentDemand"    )
            ( "WattsRecCurrentDemand"    )
            ( "WattsDelPeakDemand"       )
            ( "WattsRecPeakDemand"       )
            ( "WattsDelPeakDemandFrozen" )
            ( "WattsRecPeakDemandFrozen" )
            ( "WattHourDelFrozen"        )
            ( "WattHourRecFrozen"        )
            ( "VarHourDel"               )
            ( "VarHourRec"               )
            ( "VarHourTotal"             );

    const unsigned intervalRecordingSeconds = 0x12345678;
    const unsigned intervalReportingSeconds = 0xa5a5a5a5;

    RfnSetChannelIntervalRecordingCommand cmd( metrics, intervalRecordingSeconds, intervalReportingSeconds );

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        RfnCommand::RfnRequestPayload exp = list_of
                (0x7a)(0x00)(0x01)          // command code + operation + 1 tlv
                (0x01)                      // tlv type
                (0x27)                      // tlv size (1 byte)
                (0x12)(0x34)(0x56)(0x78)    // interval recording
                (0xa5)(0xa5)(0xa5)(0xa5)    // interval reporting
                (0x0f)                      // number of metrics
                (0x00)(0x01)  (0x00)(0x02)  (0x00)(0x03)  (0x00)(0x04)
                (0x00)(0x05)  (0x00)(0x06)  (0x00)(0x07)  (0x00)(0x08)
                (0x00)(0x09)  (0x00)(0x0a)  (0x00)(0x0b)  (0x00)(0x0c)
                (0x00)(0x15)  (0x00)(0x16)  (0x00)(0x17);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const std::vector<unsigned char> response = list_of
                (0x7b)(0x00)(0X00)(0x02)    // command code + operation + status + 2 tlv
                (0x01)                      // tlv type
                (0x27)                      // tlv size (1-byte)
                (0x12)(0x34)(0x56)(0x78)    // interval recording
                (0xa5)(0xa5)(0xa5)(0xa5)    // interval reporting
                (0x0f)                      // number of metrics
                (0x00)(0x01)  (0x00)(0x02)  (0x00)(0x03)  (0x00)(0x04)
                (0x00)(0x05)  (0x00)(0x06)  (0x00)(0x07)  (0x00)(0x08)
                (0x00)(0x09)  (0x00)(0x0a)  (0x00)(0x0b)  (0x00)(0x0c)
                (0x00)(0x15)  (0x00)(0x16)  (0x00)(0x17)
                (0x02)                      // tlv type 2
                (0x79)                      // tlv size (1-byte)
                (0x1e)                      // number of metrics descriptor
                (0x00)(0x01)(0x00)(0x00)  (0x00)(0x02)(0x00)(0x00)  (0x00)(0x03)(0x00)(0x00)  (0x00)(0x04)(0x00)(0x00)
                (0x00)(0x05)(0x00)(0x00)  (0x00)(0x06)(0x00)(0x00)  (0x00)(0x07)(0x00)(0x00)  (0x00)(0x08)(0x00)(0x00)
                (0x00)(0x09)(0x00)(0x00)  (0x00)(0x0a)(0x00)(0x00)  (0x00)(0x0b)(0x00)(0x00)  (0x00)(0x0c)(0x00)(0x00)
                (0x00)(0x15)(0x00)(0x00)  (0x00)(0x16)(0x00)(0x00)  (0x00)(0x17)(0x00)(0x00)  (0x00)(0x18)(0x00)(0x00)
                (0x00)(0x19)(0x00)(0x00)  (0x00)(0x1a)(0x00)(0x00)  (0x00)(0x1b)(0x00)(0x00)  (0x00)(0x1c)(0x00)(0x00)
                (0x00)(0x1d)(0x00)(0x00)  (0x00)(0x1e)(0x00)(0x00)  (0x00)(0x1f)(0x00)(0x00)  (0x00)(0x20)(0x00)(0x00)
                (0x00)(0x21)(0x00)(0x00)  (0x00)(0x22)(0x00)(0x00)  (0x00)(0x23)(0x00)(0x00)  (0x00)(0x29)(0x00)(0x00)
                (0x00)(0x2a)(0x00)(0x00)  (0x00)(0x2b)(0x00)(0x00);

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Interval Recording Configuration:\n"
                "Interval Recording: 305419896 seconds\n"
                "Interval Reporting: 2779096485 seconds\n"
                "Metric(s) list:\n"
                "Watt hour delivered (1)\n"
                "Watt hour received (2)\n"
                "Watt hour total/sum (3)\n"
                "Watt hour net (4)\n"
                "Watts delivered, current demand (5)\n"
                "Watts received, current demand (6)\n"
                "Watts delivered, peak demand (7)\n"
                "Watts received, peak demand (8)\n"
                "Watts delivered, peak demand (Frozen) (9)\n"
                "Watts received, peak demand (Frozen) (10)\n"
                "Watt hour delivered (Frozen) (11)\n"
                "Watt hour received (Frozen) (12)\n"
                "Var hour delivered (21)\n"
                "Var hour received (22)\n"
                "Var hour total/sum (23)\n"
                "Channel Interval Recoding Full Description:\n"
                "Metric(s) descriptors:\n"
                "Watt hour delivered (1): Scaling Factor: 1\n"
                "Watt hour received (2): Scaling Factor: 1\n"
                "Watt hour total/sum (3): Scaling Factor: 1\n"
                "Watt hour net (4): Scaling Factor: 1\n"
                "Watts delivered, current demand (5): Scaling Factor: 1\n"
                "Watts received, current demand (6): Scaling Factor: 1\n"
                "Watts delivered, peak demand (7): Scaling Factor: 1\n"
                "Watts received, peak demand (8): Scaling Factor: 1\n"
                "Watts delivered, peak demand (Frozen) (9): Scaling Factor: 1\n"
                "Watts received, peak demand (Frozen) (10): Scaling Factor: 1\n"
                "Watt hour delivered (Frozen) (11): Scaling Factor: 1\n"
                "Watt hour received (Frozen) (12): Scaling Factor: 1\n"
                "Var hour delivered (21): Scaling Factor: 1\n"
                "Var hour received (22): Scaling Factor: 1\n"
                "Var hour total/sum (23): Scaling Factor: 1\n"
                "Var hour net (24): Scaling Factor: 1\n"
                "Var hour Q1 (25): Scaling Factor: 1\n"
                "Var hour Q2 (26): Scaling Factor: 1\n"
                "Var hour Q3 (27): Scaling Factor: 1\n"
                "Var hour Q4 (28): Scaling Factor: 1\n"
                "Var hour Q1 + Q4 (29): Scaling Factor: 1\n"
                "Var hour Q2 + Q3 (30): Scaling Factor: 1\n"
                "Var hour Q1 - Q4 (31): Scaling Factor: 1\n"
                "Var delivered, current demand (32): Scaling Factor: 1\n"
                "Var received, current demand (33): Scaling Factor: 1\n"
                "Var delivered, peak demand (34): Scaling Factor: 1\n"
                "Var received, peak demand (35): Scaling Factor: 1\n"
                "VA hour delivered (41): Scaling Factor: 1\n"
                "VA hour received (42): Scaling Factor: 1\n"
                "VA hour total/sum (43): Scaling Factor: 1\n";

        BOOST_CHECK_EQUAL( rcv.description, desc_exp );
        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), metrics );
    }
}

BOOST_AUTO_TEST_CASE( test_RfnGetChannelIntervalRecordingCommand )
{
    RfnChannelConfigurationCommand::MetricList zeroMetrics;

    RfnGetChannelIntervalRecordingCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        RfnCommand::RfnRequestPayload exp = list_of
                (0x7a)(0x01)(0x00);          // command code + operation + 0 tlv

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const std::vector<unsigned char> response = list_of
                (0x7b)(0x01)(0X00)(0x02)    // command code + operation + status + 2 tlv
                (0x01)                      // tlv type
                (0x09)                      // tlv size (1-byte)
                (0x12)(0x34)(0x56)(0x78)    // interval recording
                (0xa5)(0xa5)(0xa5)(0xa5)    // interval reporting
                (0x00)                      // number of metrics
                (0x02)                      // tlv type 2
                (0x01)                      // tlv size (1-byte)
                (0x00);                     // number of metrics descriptor

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Interval Recording Configuration:\n"
                "Interval Recording: 305419896 seconds\n"
                "Interval Reporting: 2779096485 seconds\n"
                "Metric(s) list:\n"
                "none\n"
                "Channel Interval Recoding Full Description:\n"
                "Metric(s) descriptors:\n"
                "none\n";

        BOOST_CHECK_EQUAL( rcv.description, desc_exp );
        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), zeroMetrics );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnSetChannelIntervalRecordingCommand_exceptions )
{
    // execute exceptions
    {
        const std::vector<RfnChannelConfigurationCommand::MetricList> metrics = list_of
                // invalid metric
                (list_of( "WattHourDel"              )
                        ( "WattHourRec"              )
                        ( "WattHourTotal"            )
                        ( "WALDO"                    )
                        ( "WattHourNet"              )
                        ( "WattsDelCurrentDemand"    ) )
                // 16 metrics
                (list_of( "WattHourDel"              )
                        ( "WattHourRec"              )
                        ( "WattHourTotal"            )
                        ( "WattHourNet"              )
                        ( "WattsDelCurrentDemand"    )
                        ( "WattsRecCurrentDemand"    )
                        ( "WattsDelPeakDemand"       )
                        ( "WattsRecPeakDemand"       )
                        ( "WattsDelPeakDemandFrozen" )
                        ( "WattsRecPeakDemandFrozen" )
                        ( "WattHourDelFrozen"        )
                        ( "WattHourRecFrozen"        )
                        ( "VarHourDel"               )
                        ( "VarHourRec"               )
                        ( "VarHourTotal"             )
                        ( "VarHourNet"               ) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( BADPARAM, "Unknown metric \"WALDO\"" ) )
                ( RfnCommand::CommandException( BADPARAM, "Number of metrics 16, expected <= 15" ) );

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnChannelConfigurationCommand::MetricList & m in metrics )
        {
            try
            {
                RfnSetChannelIntervalRecordingCommand cmd( m, 0, 0 );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                actual.push_back( ex );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                       expected.begin(), expected.end() );
    }

    // decode exceptions
    {
        const std::vector<RfnCommand::RfnResponsePayload> responses = list_of
                ( list_of(0x7b)(0x00)(0X00) )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X03)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x00) )
                // tlv types
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x07)(0x01)(0x00)                                                  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                // tlv interval recording configuration
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x00)                                                                                (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x01)                          (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x0b)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x01)(0xff)(0xff)              (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x0d)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x02)(0x00)(0x01)(0x00)(0x01)  (0x02)(0x01)(0x00) )
                // tlv full description
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x03) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0xff)(0xff)(0x00)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x09)(0x02)(0x00)(0x01)(0x00)(0x00)(0x00)(0x01)(0x00)(0x00) )
                // tlv full description - metric qualifier
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x80)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x60)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x18)(0x00) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x79)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected duplicated TLV of type (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received TLV of type(s): (1), expected: (1, 2)" ) )
                // tlv channel selection configuration
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for interval recording received 0, expected >= 9" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 1, expected 3" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 1, expected 13" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description - metric qualifier
                ( RfnCommand::CommandException( ErrorInvalidData, "Metric qualifier expected extension bit to be zero" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Primary/Secondary\" (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Fund/Harmonic\" (3)" ) );


        const RfnChannelConfigurationCommand::MetricList metrics;
        RfnSetChannelIntervalRecordingCommand cmd( metrics, 0, 0 );

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                actual.push_back( ex );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                       expected.begin(), expected.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_RfnGetChannelIntervalRecordingCommand_exceptions )
{
    // decode exceptions
    {
        const std::vector<RfnCommand::RfnResponsePayload> responses = list_of
                ( list_of(0x7b)(0x01)(0X00) )
                ( list_of(0x79)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X03)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x00) )
                // tlv types
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x07)(0x01)(0x00)                                                  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x01)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                // tlv interval recording configuration
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x00)                                                                                (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x01)                          (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x0b)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x01)(0xff)(0xff)              (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x0d)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x02)(0x00)(0x01)(0x00)(0x01)  (0x02)(0x01)(0x00) )
                // tlv full description
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x01)(0x03) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0xff)(0xff)(0x00)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x09)(0x02)(0x00)(0x01)(0x00)(0x00)(0x00)(0x01)(0x00)(0x00) )
                // tlv full description - metric qualifier
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x80)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x60)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x18)(0x00) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x79)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x00)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected duplicated TLV of type (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received TLV of type(s): (1), expected: (1, 2)" ) )
                // tlv channel selection configuration
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for interval recording received 0, expected >= 9" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 1, expected 3" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 1, expected 13" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unexpected duplicated metric: Watt hour delivered (1)" ) )
                // tlv full description - metric qualifier
                ( RfnCommand::CommandException( ErrorInvalidData, "Metric qualifier expected extension bit to be zero" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Primary/Secondary\" (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Fund/Harmonic\" (3)" ) );

        RfnGetChannelIntervalRecordingCommand cmd;

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                actual.push_back( ex );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                       expected.begin(), expected.end() );
    }
}

BOOST_AUTO_TEST_SUITE_END()
