#include "resource_helper.h"
#include "std_helper.h"
#include "devicetypes.h"

namespace Cti {

DataBuffer loadResourceFromLibrary( const int resourceId, const char * resourceType, const char * libraryName )
{
    DataBuffer  loadedResource;

    if ( HINSTANCE library = LoadLibrary( libraryName ) )
    {
        if ( HRSRC resourceSearch = FindResource( library, MAKEINTRESOURCE( resourceId ), resourceType ) )
        {
            if ( HGLOBAL resource = LoadResource( library, resourceSearch ) )
            {
                if ( const unsigned char * data = static_cast<unsigned char *>( LockResource( resource ) ) )
                {
                    if ( DWORD size = SizeofResource( library, resourceSearch ) )
                    {
                        loadedResource.assign( data, data + size );
                    }
                    else
                    {
                        DWORD errorCode = GetLastError();

                        throw std::runtime_error((std::ostringstream() 
                            << "Failed to size resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                            << "  (error code: " << errorCode << ")").str());
                    }
                }
                else
                {
                    throw std::runtime_error((std::ostringstream()
                        << "Failed to lock resource: " << resourceId << ", "  << resourceType << " in: " << libraryName).str());
                }
            }
            else
            {
                DWORD errorCode = GetLastError();

                throw std::runtime_error((std::ostringstream()
                    << "Failed to load resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                        << "  (error code: " << errorCode << ")").str());
            }
        }
        else
        {
            DWORD errorCode = GetLastError();

            throw std::runtime_error((std::ostringstream()
                << "Failed to find resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                    << "  (error code: " << errorCode << ")").str());

        }

        if ( ! FreeLibrary( library ) )
        {
            DWORD errorCode = GetLastError();

            throw std::runtime_error((std::ostringstream()
                << "Failed to unload library: " << libraryName
                    << "  (error code: " << errorCode << ")").str());
        }
    }
    else
    {
        DWORD errorCode = GetLastError();

        throw std::runtime_error((std::ostringstream()
            << "Failed to load library: " << libraryName
                << "  (error code: " << errorCode << ")").str());
    }

    return loadedResource;
}


DeviceTypes resolvePaoIdXmlType( const std::string & type )
{
    static const std::map<std::string, DeviceTypes> _lookup
    {
        //  --- GridSmart ---
        { "NEUTRAL_MONITOR",             TYPE_NEUTRAL_MONITOR },
        { "FAULT_CI",                    TYPE_FCI },

        //  --- Capacitor Control ---
        { "CAPBANK",                     TYPE_CAPBANK },
        { "CBC_7010",                    TYPE_CBC7010 },
        { "CBC_7011",                    TYPE_CBC7010 },
        { "CBC_7012",                    TYPE_CBC7010 },
        { "CBC_7020",                    TYPE_CBC7020 },
        { "CBC_7022",                    TYPE_CBC7020 },
        { "CBC_7023",                    TYPE_CBC7020 },
        { "CBC_7024",                    TYPE_CBC7020 },
//      { "cbc 7030",                    TYPE_CBC7020 },
        { "CBC_8020",                    TYPE_CBC8020 },
        { "CBC_8024",                    TYPE_CBC8020 },
        { "CBC_DNP",                     TYPE_CBCDNP },
        { "CBC_LOGICAL",                 TYPE_CBCLOGICAL },
        { "CBC_EXPRESSCOM",              TYPE_EXPRESSCOMCBC },
        { "CBC_FP_2800",                 TYPE_FISHERPCBC },
//      { "cbc versacom",                TYPE_VERSACOMCBC },

        //  --- Voltage Regulator ---
        { "LOAD_TAP_CHANGER",            TYPE_LOAD_TAP_CHANGER },
        { "GANG_OPERATED",               TYPE_GANG_OPERATED_REGULATOR },
        { "PHASE_OPERATED",              TYPE_PHASE_OPERATED_REGULATOR },

        //  --- Cooper PLC ---
//      { "ccu-700",                     TYPE_CCU700 },
        { "CCU710A",                     TYPE_CCU710 },
        { "CCU711",                      TYPE_CCU711 },
        { "CCU721",                      TYPE_CCU721 },
        { "LCR3102",                     TYPELCR3102 },
        { "LMT_2",                       TYPELMT2 },
        { "MCTBROADCAST",                TYPEMCTBCAST },
        { "MCT210",                      TYPEMCT210 },
//      { "mct-212",                     TYPEMCT212 },
        { "MCT213",                      TYPEMCT213 },
//      { "mct-224",                     TYPEMCT224 },
//      { "mct-226",                     TYPEMCT226 },
        { "MCT240",                      TYPEMCT240 },
//      { "mct-242",                     TYPEMCT242 },
        { "MCT248",                      TYPEMCT248 },
        { "MCT250",                      TYPEMCT250 },
        { "MCT310",                      TYPEMCT310 },
        { "MCT310CT",                    TYPEMCT310 },
        { "MCT310ID",                    TYPEMCT310ID },
        { "MCT310IDL",                   TYPEMCT310IDL },
        { "MCT310IL",                    TYPEMCT310IL },
        { "MCT318",                      TYPEMCT318 },
        { "MCT318L",                     TYPEMCT318L },
        { "MCT360",                      TYPEMCT360 },
        { "MCT370",                      TYPEMCT370 },
        { "MCT410CL",                    TYPEMCT410CL },
        { "MCT410FL",                    TYPEMCT410FL },
        { "MCT410GL",                    TYPEMCT410GL },
        { "MCT410IL",                    TYPEMCT410IL },
        { "MCT420CL",                    TYPEMCT420CL },
        { "MCT420CD",                    TYPEMCT420CD },
        { "MCT420FL",                    TYPEMCT420FL },
        { "MCT420FD",                    TYPEMCT420FD },
        { "MCT430A",                     TYPEMCT430A },
        { "MCT430A3",                    TYPEMCT430A3 },
        { "MCT430S4",                    TYPEMCT430S4 },
        { "MCT430SL",                    TYPEMCT430SL },
        { "MCT470",                      TYPEMCT470 },
        { "MCT440_2131B",                TYPEMCT440_2131B },
        { "MCT440_2132B",                TYPEMCT440_2132B },
        { "MCT440_2133B",                TYPEMCT440_2133B },
        { "REPEATER_800",                TYPE_REPEATER800 },
        { "REPEATER_801",                TYPE_REPEATER800 },
        { "REPEATER_850",                TYPE_REPEATER850 },
        { "REPEATER_902",                TYPE_REPEATER900 },
        { "REPEATER_921",                TYPE_REPEATER900 },
        { "REPEATER",                    TYPE_REPEATER900 },

        //  --- Receivers ---
//      { "page receiver",               TYPE_PAGING_RECEIVER },

        //  --- RF mesh meters ---
        { "RFN410FL",                    TYPE_RFN410FL },
        { "RFN410FX",                    TYPE_RFN410FX },
        { "RFN410FD",                    TYPE_RFN410FD },
        { "RFN420FL",                    TYPE_RFN420FL },
        { "RFN420FX",                    TYPE_RFN420FX },
        { "RFN420FD",                    TYPE_RFN420FD },
        { "RFN420FRX",                   TYPE_RFN420FRX },
        { "RFN420FRD",                   TYPE_RFN420FRD },
        { "RFN410CL",                    TYPE_RFN410CL },
        { "RFN420CL",                    TYPE_RFN420CL },
        { "RFN420CD",                    TYPE_RFN420CD },
        { "RFN430A3D",                   TYPE_RFN430A3D },
        { "RFN430A3T",                   TYPE_RFN430A3T },
        { "RFN430A3K",                   TYPE_RFN430A3K },
        { "RFN430A3R",                   TYPE_RFN430A3R },
        { "RFN430KV",                    TYPE_RFN430KV },
        { "RFN430SL0",                   TYPE_RFN430SL0 },
        { "RFN430SL1",                   TYPE_RFN430SL1 },
        { "RFN430SL2",                   TYPE_RFN430SL2 },
        { "RFN430SL3",                   TYPE_RFN430SL3 },
        { "RFN430SL4",                   TYPE_RFN430SL4 },
        { "RFN510FL",                    TYPE_RFN510FL },
        { "RFN520FAX",                   TYPE_RFN520FAX },
        { "RFN520FRX",                   TYPE_RFN520FRX },
        { "RFN520FAXD",                  TYPE_RFN520FAXD },
        { "RFN520FRXD",                  TYPE_RFN520FRXD },
        { "RFN530FAX",                   TYPE_RFN530FAX },
        { "RFN530FRX",                   TYPE_RFN530FRX },
        { "RFN530S4X",                   TYPE_RFN530S4X },
        { "RFN530S4EAX",                 TYPE_RFN530S4EAX },
        { "RFN530S4EAXR",                TYPE_RFN530S4EAXR },
        { "RFN530S4ERX",                 TYPE_RFN530S4ERX },
        { "RFN530S4ERXR",                TYPE_RFN530S4ERXR },

        { "RFN1200", TYPE_RFN1200 },

        //  --- RF water meters ---
        { "RFW201",                      TYPE_RFW201 },

        //  --- RF gas meters ---
        { "RFG201",                      TYPE_RFG201 },
        { "RFG301",                      TYPE_RFG301 },

        //  --- RTU devices ---
        { "RTU_DART",                    TYPE_DARTRTU },
        { "RTU_DNP",                     TYPE_DNPRTU },
        { "RTUILEX",                     TYPE_ILEXRTU },
        { "SERIES_5_LMI",                TYPE_SERIESVLMIRTU },
        { "RTU_MODBUS",                  TYPE_MODBUS },
//      { "rtu-ses92",                   TYPE_SES92RTU },
        { "RTUWELCO",                    TYPE_WELCORTU },

        //  --- GRE { Great River Energy }, transmitters ---
        { "RTC",                         TYPE_RTC },
        { "RTM",                         TYPE_RTM },

        //  --- GRE { Great River Energy }, Load Management groups ---
        { "LM_GROUP_GOLAY",              TYPE_LMGROUP_GOLAY },
//      { "sa-105 group",                TYPE_LMGROUP_SA105 },
        { "LM_GROUP_SA205",              TYPE_LMGROUP_SA205 },
        { "LM_GROUP_SA305",              TYPE_LMGROUP_SA305 },
        { "LM_GROUP_SADIGITAL",          TYPE_LMGROUP_SADIGITAL },

        //  --- Load Management ---
//      { "ci customer",                 TYPE_CI_CUSTOMER },
        { "LM_CONTROL_AREA",             TYPE_LM_CONTROL_AREA },
//      { "lm curtail program",          TYPE_LMPROGRAM_CURTAILMENT },
        { "LM_DIRECT_PROGRAM",           TYPE_LMPROGRAM_DIRECT },
//      { "lm energy exchange",          TYPE_LMPROGRAM_ENERGYEXCHANGE },
        { "LM_SEP_PROGRAM",              TYPE_LMPROGRAM_DIRECT },
        { "LM_GROUP_DIGI_SEP",           TYPE_LMGROUP_DIGI_SEP },
        { "LM_GROUP_ECOBEE",             TYPE_LMGROUP_ECOBEE },
        { "LM_GROUP_EMETCON",            TYPE_LMGROUP_EMETCON },
        { "LM_GROUP_EXPRESSCOMM",        TYPE_LMGROUP_EXPRESSCOM },
        { "LM_GROUP_RFN_EXPRESSCOMM",    TYPE_LMGROUP_RFN_EXPRESSCOM },
        { "LM_GROUP_MCT",                TYPE_LMGROUP_MCT },
        { "LM_GROUP_POINT",              TYPE_LMGROUP_POINT },
        { "LM_GROUP_RIPPLE",             TYPE_LMGROUP_RIPPLE },
        { "LM_GROUP_VERSACOM",           TYPE_LMGROUP_VERSACOM },

        //  --- System ---
        { "MACRO_GROUP",                 TYPE_MACRO },
//      { "script",                      0 },
//      { "simple",                      0 },
        { "SYSTEM",                      TYPE_SYSTEM },
        { "VIRTUAL_SYSTEM",              TYPE_VIRTUAL_SYSTEM },

        //  --- Transmitters ---
        { "LCU415",                      TYPE_LCU415 },
        { "LCU_ER",                      TYPE_LCU415ER },
        { "LCULG",                       TYPE_LCU415LG },
        { "LCU_T3026",                   TYPE_LCUT3026 },
        { "RDS_TERMINAL",                TYPE_RDS },
        { "SNPP_TERMINAL",               TYPE_SNPP },
        { "TAPTERMINAL",                 TYPE_TAPTERM },
        { "TCU5000",                     TYPE_TCU5000 },
        { "TCU5500",                     TYPE_TCU5500 },
        { "TNPP_TERMINAL",               TYPE_TNPP },
        { "WCTP_TERMINAL",               TYPE_WCTP },

        //  --- IEDs and electronic meters ---
        { "ALPHA_A1",                    TYPE_ALPHA_A1 },
        { "ALPHA_A3",                    TYPE_ALPHA_A3 },
        { "ALPHA_PPLUS",                 TYPE_ALPHA_PPLUS },
        { "DAVISWEATHER",                TYPE_DAVIS },
        { "DCT_501",                     TYPEDCT501 },
        { "DR_87",                       TYPE_DR87 },
        { "FOCUS",                       TYPE_FOCUS },
        { "IPC410FL",                    TYPE_IPC_410FL },
        { "IPC420FD",                    TYPE_IPC_420FD },
        { "IPC430S4E",                   TYPE_IPC_430S4E },
        { "IPC430SL",                    TYPE_IPC_430SL },
        { "FULCRUM",                     TYPE_FULCRUM },
        { "ION_7330",                    TYPE_ION7330 },
        { "ION_7700",                    TYPE_ION7700 },
        { "ION_8300",                    TYPE_ION8300 },
        { "KV",                          TYPE_KV2 },
        { "KVII",                        TYPE_KV2 },
        { "LANDISGYRS4",                 TYPE_LGS4 },
        { "QUANTUM",                     TYPE_QUANTUM },
        { "SENTINEL",                    TYPE_SENTINEL },
        { "SIXNET",                      TYPE_SIXNET },
        { "TRANSDATA_MARKV",             TYPE_TDMARKV },
        { "VECTRON",                     TYPE_VECTRON },

        //  --- Ports ---
        //PortTypeRfDa remains unused, separate issue?
        //PortTypeTServerDialback remains unused, ignore for now
        { "DIALOUT_POOL",                PortTypePoolDialout },
        { "LOCAL_DIALBACK",              PortTypeLocalDialBack },
        { "LOCAL_DIALUP",                PortTypeLocalDialup },
        { "LOCAL_DIRECT",                PortTypeLocalDirect },
        { "TCPPORT",                     PortTypeTcp },
        { "TSERVER_DIALUP",              PortTypeTServerDialup },
        { "TSERVER_DIRECT",              PortTypeTServerDirect },
        { "UDP_PORT",                    PortTypeUdp }
    };

    return mapFindOrDefault( _lookup, type, TYPE_NONE );

/*
    Missing...

    LCR6(2|6)00_RFN
    RFWMETER
    ZIGBEE_ENDPOINT
    DIGIGATEWAY
    RFN440_213(1|2|3)TD?
    CAP_CONTROL_((SPECIAL_)?AREA|FEEDER|SUB(BUS|STATION))
    CAPBANKCONTROLLER
    (GANG|PHASE)_OPERATED
    LM_SCENARIO
    MCT310IM
    LOCAL_RADIO
    LOCAL_SHARED
    TSERVER_RADIO
    TSERVER_SHARED
*/
}

}

