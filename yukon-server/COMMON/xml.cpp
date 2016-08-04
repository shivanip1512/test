#include "precompiled.h"

#include "logger.h"
#include "xml.h"
#include "DeviceAttributeLookup.h"
#include "DeviceConfigDescription.h"
#include "resolvers.h"
#include "resource_helper.h"

#include "std_helper.h"


#include <shlwapi.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/sax2/SAX2XMLReader.hpp>
#include <xercesc/sax2/XMLReaderFactory.hpp>
#include <xercesc/sax2/Attributes.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

#include <memory>
#include <algorithm>

#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>
#include <boost/algorithm/string/replace.hpp>
#include <boost/assign.hpp>
#include <boost/lexical_cast.hpp>


namespace Cti {

std::string XmlStringTranscode( const XMLCh * const xcode )
{
    char * local = xercesc::XMLString::transcode( xcode );

    std::string transcodedString( local );

    xercesc::XMLString::release( &local );

    return transcodedString;
}

namespace Logging {

std::string getExceptionCause(const xercesc::XMLException& ex)
{
    return XmlStringTranscode(ex.getMessage());
}

std::string getExceptionCause(const xercesc::SAXException& ex)
{
    return XmlStringTranscode(ex.getMessage());
}

} // namespace Logging

IM_EX_CTIBASE void parseXmlFiles( const std::string & yukonBase )
{
    try
    {
        xercesc::XMLPlatformUtils::Initialize();
    }
    catch ( const xercesc::XMLException & ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex, "** Error during XML initialization");

        return;
    }

    // Do your actual work with Xerces-C++ here.

    char    deviceDefinition[ MAX_PATH ];

    PathCombine( deviceDefinition,
                 yukonBase.c_str(), "Server\\Config\\deviceDefinition.xml");

    DataBuffer  paoDefinitionSchema = loadResourceFromLibrary( Resource_PaoDefinitionXsd, "SCHEMA", "yukon-resource.dll" );
    DataBuffer  paoDefinitionXml    = loadResourceFromLibrary( Resource_PaoDefinitionXml, "XML",    "yukon-resource.dll" );

    DataBuffer  configCategoryDefinitionXml = loadResourceFromLibrary( Resource_ConfigCategoryDefinitionXml, "XML",    "yukon-resource.dll" );
    DataBuffer  configCategorySchema        = loadResourceFromLibrary( Resource_ConfigCategoryDefinitionXsd, "SCHEMA", "yukon-resource.dll" );
    DataBuffer  devConfigCategorySchema     = loadResourceFromLibrary( Resource_DeviceConfigCategoryXsd,     "SCHEMA", "yukon-resource.dll" );

