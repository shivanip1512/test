#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "pointtypes.h"
#include <map>

using std::map;

class PointOffestValueHolder
{
    public:
        PointOffestValueHolder();

        virtual void addPointOffset(int offset, CtiPointType_t type, int pointId, int pointValue);

        bool getPointValueByOffsetAndType(int offset, CtiPointType_t type, int& result);
        bool getPointIdByOffsetAndType(int offset, CtiPointType_t type, int& result);

        struct PointOffsetTypePair
        {
            PointOffsetTypePair()
            {
               pointOffset = 0;
               pointType = InvalidPointType;
            }
            PointOffsetTypePair(int offset, CtiPointType_t type)
            {
               pointOffset = offset;
               pointType = type;
            }

            bool operator < (const PointOffsetTypePair& rhs) const
            {
                if (pointOffset < rhs.pointOffset) {
                    return true;
                }
                if (pointOffset == rhs.pointOffset) {
                    if (pointType < rhs.pointType) {
                        return true;
                    }
                }
                return false;
            }

            int pointOffset;
            CtiPointType_t pointType;
        };

    private:

        struct PointIdValuePair {
            int pointId;
            int pointValue;
        };

        typedef std::map<PointOffsetTypePair,PointIdValuePair> OffsetMap;
        typedef std::map<PointOffsetTypePair,PointIdValuePair>::iterator OffsetMapItr;

        OffsetMap _offsetMap;

};


