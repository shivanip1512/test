#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include <map>
#include <string>

#include "LtcPointHolder.h"
#include "CapControlPao.h"

class LoadTapChanger : public CapControlPao
{
    private:
        LtcPointHolder _pointValues;

        typedef CapControlPao Inherited;

    public:
        LoadTapChanger();
        LoadTapChanger(RWDBReader& rdr);

        LtcPointHolder& getPointValueHolder();

};

typedef LoadTapChanger* LoadTapChangerPtr;
