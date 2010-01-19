#pragma warning( disable : 4786)
#pragma once

#include <string>
#include "dlldefs.h"

#include "pointtypes.h"

class IM_EX_CTIBASE LitePoint
{
    public:
        LitePoint();
        LitePoint(const LitePoint& point);
        ~LitePoint();

        void setPointId(int pointId);
        int getPointId();

        void setPointType(CtiPointType_t pointType);
        CtiPointType_t getPointType();

        void setPointName(const std::string& pointName);
        std::string getPointName() const;

        void setPaoId(int paoId);
        int getPaoId();

        void setPointOffset(int pointOffset);
        int getPointOffset();

        LitePoint& operator=(const LitePoint& right);

    private:

        int _pointId;
        CtiPointType_t _pointType;
        std::string _pointName;
        int _paoId;
        int _pointOffset;

};