    {   // New scope as living quarters for the auto-pointer -- he needs to be deleted before
        // the call to Terminate()

        // types
        typedef std::auto_ptr<xercesc::MemBufInputSource>   BufferPtr;
        typedef std::auto_ptr<xercesc::SAX2XMLReader>       XmlReader;
        typedef std::auto_ptr<xercesc::DefaultHandler>      EntityHandler;

        /// paoDefinition.xml

        // data
        BufferPtr   paoDefinitionXsdBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &paoDefinitionSchema[0] ),
                                                                            paoDefinitionSchema.size(),
                                                                            "paoDefinition.xsd" ) );

        BufferPtr   paoDefinitionXmlBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &paoDefinitionXml[0] ),
                                                                            paoDefinitionXml.size(),
                                                                            "paoDefinition.xml" ) );

        // reader
        XmlReader   paoDefinitionReader( xercesc::XMLReaderFactory::createXMLReader() );

        // set the grammer and turn on validation in the reader
        paoDefinitionReader->loadGrammar( *paoDefinitionXsdBuffer, xercesc::Grammar::SchemaGrammarType, true );

        paoDefinitionReader->setFeature( xercesc::XMLUni::fgSAX2CoreValidation, true );
        paoDefinitionReader->setFeature( xercesc::XMLUni::fgXercesCacheGrammarFromParse, true );

        // this is where the parsed data will go
        XmlPaoInfoCollection    paoDefinitionInfo;

        // this guy handles the xml entities and fills the collection
        EntityHandler  paoDefinitionHandler( new PaoDefinitionSAX2Handler( paoDefinitionInfo ) );

        // associate the handlers with the reader
        paoDefinitionReader->setContentHandler( paoDefinitionHandler.get() );
        paoDefinitionReader->setErrorHandler( paoDefinitionHandler.get() );

        /// configurationCategoryDefinition.xml

        // data
        BufferPtr   configCategoryXsdBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &configCategorySchema[0] ),
                                                                             configCategorySchema.size(),
                                                                             "configurationCategoryDefinition.xsd" ) );

        BufferPtr   devConfigCategoryXsdBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &devConfigCategorySchema[0] ),
                                                                                devConfigCategorySchema.size(),
                                                                                "deviceConfigurationCategory.xsd" ) );

        BufferPtr   configCategoryDefinitionXmlBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &configCategoryDefinitionXml[0] ),
                                                                                       configCategoryDefinitionXml.size(),
                                                                                       "configurationCategoryDefinition.xml" ) );

        // reader
        XmlReader   configCategoryReader( xercesc::XMLReaderFactory::createXMLReader() );

        // set the grammers and turn on validation in the reader
        configCategoryReader->loadGrammar( *configCategoryXsdBuffer, xercesc::Grammar::SchemaGrammarType, true );
        configCategoryReader->loadGrammar( *devConfigCategoryXsdBuffer, xercesc::Grammar::SchemaGrammarType, true );

        configCategoryReader->setFeature( xercesc::XMLUni::fgSAX2CoreValidation, true );
        configCategoryReader->setFeature( xercesc::XMLUni::fgXercesCacheGrammarFromParse, true );

        // this guy handles the xml entities and delegates to DeviceConfigDescription
        EntityHandler  configCategoryHandler( new DeviceConfigCategorySAX2Handler );

        // associate the handlers with the reader
        configCategoryReader->setContentHandler( configCategoryHandler.get() );
        configCategoryReader->setErrorHandler( configCategoryHandler.get() );

        try
        {
            // parse the paoDefinition.xml file and also the devicedefinition.xml file (if it exists)
            paoDefinitionReader->parse( *paoDefinitionXmlBuffer );
            if ( PathFileExists( deviceDefinition ) )
            {
                paoDefinitionReader->parse( deviceDefinition );
            }

            // parse the device configuration category info
            configCategoryReader->parse( *configCategoryDefinitionXmlBuffer );
        }
        catch ( const xercesc::XMLException & ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, "** XML Error");
            return;
        }
        catch ( const xercesc::SAXParseException & ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, "** XML Parsing Error");
            return;
        }
        catch ( ... )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "** Error: Unexpected Exception");
            return;
        }

////////////////////////////
///

        XmlPaoInfoCollection  flattened;

        while ( ! paoDefinitionInfo.empty() )
        {
            // move all stuff with no inheritance to flattened map...

            for ( XmlPaoInfoCollection::iterator b = paoDefinitionInfo.begin(), e = paoDefinitionInfo.end(); b != e; )
            {
                if ( b->second.inheritsFrom.empty() )
                {
                    // search for current element in inheritsFrom of other elements and merge...

                    for ( XmlPaoInfoCollection::iterator bb = paoDefinitionInfo.begin(), ee = paoDefinitionInfo.end(); bb != ee; ++bb  )
                    {
                        if (  bb->second.inheritsFrom.count( b->first ) != 0 )
                        {
                            // merge over...
                            bb->second.inheritsFrom.insert( b->second.inheritsFrom.begin(), b->second.inheritsFrom.end() );
                            bb->second.configCategories.insert( b->second.configCategories.begin(), b->second.configCategories.end() );
                            bb->second.pointInfo.insert( bb->second.pointInfo.end(),
                                                         b->second.pointInfo.begin(), b->second.pointInfo.end() );
                            bb->second.attributes.insert( b->second.attributes.begin(), b->second.attributes.end() );

                            bb->second.inheritsFrom.erase( b->first );
                        }
                    }

                    flattened.insert( *b );

                    paoDefinitionInfo.erase( b++ );
                }
                else
                {
                    ++b;
                }
            }
        }

        // get rid of all the
        //      - abstract members

        for ( XmlPaoInfoCollection::iterator b = flattened.begin(), e = flattened.end(); b != e; )
        {
            if ( b->second.isAbstract )
            {
                flattened.erase( b++ );
            }
            else
            {
                ++b;
            }
        }

