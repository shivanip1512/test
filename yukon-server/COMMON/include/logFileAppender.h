#pragma once

#include "logManager.h"
#include "ctidate.h"
#include "dlldefs.h"

#include <string>

#include "log4cxx/writerappender.h"

namespace Cti {
namespace Logging {

class IM_EX_CTIBASE LogFileAppender : public log4cxx::WriterAppender
{
    class CountingOutputStream; // forward declaration

    const FileInfo  _fileInfo;
    CtiDate         _today;
    long long         _tomorrow;
    long long         _nextResumeAttempt;
    bool            _maxFileSizeLogged;
    bool            _lastRolloverFailed;
    size_t          _fileSize;

    log4cxx::helpers::WriterPtr _writer;
    long long         _nextFlush;
    long long         _flushInterval;

    void activateOptions(
            log4cxx::helpers::Pool& p);

    void setFile(
            const log4cxx::LogString& file,
            bool append,
            bool bufferedIO,
            size_t bufferSize,
            log4cxx::helpers::Pool& p);

    void subAppend(
            const log4cxx::spi::LoggingEventPtr &event,
            log4cxx::helpers::Pool &p) override;

    bool tryResumeWriting(
            const long long timestamp,
            log4cxx::helpers::Pool &p);

    bool rollover(
            const long long eventTimestamp,
            log4cxx::helpers::Pool &p);

    void cleanupOldFiles() const;

public:

    LogFileAppender(const log4cxx::LayoutPtr& layout,
                    const FileInfo& fileInfo);
};

}
} // namespace Cti::Logging
