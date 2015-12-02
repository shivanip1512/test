#pragma once

#include <exception>

namespace Cti {
namespace Devices {

/**
 * Exception thrown if config data is missing
 */
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

/**
 * Exception thrown if config data is invalid
 */
struct InvalidConfigDataException : public std::exception
{
public:
    const std::string configKey;
    const std::string cause;
    const std::string message;

    InvalidConfigDataException( const std::string& key, const std::string& cause_ ) :
        configKey( key ),
        cause( cause_ ),
        message( "Invalid data for config key \"" + key + "\" : " + cause_ + "." )
    {
    }

    virtual const char *what() const
    {
        return message.c_str();
    }
};

}
}
