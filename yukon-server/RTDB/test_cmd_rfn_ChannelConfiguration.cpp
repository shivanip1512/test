#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "std_helper.h"
#include "boost_test_helpers.h"
#include "cmd_rfn_ChannelConfiguration.h"

using boost::assign::list_of;

using namespace Cti::Devices::Commands;

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

ostream & operator<<( ostream & os, const RfnChannelConfigurationCommand::MetricIds& metrics )
{
    RfnChannelConfigurationCommand::MetricIds::const_iterator itr = metrics.begin();

    if( itr != metrics.end() )
    {
        os << "("<< *(itr++) << ")";
        while( itr != metrics.end() )
        {
            os << ",(" << *(itr++) << ")";
        }
    }
    return os;
}

ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex );

ostream & operator<<( ostream & os, const std::vector<unsigned char> & bytes);

}

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_ChannelConfiguration )

namespace { // anonymous

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

// NOTE: metrics are split up in list of 80 metrics or less
const std::vector<RfnChannelConfigurationCommand::MetricIds> metricsAll = list_of
        (list_of(    1 )(    2 )(    3 )(    4 )(    5 )(    6 )(    7 )(    8 )(    9 )(   10 )(   11 )(   12 )(   21 )(   22 )(   23 )(   24 )
                (   25 )(   26 )(   27 )(   28 )(   29 )(   30 )(   31 )(   32 )(   33 )(   34 )(   35 )(   36 )(   37 )(   41 )(   42 )(   43 )
                (   44 )(   45 )(   46 )(   47 )(   48 )(   49 )(   50 )(   51 )(   52 )(   61 )(   62 )(   63 )(   64 )(   65 )(   66 )(   67 )
                (   68 )(   69 )(   70 )(   81 )(   82 )(   83 )(  100 )(  101 )(  102 )(  103 )(  104 )(  105 )(  106 )(  107 )(  108 )(  109 )
                (  110 )(  111 )(  112 )(  113 )(  114 )(  115 )(  116 )(  117 )(  118 )(  119 )(  120 )(  121 )(  122 )(  123 )(  124 )(  150 ))

        (list_of(  151 )(  152 )(  153 )(  154 )(  155 )(  156 )(  157 )(  158 )(  159 )(  160 )(  161 )(  162 )(  163 )(  164 )(  256 )(  257 )
                ( 1001 )( 1002 )( 1003 )( 1004 )( 1005 )( 1006 )( 1007 )( 1008 )( 1009 )( 1010 )( 1011 )( 1012 )( 1021 )( 1022 )( 1023 )( 1024 )
                ( 1025 )( 1026 )( 1027 )( 1028 )( 1029 )( 1030 )( 1031 )( 1032 )( 1033 )( 1034 )( 1035 )( 1036 )( 1037 )( 1041 )( 1042 )( 1043 )
                ( 1044 )( 1045 )( 1046 )( 1047 )( 1048 )( 1049 )( 1050 )( 1051 )( 1052 )( 1061 )( 1062 )( 1063 )( 1064 )( 1065 )( 1066 )( 1067 )
                ( 1068 )( 1069 )( 1070 )( 1081 )( 1082 )( 1083 )( 1100 )( 1101 )( 1102 )( 1103 )( 1104 )( 1105 )( 1106 )( 1107 )( 1108 )( 1109 ))

        (list_of( 1110 )( 1111 )( 1112 )( 1113 )( 1114 )( 1115 )( 1116 )( 1117 )( 1118 )( 1119 )( 1120 )( 1121 )( 1122 )( 1123 )( 1124 )( 1150 )
                ( 1151 )( 1152 )( 1153 )( 1154 )( 1155 )( 1156 )( 1157 )( 1158 )( 1159 )( 1160 )( 1161 )( 1162 )( 1163 )( 1164 )
                ( 2001 )( 2002 )( 2003 )( 2004 )( 2005 )( 2006 )( 2007 )( 2008 )( 2009 )( 2010 )( 2011 )( 2012 )( 2021 )( 2022 )( 2023 )( 2024 )
                ( 2025 )( 2026 )( 2027 )( 2028 )( 2029 )( 2030 )( 2031 )( 2032 )( 2033 )( 2034 )( 2035 )( 2036 )( 2037 )( 2041 )( 2042 )( 2043 )
                ( 2044 )( 2045 )( 2046 )( 2047 )( 2048 )( 2049 )( 2050 )( 2051 )( 2052 )( 2061 )( 2062 )( 2063 )( 2064 )( 2065 )( 2066 )( 2067 ))

        (list_of( 2068 )( 2069 )( 2070 )( 2081 )( 2082 )( 2083 )( 2100 )( 2101 )( 2102 )( 2103 )( 2104 )( 2105 )( 2106 )( 2107 )( 2108 )( 2109 )
                ( 2110 )( 2111 )( 2112 )( 2113 )( 2114 )( 2115 )( 2116 )( 2117 )( 2118 )( 2119 )( 2120 )( 2121 )( 2122 )( 2123 )( 2124 )( 2150 )
                ( 2151 )( 2152 )( 2153 )( 2154 )( 2155 )( 2156 )( 2157 )( 2158 )( 2159 )( 2160 )( 2161 )( 2162 )( 2163 )( 2164 )
                ( 3001 )( 3002 )( 3003 )( 3004 )( 3005 )( 3006 )( 3007 )( 3008 )( 3009 )( 3010 )( 3011 )( 3012 )( 3021 )( 3022 )( 3023 )( 3024 )
                ( 3025 )( 3026 )( 3027 )( 3028 )( 3029 )( 3030 )( 3031 )( 3032 )( 3033 )( 3034 )( 3035 )( 3036 )( 3037 )( 3041 )( 3042 )( 3043 ))

        (list_of( 3044 )( 3045 )( 3046 )( 3047 )( 3048 )( 3049 )( 3050 )( 3051 )( 3052 )( 3061 )( 3062 )( 3063 )( 3064 )( 3065 )( 3066 )( 3067 )
                ( 3068 )( 3069 )( 3070 )( 3081 )( 3082 )( 3083 )( 3100 )( 3101 )( 3102 )( 3103 )( 3104 )( 3105 )( 3106 )( 3107 )( 3108 )( 3109 )
                ( 3110 )( 3111 )( 3112 )( 3113 )( 3114 )( 3115 )( 3116 )( 3117 )( 3118 )( 3119 )( 3120 )( 3121 )( 3122 )( 3123 )( 3124 )( 3150 )
                ( 3151 )( 3152 )( 3153 )( 3154 )( 3155 )( 3156 )( 3157 )( 3158 )( 3159 )( 3160 )( 3161 )( 3162 )( 3163 )( 3164 )
                ( 4001 )( 4002 )( 4003 )( 4004 )( 4005 )( 4006 )( 4007 )( 4008 )( 4009 )( 4010 )( 4011 )( 4012 )( 4021 )( 4022 )( 4023 )( 4024 ))

