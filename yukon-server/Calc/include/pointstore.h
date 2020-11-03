#pragma once

#include <functional>

#include "rtdb.h"
#include "pointdefs.h"
#include "regression.h"
#include "tbl_pt_limit.h"
#include "ctitime.h"
#include "ctidate.h"

#include <boost/ptr_container/ptr_map.hpp>

#define CALC_DEBUG_INBOUND_POINTS                   0x00000001
#define CALC_DEBUG_OUTBOUND_POINTS                  0x00000002
#define CALC_DEBUG_PRECALC_VALUE                    0x00000004
#define CALC_DEBUG_POSTCALC_VALUE                   0x00000008
#define CALC_DEBUG_CALC_INIT                        0x00000010
#define CALC_DEBUG_COMPONENT_POSTCALC_VALUE         0x00000020
#define CALC_DEBUG_POINTDATA_QUALITY                0x00000040
#define CALC_DEBUG_DEMAND_AVG                       0x00000080
#define CALC_DEBUG_DISPATCH_INIT                    0x00000100
#define CALC_DEBUG_POINTDATA                        0x00000200
#define CALC_DEBUG_THREAD_REPORTING                 0x00000400
#define CALC_DEBUG_INBOUND_MSGS                     0x00000800
#define CALC_DEBUG_BASELINE                         0x00001000
#define CALC_DEBUG_RELOAD                           0x00002000

enum class CalcUpdateType
{
    Undefined,
    Periodic,
    AllUpdate,
    AnyUpdate,
    Historical,
    BackfillingHistorical,
    PeriodicPlusUpdate,
    Constant
};

class CtiPointStoreElement
{
    //  so they can access the "appendPointComponent" and "setPointValue" functions.
    //    that's so we don't have to worry about the calccomponent code modifying the values.
    friend class CtiCalculateThread;
    friend class CtiPointStore;

private:
    long _pointNum, _numUpdates, _secondsSincePreviousPointTime, _updatesInCurrentAvg, _uomID;
    double _pointValue, _curHistoricValue;
    unsigned _pointQuality;
    unsigned _pointTags;
    bool _useRegression, _isPrimed;
    CtiTime _pointTime, _calcPointWindowEndTime;
    std::set<long> _dependents;

    // The following two elements are used to determine if the VALUE changes from one scan to the next.
    CtiTime _lastValueChangedTime;
    CtiRegression _regress;
    CtiTablePointLimit *_ptLimitTable1, *_ptLimitTable2;

public:
    CtiPointStoreElement( long pointNum = 0, double pointValue = 0.0, unsigned pointQuality = UnintializedQuality, unsigned pointTags = 0 ) :
    _pointNum(pointNum), _pointValue(pointValue), _pointQuality(pointQuality), _pointTags(pointTags), _numUpdates(0), _lastValueChangedTime(pointValue),
    _updatesInCurrentAvg(0), _calcPointWindowEndTime(CtiTime(CtiDate(1,1,1990))), _secondsSincePreviousPointTime(60),// one minute seems like a reasonable default
    _ptLimitTable1(NULL), _ptLimitTable2(NULL), _useRegression(false), _isPrimed(false)
    {  };

    ~CtiPointStoreElement()
    {
        if( _ptLimitTable1 != NULL )
        {
            delete _ptLimitTable1;
            _ptLimitTable1 = NULL;
        }

        if( _ptLimitTable2 != NULL )
        {
            delete _ptLimitTable2;
            _ptLimitTable2 = NULL;
        }
    }

    long    getPointNum( void ) const      {   return _pointNum;   };
    double  getPointValue( void )          {   return _pointValue; };
    unsigned  getPointQuality( void )      {   return _pointQuality; };
    unsigned  getPointTags( void )         {   return _pointTags; };
    CtiTime  getPointTime( void )           {   return _pointTime;  };
    long    getNumUpdates( void )          {   return _numUpdates; };
    long    getSecondsSincePreviousPointTime( void )       {   return _secondsSincePreviousPointTime; }; //mostly used for demand average points
    std::set<long> getDependents( void )   {   return _dependents;  }

    CtiTime  getLastValueChangedTime( void )        {   return _lastValueChangedTime;  };
    void resize_regession(int data_elements)
    {
        _regress.resize(data_elements);
        return;
    }
    double regression( double x)
    {
        return _regress.regression(x);
    }
    bool linearRegression( double &slope, double &intercept )
    {
        return _regress.linearConstantIntervalRegression(slope, intercept);
    }

