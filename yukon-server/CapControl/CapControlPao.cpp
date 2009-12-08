#include "yukon.h"

#include "CapControlPao.h"
#include "rwutil.h"
#include "resolvers.h"

using std::string;

CapControlPao::CapControlPao() :
    _paoId(0),
    _paoCategory(0),
    _paoClass(0),
    _paoType(0),
    _disableFlag(false)
{

}

CapControlPao::CapControlPao(RWDBReader& rdr)
{
    string tempString;
    string categoryString;

    rdr["paobjectid"] >> _paoId;
    rdr["category"] >> categoryString;
   _paoCategory = resolvePAOCategory(categoryString);

    rdr["paoclass"] >> tempString;
    _paoClass = resolvePAOClass(tempString);

    rdr["paoname"] >> _paoName;
    rdr["type"] >> tempString;
    _paoType = resolvePAOType(categoryString,tempString);

    rdr["description"] >> _description;

    rdr["disableflag"] >> tempString;
    std::transform(tempString.begin(), tempString.end(), tempString.begin(), tolower);
    _disableFlag = (tempString=="y"?true:false);
}

int CapControlPao::getPaoId()
{
    return _paoId;
}

void CapControlPao::setPaoId(int paoId)
{
    _paoId = paoId;
}

int CapControlPao::getPaoCategory()
{
    return _paoCategory;
}

void CapControlPao::setPaocategory(int paoCategory)
{
    _paoCategory = paoCategory;
}

int CapControlPao::getPaoClass()
{
    return _paoClass;
}

void CapControlPao::setPaoClass(int paoClass)
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

int CapControlPao::getPaoType()
{
    return _paoType;
}

void CapControlPao::setPaoType(int paoType)
{
    _paoType = paoType;
}

const std::string& CapControlPao::getDescription()
{
    return _description;
}

void CapControlPao::setDescription(const std::string& description)
{
    _description = description;
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