        (list_of( 4025 )( 4026 )( 4027 )( 4028 )( 4029 )( 4030 )( 4031 )( 4032 )( 4033 )( 4034 )( 4035 )( 4036 )( 4037 )( 4041 )( 4042 )( 4043 )
                ( 4044 )( 4045 )( 4046 )( 4047 )( 4048 )( 4049 )( 4050 )( 4051 )( 4052 )( 4061 )( 4062 )( 4063 )( 4064 )( 4065 )( 4066 )( 4067 )
                ( 4068 )( 4069 )( 4070 )( 4081 )( 4082 )( 4083 )( 4100 )( 4101 )( 4102 )( 4103 )( 4104 )( 4105 )( 4106 )( 4107 )( 4108 )( 4109 )
                ( 4110 )( 4111 )( 4112 )( 4113 )( 4114 )( 4115 )( 4116 )( 4117 )( 4118 )( 4119 )( 4120 )( 4121 )( 4122 )( 4123 )( 4124 )( 4150 )
                ( 4151 )( 4152 )( 4153 )( 4154 )( 4155 )( 4156 )( 4157 )( 4158 )( 4159 )( 4160 )( 4161 )( 4162 )( 4163 )( 4164 ))
        ;

// NOTE: descriptions follow metrics defined above
const std::vector< std::vector< std::string > > descriptionsAll = list_of<std::vector< std::string >>
        (list_of("Watt hour delivered (1)")
                ("Watt hour received (2)")
                ("Watt hour total/sum (3)")
                ("Watt hour net (4)")
                ("Watts delivered, current demand (5)")
                ("Watts received, current demand (6)")
                ("Watts delivered, peak demand (7)")
                ("Watts received, peak demand (8)")
                ("Watts delivered, peak demand (Frozen) (9)")
                ("Watts received, peak demand (Frozen) (10)")
                ("Watt hour delivered (Frozen) (11)")
                ("Watt hour received (Frozen) (12)")
                ("Var hour delivered (21)")
                ("Var hour received (22)")
                ("Var hour total/sum (23)")
                ("Var hour net (24)")
                ("Var hour Q1 (25)")
                ("Var hour Q2 (26)")
                ("Var hour Q3 (27)")
                ("Var hour Q4 (28)")
                ("Var hour Q1 + Q4 (29)")
                ("Var hour Q2 + Q3 (30)")
                ("Var hour Q1 - Q4 (31)")
                ("Var delivered, current demand (32)")
                ("Var received, current demand (33)")
                ("Var delivered, peak demand (34)")
                ("Var received, peak demand (35)")
                ("Var delivered, peak demand coincident (36)")
                ("Var received, peak demand coincident (37)")
                ("VA hour delivered (41)")
                ("VA hour received (42)")
                ("VA hour total/sum (43)")
                ("VA hour net (44)")
                ("VA hour Q1 (45)")
                ("VA hour Q2 (46)")
                ("VA hour Q3 (47)")
                ("VA hour Q4 (48)")
                ("VA delivered, current demand (49)")
                ("VA received, current demand (50)")
                ("VA received, peak demand (51)")
                ("VA delivered, peak demand (52)")
                ("Q hour delivered (61)")
                ("Q hour received (62)")
                ("Q hour total/sum (63)")
                ("Q hour net (64)")
                ("Q delivered, current demand (65)")
                ("Q received, current demand (66)")
                ("Q delivered, peak demand (67)")
                ("Q received, peak demand (68)")
                ("Q delivered, peak demand coincident (69)")
                ("Q received, peak demand coincident (70)")
                ("Power Factor kWh(del)/kVar(del) (81)")
                ("Power Factor kWh(rec)/kVar(rec) (82)")
                ("Power Factor kWh(total)/kVar(total) (83)")
                ("Voltage Phase A (100)")
                ("Voltage Phase B (101)")
                ("Voltage Phase C (102)")
                ("Current Phase A (103)")
                ("Current Phase B (104)")
                ("Current Phase C (105)")
                ("Voltage Angle Phase A (106)")
                ("Voltage Angle Phase B (107)")
                ("Voltage Angle Phase C (108)")
                ("Current Angle Phase A (109)")
                ("Current Angle Phase B (110)")
                ("Current Angle Phase C (111)")
                ("Voltage Min (112)")
                ("Voltage Average (113)")
                ("Voltage Max (114)")
                ("Voltage (115)")
                ("Voltage Min Phase A (116)")
                ("Voltage Min Phase B (117)")
                ("Voltage Min Phase C (118)")
                ("Voltage Average Phase A (119)")
                ("Voltage Average Phase B (120)")
                ("Voltage Average Phase C (121)")
                ("Voltage Max Phase A (122)")
                ("Voltage Max Phase B (123)")
                ("Voltage Max Phase C (124)")
                ("Watts Phase A (150)"))

        (list_of("Watts Phase B (151)")
                ("Watts Phase C (152)")
                ("Var Phase A (153)")
                ("Var Phase B (154)")
                ("Var Phase C (155)")
                ("VA Phase A (156)")
                ("VA Phase B (157)")
                ("VA Phase C (158)")
                ("PF degree Phase A (159)")
                ("PF degree Phase B (160)")
                ("PF degree Phase C (161)")
                ("PF Phase A (162)")
                ("PF Phase B (163)")
                ("PF Phase C (164)")
                ("Time in Seconds (256)")
                ("Temperature in Centigrade (257)")
                ("Watt hour delivered, Rate A (1001)")
                ("Watt hour received, Rate A (1002)")
                ("Watt hour total/sum, Rate A (1003)")
                ("Watt hour net, Rate A (1004)")
                ("Watts delivered, current demand, Rate A (1005)")
                ("Watts received, current demand, Rate A (1006)")
                ("Watts delivered, peak demand, Rate A (1007)")
                ("Watts received, peak demand, Rate A (1008)")
                ("Watts delivered, peak demand (Frozen), Rate A (1009)")
                ("Watts received, peak demand (Frozen), Rate A (1010)")
                ("Watt hour delivered (Frozen), Rate A (1011)")
                ("Watt hour received (Frozen), Rate A (1012)")
                ("Var hour delivered, Rate A (1021)")
                ("Var hour received, Rate A (1022)")
                ("Var hour total/sum, Rate A (1023)")
                ("Var hour net, Rate A (1024)")
                ("Var hour Q1, Rate A (1025)")
                ("Var hour Q2, Rate A (1026)")
                ("Var hour Q3, Rate A (1027)")
                ("Var hour Q4, Rate A (1028)")
                ("Var hour Q1 + Q4, Rate A (1029)")
                ("Var hour Q2 + Q3, Rate A (1030)")
                ("Var hour Q1 - Q4, Rate A (1031)")
                ("Var delivered, current demand, Rate A (1032)")
                ("Var received, current demand, Rate A (1033)")
                ("Var delivered, peak demand, Rate A (1034)")
                ("Var received, peak demand, Rate A (1035)")
                ("Var delivered, peak demand coincident, Rate A (1036)")
                ("Var received, peak demand coincident, Rate A (1037)")
                ("VA hour delivered, Rate A (1041)")
                ("VA hour received, Rate A (1042)")
                ("VA hour total/sum, Rate A (1043)")
                ("VA hour net, Rate A (1044)")
                ("VA hour Q1, Rate A (1045)")
                ("VA hour Q2, Rate A (1046)")
                ("VA hour Q3, Rate A (1047)")
                ("VA hour Q4, Rate A (1048)")
                ("VA delivered, current demand, Rate A (1049)")
                ("VA received, current demand, Rate A (1050)")
                ("VA received, peak demand, Rate A (1051)")
                ("VA delivered, peak demand, Rate A (1052)")
                ("Q hour delivered, Rate A (1061)")
                ("Q hour received, Rate A (1062)")
                ("Q hour total/sum, Rate A (1063)")
                ("Q hour net, Rate A (1064)")
                ("Q delivered, current demand, Rate A (1065)")
                ("Q received, current demand, Rate A (1066)")
                ("Q delivered, peak demand, Rate A (1067)")
                ("Q received, peak demand, Rate A (1068)")
                ("Q delivered, peak demand coincident, Rate A (1069)")
                ("Q received, peak demand coincident, Rate A (1070)")
                ("Power Factor kWh(del)/kVar(del), Rate A (1081)")
                ("Power Factor kWh(rec)/kVar(rec), Rate A (1082)")
                ("Power Factor kWh(total)/kVar(total), Rate A (1083)")
                ("Voltage Phase A, Rate A (1100)")
                ("Voltage Phase B, Rate A (1101)")
                ("Voltage Phase C, Rate A (1102)")
                ("Current Phase A, Rate A (1103)")
                ("Current Phase B, Rate A (1104)")
                ("Current Phase C, Rate A (1105)")
                ("Voltage Angle Phase A, Rate A (1106)")
                ("Voltage Angle Phase B, Rate A (1107)")
                ("Voltage Angle Phase C, Rate A (1108)")
                ("Current Angle Phase A, Rate A (1109)"))

