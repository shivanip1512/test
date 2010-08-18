#include "yukon.h"

#include "ConstraintViolation.h"
#include "lmid.h"
#include "rwutil.h"

#include <algorithm>

RWDEFINE_COLLECTABLE( ConstraintViolation, CTILMCONSTRAINTVIOLATION_ID )

ConstraintViolation::ConstraintViolation(CV_Type_Double error, double value) :
   _errorCode(error)
{
    _doubleParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Type_TimeInt error, const CtiTime &time, int value) :
    _errorCode(error)
{
    _datetimeParams.push_back(time);
    _integerParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Type_StringDouble error, const string &str, double value) :
    _errorCode(error)
{
    _stringParams.push_back(str);
    _doubleParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Type_Time error, const CtiTime &time) :
    _errorCode(error)
{
    _datetimeParams.push_back(time);
}

ConstraintViolation::ConstraintViolation(CV_Type_StringInt error, const string &str, int value) :
    _errorCode(error)
{
    _stringParams.push_back(str);
    _integerParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Type_NoParameters error) :
    _errorCode(error)
{
}

ConstraintViolation::ConstraintViolation(CV_Type_ThreeTimes error, const CtiTime &time1, const CtiTime &time2, const CtiTime &time3) :
    _errorCode(error)
{
    _datetimeParams.push_back(time1);
    _datetimeParams.push_back(time2);
    _datetimeParams.push_back(time3);
}

ConstraintViolation::ConstraintViolation(CV_Type_TwoTimes error, const CtiTime &time1, const CtiTime &time2) :
    _errorCode(error)
{
    _datetimeParams.push_back(time1);
    _datetimeParams.push_back(time2);
}

ConstraintViolation::ConstraintViolation(CV_Type_TimeDoubleDouble error, const CtiTime &time, double value1, double value2) :
    _errorCode(error)
{
    _datetimeParams.push_back(time);
    _doubleParams.push_back(value1);
    _doubleParams.push_back(value2);
}

ConstraintViolation::ConstraintViolation(CV_Type_String error, const string &str) :
    _errorCode(error)
{
    _stringParams.push_back(str);
}

ConstraintViolation::ConstraintViolation(CV_Type_TimeTimeInt error, const CtiTime &time1, const CtiTime &time2, int value) :
    _errorCode(error)
{
    _datetimeParams.push_back(time1);
    _datetimeParams.push_back(time2);
    _integerParams.push_back(value);
}

int ConstraintViolation::getErrorCode() const
{
    return _errorCode;
}

std::vector<double> ConstraintViolation::getDoubleParams() const
{
    return _doubleParams;
}

std::vector<int> ConstraintViolation::getIntegerParams() const
{
    return _integerParams;
}

std::vector<string> ConstraintViolation::getStringParams() const
{
    return _stringParams;
}

std::vector<CtiTime> ConstraintViolation::getDateTimeParams() const
{
    return _datetimeParams;
}

bool ConstraintViolation::operator==(const ConstraintViolation &rhs) const
{
    if (_errorCode != rhs.getErrorCode() || _doubleParams.size() != rhs.getDoubleParams().size() ||
        _integerParams.size() != rhs.getIntegerParams().size() || _stringParams.size() != rhs.getStringParams().size() ||
        _datetimeParams.size() != rhs.getDateTimeParams().size())
    {
        return false;
    }

    return ( equal(  _doubleParams.begin(),   _doubleParams.end(),   rhs.getDoubleParams().begin()) &&
             equal( _integerParams.begin(),  _integerParams.end(),  rhs.getIntegerParams().begin()) &&
             equal(  _stringParams.begin(),   _stringParams.end(),   rhs.getStringParams().begin()) &&
             equal(_datetimeParams.begin(), _datetimeParams.end(), rhs.getDateTimeParams().begin()) );
}

void ConstraintViolation::restoreGuts(RWvistream& strm)
{
    strm >> _errorCode
         >> _doubleParams
         >> _integerParams
         >> _stringParams
         >> _datetimeParams;
}

void ConstraintViolation::saveGuts(RWvostream& strm) const
{
    strm << _errorCode      
         << _doubleParams   
         << _integerParams  
         << _stringParams   
         << _datetimeParams;
}
