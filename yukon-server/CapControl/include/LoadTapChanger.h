#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include <map>
#include <string>

#include "LitePoint.h"
#include "PointValueHolder.h"
#include "CapControlPao.h"
#include "UpdatablePao.h"

class LoadTapChanger : public CapControlPao, public UpdatablePao
{
    private:
        LitePoint _ltcVoltagePoint;
        LitePoint _lowerTapPoint;
        LitePoint _raiseTapPoint;
        LitePoint _autoRemotePoint;
        LitePoint _tapPositionPoint;

        PointValueHolder _pointValues;

    public:
        LoadTapChanger();
        LoadTapChanger(RWDBReader& rdr);

        PointValueHolder& getPointValueHolder();

        //Change to inherit from an interface?
        bool getPointValue(int pointId, double& value);

        //From UpdatablePao Interface
        virtual void handlePointData(CtiPointDataMsg* message);

        void setLtcVoltagePoint(const LitePoint& point);
        const LitePoint& getLtcVoltagePoint();

        void setLowerTapPoint(const LitePoint& point);
        const LitePoint& getLowerTapPoint();

        void setRaiseTapPoint(const LitePoint& point);
        const LitePoint& getRaiseTapPoint();

        void setAutoRemotePoint(const LitePoint& point);
        const LitePoint& getAutoRemotePoint();

        void setTapPosition(const LitePoint& point);
        const LitePoint& getTapPosition();

        void getRegistrationPoints(std::set<long>& regPoints);
};

typedef LoadTapChanger* LoadTapChangerPtr;
