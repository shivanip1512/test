#pragma once

#include "row_reader.h"
#include "loggable.h"


class IM_EX_CTIYUKONDB CtiTablePaoExclusion : public Cti::Loggable
{
    long        _exclusionId;       // This is an index column.  Not used by exclusion logic.
    long        _paoId;             // This is the pao which "owns" this exclusion information.  It may apply to this pao too.
    long        _excludedPaoId;     // This is the pao which is excluded if the logic reports a "yes".  It may be the pao.
    double      _value;             // This is a value which may relate to the point, or to the functionid.
    long        _functionId;        // function id represents the type of exclusion that is defined by this data set.
    std::string _funcName;          // a string which can represent a dynamicly loaded function (future)
    long        _funcRequeue;       // This value indicates the requeue behaviour to execute if excluded.
    std::string _funcParams;        // a string which can represent arguments to a function (to be parsed by that function)

    // This is the componentry of the _funcParams, i.f.f. the function is a Time Method
    int         _cycleTime;
    int         _cycleOffset;
    int         _transmitTime;
    int         _maxTransmitTime;

public:

    CtiTablePaoExclusion(long xid = 0,
                         long paoid = 0,
                         long excludedpaoid = 0,
                         long pointid = 0,
                         double value = 0.0,
                         long function = ExFunctionInvalid,
                         std::string str = std::string(),
                         long funcrequeue = 0);

    CtiTablePaoExclusion(const CtiTablePaoExclusion& aRef);

    bool operator<(const CtiTablePaoExclusion &rhs) const;

    long getExclusionId() const;

    long getPaoId() const;

    long getExcludedPaoId() const;

    double getValue() const;

    long getFunctionId() const;

    std::string getFunctionName() const;

    long getFunctionRequeue() const;

    static std::string getSQLCoreStatement(long id = 0);

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual std::string toString() const override;

    enum
    {
        RequeueNextExecutableOM,        // Find and substitute next executable OM with the highest priority.
        RequeueThisCommandNext,         // Maintain the Q's order.  This message must go next.
        RequeueQueuePriority            // Requeue this OM.  If there are any messages of equal or higher priorty, they will be selected first.

    };

    enum
    {
        ExFunctionIdExclusion,          // This is the default and stipulates a non-simultaneous execution.  A cannot execute with B.
        ExFunctionPlaceHolder,          //
        ExFunctionCycleTime,            // Excludes based upon an aligned window offset and duration from aligned window start.
        ExFunctionLMSubordination,      // Used by LM to determin which groups/programs are subordinate to others.

        ExFunctionInvalid

    };

    int getCycleTime() const;
    CtiTablePaoExclusion &setCycleTime(int cycletime);
    int getCycleOffset() const;
    CtiTablePaoExclusion &setCycleOffset(int cycleoffset);
    int getTransmitTime() const;
    CtiTablePaoExclusion &setTransmitTime(int transmittime);
    int getMaxTransmitTime() const;

};
