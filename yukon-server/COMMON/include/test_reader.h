
#pragma once

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "row_reader.h"

namespace Cti {
namespace Test {

template <int T>
struct StringRow {
    std::string strArray[T];
    unsigned int size()
    {
        return T;
    }

    std::string operator[](int i)
    {
        return strArray[i];
    }
};

template <class T>
class TestReader : public Cti::RowReader
{
private:
    T _columnNames;
    std::vector<T> _values;

    int _currentRow;
    int _currentColumn;

public:

    TestReader(T &columnNames, std::vector<T> &values)
    {
        _currentColumn = 0;
        _currentRow = -1;
        _columnNames = columnNames;
        _values = values;
    }
    
    ~TestReader() { }
   
    bool setCommandText(const std::string &command) { return true; }
    bool isValid() { return true; }
    bool execute() { return true; }

    bool isNull() 
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            if( _values[_currentRow][_currentColumn] == getNullString() )
                return true;
            else 
                return false;
        }

        throw;
    }

    bool operator()()
    {
        _currentColumn = 0;
        _currentRow ++;
        return _currentRow < _values.size();
    }
    
    RowReader &operator[](const std::string &columnName)
    {
        if( _currentRow < _values.size() )
        {
            for( int i=0; i < _columnNames.size(); i++ )
            {
                if( _columnNames[i] == columnName )
                {
                    _currentColumn = i;
                    return *this;
                }
            }
        }

        // We didnt find it or we should not be here.
        throw;
    }

    RowReader &operator[](int columnNumber) { _currentColumn = columnNumber; return *this; } // 0 based

    RowReader &operator>>(bool &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            if( _values[_currentRow][_currentColumn] == getTrueString() )
            {
                operand = true;
                _currentColumn++;
                return *this;
            }
            else if( _values[_currentRow][_currentColumn] == getFalseString() )
            {
                operand = false;
                _currentColumn++;
                return *this;
            }
        }

        throw;
    }

    RowReader &operator>>(short &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atoi(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(unsigned short &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atoi(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(long &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atoi(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(INT &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atoi(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(UINT &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atoi(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(UCHAR &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atoi(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(unsigned long &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atoi(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(double &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atof(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(float &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = atof(_values[_currentRow][_currentColumn].c_str());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(CtiTime &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = CtiTime::now();
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(boost::posix_time::ptime &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = boost::posix_time::from_time_t(CtiTime::now().seconds());
            _currentColumn++;
            return *this;
        }

        throw;
    }

    RowReader &operator>>(std::string &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = _values[_currentRow][_currentColumn];
            _currentColumn++;
            return *this;
        }

        throw;
    }

    // inputs for variable binding
    RowReader &operator<<(const bool operand) { return *this; }
    RowReader &operator<<(const short operand) { return *this; }
    RowReader &operator<<(const unsigned short operand) { return *this; }
    RowReader &operator<<(const long operand) { return *this; }
    RowReader &operator<<(const INT operand) { return *this; }
    RowReader &operator<<(const UINT operand) { return *this; }
    RowReader &operator<<(const unsigned long operand) { return *this; }
    RowReader &operator<<(const double operand) { return *this; }
    RowReader &operator<<(const float operand) { return *this; }
    RowReader &operator<<(const CtiTime &operand) { return *this; }
    RowReader &operator<<(const boost::posix_time::ptime &operand) { return *this; }
    RowReader &operator<<(const std::string &operand) { return *this; }

    std::string asString() { return "UNIMPLEMENTED" }

    static std::string getNullString()
    {
        return "aR4nd0MNullStR1ng";
    }

    static std::string getTrueString()
    {
        return "bR4nd0mTrueStr1ng";
    }

    static std::string getFalseString()
    {
        return "cR4nd0mFalseStr1ng";
    }
};

}// namespace test
}// namespace cti
