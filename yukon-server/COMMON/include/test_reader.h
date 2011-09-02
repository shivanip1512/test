#pragma once

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
        else
        {
            throw;
        }
    }

    bool operator()()
    {
        _currentColumn = 0;
        _currentRow ++;
        return _currentRow < _values.size();
    }

    RowReader &operator[](const char *columnName)
    {
        return this->operator[](std::string(columnName));
    }

    RowReader &operator[](const std::string &columnName)
    {
        bool found = false;

        if( _currentRow < _values.size() )
        {
            for( int i=0; i < _columnNames.size(); i++ )
            {
                if( _columnNames[i] == columnName )
                {
                    _currentColumn = i;
                    found = true;
                    break;
                }
            }
        }

        if( !found )
        {
            throw;
        }

        return *this;
    }

    RowReader &operator[](int columnNumber) { _currentColumn = columnNumber; return *this; } // 0 based

    RowReader &operator>>(bool &operand)
    {
        bool found = false;

        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            if( _values[_currentRow][_currentColumn] == getTrueString() )
            {
                operand = true;
                found = true;
            }
            else if( _values[_currentRow][_currentColumn] == getFalseString() )
            {
                operand = false;
                found = true;
            }
        }

        if( !found )
        {
            throw;
        }

        _currentColumn++;
        return *this;
    }

    RowReader &operator>>(short &operand)          { operand = getNextIntegerValue(); return *this;  }
    RowReader &operator>>(unsigned short &operand) { operand = getNextIntegerValue(); return *this;  }
    RowReader &operator>>(long &operand)           { operand = getNextIntegerValue(); return *this;  }
    RowReader &operator>>(INT &operand)            { operand = getNextIntegerValue(); return *this;  }
    RowReader &operator>>(UINT &operand)           { operand = getNextIntegerValue(); return *this;  }
    RowReader &operator>>(UCHAR &operand)          { operand = getNextIntegerValue(); return *this;  }
    RowReader &operator>>(unsigned long &operand)  { operand = getNextIntegerValue(); return *this;  }

    RowReader &operator>>(double &operand)         { operand = getNextFloatValue();   return *this;  }
    RowReader &operator>>(float &operand)          { operand = getNextFloatValue();   return *this;  }

    RowReader &operator>>(CtiTime &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = CtiTime::now();
        }
        else
        {
            throw;
        }

        _currentColumn++;
        return *this;
    }

    RowReader &operator>>(boost::posix_time::ptime &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = boost::posix_time::from_time_t(CtiTime::now().seconds());
        }
        else
        {
            throw;
        }

        _currentColumn++;
        return *this;
    }

    RowReader &operator>>(std::string &operand)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            operand = _values[_currentRow][_currentColumn];
        }
        else
        {
            throw;
        }

        _currentColumn++;
        return *this;
    }

    //  only implemented for StringRow so far
    RowReader &extractChars(char *destination, unsigned count)
    {
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            const std::string &s = _values[_currentRow][_currentColumn];

            unsigned to_copy = std::min(s.size(), count - 1);

            std::copy(s.begin(), s.begin() + to_copy, destination);

            std::fill(destination + to_copy, destination + count, 0);
        }
        else
        {
            throw;
        }

        _currentColumn++;
        return *this;
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
    RowReader &operator<<(const char *operand) { return *this; }

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

private:
    // helper function, returns a proper value and incrememts the column or throws.
    int getNextIntegerValue()
    {
        int retVal = 0;
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            retVal = atoi(_values[_currentRow][_currentColumn].c_str());
        }
        else
        {
            throw;
        }

        _currentColumn++;
        return retVal;
    }

    // helper function, returns a proper value and incrememts the column or throws.
    double getNextFloatValue()
    {
        double retVal = 0;
        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            retVal = atof(_values[_currentRow][_currentColumn].c_str());
        }
        else
        {
            throw;
        }

        _currentColumn++;
        return retVal;
    }
};

}// namespace test
}// namespace cti
