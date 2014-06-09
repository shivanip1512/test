#pragma once

#include "devicetypes.h"
#include "pointtypes.h"
#include "resource_helper.h"
#include "PointAttribute.h"
#include "DeviceConfigDescription.h"

#include <xercesc/sax2/DefaultHandler.hpp>



namespace Cti
{

IM_EX_CTIBASE void parseXmlFiles( const std::string & yukonBase );

DeviceTypes resolvePaoIdXmlType( const std::string & type );


struct XmlPaoInfo
{
    struct PointInfo
    {
        std::string     name;
        CtiPointType_t  type;
        unsigned        offset;
    };

    std::string     ID;
    bool            isAbstract;

    std::set<std::string>   inheritsFrom;
    std::set<std::string>   configCategories;

    std::vector<PointInfo>  pointInfo;

    typedef std::map<std::string, std::string>  AttributeMap;

    AttributeMap attributes;

    void reset()
    {
        ID.clear();
        isAbstract = false;
        inheritsFrom.clear();
        configCategories.clear();
        pointInfo.clear();
        attributes.clear();
    }
};

typedef std::map<std::string, XmlPaoInfo>   XmlPaoInfoCollection;



class PaoDefinitionSAX2Handler : public xercesc::DefaultHandler
{
public:

    PaoDefinitionSAX2Handler( XmlPaoInfoCollection & paoInfoCollection );

    void startElement( const XMLCh * const uri,
                       const XMLCh * const localname,
                       const XMLCh * const qname,
                       const xercesc::Attributes &  attrs );

    void endElement( const XMLCh * const uri,
                     const XMLCh * const localname,
                     const XMLCh * const qname );

    void characters( const XMLCh * const chars,
                     const XMLSize_t     length );

    void fatalError( const xercesc::SAXParseException & ex );

private:

    bool inNameElement;

    XmlPaoInfoCollection &  paoInfo;

    XmlPaoInfo              currentPao;
    XmlPaoInfo::PointInfo   currentPoint;
};


class DeviceConfigCategorySAX2Handler : public xercesc::DefaultHandler
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

    void fatalError( const xercesc::SAXParseException & ex );

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


}

