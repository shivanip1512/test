#pragma once

#include "dlldefs.h"
#include "module_util.h"
#include "logger.h"

#include <string>

namespace Cti {
namespace Logging {

struct IM_EX_CTIBASE AutoShutdownLoggers
{
    ~AutoShutdownLoggers();
};

struct OwnerInfo
{
    std::string _project;
    std::string _version;
    std::string _details;
    std::string _date;
};

struct IM_EX_CTIBASE FileInfo
{
    std::string _path;
    std::string _baseFileName;

    size_t        _maxFileSize;
    unsigned long _maxOpenRetries;
    unsigned long _openRetryMillis;
    unsigned long _logRetentionDays;
    bool          _fileAppend;
    bool          _bufferedIO;
    size_t        _bufferSize;

    FileInfo();

    std::string logFileName      (const CtiDate &date) const;
    bool        shouldDeleteFile (const std::string& fileToDelete, const CtiDate& cutOffDate) const;
};

class IM_EX_CTIBASE LogManager : private boost::noncopyable
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    LogManager(const LogManager&);
    LogManager& operator=(const LogManager&);

    const std::string _baseLoggerName;

    bool      _toStdout;
    bool      _started;
    FileInfo  _fileInfo;
    OwnerInfo _ownerInfo;

protected:
    static std::string scrub(std::string fileName);
    const FileInfo& getFileInfo() const;

public:
    LogManager(const std::string &baseLoggerName);

    void setOutputPath    (const std::string &path);
    void setOutputFile    (const std::string &basefilename);
    void setOwnerInfo     (const compileinfo_t &ownerinfo);
    void setToStdOut      (bool toStdout);
    void setRetentionDays (unsigned long days);
    void start();

    bool isStarted() const;

    LoggerPtr getLogger () const;
    LoggerPtr getLogger (const std::string &loggerName) const;
};

}
} // namespace Cti::Logging

IM_EX_CTIBASE extern Cti::Logging::LogManager doutManager;
IM_EX_CTIBASE extern Cti::Logging::LogManager slogManager;
