#pragma once

#include "MsgItemCommand.h"

class CtiCCCapBankMoveMsg : public ItemCommand
{
    public:
        DECLARE_COLLECTABLE( CtiCCCapBankMoveMsg )

    private:
        typedef ItemCommand Inherited;

    public:

        virtual ~CtiCCCapBankMoveMsg();

        int getPermanentFlag() const;
        long getOldFeederId() const;
        long getNewFeederId() const;
        float getCapSwitchingOrder() const;
        float getCloseOrder() const;
        float getTripOrder() const;

        CtiCCCapBankMoveMsg& operator=(const CtiCCCapBankMoveMsg& right);


        CtiCCCapBankMoveMsg( int   permanentflag,
                             long  oldfeederid,
                             long  newfeederid,
                             float capswitchingorder,
                             float closeOrder,
                             float tripOrder ) :
            _permanentflag      ( permanentflag ),
            _oldfeederid        ( oldfeederid ),
            _newfeederid        ( newfeederid ),
            _capswitchingorder  ( capswitchingorder ),
            _closeOrder         ( closeOrder ),
            _tripOrder          ( tripOrder)
        {};

    private:
        //provided for polymorphic persitence only
        CtiCCCapBankMoveMsg( ){};

        int _permanentflag;
        long _oldfeederid;
        long _newfeederid;
        float _capswitchingorder;
        float _closeOrder;
        float _tripOrder;
};
