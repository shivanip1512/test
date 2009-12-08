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
        int _paoCategory;
        int _paoClass;
        std::string _paoName;
        int _paoType;
        std::string _description;
        bool _disableFlag;
        std::string _paoStatistics;

    public:

        CapControlPao();
        CapControlPao(RWDBReader& rdr);

        int getPaoId();
        void setPaoId(int paoId);

        int getPaoCategory();
        void setPaocategory(int paoCategory);

        int getPaoClass();
        void setPaoClass(int paoClass);

        const std::string& getPaoName();
        void setPaoName(const std::string& paoName);

        int getPaoType();
        void setPaoType(int paoType);

        const std::string& getDescription();
        void setDescription(const std::string& description);

        bool getDisableFlag();
        void setDisableFlag(bool disableFlag);

        const std::string& getPaoStatistics();
        void setPaoStatistics(const std::string& paoStatistics);

};
