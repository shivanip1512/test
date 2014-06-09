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
        ItemDescription( const std::string & name_, unsigned minOccurs_, unsigned maxOccurs_ )
            :   name( name_ ),
                minOccurs( minOccurs_ ),
                maxOccurs( maxOccurs_ )
        { }

        std::string name;

        unsigned minOccurs, maxOccurs;
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
    static const ContainerHandle AddIndexedItem( const ContainerHandle container, const std::string & itemName, const unsigned minOccurs, const unsigned maxOccurs );

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