        (list_of("Current Angle Phase B, Rate A (1110)")
                ("Current Angle Phase C, Rate A (1111)")
                ("Voltage Min, Rate A (1112)")
                ("Voltage Average, Rate A (1113)")
                ("Voltage Max, Rate A (1114)")
                ("Voltage, Rate A (1115)")
                ("Voltage Min Phase A, Rate A (1116)")
                ("Voltage Min Phase B, Rate A (1117)")
                ("Voltage Min Phase C, Rate A (1118)")
                ("Voltage Average Phase A, Rate A (1119)")
                ("Voltage Average Phase B, Rate A (1120)")
                ("Voltage Average Phase C, Rate A (1121)")
                ("Voltage Max Phase A, Rate A (1122)")
                ("Voltage Max Phase B, Rate A (1123)")
                ("Voltage Max Phase C, Rate A (1124)")
                ("Watts Phase A, Rate A (1150)")
                ("Watts Phase B, Rate A (1151)")
                ("Watts Phase C, Rate A (1152)")
                ("Var Phase A, Rate A (1153)")
                ("Var Phase B, Rate A (1154)")
                ("Var Phase C, Rate A (1155)")
                ("VA Phase A, Rate A (1156)")
                ("VA Phase B, Rate A (1157)")
                ("VA Phase C, Rate A (1158)")
                ("PF degree Phase A, Rate A (1159)")
                ("PF degree Phase B, Rate A (1160)")
                ("PF degree Phase C, Rate A (1161)")
                ("PF Phase A, Rate A (1162)")
                ("PF Phase B, Rate A (1163)")
                ("PF Phase C, Rate A (1164)")
                ("Watt hour delivered, Rate B (2001)")
                ("Watt hour received, Rate B (2002)")
                ("Watt hour total/sum, Rate B (2003)")
                ("Watt hour net, Rate B (2004)")
                ("Watts delivered, current demand, Rate B (2005)")
                ("Watts received, current demand, Rate B (2006)")
                ("Watts delivered, peak demand, Rate B (2007)")
                ("Watts received, peak demand, Rate B (2008)")
                ("Watts delivered, peak demand (Frozen), Rate B (2009)")
                ("Watts received, peak demand (Frozen), Rate B (2010)")
                ("Watt hour delivered (Frozen), Rate B (2011)")
                ("Watt hour received (Frozen), Rate B (2012)")
                ("Var hour delivered, Rate B (2021)")
                ("Var hour received, Rate B (2022)")
                ("Var hour total/sum, Rate B (2023)")
                ("Var hour net, Rate B (2024)")
                ("Var hour Q1, Rate B (2025)")
                ("Var hour Q2, Rate B (2026)")
                ("Var hour Q3, Rate B (2027)")
                ("Var hour Q4, Rate B (2028)")
                ("Var hour Q1 + Q4, Rate B (2029)")
                ("Var hour Q2 + Q3, Rate B (2030)")
                ("Var hour Q1 - Q4, Rate B (2031)")
                ("Var delivered, current demand, Rate B (2032)")
                ("Var received, current demand, Rate B (2033)")
                ("Var delivered, peak demand, Rate B (2034)")
                ("Var received, peak demand, Rate B (2035)")
                ("Var delivered, peak demand coincident, Rate B (2036)")
                ("Var received, peak demand coincident, Rate B (2037)")
                ("VA hour delivered, Rate B (2041)")
                ("VA hour received, Rate B (2042)")
                ("VA hour total/sum, Rate B (2043)")
                ("VA hour net, Rate B (2044)")
                ("VA hour Q1, Rate B (2045)")
                ("VA hour Q2, Rate B (2046)")
                ("VA hour Q3, Rate B (2047)")
                ("VA hour Q4, Rate B (2048)")
                ("VA delivered, current demand, Rate B (2049)")
                ("VA received, current demand, Rate B (2050)")
                ("VA received, peak demand, Rate B (2051)")
                ("VA delivered, peak demand, Rate B (2052)")
                ("Q hour delivered, Rate B (2061)")
                ("Q hour received, Rate B (2062)")
                ("Q hour total/sum, Rate B (2063)")
                ("Q hour net, Rate B (2064)")
                ("Q delivered, current demand, Rate B (2065)")
                ("Q received, current demand, Rate B (2066)")
                ("Q delivered, peak demand, Rate B (2067)"))

