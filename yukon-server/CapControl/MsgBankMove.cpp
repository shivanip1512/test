#include "precompiled.h"

#include "MsgBankMove.h"
#include "ccid.h"

DEFINE_COLLECTABLE( CtiCCCapBankMoveMsg, CTICCCAPBANKMOVEMSG_ID )

CtiCCCapBankMoveMsg::~CtiCCCapBankMoveMsg()
{

}

int CtiCCCapBankMoveMsg::getPermanentFlag() const
{
    return _permanentflag;
}

long CtiCCCapBankMoveMsg::getOldFeederId() const
{
    return _oldfeederid;
}

long CtiCCCapBankMoveMsg::getNewFeederId() const
{
    return _newfeederid;
}

float CtiCCCapBankMoveMsg::getCapSwitchingOrder() const
{
    return _capswitchingorder;
}

float CtiCCCapBankMoveMsg::getCloseOrder() const
{
    return _closeOrder;
}

float CtiCCCapBankMoveMsg::getTripOrder() const
{
    return _tripOrder;
}

CtiCCCapBankMoveMsg& CtiCCCapBankMoveMsg::operator=(const CtiCCCapBankMoveMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);

        _permanentflag    = right._permanentflag;
        _oldfeederid      = right._oldfeederid;
        _newfeederid      = right._newfeederid;
        _capswitchingorder= right._capswitchingorder;
        _closeOrder       = right._closeOrder;
        _tripOrder        = right._tripOrder;
    }

    return *this;
}
