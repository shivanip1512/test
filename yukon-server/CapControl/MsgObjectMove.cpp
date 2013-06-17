#include "precompiled.h"

#include "MsgObjectMove.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( CtiCCObjectMoveMsg, CTICCOBJECTMOVEMSG_ID )

CtiCCObjectMoveMsg::CtiCCObjectMoveMsg(bool permanentflag, long oldparentid, long objectid, long newparentid,
                   float switchingorder, float closeOrder, float tripOrder) :
    Inherited(),
    _permanentflag(permanentflag),
    _oldparentid(oldparentid),
    _objectid(objectid),
    _newparentid(newparentid),
    _switchingorder(switchingorder),
    _closeOrder(closeOrder),
    _tripOrder(tripOrder)
{

}

CtiCCObjectMoveMsg::~CtiCCObjectMoveMsg()
{

}

bool CtiCCObjectMoveMsg::getPermanentFlag() const
{
    return _permanentflag;
}

long CtiCCObjectMoveMsg::getOldParentId() const
{
    return _oldparentid;
}

long CtiCCObjectMoveMsg::getObjectId() const
{
    return _objectid;
}

long CtiCCObjectMoveMsg::getNewParentId() const
{
    return _newparentid;
}

float CtiCCObjectMoveMsg::getSwitchingOrder() const
{
    return _switchingorder;
}

float CtiCCObjectMoveMsg::getCloseOrder() const
{
    return _closeOrder;
}

float CtiCCObjectMoveMsg::getTripOrder() const
{
    return _tripOrder;
}

void CtiCCObjectMoveMsg::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);

    strm >> _permanentflag
         >> _oldparentid
         >> _objectid
         >> _newparentid
         >> _switchingorder
         >> _closeOrder
         >> _tripOrder;

    return;
}

void CtiCCObjectMoveMsg::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);

    strm << _permanentflag
         << _oldparentid
         << _objectid
         << _newparentid
         << _switchingorder
         << _closeOrder
         << _tripOrder;

    return;
}

CtiCCObjectMoveMsg& CtiCCObjectMoveMsg::operator=(const CtiCCObjectMoveMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);

        _permanentflag   = right._permanentflag;
        _oldparentid     = right._oldparentid;
        _objectid        = right._objectid;
        _newparentid     = right._newparentid;
        _switchingorder  = right._switchingorder;
        _closeOrder      = right._closeOrder;
        _tripOrder       = right._tripOrder;

    }

    return *this;
}

CtiMessage* CtiCCObjectMoveMsg::replicateMessage() const
{
    return new CtiCCObjectMoveMsg(*this);
}
