#pragma once

#include "yukon.h"

namespace Cti
{
    class RowReader;
}



class IM_EX_CTIYUKONDB  CtiTableDynamicLcrCommunications : private boost::noncopyable
{
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