        (list_of("Q received, peak demand, Rate B (2068)")
                ("Q delivered, peak demand coincident, Rate B (2069)")
                ("Q received, peak demand coincident, Rate B (2070)")
                ("Power Factor kWh(del)/kVar(del), Rate B (2081)")
                ("Power Factor kWh(rec)/kVar(rec), Rate B (2082)")
                ("Power Factor kWh(total)/kVar(total), Rate B (2083)")
                ("Voltage Phase A, Rate B (2100)")
                ("Voltage Phase B, Rate B (2101)")
                ("Voltage Phase C, Rate B (2102)")
                ("Current Phase A, Rate B (2103)")
                ("Current Phase B, Rate B (2104)")
                ("Current Phase C, Rate B (2105)")
                ("Voltage Angle Phase A, Rate B (2106)")
                ("Voltage Angle Phase B, Rate B (2107)")
                ("Voltage Angle Phase C, Rate B (2108)")
                ("Current Angle Phase A, Rate B (2109)")
                ("Current Angle Phase B, Rate B (2110)")
                ("Current Angle Phase C, Rate B (2111)")
                ("Voltage Min, Rate B (2112)")
                ("Voltage Average, Rate B (2113)")
                ("Voltage Max, Rate B (2114)")
                ("Voltage, Rate B (2115)")
                ("Voltage Min Phase A, Rate B (2116)")
                ("Voltage Min Phase B, Rate B (2117)")
                ("Voltage Min Phase C, Rate B (2118)")
                ("Voltage Average Phase A, Rate B (2119)")
                ("Voltage Average Phase B, Rate B (2120)")
                ("Voltage Average Phase C, Rate B (2121)")
                ("Voltage Max Phase A, Rate B (2122)")
                ("Voltage Max Phase B, Rate B (2123)")
                ("Voltage Max Phase C, Rate B (2124)")
                ("Watts Phase A, Rate B (2150)")
                ("Watts Phase B, Rate B (2151)")
                ("Watts Phase C, Rate B (2152)")
                ("Var Phase A, Rate B (2153)")
                ("Var Phase B, Rate B (2154)")
                ("Var Phase C, Rate B (2155)")
                ("VA Phase A, Rate B (2156)")
                ("VA Phase B, Rate B (2157)")
                ("VA Phase C, Rate B (2158)")
                ("PF degree Phase A, Rate B (2159)")
                ("PF degree Phase B, Rate B (2160)")
                ("PF degree Phase C, Rate B (2161)")
                ("PF Phase A, Rate B (2162)")
                ("PF Phase B, Rate B (2163)")
                ("PF Phase C, Rate B (2164)")
                ("Watt hour delivered, Rate C (3001)")
                ("Watt hour received, Rate C (3002)")
                ("Watt hour total/sum, Rate C (3003)")
                ("Watt hour net, Rate C (3004)")
                ("Watts delivered, current demand, Rate C (3005)")
                ("Watts received, current demand, Rate C (3006)")
                ("Watts delivered, peak demand, Rate C (3007)")
                ("Watts received, peak demand, Rate C (3008)")
                ("Watts delivered, peak demand (Frozen), Rate C (3009)")
                ("Watts received, peak demand (Frozen), Rate C (3010)")
                ("Watt hour delivered (Frozen), Rate C (3011)")
                ("Watt hour received (Frozen), Rate C (3012)")
                ("Var hour delivered, Rate C (3021)")
                ("Var hour received, Rate C (3022)")
                ("Var hour total/sum, Rate C (3023)")
                ("Var hour net, Rate C (3024)")
                ("Var hour Q1, Rate C (3025)")
                ("Var hour Q2, Rate C (3026)")
                ("Var hour Q3, Rate C (3027)")
                ("Var hour Q4, Rate C (3028)")
                ("Var hour Q1 + Q4, Rate C (3029)")
                ("Var hour Q2 + Q3, Rate C (3030)")
                ("Var hour Q1 - Q4, Rate C (3031)")
                ("Var delivered, current demand, Rate C (3032)")
                ("Var received, current demand, Rate C (3033)")
                ("Var delivered, peak demand, Rate C (3034)")
                ("Var received, peak demand, Rate C (3035)")
                ("Var delivered, peak demand coincident, Rate C (3036)")
                ("Var received, peak demand coincident, Rate C (3037)")
                ("VA hour delivered, Rate C (3041)")
                ("VA hour received, Rate C (3042)")
                ("VA hour total/sum, Rate C (3043)"))

        (list_of("VA hour net, Rate C (3044)")
                ("VA hour Q1, Rate C (3045)")
                ("VA hour Q2, Rate C (3046)")
                ("VA hour Q3, Rate C (3047)")
                ("VA hour Q4, Rate C (3048)")
                ("VA delivered, current demand, Rate C (3049)")
                ("VA received, current demand, Rate C (3050)")
                ("VA received, peak demand, Rate C (3051)")
                ("VA delivered, peak demand, Rate C (3052)")
                ("Q hour delivered, Rate C (3061)")
                ("Q hour received, Rate C (3062)")
                ("Q hour total/sum, Rate C (3063)")
                ("Q hour net, Rate C (3064)")
                ("Q delivered, current demand, Rate C (3065)")
                ("Q received, current demand, Rate C (3066)")
                ("Q delivered, peak demand, Rate C (3067)")
                ("Q received, peak demand, Rate C (3068)")
                ("Q delivered, peak demand coincident, Rate C (3069)")
                ("Q received, peak demand coincident, Rate C (3070)")
                ("Power Factor kWh(del)/kVar(del), Rate C (3081)")
                ("Power Factor kWh(rec)/kVar(rec), Rate C (3082)")
                ("Power Factor kWh(total)/kVar(total), Rate C (3083)")
                ("Voltage Phase A, Rate C (3100)")
                ("Voltage Phase B, Rate C (3101)")
                ("Voltage Phase C, Rate C (3102)")
                ("Current Phase A, Rate C (3103)")
                ("Current Phase B, Rate C (3104)")
                ("Current Phase C, Rate C (3105)")
                ("Voltage Angle Phase A, Rate C (3106)")
                ("Voltage Angle Phase B, Rate C (3107)")
                ("Voltage Angle Phase C, Rate C (3108)")
                ("Current Angle Phase A, Rate C (3109)")
                ("Current Angle Phase B, Rate C (3110)")
                ("Current Angle Phase C, Rate C (3111)")
                ("Voltage Min, Rate C (3112)")
                ("Voltage Average, Rate C (3113)")
                ("Voltage Max, Rate C (3114)")
                ("Voltage, Rate C (3115)")
                ("Voltage Min Phase A, Rate C (3116)")
                ("Voltage Min Phase B, Rate C (3117)")
                ("Voltage Min Phase C, Rate C (3118)")
                ("Voltage Average Phase A, Rate C (3119)")
                ("Voltage Average Phase B, Rate C (3120)")
                ("Voltage Average Phase C, Rate C (3121)")
                ("Voltage Max Phase A, Rate C (3122)")
                ("Voltage Max Phase B, Rate C (3123)")
                ("Voltage Max Phase C, Rate C (3124)")
                ("Watts Phase A, Rate C (3150)")
                ("Watts Phase B, Rate C (3151)")
                ("Watts Phase C, Rate C (3152)")
                ("Var Phase A, Rate C (3153)")
                ("Var Phase B, Rate C (3154)")
                ("Var Phase C, Rate C (3155)")
                ("VA Phase A, Rate C (3156)")
                ("VA Phase B, Rate C (3157)")
                ("VA Phase C, Rate C (3158)")
                ("PF degree Phase A, Rate C (3159)")
                ("PF degree Phase B, Rate C (3160)")
                ("PF degree Phase C, Rate C (3161)")
                ("PF Phase A, Rate C (3162)")
                ("PF Phase B, Rate C (3163)")
                ("PF Phase C, Rate C (3164)")
                ("Watt hour delivered, Rate D (4001)")
                ("Watt hour received, Rate D (4002)")
                ("Watt hour total/sum, Rate D (4003)")
                ("Watt hour net, Rate D (4004)")
                ("Watts delivered, current demand, Rate D (4005)")
                ("Watts received, current demand, Rate D (4006)")
                ("Watts delivered, peak demand, Rate D (4007)")
                ("Watts received, peak demand, Rate D (4008)")
                ("Watts delivered, peak demand (Frozen), Rate D (4009)")
                ("Watts received, peak demand (Frozen), Rate D (4010)")
                ("Watt hour delivered (Frozen), Rate D (4011)")
                ("Watt hour received (Frozen), Rate D (4012)")
                ("Var hour delivered, Rate D (4021)")
                ("Var hour received, Rate D (4022)")
                ("Var hour total/sum, Rate D (4023)")
                ("Var hour net, Rate D (4024)"))

