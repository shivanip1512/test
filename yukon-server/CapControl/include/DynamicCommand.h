#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include <map>

#include "MsgCapControlCommand.h"

//No namespace. RogueWave Pukes

class DynamicCommand : public CapControlCommand
{
    RWDECLARE_COLLECTABLE( DynamicCommand );

    private:
        typedef CapControlCommand Inherited;

    public:
       enum CommandType {
           UNDEFINED,
           DELTA
       };

       enum Parameter {
           DEVICE_ID,
           POINT_ID,
           POINT_RESPONSE_DELTA,
           POINT_RESPONSE_STATIC_DELTA
       };

       DynamicCommand();

       void restoreGuts(RWvistream& iStream);
       void saveGuts(RWvostream & oStream);

       bool getParameter(Parameter parameter, long& value);
       bool getParameter(Parameter parameter, double& value);
       CommandType getCommandType();

    private:
       CommandType _commandType;

       typedef std::map<int,long> LongParameterMap;
       typedef std::map<int,double> DoubleParameterMap;

       LongParameterMap _longParameters;
       DoubleParameterMap _doubleParameters;
};
