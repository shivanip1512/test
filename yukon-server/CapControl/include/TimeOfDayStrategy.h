
#pragma once

#include <string>
#include <map>

#include "ControlStrategy.h"


class TimeOfDayStrategy : public ControlStrategy
{

public:

    struct TimeOfDay
    {
        long    timeOffset;
        double  weekdayPercent;
        double  weekendPercent;
    };

    TimeOfDayStrategy();

    virtual ~TimeOfDayStrategy();

    virtual void execute();

    virtual void restoreParameters(const std::string &name, const std::string &type, const std::string &value);

    TimeOfDay getNextTimeOfDay(const long timeOffset) const;

    virtual void setControlMethod(const std::string & method);

    virtual const ControlStrategy::ControlMethodType getMethodType() const;

    virtual const std::string getControlMethod() const;

    virtual const ControlStrategy::ControlUnitType   getUnitType() const;

    virtual const std::string getControlUnits() const;

private:

    const long getTimeOffset(const std::string &inputTime) const;

    typedef std::map<long, TimeOfDay>   TimeOfDayMapping;

    TimeOfDayMapping    _timeOfDay;

};

