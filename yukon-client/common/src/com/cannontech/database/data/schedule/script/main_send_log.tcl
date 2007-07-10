#Send out a log message
set GCount [llength $SuccessList]
set Message [getMeterCountLogText $GCount [llength $MissedList]]
dout $Message

#Make some text for additional info, log info events with counts
set ExInfoText [getTryCountLogText -1 $ScheduleName]
LogEvent "" $Message $ExInfoText

#Write the un/successfully read meters to respective files
saveListToNewFile $SuccessList $SuccessFileName $FilePath
saveListToNewFile $MissedList $MissedFileName $FilePath

#Main loop - break out when there are no more missed meters
while { $RetryCount > 0 && [llength $MissedList] > 0} {

    #Only check if there is a retry finish time
    if { [ expr [ clock seconds ] ] > $RetriesDoneAt } {
        dout "Retry Expire time has exceeded, this will be the Last attempt"
        set RetryCount 1 
    }

