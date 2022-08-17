#include "precompiled.h"

#include "xml.h"

#include "logger.h"
#include "DeviceAttributeLookup.h"
#include "DeviceConfigDescription.h"
#include "resource_ids.h"
#include "resource_helper.h"
#include "xml_parsers.h"
#include "error.h"
#include "resolvers.h"

#include <shlwapi.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/sax2/SAX2XMLReader.hpp>
#include <xercesc/sax2/XMLReaderFactory.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

#include <boost/range/algorithm/find_if.hpp>
#include <boost/algorithm/string/case_conv.hpp>

namespace Cti {

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

void parseXmlFiles( const std::string & yukonBase )
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

    {   // New scope as living quarters for the auto-pointers -- they need to be deleted before
        // the call to Terminate()

        // types
        typedef std::unique_ptr<xercesc::MemBufInputSource>   BufferPtr;
        typedef std::unique_ptr<xercesc::SAX2XMLReader>       XmlReader;
        typedef std::unique_ptr<xercesc::DefaultHandler>      EntityHandler;

        // read index

        ResourceIndex   resourceIndex;
        {
            DataBuffer  rawDataBuffer
                = loadResourceFromLibrary( RC_INDEX_ID, "XML", "yukon-resource.dll" );

            BufferPtr   memoryBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &rawDataBuffer[0] ),
                                                                      rawDataBuffer.size(),
                                                                      "resource_index.xml" ) );

            XmlReader   reader( xercesc::XMLReaderFactory::createXMLReader() );

            // this guy handles the xml entities and fills the collection
            EntityHandler  handler( new ResourceIndexHandler( resourceIndex ) );

            // associate the handlers with the reader
            reader->setContentHandler( handler.get() );
            reader->setErrorHandler( handler.get() );

            try
            {
                reader->parse( *memoryBuffer );
            }
            catch ( const xercesc::XMLException & ex )
            {
                CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Error" );
                return;
            }
            catch ( const xercesc::SAXParseException & ex )
            {
                CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Parsing Error" );
                return;
            }
            catch ( ... )
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR( dout, "** Error: Unexpected Exception" );
                return;
            }
        }

        // read device config info

        {
            DataBuffer  configCategorySchema
                = loadResourceFromLibrary( resourceIndex.right.at( "configurationcategorydefinition.xsd" ), "XSD", "yukon-resource.dll" );

            BufferPtr   configCategoryXsdBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &configCategorySchema[0] ),
                                                                                 configCategorySchema.size(),
                                                                                 "configurationcategorydefinition.xsd" ) );

            DataBuffer  devConfigCategorySchema
                = loadResourceFromLibrary( resourceIndex.right.at( "deviceconfigurationcategory.xsd" ), "XSD", "yukon-resource.dll" );

            BufferPtr   devConfigCategoryXsdBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &devConfigCategorySchema[0] ),
                                                                                    devConfigCategorySchema.size(),
                                                                                    "deviceconfigurationcategory.xsd" ) );
            // reader
            XmlReader   reader( xercesc::XMLReaderFactory::createXMLReader() );

            // set the grammers and turn on validation in the reader
            reader->loadGrammar( *configCategoryXsdBuffer, xercesc::Grammar::SchemaGrammarType, true );
            reader->loadGrammar( *devConfigCategoryXsdBuffer, xercesc::Grammar::SchemaGrammarType, true );

            reader->setFeature( xercesc::XMLUni::fgSAX2CoreValidation, true );
            reader->setFeature( xercesc::XMLUni::fgXercesCacheGrammarFromParse, true );

            // this guy handles the xml entities and delegates to DeviceConfigDescription
            EntityHandler  handler( new DeviceConfigCategoryHandler );

            // associate the handlers with the reader
            reader->setContentHandler( handler.get() );
            reader->setErrorHandler( handler.get() );

            DataBuffer  configCategoryDefinitionXml
                = loadResourceFromLibrary( resourceIndex.right.at( "configurationcategorydefinition.xml" ), "XML", "yukon-resource.dll" );

            BufferPtr   configCategoryDefinitionXmlBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &configCategoryDefinitionXml[0] ),
                                                                                           configCategoryDefinitionXml.size(),
                                                                                           "configurationcategorydefinition.xml" ) );
            try
            {
                reader->parse( *configCategoryDefinitionXmlBuffer );
            }
            catch ( const xercesc::XMLException & ex )
            {
                CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Error" );
                return;
            }
            catch ( const xercesc::SAXParseException & ex )
            {
                CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Parsing Error" );
                return;
            }
            catch ( ... )
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR( dout, "** Error: Unexpected Exception" );
                return;
            }
        }

        // read paos

        BasicPaoInfoCollection  paos;
        {
            DataBuffer  rawDataBuffer
                = loadResourceFromLibrary( resourceIndex.right.at( "pao.xsd" ), "XSD", "yukon-resource.dll" );

            BufferPtr   memoryBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &rawDataBuffer[0] ),
                                                                      rawDataBuffer.size(),
                                                                      "pao.xsd" ) );

            XmlReader   reader( xercesc::XMLReaderFactory::createXMLReader() );

            // set the grammer and turn on validation in the reader
            reader->loadGrammar( *memoryBuffer, xercesc::Grammar::SchemaGrammarType, true );

            reader->setFeature( xercesc::XMLUni::fgSAX2CoreValidation, true );
            reader->setFeature( xercesc::XMLUni::fgXercesCacheGrammarFromParse, true );

            // this guy handles the xml entities and fills the collection
            EntityHandler  handler( new PaoHandler( paos ) );

            // associate the handlers with the reader
            reader->setContentHandler( handler.get() );
            reader->setErrorHandler( handler.get() );

            for ( int index = RC_PAO_START_ID; index < RC_PAO_END_ID; index++ )
            {
                DataBuffer  paoXml
                    = loadResourceFromLibrary( index, "XML", "yukon-resource.dll" );

                BufferPtr   paoXmlBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &paoXml[0] ),
                                                                          paoXml.size(),
                                                                          resourceIndex.left.at( index ).c_str() ) );
                try
                {
                    reader->parse( *paoXmlBuffer );
                }
                catch ( const xercesc::XMLException & ex )
                {
                    CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Error" );
                    return;
                }
                catch ( const xercesc::SAXParseException & ex )
                {
                    CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Parsing Error" );
                    return;
                }
                catch ( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR( dout, "** Error: Unexpected Exception" );
                    return;
                }
            }
        }

        // read points

        BasicPointInfoCollection    points;
        {
            DataBuffer  rawDataBuffer
                = loadResourceFromLibrary( resourceIndex.right.at( "points.xsd" ), "XSD", "yukon-resource.dll" );

            BufferPtr   memoryBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &rawDataBuffer[0] ),
                                                                      rawDataBuffer.size(),
                                                                      "points.xsd" ) );

            XmlReader   reader( xercesc::XMLReaderFactory::createXMLReader() );

            // set the grammer and turn on validation in the reader
            reader->loadGrammar( *memoryBuffer, xercesc::Grammar::SchemaGrammarType, true );

            reader->setFeature( xercesc::XMLUni::fgSAX2CoreValidation, true );
            reader->setFeature( xercesc::XMLUni::fgXercesCacheGrammarFromParse, true );

            // this guy handles the xml entities and fills the collection
            EntityHandler  handler( new PointHandler( points ) );

            // associate the handlers with the reader
            reader->setContentHandler( handler.get() );
            reader->setErrorHandler( handler.get() );

            for ( int index = RC_POINT_START_ID; index < RC_POINT_END_ID; index++ )
            {
                DataBuffer  pointsXml
                    = loadResourceFromLibrary( index, "XML", "yukon-resource.dll" );

                BufferPtr   pointsXmlBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &pointsXml[0] ),
                                                                             pointsXml.size(),
                                                                             resourceIndex.left.at( index ).c_str() ) );
                try
                {
                    reader->parse( *pointsXmlBuffer );
                }
                catch ( const xercesc::XMLException & ex )
                {
                    CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Error" );
                    return;
                }
                catch ( const xercesc::SAXParseException & ex )
                {
                    CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Parsing Error" );
                    return;
                }
                catch ( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR( dout, "** Error: Unexpected Exception" );
                    return;
                }
            }
        }

        // read deviceDefinition.xml and edit above data structures

        {
            char    deviceDefinition[ MAX_PATH ];

            PathCombine( deviceDefinition,
                         yukonBase.c_str(), "Server\\Config\\deviceDefinition.xml");

            // TBD -- future work

        }

        // load device config categories

        for ( const auto & pao : paos )
        {
            DeviceConfigDescription::AddCategoriesForDeviceType( resolvePaoIdXmlType( pao.paoType ), pao.configCategories );
        }

        // load the device attribute lookup

        for ( int index = RC_PAO_START_ID; index < RC_PAO_END_ID; index++ )
        {
            const auto & pao = paos[ index - RC_PAO_START_ID ];

            for ( const auto & point : pao.pointInfo ) 
            {
                if ( point.attributeList.size() > 0 )
                {
                    // need to build up the complete point file key from the pao key and the point file name info 

                    const std::string & paoKey = resourceIndex.left.at( index );

                    // want the directory name here -- truncate at the first '\' then add in the rest of the key

                    std::string pointFile( paoKey.begin(), std::find( paoKey.begin(), paoKey.end(), '\\' ) );
                    pointFile += "\\\\points\\\\" + boost::algorithm::to_lower_copy( pao.pointFileName );

                    int pointFileIndex = resourceIndex.right.at( pointFile );

                    const auto & pointsForPao = points[ pointFileIndex - RC_POINT_START_ID ];

                    auto pointItr = boost::range::find_if( pointsForPao.pointInfo, 
                            [ & ]( const PointInfoCollection::PointInfo & p ) 
                            { 
                                return point.pointName == p.name; 
                            } );

                    if ( pointItr != pointsForPao.pointInfo.end() )
                    {
                        DeviceTypes devType = resolvePaoIdXmlType( pao.paoType );

                        for ( const auto & attributeName : point.attributeList )
                        {
                            try
                            {
                                const auto& attribute = Attribute::Lookup( attributeName );

                                DeviceAttributeLookup::AddRelation( devType, attribute, pointItr->type, pointItr->offset );
                            }
                            catch( const AttributeNotFound&)
                            {
                                DeviceAttributeLookup::AddUnknownAttribute( devType, attributeName );
                            }
                        }
                    }
                }
            }
        }

        // Read error codes

        BasicErrorCodeInfoCollection errorCodes;
        {
            DataBuffer  rawDataBuffer
                = loadResourceFromLibrary( ERROR_CODE_ID, "XML", "yukon-resource.dll" );

            BufferPtr   memoryBuffer( new xercesc::MemBufInputSource( reinterpret_cast<const XMLByte *>( &rawDataBuffer[0] ),
                                      rawDataBuffer.size(),
                                      "error-code.xml" ) );

            XmlReader   reader( xercesc::XMLReaderFactory::createXMLReader() );

            // set the grammer and turn on validation in the reader
            reader->loadGrammar( *memoryBuffer, xercesc::Grammar::SchemaGrammarType, true );

            reader->setFeature( xercesc::XMLUni::fgSAX2CoreValidation, true );
            reader->setFeature( xercesc::XMLUni::fgXercesCacheGrammarFromParse, true );

            // this guy handles the xml entities and fills the collection
            EntityHandler  handler( new ErrorCodeHandler( errorCodes ) );

            // associate the handlers with the reader
            reader->setContentHandler( handler.get() );
            reader->setErrorHandler( handler.get() );

            try
            {
                reader->parse( *memoryBuffer );
            }
            catch( const xercesc::XMLException & ex )
            {
                CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Error" );
                return;
            }
            catch( const xercesc::SAXParseException & ex )
            {
                CTILOG_EXCEPTION_ERROR( dout, ex, "** XML Parsing Error" );
                return;
            }
            catch( ... )
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR( dout, "** Error: Unexpected Exception" );
                return;
            }
        }

        // Load the CtiErrors map
        {
            YukonError_t currentErrorCode;
            ErrorTypes currentErrorType;
            std::string currentErrorDescription;

            for( auto currentError = errorCodes.begin(); currentError != errorCodes.end(); currentError++ )
            {
                if( ! currentError->errorCode )
                {
                    currentErrorType = resolveErrorType( currentError->typeString );
                    currentErrorDescription = currentError->porterString;

                    CtiError::AddUnknownError( currentErrorType, currentErrorDescription );
                }
                else
                {
                    currentErrorCode = static_cast<YukonError_t>( * currentError->errorCode );
                    currentErrorType = resolveErrorType( currentError->typeString );
                    currentErrorDescription = currentError->porterString;

                    CtiError::AddErrorInfo( currentErrorCode, currentErrorType, currentErrorDescription );
                }
            }
        }
    }

    xercesc::XMLPlatformUtils::Terminate();
}

}

