#include "MsgItemCommand.h"


class CtiCCCapBankMoveMsg : public ItemCommand
{
    RWDECLARE_COLLECTABLE( CtiCCCapBankMoveMsg )

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

        void restoreGuts(RWvistream&);
        void saveGuts(RWvostream&) const;

        CtiCCCapBankMoveMsg& operator=(const CtiCCCapBankMoveMsg& right);

    private:
        //provided for polymorphic persitence only
        CtiCCCapBankMoveMsg(){};

        int _permanentflag;
        long _oldfeederid;
        long _newfeederid;
        float _capswitchingorder;
        float _closeOrder;
        float _tripOrder;
};
