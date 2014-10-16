#include "precompiled.h"

#include <numeric>

#include <boost/algorithm/string/case_conv.hpp>
#include <boost/algorithm/string/trim.hpp>
#include <boost/algorithm/string/split.hpp>

#include "string_util.h"

namespace Cti {
namespace {

template <class CharT>
void align_left(std::basic_string<CharT>& str, unsigned width, CharT fill, bool truncate)
{
    const unsigned len = str.length();

    if( len < width )
    {
        str.resize(width, fill);
    }
    else if( truncate && width < len )
    {
        str.resize(width);
    }
}

template <class CharT>
void align_right(std::basic_string<CharT>& str, unsigned width, CharT fill, bool truncate)
{
    const unsigned len = str.length();

    if( len < width )
    {
        str.insert(0, (width - len), fill);
    }
    else if( truncate && width < len )
    {
        str.swap(str.substr((len - width), std::basic_string<CharT>::npos));
    }
}

template <class CharT>
void align_center(std::basic_string<CharT>& str, unsigned width, CharT fill, bool truncate)
{
    const unsigned len = str.length();

    if( len < width )
    {
        const unsigned diff = width - len;
        const unsigned fill_left = diff / 2;

        str.insert(0, fill_left, fill);
        str.resize(width, fill);
    }
    else if( truncate && width < len )
    {
        const unsigned diff = len - width;
        const unsigned trim_left = diff / 2;

        str.swap(str.substr(trim_left, std::basic_string<CharT>::npos));
        str.resize(width);
    }
}

} // namespace anonymous

/// StringFormatter ///

StringFormatter::StringFormatter()
    :   _align(Align_Left),
        _width(0),
        _fill(' '),
        _truncate(false)
{
}

StringFormatter& StringFormatter::left()
{
    _align = Align_Left;
    return *this;
}

StringFormatter& StringFormatter::right()
{
    _align = Align_Right;
    return *this;
}

StringFormatter& StringFormatter::center()
{
    _align = Align_Center;
    return *this;
}

StringFormatter& StringFormatter::width(unsigned val)
{
    _width = val;
    return *this;
}

StringFormatter& StringFormatter::fill(char c)
{
    _fill = c;
    return *this;
}

StringFormatter& StringFormatter::truncate()
{
    _truncate = true;
    return *this;
}

StringFormatter& StringFormatter::no_truncate()
{
    _truncate = false;
    return *this;
}

std::string& StringFormatter::format(std::string& s) const
{
    switch(_align)
    {
    case Align_Left:
        {
            align_left(s, _width, _fill, _truncate);
            break;
        }
    case Align_Right:
        {
            align_right(s, _width, _fill, _truncate);
            break;
        }
    case Align_Center:
        {
            align_center(s, _width, _fill, _truncate);
            break;
        }
    }

    return s;
}

std::string StringFormatter::format_copy(const std::string& s) const
{
    return format(std::string(s));
}

/// FormattedTable ///

namespace {

boost::optional<FormattedTable::Range> findLimits(const FormattedTable::Range& r, unsigned max)
{
    const unsigned last = (r._last == FormattedTable::Range::end) ?  max : r._last;

    if( r._first > last )
    {
        return boost::none;
    }

    return FormattedTable::Range(r._first, last);
}

} // namespace anonymous

const std::string& FormattedTable::Content::getText() const
{
    if( !_text )
    {
        const_cast<FormattedTable::Content*>(this)->extractToText();
    }

    return *_text;
}

void FormattedTable::Content::extractToText()
{
    _text = "";
    _text->swap(_streamBuffer.extractToString());
}

struct FormattedTable::Draft
{
    std::vector<unsigned> _columnsWidth, _rowsHeight;

    typedef std::vector<std::string>   CellLinesType;
    typedef std::vector<CellLinesType> RowType;
    typedef std::vector<RowType>       TableType;

    TableType _table;

