#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include <map>
#include <string>

#include "PointOffsetValueHolder.h"

/**
 * LTC Devices do not have static point offsets so we are
 * storing the offsets by name instead of using the basic
 * PointOffestValueHolder
 *
 */
class LtcPointHolder : public PointOffestValueHolder
{
    public:
        LtcPointHolder();

        //Do something about the required points?
        virtual void addPointOffset(std::string name, int offset, CtiPointType_t type, int pointId, int pointValue);

        bool getPointValueByName(std::string name, int& value);

    private:
        typedef PointOffestValueHolder Inherited;

        typedef std::map<std::string,Inherited::PointOffsetTypePair> NameToOffsetMap;
        typedef std::map<std::string,Inherited::PointOffsetTypePair>::iterator NameToOffsetMapItr;

        NameToOffsetMap nameToOffset;
};
