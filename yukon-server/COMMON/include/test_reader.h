#pragma once

#include "row_reader.h"
#include "utility.h"

#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/format.hpp>
namespace Cti {
namespace Test {

template <int T>
struct StringRow {
    std::string strArray[T];
    unsigned int size()
    {
        return T;
    }

    std::string & operator[](int i)
    {
        return strArray[i];
    }
    const std::string & operator[](int i) const
    {
        return strArray[i];
    }
};

template <class T>
class TestReader : public Cti::RowReader
{
private:
    std::vector<std::string> _columnNames;
    std::vector<std::vector<std::string>> _values;

    int _currentRow;
    int _currentColumn;

public:

    /**
     *  Legacy constructor for those who want to construct their test DB using a vector of StringRows.
     */
    TestReader(T &columnNames, std::vector<T> &values)
    {
        _currentColumn = 0;
        _currentRow = -1;

        // Copy column names from the T to our vector of strings
        for(int i = 0; i < columnNames.size(); ++i)
        {
            _columnNames.push_back(columnNames[i]);
        }

        for(int i = 0; i < values.size(); ++i)
        {
            std::vector<std::string> newVec;
            for(int j = 0; j < values[i].size(); ++j)
            {
                newVec.push_back(values[i][j]);
            }
            _values.push_back(newVec);
        }
    }

    /**
     *  Constructor for those who want to construct their test DB using << to add data.
     */
    TestReader(std::vector<std::string> columnNames, int size = 1)
    {
        _currentColumn = 0;
        _currentRow = 0;
        _columnNames = columnNames;
        _values = std::vector<std::vector<std::string>>(size);
    }

    /**
     *  Constructor taking a list of rows.  The first element of each vector in 
     *  the column name, the remaining elements are the rows.
     */
    TestReader(std::initializer_list<std::vector<std::string>> rows)
    {
        _currentColumn = 0;
        _currentRow = 0;
        _columnNames = std::vector<std::string>();
        _values = std::vector<std::vector<std::string>>();
        _values.push_back(std::vector<std::string>());

        for(std::vector<std::string> pair : rows)
        {
            auto row_itr = pair.begin();
            auto value_itr = _values.begin();

            // Grab the first string as the row name
            _columnNames.push_back(*row_itr++);

            // The rest are values
            while(row_itr != pair.end())
            {
                (*value_itr++).push_back(*row_itr++);
            }
        }
    }

    ~TestReader() { }

    bool setCommandText(const std::string &command) { return true; }
    bool isValid() { return true; }
    
    /**
     *  Bring us to the first record in our mock DB
     */
    bool execute() { 
        _currentColumn = 0;
        _currentRow = 0;
        return true; 
    }

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

    /**
     *  Simulate reading the next row.
     */
    bool operator()()
    {
        _currentColumn = 0;
        _currentRow ++;
        return _currentRow < _values.size();
    }

    /**
     *  Access the column by character string name
     */
    RowReader &operator[](const char *columnName)
    {
        return this->operator[](std::string(columnName));
    }

    /**
     *  Access the column by std::string name
     */
    RowReader &operator[](const std::string &columnName)
    {
        bool found = false;

        if( _currentRow < _values.size() )
        {
            for( int i=0; i < _columnNames.size(); i++ )
            {
                if( ciStringEqual(_columnNames[i], columnName) )
                {
                    _currentColumn = i;
                    found = true;
                    break;
                }
            }
        }

        if( !found )
        {
            throw std::invalid_argument("Column " + columnName + " not found.");
        }

        return *this;
    }

    /**
     *  Access a column by column number
     */
    RowReader &operator[](int columnNumber) { _currentColumn = columnNumber; return *this; } // 0 based

