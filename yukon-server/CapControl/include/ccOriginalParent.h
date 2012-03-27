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

    long getPAOId() const;
    long getOriginalParentId() const;
    float getOriginalSwitchingOrder() const;
    float getOriginalCloseOrder() const;
    float getOriginalTripOrder() const;

    void setPAOId(long paoId);
    void setOriginalParentId(long parentId);
    void setOriginalSwitchingOrder(float order);
    void setOriginalCloseOrder(float order);
    void setOriginalTripOrder(float order);


    CtiCCOriginalParent& operator=(const CtiCCOriginalParent& right);
    int operator==(const CtiCCOriginalParent& right) const;
    int operator!=(const CtiCCOriginalParent& right) const;

    bool isDirty();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);


    void restore(Cti::RowReader& rdr);

private:

    long _paoId;       
    long _originalParentId;  
    float _originalSwitchingOrder; 
    float _originalCloseOrder; 
    float _originalTripOrder; 
    
    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty; 
};
