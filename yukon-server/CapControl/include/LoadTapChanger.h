#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include <map>
#include <string>

#include "LtcPointHolder.h"
#include "CapControlPao.h"
#include "UpdatablePao.h"

class LoadTapChanger : public CapControlPao, public UpdatablePao
{
    private:
        LtcPointHolder _pointValues;

        typedef CapControlPao Inherited;

    public:
        LoadTapChanger();
        LoadTapChanger(RWDBReader& rdr);

        LtcPointHolder& getPointValueHolder();

        //From UpdatablePao Interface
        virtual void handlePointData(CtiPointDataMsg* message);

};

typedef LoadTapChanger* LoadTapChangerPtr;