        (list_of("Var hour Q1, Rate D (4025)")
                ("Var hour Q2, Rate D (4026)")
                ("Var hour Q3, Rate D (4027)")
                ("Var hour Q4, Rate D (4028)")
                ("Var hour Q1 + Q4, Rate D (4029)")
                ("Var hour Q2 + Q3, Rate D (4030)")
                ("Var hour Q1 - Q4, Rate D (4031)")
                ("Var delivered, current demand, Rate D (4032)")
                ("Var received, current demand, Rate D (4033)")
                ("Var delivered, peak demand, Rate D (4034)")
                ("Var received, peak demand, Rate D (4035)")
                ("Var delivered, peak demand coincident, Rate D (4036)")
                ("Var received, peak demand coincident, Rate D (4037)")
                ("VA hour delivered, Rate D (4041)")
                ("VA hour received, Rate D (4042)")
                ("VA hour total/sum, Rate D (4043)")
                ("VA hour net, Rate D (4044)")
                ("VA hour Q1, Rate D (4045)")
                ("VA hour Q2, Rate D (4046)")
                ("VA hour Q3, Rate D (4047)")
                ("VA hour Q4, Rate D (4048)")
                ("VA delivered, current demand, Rate D (4049)")
                ("VA received, current demand, Rate D (4050)")
                ("VA received, peak demand, Rate D (4051)")
                ("VA delivered, peak demand, Rate D (4052)")
                ("Q hour delivered, Rate D (4061)")
                ("Q hour received, Rate D (4062)")
                ("Q hour total/sum, Rate D (4063)")
                ("Q hour net, Rate D (4064)")
                ("Q delivered, current demand, Rate D (4065)")
                ("Q received, current demand, Rate D (4066)")
                ("Q delivered, peak demand, Rate D (4067)")
                ("Q received, peak demand, Rate D (4068)")
                ("Q delivered, peak demand coincident, Rate D (4069)")
                ("Q received, peak demand coincident, Rate D (4070)")
                ("Power Factor kWh(del)/kVar(del), Rate D (4081)")
                ("Power Factor kWh(rec)/kVar(rec), Rate D (4082)")
                ("Power Factor kWh(total)/kVar(total), Rate D (4083)")
                ("Voltage Phase A, Rate D (4100)")
                ("Voltage Phase B, Rate D (4101)")
                ("Voltage Phase C, Rate D (4102)")
                ("Current Phase A, Rate D (4103)")
                ("Current Phase B, Rate D (4104)")
                ("Current Phase C, Rate D (4105)")
                ("Voltage Angle Phase A, Rate D (4106)")
                ("Voltage Angle Phase B, Rate D (4107)")
                ("Voltage Angle Phase C, Rate D (4108)")
                ("Current Angle Phase A, Rate D (4109)")
                ("Current Angle Phase B, Rate D (4110)")
                ("Current Angle Phase C, Rate D (4111)")
                ("Voltage Min, Rate D (4112)")
                ("Voltage Average, Rate D (4113)")
                ("Voltage Max, Rate D (4114)")
                ("Voltage, Rate D (4115)")
                ("Voltage Min Phase A, Rate D (4116)")
                ("Voltage Min Phase B, Rate D (4117)")
                ("Voltage Min Phase C, Rate D (4118)")
                ("Voltage Average Phase A, Rate D (4119)")
                ("Voltage Average Phase B, Rate D (4120)")
                ("Voltage Average Phase C, Rate D (4121)")
                ("Voltage Max Phase A, Rate D (4122)")
                ("Voltage Max Phase B, Rate D (4123)")
                ("Voltage Max Phase C, Rate D (4124)")
                ("Watts Phase A, Rate D (4150)")
                ("Watts Phase B, Rate D (4151)")
                ("Watts Phase C, Rate D (4152)")
                ("Var Phase A, Rate D (4153)")
                ("Var Phase B, Rate D (4154)")
                ("Var Phase C, Rate D (4155)")
                ("VA Phase A, Rate D (4156)")
                ("VA Phase B, Rate D (4157)")
                ("VA Phase C, Rate D (4158)")
                ("PF degree Phase A, Rate D (4159)")
                ("PF degree Phase B, Rate D (4160)")
                ("PF degree Phase C, Rate D (4161)")
                ("PF Phase A, Rate D (4162)")
                ("PF Phase B, Rate D (4163)")
                ("PF Phase C, Rate D (4164)"));

/**
 * Helper function to generate a channel selection configuration TLV from a set of metric Ids
 * @param metrics
 * @return TLV bytes
 */
std::vector<unsigned char> test_createChannelSelectionConfigurationTlv( RfnChannelConfigurationCommand::MetricIds metrics )
{
    std::vector<unsigned char> result;
    result.push_back(0x01); // tlv type

    const unsigned size = metrics.size() * 2 + 1; // 2 bytes per metrics + the number of metrics

    result.push_back( size >> 8 );   // msb
    result.push_back( size & 0xff ); // lsb

    result.push_back( metrics.size() & 0xff );

    for each( const unsigned metric in metrics )
    {
        result.push_back( metric >> 8 );   // msb
        result.push_back( metric & 0xff ); // lsb
    }

    return result;
}

/**
 * Helper function to generate a channel selection description TLV from a set of metric Ids
 * NOTE: Metric qualifiers are set to zero : Metric qualifiers are tested in detail in separate test
 * @param metrics
 * @return TLV bytes
 */
std::vector<unsigned char> test_createChannelSelectionDescriptionTlv( RfnChannelConfigurationCommand::MetricIds metrics )
{
    std::vector<unsigned char> result;
    result.push_back(0x02); // tlv type

    const unsigned size = metrics.size() * 4 + 1; // 4 bytes per metrics + the number of metrics

    result.push_back( size >> 8 );   // msb
    result.push_back( size & 0xff ); // lsb

    result.push_back( metrics.size() & 0xff );

    for each( const unsigned metric in metrics )
    {
        result.push_back( metric >> 8 );   // msb
        result.push_back( metric & 0xff ); // lsb

        // the metric qualifier is set to zero
        result.push_back( 0 );
        result.push_back( 0 );
    }

    return result;
}

} // anonymous namespace


