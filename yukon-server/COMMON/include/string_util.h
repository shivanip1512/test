#pragma once

#include <string>
#include "boost/lexical_cast.hpp"
#include "boost/optional.hpp"
#include "boost/ptr_container/ptr_vector.hpp"
#include "boost/shared_ptr.hpp"
#include "dlldefs.h"
#include "streamBuffer.h"

/*
 * A few functions to trim your c++ strings
 */

template<class size_type>
inline std::basic_string<size_type>&
trim_left( std::basic_string<size_type>& str )
{
   return( str = str.substr( str.find_first_not_of( ' ' ) ) );

}

template<class size_type>
inline std::basic_string<size_type>&
trim_right( std::basic_string<size_type>& str )
{
   return( str = str.substr( 0, str.find_last_not_of( ' ' ) + 1 ) );

}

template<class size_type>
inline std::basic_string<size_type>&
trim( std::basic_string<size_type>& str )
{
   return( trim_left( trim_right( str ) ) );

}

namespace Cti {

class IM_EX_CTIBASE StringFormatter
{
    enum AlignOptions
    {
        Align_Left,
        Align_Right,
        Align_Center
    };

    AlignOptions _align;
    unsigned     _width;
    char         _fill;
    bool         _truncate;

public:

    StringFormatter();

    // set alignment
    StringFormatter& left();
    StringFormatter& right();
    StringFormatter& center();

    // set string width
    StringFormatter& width(unsigned val);

    // set fill character
    StringFormatter& fill(char c);

    // enable/disable truncate
    StringFormatter& truncate();
    StringFormatter& no_truncate();

    std::string& format(std::string& s) const;
    std::string  format_copy(const std::string& s) const;
};

class IM_EX_CTIBASE FormattedTable : public Loggable, private boost::noncopyable
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    FormattedTable(const FormattedTable&);
    FormattedTable& operator=(const FormattedTable&);

    class Input;

public:

    enum AlignOptions
    {
        Top_Left,
        Top_Right,
        Top_Center,
        Middle_Left,
        Middle_Center,
        Middle_Right,
        Bottom_Left,
        Bottom_Right,
        Bottom_Center,
    };

    enum BordersOptions
    {
        Borders_None,
        Borders_Inside,
        Borders_Outside_Top,
        Borders_Outside_Bottom,
        Borders_Outside_Left,
        Borders_Outside_Right,
        Borders_Outside,
        Borders_All,
    };

    struct IM_EX_CTIBASE Range
    {
        const unsigned _first;
        const unsigned _last;

        static const unsigned end;

        Range(unsigned first, unsigned last);
    };

    FormattedTable ();

    void resize (unsigned rows, unsigned columns);

    void setHorizontalBorders (BordersOptions borders);
    void setHorizontalBorders (BordersOptions borders, unsigned row);
    void setHorizontalBorders (BordersOptions borders, const Range& rows);

    void setVerticalBorders   (BordersOptions borders);
    void setVerticalBorders   (BordersOptions borders, unsigned column);
    void setVerticalBorders   (BordersOptions borders, const Range& columns);

    Input setCell   (unsigned row, unsigned column);
    Input setCell   (unsigned row, unsigned column, AlignOptions align);

    Input setCells  (unsigned row, const Range& columns);
    Input setCells  (unsigned row, const Range& columns, AlignOptions align);

    Input setCells  (const Range &rows, unsigned column);
    Input setCells  (const Range &rows, unsigned column, AlignOptions align);

    Input setCells  (const Range& rows, const Range& columns);
    Input setCells  (const Range& rows, const Range& columns, AlignOptions align);

    // draw table
    std::string toString() const override;

private:

    unsigned getRowsCount()    const;
    unsigned getColumnsCount() const;

    struct Content
    {
        StreamBufferSink _streamBuffer;
        const std::string& getText() const;

    private:
        boost::optional<std::string> _text;
        void extractToText();
    };

    struct Cell
    {
        AlignOptions                _align;
        boost::shared_ptr<Content>  _contentSPtr;

        Cell() : _align(Top_Left)
        {}
    };

