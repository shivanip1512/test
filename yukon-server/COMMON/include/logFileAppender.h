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
    class CountingOutputStream; // forward declaration

    const FileInfo  _fileInfo;
    long long       _nextResumeAttempt;
    bool            _maxFileSizeLogged;
    size_t          _fileSize;

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

    void cleanupOldFiles() const;

public:

    LogFileAppender(const log4cxx::LayoutPtr& layout,
                    const FileInfo& fileInfo);
};

}
