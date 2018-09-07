#pragma once

#include "yukon.h"
#include "cctypes.h"
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"
#include "pointtypes.h"


namespace Cti
{
class RowReader;
}

class CtiPointDataMsg;


class CapControlPao
{
public:

    CapControlPao();
    CapControlPao(Cti::RowReader& rdr);

    ~CapControlPao();

    int getPaoId() const;
    void setPaoId(int paoId);

    const std::string& getPaoCategory() const;
    void setPaoCategory(const std::string& paoCategory);

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

    void setDisabledStatePointId( const long newId, bool sendDisablePointMessage = false );
    long getDisabledStatePointId() const;

    Cti::CapControl::PointIdVector* getPointIds();
    void removePointId(const long pId);
    void addPointId(const long ID);

    CtiCCOperationStats & getOperationStats();
    const CtiCCOperationStats & getOperationStats() const;
    CtiCCConfirmationStats & getConfirmationStats();

    void assignPoint( Cti::RowReader& rdr );
    std::set<long> getPointRegistrationIds() const;

    void restore( Cti::RowReader& rdr );

    CapControlPao& operator=(const CapControlPao& right);

    void handlePointData( const CtiPointDataMsg & message );

protected:

    void insertPointRegistration( std::set<long> & registrationIDs, const long pointID ) const;

private:

    bool assignCommonPoint( const long pointID, const long pointOffset, const CtiPointType_t pointType );

    virtual bool assignSpecializedPoint( const long pointID, const long pointOffset, const CtiPointType_t pointType );
    virtual void getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) const;
    virtual void handleSpecializedPointData( const CtiPointDataMsg & message );

    int _paoId;
    std::string _paoCategory;
    std::string _paoClass;
    std::string _paoName;
    std::string _paoType;
    std::string _paoDescription;
    bool _disableFlag;

    long    _disabledStatePointId;
    Cti::CapControl::PointIdVector _pointIds;

    CtiCCOperationStats     _operationStats;
    CtiCCConfirmationStats  _confirmationStats;
};


inline bool operator==( const CapControlPao & lhs, const CapControlPao & rhs )
{
    return lhs.getPaoId() == rhs.getPaoId();
}

inline bool operator!=( const CapControlPao & lhs, const CapControlPao & rhs )
{
    return ! ( lhs == rhs );
}

