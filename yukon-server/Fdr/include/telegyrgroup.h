
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:17:16 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TELEGYRGROUP_H__
#define __TELEGYRGROUP_H__

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

        string getGroupName( void ) const;
        CtiTelegyrGroup& setGroupName( string aGroupName );

        string getGroupType( void ) const;
        CtiTelegyrGroup& setGroupType( string aGroupType );

        int getInterval( void );
        CtiTelegyrGroup& setInterval( int interval );

        int getGroupID( void );
        CtiTelegyrGroup& setGroupID( int groupID );

        vector< CtiFDRPoint > getPointList( void ) const;
        vector< CtiFDRPoint > & getPointList( void );

    private:

        string             _groupName;
        string             _groupType;

        int                   _interval;
        int                   _groupID;

        vector< CtiFDRPoint > _pointList;
};

#endif // #ifndef __TELEGYRGROUP_H__
