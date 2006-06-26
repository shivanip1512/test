
/*---------------------------------------------------------------------------
        Filename:  ccmonitorpoint.h
        
        Programmer:  julie Richter
        
        Description:    Header file for CtiCCMonitorPoint
                        CtiCCMonitorPoint maintains the monitor point information
                        in multiple monitor point bus control.

        Initial Date:  8/30/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCMONITORPOINT_H
#define CTICCMONITORPOINT_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <list>

#include "dbaccess.h"
#include "observe.h"
#include "ctitime.h"

                
class CtiCCMonitorPoint : public RWCollectable
{

public:

  RWDECLARE_COLLECTABLE( CtiCCMonitorPoint )
    CtiCCMonitorPoint();
    CtiCCMonitorPoint(RWDBReader& rdr);
    CtiCCMonitorPoint(const CtiCCMonitorPoint& point);

    virtual ~CtiCCMonitorPoint();

    LONG getPointId() const;
    LONG getBankId() const;
    DOUBLE getValue() const;
    LONG getDisplayOrder() const;
    BOOL isScannable() const;
    LONG getNInAvg() const;
    DOUBLE getUpperBandwidth() const;
    DOUBLE getLowerBandwidth() const;
    
    CtiTime getTimeStamp() const;
    BOOL getScanInProgress() const;

    CtiCCMonitorPoint& setPointId(LONG pointId);
    CtiCCMonitorPoint& setBankId(LONG bankId);
    CtiCCMonitorPoint& setValue(DOUBLE value);
    CtiCCMonitorPoint& setDisplayOrder(LONG displayOrder);
    CtiCCMonitorPoint& setScannable(BOOL flag);
    CtiCCMonitorPoint& setNInAvg(LONG n);
    CtiCCMonitorPoint& setUpperBandwidth(DOUBLE upperBW);
    CtiCCMonitorPoint& setLowerBandwidth(DOUBLE lowerBW);
    
    CtiCCMonitorPoint& setTimeStamp(CtiTime timeStamp);
    CtiCCMonitorPoint& setScanInProgress(BOOL flag);

    CtiCCMonitorPoint* replicate() const;
    virtual int compareTo(const RWCollectable* right) const;

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);

    void setDynamicData(RWDBReader& rdr);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCMonitorPoint& operator=(const CtiCCMonitorPoint& right);

    int operator==(const CtiCCMonitorPoint& right) const;
    int operator!=(const CtiCCMonitorPoint& right) const;


private:

    LONG _pointId;
    LONG _bankId;

    LONG _displayOrder;
    BOOL _scannable;
    LONG _nInAvg;
    DOUBLE _upperBW;
    DOUBLE _lowerBW;
    DOUBLE _value;
    CtiTime _timeStamp;  //averaged value change.
    BOOL _scanInProgress;                              

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    

};

typedef CtiCCMonitorPoint* CtiCCMonitorPointPtr;
#endif
