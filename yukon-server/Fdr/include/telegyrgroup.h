
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/12 18:31:29 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TELEGYRGROUP_H__
#define __TELEGYRGROUP_H__

#include <windows.h>

#include <rw/cstring.h>
#include <rw/rwtime.h>

#include <vector>

#include "dlldefs.h"
#include "fdr.h"
#include "fdrpoint.h"


class IM_EX_FDRBASE CtiTelegyrGroup
{
    public:

        CtiTelegyrGroup( void );
        virtual ~CtiTelegyrGroup();
        CtiTelegyrGroup& operator=( const CtiTelegyrGroup &other );

        RWCString getGroupName( void ) const;
        CtiTelegyrGroup& setGroupName( RWCString aGroupName );

        RWCString getGroupType( void ) const;
        CtiTelegyrGroup& setGroupType( RWCString aGroupType );

        int getInterval( void );
        CtiTelegyrGroup& setInterval( int interval );

        int getGroupID( void );
        CtiTelegyrGroup& setGroupID( int groupID );

        vector< CtiFDRPoint > getPointList( void ) const;
        vector< CtiFDRPoint > & getPointList( void );

    private:

        RWCString             _groupName;
        int                   _groupID;
        RWCString             _groupType;
        int                   _interval;
        vector< CtiFDRPoint > _pointList;
};

#endif // #ifndef __TELEGYRGROUP_H__