    Draft(const FormattedTable& formattedTable)
    {
        const unsigned totalRows    = formattedTable.getRowsCount();
        const unsigned totalColumns = formattedTable.getColumnsCount();

        _columnsWidth.resize(totalColumns, 0);
        _rowsHeight  .resize(totalRows,    0);
        _table       .resize(totalRows);

        // first pass :
        // - trim text, and add space characters
        // - find columns width, and rows height

        for(unsigned row=0; row < totalRows; row++)
        {
            const FormattedTable::RowType& tableRow = formattedTable._table[row];

            RowType& draftRow  = _table[row];
            draftRow.resize(totalColumns);

            for(unsigned column=0; column < totalColumns; column++)
            {
                const FormattedTable::Cell& cell = tableRow[column];
                if( ! cell._contentSPtr )
                {
                    continue;
                }

                CellLinesType& formattedLines = draftRow[column];
                const std::string& text = cell._contentSPtr->getText();

                boost::split(formattedLines, text, boost::is_any_of("\n"));

                if( _rowsHeight[row] < formattedLines.size() )
                {
                    _rowsHeight[row] = formattedLines.size();
                }

                std::vector<std::string>::iterator line_itr = formattedLines.begin();

                for(; line_itr != formattedLines.end(); line_itr++)
                {
                    boost::trim(*line_itr);

                    if( column != 0 || formattedTable._verticalBorders[0] )
                    {
                        line_itr->insert(line_itr->begin(), 1, ' ');
                    }

                    if( (column + 1) < totalColumns || formattedTable._verticalBorders[totalColumns])
                    {
                        line_itr->append(1, ' ');
                    }

                    if( _columnsWidth[column] < line_itr->length() )
                    {
                        _columnsWidth[column] = line_itr->length();
                    }
                }
            }
        }

        // second pass :
        // align and adjust text in each cell

        for(unsigned row=0; row < totalRows; row++)
        {
            const FormattedTable::RowType&  tableRow = formattedTable._table[row];
            const unsigned  height = _rowsHeight[row];
            RowType& draftRow      = _table[row];

            for(unsigned column=0; column < totalColumns; column++)
            {
                const FormattedTable::Cell& cell = tableRow[column];

                CellLinesType& formattedLines = draftRow[column];

                if( formattedLines.size() < height )
                {
                    switch(cell._align)
                    {
                    case Top_Left:
                    case Top_Right:
                    case Top_Center:
                        {
                            formattedLines.resize(height);
                            break;
                        }
                    case Middle_Left:
                    case Middle_Center:
                    case Middle_Right:
                        {
                            const unsigned diff = height - formattedLines.size();
                            formattedLines.insert(formattedLines.begin(), (diff / 2), std::string());
                            formattedLines.resize(height);
                            break;
                        }
                    case Bottom_Left:
                    case Bottom_Right:
                    case Bottom_Center:
                        {
                            const unsigned diff = height - formattedLines.size();
                            formattedLines.insert(formattedLines.begin(), diff, std::string());
                            break;
                        }
                    }
                }

                const unsigned width = _columnsWidth[column];
                StringFormatter strFormatter;
                strFormatter.width(width);

                switch(cell._align)
                {
                case Top_Left:
                case Middle_Left:
                case Bottom_Left:
                    {
                        strFormatter.left();
                        break;
                    }
                case Top_Right:
                case Middle_Right:
                case Bottom_Right:
                    {
                        strFormatter.right();
                        break;
                    }
                case Top_Center:
                case Middle_Center:
                case Bottom_Center:
                    {
                        strFormatter.center();
                        break;
                    }
                }

                for(unsigned cell_line=0; cell_line < height; cell_line++)
                {
                    strFormatter.format(formattedLines[cell_line]);
                }
            }
        }
    }
};



FormattedTable::FormattedTable()
{}

unsigned FormattedTable::getColumnsCount() const
{
    if( _table.empty() )
    {
        return 0;
    }

    return _table[0].size();
}

unsigned FormattedTable::getRowsCount() const
{
    return _table.size();
}

void FormattedTable::resize(unsigned rows, unsigned columns)
{
    const bool resizeColumns = columns != getColumnsCount();
    const bool resizeRows    = rows    != getRowsCount();

    if( ! resizeColumns && ! resizeRows )
    {
        return; // no change
    }

    if( resizeRows )
    {
        // resize the table by columns
        _table.resize(rows);
        
        // resize horizontal borders (new borders are disable by default)
        _horizontalBorders.resize(rows+1, 0);
    }

    if( resizeColumns )
    {
        // resize vertical borders (new borders are disable by default)
        _verticalBorders.resize(columns+1, 0);
    }

    // resize each of the table rows
    TableType::iterator itr = _table.begin();
    for(;itr != _table.end(); itr++)
    {
        itr->resize(columns);
    }
}

void FormattedTable::validateSize(unsigned row, unsigned column)
{
    const unsigned rows    = std::max(row+1,    getRowsCount());
    const unsigned columns = std::max(column+1, getColumnsCount());

    resize(rows, columns);
}

void FormattedTable::setHorizontalBorders(BordersOptions borders)
{
    setHorizontalBorders(borders, Range(0, Range::end));
}

void FormattedTable::setHorizontalBorders(BordersOptions borders, unsigned row)
{
    setHorizontalBorders(borders, Range(row, row));
}

void FormattedTable::setHorizontalBorders(BordersOptions borders, const Range& rows)
{
    const boost::optional<Range> rowsLimits = findLimits(rows, getRowsCount()-1);

    if( ! rowsLimits )
    {
        return;
    }

    validateSize(rowsLimits->_last, 0);

    // outside borders //
    const unsigned top    = rowsLimits->_first;
    const unsigned bottom = rowsLimits->_last + 1;

    switch(borders)
    {
        case Borders_Outside_Top:
        {
            _horizontalBorders[top]    = 1;
            _horizontalBorders[bottom] = 0;
            break;
        }
        case Borders_Outside_Bottom:
        {
            _horizontalBorders[top]    = 0;
            _horizontalBorders[bottom] = 1;
            break;
        }
        case Borders_All:
        case Borders_Outside:
        {
            _horizontalBorders[top] = _horizontalBorders[bottom] = 1;
            break;
        }
        case Borders_None:
        case Borders_Inside:
        {
            _horizontalBorders[top] = _horizontalBorders[bottom] = 0;
            break;
        }
        default:
        {
            // no effect
        }
    }

    // inside borders //

    const unsigned inside = (rowsLimits->_last - rowsLimits->_first);

    if( inside )
    {
        switch(borders)
        {
            case Borders_All:
            case Borders_Inside:
            {
                std::fill_n(_horizontalBorders.begin() + rowsLimits->_first + 1, inside, 1);
                break;
            }
            case Borders_None:
            case Borders_Outside:
            case Borders_Outside_Top:
            case Borders_Outside_Bottom:
            {
                std::fill_n(_horizontalBorders.begin() + rowsLimits->_first + 1, inside, 0);
                break;
            }
            default:
            {
                // no effect
            }
        }
    }
}

void FormattedTable::setVerticalBorders(BordersOptions borders)
{
    setVerticalBorders(borders, Range(0, Range::end));
}

void FormattedTable::setVerticalBorders(BordersOptions borders, unsigned column)
{
    setVerticalBorders(borders, Range(column, column));
}

void FormattedTable::setVerticalBorders(BordersOptions borders, const Range& columns)
{
    const boost::optional<Range> columnsLimits = findLimits(columns, getColumnsCount()-1);

    if( ! columnsLimits )
    {
        return;
    }

    validateSize(0, columnsLimits->_last);

    // outside borders //

    const unsigned left  = columnsLimits->_first;
    const unsigned right = columnsLimits->_last + 1;

    switch(borders)
    {
        case Borders_Outside_Left:
        {
            _verticalBorders[left]  = 1;
            _verticalBorders[right] = 0;
            break;
        }
        case Borders_Outside_Right:
        {
            _verticalBorders[left]  = 0;
            _verticalBorders[right] = 1;
            break;
        }
        case Borders_All:
        case Borders_Outside:
        {
            _verticalBorders[left] = _verticalBorders[right] = 1;
            break;
        }
        case Borders_None:
        case Borders_Inside:
        {
            _verticalBorders[left] = _verticalBorders[right] = 0;
            break;
        }
        default:
        {
            // no effect
        }
    }

    // inside borders //

    const unsigned inside = (columnsLimits->_last - columnsLimits->_first);

    if( inside )
    {
        switch(borders)
        {
            case Borders_All:
            case Borders_Inside:
            {
                std::fill_n(_verticalBorders.begin() + columnsLimits->_first + 1, inside, 1);
                break;
            }
            case Borders_None:
            case Borders_Outside:
            case Borders_Outside_Left:
            case Borders_Outside_Right:
            {
                std::fill_n(_verticalBorders.begin() + columnsLimits->_first + 1, inside, 0);
                break;
            }
            default:
            {
                // no effect
            }
        }
    }
}

FormattedTable::Input FormattedTable::setCell(unsigned row, unsigned column)
{
    return updateCell(row, column, NULL);
}

FormattedTable::Input FormattedTable::setCell(unsigned row, unsigned column, AlignOptions align)
{
    return updateCell(row, column, &align);
}

FormattedTable::Input FormattedTable::setCells(unsigned row, const Range& columns)
{
    return updateCells(row, columns, NULL);
}

FormattedTable::Input FormattedTable::setCells(unsigned row, const Range& columns, AlignOptions align)
{
    return updateCells(row, columns, &align);
}

FormattedTable::Input FormattedTable::setCells(const Range& rows, unsigned column)
{
    return updateCells(rows, column, NULL);
}

FormattedTable::Input FormattedTable::setCells(const Range& rows, unsigned column, AlignOptions align)
{
    return updateCells(rows, column, &align);
}

FormattedTable::Input FormattedTable::setCells(const Range& rows, const Range& columns)
{
    return updateCells(rows, columns, NULL);
}

FormattedTable::Input FormattedTable::setCells(const Range& rows, const Range& columns, AlignOptions align)
{
    return updateCells(rows, columns, &align);
}

FormattedTable::Input FormattedTable::updateCell(unsigned row, unsigned column, AlignOptions* align)
{
    validateSize(row, column);

    Input::CellGroup cells;

    Cell& cell = _table[row][column];

    if( align )
    {
        cell._align = *align; // set the cell alignment
    }

    cells.push_back(&cell);

    return Input(cells);
}

FormattedTable::Input FormattedTable::updateCells(unsigned row, const Range& columns, AlignOptions* align)
{
    const boost::optional<Range> columnsLimits = findLimits(columns, getColumnsCount()-1);

    assert( columnsLimits );

    validateSize(row, columnsLimits->_last);

    Input::CellGroup cells;

    RowType& tableRow = _table[row];

    for( unsigned column= columnsLimits->_first; column <= columnsLimits->_last; column++ )
    {
        Cell& cell = tableRow[column];

        if( align )
        {
            cell._align = *align; // set the cell alignment
        }

        cells.push_back(&cell);
    }

    return Input(cells);
}

FormattedTable::Input FormattedTable::updateCells(const Range& rows, unsigned column, AlignOptions * align)
{
    const boost::optional<Range> rowsLimits = findLimits(rows, getRowsCount()-1);

    assert( rowsLimits );

    validateSize(rowsLimits->_last, column);

    Input::CellGroup cells;

    for( unsigned row= rowsLimits->_first; row <= rowsLimits->_last; row++ )
    {
        Cell& cell = _table[row][column];

        if( align )
        {
            cell._align = *align; // set the cell alignment
        }

        cells.push_back(&cell);
    }

    return Input(cells);
}

FormattedTable::Input FormattedTable::updateCells(const Range& rows, const Range& columns, AlignOptions * align)
{
    const boost::optional<Range> rowsLimits    = findLimits(rows,    getRowsCount()-1);
    const boost::optional<Range> columnsLimits = findLimits(columns, getColumnsCount()-1);

    assert( columnsLimits && rowsLimits );

    validateSize(rowsLimits->_last, columnsLimits->_last);

    Input::CellGroup cells;

    for( unsigned row= rowsLimits->_first; row <= rowsLimits->_last; row++ )
    {
        RowType& tableRow = _table[row];

        for( unsigned column= columnsLimits->_first; column <= columnsLimits->_last; column++ )
        {
            Cell& cell = tableRow[column];

            if( align )
            {
                cell._align = *align; // set the cell alignment
            }

            cells.push_back(&cell);
        }
    }

    return Input(cells);
}

std::string FormattedTable::toString() const
{
    const Draft draft(*this);

    const unsigned total_height = std::accumulate(draft._rowsHeight.begin(),   draft._rowsHeight.end(),   0)
                                + std::accumulate(_horizontalBorders.begin(),  _horizontalBorders.end(),  0);

    const unsigned total_width  = std::accumulate(draft._columnsWidth.begin(), draft._columnsWidth.end(), 0)
                                + std::accumulate(_verticalBorders.begin(),    _verticalBorders.end(),    0) + 1; // add 1 newline character per line

    std::string tableResult;
    tableResult.reserve(total_width * total_height);

    unsigned row = 0;
    unsigned row_line = 0;

    bool isHorizontalBorder = _horizontalBorders[0];

    for(unsigned line=0; line < total_height; line++)
    {
        tableResult += '\n';

        if( isHorizontalBorder )
        {
            drawHorizontalBorder(tableResult, draft);
            isHorizontalBorder = false;
        }
        else
        {
            writeLine(tableResult, draft, row, row_line);

            if(++row_line >= draft._rowsHeight[row])
            {
                row++;
                row_line = 0;

                isHorizontalBorder = _horizontalBorders[row];
            }
        }
    }

    return tableResult;
}

void FormattedTable::drawHorizontalBorder(std::string& tableResult, const Draft& draft) const
{
    const unsigned totalColumns = getColumnsCount();

    for( unsigned column=0; column < totalColumns; column++ )
    {
        if( _verticalBorders[column] )
        {
            tableResult += '+'; // intersection borders
        }

        tableResult.append(draft._columnsWidth[column], '-'); // horizontal border
    }

    // process the last border
    if( _verticalBorders[totalColumns] )
    {
        tableResult += '+'; // intersection borders
    }
}

void FormattedTable::writeLine(std::string& tableResult, const Draft& draft, unsigned row, unsigned row_line) const
{
    const unsigned totalColumns = getColumnsCount();
    const Draft::RowType& draftRow = draft._table[row];

    for(unsigned column=0; column < totalColumns; column++)
    {
        if( _verticalBorders[column] )
        {
            tableResult += '|'; // vertical border
        }

        // append text line
        tableResult += draftRow[column][row_line];
    }

    // process the last border
    if( _verticalBorders[totalColumns] )
    {
        tableResult += '|'; // vertical border
    }
}

FormattedTable::Range::Range(unsigned first, unsigned last) :
    _first(first), _last(last)
{}

const unsigned FormattedTable::Range::end = -1;

/// FormattedList ///

namespace {

const std::string seperator = " : ";

} // namespace anonymous

FormattedList::FormattedList()
    :   _maxNameLen(0),
        _totalNamedValueLines(0),
        _totalValuesLen(0),
        _totalNamesExtraLinesLen(0)
{}

void FormattedList::updateLastItem()
{
    if( !_itemList.size() )
    {
        return;
    }

    Item &item = _itemList.back();
    item._value.swap(item._streamBuffer.extractToString());

    if( item._name )
    {
        // find the initial "extra" line name length
        size_t pos = 0;
        size_t end = item._name->find('\n');

        while( end != std::string::npos )
        {
            const size_t len = end - pos;
            if(_maxNameLen < len )
            {
                _maxNameLen = len;
            }

            item._additionalNameLines += (1 + len); // add 1 for the newline

            pos = end + 1;
            end = item._name->find('\n', pos);
        }

        // keep track of the total extra line length - for the reserve in toString()
        _totalNamesExtraLinesLen += item._additionalNameLines;

        // find the final line length - will contain the seperator
        const size_t len = item._name->length() - pos;
        if(_maxNameLen < len )
        {
            _maxNameLen = len;
        }

        // determine if this is multi "value" line and keep track of the number of lines required
        _totalNamedValueLines++;
        if( const size_t additionalLines = std::count(item._value.begin(), item._value.end(), '\n') )
        {
            item._multilineValue = true;
            _totalNamedValueLines += additionalLines;
        }
    }

    _totalValuesLen += item._value.length();
}

StreamBufferSink& FormattedList::add(std::string name)
{
    updateLastItem();

    _itemList.push_back(new Item);

    Item &item = _itemList.back();

    item._name = "";
    item._name->swap(name);

    return item._streamBuffer;
}

StreamBufferSink& FormattedList::add()
{
    updateLastItem();

    _itemList.push_back(new Item);

    return _itemList.back()._streamBuffer;
}

std::string FormattedList::toString() const
{
    const_cast<FormattedList*>(this)->updateLastItem();

    const size_t nameAndSeperatorLen = _maxNameLen + seperator.length();

    std::string listResult;
    // determine and reserve the total length required
    listResult.reserve(
            (_totalNamedValueLines * nameAndSeperatorLen) // those are named value lines
            + _totalValuesLen                             // values total length
            + _totalNamesExtraLinesLen                    // named line initial lines
            + _itemList.size());                          // reserve 1 newline character per Item

    StringFormatter formatter;

    for each(const Item& item in _itemList)
    {
        formatter.width(_maxNameLen + item._additionalNameLines);

        listResult += '\n';

        if( item._name )
        {
            listResult += formatter.format_copy(*item._name);
            listResult += seperator;

            if( item._multilineValue )
            {
                size_t offset = 0;
                std::string::const_iterator itr_start = item._value.begin();

                while( itr_start != item._value.end() )
                {
                    if( offset )
                    {
                        listResult.append(nameAndSeperatorLen, ' ');
                    }

                    offset = item._value.find('\n', offset);

                    std::string::const_iterator itr_stop  = (offset == std::string::npos)
                            ? item._value.end()
                            : item._value.begin() + (++offset); // go one index after the newline char

                    listResult.append(itr_start, itr_stop);

                    itr_start = itr_stop;
                }

                continue;
            }
        }

        listResult += item._value;
    }

    return listResult;
}

} // namespace Cti
