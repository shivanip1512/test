#pragma once

#include "message.h"

class CapControlMessage : public CtiMessage
{
    private:
        typedef CtiMessage Inherited;

    public:
        CapControlMessage();
        virtual ~CapControlMessage();

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        CapControlMessage& operator=(const CapControlMessage& right);
};
