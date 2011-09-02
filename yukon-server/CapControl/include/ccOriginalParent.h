#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "dbaccess.h"
#include "types.h"
#include "ctitime.h"
#include "logger.h"
#include "row_reader.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

class CtiCCOriginalParent
{
public:
    CtiCCOriginalParent();
    CtiCCOriginalParent(Cti::RowReader& rdr);
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
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);


    void restore(Cti::RowReader& rdr);

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