    typedef std::vector<Cell>    RowType;
    typedef std::vector<RowType> TableType;

    TableType _table;

    class Input
    {
        friend class FormattedTable;

        typedef std::vector<Cell*> CellGroup;
        CellGroup _cells;

        boost::shared_ptr<Content> _contentSPtr;

        Input(const CellGroup& cells) : _cells(cells)
        {}

        void resetContent()
        {
            _contentSPtr.reset(new Content);

            CellGroup::iterator itr = _cells.begin();
            for(; itr != _cells.end(); itr++)
            {
                (*itr)->_contentSPtr = _contentSPtr;
            }
        }

    public:

        operator StreamBufferSink&() { resetContent(); return _contentSPtr->_streamBuffer; }

        template<typename T> StreamBufferSink& operator<< (T& val)          { resetContent(); return (_contentSPtr->_streamBuffer << val); }
        template<typename T> StreamBufferSink& operator<< (const T& val)    { resetContent(); return (_contentSPtr->_streamBuffer << val); }
        template<typename T> StreamBufferSink& operator<< (T *ptr)          { resetContent(); return (_contentSPtr->_streamBuffer << ptr); }
        template<typename T> StreamBufferSink& operator<< (const T *ptr)    { resetContent(); return (_contentSPtr->_streamBuffer << ptr); }

        StreamBufferSink& operator<< (std::ostream& (*pf)(std::ostream&))   { resetContent(); return (_contentSPtr->_streamBuffer << pf); }
        StreamBufferSink& operator<< (std::ios& (*pf)(std::ios&))           { resetContent(); return (_contentSPtr->_streamBuffer << pf); }
        StreamBufferSink& operator<< (std::ios_base& (*pf)(std::ios_base&)) { resetContent(); return (_contentSPtr->_streamBuffer << pf); }
    };

    std::vector<unsigned> _verticalBorders, _horizontalBorders;

    // forward declaration
    struct Draft;

    void drawHorizontalBorder (std::string& tableResult, const Draft& draft) const;
    void writeLine            (std::string& tableResult, const Draft& draft, unsigned row, unsigned row_line) const;
    void validateSize         (unsigned row, unsigned column);

    Input updateCell  (unsigned row,      unsigned column,       AlignOptions* align);
    Input updateCells (unsigned row,      const Range& columns,  AlignOptions* align);
    Input updateCells (const Range& rows, unsigned column,       AlignOptions* align);
    Input updateCells (const Range& rows, const Range& columns,  AlignOptions* align);
};

class IM_EX_CTIBASE FormattedList : public Loggable, private boost::noncopyable
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    FormattedList(const FormattedList&);
    FormattedList& operator=(const FormattedList&);

    struct Item
    {
        boost::optional<std::string> _name;
        std::string                  _value;
        bool                         _multilineValue;
        StreamBufferSink             _streamBuffer;
        size_t                       _additionalNameLines;

        Item() :
            _multilineValue(false),
            _additionalNameLines(0)
        {}
    };

    boost::ptr_vector<Item> _itemList;
    size_t _maxNameLen;
    size_t _totalNamedValueLines;
    size_t _totalValuesLen;
    size_t _totalNamesExtraLinesLen;

    void updateLastItem();
    StreamBufferSink& add();

public:
    FormattedList();

    StreamBufferSink& add(std::string name);

    template<typename T> StreamBufferSink& operator<< (T& val)          { return (add() << val); }
    template<typename T> StreamBufferSink& operator<< (const T& val)    { return (add() << val); }
    template<typename T> StreamBufferSink& operator<< (T *ptr)          { return (add() << ptr); }
    template<typename T> StreamBufferSink& operator<< (const T *ptr)    { return (add() << ptr); }

    StreamBufferSink& operator<< (std::ostream& (*pf)(std::ostream&))   { return (add() << pf); }
    StreamBufferSink& operator<< (std::ios& (*pf)(std::ios&))           { return (add() << pf); }
    StreamBufferSink& operator<< (std::ios_base& (*pf)(std::ios_base&)) { return (add() << pf); }

    // write list
    std::string toString() const override;
};

} // namespace Cti