    RowReader &operator>>(bool &operand)
    {
        checkBounds();

        if( _values[_currentRow][_currentColumn] == getTrueString() )
        {
            operand = true;
        }
        else if( _values[_currentRow][_currentColumn] == getFalseString() )
        {
            operand = false;
        }
        else
        {
            throw std::invalid_argument("Value " + _values[_currentRow][_currentColumn] + " not True or False.");
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
    RowReader &operator>>(long long &operand)      { operand = getNextIntegerValue(); return *this;  }

    RowReader &operator>>(double &operand)         { operand = getNextFloatValue();   return *this;  }
    RowReader &operator>>(float &operand)          { operand = getNextFloatValue();   return *this;  }

    RowReader &operator>>(CtiTime &operand)
    {
        checkBounds();

        operand = CtiTime::now();

        _currentColumn++;
        return *this;
    }

    RowReader &operator>>(boost::posix_time::ptime &operand)
    {
        checkBounds();
        operand = boost::posix_time::from_time_t(CtiTime::now().seconds());

        _currentColumn++;
        return *this;
    }

    RowReader &operator>>(std::string &operand)
    {
        checkBounds();
        operand = _values[_currentRow][_currentColumn];

        _currentColumn++;
        return *this;
    }

    //  only implemented for StringRow so far
    RowReader &extractChars(char *destination, unsigned count)
    {
        auto dest = stdext::make_checked_array_iterator(destination, count);

        if( _currentRow < _values.size() && _currentColumn < _columnNames.size() )
        {
            const std::string &s = _values[_currentRow][_currentColumn];

            unsigned to_copy = std::min(s.size(), count - 1);

            std::copy(s.begin(), s.begin() + to_copy, dest);

            std::fill(dest + to_copy, dest + count, 0);
        }
        else
        {
            throw;
        }

        _currentColumn++;
        return *this;
    }

    // inputs for variable binding
    RowReader &operator<<(const bool operand) {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const short operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const unsigned short operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const long operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const INT operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const UINT operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const unsigned long operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const long long operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const double operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const float operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const CtiTime &operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand.asString();

        _currentColumn++;
        return *this;
    }

    RowReader &operator<<(const boost::posix_time::ptime &operand)
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = to_simple_string(operand);

        _currentColumn++;
        return *this;
    }

    // Write a string field into our mock DB
    RowReader &operator<<(const std::string &operand) 
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;
    
        _currentColumn++;
        return *this;
    }

    // Write a char array field into our mock DB
    RowReader &operator<<(const char *operand) 
    {
        checkAndExtend();
        // Replace the field to the vector
        _values[_currentRow][_currentColumn] = operand;

        _currentColumn++;
        return *this;
    }

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

        checkBounds();
        retVal = atoi(_values[_currentRow][_currentColumn].c_str());

        _currentColumn++;
        return retVal;
    }

    // helper function, returns a proper value and incrememts the column or throws.
    double getNextFloatValue()
    {
        double retVal = 0;

        checkBounds();
        retVal = atof(_values[_currentRow][_currentColumn].c_str());

        _currentColumn++;
        return retVal;
    }

    void checkBounds()
    {
        if(_currentRow >= _values.size())
        {
            throw std::out_of_range(str(boost::format("Current row out of bounds (%1% >= %2%)") % _currentRow % _values.size()));
        }
        if(_currentColumn >= _columnNames.size())
        {
            throw std::out_of_range(str(boost::format("Current column out of bounds (%1% >= %2%)") % _currentColumn % _columnNames.size()));
        }
    }

    void checkAndExtend()
    {
        if(_currentColumn >= _columnNames.size())
        {
            throw std::out_of_range(str(boost::format("Current column out of bounds (%1% >= %2%)") % _currentColumn % _columnNames.size()));
        }

        if(_currentRow > _values.size())
        {
            // Add a new row to the template
            _values.push_back(std::vector<std::string>());
            _values[_currentRow].resize(_columnNames.size());
        }

        if(_currentColumn >= _values[_currentRow].size())
        {
            _values[_currentRow].resize(_columnNames.size());
        }
    }

};

}// namespace test
}// namespace cti
