#pragma once

#include "yukon.h"
#include "dllbase.h"

#include <boost/shared_ptr.hpp>
#include <boost/optional.hpp>


namespace Cti {

class ConfigManager;

namespace Config {

class IM_EX_CONFIG Category
{
public:

    Category( const std::string & type );

    typedef boost::shared_ptr<Category> SharedPtr;

    typedef std::map<std::string, std::string> ItemMap;
    typedef std::vector<ItemMap>               IndexedItem;
    typedef std::map<std::string, IndexedItem> IndexedItemMap;

    static SharedPtr ConstructCategory( const std::string & type, const std::map<std::string, std::string> & databaseItems );

    std::string getType() const;

    ItemMap        getItems()        const;
    IndexedItemMap getIndexedItems() const;

private:

    ItemMap         _items;
    IndexedItemMap  _indexedItems;

    std::string  _type;
};

typedef Category::SharedPtr CategorySPtr;


class IM_EX_CONFIG DeviceConfig
{
public:

    template <typename T>
    boost::optional<T> findValue( const std::string & key ) const;

    std::string getValueFromKey( const std::string & key ) const;
    long        getLongValueFromKey( const std::string & key ) const;
    double      getFloatValueFromKey( const std::string & key ) const;

    typedef Category::ItemMap        ItemsByName;
    typedef Category::IndexedItem    IndexedItem;
    typedef Category::IndexedItemMap IndexedItemsByName;

    boost::optional<IndexedItem> getIndexedItem( const std::string & prefix );

protected:

    void addCategory( const CategorySPtr & category );
    bool insertValue( std::string identifier, const std::string & value );
    bool getLongValue( const std::string & key, long & value ) const;

private:

    friend class ConfigManager;

    ItemsByName        _items;
    IndexedItemsByName _indexedItems;

    boost::optional<std::string> lookup( std::string key ) const;
};

typedef boost::shared_ptr< DeviceConfig > DeviceConfigSPtr;

template<> IM_EX_CONFIG boost::optional<std::string> DeviceConfig::findValue<std::string> ( const std::string & key ) const;
template<> IM_EX_CONFIG boost::optional<bool>        DeviceConfig::findValue<bool>        ( const std::string & key ) const;
template<> IM_EX_CONFIG boost::optional<long>        DeviceConfig::findValue<long>        ( const std::string & key ) const;
template<> IM_EX_CONFIG boost::optional<double>      DeviceConfig::findValue<double>      ( const std::string & key ) const;

}
}

