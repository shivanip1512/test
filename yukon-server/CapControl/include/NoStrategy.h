/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#ifndef CTI_CONTROLSTRATEGY_NOSTRATEGY_H
#define CTI_CONTROLSTRATEGY_NOSTRATEGY_H

#include <string>

#include "ControlStrategies.h"


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

#endif

