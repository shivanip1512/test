#pragma once

#include "boostutil.h"
#include "dsm2.h"
#include "dbmemobject.h"
#include "cmdparse.h"
#include "ctibase.h"
#include "message.h"
#include "tbl_pao_lite.h"
#include "tbl_rtcomm.h"
#include "msg_signal.h"
#include "yukon.h"
#include "tbl_static_paoinfo.h"
#include "encryption.h"
#include "loggable.h"
#include "std_helper.h"

#include <boost/shared_ptr.hpp>
#include <list>
#include <set>
#include <string>

class CtiRequestMsg;    // Use forward declaration #include "msg_pcrequest.h"
class CtiReturnMsg;

class IM_EX_DEVDB CtiRouteBase : private boost::noncopyable, public CtiMemDBObject, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiRouteBase(const CtiRouteBase&);
    CtiRouteBase& operator=(const CtiRouteBase&);

protected:

    CtiTblPAOLite _tblPAO;
    CtiTableCommRoute _tblComm;

private:
    mutable CtiMutex _classMutex;

public:
    typedef CtiMemDBObject Inherited;

    CtiRouteBase() {}
    virtual ~CtiRouteBase() {}

    /*
     *  Some nice little virtual functinos to make me like life...
     */
    virtual std::string toString() const override
    {
        Cti::FormattedList itemList;

        itemList << _tblPAO;
        itemList << _tblComm;

        return itemList.toString();
    }

    virtual LONG getTrxDeviceID() const                { return -1L;}
    virtual INT  getBus() const                        { return 0;}
    virtual INT  getCCUFixBits() const                 { return 0;}
    virtual INT  getCCUVarBits() const                 { return 0;}
    virtual INT  getStages() const                     { return 0;}

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        _tblPAO.DecodeDatabaseReader(rdr);
        _tblComm.DecodeDatabaseReader(rdr);

        purgeStaticPaoInfo();
    }

    virtual YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) = 0;

    CtiTableCommRoute& getCommRoute()
    {
        return _tblComm;
    }

    virtual bool isDefaultRoute() const
    {
        return _tblComm.getDefaultRoute();
    }


    LONG getRouteID() const;
    std::string getName() const;
    INT getType() const;

    virtual bool processAdditionalRoutes( const INMESS &InMessage ) const;

protected:

    std::map<CtiTableStaticPaoInfo::PaoInfoKeys, CtiTableStaticPaoInfo> _staticPaoInfo;

public:

    void purgeStaticPaoInfo()
    {
        _staticPaoInfo.clear();
    }

    bool hasStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const
    {
        return _staticPaoInfo.count(k);
    }

    bool setStaticInfo(const CtiTableStaticPaoInfo &info)
    {
        auto itr = _staticPaoInfo.find(info.getKey());

        if( itr != _staticPaoInfo.end() )
        {
            itr->second = info;
            return false;       // update existing entry
        }

        _staticPaoInfo.emplace(info.getKey(), info);
        return true;            // insert a new entry
    }

    bool getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, std::string &destination) const
    {
        auto itr = _staticPaoInfo.find(k);

        if( itr != _staticPaoInfo.end() )
        {
            itr->second.getValue(destination);
            return true;
        }

        return false;
    }

    bool getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, double &destination) const
    {
        auto itr = _staticPaoInfo.find(k);

        if( itr != _staticPaoInfo.end() )
        {
            itr->second.getValue(destination);
            return true;
        }

        return false;
    }

    long getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const
    {
        long value = 0;
        auto itr = _staticPaoInfo.find(k);

        if( itr != _staticPaoInfo.end() )
        {
            itr->second.getValue(value);
        }

        return value;
    }

protected:

    Cti::Encryption::Buffer _encryptionKey;

public:

    void setEncryptionKey( const Cti::Encryption::Buffer & key )
    {
        _encryptionKey = key;
    }
};

inline LONG CtiRouteBase::getRouteID() const { return _tblPAO.getID(); }
inline std::string CtiRouteBase::getName() const { return _tblPAO.getName(); }
inline INT CtiRouteBase::getType() const { return _tblPAO.getType(); }
inline bool CtiRouteBase::processAdditionalRoutes( const INMESS &InMessage ) const { return false; }

typedef CtiRouteBase CtiRoute;

typedef boost::shared_ptr< CtiRouteBase > CtiRouteSPtr;
