#include "precompiled.h"

#include "error.h"
#include "yukon.h"
#include "constants.h"
#include "numstr.h"
#include "boostutil.h"

#include "std_helper.h"


std::vector<CtiError::error_info> CtiError::_unknownErrors;
std::map<YukonError_t, CtiError::error_info> CtiError::CtiErrors;

void CtiError::AddErrorInfo(YukonError_t code, ErrorTypes type, std::string description)
{
    CtiError::error_info errorInfo{ type, description };
    CtiErrors.emplace( code, errorInfo );
}

void CtiError::AddUnknownError(const ErrorTypes type, const std::string description)
{
    CtiError::error_info unknownErrorInfo{ type, description };
    CtiError::_unknownErrors.push_back( unknownErrorInfo );
}

//  Returns the error's description
std::string CtiError::GetErrorString(YukonError_t errorNumber)
{
    if (const boost::optional<CtiError::error_info> info = Cti::mapFind(CtiError::CtiErrors, errorNumber))
    {
        return info->description;
    }

    return "Unknown Error Code (" + CtiNumStr(errorNumber) + ")";
}

//  Returns the error's type
ErrorTypes CtiError::GetErrorType(YukonError_t errorNumber)
{
    if (const boost::optional<CtiError::error_info> info = Cti::mapFind(CtiError::CtiErrors, errorNumber))
    {
        return info->type;
    }

    return ERRTYPESYSTEM;
}

std::vector<CtiError::error_info> CtiError::GetUnknownErrors()
{
    return CtiError::_unknownErrors;
}
