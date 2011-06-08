
/*-----------------------------------------------------------------------------*
*
* File:   telegyrgroup
*
* Class:
* Date:   5/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:46 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TELEGYRGROUP_H__
#define __TELEGYRGROUP_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "ctitime.h"

#include <vector>

#include "dlldefs.h"
#include "fdr.h"
#include "fdrpoint.h"


class IM_EX_FDRTELEGYRAPI CtiTelegyrGroup
{
    public:

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

#endif // #ifndef __TELEGYRGROUP_H__
