#Append the successfully read meters to the Success file
appendListToFile $SuccessList $SuccessFileName $FilePath

#Write the unsuccessfullly read meters to the Missed file
saveListToNewFile $MissedList $MissedFileName $FilePath

#Send out a log message
set GCount [llength $SuccessList]
set Message [getMeterCountLogText $GCount [llength $MissedList]]
dout $Message

#Log info events with counts
LogEvent "" $Message $ScheduleName
#END_MAIN_CODE
