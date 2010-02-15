
/*---------------------------------------------------------------------------
        Filename:  ccspecial.h

        Programmer:  Josh Wolberg

        Description:    Header file for CtiCCSpecial
                        CtiCCSpecial maintains the state and handles
                        the persistence of strategies for Cap Control.

        Initial Date:  8/27/2007

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCSPECIALIMPL_H
#define CTICCSPECIALIMPL_H

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
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "StrategyManager.h"
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"
#include "Controllable.h"

class CtiCCSpecial : public RWCollectable, public Controllable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSpecial )

    CtiCCSpecial();
    CtiCCSpecial(StrategyManager * strategyManager);
    CtiCCSpecial(RWDBReader& rdr, StrategyManager * strategyManager);
    CtiCCSpecial(const CtiCCSpecial& area);

    virtual ~CtiCCSpecial();

    LONG getVoltReductionControlPointId() const;
    BOOL getVoltReductionControlValue() const;

    BOOL getOvUvDisabledFlag() const;
    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;

    CtiCCSpecial& setVoltReductionControlPointId(LONG pointId);
    CtiCCSpecial& setVoltReductionControlValue(BOOL flag);

    CtiCCSpecial& setOvUvDisabledFlag(BOOL flag);
    CtiCCSpecial& setPFactor(DOUBLE pfactor);
    CtiCCSpecial& setEstPFactor(DOUBLE estpfactor);

    list <LONG>* getSubstationIds() {return &_substationIds;};
    list <LONG>* getPointIds() {return &_pointIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCSpecial& operator=(const CtiCCSpecial& right);

    CtiCCSpecial* replicate() const;

private:

    LONG _voltReductionControlPointId;
    BOOL _voltReductionControlValue;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;

    std::list <LONG> _substationIds;
    BOOL _isSpecial;

    std::list <long> _pointIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

   //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);

};


//typedef shared_ptr<CtiCCSpecial> CtiCCSpecialPtr;
typedef CtiCCSpecial* CtiCCSpecialPtr;
#endif

