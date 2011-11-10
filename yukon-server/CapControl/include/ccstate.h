#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include "connection.h"
#include "observe.h"

class CtiCCState : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCState )

    CtiCCState();
    CtiCCState(Cti::RowReader& rdr);
    CtiCCState(const CtiCCState& state);

    virtual ~CtiCCState();

    const std::string& getText() const;
    LONG getForegroundColor() const;
    LONG getBackgroundColor() const;

    CtiCCState& setText(const std::string& text);
    CtiCCState& setForegroundColor(LONG foregroundcolor);
    CtiCCState& setBackgroundColor(LONG backgroundcolor);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCState& operator=(const CtiCCState& right);

    CtiCCState* replicate() const;

private:

    std::string _text;
    LONG        _foregroundcolor;
    LONG        _backgroundcolor;

    void restore(Cti::RowReader& rdr);
};

typedef CtiCCState* CtiCCStatePtr;
typedef std::vector<CtiCCStatePtr> CtiCCState_vec;
