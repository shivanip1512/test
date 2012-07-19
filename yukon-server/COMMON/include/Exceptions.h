#pragma once

#include <exception>

class NotFoundException : public std::exception
{
};

class MissingConfigException : public std::exception
{
};
