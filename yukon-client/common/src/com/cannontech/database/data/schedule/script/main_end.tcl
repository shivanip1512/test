    #Append successfully read meters to list/file
    appendListToFile $SuccessList $SuccessFileName $FilePath

    #Write unsuccessfully read meters to new list/file
    saveListToNewFile $MissedList $MissedFileName $FilePath

    #Total the successfully read meters count
    set GCount [expr $GCount + [llength $SuccessList]]

    #Send out a log message
    set Message [getMeterCountLogText $GCount [llength $MissedList]]
    dout $Message

    #Make some text for additional info, log info events with counts
    set ExInfoText [getTryCountLogText -1 $ScheduleName]
    LogEvent "" $Message $ExInfoText

    #Decrement Retry Count
    set RetryCount [expr $RetryCount - 1]
}
#End Main Loop
#END_MAIN_CODE
