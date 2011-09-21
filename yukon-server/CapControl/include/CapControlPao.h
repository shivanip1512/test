#pragma once

#include "yukon.h"
#include "row_reader.h"
#include "rwutil.h"
#include <string>

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

        long    _disabledStatePointId;

    public:

        CapControlPao();
        CapControlPao(Cti::RowReader& rdr);

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
        void setDisableFlag(bool disableFlag);

        const std::string& getPaoStatistics() const;
        void setPaoStatistics(const std::string& paoStatistics);

        CapControlPao& operator=(const CapControlPao& right);

        void CapControlPao::restore(Cti::RowReader& rdr);
        void CapControlPao::saveGuts(RWvostream& ostrm) const;

        bool CapControlPao::operator == (const CapControlPao& right) const;
        bool CapControlPao::operator != (const CapControlPao& right) const;

        void setDisabledStatePointId( const long newId );
        long getDisabledStatePointId() const;
};
