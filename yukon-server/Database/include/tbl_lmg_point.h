
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_point
*
* Class:  CtiTablePointGroup
* Date:   4/3/2006
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
* HISTORY      :
* $Log: tbl_lmg_point.h,v $
* Revision 1.2.24.1  2008/11/13 17:23:49  jmarks
* YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008
*
* Responded to reviewer comments again.
*
* I eliminated excess references to windows.h .
*
* This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.
*
* None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
* Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.
*
* In this process I occasionally deleted a few empty lines, and when creating the define, also added some.
*
* This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.
*
* Revision 1.2  2006/04/05 16:22:18  cplender
* Initial Revision
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_POINT_H__
#define __TBL_LMG_POINT_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>


#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTablePointGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _controlDevice;
    LONG _controlPoint;
    LONG _controlStartRawState;

    std::string _rawstate[2];             // These are the state strings.

private:

public:

    CtiTablePointGroup();
    CtiTablePointGroup(const CtiTablePointGroup& aRef);
    virtual ~CtiTablePointGroup();

    LONG getLmGroupId() const;
    LONG getControlDevice() const;
    LONG getControlPoint() const;
    LONG getControlStartRawState() const;
    std::string getControlStartString() const;
    std::string getControlStopString() const;

    static std::string getTableName( void );

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

#endif // #ifndef __TBL_LMG_POINT_H__
