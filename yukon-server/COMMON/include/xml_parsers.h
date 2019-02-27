#pragma once

#include "pointtypes.h"
#include "DeviceConfigDescription.h"

#include <xercesc/sax2/DefaultHandler.hpp>

//  forward declarations for bimap
namespace boost {
namespace mpl {
    struct na;
}

template
    <
    class KeyTypeA, class KeyTypeB,
    class AP1 = ::boost::mpl::na,
    class AP2 = ::boost::mpl::na,
    class AP3 = ::boost::mpl::na
    >
    class bimap;
}

namespace Cti
{

std::string XmlStringTranscode( const XMLCh * const xcode );


////
 
 
struct BaseSAX2Handler : public xercesc::DefaultHandler
{
    void fatalError( const xercesc::SAXParseException & ex );
};


////


using ResourceIndex = boost::bimap<int, std::string>;

class ResourceIndexHandler : public BaseSAX2Handler
{
public:

    ResourceIndexHandler( ResourceIndex & collection );

    void startElement( const XMLCh * const uri,
                       const XMLCh * const localname,
                       const XMLCh * const qname,
                       const xercesc::Attributes & attrs );

    void endElement( const XMLCh * const uri,
                     const XMLCh * const localname,
                     const XMLCh * const qname )
    {
        // empty
    }

    void characters( const XMLCh * const chars,
                     const XMLSize_t     length );

private:

    ResourceIndex & index;

    bool    inKey,
            inValue;
};


////


struct BasicPaoInfo
{
    struct BasicPointInfo
    {
        std::string                 pointName;
        std::vector<std::string>    attributeList;
    };

    std::string paoType,
                pointFileName;

    std::vector<std::string>    tags;

    std::set<std::string>       configCategories;

    std::vector<BasicPointInfo> pointInfo;

    void clear()
    {
        paoType.clear();
        pointFileName.clear();
        tags.clear();
        configCategories.clear();
        pointInfo.clear();
    }
};

using BasicPaoInfoCollection = std::vector<BasicPaoInfo>;

class PaoHandler : public BaseSAX2Handler
{
public:

    PaoHandler( BasicPaoInfoCollection & c );

    void startElement( const XMLCh * const uri,
                       const XMLCh * const localname,
                       const XMLCh * const qname,
                       const xercesc::Attributes & attrs );

    void endElement( const XMLCh * const uri,
                     const XMLCh * const localname,
                     const XMLCh * const qname );

    void characters( const XMLCh * const chars,
                     const XMLSize_t     length );

private:

    enum
    {
        OutsideRootNode,
        InsideRootNode,
        InsidePointFilesNode,
        ReadPointFileName,
        InsideTagsNode,
        InsideConfigurationNode,
        InsidePointInfosNode
    }
    _parseState;

    BasicPaoInfoCollection & collection;

    BasicPaoInfo     currentPao;
};


////


struct PointInfoCollection
{
    struct PointInfo
    {
        std::string     name;
        CtiPointType_t  type;
        unsigned        offset;
    };

    std::vector<PointInfo>  pointInfo;

    void clear()
    {
        pointInfo.clear();
    }
};

using BasicPointInfoCollection = std::vector<PointInfoCollection>;


class PointHandler : public BaseSAX2Handler
{
public:

    PointHandler( BasicPointInfoCollection & c );

    void startElement( const XMLCh * const uri,
                       const XMLCh * const localname,
                       const XMLCh * const qname,
                       const xercesc::Attributes & attrs );

    void endElement( const XMLCh * const uri,
                     const XMLCh * const localname,
                     const XMLCh * const qname );

    void characters( const XMLCh * const chars,
                     const XMLSize_t     length );

private:

    enum
    {
        OutsideRootNode,
        InsideRootNode,
        InsidePointNode,
        ReadPointName
    }
    _parseState;

    BasicPointInfoCollection & collection;

    PointInfoCollection::PointInfo  currentPoint;
    PointInfoCollection             currentCollection;
};


////


class DeviceConfigCategoryHandler : public BaseSAX2Handler
{
public:

    void startElement( const XMLCh * const uri,
                       const XMLCh * const localname,
                       const XMLCh * const qname,
                       const xercesc::Attributes &  attrs );

    void endElement( const XMLCh * const uri,
                     const XMLCh * const localname,
                     const XMLCh * const qname );

    void characters( const XMLCh * const chars,
                     const XMLSize_t     length )
    {
        // empty
    }

private:

    typedef DeviceConfigDescription::ContainerHandle ContainerHandle;

    std::vector<ContainerHandle> currentContainer;

    void setCurrentCategory  ( const xercesc::Attributes & attrs );

    void addField            ( const std::string & fieldName );

    void insertField         ( const xercesc::Attributes & attrs );
    void insertTimeRateField ( const xercesc::Attributes & attrs );
    void setMapEntryPrefix   ( const xercesc::Attributes & attrs );

    void pushIndexedField    ( const xercesc::Attributes & attrs );
    void popIndexedField     ( void );

    std::string mapEntryPrefix;
};


////


struct BasicErrorCodeInfo
{
    boost::optional<long> errorCode;

    std::string typeString, porterString;

    void clear()
    {
        errorCode = boost::none;
        porterString.clear();
        typeString.clear();
    }
};

using BasicErrorCodeInfoCollection = std::vector<BasicErrorCodeInfo>;

class ErrorCodeHandler : public BaseSAX2Handler
{

public:
    ErrorCodeHandler( BasicErrorCodeInfoCollection & c );

    void startElement( const XMLCh * const uri,
                       const XMLCh * const localname,
                       const XMLCh * const qname,
                       const xercesc::Attributes & attrs );

    void endElement( const XMLCh * const uri,
                     const XMLCh * const localname,
                     const XMLCh * const qname );

    void characters( const XMLCh * const chars,
                     const XMLSize_t     length );


private: 

    enum
    {
        //states
        OutsideRootNode,
        InsideRootNode,
        InsideErrorNode,
        ReadPorterNode,
        InsidePorterNode
    }
    _parseState;

    BasicErrorCodeInfoCollection & collection;

    BasicErrorCodeInfo currentErrorCode;
};

}