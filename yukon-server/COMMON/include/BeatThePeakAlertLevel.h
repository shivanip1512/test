#pragma once
#include <string>
#include "dlldefs.h"
#include "utility.h"

namespace Cti {
namespace BeatThePeak {

class IM_EX_CTIBASE AlertLevel
{
public:
    AlertLevel();
    AlertLevel(std::string alertLevel);
    std::string asString();

    int AlertLevel::operator==(const AlertLevel& right) const;
    int AlertLevel::operator!=(const AlertLevel& right) const;

    class InvalidAlertLevel : public std::exception
    {
    public:
        InvalidAlertLevel(std::string alertLevel)
        {
            _description = std::string("Invalid Alert Level: ") + alertLevel;
        }
        virtual const char * what( ) const
        {
            return _description.c_str();
        }
    private:
        std::string _description;
    };
    static const std::string BTP_RED;
    static const std::string BTP_GREEN;
    static const std::string BTP_YELLOW;
    static const std::string BTP_RESTORE;

private:

    std::string _alertLevel;

};

static const AlertLevel AlertLevelRed = AlertLevel(AlertLevel::BTP_RED);
static const AlertLevel AlertLevelYellow = AlertLevel(AlertLevel::BTP_YELLOW);
static const AlertLevel AlertLevelGreen = AlertLevel(AlertLevel::BTP_GREEN);


}
}
