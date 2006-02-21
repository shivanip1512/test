

/*---------------------------------------------------------------------------
        Filename:  ccpointresponse.h
        
        Programmer:  julie Richter
        
        Description:    Header file for CtiCCPointResponse
                        CtiCCPointResponse maintains the monitor point value 
                        
        Initial Date:  2/06/2006
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCPOINTRESPONSE_H
#define CTICCPOINTRESPNSE_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <list>

#include "dbaccess.h"
#include "observe.h"

                
class CtiCCPointResponse : public RWCollectable
{

public:

  RWDECLARE_COLLECTABLE( CtiCCPointResponse )

    CtiCCPointResponse(RWDBReader& rdr);
    CtiCCPointResponse(const CtiCCPointResponse& point);

    virtual ~CtiCCPointResponse();

    LONG getPointId() const;
    DOUBLE getPreOpValue() const;
    DOUBLE getDelta() const;

    CtiCCPointResponse& setPointId(LONG pointId);
    CtiCCPointResponse& setPreOpValue(DOUBLE value);
    CtiCCPointResponse& setDelta(DOUBLE delta);

    CtiCCPointResponse* replicate() const;

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCPointResponse& operator=(const CtiCCPointResponse& right);

    int operator==(const CtiCCPointResponse& right) const;
    int operator!=(const CtiCCPointResponse& right) const;


private:

    LONG _pointId;
    DOUBLE _preOpValue;
    DOUBLE _delta;
    
    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    CtiCCPointResponse();

};

typedef CtiCCPointResponse* CtiCCPointResponsePtr;
#endif
