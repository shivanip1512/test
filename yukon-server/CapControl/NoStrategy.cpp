
#include "precompiled.h"

#include "NoStrategy.h"


NoStrategy::NoStrategy()
    : ControlStrategy()
{
    // empty!
}


NoStrategy::~NoStrategy()
{
    // empty!
}


const ControlStrategy::ControlUnitType NoStrategy::getUnitType() const
{
    return ControlStrategy::None;
}


const std::string NoStrategy::getControlUnits() const
{
    return ControlStrategy::NoControlUnit;
}


const ControlStrategy::ControlMethodType NoStrategy::getMethodType() const
{
    return ControlStrategy::NoMethod;
}


const std::string NoStrategy::getControlMethod() const
{
    return ControlStrategy::NoControlMethod;
}


void NoStrategy::setControlMethod(const std::string & method)
{
    // empty!
}


void NoStrategy::restoreParameters(const std::string &name, const std::string &type, const std::string &value)
{
    // empty!
}


void NoStrategy::execute()
{
    // empty!
}

