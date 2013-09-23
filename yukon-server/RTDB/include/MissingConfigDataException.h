#pragma once

#include <exception>

namespace Cti {
namespace Devices {

struct MissingConfigDataException : public std::exception
{
public:
    const std::string configKey;
    const std::string message;

    MissingConfigDataException( const std::string& key ) :
        configKey( key ),
        message( "Missing data for config key \"" + key + "\"." )
    {
    }

    virtual const char *what() const
    {
        return message.c_str();
    }
};

}
}
