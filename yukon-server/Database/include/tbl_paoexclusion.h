/*-----------------------------------------------------------------------------*
*
* File:   tbl_paoexclusion
*
* Class:  CtiTablePaoExclusion
* Date:   5/14/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/05/10 21:35:52 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PAOEXCLUSION_H__
#define __TBL_PAOEXCLUSION_H__

#include <rw\cstring.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>


class IM_EX_CTIYUKONDB CtiTablePaoExclusion
{
protected:

    long        _exclusionId;       // This is an index column.  Not used by exclusion logic.
    long        _paoId;             // This is the pao which "owns" this exclusion information.  It may apply to this pao too.
    long        _excludedPaoId;     // This is the pao which is excluded if the logic reports a "yes".  It may be the pao.
    long        _pointId;           // This is a pointid which may figure into the exclusion question.
    double      _value;             // This is a value which may relate to the point, or to the functionid.
    long        _functionId;        // function id represents the type of exclusion that is defined by this data set.
    RWCString   _funcName;          // a rwcstring which can represent a dynamicly loaded function (future)
    long        _funcRequeue;       // This value indicates the requeue behaviour to execute if excluded.
    RWCString   _funcParams;        // a rwcstring which can represent arguments to a function (to be parsed by that function)

private:

public:

    CtiTablePaoExclusion(long xid = 0,
                         long paoid = 0,
                         long excludedpaoid = 0,
                         long pointid = 0,
                         double value = 0.0,
                         long function = 0,
                         RWCString str = RWCString(),
                         long funcrequeue = 0);

    CtiTablePaoExclusion(const CtiTablePaoExclusion& aRef);

    bool operator<(const CtiTablePaoExclusion &rhs) const;

    long getExclusionId() const;
    CtiTablePaoExclusion& setExclusionId(long xid);

    long getPaoId() const;
    CtiTablePaoExclusion& setPaoId(long val);

    long getExcludedPaoId() const;
    CtiTablePaoExclusion& setExcludedPaoId(long val);

    long getPointId() const;
    CtiTablePaoExclusion& setPointId(long val);

    double getValue() const;
    CtiTablePaoExclusion& setValue(double val);

    long getFunctionId() const;
    CtiTablePaoExclusion& setFunctionId(long val);

    RWCString getFunctionName() const;
    CtiTablePaoExclusion& setFunctionName(RWCString val);

    long getFunctionRequeue() const;
    CtiTablePaoExclusion& setFunctionRequeue(long val);

    RWCString getFunctionParams() const;
    CtiTablePaoExclusion& setFunctionParams(RWCString val);

    static RWCString getTableName();
    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Insert(RWDBConnection &conn);
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

    void dump() const;

    enum
    {
        RequeueNextExecutableOM,        // Find and substitute next executable OM with the highest priority.
        RequeueThisCommandNext,         // Maintain the Q's order.  This message must go next.
        RequeueQueuePriority            // Requeue this OM.  If there are any messages of equal or higher priorty, they will be selected first.

    } CtiExclusionRequeue_t;

    enum
    {
        ExFunctionIdExclusion,          // This is the default and stipulates a non-simultaneous execution.  A cannot execute with B.
        ExFunctionPlaceHolder,          //
        ExFunctionTimeMethod1           // Excludes based upon an aligned windo offset and duration from aligned window start.

    } CtiExclusionFunction_t;

};
#endif // #ifndef __TBL_PAOEXCLUSION_H__
