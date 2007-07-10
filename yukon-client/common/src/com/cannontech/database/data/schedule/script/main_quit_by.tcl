
#START_MAIN_CODE
#Establish a time of day to quit by, default to one day max
if {$MaxRetryHours > 0 } {
    set RetriesDoneAt [calcTargetTimeInSecs [ expr $MaxRetryHours * 3600 ] ]
} else {
    set RetriesDoneAt [calcTargetTimeInSecs 86400 ]
}

