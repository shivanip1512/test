#include "precompiled.h"

#include "ctitime.h"
#include "logLayout.h"

#include "log4cxx/helpers/transcoder.h"

namespace Cti {
namespace Logging {

const CtiTime gRunningSince;

namespace {

log4cxx::LogString toLogStr(const std::string &str)
{
    log4cxx::LogString logStr;
    log4cxx::helpers::Transcoder::decode(str, logStr);
    return logStr;
}

} // namespace anonymous

/*
 * Pattern layout syntax:
 * http://svn.apache.org/repos/asf/logging/site/trunk/docs/log4cxx/apidocs/classlog4cxx_1_1_pattern_layout.html
 * http://logging.apache.org/log4cxx/apidocs/classlog4cxx_1_1_pattern_layout.html
 *
 * -------------------------------
 * Conversion Character - Effect
 * -------------------------------
 * %c - Used to output the logger of the logging event. The logger conversion specifier can be optionally followed
 *      by precision specifier, that is a decimal constant in brackets. If a precision specifier is given, then only
 *      the corresponding number of right most components of the logger name will be printed. By default the logger
 *      name is printed in full.
 *      For example, for the logger name "a.b.c" the pattern %c{2} will output "b.c".
 *
 * %d - Used to output the date of the logging event. The date conversion specifier may be followed by a set of braces
 *      containing a date and time pattern string compatible with java.text.SimpleDateFormat, ABSOLUTE, DATE or ISO8601.
 *      For example, d{HH:mm:ss,SSS}, d{dd MMM yyyy HH:mm:ss,SSS} or d{DATE}.
 *      If no date format specifier is given then ISO8601 format is assumed.
 *
 * %F - Used to output the file name where the logging request was issued.
 *
 * %l - Used to output location information of the caller which generated the logging event.
 *
 * %L - Used to output the line number from where the logging request was issued.
 *
 * %m - Used to output the application supplied message associated with the logging event.
 *
 * %n - Outputs the platform dependent line separator character or characters. This conversion character offers
 *      practically the same performance as using non-portable line separator strings such as "\n", or "\r\n".
 *      Thus, it is the preferred way of specifying a line separator.
 *
 * %p - Used to output the level of the logging event.
 *
 * %r - Used to output the number of milliseconds elapsed since the start of the application until the creation of the
 *      logging event.
 *
 * %t - Used to output the name of the thread that generated the logging event.
 *
 * %x - Used to output the NDC (nested diagnostic context) associated with the thread that generated the logging event.
 *
 * %X - Used to output the MDC (mapped diagnostic context) associated with the thread that generated the logging event.
 *      The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber}
 *      where clientNumber is the key. The value in the MDC corresponding to the key will be output. See MDC class for
 *      more details.
 *
 * %% - The sequence %% outputs a single percent sign.
 *
 * %M - Used to output the method location from where the logging request was issued.
 *
 * NOTE:
 * The %M conversion character will only works with windows - it does not currently appear in the log4cxx doxygen doc
 *
 * -------------------------------------------------------------------------
 * Format modifier - left justify - minimum width - maximum width - comment
 * -------------------------------------------------------------------------
 * %20c              false          20              none            Left pad with spaces if the logger name is less than 20 characters long.
 *
 * %-20c             true           20              none            Right pad with spaces if the logger name is less than 20 characters long.
 *
 * %.30c             NA             none            30              Truncate from the beginning if the logger name is longer than 30 characters.
 *
 * %20.30c           false          20              30              Left pad with spaces if the logger name is shorter than 20 characters.
 *                                                                  However, if logger name is longer than 30 characters,
 *                                                                  then truncate from the beginning.
 *
 * %-20.30c          true           20              30              Right pad with spaces if the logger name is shorter than 20 characters.
 *                                                                  However, if logger name is longer than 30 characters,
 *                                                                  then truncate from the beginning.
 */

LogLayout::LogLayout(const OwnerInfo& ownerInfo, const log4cxx::logchar *patternFormat) :
    log4cxx::PatternLayout(patternFormat),
    _bFirstHeader(true),
    _ownerInfo(ownerInfo)
{
}

void LogLayout::appendHeader(log4cxx::LogString& output, log4cxx::helpers::Pool& p)
{
    std::ostringstream oss;

    if( ! _ownerInfo._project.empty() && ! _ownerInfo._version.empty() )
    {
        oss <<"--------  "<< _ownerInfo._project <<" [Version "<< _ownerInfo._version <<"]  --------\r\n";
    }

    if( _bFirstHeader )
    {
        _bFirstHeader = false;
        oss <<"--------  LOG BEGINS ("<< gRunningSince <<")  --------\r\n";
    }
    else
    {
        oss <<"--------  LOG CONTINUES (Running since "<< gRunningSince <<")  --------\r\n";
    }

    output.append(toLogStr(oss.str()));
}

void LogLayout::appendFooter(log4cxx::LogString& output, log4cxx::helpers::Pool& p)
{
    std::ostringstream oss;

    const CtiTime footerTime;

    oss <<"--------  LOG ENDS ("<< footerTime <<")  --------\r\n";

    output.append(toLogStr(oss.str()));
}

GeneralLogLayout::GeneralLogLayout(const OwnerInfo &ownerInfo) :
    LogLayout(ownerInfo, LOG4CXX_STR("%d{ISO8601} %X{tz} [%t] %p %C::%M:%F:%L - %m%n"))  //  include date/time, timezone, thread, log level, call site, message, and a newline
{
}

CommsLogLayout::CommsLogLayout(const OwnerInfo &ownerInfo) :
    LogLayout(ownerInfo, LOG4CXX_STR("%m%n"))  //  just the message and a newline
{
}

}
} // namespace Cti::Logging
