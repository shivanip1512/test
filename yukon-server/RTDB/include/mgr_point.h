#pragma warning( disable : 4786 )

#ifndef __MGR_POINT_H__
#define __MGR_POINT_H__
/*************************************************************************
 *
 * mgr_route.h      7/7/99
 *
 *****
 *
 * The class which owns and manages route real time database
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_point.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/08/05 20:42:57 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/

#include <rw/db/connect.h>

#include "dlldefs.h"
#include "rtdb.h"
#include "pt_base.h"
#include "slctpnt.h"

/*
 *  The following functions may be used to create sublists for the points in our database.
 */

class IM_EX_PNTDB CtiPointManager : public CtiRTDB<CtiPoint>
{
private:

    // Inherit "List" from Parent

    // RWDBConnection conn;

    void RefreshPoints(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);
    void RefreshPointLimits(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);
    void RefreshCalcElements(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);
    void RefreshAlarming(bool &rowFound);

public:
    CtiPointManager();
    virtual ~CtiPointManager();


    virtual void RefreshList(BOOL (*fn)(CtiPointBase*,void*) = isAPoint, void *d = NULL);
    virtual void RefreshList( LONG );

    virtual void DumpList(void);
    virtual void DeleteList(void);

    CtiPoint* getControlOffsetEqual(LONG pao, INT Offset);
    CtiPoint* getOffsetTypeEqual(LONG pao, INT Offset, INT Type);
    CtiPoint* getEqual(LONG Pt);
    CtiPoint* getEqualByName(LONG pao, RWCString pname);

};

#endif                  // #ifndef __MGR_POINT_H__