////////////////////////////
///

        for ( XmlPaoInfoCollection::iterator b = flattened.begin(), e = flattened.end(); b != e; ++b )
        {
            DeviceTypes devType = resolvePaoIdXmlType( b->first );

            // grab attributes and search for a matching point...

            for each ( XmlPaoInfo::AttributeMap::value_type a in b->second.attributes )
            {
                const std::string & attributeName   = a.first;
                const std::string & pointName       = a.second;

                // find point

                std::vector<XmlPaoInfo::PointInfo>::iterator    searchItem;

                for ( searchItem = b->second.pointInfo.begin();
                      searchItem != b->second.pointInfo.end() && searchItem->name != pointName;
                      ++searchItem )
                    ;   // <-- empty loop body...  [ find_if() and a lambda would be awesome here... ]

                if ( searchItem != b->second.pointInfo.end() )
                {
                    DeviceAttributeLookup::AddRelation( devType, Attribute::Lookup( attributeName ), searchItem->type, searchItem->offset );
                }
            }
        }

////////////////////////////
///

        for ( XmlPaoInfoCollection::iterator b = flattened.begin(), e = flattened.end(); b != e; ++b )
        {
            DeviceConfigDescription::AddCategoriesForDeviceType( resolvePaoIdXmlType( b->first ), b->second.configCategories );
        }

    }

    xercesc::XMLPlatformUtils::Terminate();
}




PaoDefinitionSAX2Handler::PaoDefinitionSAX2Handler( XmlPaoInfoCollection & paoInfoCollection )
    :   inNameElement( false ),
        paoInfo( paoInfoCollection )
{
    // empty...
}


void PaoDefinitionSAX2Handler::startElement( const XMLCh * const uri,
                                             const XMLCh * const localname,
                                             const XMLCh * const qname,
                                             const xercesc::Attributes &  attrs )
{
    static     std::string currentAttributeName;

    std::string element( XmlStringTranscode( localname ) );

    if ( element == "pao" )
    {
        currentPao.reset();

        for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
        {
            std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

            if ( name == "id" )
            {
                currentPao.ID = XmlStringTranscode( attrs.getValue(i) );
            }

            if ( name == "abstract" )
            {
                currentPao.isAbstract = ( XmlStringTranscode( attrs.getValue(i) ) == "true" );
            }

            if ( name == "inherits" )
            {
                boost::split( currentPao.inheritsFrom, XmlStringTranscode( attrs.getValue(i) ),
                              is_chars{',',' '}, boost::token_compress_on );
            }
        }
    }

    if ( element == "point" )
    {
        currentPoint.name = "<unassigned>";

        for ( XMLSize_t i = 0; i < attrs.getLength(); i++)
        {
            std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

            if ( name == "type" )
            {
                currentPoint.type = resolvePointType( XmlStringTranscode( attrs.getValue(i) ) );
            }

            if ( name == "offset" )
            {
                currentPoint.offset = atoi( XmlStringTranscode( attrs.getValue(i) ).c_str() );
            }
        }
    }

    if ( element == "attribute" )
    {
        for ( XMLSize_t i = 0; i < attrs.getLength(); i++)
        {
            std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

            if ( name == "name" )
            {
                currentAttributeName = XmlStringTranscode( attrs.getValue(i) );
            }
        }
    }

    if ( element == "basicLookup" )
    {
        for ( XMLSize_t i = 0; i < attrs.getLength(); i++)
        {
            std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

            if ( name == "point" )
            {
                currentPao.attributes[ currentAttributeName ] = XmlStringTranscode( attrs.getValue(i) );
            }
        }
    }

    if ( element == "category" )
    {
        for ( XMLSize_t i = 0; i < attrs.getLength(); i++)
        {
            std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

            if ( name == "type" )
            {
                currentPao.configCategories.insert( XmlStringTranscode( attrs.getValue(i) ) );
            }
        }
    }

    if ( element == "name" )
    {
        inNameElement = true;
    }
}


