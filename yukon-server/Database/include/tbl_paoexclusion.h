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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PAOEXCLUSION_H__
#define __TBL_PAOEXCLUSION_H__

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
    string   _funcName;          // a string which can represent a dynamicly loaded function (future)
    long        _funcRequeue;       // This value indicates the requeue behaviour to execute if excluded.
    string   _funcParams;        // a string which can represent arguments to a function (to be parsed by that function)

    // This is the componentry of the _funcParams, i.f.f. the function is a Time Method
    int         _cycleTime;
    int         _cycleOffset;
    int         _transmitTime;
    int         _maxTransmitTime;

private:

public:

    CtiTablePaoExclusion(long xid = 0,
                         long paoid = 0,
                         long excludedpaoid = 0,
                         long pointid = 0,
                         double value = 0.0,
                         long function = ExFunctionInvalid,
                         string str = string(),
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

    string getFunctionName() const;
    CtiTablePaoExclusion& setFunctionName(string val);

    long getFunctionRequeue() const;
    CtiTablePaoExclusion& setFunctionRequeue(long val);

    string getFunctionParams() const;
    CtiTablePaoExclusion& setFunctionParams(string val);

    static string getTableName();
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
        ExFunctionCycleTime,            // Excludes based upon an aligned window offset and duration from aligned window start.
        ExFunctionLMSubordination,      // Used by LM to determin which groups/programs are subordinate to others.

        ExFunctionInvalid

    } CtiExclusionFunction_t;

    int getCycleTime() const;
    CtiTablePaoExclusion &setCycleTime(int cycletime);
    int getCycleOffset() const;
    CtiTablePaoExclusion &setCycleOffset(int cycleoffset);
    int getTransmitTime() const;
    CtiTablePaoExclusion &setTransmitTime(int transmittime);
    int getMaxTransmitTime() const;
    CtiTablePaoExclusion &setMaxTransmitTime(int maxtransmittime);

};
#endif // #ifndef __TBL_PAOEXCLUSION_H__
