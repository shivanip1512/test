#include "precompiled.h"

#include "CapControlPao.h"
#include "capcontroller.h"
#include "ccutil.h"

using Cti::CapControl::PointIdVector;
using Cti::CapControl::deserializeFlag;



CapControlPao::CapControlPao()
    :   _paoId( 0 ),
        _disabledStatePointId( 0 ),
        _disableFlag( false )
{
}

CapControlPao::CapControlPao(Cti::RowReader& rdr)
    :   _disabledStatePointId( 0 )
{
    restore(rdr);
}

CapControlPao::~CapControlPao()
{
    _pointIds.clear();
}

void CapControlPao::restore(Cti::RowReader& rdr)
{
    long paoID;

    rdr["PAObjectID"] >> paoID;
    setPaoId( paoID );

    rdr["Category"]    >> _paoCategory;
    rdr["PAOClass"]    >> _paoClass;
    rdr["PAOName"]     >> _paoName;
    rdr["Type"]        >> _paoType;
    rdr["Description"] >> _paoDescription;

    std::string disableStr;

    rdr["DisableFlag"] >> disableStr;
    _disableFlag = deserializeFlag( disableStr );
}

int CapControlPao::getPaoId() const
{
    return _paoId;
}

void CapControlPao::setPaoId(int paoId)
{
    _paoId = paoId;

    _operationStats.setPAOId( _paoId );
    _confirmationStats.setPAOId( _paoId );
}

const std::string& CapControlPao::getPaoCategory() const
{
    return _paoCategory;
}

void CapControlPao::setPaoCategory(const std::string& paoCategory)
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
            CtiCapController::getInstance()->sendMessageToDispatch(pMsg, CALLSITE);
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

void CapControlPao::setDisabledStatePointId( const long newId, bool sendDisablePointMessage )
{
    _disabledStatePointId = newId;
    if ( sendDisablePointMessage )
    {
        CtiCapController::getInstance()->sendMessageToDispatch( 
            new CtiPointDataMsg(_disabledStatePointId, _disableFlag ? 1.0 : 0.0), CALLSITE);
    }
}


long CapControlPao::getDisabledStatePointId() const
{
    return _disabledStatePointId;
}

Cti::CapControl::PointIdVector* CapControlPao::getPointIds()
{
    return &_pointIds;
}

void CapControlPao::removePointId( const long pId )
{
    _pointIds.erase( std::remove( _pointIds.begin(), _pointIds.end(), pId ),
                     _pointIds.end() );
}

void CapControlPao::addPointId( const long ID )
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

