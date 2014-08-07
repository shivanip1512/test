#pragma once

#include "yukon.h"

namespace Cti
{
    class RowReader;
}



class IM_EX_CTIYUKONDB  CtiTableDynamicLcrCommunications : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDynamicLcrCommunications(const CtiTableDynamicLcrCommunications&);
    CtiTableDynamicLcrCommunications& operator=(const CtiTableDynamicLcrCommunications&);

public:

    CtiTableDynamicLcrCommunications();

    void DecodeDatabaseReader( Cti::RowReader & rdr );

    void updateRelayRuntime( const int relay_number, const CtiTime & latest_runtime );
    void updateNonZeroRuntime( const CtiTime & nonzero_runtime );
    void updateLastCommsTime( const CtiTime & latest_comms = CtiTime() );

protected:

    bool prepareTableForUpdates();

    void updateTime( const std::string column_name, const CtiTime & new_time );

    bool    _needPreInsert;
    long    _deviceID;
};

