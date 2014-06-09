#include "precompiled.h"

#include "DeviceConfigDescription.h"

#include "std_helper.h"

namespace Cti {

DeviceConfigDescription::CategoriesPerDeviceType  DeviceConfigDescription::_deviceCategories;
DeviceConfigDescription::CategoriesByName         DeviceConfigDescription::_categories;

typedef DeviceConfigDescription::ContainerHandle  ContainerHandle;
typedef DeviceConfigDescription::OptionalCategoryDescription OptionalCategoryDescription;

void DeviceConfigDescription::AddCategoriesForDeviceType( const DeviceTypes     deviceType,
                                                     const CategoryNames & categoryNames )
{
    _deviceCategories[ deviceType ].insert( categoryNames.begin(), categoryNames.end() );
}

DeviceConfigDescription::CategoryNames & DeviceConfigDescription::GetCategoryNamesForDeviceType( const DeviceTypes deviceType )
{
    return _deviceCategories[ deviceType ];
}


const ContainerHandle DeviceConfigDescription::AddCategory( const std::string &categoryName )
{
    std::auto_ptr<CategoryDescription> category(new CategoryDescription);

    ContainerHandle h(*category);

    _categories.insert(categoryName, category);

    return h;
}

void DeviceConfigDescription::AddItem( const ContainerHandle handle, const std::string &itemName )
{
    handle.itemContainer->elements.push_back( ItemDescription(itemName, 0, 0) );
}

const ContainerHandle DeviceConfigDescription::AddIndexedItem( const ContainerHandle handle, const std::string &itemName, const unsigned minOccurs, const unsigned maxOccurs )
{
    handle.itemContainer->elements.push_back( ItemDescription(itemName, minOccurs, maxOccurs) );

    return handle.itemContainer->elements.back();
}

const OptionalCategoryDescription DeviceConfigDescription::GetCategoryDescription( const std::string & categoryName )
{
    CategoriesByName::const_iterator itr = _categories.find(categoryName);

    if( itr != _categories.end() && itr->second )
    {
        return *(itr->second);
    }

    return boost::none;
}

}

