#pragma once

#include <string>
#include "dlldefs.h"

#include "pointtypes.h"

class IM_EX_CTIBASE LitePoint
{
public:
    LitePoint();
    LitePoint( const int Id, const CtiPointType_t Type, const std::string & Name,
               const int PaoId, const int Offset,
               const std::string & stateZeroControl,
               const std::string & stateOneControl );

    void setPointId(int pointId);
    int getPointId() const;

    void setPointType(CtiPointType_t pointType);
    CtiPointType_t getPointType() const;

    void setPointName(const std::string& pointName);
    std::string getPointName() const;

    void setPaoId(int paoId);
    int getPaoId() const;

    void setPointOffset(int pointOffset);
    int getPointOffset() const;

    void setControlOffset(int controlOffset);
    int getControlOffset() const;

    void setStateZeroControl(const std::string & stateZeroControl);
    std::string getStateZeroControl() const;

    void setStateOneControl(const std::string & stateOneControl);
    std::string getStateOneControl() const;

private:

    int _pointId;
    CtiPointType_t _pointType;
    std::string _pointName;
    int _paoId;
    int _pointOffset;
    int _controlOffset;

    std::string _stateZeroControl;
    std::string _stateOneControl;
};