    void setUpdatesInCurrentAvg( long newCount )   {   _updatesInCurrentAvg = newCount;   };
    long getUpdatesInCurrentAvg( void )            {   return _updatesInCurrentAvg; };
    void setHistoricValue(double historicValue)    {   _curHistoricValue = historicValue; };
    double getHistoricValue()                      {   return _curHistoricValue;          };
    void setUOMID(long uomid)                      {   _uomID = uomid;                   };
    long getUOMID(void)                            {   return _uomID;                     };
    void setUseRegression(void)                    {   _useRegression = true;             };
    int  getRegressCurrentDepth()                  {   return _regress.getCurDepth();     };

    CtiTablePointLimit *getLimit(long limit)       {   return limit == 1 ? _ptLimitTable1 : _ptLimitTable2;   };

    const CtiTime& getPointCalcWindowEndTime( void )                  {   return _calcPointWindowEndTime;    };
    void          setPointCalcWindowEndTime( const CtiTime& endTime ) {   _calcPointWindowEndTime = endTime;  };

    //removes the dependent and returns the number of dependents remaining
    unsigned int removeDependent( long dependentID )
    {
        _dependents.erase(dependentID);

        return _dependents.size();
    }

    void readLimits( Cti::RowReader &rdr )
    {
        int limitnum;
        rdr["limitnumber"] >> limitnum;

        if( limitnum == 1 )
        {
            if( _ptLimitTable1 == NULL )
            {
                _ptLimitTable1 = CTIDBG_new CtiTablePointLimit(rdr);
            }
            else
            {
                *_ptLimitTable1 = CtiTablePointLimit(rdr);
            }
        }
        else if( limitnum == 2 )
        {
            if( _ptLimitTable2 == NULL )
            {
                _ptLimitTable2 = CTIDBG_new CtiTablePointLimit(rdr);
            }
            else
            {
                *_ptLimitTable2 = CtiTablePointLimit(rdr);
            }
        }
    }

    void addRegressionVal( const CtiTime &time, double value )
    {
        _regress.appendWithoutFill(std::make_pair((double)time.seconds(), value));
        _isPrimed = true;
    }

    void setRegressionDepth( int size )
    {
        _regress.setDepth(size);
    }

    void setRegressionMinDepth( int size )
    {
        _regress.setMinDepth(size);
    }

    bool isPrimed()
    {
        return _isPrimed;
    }

protected:
    void setPointValue( double newValue, const CtiTime &newTime, unsigned newQuality, unsigned newTags )
    {
        /*
         *  This represents a bonofide value change.  Record the time of the change.
         */
        if(newValue != _pointValue)
        {
            _lastValueChangedTime = newTime;
        }

        _secondsSincePreviousPointTime = newTime.seconds() - _pointTime.seconds();
        _pointTime = newTime;
        _pointValue = newValue;
        _pointQuality = newQuality;
        _pointTags = newTags;
        _numUpdates++;

        if( _useRegression )
        {
            _regress.append(std::make_pair((double)_pointTime.seconds(), _pointValue));
        }
    };

    void firstPointValue( double newValue, const CtiTime &newTime, unsigned newQuality, unsigned newTags )
    {
        _pointTime = newTime;
        _pointValue = newValue;
        _pointQuality = newQuality;
        _pointTags = newTags;

        _lastValueChangedTime = _pointTime;

        if( _useRegression )
        {
            _regress.append(std::make_pair((double)_pointTime.seconds(), _pointValue));
        }
    };

    void setPointTags( unsigned newTags )
    {
        _pointTags = newTags;
    }

    void appendDependent( long dependentID )
    {
        if( dependentID != _pointNum ) //You are not allowed to be dependent on yourself!!
        {
            _dependents.insert( dependentID );
        }
    };
};


class CtiPointStore
{
public:
    static CtiPointStoreElement *find(long pointId);
    static CtiPointStoreElement *insert(long pointId, long dependentId, CalcUpdateType updateType);
    static void remove(long pointId);

    static std::set<long> getPointIds();

    static void freeInstance();

private:

    CtiPointStore();

    typedef boost::ptr_map<long, CtiPointStoreElement> IdToElementPtrMap;

    IdToElementPtrMap _store;

    static CtiPointStore &instance();
};
