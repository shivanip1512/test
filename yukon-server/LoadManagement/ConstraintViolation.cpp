#include "yukon.h"

#include "ConstraintViolation.h"
#include "lmid.h"
#include "rwutil.h"

RWDEFINE_COLLECTABLE( ConstraintViolation, CTILMCONSTRAINTVIOLATION_ID )

ConstraintViolation::ConstraintViolation(CV_Enum_Double error, double value) :
   _errorCode(error)
{
    _doubleParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Enum_TimeInt error, const CtiTime &time, int value) :
    _errorCode(error)
{
    _datetimeParams.push_back(time);
    _integerParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Enum_StringDouble error, const string &str, double value) :
    _errorCode(error)
{
    _stringParams.push_back(str);
    _doubleParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Enum_Time error, const CtiTime &time) :
    _errorCode(error)
{
    _datetimeParams.push_back(time);
}

ConstraintViolation::ConstraintViolation(CV_Enum_StringInt error, const string &str, int value) :
    _errorCode(error)
{
    _stringParams.push_back(str);
    _integerParams.push_back(value);
}

ConstraintViolation::ConstraintViolation(CV_Enum_Empty error) :
    _errorCode(error)
{
}

ConstraintViolation::ConstraintViolation(CV_Enum_ThreeTimes error, const CtiTime &time1, const CtiTime &time2, const CtiTime &time3) :
    _errorCode(error)
{
    _datetimeParams.push_back(time1);
    _datetimeParams.push_back(time2);
    _datetimeParams.push_back(time3);
}

ConstraintViolation::ConstraintViolation(CV_Enum_TwoTimes error, const CtiTime &time1, const CtiTime &time2) :
    _errorCode(error)
{
    _datetimeParams.push_back(time1);
    _datetimeParams.push_back(time2);
}

ConstraintViolation::ConstraintViolation(CV_Enum_TimeDoubleDouble error, const CtiTime &time, double value1, double value2) :
    _errorCode(error)
{
    _datetimeParams.push_back(time);
    _doubleParams.push_back(value1);
    _doubleParams.push_back(value2);
}

ConstraintViolation::ConstraintViolation(CV_Enum_String error, const string &str) :
    _errorCode(error)
{
    _stringParams.push_back(str);
}

ConstraintViolation::ConstraintViolation(CV_Enum_TimeTimeInt error, const CtiTime &time1, const CtiTime &time2, int value) :
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
    strm << getErrorCode()
         << getDoubleParams()
         << getIntegerParams()
         << getStringParams()
         << getDateTimeParams();
}
