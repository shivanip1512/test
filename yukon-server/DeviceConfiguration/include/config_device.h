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
    template <typename T>
    boost::optional<T> findValue( const std::string & key, const std::map<std::string, T> &map ) const;


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

template<> IM_EX_CONFIG boost::optional<std::string> DeviceConfig::findValue<std::string> ( const std::string & key, const std::map<std::string, std::string> &map ) const;
template<> IM_EX_CONFIG boost::optional<bool>        DeviceConfig::findValue<bool>        ( const std::string & key, const std::map<std::string, bool>        &map ) const;
template<> IM_EX_CONFIG boost::optional<long>        DeviceConfig::findValue<long>        ( const std::string & key, const std::map<std::string, long>        &map ) const;
template<> IM_EX_CONFIG boost::optional<double>      DeviceConfig::findValue<double>      ( const std::string & key, const std::map<std::string, double>      &map ) const;

enum DisplayItem {
    slotDisabled = 0,
    noSegments = 1,
    allSegments = 2,
    // Item #3 is deprecated and unused.
    currentLocalTime = 4,
    currentLocalDate = 5,
    totalKwh = 6,
    netKwh = 7,
    deliveredKwh = 8,
    receivedKwh = 9,
    lastIntervalKw = 10,
    peakKw = 11,
    peakKwDate = 12,
    peakKwTime = 13,
    lastIntervalVoltage = 14,
    peakVoltage = 15,
    peakVoltageDate = 16,
    peakVoltageTime = 17,
    minimumVoltage = 18,
    minimumVoltageDate = 19,
    minimumVoltageTime = 20,
    touRateAKwh = 21,
    touRateAPeakKw = 22,
    touRateADateOfPeakKw = 23,
    touRateATimeOfPeakKw = 24,
    touRateBKwh = 25,
    touRateBPeakKw = 26,
    touRateBDateOfPeakKw = 27,
    touRateBTimeOfPeakKw = 28,
    touRateCKwh = 29,
    touRateCPeakKw = 30,
    touRateCDateOfPeakKw = 31,
    touRateCTimeOfPeakKw = 32,
    touRateDKwh = 33,
    touRateDPeakKw = 34,
    touRateDDateOfPeakKw = 35,
    touRateDTimeOfPeakKw = 36
};

class IM_EX_CONFIG DisplayItemValues
{
public:
    static const std::string SLOT_DISABLED;			    //0
    static const std::string NO_SEGMENTS;			    //1
    static const std::string ALL_SEGMENTS;			    //2
    // Item #3 is deprecated and unused.
    static const std::string CURRENT_LOCAL_TIME;		//4
    static const std::string CURRENT_LOCAL_DATE;		//5
    static const std::string TOTAL_KWH;			        //6
    static const std::string NET_KWH;			        //7
    static const std::string DELIVERED_KWH;			    //8
    static const std::string RECEIVED_KWH;			    //9
    static const std::string LAST_INTERVAL_KW;			//10
    static const std::string PEAK_KW;			        //11
    static const std::string PEAK_KW_DATE;			    //12
    static const std::string PEAK_KW_TIME;			    //13
    static const std::string LAST_INTERVAL_VOLTAGE;		//14
    static const std::string PEAK_VOLTAGE;			    //15
    static const std::string PEAK_VOLTAGE_DATE;			//16
    static const std::string PEAK_VOLTAGE_TIME;			//17
    static const std::string MINIMUM_VOLTAGE;			//18
    static const std::string MINIMUM_VOLTAGE_DATE;		//19
    static const std::string MINIMUM_VOLTAGE_TIME;		//20
    static const std::string TOU_RATE_A_KWH;			//21
    static const std::string TOU_RATE_A_PEAK_KW;		//22
    static const std::string TOU_RATE_A_DATE_OF_PEAK_KW; //23
    static const std::string TOU_RATE_A_TIME_OF_PEAK_KW; //24
    static const std::string TOU_RATE_B_KWH;			//25
    static const std::string TOU_RATE_B_PEAK_KW;		//26
    static const std::string TOU_RATE_B_DATE_OF_PEAK_KW; //27
    static const std::string TOU_RATE_B_TIME_OF_PEAK_KW; //28
    static const std::string TOU_RATE_C_KWH;			//29
    static const std::string TOU_RATE_C_PEAK_KW;		//30
    static const std::string TOU_RATE_C_DATE_OF_PEAK_KW; //31
    static const std::string TOU_RATE_C_TIME_OF_PEAK_KW; //32
    static const std::string TOU_RATE_D_KWH;			//33
    static const std::string TOU_RATE_D_PEAK_KW;		//34
    static const std::string TOU_RATE_D_DATE_OF_PEAK_KW; //35
    static const std::string TOU_RATE_D_TIME_OF_PEAK_KW; //36;
};

extern const IM_EX_CONFIG std::map<std::string, long> displayItemMap;

}
}

