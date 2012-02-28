#include "precompiled.h"

#include "DynamicCommand.h"
#include "ccid.h"
#include "rwutil.h"

using std::map;

RWDEFINE_COLLECTABLE( DynamicCommand, DYNAMICCOMMAND_ID )

DynamicCommand::DynamicCommand() : Inherited(), _commandType(DynamicCommand::UNDEFINED)
{

}

void DynamicCommand::restoreGuts(RWvistream& iStream)
{
    Inherited::restoreGuts(iStream);

    int commandType = 0;
    iStream >> commandType;
    _commandType = (DynamicCommand::CommandType)commandType;

    iStream >> _longParameters;
    iStream >> _doubleParameters;

    return;
}

bool DynamicCommand::getParameter(Parameter parameter, long& value)
{
    LongParameterMap::iterator intItr = _longParameters.find(parameter);

    if (intItr != _longParameters.end())
    {
        value = intItr->second;
        return true;
    }

    return false;
}

bool DynamicCommand::getParameter(Parameter parameter, double& value)
{
    DoubleParameterMap::iterator doubleItr = _doubleParameters.find(parameter);

    if (doubleItr != _doubleParameters.end())
    {
        value = doubleItr->second;
        return true;
    }

    return false;
}

DynamicCommand::CommandType DynamicCommand::getCommandType()
{
    return _commandType;
}