void PaoDefinitionSAX2Handler::endElement( const XMLCh * const uri,
                                           const XMLCh * const localname,
                                           const XMLCh * const qname )
{
    std::string element( XmlStringTranscode( localname ) );

    if ( element == "pao" )
    {
        // if not in the map, add it - if it is then merge the new data with the existing elements...

        std::pair<XmlPaoInfoCollection::iterator, bool> doInsert =
            paoInfo.insert( std::make_pair( currentPao.ID, currentPao ) );

        if ( ! doInsert.second )
        {
            XmlPaoInfo  & entry = doInsert.first->second;

            entry.inheritsFrom.insert( currentPao.inheritsFrom.begin(), currentPao.inheritsFrom.end() );
            entry.configCategories.insert( currentPao.configCategories.begin(), currentPao.configCategories.end() );
            entry.pointInfo.insert( entry.pointInfo.end(),
                                     currentPao.pointInfo.begin(), currentPao.pointInfo.end() );
            entry.attributes.insert( currentPao.attributes.begin(), currentPao.attributes.end() );
        }
    }

    if ( element == "point" )
    {
        currentPao.pointInfo.push_back( currentPoint );
    }

    if ( element == "name" )
    {
        inNameElement = false;
    }
}


void PaoDefinitionSAX2Handler::characters( const XMLCh * const chars,
                                           const XMLSize_t     length )
{
    if ( inNameElement )
    {
        currentPoint.name = XmlStringTranscode( chars );
    }
}


void PaoDefinitionSAX2Handler::fatalError( const xercesc::SAXParseException & ex )
{
    const std::string message(XmlStringTranscode(ex.getMessage()));
    const unsigned line = ex.getLineNumber();

    CTILOG_FATAL(dout, "Fatal Error: " << message << " at line: " << line);
}



