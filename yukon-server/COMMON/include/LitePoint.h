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
               const std::string & stateOneControl,
               const double Multiplier,
               const int stateGroupId );

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

    void setControlType(CtiControlType_t controlType);
    CtiControlType_t getControlType() const;

    void setCloseTime1(int closeTime);
    int getCloseTime1() const;

    void setCloseTime2(int closeTime);
    int getCloseTime2() const;

    void setStateZeroControl(const std::string & stateZeroControl);
    std::string getStateZeroControl() const;

    void setStateOneControl(const std::string & stateOneControl);
    std::string getStateOneControl() const;

    void setMultiplier(const double multiplier);
    double getMultiplier() const;

    void setStateGroupId(const int stateGroupId);
    int getStateGroupId() const;

private:

    int _pointId;
    CtiPointType_t _pointType;
    std::string _pointName;
    int _paoId;
    int _pointOffset;
    int _controlOffset;

    CtiControlType_t _controlType;
    int _closeTime1;
    int _closeTime2;
    std::string _stateZeroControl;
    std::string _stateOneControl;

    double _multiplier;

    int _stateGroupId;
};
