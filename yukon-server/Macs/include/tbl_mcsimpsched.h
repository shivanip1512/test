#pragma once

#include <string>

#include "mutex.h"
#include "guard.h"
#include "logger.h"
#include "row_reader.h"

class CtiTableMCSimpleSchedule
{
public:
    CtiTableMCSimpleSchedule(   long schedule_id = 0,
                                long target_id = 0,
                                const std::string& start_command = "",
                                const std::string& stop_command = "",
                                long repeat_interval = 0 );

    virtual ~CtiTableMCSimpleSchedule();

    long getScheduleID() const;
    long getTargetPaoId() const;
    const std::string& getStartCommand() const;
    const std::string& getStopCommand() const;
    long getRepeatInterval() const;

    CtiTableMCSimpleSchedule& setScheduleID(long schedule_id);
    CtiTableMCSimpleSchedule& setTargetPaoID(const int target_id);
    CtiTableMCSimpleSchedule& setStartCommand(const std::string& start_command);
    CtiTableMCSimpleSchedule& setStopCommand(const std::string& stop_command);
    CtiTableMCSimpleSchedule& setRepeatInterval(long repeat_interval);

    bool DecodeDatabaseReader(Cti::RowReader &rdr);

    //database operations
    bool Update();
    bool Insert();
    bool Delete();

private:

    //CtiMutex _mux;

    static const char* _table_name;

    long    _schedule_id;
    long    _target_id;
    std::string  _start_command;
    std::string  _stop_command;
    long    _repeat_interval;
};