DeviceTypes resolvePaoIdXmlType( const std::string & type )
{
    typedef std::map<std::string, DeviceTypes>  XmlPaoIdLookup;

    static const XmlPaoIdLookup _lookup = boost::assign::map_list_of<std::string, DeviceTypes>
        //  --- GridSmart ---
        ("NEUTRAL_MONITOR",             TYPE_NEUTRAL_MONITOR)
        ("FAULT_CI",                    TYPE_FCI)

        //  --- Capacitor Control ---
        ("CAPBANK",                     TYPECAPBANK)
        ("DNP_CBC_6510",                TYPECBC6510)
        ("CBC_7010",                    TYPECBC7010)
        ("CBC_7011",                    TYPECBC7010)
        ("CBC_7012",                    TYPECBC7010)
        ("CBC_7020",                    TYPECBC7020)
        ("CBC_7022",                    TYPECBC7020)
        ("CBC_7023",                    TYPECBC7020)
        ("CBC_7024",                    TYPECBC7020)
//    ("cbc 7030",                    TYPECBC7020)
        ("CBC_8020",                    TYPECBC8020)
        ("CBC_8024",                    TYPECBC8020)
        ("CBC_DNP",                     TYPECBCDNP)
        ("CBC_EXPRESSCOM",              TYPEEXPRESSCOMCBC)
        ("CBC_FP_2800",                 TYPEFISHERPCBC)
//    ("cbc versacom",                TYPEVERSACOMCBC)

        //  --- Voltage Regulator ---
        ("LOAD_TAP_CHANGER",            TYPE_LOAD_TAP_CHANGER)
        ("GANG_OPERATED",               TYPE_GANG_OPERATED_REGULATOR)
        ("PHASE_OPERATED",              TYPE_PHASE_OPERATED_REGULATOR)

        //  --- Cooper PLC ---
//    ("ccu-700",                     TYPE_CCU700)
        ("CCU710A",                     TYPE_CCU710)
        ("CCU711",                      TYPE_CCU711)
        ("CCU721",                      TYPE_CCU721)
        ("LCR3102",                     TYPELCR3102)
        ("LMT_2",                       TYPELMT2)
        ("MCTBROADCAST",                TYPEMCTBCAST)
        ("MCT210",                      TYPEMCT210)
//    ("mct-212",                     TYPEMCT212)
        ("MCT213",                      TYPEMCT213)
//    ("mct-224",                     TYPEMCT224)
//    ("mct-226",                     TYPEMCT226)
        ("MCT240",                      TYPEMCT240)
//    ("mct-242",                     TYPEMCT242)
        ("MCT248",                      TYPEMCT248)
        ("MCT250",                      TYPEMCT250)
        ("MCT310",                      TYPEMCT310)
        ("MCT310CT",                    TYPEMCT310)
        ("MCT310ID",                    TYPEMCT310ID)
        ("MCT310IDL",                   TYPEMCT310IDL)
        ("MCT310IL",                    TYPEMCT310IL)
        ("MCT318",                      TYPEMCT318)
        ("MCT318L",                     TYPEMCT318L)
        ("MCT360",                      TYPEMCT360)
        ("MCT370",                      TYPEMCT370)
        ("MCT410CL",                    TYPEMCT410CL)
        ("MCT410FL",                    TYPEMCT410FL)
        ("MCT410GL",                    TYPEMCT410GL)
        ("MCT410IL",                    TYPEMCT410IL)
        ("MCT420CL",                    TYPEMCT420CL)
        ("MCT420CD",                    TYPEMCT420CD)
        ("MCT420FL",                    TYPEMCT420FL)
        ("MCT420FD",                    TYPEMCT420FD)
        ("MCT430A",                     TYPEMCT430A)
        ("MCT430A3",                    TYPEMCT430A3)
        ("MCT430S4",                    TYPEMCT430S4)
        ("MCT430SL",                    TYPEMCT430SL)
        ("MCT470",                      TYPEMCT470)
        ("MCT440_2131B",                TYPEMCT440_2131B)
        ("MCT440_2132B",                TYPEMCT440_2132B)
        ("MCT440_2133B",                TYPEMCT440_2133B)
        ("REPEATER_800",                TYPE_REPEATER800)
        ("REPEATER_801",                TYPE_REPEATER800)
        ("REPEATER_850",                TYPE_REPEATER850)
        ("REPEATER_902",                TYPE_REPEATER900)
        ("REPEATER_921",                TYPE_REPEATER900)
        ("REPEATER",                    TYPE_REPEATER900)

        //  --- Receivers ---
//    ("page receiver",               TYPE_PAGING_RECEIVER)

        //  --- RF mesh meters ---
        ("RFN410FL",                    TYPE_RFN410FL)
        ("RFN410FX",                    TYPE_RFN410FX)
        ("RFN410FD",                    TYPE_RFN410FD)
        ("RFN420FL",                    TYPE_RFN420FL)
        ("RFN420FX",                    TYPE_RFN420FX)
        ("RFN420FD",                    TYPE_RFN420FD)
        ("RFN420FRX",                   TYPE_RFN420FRX)
        ("RFN420FRD",                   TYPE_RFN420FRD)
        ("RFN410CL",                    TYPE_RFN410CL)
        ("RFN420CL",                    TYPE_RFN420CL)
        ("RFN420CD",                    TYPE_RFN420CD)
        ("RFN430A3D",                   TYPE_RFN430A3D)
        ("RFN430A3T",                   TYPE_RFN430A3T)
        ("RFN430A3K",                   TYPE_RFN430A3K)
        ("RFN430A3R",                   TYPE_RFN430A3R)
        ("RFN430KV",                    TYPE_RFN430KV)
        ("RFN430SL0",                   TYPE_RFN430SL0)
        ("RFN430SL1",                   TYPE_RFN430SL1)
        ("RFN430SL2",                   TYPE_RFN430SL2)
        ("RFN430SL3",                   TYPE_RFN430SL3)
        ("RFN430SL4",                   TYPE_RFN430SL4)
        ("RFN510FL",                    TYPE_RFN510FL)
        ("RFN520FAX",                   TYPE_RFN520FAX)
        ("RFN520FRX",                   TYPE_RFN520FRX)
        ("RFN520FAXD",                  TYPE_RFN520FAXD)
        ("RFN520FRXD",                  TYPE_RFN520FRXD)
        ("RFN530FAX",                   TYPE_RFN530FAX)
        ("RFN530FRX",                   TYPE_RFN530FRX)
        ("RFN530S4X",                   TYPE_RFN530S4X)
        ("RFN530S4EAD",                 TYPE_RFN530S4EAD)
        ("RFN530S4EAT",                 TYPE_RFN530S4EAT)
        ("RFN530S4ERD",                 TYPE_RFN530S4ERD)
        ("RFN530S4ERT",                 TYPE_RFN530S4ERT)

        ("RFN1200",                     TYPE_RFN1200)

        //  --- RTU devices ---
        ("RTU_DART",                    TYPE_DARTRTU)
        ("RTU_DNP",                     TYPE_DNPRTU)
        ("RTUILEX",                     TYPE_ILEXRTU)
        ("SERIES_5_LMI",                TYPE_SERIESVLMIRTU)
        ("RTU_MODBUS",                  TYPE_MODBUS)
//    ("rtu-ses92",                   TYPE_SES92RTU)
        ("RTUWELCO",                    TYPE_WELCORTU)

    //  --- GRE (Great River Energy) transmitters ---
        ("RTC",                         TYPE_RTC)
        ("RTM",                         TYPE_RTM)

        //  --- GRE (Great River Energy) Load Management groups ---
        ("LM_GROUP_GOLAY",              TYPE_LMGROUP_GOLAY)
//    ("sa-105 group",                TYPE_LMGROUP_SA105)
        ("LM_GROUP_SA205",              TYPE_LMGROUP_SA205)
        ("LM_GROUP_SA305",              TYPE_LMGROUP_SA305)
        ("LM_GROUP_SADIGITAL",          TYPE_LMGROUP_SADIGITAL)

        //  --- Load Management ---
//    ("ci customer",                 TYPE_CI_CUSTOMER)
        ("LM_CONTROL_AREA",             TYPE_LM_CONTROL_AREA)
//    ("lm curtail program",          TYPE_LMPROGRAM_CURTAILMENT)
        ("LM_DIRECT_PROGRAM",           TYPE_LMPROGRAM_DIRECT)
//    ("lm energy exchange",          TYPE_LMPROGRAM_ENERGYEXCHANGE)
        ("LM_SEP_PROGRAM",              TYPE_LMPROGRAM_DIRECT)
        ("LM_GROUP_DIGI_SEP",           TYPE_LMGROUP_DIGI_SEP)
        ("LM_GROUP_ECOBEE",             TYPE_LMGROUP_ECOBEE)
        ("LM_GROUP_EMETCON",            TYPE_LMGROUP_EMETCON)
        ("LM_GROUP_EXPRESSCOMM",        TYPE_LMGROUP_EXPRESSCOM)
        ("LM_GROUP_RFN_EXPRESSCOMM",    TYPE_LMGROUP_RFN_EXPRESSCOM)
        ("LM_GROUP_MCT",                TYPE_LMGROUP_MCT)
        ("LM_GROUP_POINT",              TYPE_LMGROUP_POINT)
        ("LM_GROUP_RIPPLE",             TYPE_LMGROUP_RIPPLE)
        ("LM_GROUP_VERSACOM",           TYPE_LMGROUP_VERSACOM)

        //  --- System ---
        ("MACRO_GROUP",                 TYPE_MACRO)
//    ("script",                      0)
//    ("simple",                      0)
        ("SYSTEM",                      TYPE_SYSTEM)
        ("VIRTUAL_SYSTEM",              TYPE_VIRTUAL_SYSTEM)

        //  --- Transmitters ---
        ("LCU415",                      TYPE_LCU415)
        ("LCU_ER",                      TYPE_LCU415ER)
        ("LCULG",                       TYPE_LCU415LG)
        ("LCU_T3026",                   TYPE_LCUT3026)
        ("RDS_TERMINAL",                TYPE_RDS)
        ("SNPP_TERMINAL",               TYPE_SNPP)
        ("TAPTERMINAL",                 TYPE_TAPTERM)
        ("TCU5000",                     TYPE_TCU5000)
        ("TCU5500",                     TYPE_TCU5500)
        ("TNPP_TERMINAL",               TYPE_TNPP)
        ("WCTP_TERMINAL",               TYPE_WCTP)

        //  --- IEDs and electronic meters ---
        ("ALPHA_A1",                    TYPE_ALPHA_A1)
        ("ALPHA_A3",                    TYPE_ALPHA_A3)
        ("ALPHA_PPLUS",                 TYPE_ALPHA_PPLUS)
        ("DAVISWEATHER",                TYPE_DAVIS)
        ("DCT_501",                     TYPEDCT501)
        ("DR_87",                       TYPE_DR87)
        ("FOCUS",                       TYPE_FOCUS)
        ("IPC410FL",                    TYPE_IPC_410FL)
        ("IPC420FD",                    TYPE_IPC_420FD)
        ("IPC430S4E",                   TYPE_IPC_430S4E)
        ("IPC430SL",                    TYPE_IPC_430SL)
        ("FULCRUM",                     TYPE_FULCRUM)
        ("ION_7330",                    TYPE_ION7330)
        ("ION_7700",                    TYPE_ION7700)
        ("ION_8300",                    TYPE_ION8300)
        ("KV",                          TYPE_KV2)
        ("KVII",                        TYPE_KV2)
        ("LANDISGYRS4",                 TYPE_LGS4)
        ("QUANTUM",                     TYPE_QUANTUM)
        ("SENTINEL",                    TYPE_SENTINEL)
        ("SIXNET",                      TYPE_SIXNET)
        ("TRANSDATA_MARKV",             TYPE_TDMARKV)
        ("VECTRON",                     TYPE_VECTRON)
            ;

    XmlPaoIdLookup::const_iterator result = _lookup.find( type );

    if ( result != _lookup.end() )
    {
        return result->second;
    }

    return TYPE_NONE;
}


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
*/




