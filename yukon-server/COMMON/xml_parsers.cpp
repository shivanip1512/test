#include "precompiled.h"

#include "xml_parsers.h"

#include "logger.h"
#include "resolvers.h"
#include "std_helper.h"

#include <xercesc/util/XMLString.hpp>
#include <xercesc/sax2/Attributes.hpp>

#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>
#include <boost/algorithm/string/replace.hpp>
#include <boost/bimap.hpp>

namespace Cti
{

std::string XmlStringTranscode( const XMLCh * const xcode )
{
    char * local = xercesc::XMLString::transcode( xcode );

    std::string transcodedString( local );

    xercesc::XMLString::release( &local );

    return transcodedString;
}

namespace
{

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

}


////


void BaseSAX2Handler::fatalError( const xercesc::SAXParseException & ex )
{
    const std::string message( XmlStringTranscode( ex.getMessage() ) );
    const unsigned line = ex.getLineNumber();

    CTILOG_FATAL( dout, "Fatal Error: " << message << " at line: " << line );
}


////


ResourceIndexHandler::ResourceIndexHandler( ResourceIndex & collection )
    :   index( collection ),
        inKey( false ),
        inValue( false )
{
    // empty...
}

void ResourceIndexHandler::startElement( const XMLCh * const uri,
                                         const XMLCh * const localname,
                                         const XMLCh * const qname,
                                         const xercesc::Attributes & attrs )
{
    std::string element( XmlStringTranscode( localname ) );

    if ( element == "Property" )
    {
        for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
        {
            std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

            if ( name == "Name" )
            {
                std::string value = XmlStringTranscode( attrs.getValue(i) );

                if ( value == "Key" )
                {
                    inKey = true;
                }

                if ( value == "Value" )
                {
                    inValue = true;
                }
            }
        }
    }
}

void ResourceIndexHandler::characters( const XMLCh * const chars,
                                       const XMLSize_t     length )
{
    static int lastKeyRead;

    if ( inKey )
    {
        inKey = false;

        lastKeyRead = atoi( XmlStringTranscode( chars ).c_str() );
    }

    if ( inValue )
    {
        inValue = false;

        index.left.insert( ResourceIndex::left_value_type( lastKeyRead, XmlStringTranscode( chars ) ) );
    }
}


////


PaoHandler::PaoHandler( BasicPaoInfoCollection & c )
    :   collection( c ),
        _parseState( OutsideRootNode )
{
    // empty...
}

void PaoHandler::startElement( const XMLCh * const uri,
                               const XMLCh * const localname,
                               const XMLCh * const qname,
                               const xercesc::Attributes & attrs )
{
    std::string element( XmlStringTranscode( localname ) );

    switch ( _parseState )
    {
        case OutsideRootNode:
        {
            if ( element == "pao" )
            {
                _parseState = InsideRootNode;

                for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
                {
                    std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

                    if ( name == "paoType" )
                    {
                        currentPao.paoType = XmlStringTranscode( attrs.getValue( i ) );
                    }
                }
            }
            break;
        }
        case InsideRootNode:
        {
            if ( element == "pointFiles" )
            {
                _parseState = InsidePointFilesNode;
            }

            if ( element == "tags" )
            {
                _parseState = InsideTagsNode;
            }

            if ( element == "configuration" )
            {
                _parseState = InsideConfigurationNode;
            }

            if ( element == "pointInfos" )
            {
                _parseState = InsidePointInfosNode;
            }
            break;
        }
        case InsidePointFilesNode:
        {
            if ( element == "pointFile" )
            {
                _parseState = ReadPointFileName;
            }
            break;
        }
        case InsideTagsNode:
        {
            if ( element == "tag" )
            {
                for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
                {
                    std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

                    if ( name == "name" )
                    {
                        currentPao.tags.push_back( XmlStringTranscode( attrs.getValue( i ) ) );
                    }
                }
            }
            break;
        }
        case InsideConfigurationNode:
        {
            if ( element == "category" )
            {
                for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
                {
                    std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

                    if ( name == "type" )
                    {
                        currentPao.configCategories.insert( XmlStringTranscode( attrs.getValue( i ) ) );
                    }
                }
            }
            break;
        }
        case InsidePointInfosNode:
        {
            if ( element == "pointInfo" )
            {
                BasicPaoInfo::BasicPointInfo    info;

                for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
                {
                    std::string name( XmlStringTranscode( attrs.getLocalName(i) ) );

                    if ( name == "name" )
                    {
                        info.pointName = XmlStringTranscode( attrs.getValue( i ) );
                    }

                    if ( name == "attributes" )
                    {
                        boost::split( info.attributeList, XmlStringTranscode( attrs.getValue( i ) ),
                                      is_chars{',',' '}, boost::token_compress_on );
                    }
                }

                currentPao.pointInfo.push_back( info );
            }
            break;
        }
    }
}

void PaoHandler::endElement( const XMLCh * const uri,
                             const XMLCh * const localname,
                             const XMLCh * const qname )
{
    std::string element( XmlStringTranscode( localname ) );

    switch ( _parseState )
    {
        case InsideRootNode:
        {
            if ( element == "pao" )
            {
                _parseState = OutsideRootNode;

                collection.push_back( currentPao );

                currentPao.clear();
            }
            break;
        }
        case InsidePointFilesNode:
        {
            if ( element == "pointFiles" )
            {
                _parseState = InsideRootNode;
            }
            break;
        }
        case ReadPointFileName:
        {
            if ( element == "pointFile" )
            {
                _parseState = InsidePointFilesNode;
            }
            break;
        }
        case InsideTagsNode:
        {
            if ( element == "tags" )
            {
                _parseState = InsideRootNode;
            }
            break;
        }
        case InsideConfigurationNode:
        {
            if ( element == "configuration" )
            {
                _parseState = InsideRootNode;
            }
            break;
        }
        case InsidePointInfosNode:
        {
            if ( element == "pointInfos" )
            {
                _parseState = InsideRootNode;
            }
            break;
        }
    }
}

void PaoHandler::characters( const XMLCh * const chars,
                             const XMLSize_t     length )
{
    switch ( _parseState )
    {
        case ReadPointFileName:
        {
            currentPao.pointFileName = XmlStringTranscode( chars );
            break;
        }
    }
}


////


PointHandler::PointHandler( BasicPointInfoCollection & c )
    :   collection( c ),
        _parseState( OutsideRootNode )
{
    // empty...
}

void PointHandler::startElement( const XMLCh * const uri,
                                 const XMLCh * const localname,
                                 const XMLCh * const qname,
                                 const xercesc::Attributes & attrs )
{
    std::string element( XmlStringTranscode( localname ) );

    switch ( _parseState )
    {
        case OutsideRootNode:
        {
            if ( element == "points" )
            {
                _parseState = InsideRootNode;
            }
            break;
        }
        case InsideRootNode:
        {
            if ( element == "point" )
            {
                _parseState = InsidePointNode;

                currentPoint.name = "<unassigned>";

                for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
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
            break;
        }
        case InsidePointNode:
        {
            if ( element == "name" )
            {
                _parseState = ReadPointName; 
            }
            break;
        }
    }
}

void PointHandler::endElement( const XMLCh * const uri,
                               const XMLCh * const localname,
                               const XMLCh * const qname )
{
    std::string element( XmlStringTranscode( localname ) );

    switch ( _parseState )
    {
        case InsideRootNode:
        {
            if ( element == "points" )
            {
                _parseState = OutsideRootNode;

                collection.push_back( currentCollection );

                currentCollection.clear();
            }
            break;
        }
        case InsidePointNode:
        {
            if ( element == "point" )
            {
                _parseState = InsideRootNode;

                currentCollection.pointInfo.push_back( currentPoint );
            }
            break;
        }
        case ReadPointName:
        {
            if ( element == "name" )
            {
                _parseState = InsidePointNode;
            }
            break;
        }
    }
}

void PointHandler::characters( const XMLCh * const chars,
                               const XMLSize_t     length )
{
    switch ( _parseState )
    {
        case ReadPointName:
        {
            currentPoint.name = XmlStringTranscode( chars );
            break;
        }
    }
}


////


void DeviceConfigCategoryHandler::startElement( const XMLCh * const uri,
                                                const XMLCh * const localname,
                                                const XMLCh * const qname,
                                                const xercesc::Attributes &  attrs )
{
    typedef DeviceConfigCategoryHandler This;
    typedef std::function<void (This *, const xercesc::Attributes &)> StartElementHandler;

    static const std::map<std::string, StartElementHandler> elementHandlers
    {
        { "category", &This::setCurrentCategory     },
        { "enum",     &This::insertField            },
        { "boolean",  &This::insertField            },
        { "integer",  &This::insertField            },
        { "string",   &This::insertField            },
        { "float",    &This::insertField            },
        { "entry",    &This::insertTimeRateField    },  //  <--- These two need to be converted to indexed fields
        { "map",      &This::setMapEntryPrefix      },  //  <---
        { "indexed",  &This::pushIndexedField       }
    };

    const boost::optional<StartElementHandler> elementHandler = Cti::mapFind( elementHandlers, XmlStringTranscode( localname ) );

    if ( elementHandler )
    {
        (*elementHandler)(this, attrs);
    }
}

void DeviceConfigCategoryHandler::endElement( const XMLCh * const uri,
                                              const XMLCh * const localname,
                                              const XMLCh * const qname )
{
    typedef DeviceConfigCategoryHandler This;
    typedef std::function<void (This *)> EndElementHandler;

    static const std::map<std::string, EndElementHandler> elementHandlers
    {
        { "indexed", &This::popIndexedField     }
    };

    const boost::optional<EndElementHandler> elementHandler = Cti::mapFind( elementHandlers, XmlStringTranscode( localname ) );

    if ( elementHandler )
    {
        (*elementHandler)(this);
    }
}

void DeviceConfigCategoryHandler::setCurrentCategory( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> typeAttr = findAttribute( attrs, "type" );

    if ( typeAttr )
    {
        currentContainer.assign( 1, DeviceConfigDescription::AddCategory( *typeAttr ) );
    }
}

void DeviceConfigCategoryHandler::addField( const std::string & fieldName )
{
    if ( ! currentContainer.empty() )
    {
        DeviceConfigDescription::AddItem( currentContainer.back(), fieldName );
    }
}

void DeviceConfigCategoryHandler::insertField( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> fieldAttr = findAttribute( attrs, "field" );

    if ( fieldAttr )
    {
        addField( *fieldAttr );
    }
}

void DeviceConfigCategoryHandler::setMapEntryPrefix( const xercesc::Attributes & attrs )
{
    const boost::optional<std::string> fieldAttr = findAttribute( attrs, "field" );

    if ( fieldAttr )
    {
        mapEntryPrefix = *fieldAttr;
    }
}

void DeviceConfigCategoryHandler::insertTimeRateField( const xercesc::Attributes & attrs )
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

void DeviceConfigCategoryHandler::pushIndexedField( const xercesc::Attributes & attrs )
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

void DeviceConfigCategoryHandler::popIndexedField()
{
    if ( ! currentContainer.empty() )
    {
        currentContainer.pop_back();
    }
}


////


ErrorCodeHandler::ErrorCodeHandler( BasicErrorCodeInfoCollection & c )
    :   collection( c ),
        _parseState( OutsideRootNode )
{
    // empty...
}

void ErrorCodeHandler::startElement( const XMLCh * const uri,
                                     const XMLCh * const localname,
                                     const XMLCh * const qname,
                                     const xercesc::Attributes & attrs )
{
    std::string element( XmlStringTranscode( localname ) );

    switch ( _parseState )
    {
        case OutsideRootNode:
        {
            if ( element == "portererrorcodes" )
            {
                _parseState = InsideRootNode;
            }
            break;
        }
        case InsideRootNode:
        {
            if ( element == "error" )
            {
                _parseState = InsideErrorNode;

                for ( XMLSize_t i = 0; i < attrs.getLength(); i++ )
                {
                    std::string name( XmlStringTranscode( attrs.getLocalName( i ) ) );

                    if ( name == "code" )
                    {
                        currentErrorCode.errorCode = stol( XmlStringTranscode( attrs.getValue( i ) ) );
                    }

                    if ( name == "type" )
                    {
                        currentErrorCode.typeString = XmlStringTranscode( attrs.getValue( i ) );
                    }
                }
            }
            break;
        }
        case InsideErrorNode: 
        {
            if ( element == "porter" ) 
            {
                _parseState = ReadPorterNode;
            }
            break;
        }
    }
}

void ErrorCodeHandler::endElement( const XMLCh * const uri,
                                   const XMLCh * const localname,
                                   const XMLCh * const qname )
{
    std::string element( XmlStringTranscode( localname ) );

    switch ( _parseState )
    {
        case InsideRootNode:
        {
            if ( element == "portererrorcodes" )
            {
                _parseState = OutsideRootNode;
            }
            break;
        }
        case InsideErrorNode:
        {
            if ( element == "error" )
            {
                _parseState = InsideRootNode;

                collection.push_back(currentErrorCode);

                currentErrorCode.clear();
            }
            break;
        }
        case ReadPorterNode:
        {
            if ( element == "porter" ) 
            {
                _parseState = InsideErrorNode;
            }
            break;
        }
    }
}

void ErrorCodeHandler::characters( const XMLCh * const chars,
                                   const XMLSize_t     length )
{
    switch ( _parseState )
    {
        case ReadPorterNode:
        {
            currentErrorCode.porterString = XmlStringTranscode( chars );
            break;
        }
    }
}

}