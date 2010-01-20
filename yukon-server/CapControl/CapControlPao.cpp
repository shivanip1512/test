#include "yukon.h"

#include "CapControlPao.h"
#include "rwutil.h"
#include "resolvers.h"

using std::string;

CapControlPao::CapControlPao() :
    _paoId(0),
    _disableFlag(false)
{

}

CapControlPao::CapControlPao(RWDBReader& rdr)
{
    restore(rdr);
}

void CapControlPao::restore(RWDBReader& rdr)
{
    string tempString;
    //string categoryString;

    rdr["paobjectid"] >> _paoId;
    rdr["category"] >> _paoCategory;
    //_paoCategory = resolvePAOCategory(categoryString);

    rdr["paoclass"] >> _paoClass;
    //_paoClass = resolvePAOClass(tempString);

    rdr["paoname"] >> _paoName;
    rdr["type"] >> _paoType;
    //_paoType = resolvePAOType(categoryString,tempString);

    rdr["description"] >> _paoDescription;

    rdr["disableflag"] >> tempString;
    std::transform(tempString.begin(), tempString.end(), tempString.begin(), tolower);
    _disableFlag = (tempString=="y"?true:false);
}

int CapControlPao::getPaoId() const
{
    return _paoId;
}

void CapControlPao::setPaoId(int paoId)
{
    _paoId = paoId;
}

const std::string& CapControlPao::getPaoCategory()
{
    return _paoCategory;
}

void CapControlPao::setPaocategory(const std::string& paoCategory)
{
    _paoCategory = paoCategory;
}

const std::string& CapControlPao::getPaoClass()
{
    return _paoClass;
}

void CapControlPao::setPaoClass(const std::string& paoClass)
{
    _paoClass = paoClass;
}

const std::string& CapControlPao::getPaoName()
{
    return _paoName;
}

void CapControlPao::setPaoName(const std::string& paoName)
{
    _paoName = paoName;
}

const std::string& CapControlPao::getPaoType()
{
    return _paoType;
}

void CapControlPao::setPaoType(const std::string& paoType)
{
    _paoType = paoType;
}

const std::string& CapControlPao::getPaoDescription()
{
    return _paoDescription;
}

void CapControlPao::setPaoDescription(const std::string& description)
{
    _paoDescription = description;
}

bool CapControlPao::getDisableFlag()
{
    return _disableFlag;
}

void CapControlPao::setDisableFlag(bool disableFlag)
{
    _disableFlag = disableFlag;
}

const std::string& CapControlPao::getPaoStatistics()
{
    return _paoStatistics;
}

void CapControlPao::setPaoStatistics(const std::string& paoStatistics)
{
    _paoStatistics = paoStatistics;
}

void CapControlPao::saveGuts(RWvostream& ostrm) const
{
    ostrm << _paoId
    << _paoCategory
    << _paoClass
    << _paoName
    << _paoType
    << _paoDescription
    << _disableFlag;
}

CapControlPao& CapControlPao::operator=(const CapControlPao& right)
{
    if (this != &right)
    {
        _paoId = right._paoId;
        _paoCategory = right._paoCategory;
        _paoClass = right._paoClass;
        _paoName = right._paoName;
        _paoType = right._paoType;
        _paoDescription = right._paoDescription;
        _disableFlag = right._disableFlag;
    }
    return *this;
}

bool CapControlPao::operator == (const CapControlPao& right) const
{
    return _paoId == right._paoId;
}

bool CapControlPao::operator != (const CapControlPao& right) const
{
    return _paoId != right._paoId;
}

