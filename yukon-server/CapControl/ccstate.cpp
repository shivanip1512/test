#include "precompiled.h"

#include "ccstate.h"
#include "ccid.h"
#include "logger.h"
#include "row_reader.h"

using std::endl;
using std::string;

extern unsigned long _CC_DEBUG;

DEFINE_COLLECTABLE( CtiCCState, CTICCSTATE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCState::CtiCCState()
{
    _text = "";
    _foregroundcolor = 0;
    _backgroundcolor = 0;
}

CtiCCState::CtiCCState(Cti::RowReader& rdr)
{
    _text = "";
    restore(rdr);
}

CtiCCState::CtiCCState(const CtiCCState& state)
{
    operator=( state );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCState::~CtiCCState()
{
}

/*---------------------------------------------------------------------------
    Text

    Returns the text of the state
---------------------------------------------------------------------------*/
const string& CtiCCState::getText() const
{
    return _text;
}
/*---------------------------------------------------------------------------
    ForegroundColor

    Returns the foreground color of the state
---------------------------------------------------------------------------*/
long CtiCCState::getForegroundColor() const
{
    return _foregroundcolor;
}

/*---------------------------------------------------------------------------
    BackgroundColor

    Returns the background color of the state
---------------------------------------------------------------------------*/
long CtiCCState::getBackgroundColor() const
{
    return _backgroundcolor;
}

/*---------------------------------------------------------------------------
    setText

    Sets the Text of the state
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::setText(const string& text)
{
    _text = text;

    return *this;
}

/*---------------------------------------------------------------------------
    setForegroundColor

    Sets the foreground color of the state
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::setForegroundColor(long foregroundcolor)
{
    _foregroundcolor = foregroundcolor;

    return *this;
}

/*---------------------------------------------------------------------------
    setBackgroundColor

    Sets the background color of the state
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::setBackgroundColor(long backgroundcolor)
{
    _backgroundcolor = backgroundcolor;

    return *this;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::operator=(const CtiCCState& right)
{

    if ( &right != NULL )
    {
        if( this != &right )
        {
            _text = right.getText();
            _foregroundcolor = right.getForegroundColor();
            _backgroundcolor = right.getBackgroundColor();
        }
    }
    else
    {
        CTILOG_ERROR(dout, "state == NULL");
    }
    return *this;

}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCState* CtiCCState::replicate() const
{
    return (new CtiCCState(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCState::restore(Cti::RowReader& rdr)
{
    rdr["text"] >> _text;
    rdr["foregroundcolor"] >> _foregroundcolor;
    rdr["backgroundcolor"] >> _backgroundcolor;
}
