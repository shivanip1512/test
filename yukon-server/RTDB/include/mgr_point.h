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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/02/16 21:03:11 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __MGR_POINT_H__
#define __MGR_POINT_H__
#pragma warning( disable : 4786 )


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

    void refreshPoints(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);


    // These are properties of already collected points.
    void refreshPointProperties(LONG pntID = 0, LONG paoID = 0);
    void refreshPointLimits(LONG pntID = 0, LONG paoID = 0);
    void refreshAlarming(LONG pntID = 0, LONG paoID = 0);

public:

    CtiPointManager();
    virtual ~CtiPointManager();


    virtual void refreshList(BOOL (*fn)(CtiPointBase*,void*) = isPoint, void *d = NULL, LONG pntID = 0, LONG paoID = 0);

    virtual void DumpList(void);
    virtual void DeleteList(void);

    CtiPoint* getControlOffsetEqual(LONG pao, INT Offset);
    CtiPoint* getOffsetTypeEqual(LONG pao, INT Offset, INT Type);
    CtiPoint* getEqual(LONG Pt);
    CtiPoint* getEqualByName(LONG pao, RWCString pname);
    bool isIdValid(LONG Pt);

};

#endif                  // #ifndef __MGR_POINT_H__
