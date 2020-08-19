#pragma once

#include <string>

namespace Cti::Messaging::FieldSimulator {

struct Settings
{
    std::string deviceGroup;
    int deviceConfigFailureRate;
};

struct StatusRequestMsg
{
    //  empty
};

struct StatusResponseMsg
{
    Settings settings;
};

struct ModifyConfigurationRequestMsg
{
    Settings settings;
};

struct ModifyConfigurationResponseMsg
{
    Settings settings;
    bool success;
};

}