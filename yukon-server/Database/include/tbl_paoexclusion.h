#pragma once

#include "row_reader.h"
#include "rwutil.h"

class IM_EX_CTIYUKONDB CtiTablePaoExclusion
{
protected:

    long        _exclusionId;       // This is an index column.  Not used by exclusion logic.
    long        _paoId;             // This is the pao which "owns" this exclusion information.  It may apply to this pao too.
    long        _excludedPaoId;     // This is the pao which is excluded if the logic reports a "yes".  It may be the pao.
    long        _pointId;           // This is a pointid which may figure into the exclusion question.
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

private:

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

    std::string getFunctionName() const;
    CtiTablePaoExclusion& setFunctionName(std::string val);

    long getFunctionRequeue() const;
    CtiTablePaoExclusion& setFunctionRequeue(long val);

    std::string getFunctionParams() const;
    CtiTablePaoExclusion& setFunctionParams(std::string val);

    static std::string getSQLCoreStatement(long id = 0);

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    void dump() const;

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
    CtiTablePaoExclusion &setMaxTransmitTime(int maxtransmittime);

};
