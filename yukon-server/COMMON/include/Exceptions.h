#pragma once

#include "yukon.h"

#include <exception>

class NotFoundException : public std::exception
{
};

class MissingConfigException : public std::exception
{
};

namespace Cti {

struct YukonErrorException : std::exception
{
    YukonErrorException(YukonError_t code, std::string description) :
        error_code(code),
        error_description(description)
    {
    }

    std::string error_description;
    YukonError_t error_code;

    virtual const char *what() const
    {
        return error_description.c_str();
    }
};

}
