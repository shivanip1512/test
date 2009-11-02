
/*---------------------------------------------------------------------------
        Filename:  ccoriginalparent.h
        
        Programmer:  Julie Richter
                
        Description:    Header file for CCOriginalParent
                        

        Initial Date:  2/24/2009
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

#ifndef CCORIGINALPARENT_H
#define CCORIGINALPARENT_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "dbaccess.h"
#include "types.h"
#include "ctitime.h"
#include "logger.h"

class CtiCCOriginalParent
{
public:
    CtiCCOriginalParent();
    CtiCCOriginalParent(RWDBReader& rdr);
    ~CtiCCOriginalParent();

    LONG getPAOId() const;
    LONG getOriginalParentId() const;
    float getOriginalSwitchingOrder() const;
    float getOriginalCloseOrder() const;
    float getOriginalTripOrder() const;

    void setPAOId(LONG paoId);
    void setOriginalParentId(LONG parentId);
    void setOriginalSwitchingOrder(float order);
    void setOriginalCloseOrder(float order);
    void setOriginalTripOrder(float order);


    CtiCCOriginalParent& operator=(const CtiCCOriginalParent& right);
    int operator==(const CtiCCOriginalParent& right) const;
    int operator!=(const CtiCCOriginalParent& right) const;

    BOOL isDirty();
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);


    void restore(RWDBReader& rdr);

private:

    LONG _paoId;       
    LONG _originalParentId;  
    float _originalSwitchingOrder; 
    float _originalCloseOrder; 
    float _originalTripOrder; 
    
    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty; 
};


#endif

