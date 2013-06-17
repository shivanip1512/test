#include "MsgCapControlMessage.h"

class CtiCCObjectMoveMsg : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( CtiCCObjectMoveMsg )

    private:
        typedef CapControlMessage Inherited;

    public:
        CtiCCObjectMoveMsg(bool permanentflag, long oldparentid, long objectid, long newparentid,
                           float switchingorder, float closeOrder = 0, float tripOrder = 0);
        virtual ~CtiCCObjectMoveMsg();

        bool getPermanentFlag() const;
        long getOldParentId() const;
        long getObjectId() const;
        long getNewParentId() const;
        float getSwitchingOrder() const;
        float getCloseOrder() const;
        float getTripOrder() const;

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        CtiCCObjectMoveMsg& operator=(const CtiCCObjectMoveMsg& right);
        virtual CtiMessage* replicateMessage() const;

    private:
        CtiCCObjectMoveMsg() { }; //provided for polymorphic persitence only

        bool _permanentflag;
        long _oldparentid;
        long _objectid;
        long _newparentid;
        float _switchingorder;
        float _closeOrder;
        float _tripOrder;
};
