#pragma once

#include "yukon.h"
#include "cctypes.h"

namespace Cti
{
class RowReader;
}


class CapControlPao
{
    private:
        int _paoId;
        std::string _paoCategory;
        std::string _paoClass;
        std::string _paoName;
        std::string _paoType;
        std::string _paoDescription;
        bool _disableFlag;

        long    _disabledStatePointId;
        Cti::CapControl::PointIdVector _pointIds;

    public:

        CapControlPao();
        CapControlPao(Cti::RowReader& rdr);
        ~CapControlPao();

        int getPaoId() const;
        void setPaoId(int paoId);

        const std::string& getPaoCategory() const;
        void setPaocategory(const std::string& paoCategory);

        const std::string& getPaoClass() const;
        void setPaoClass(const std::string& paoClass);

        const std::string getPaoName() const;
        void setPaoName(const std::string& paoName);

        const std::string& getPaoType() const;
        void setPaoType(const std::string& paoType);

        const std::string& getPaoDescription() const;
        void setPaoDescription(const std::string& description);

        bool getDisableFlag() const;
        void setDisableFlag(bool disableFlag, int priority = 7);

        CapControlPao& operator=(const CapControlPao& right);

        void restore(Cti::RowReader& rdr);

        bool operator == (const CapControlPao& right) const;
        bool operator != (const CapControlPao& right) const;

        void setDisabledStatePointId( const long newId, bool sendDisablePointMessage = false );
        long getDisabledStatePointId() const;

        Cti::CapControl::PointIdVector* getPointIds() {return &_pointIds;}
        void removePointId(long pId);
        void addPointId(const long ID);
};

