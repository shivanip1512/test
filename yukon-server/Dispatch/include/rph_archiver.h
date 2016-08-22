#pragma once

#include "worker_thread.h"

#include <mutex>

struct CtiTableRawPointHistory;
class CtiPointDataMsg;

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
namespace Dispatch {

class RawPointHistoryArchiver
{
public:
    RawPointHistoryArchiver(const bool &shutdownOnThreadTimeout, void (*shutdownFunc)(const std::string& who));
    ~RawPointHistoryArchiver();  //  defer destructor definition to allow unique_ptr of incomplete types

    void start();

    std::vector<std::unique_ptr<CtiTableRawPointHistory>> _archiverQueue;
    std::mutex _archiverLock;

    void submitRows(std::vector<std::unique_ptr<CtiTableRawPointHistory>>&& rows);
    void submitPointData(const CtiPointDataMsg& msg);
    unsigned archiverQueueSize();

    unsigned writeRawPointHistory(Cti::Database::DatabaseConnection &conn, std::vector<std::unique_ptr<CtiTableRawPointHistory>>&& rowsToWrite);

    bool isRunning();
    void interrupt();
    bool tryJoinFor(const std::chrono::seconds duration);
    void terminate();

protected:

    enum WriteMode
    {
        WriteMode_WriteChunkIfOverThreshold,
        WriteMode_WriteChunk,
        WriteMode_WriteAll
    };

    bool writeArchiveDataToDB(Cti::Database::DatabaseConnection& conn, const WriteMode wm);

private:

    void mainThread();

    WorkerThread _archiverThread;

    bool ShutdownOnThreadTimeout;
    void (*ShutdownFunc)(const std::string& who);
};

}
}