
/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.h

        Programmer:  Josh Wolberg

        Description:    Header file for CtiCCSubstationBus
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of strategies for Cap Control.

        Initial Date:  8/27/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCAREAIMPL_H
#define CTICCAREAIMPL_H

#include <list>
using std::list;

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>
#include <list>
#include <vector>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccsubstationbus.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "ControlStrategy.h"
#include "ccmonitorpoint.h"
#include "Controllable.h"

typedef std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBus_vec;


class CtiCCArea : public RWCollectable, public Controllable
{

public:

RWDECLARE_COLLECTABLE( CtiCCArea )

    CtiCCArea();
    CtiCCArea(RWDBReader& rdr, StrategyPtr strategy);
    CtiCCArea(const CtiCCArea& area);

    virtual ~CtiCCArea();

    LONG getVoltReductionControlPointId() const;
    BOOL getVoltReductionControlValue() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getReEnableAreaFlag() const;

    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;
    BOOL getChildVoltReductionFlag() const;
    std::list<long>* getSubStationList(){return &_subStationIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    list <LONG>* getPointIds() {return &_pointIds;};
    BOOL getAreaUpdatedFlag() const;

    void deleteCCSubs(long subId);

    CtiCCArea& setVoltReductionControlPointId(LONG pointId);
    CtiCCArea& setVoltReductionControlValue(BOOL flag);
    CtiCCArea& setOvUvDisabledFlag(BOOL flag);
    CtiCCArea& setReEnableAreaFlag(BOOL flag);

    CtiCCArea& setPFactor(DOUBLE pfactor);
    CtiCCArea& setEstPFactor(DOUBLE estPfactor);
    CtiCCArea& setChildVoltReductionFlag(BOOL flag);
    CtiCCArea& setAreaUpdatedFlag(BOOL flag);

    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    CtiCCArea& checkAndUpdateChildVoltReductionFlags();

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCArea& operator=(const CtiCCArea& right);

    CtiCCArea* replicate() const;

private:

    LONG _voltReductionControlPointId;
    BOOL _voltReductionControlValue;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;
    BOOL _reEnableAreaFlag;
    BOOL _childVoltReductionFlag;

    std::list<long> _subStationIds;
    std::list <long> _pointIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

       //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;
    BOOL _areaUpdatedFlag;

    void restore(RWDBReader& rdr);


};


//typedef shared_ptr<CtiCCArea> CtiCCAreaPtr;
typedef CtiCCArea* CtiCCAreaPtr;
#endif