BOOST_AUTO_TEST_CASE( test_RfnChannelSelectionCommand_helper )
{
    // Verify Unit test helper functions

    const std::vector<RfnChannelConfigurationCommand::MetricIds> metricsTest = list_of
        ( RfnChannelConfigurationCommand::MetricIds() )  // 0 metrics
        ( list_of( 1 ))                                  // 1 metric
        ( list_of( 1 )( 2 ));                            // 2 metrics

    // Test generation of channel selection configuration TLV

    {
        std::vector< std::vector< unsigned char > > rcv;
        for each( const RfnChannelConfigurationCommand::MetricIds & metrics in metricsTest )
        {
            rcv.push_back( test_createChannelSelectionConfigurationTlv( metrics ));
        }

        // tlv_type (1-byte) + tlv size (2-bytes) + number of metrics (1-byte) + metrics (2-bytes each)
        const std::vector< std::vector< unsigned char > > exp = list_of
            (list_of(0x01)  (0x00)(0x01)  (0x00))
            (list_of(0x01)  (0x00)(0x03)  (0x01)  (0x00)(0x01))
            (list_of(0x01)  (0x00)(0x05)  (0x02)  (0x00)(0x01)  (0x00)(0x02));

         BOOST_CHECK_EQUAL_COLLECTIONS(
                 rcv.begin(), rcv.end(),
                 exp.begin(), exp.end());
    }

    // Test generation of channel selection description TLV

    {
        std::vector< std::vector< unsigned char > > rcv;
        for each( const RfnChannelConfigurationCommand::MetricIds & metrics in metricsTest )
        {
            rcv.push_back( test_createChannelSelectionDescriptionTlv( metrics ));
        }

        // tlv_type (1-byte) + tlv size (2-bytes) + number of metrics (1-byte) + metric (2-bytes) + descriptor (2-bytes)
        const std::vector< std::vector< unsigned char > > exp = list_of
            (list_of(0x02)  (0x00)(0x01)  (0x00))
            (list_of(0x02)  (0x00)(0x05)  (0x01)  (0x00)(0x01)(0x00)(0x00))
            (list_of(0x02)  (0x00)(0x09)  (0x02)  (0x00)(0x01)(0x00)(0x00)  (0x00)(0x02)(0x00)(0x00));

         BOOST_CHECK_EQUAL_COLLECTIONS(
                 rcv.begin(), rcv.end(),
                 exp.begin(), exp.end());
    }
}


