#pragma once

#include "collectable.h"

namespace Cti
{
class RowReader;
}


class CtiCCState
{
public:
    DECLARE_COLLECTABLE( CtiCCState );

public:
    CtiCCState();
    CtiCCState(Cti::RowReader& rdr);
    CtiCCState(const CtiCCState& state);

    virtual ~CtiCCState();

    const std::string& getText() const;
    long getForegroundColor() const;
    long getBackgroundColor() const;

    CtiCCState& setText(const std::string& text);
    CtiCCState& setForegroundColor(long foregroundcolor);
    CtiCCState& setBackgroundColor(long backgroundcolor);

    CtiCCState& operator=(const CtiCCState& right);

    CtiCCState* replicate() const;

private:

    std::string _text;
    long        _foregroundcolor;
    long        _backgroundcolor;

    void restore(Cti::RowReader& rdr);
};

typedef CtiCCState* CtiCCStatePtr;
typedef std::vector<CtiCCStatePtr> CtiCCState_vec;
