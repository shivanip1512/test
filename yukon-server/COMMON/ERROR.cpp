#include "precompiled.h"

#include "dsm2err.h"
#include "yukon.h"
#include "constants.h"
#include "numstr.h"
#include "boostutil.h"

#include "std_helper.h"

struct error_info
{
    ErrorTypes type;
    std::string description;
};

std::map<YukonError_t, error_info> CtiErrors;

void addErrorInfo( YukonError_t code, ErrorTypes type, std::string description )
{
    error_info infoStruct{ type, description };
    CtiErrors.emplace( code, infoStruct );
}


//  Returns the error's description
IM_EX_CTIBASE std::string GetErrorString(YukonError_t errorNumber)
{
    if( const boost::optional<error_info> info = Cti::mapFind(CtiErrors, errorNumber) )
    {
        return info->description;
    }

    return "Unknown Error Code (" + CtiNumStr(errorNumber) + ")";
}


//  Returns the error's type
IM_EX_CTIBASE ErrorTypes GetErrorType(YukonError_t errorNumber)
{
    if( const boost::optional<error_info> info = Cti::mapFind(CtiErrors, errorNumber) )
    {
        return info->type;
    }

    return ERRTYPESYSTEM;
}

