
#pragma once

#include <string>

#include "ControlStrategy.h"


class NoStrategy : public ControlStrategy
{

public:

    NoStrategy();

    virtual ~NoStrategy();

    virtual void execute();

    virtual void restoreParameters(const std::string &name, const std::string &type, const std::string &value);

    virtual const ControlStrategy::ControlUnitType getUnitType() const;

    virtual const std::string getControlUnits() const;

    virtual const ControlStrategy::ControlMethodType getMethodType() const;

    virtual const std::string getControlMethod() const;

    virtual void setControlMethod(const std::string & method);
};