void DeviceConfigCategorySAX2Handler::startElement( const XMLCh * const uri,
                                                    const XMLCh * const localname,
                                                    const XMLCh * const qname,
                                                    const xercesc::Attributes &  attrs )
{
    typedef DeviceConfigCategorySAX2Handler This;
    typedef boost::function<void (This *, const xercesc::Attributes &)> StartElementHandler;

    static const std::map<std::string, StartElementHandler> elementHandlers = boost::assign::map_list_of
        ("category", &This::setCurrentCategory)
        ("enum",     &This::insertField)
        ("boolean",  &This::insertField)
        ("integer",  &This::insertField)
        ("float",    &This::insertField)
        ("entry",    &This::insertTimeRateField)  //  <--- These two need to be converted to indexed fields
        ("map",      &This::setMapEntryPrefix)    //  <---
        ("indexed",  &This::pushIndexedField);

    const boost::optional<StartElementHandler> elementHandler = Cti::mapFind( elementHandlers, XmlStringTranscode( localname ) );

    if ( elementHandler )
    {
        (*elementHandler)(this, attrs);
    }
}


void DeviceConfigCategorySAX2Handler::endElement( const XMLCh * const uri,
                                                  const XMLCh * const localname,
                                                  const XMLCh * const qname )
{
    typedef DeviceConfigCategorySAX2Handler This;
    typedef boost::function<void (This *)> EndElementHandler;

    static const std::map<std::string, EndElementHandler> elementHandlers = boost::assign::map_list_of
        ("indexed", &This::popIndexedField);

    const boost::optional<EndElementHandler> elementHandler = Cti::mapFind( elementHandlers, XmlStringTranscode( localname ) );

    if ( elementHandler )
    {
        (*elementHandler)(this);
    }
}


