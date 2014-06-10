#pragma once

#include "devicetypes.h"
#include "pointtypes.h"
#include "PointAttribute.h"

#include <boost/optional.hpp>

namespace Cti {

class IM_EX_CTIBASE DeviceConfigDescription
{
public:

    struct ItemDescription;

    struct ItemContainer
    {
        std::vector<ItemDescription> elements;
    };

    struct ItemDescription : ItemContainer
    {
        ItemDescription( const std::string & name_ )
            :   name( name_ ),
                isIndexed( false )
        { }

        ItemDescription( const std::string & name_, boost::optional<unsigned> minOccurs_, boost::optional<unsigned> maxOccurs_ )
            :   name( name_ ),
                minOccurs( minOccurs_ ),
                maxOccurs( maxOccurs_ ),
                isIndexed( true )
        { }

        std::string name;

        bool isIndexed;

        boost::optional<unsigned> minOccurs, maxOccurs;
    };

    struct CategoryDescription : ItemContainer
    {
        //  name is stored in the map
    };

    class ContainerHandle
    {
        friend class DeviceConfigDescription;

        ContainerHandle( ItemContainer &ic ) : itemContainer(&ic)  { }

        ItemContainer *itemContainer;
    };

    static const ContainerHandle AddCategory   ( const std::string & categoryName );
    static void                  AddItem       ( const ContainerHandle container, const std::string & itemName );
    static const ContainerHandle AddIndexedItem( const ContainerHandle container, const std::string & itemName, const boost::optional<unsigned> minOccurs, const boost::optional<unsigned> maxOccurs );

    typedef std::set<std::string>  CategoryNames;

    static void AddCategoriesForDeviceType( const DeviceTypes      deviceType,
                                            const CategoryNames &  categories );

    static CategoryNames & GetCategoryNamesForDeviceType( const DeviceTypes deviceType );

    typedef boost::optional<const CategoryDescription &> OptionalCategoryDescription;

    static const OptionalCategoryDescription GetCategoryDescription( const std::string & categoryName );

private:

    typedef std::map<DeviceTypes, CategoryNames>  CategoriesPerDeviceType;
    typedef boost::ptr_map<std::string, CategoryDescription> CategoriesByName;

    static CategoriesPerDeviceType _deviceCategories;
    static CategoriesByName        _categories;
};

}

