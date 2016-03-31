#include "precompiled.h"

#include "CapControlPao.h"
#include "capcontroller.h"
#include "ccutil.h"
#include "resolvers.h"

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
            auto pMsg = std::make_unique<CtiPointDataMsg>( _disabledStatePointId, _disableFlag ? 1.0 : 0.0 );   // NormalQuality, StatusPointType
            pMsg->setMessagePriority( priority );

            CtiCapController::getInstance()->sendMessageToDispatch( pMsg.release(), CALLSITE );
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
        auto pMsg = std::make_unique<CtiPointDataMsg>( _disabledStatePointId, _disableFlag ? 1.0 : 0.0 );   // NormalQuality, StatusPointType

        CtiCapController::getInstance()->sendMessageToDispatch( pMsg.release(), CALLSITE );
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

/*
    Extracts the point information from the reader and tries to assign it to this object.  If it
        fails to do so, it dumps the point information to the log.
*/
void CapControlPao::assignPoint( Cti::RowReader& rdr )
{
    long pointId,
         pointOffset;

    std::string pointTypeStr;

    rdr["POINTID"]     >> pointId;
    rdr["POINTOFFSET"] >> pointOffset;
    rdr["POINTTYPE"]   >> pointTypeStr;

    CtiPointType_t pointType = resolvePointType( pointTypeStr );

    if ( ! ( assignCommonPoint( pointId, pointOffset, pointType ) ||
             assignSpecializedPoint( pointId, pointOffset, pointType ) ) )
    {
        Cti::FormattedList  logMessage;

        logMessage << "Undefined point for (" << getPaoType() << "): " << getPaoName();
        logMessage.add("Type")   << pointTypeStr;
        logMessage.add("Offset") << pointOffset;
        logMessage.add("ID")     << pointId;

        CTILOG_INFO( dout, logMessage );
    }
}

/*
    Helper function to assign the given point info to points that are available on every device that
        derives from CapControlPao.
 
    Returns true if the point is assigned in this function, false otherwise.
*/
bool CapControlPao::assignCommonPoint( const long pointID, const long pointOffset, const CtiPointType_t pointType )
{
    const std::size_t initialSize = getPointIds()->size();

    switch ( pointType )
    {
        case StatusPointType:
        {
            if ( pointOffset == -1 )
            {
                return true;        // tag point - ignore it
            }
            else if ( pointOffset == Cti::CapControl::Offset_PaoIsDisabled )
            {
                setDisabledStatePointId( pointID, _paoId );
                addPointId( pointID );
            }
            break;
        }
        case AnalogPointType:
        {
            if ( pointOffset >= Cti::CapControl::Offset_OperationSuccessPercentRangeMin &&
                 pointOffset <= Cti::CapControl::Offset_OperationSuccessPercentRangeMax )
            {
                if ( getOperationStats().setSuccessPercentPointId( pointID, pointOffset ) )
                {
                    addPointId( pointID );
                }
            }
            else if ( pointOffset >= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMin &&
                      pointOffset <= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMax )
            {
                if ( getConfirmationStats().setSuccessPercentPointId( pointID, pointOffset ) )
                {
                    addPointId( pointID );
                }
            }
            break;
        }
    }

    return getPointIds()->size() > initialSize;
}

/*
    Virtual function to be overriden by child classes to assign points that are specific to
        the child class.
 
    Returns true if the point is assigned in this function, false otherwise.
*/
bool CapControlPao::assignSpecializedPoint( const long pointID, const long pointOffset, const CtiPointType_t pointType )
{
    return false;
}

/*
    Insert the point IDs into the supplied set that this object needs to register with dispatch.
*/
void CapControlPao::getPointRegistrationIds( std::set<long> & registrationIDs )
{
    getSpecializedPointRegistrationIds( registrationIDs );

    insertPointRegistration( registrationIDs, getDisabledStatePointId() );

    insertPointRegistration( registrationIDs, getOperationStats().getUserDefOpSuccessPercentId() );
    insertPointRegistration( registrationIDs, getOperationStats().getDailyOpSuccessPercentId() );
    insertPointRegistration( registrationIDs, getOperationStats().getWeeklyOpSuccessPercentId() );
    insertPointRegistration( registrationIDs, getOperationStats().getMonthlyOpSuccessPercentId() );
}

/*
    Virtual function to be overridden by child classes to supply additional point IDs to register with dispatch.
*/
void CapControlPao::getSpecializedPointRegistrationIds( std::set<long> & registrationIDs )
{

}

void CapControlPao::insertPointRegistration( std::set<long> & registrationIDs, const long pointID ) const
{
    if ( pointID > 0 )
    {
        registrationIDs.insert( pointID );
    }
}

