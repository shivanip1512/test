#START_MAIN_CODE
#Load the meter list from the file
#Load the Missed (unsuccessful) meters to read
set Missed [loadListFromFile $MissedFileName $FilePath]

set Message "Meters to Retry: "
append Message [llength $Missed]
dout $Message

#Log an Info Event containing the Missed Meter count
LogEvent "" $Message $ScheduleName

