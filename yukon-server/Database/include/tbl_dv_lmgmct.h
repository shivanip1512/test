#pragma once

#include <string>

#include "dlldefs.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableLMGroupMCT : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableLMGroupMCT(const CtiTableLMGroupMCT&);
    CtiTableLMGroupMCT& operator=(const CtiTableLMGroupMCT&);

    enum AddressLevels;

    unsigned long _address;

    AddressLevels _addressLevel;

    unsigned int _relays;

    long _routeID, _deviceID, _mctUniqueAddress;

public:

    CtiTableLMGroupMCT();
    virtual ~CtiTableLMGroupMCT();

    enum AddressLevels
    {
        Addr_Bronze,
        Addr_Lead,
        Addr_Unique,
        Addr_Invalid
    };

    static std::string getTableName();

    unsigned int getRelays();
    unsigned long getAddress() const;
    AddressLevels getAddressLevel() const;
    long getRouteID() const;
    long getMCTUniqueAddress() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
