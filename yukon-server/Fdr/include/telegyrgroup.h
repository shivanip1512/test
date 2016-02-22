#pragma once

#include <windows.h>

#include "ctitime.h"

#include <vector>

#include "dlldefs.h"
#include "fdr.h"
#include "fdrpoint.h"


class IM_EX_FDRTELEGYRAPI CtiTelegyrGroup
{
    public:
        DEBUG_INSTRUMENTATION

        CtiTelegyrGroup( void );
        virtual ~CtiTelegyrGroup();
        CtiTelegyrGroup& operator=( const CtiTelegyrGroup &other );

        std::string getGroupName( void ) const;
        CtiTelegyrGroup& setGroupName( std::string aGroupName );

        std::string getGroupType( void ) const;
        CtiTelegyrGroup& setGroupType( std::string aGroupType );

        int getInterval( void );
        CtiTelegyrGroup& setInterval( int interval );

        int getGroupID( void );
        CtiTelegyrGroup& setGroupID( int groupID );

        std::vector< CtiFDRPoint > getPointList( void ) const;
        std::vector< CtiFDRPoint > & getPointList( void );

    private:

        std::string             _groupName;
        std::string             _groupType;

        int                   _interval;
        int                   _groupID;

        std::vector< CtiFDRPoint > _pointList;
};