boost::optional<std::string> findAttribute( const xercesc::Attributes & attrs, const std::string & attributeName )
{
    for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
    {
        std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

        if ( name == attributeName )
        {
            return XmlStringTranscode( attrs.getValue(i) );
        }
    }

    return boost::none;
}


void DeviceConfigCategorySAX2Handler::setCurrentCategory( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> typeAttr = findAttribute( attrs, "type" );

    if ( typeAttr )
    {
        currentContainer.assign( 1, DeviceConfigDescription::AddCategory( *typeAttr ) );
    }
}

void DeviceConfigCategorySAX2Handler::addField( const std::string & fieldName )
{
    if ( ! currentContainer.empty() )
    {
        DeviceConfigDescription::AddItem( currentContainer.back(), fieldName );
    }
}

void DeviceConfigCategorySAX2Handler::insertField( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> fieldAttr = findAttribute( attrs, "field" );

    if ( fieldAttr )
    {
        addField( *fieldAttr );
    }
}

void DeviceConfigCategorySAX2Handler::setMapEntryPrefix( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> fieldAttr = findAttribute( attrs, "field" );

    if ( fieldAttr )
    {
        mapEntryPrefix = *fieldAttr;
    }
}

void DeviceConfigCategorySAX2Handler::insertTimeRateField( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> fieldAttr = findAttribute( attrs, "field" );

    if ( fieldAttr )
    {
        // entrys are of the form "time0"  -- we need to add a time and rate entry...

        // insert the time one...

        addField( mapEntryPrefix + *fieldAttr );

        // replace the word time with the word rate

        std::string rateEntry = boost::algorithm::replace_head_copy( *fieldAttr, 4, "rate" );

        addField( mapEntryPrefix + rateEntry );
    }
}

