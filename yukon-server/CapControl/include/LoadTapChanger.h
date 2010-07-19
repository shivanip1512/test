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

class LoadTapChanger : public RWCollectable, public CapControlPao, public UpdatablePao
{
    public:
        enum TapOperation
        {
            None,
            LowerTap,
            RaiseTap
        };

    private:
        LitePoint _ltcVoltagePoint;
        LitePoint _lowerTapPoint;
        LitePoint _raiseTapPoint;
        LitePoint _autoRemotePoint;
        LitePoint _tapPositionPoint;

        bool _updated;
        bool _manualLocalMode;

        bool _lowerTap;
        bool _raiseTap;
        bool _autoRemote;

        CtiTime _lastTapOperationTime;
        TapOperation _lastTapOperation;

        PointValueHolder _pointValues;

    public:

        RWDECLARE_COLLECTABLE( LoadTapChanger )

        LoadTapChanger();
        LoadTapChanger(Cti::RowReader& rdr);
        LoadTapChanger(const LoadTapChanger& ltc);

        PointValueHolder& getPointValueHolder();

        //Change to inherit from an interface?
        bool getPointValue(int pointId, double& value);

        //From UpdatablePao Interface
        virtual void handlePointData(CtiPointDataMsg* message);

        bool isUpdated();
        void setUpdated(bool updated);

        bool isManualLocalMode();
        void setManualLocalMode(bool manualLocalMode);

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

        void notifyTapOperation(TapOperation operation, CtiTime timeStamp);
        void updateFlags();

        LoadTapChanger& operator=(const LoadTapChanger& right);
};

typedef LoadTapChanger* LoadTapChangerPtr;