BOOST_AUTO_TEST_CASE( test_RfnSetChannelSelectionCommand_allMetrics )
{
    // execute
    {
        std::vector< RfnCommand::RfnRequestPayload > execute_rcv, execute_exp;

        for each( const RfnChannelConfigurationCommand::MetricIds& metrics in metricsAll )
        {
            // actual
            {
                RfnSetChannelSelectionCommand cmd( metrics );

                execute_rcv.push_back( cmd.executeCommand( execute_time ));
            }

            // expected
            {
                const std::vector<unsigned char> tlv_bytes_exp = test_createChannelSelectionConfigurationTlv( metrics );

                RfnCommand::RfnRequestPayload exp_bytes = list_of(0x78)(0x00)(0x01);  // command code + operation + 1 tlv

                exp_bytes.insert( exp_bytes.end(), tlv_bytes_exp.begin(), tlv_bytes_exp.end() );
                execute_exp.push_back( exp_bytes );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                execute_rcv.begin(), execute_rcv.end(),
                execute_exp.begin(), execute_exp.end());
    }

    // decode
    {
        std::vector< std::string > descriptions_rcv, descriptions_exp;
        std::vector< RfnChannelConfigurationCommand::MetricIds > metrics_rcv, metrics_exp;

        BOOST_REQUIRE( metricsAll.size() == descriptionsAll.size() );

        unsigned descNbr = 0;
        for each( const RfnChannelConfigurationCommand::MetricIds& metrics in metricsAll )
        {
            // actual
            {
                RfnSetChannelSelectionCommand cmd( metrics );

                std::vector<unsigned char> response = list_of(0x79)(0x00)(0X00)(0x01);    // command code + operation + status + 1 tlv

                // create the response
                {
                    const std::vector<unsigned char> tlv_channel_description = test_createChannelSelectionDescriptionTlv( metrics );

                    response.insert( response.end(), tlv_channel_description.begin(), tlv_channel_description.end());
                }

                RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

                descriptions_rcv.push_back( rcv.description );
                metrics_rcv.push_back( cmd.getMetricsReceived() );
            }

            // expected
            {
                const std::vector<std::string> descriptions = descriptionsAll[descNbr++];

                std::string desc_exp =
                        "Status: Success (0)\n"
                        "Channel Registration Full Description:\n"
                        "Metric(s) descriptors:\n";

                for each( const std::string & desc in descriptions )
                {
                    desc_exp += desc + ": Scaling Factor: 1\n";
                }

                descriptions_exp.push_back( desc_exp );
                metrics_exp.push_back( metrics );
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                descriptions_rcv.begin(), descriptions_rcv.end(),
                descriptions_exp.begin(), descriptions_exp.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
                metrics_rcv.begin(), metrics_rcv.end(),
                metrics_exp.begin(), metrics_exp.end());
    }
}


BOOST_AUTO_TEST_CASE( test_RfnGetChannelSelectionCommand )
{
    // execute
    {
        RfnGetChannelSelectionCommand cmd;

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        RfnCommand::RfnRequestPayload exp = list_of(0x78)(0x01)(0x00);  // command code + operation + 0 tlv

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector< std::string > descriptions_rcv, descriptions_exp;
        std::vector< RfnChannelConfigurationCommand::MetricIds > metrics_rcv, metrics_exp;

        BOOST_REQUIRE( metricsAll.size() == descriptionsAll.size() );

        unsigned descNbr = 0;
        for each( const RfnChannelConfigurationCommand::MetricIds& metrics in metricsAll )
        {
            // actual
            {
                RfnGetChannelSelectionCommand cmd;

                std::vector<unsigned char> response = list_of(0x79)(0x01)(0X00)(0x01); // command code + operation + status + 1 tlv

                // create the response
                {
                    const std::vector<unsigned char> tlv_channel_config = test_createChannelSelectionConfigurationTlv( metrics );

                    response.insert( response.end(), tlv_channel_config.begin(), tlv_channel_config.end());
                }

                RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

                descriptions_rcv.push_back( rcv.description );
                metrics_rcv.push_back( cmd.getMetricsReceived() );
            }

            // expected
            {
                const std::vector<std::string> descriptions = descriptionsAll[descNbr++];

                std::string desc_exp =
                        "Status: Success (0)\n"
                        "Channel Selection Configuration:\n"
                        "Metric(s) list:\n";

                for each( const std::string & desc in descriptions )
                {
                    desc_exp += desc + "\n";
                }

                descriptions_exp.push_back( desc_exp );
                metrics_exp.push_back( RfnChannelConfigurationCommand::MetricIds() );  //  empty
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                descriptions_rcv.begin(), descriptions_rcv.end(),
                descriptions_exp.begin(), descriptions_exp.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
                metrics_rcv.begin(), metrics_rcv.end(),
                metrics_exp.begin(), metrics_exp.end());
    }
}


BOOST_AUTO_TEST_CASE( test_RfnGetChannelSelectionFullDescriptionCommand )
{
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
                (0x79)(0x02)(0X00)(0x01)  // command code + operation + status + 1 tlv
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
                (0x00)(0x01)(0x00)(0x10)  // Coincident Value - 2 // test duplicate id has concident value
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
                "Watt hour delivered (1): Coincident Value 2, Scaling Factor: 1\n" // test duplicate id has concident value
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

        const RfnChannelConfigurationCommand::MetricIds metricsExpected = boost::assign::list_of
            (0x01)(0x02)(0x03)(0x05)(0x06)(0x07)(0x08)(0x09)
            (0x0a)(0x0b)(0x0c)(0x15)(0x16)(0x17)(0x1f)(0x20)
            (0x21)(0x22)(0x23)(0x29)(0x2a);

        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), metricsExpected );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnSetChannelSelectionCommand_exceptions )
{
    // execute exceptions
    {
        const std::vector< RfnChannelConfigurationCommand::MetricIds > test_metrics = list_of
                (list_of(   1 )(   2 )(   3 )(   4 )(   5 )(  13 )) // invalid Metric
                (list_of(   1 )(   2 )(   3 )(   4 )(   5 )(   6 )(   7 )(   8 )(   9 )(  10 )(  11 )(  12 )(  21 )(  22 )(  23 )(   24 )
                        (  25 )(  26 )(  27 )(  28 )(  29 )(  30 )(  31 )(  32 )(  33 )(  34 )(  35 )(  36 )(  37 )(  41 )(  42 )(   43 )
                        (  44 )(  45 )(  46 )(  47 )(  48 )(  49 )(  50 )(  51 )(  52 )(  61 )(  62 )(  63 )(  64 )(  65 )(  66 )(   67 )
                        (  68 )(  69 )(  70 )(  81 )(  82 )(  83 )( 100 )( 101 )( 102 )( 103 )( 104 )( 105 )( 106 )( 107 )( 108 )(  109 )
                        ( 110 )( 111 )( 112 )( 113 )( 114 )( 115 )( 116 )( 117 )( 118 )( 119 )( 120 )( 121 )( 122 )( 123 )( 124 )(  150 )
                        ( 151 )); // 81 metrics

        const std::vector< RfnCommand::CommandException > exp = list_of
                ( RfnCommand::CommandException( BADPARAM, "Invalid metric id (13)" ))
                ( RfnCommand::CommandException( BADPARAM, "Number of metrics 81, expected <= 80" ));

        std::vector< RfnCommand::CommandException > rcv;

        for each( const RfnChannelConfigurationCommand::MetricIds& metrics in test_metrics )
        {
            try
            {
                 RfnSetChannelSelectionCommand cmd( metrics );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                rcv.push_back(ex);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode exceptions
    {
        const std::vector<RfnCommand::RfnResponsePayload> responses = list_of
                ( list_of(0x79)(0x00)(0X00) )
                ( list_of(0x7a)(0x00)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x01)(0X00)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x00)(0X03)(0x02)  (0x01)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                // tlv types
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x07)(0x00)(0x01)(0x00)                           )
                ( list_of(0x79)(0x00)(0X00)(0x02)  (0x02)(0x00)(0x01)(0x00)  (0x02)(0x00)(0x01)(0x00) )
                // tlv full description
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x02)(0x00)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x02)(0x00)(0x01)(0x03) )
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0xff)(0xff)(0x00)(0x00) )
                // tlv full description - metric qualifier
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x80)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x60)(0x00) )
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x18)(0x00) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x7a)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7), expected (2)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV count (2), expected (1)" ) )
                // tlv full description
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 1, expected 13" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                // tlv full description - metric qualifier
                ( RfnCommand::CommandException( ErrorInvalidData, "Metric qualifier expected extension bit to be zero" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Primary/Secondary\" (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Fund/Harmonic\" (3)" ) );


        const RfnChannelConfigurationCommand::MetricIds metrics;

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            RfnSetChannelSelectionCommand cmd( metrics );

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

                BOOST_ERROR( "cmd.decodeCommand() did not throw with payload " );
                BOOST_ERROR( response );
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
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x01)(0x00)(0x03)(0x01)(0xff)(0xff) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x7a)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x00)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7), expected (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV count (2), expected (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV count (0), expected (1)" ) )
                // tlv channel selection configuration
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 1, expected 7" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) );

        RfnGetChannelSelectionCommand cmd;

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

                BOOST_ERROR( "cmd.decodeCommand() did not throw with payload " );
                BOOST_ERROR( response );
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
                ( list_of(0x7a)(0x02)(0X00)(0x01)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x02)(0x00)(0x01)(0x00) )
                ( list_of(0x79)(0x02)(0X03)(0x01)  (0x02)(0x00)(0x01)(0x00) )
                // tlv types
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x07)(0x01)(0x00)       )
                ( list_of(0x79)(0x02)(0X00)(0x02)  (0x02)(0x00)(0x01)(0x00) (0x02)(0x00)(0x01)(0x00) )
                // tlv full description
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x02)(0x00)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x02)(0x00)(0x01)(0x03) )
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0xff)(0xff)(0x00)(0x00) )
                // tlv full description - metric qualifier
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x80)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x60)(0x00) )
                ( list_of(0x79)(0x02)(0X00)(0x01)  (0x02)(0x00)(0x05)(0x01)(0x00)(0x01)(0x18)(0x00) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x7a)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7), expected (2)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV count (2), expected (1)" ) )
                // tlv full description
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 1, expected 13" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                // tlv full description - metric qualifier
                ( RfnCommand::CommandException( ErrorInvalidData, "Metric qualifier expected extension bit to be zero" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Primary/Secondary\" (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Fund/Harmonic\" (3)" ) );

        std::vector<RfnCommand::CommandException> actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            RfnGetChannelSelectionFullDescriptionCommand cmd;

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

                BOOST_ERROR( "cmd.decodeCommand() did not throw with payload " );
                BOOST_ERROR( response );
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
    RfnChannelConfigurationCommand::MetricIds metrics = list_of
            (  1 )(  2 )(  3 )(  4 )(  5 )(  6 )(  7 )(  8 )(  9 )( 10 )( 11 )( 12 )
            ( 21 )( 22 )( 23 );

    const unsigned intervalRecordingSeconds = 0x12345678;
    const unsigned intervalReportingSeconds = 0xa5a5a5a5;

    RfnChannelIntervalRecording::SetConfigurationCommand cmd( metrics, intervalRecordingSeconds, intervalReportingSeconds );

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
                (0x7b)(0x00)(0X00)(0x01)    // command code + operation + status + 1 tlv
                (0x02)                      // tlv type 2
                (0x3d)                      // tlv size (1-byte)
                (0x0f)                      // number of metrics descriptor
                (0x00)(0x01)(0x00)(0x00)  (0x00)(0x02)(0x00)(0x00)  (0x00)(0x03)(0x00)(0x00)  (0x00)(0x04)(0x00)(0x00)
                (0x00)(0x05)(0x00)(0x00)  (0x00)(0x06)(0x00)(0x00)  (0x00)(0x07)(0x00)(0x00)  (0x00)(0x08)(0x00)(0x00)
                (0x00)(0x09)(0x00)(0x00)  (0x00)(0x0a)(0x00)(0x00)  (0x00)(0x0b)(0x00)(0x00)  (0x00)(0x0c)(0x00)(0x00)
                (0x00)(0x15)(0x00)(0x00)  (0x00)(0x16)(0x00)(0x00)  (0x00)(0x17)(0x00)(0x00);

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Interval Recording Full Description:\n"
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
                "Var hour total/sum (23): Scaling Factor: 1\n";

        BOOST_CHECK_EQUAL( rcv.description, desc_exp );
        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), metrics );
    }
}

