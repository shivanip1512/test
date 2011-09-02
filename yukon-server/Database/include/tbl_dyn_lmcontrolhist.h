#pragma once

#include "tbl_lm_controlhist.h"

class CtiTableDynamicLMControlHistory : public CtiTableLMControlHistory
{
protected:

    private:

    public:

    typedef CtiTableLMControlHistory Inherited;

    CtiTableDynamicLMControlHistory(LONG               paoid   = 0,
                                    const CtiTime&     start   = CtiTime(),
                                    LONG               soe     = 0,
                                    INT                dur     = 0,
                                    const std::string& type    = std::string(),
                                    LONG               daily   = 0,
                                    LONG               month   = 0,
                                    LONG               season  = 0,
                                    LONG               annual  = 0,
                                    const std::string& restore = std::string(),
                                    DOUBLE             reduce  = 0.0,
                                    LONG               lmchid  = LMControlHistoryIdGen()) :
    Inherited(paoid, start, soe, dur, type, daily, month, season, annual, restore, reduce, lmchid)
    {}

    CtiTableDynamicLMControlHistory(const CtiTableDynamicLMControlHistory& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiTableDynamicLMControlHistory() {}

    static std::string getTableName()     // OverRide the parent call.  Our only purpose in life!
    {
        return std::string("DynamicLMControlHistory");
    }

};
