
/*---------------------------------------------------------------------------
        Filename:  ccoriginalparent.cpp
        
        Programmer:  Julie Richter
                
        Description:    CtiCCOriginalParent
                        

        Initial Date:  10/30/2009
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

#include "yukon.h"
#include "ccoriginalparent.h"
#include "ccid.h"
#include "row_reader.h"
#include "database_writer.h"

extern ULONG _CC_DEBUG;
 

CtiCCOriginalParent::CtiCCOriginalParent()
{
    _paoId                  = 0;
    _originalParentId       = 0;
    _originalSwitchingOrder = 0;
    _originalCloseOrder     = 0;
    _originalTripOrder      = 0;
    _dirty = true;
    _insertDynamicDataFlag = true;
};


CtiCCOriginalParent::CtiCCOriginalParent(Cti::RowReader& rdr)
{
    restore(rdr);
};

CtiCCOriginalParent::~CtiCCOriginalParent()
{
};

CtiCCOriginalParent& CtiCCOriginalParent::operator=(const CtiCCOriginalParent& right)
{
    if( this != &right )
    {
       _paoId                   = right._paoId;                 
       _originalParentId        = right._originalParentId;      
       _originalSwitchingOrder  = right._originalSwitchingOrder; 
       _originalCloseOrder      = right._originalCloseOrder; 
       _originalTripOrder       = right._originalTripOrder; 
       _dirty                   = right._dirty;
       _insertDynamicDataFlag   = right._insertDynamicDataFlag;
    }
    return *this;
}

int CtiCCOriginalParent::operator==(const CtiCCOriginalParent& right) const
{
    return getPAOId() == right.getPAOId();
}
int CtiCCOriginalParent::operator!=(const CtiCCOriginalParent& right) const
{
    return getPAOId() != right.getPAOId();
}



LONG CtiCCOriginalParent::getPAOId() const
{
    return _paoId;
}

LONG CtiCCOriginalParent::getOriginalParentId() const
{
    return _originalParentId;
}

float CtiCCOriginalParent::getOriginalSwitchingOrder() const
{
    return _originalSwitchingOrder;
}
float CtiCCOriginalParent::getOriginalCloseOrder() const
{
    return _originalCloseOrder;
}
float CtiCCOriginalParent::getOriginalTripOrder() const
{
    return _originalTripOrder;
}

void CtiCCOriginalParent::setPAOId(LONG paoId)
{
    if( _paoId != paoId )
    {
        _dirty = TRUE;
    }
    _paoId = paoId;
}

void CtiCCOriginalParent::setOriginalParentId(LONG parentId)
{
    if( _originalParentId != parentId )
    {
        _dirty = TRUE;
    }
    _originalParentId = parentId;
}


void CtiCCOriginalParent::setOriginalSwitchingOrder(float order)
{
    if( _originalSwitchingOrder != order )
    {
        _dirty = TRUE;
    }
    _originalSwitchingOrder = order;
}

void CtiCCOriginalParent::setOriginalCloseOrder(float order)
{
    if( _originalCloseOrder != order )
    {
        _dirty = TRUE;
    }
    _originalCloseOrder = order;
}

void CtiCCOriginalParent::setOriginalTripOrder(float order)
{
    if( _originalTripOrder != order )
    {
        _dirty = TRUE;
    }
    _originalTripOrder = order;
}



void CtiCCOriginalParent::restore(Cti::RowReader& rdr)
{
    rdr["originalparentid"] >> _originalParentId;
    rdr["originalswitchingorder"] >>  _originalSwitchingOrder;
    rdr["originalcloseorder"] >>  _originalCloseOrder;
    rdr["originaltriporder"] >>  _originalTripOrder;

    _insertDynamicDataFlag = false;
    _dirty = false;

}

BOOL CtiCCOriginalParent::isDirty()
{
    return _dirty;
}
void CtiCCOriginalParent::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    {
        if( !_insertDynamicDataFlag )
        {
            static const string updaterSql = "update dynamicccoriginalparent set "
                                         "originalparentid = ?, "
                                         "originalswitchingorder = ?, "
                                         "originalcloseorder = ?, "
                                         "originaltriporder = ? "
                                         " where paobjectid = ?";
            Cti::Database::DatabaseWriter updater(conn, updaterSql);

            updater << _originalParentId
                    << _originalSwitchingOrder
                    << _originalCloseOrder
                    << _originalTripOrder
                    << _paoId;

            if(updater.execute())    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    string loggedSQLstring = updater.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted CC Original Parent Info into DynamicCtiCCOriginalParentInfo: " << getPAOId() << endl;
            }
            static const string inserterSql = "insert into dynamicccoriginalparent values (?, ?, ?, ? ?)";
            Cti::Database::DatabaseWriter inserter(conn, inserterSql);
            
            inserter << _paoId
                    << _originalParentId
                    << _originalSwitchingOrder
                    << _originalCloseOrder
                    << _originalTripOrder;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = inserter.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            if(inserter.execute())    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    string loggedSQLstring = inserter.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
    }

}
