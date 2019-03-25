#pragma once

#include "logManager.h"
#include "ctidate.h"
#include "dlldefs.h"

#include <string>

#include "log4cxx/rolling/rollingfileappender.h"
#include <log4cxx/helpers/optionconverter.h>
#include <log4cxx/helpers/stringhelper.h>

namespace Cti::Logging {
    
class IM_EX_CTIBASE LogFileAppender : public log4cxx::rolling::RollingFileAppender
{
    static std::atomic<uintmax_t> _maxFileSize;
    static std::atomic<uint16_t>  _logRetentionDays;

    const FileInfo  _fileInfo;
    uint16_t        _retentionDays;
    long long       _nextCleanupAttempt;
    long long       _nextResumeAttempt;
    bool            _maxFileSizeLogged;

    log4cxx::helpers::WriterPtr _writer;
    long long         _nextFlush;
    long long         _flushInterval;

    log4cxx::helpers::WriterPtr createWriter(
            log4cxx::helpers::OutputStreamPtr& os) override;

    void subAppend(
            const log4cxx::spi::LoggingEventPtr &event,
            log4cxx::helpers::Pool &p) override;

    bool tryResumeWriting(
            const long long timestamp,
            log4cxx::helpers::Pool &p);

    void tryCleanupOldFiles(
            const long long timestamp);

    void cleanupOldFiles();

public:

    LogFileAppender(const log4cxx::LayoutPtr& layout,
                    const FileInfo& fileInfo);

    static void setMaxFileSize(uintmax_t fileSize);
    static void setLogRetentionDays(uint16_t days);
};

}
