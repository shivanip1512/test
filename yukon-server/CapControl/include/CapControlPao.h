#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"

#include <string>

#include <rw/db/db.h>
#include <rw/vstream.h>

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
        std::string _paoStatistics;

    public:

        CapControlPao();
        CapControlPao(RWDBReader& rdr);

        int getPaoId() const;
        void setPaoId(int paoId);

        const std::string& getPaoCategory();
        void setPaocategory(const std::string& paoCategory);

        const std::string& getPaoClass();
        void setPaoClass(const std::string& paoClass);

        const std::string& getPaoName();
        void setPaoName(const std::string& paoName);

        const std::string& getPaoType();
        void setPaoType(const std::string& paoType);

        const std::string& getPaoDescription();
        void setPaoDescription(const std::string& description);

        bool getDisableFlag();
        void setDisableFlag(bool disableFlag);

        const std::string& getPaoStatistics();
        void setPaoStatistics(const std::string& paoStatistics);

        CapControlPao& operator=(const CapControlPao& right);

        void CapControlPao::restore(RWDBReader& rdr);
        void CapControlPao::saveGuts(RWvostream& ostrm) const;

        bool CapControlPao::operator == (const CapControlPao& right) const;
        bool CapControlPao::operator != (const CapControlPao& right) const;
};
