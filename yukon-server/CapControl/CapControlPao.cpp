#include "precompiled.h"

#include "CapControlPao.h"
#include "capcontroller.h"


CapControlPao::CapControlPao() :
    _paoId(0),
    _disabledStatePointId(0),
    _disableFlag(false)
{

}

CapControlPao::CapControlPao(Cti::RowReader& rdr)
{
    restore(rdr);

    _operationStats.setPAOId(getPaoId());
    _confirmationStats.setPAOId(getPaoId());
}

CapControlPao::~CapControlPao()
{
    _pointIds.clear();
}

void CapControlPao::restore(Cti::RowReader& rdr)
{
    std::string tempString;

    rdr["paobjectid"] >> _paoId;
    rdr["category"] >> _paoCategory;
    rdr["paoclass"] >> _paoClass;
    rdr["paoname"] >> _paoName;
    rdr["type"] >> _paoType;
    rdr["description"] >> _paoDescription;
    rdr["disableflag"] >> tempString;

    std::transform(tempString.begin(), tempString.end(), tempString.begin(), tolower);
    _disableFlag = (tempString=="y");

    _disabledStatePointId = 0;
}

int CapControlPao::getPaoId() const
{
    return _paoId;
}

void CapControlPao::setPaoId(int paoId)
{
    _paoId = paoId;
}

const std::string& CapControlPao::getPaoCategory() const
{
    return _paoCategory;
}

void CapControlPao::setPaocategory(const std::string& paoCategory)
{
    _paoCategory = paoCategory;
}

const std::string& CapControlPao::getPaoClass() const
{
    return _paoClass;
}

void CapControlPao::setPaoClass(const std::string& paoClass)
{
    _paoClass = paoClass;
}

const std::string CapControlPao::getPaoName() const
{
    return _paoName;
}

void CapControlPao::setPaoName(const std::string& paoName)
{
    _paoName = paoName;
}

const std::string& CapControlPao::getPaoType() const
{
    return _paoType;
}

void CapControlPao::setPaoType(const std::string& paoType)
{
    _paoType = paoType;
}

const std::string& CapControlPao::getPaoDescription() const
{
    return _paoDescription;
}

void CapControlPao::setPaoDescription(const std::string& description)
{
    _paoDescription = description;
}

bool CapControlPao::getDisableFlag() const
{
    return _disableFlag;
}

void CapControlPao::setDisableFlag(bool disableFlag, int priority)
{
    if ( _disableFlag != disableFlag )
    {
        _disableFlag = disableFlag;
        
        if ( _disabledStatePointId != 0 )
        {
            CtiPointDataMsg* pMsg = new CtiPointDataMsg(
                                _disabledStatePointId, _disableFlag ? 1.0 : 0.0 ); // NormalQuality, StatusPointType
            pMsg->setMessagePriority(priority);
            CtiCapController::getInstance()->getDispatchConnection()->WriteConnQue(pMsg, CALLSITE);
        }
    }
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
        _disabledStatePointId = right._disabledStatePointId;
        _pointIds = right._pointIds;
        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;
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


void CapControlPao::setDisabledStatePointId( const long newId, bool sendDisablePointMessage )
{
    _disabledStatePointId = newId;
    if ( sendDisablePointMessage )
    {
        CtiCapController::getInstance()->getDispatchConnection()->WriteConnQue( 
            new CtiPointDataMsg(_disabledStatePointId, _disableFlag ? 1.0 : 0.0), CALLSITE);
    }
}


long CapControlPao::getDisabledStatePointId() const
{
    return _disabledStatePointId;
}


void CapControlPao::removePointId(long pId)
{
    _pointIds.erase(remove(_pointIds.begin(), _pointIds.end(), pId), _pointIds.end());
}

void CapControlPao::addPointId(const long ID)
{
    _pointIds.push_back(ID);
}

CtiCCOperationStats & CapControlPao::getOperationStats()
{
    return _operationStats;
}

CtiCCConfirmationStats & CapControlPao::getConfirmationStats()
{
    return _confirmationStats;
}

