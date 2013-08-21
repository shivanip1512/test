#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include <map>

#include "MsgCapControlCommand.h"

//No namespace. RogueWave Pukes

class DynamicCommand : public CapControlCommand
{
    public:
        DECLARE_COLLECTABLE( DynamicCommand );

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

       bool getParameter(Parameter parameter, long& value);
       bool getParameter(Parameter parameter, double& value);
       CommandType getCommandType();

       typedef std::map<int,long> LongParameterMap;
       typedef std::map<int,double> DoubleParameterMap;

    private:
       CommandType _commandType;
       LongParameterMap _longParameters;
       DoubleParameterMap _doubleParameters;

    public:
       // added for serialization
       void setCommandType      ( const CommandType ctype )                { _commandType = ctype; }
       void setLongParameters   ( const LongParameterMap& longParams )     { _longParameters = longParams; }
       void setDoubleParameters ( const DoubleParameterMap& doubleParams ) { _doubleParameters = doubleParams; }

};