BOOST_AUTO_TEST_CASE( test_RfnChannelIntervalRecordingGetConfigurationCommand )
{
    RfnChannelConfigurationCommand::MetricIds zeroMetrics;

    RfnChannelIntervalRecording::GetConfigurationCommand cmd;

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
                (0x7b)(0x01)(0X00)(0x01)    // command code + operation + status + 1 tlv
                (0x01)                      // tlv type
                (0x09)                      // tlv size (1-byte)
                (0x12)(0x34)(0x56)(0x78)    // interval recording
                (0xa5)(0xa5)(0xa5)(0xa5)    // interval reporting
                (0x00);                     // number of metrics

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Interval Recording Configuration:\n"
                "Interval Recording: 305419896 seconds\n"
                "Interval Reporting: 2779096485 seconds\n"
                "Metric(s) list:\n"
                "none\n";

        BOOST_CHECK_EQUAL( rcv.description, desc_exp );
        BOOST_CHECK_EQUAL( cmd.getMetricsReceived(), zeroMetrics );
    }
}


BOOST_AUTO_TEST_CASE( test_RfnChannelIntervalRecordingGetActiveConfigurationCommand )
{
    RfnChannelConfigurationCommand::MetricIds zeroMetrics;

    RfnChannelIntervalRecording::GetActiveConfigurationCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        RfnCommand::RfnRequestPayload exp = list_of
                (0x7a)(0x02)(0x00);          // command code + operation + 0 tlv

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const std::vector<unsigned char> response = list_of
                (0x7b)(0x02)(0X00)(0x01)    // command code + operation + status + 1 tlv
                (0x03)                      // tlv type
                (0x09)                      // tlv size (1-byte)
                (0x12)(0x34)(0x56)(0x78)    // interval recording
                (0xa5)(0xa5)(0xa5)(0xa5)    // interval reporting
                (0x00);                     // number of metrics

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
                "Status: Success (0)\n"
                "Channel Interval Recording Active Configuration:\n"
                "Interval Recording: 305419896 seconds\n"
                "Interval Reporting: 2779096485 seconds\n"
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
        const std::vector<RfnChannelConfigurationCommand::MetricIds> metrics = list_of
                // invalid metric
                (list_of(  1 )(  2 )(  3 )(  4 )(  5 )( 38 ))
                // 16 metrics
                (list_of(  1 )(  2 )(  3 )(  4 )(  5 )(  6 )(  7 )(  8 )(  9 )( 10 )( 11 )( 12 )( 21 )( 22 )( 23 )( 24 ));

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( BADPARAM, "Invalid metric id (38)" ) )
                ( RfnCommand::CommandException( BADPARAM, "Number of metrics 16, expected <= 15" ) );

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnChannelConfigurationCommand::MetricIds & metricIds in metrics )
        {
            try
            {
                RfnChannelIntervalRecording::SetConfigurationCommand cmd( metricIds, 0, 0 );
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
                ( list_of(0x79)(0x00)(0X00)(0x01)  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x01)  (0x02)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X03)(0x01)  (0x02)(0x01)(0x00) )
                // tlv types
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x07)(0x01)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x02)  (0x02)(0x01)(0x00)  (0x02)(0x01)(0x00) )
                // tlv full description
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x02)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x02)(0x01)(0x03) )
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x02)(0x05)(0x01)(0xff)(0xff)(0x00)(0x00) )
                // tlv full description - metric qualifier
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x80)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x60)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x02)(0x05)(0x01)(0x00)(0x01)(0x18)(0x00) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x79)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7), expected (2)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV count (2), expected (1)" ) )
                // tlv full description
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 0, expected >= 1" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for channel descriptors received 1, expected 13" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) )
                // tlv full description - metric qualifier
                ( RfnCommand::CommandException( ErrorInvalidData, "Metric qualifier expected extension bit to be zero" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Primary/Secondary\" (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid metric qualifier value for \"Fund/Harmonic\" (3)" ) );


        const RfnChannelConfigurationCommand::MetricIds metrics;

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            RfnChannelIntervalRecording::SetConfigurationCommand cmd( metrics, 0, 0 );

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

                BOOST_ERROR( "cmd.decodeCommand() did not throw with payload " );
                BOOST_ERROR( response );
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
                ( list_of(0x79)(0x01)(0X00)(0x01)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                ( list_of(0x7b)(0x00)(0X00)(0x01)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                ( list_of(0x7b)(0x01)(0X03)(0x01)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                // tlv types
                ( list_of(0x7b)(0x01)(0X00)(0x01)  (0x07)(0x01)(0x00) )
                ( list_of(0x7b)(0x01)(0X00)(0x02)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00) )
                // tlv interval recording configuration
                ( list_of(0x7b)(0x01)(0X00)(0x01)  (0x01)(0x00)                                                                   )
                ( list_of(0x7b)(0x01)(0X00)(0x01)  (0x01)(0x09)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x01)             )
                ( list_of(0x7b)(0x01)(0X00)(0x01)  (0x01)(0x0b)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x01)(0xff)(0xff) );

        const std::vector<RfnCommand::CommandException> expected = list_of
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Length (3)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x79)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x00)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
                // tlv types
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV of type (7), expected (1)" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Unexpected TLV count (2), expected (1)" ) )
                // tlv channel selection configuration
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for interval recording received 0, expected >= 9" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Number of bytes for list of metric IDs received 1, expected 3" ) )
                ( RfnCommand::CommandException( ErrorInvalidData, "Received unknown metric id (65535)" ) );

        std::vector< RfnCommand::CommandException > actual;

        for each ( const RfnCommand::RfnResponsePayload & response in responses )
        {
            RfnChannelIntervalRecording::GetConfigurationCommand cmd;

            try
            {
                RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

                BOOST_ERROR( "cmd.decodeCommand() did not throw with payload " );
                BOOST_ERROR( response );
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
