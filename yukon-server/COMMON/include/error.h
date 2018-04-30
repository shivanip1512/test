#pragma once

#include "dlldefs.h"
#include "yukon.h"

#include <string>

enum IM_EX_CTIBASE ErrorTypes
{
    ERRTYPENONE = 0,
    ERRTYPEUNKNOWN = 0,
    ERRTYPESYSTEM = 1,
    ERRTYPEPROTOCOL,
    ERRTYPECOMM,
};

class IM_EX_CTIBASE CtiError {
public:
    struct error_info {
        ErrorTypes type;
        std::string description;
    };

    static void AddErrorInfo(YukonError_t code, ErrorTypes type, std::string description);
    static ErrorTypes  GetErrorType(YukonError_t errorNumber);
    static std::string GetErrorString(YukonError_t errorNumber);

    static void AddUnknownError(const ErrorTypes type, const std::string description);
    static std::vector<error_info> GetUnknownErrors();

private:
    static std::map<YukonError_t, error_info> CtiErrors;
    static std::vector<error_info> _unknownErrors;
};
