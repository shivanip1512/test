#pragma once

#include <exception>
#include <string>

#include <boost/noncopyable.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

struct BaseCommand : boost::noncopyable
{
    virtual bool isComplete()
    {
        return true;
    }

    struct CommandException : std::exception
    {
        CommandException(int code, std::string description) :
            error_code(code),
            error_description(description)
        {
        }

        std::string error_description;
        int error_code;

        virtual const char *what() const
        {
            return error_description.c_str();
        }
    };
};

}
}
}
