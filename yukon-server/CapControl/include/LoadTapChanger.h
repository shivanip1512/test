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
        LitePoint _upperVoltPoint;
        LitePoint _lowerVoltPoint;

        PointValueHolder _pointValues;

        typedef CapControlPao Inherited;

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

        void setUpperVoltPoint(const LitePoint& point);
        const LitePoint& getUpperVoltPoint();

        void setLowerVoltPoint(const LitePoint& point);
        const LitePoint& getLowerVoltPoint();

        void getRegistrationPoints(std::list<int>& regPoints);

        static const std::string LowerTapStr;
        static const std::string RaiseTapStr;
        static const std::string UpperVoltStr;
        static const std::string LowerVoltStr;
        static const std::string AutoRemoteStr;
        static const std::string LtcVoltageStr;
};

typedef LoadTapChanger* LoadTapChangerPtr;
