#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include <map>
#include <string>
#include <rw/collect.h>

#include "LitePoint.h"
#include "PointValueHolder.h"
#include "CapControlPao.h"
#include "UpdatablePao.h"

class LoadTapChanger;
typedef LoadTapChanger* LoadTapChangerPtr;

class LoadTapChanger : public RWCollectable, public CapControlPao, public UpdatablePao
{
    private:
        LitePoint _ltcVoltagePoint;
        LitePoint _lowerTapPoint;
        LitePoint _raiseTapPoint;
        LitePoint _autoRemotePoint;
        LitePoint _tapPositionPoint;

        bool _updated;

        PointValueHolder _pointValues;

    public:

        RWDECLARE_COLLECTABLE( LoadTapChanger )

        LoadTapChanger();
        LoadTapChanger(RWDBReader& rdr);
        LoadTapChanger(const LoadTapChanger& ltc);

        PointValueHolder& getPointValueHolder();

        //Change to inherit from an interface?
        bool getPointValue(int pointId, double& value);

        //From UpdatablePao Interface
        virtual void handlePointData(CtiPointDataMsg* message);

        bool isUpdated();
        void setUpdated(bool updated);

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

        void saveGuts(RWvostream& ostrm) const;

        LoadTapChanger& operator=(const LoadTapChanger& right);
};