void DeviceConfigCategorySAX2Handler::pushIndexedField( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> fieldAttr = findAttribute( attrs, "field" );
    const boost::optional<std::string> minAttr   = findAttribute( attrs, "minOccurs" );
    const boost::optional<std::string> maxAttr   = findAttribute( attrs, "maxOccurs" );

    if ( ! currentContainer.empty() && fieldAttr )
    {
        boost::optional<unsigned> minOccurs, maxOccurs;

        if ( minAttr )
        {
            try
            {
                minOccurs = boost::lexical_cast<unsigned>( *minAttr );
            }
            catch(const boost::bad_lexical_cast &)
            {
            }
        }

        if ( maxAttr )
        {
            try
            {
                maxOccurs = boost::lexical_cast<unsigned>( *maxAttr );
            }
            catch(const boost::bad_lexical_cast &)
            {
            }
        }

        currentContainer.push_back(
                DeviceConfigDescription::AddIndexedItem(
                        currentContainer.back(),
                        *fieldAttr,
                        minOccurs, maxOccurs ) );
    }
}

void DeviceConfigCategorySAX2Handler::popIndexedField()
{
    if ( ! currentContainer.empty() )
    {
        currentContainer.pop_back();
    }
}


void DeviceConfigCategorySAX2Handler::fatalError( const xercesc::SAXParseException & ex )
{
    const std::string message( XmlStringTranscode( ex.getMessage() ) );
    const unsigned line = ex.getLineNumber();

    CTILOG_FATAL(dout, "Fatal Error: " << message << " at line: " << line);
}


}

