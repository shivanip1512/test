#pragma once

#include "dlldefs.h"
#include "module_util.h"
#include "logger.h"

#include <string>

namespace Cti::Logging {

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
    std::string path;
    std::string baseFileName;

    unsigned long maxOpenRetries;
    unsigned long openRetryMillis;
    unsigned long logRetentionDays;
    bool          fileAppend;
    bool          bufferedIO;
    size_t        bufferSize;

    FileInfo();

    bool        shouldDeleteFile (const std::string& fileToDelete, const CtiDate& cutOffDate) const;
};

enum LogFormats
{
    LogFormat_General,
    LogFormat_CommLog,
};

class IM_EX_CTIBASE LogManager : private boost::noncopyable
{
    const std::string _baseLoggerName;

    bool        _toStdout;
    bool        _started;
    FileInfo    _fileInfo;
    OwnerInfo   _ownerInfo;
    LogFormats  _format;

protected:
    static std::string scrub(std::string fileName);
    const FileInfo& getFileInfo() const;

public:
    LogManager(const std::string &baseLoggerName);

    void setDefaultOptions(const compileinfo_t& ownerinfo, const std::string& basefilename);

    void setOutputPath    (const std::string &path);
    void setOutputFile    (const std::string &basefilename);
    void setOwnerInfo     (const compileinfo_t &ownerinfo);
    void setToStdOut      (const bool toStdout);
    void setRetentionDays (const unsigned long days);
    void setOutputFormat  (const LogFormats format);
    void reloadMaxFileSize();
    void start();

    static void refresh();

    bool isStarted() const;

    LoggerPtr getLogger () const;
    LoggerPtr getLogger (const std::string &loggerName) const;

    static std::atomic<bool> inShutdown;
};

}

IM_EX_CTIBASE extern Cti::Logging::LogManager doutManager;
IM_EX_CTIBASE extern Cti::Logging::LogManager slogManager;
