##
# init.tcl
#
# After each TCL interpreter is created this script is executed .
# Commonly used procedures and variables can be placed here and
# accessed from MACS schedules.
#
# Modified: April 2, 2003 - BDW
# Modified: April 9, 2003 - BDW
# Modified: May 5, 2003 - BDW
#
##
                                      
# System procedures below, do not edit

#*******************************************************************************
# Make sure we have a connection to the Porter service
pilstartup

#*******************************************************************************
# Set up some global variables

# Some possible return (error) codes from Port Control
# Useful in conjunction with the global ErrorList 
# see dsm2.h for possible values

# Default PIL Message Priority
set MessagePriority 7

set DEVICE_INHIBITED "85"

set Selected "Nothing selected"
set ScheduleName "none"

set SuccessList [ list ]
set MissedList  [ list ]
set ErrorList  [ list ]

#
#*******************************************************************************


#*******************************************************************************
# Start Proc: select --> set the current selection for use by subsequent 
#                        commands
proc select { args } {
    global Selected
    if { [ llength $args ] == 0 } {
        dout $Selected
    } else {
        set Selected [ linsert $args 0 select ]
               
    }
}
# make something is selected
select name "System Device"
# End Proc: select
#*******************************************************************************


#*******************************************************************************
# Start Proc: Select -> Added for compatibility with existing scripts
proc Select { args } {    
    eval [ linsert $args 0 select ]             
}
# End Proc: Select
#*******************************************************************************


#*******************************************************************************
# Start Proc: getScheduleName --> return the name of the currently running
#                                 schedule if any
proc getScheduleName { } {
    global ScheduleName
    return $ScheduleName
}
# End Proc: getScheduleName
#*******************************************************************************


#*******************************************************************************
# Start Proc: getSuccessList --> return the list of device names that were 
#                                successfully read
proc getSuccessList { } {
    global SuccessList
    return $SuccessList
}
# End Proc: getSuccessList
#*******************************************************************************


#*******************************************************************************
# Start Proc: getMissedList --> return the list of device names that were not
#                               successfully read
proc getMissedList { } {
    global MissedList
    return $MissedList
}
# End Proc: getMissedList
#*******************************************************************************


#*******************************************************************************
# Start Proc: getErrorList --> return the list of error codes for the MissedList
#
proc getErrorList { } {
    global ErrorList
    return $ErrorList
}
# End Proc: getErrorList
#*******************************************************************************


#*******************************************************************************
# Start Proc: getRetryList -> return the list of device names that were missed
#                             and should be retried.  A subset of the MissedList
# Exclude any device from the RetryList if its status is in 
set retryListExclude [ list $DEVICE_INHIBITED ]
proc getRetryList { } {
    global retryListExclude
    return [removeFromList [getMissedList] [getErrorList] $retryListExclude]
}
# End Proc: getRetryList
#*******************************************************************************


#*******************************************************************************
# Start Proc: removeFromList --> Create a new list of device names that excludes
#                                certain error codes.
# Example:    Send a getValue command to group "groupA" and create a list of
#             device names to retry.  We want to exclude all the devices that
#             were disabled.  85 is the error code for a disabled or inhibited
#             device
#
#             getvalue select group "groupA"
#             set retryList 
#                         [removeFromList [getMissedList] [getErrorList] {"85"}]
#
# Arguments: devList - List of device names
#            errList - List of returned error codes for each device
#            excludeList - List of error codes to exclude from the
#                          returned list
proc removeFromList { devList errList excludeList } {
    set dLst [lrange $devList 0 end]
    set eLst [lrange $errList 0 end]
    foreach i $excludeList {
	set indx [lsearch $eLst $i]
	while { $indx != -1 } {
	    set dLst [lreplace $dLst $indx $indx]
	    set eLst [lreplace $eLst $indx $indx]
	    set indx [lsearch $eLst $i]
	}
    }
    return $dLst;
}
# End Proc: removeFromList
#*******************************************************************************


#*******************************************************************************
# Start Proc: lremove -> convenience proc to remove an element from a list
proc lremove {list element {mode "-glob"}} {
    set indx [lsearch $mode $list $element]
    return [lreplace $list $indx $indx]
}
# End Proc: lremove
#*******************************************************************************


#*******************************************************************
# Start Proc: getTopOfHour --> calculates the next top of hour
proc getTopOfHour { args } {

    set cursec [ clock seconds ]  
    set partInt [expr $cursec % 3600]
    return [expr $cursec - $partInt + 3600]
}    
# End Proc: getTopOfHour
#*******************************************************************



#***************************************************************************
# Start Proc: calcTargetTimeInSecs --> Calculates a target time in the future 
#                                      
#
#  Arguments: Duration_Secs - Duration time is seconds
#                             When Interval is -1 then it returns 1-day (86400)
#
#  Return: Future_Time
#

proc calcTargetTimeInSecs { args } {

    # default to 24 hours
    set myTime 86400


    if { [ llength $args ] == 1 } {
        
        # check for a -1 
        if { [lindex $args 0] >= 0 } {
            # calculate the future time
            set myTime [ expr [ clock seconds ] + [lindex $args 0] ]
        }
    }
    
    return $myTime
}    
#
# End Proc: calcTargetTimeInSecs
#*******************************************************************



#***********************************************************************
# Start Proc: calcIntervalWaitTime --> calculates how long to wait until
#                              the target time.  Target time is pass in
#  Arguments: target_time - future time to wait for
#             max_wait    - optional maximum time that will be returned
#
proc calcIntervalWaitTime { args } {

    set defMaxWait 300

    if { [ llength $args ] == 0 } {
        dout "Invalid number of Args: calcWaitTime"
        return $defMaxWait
    }
        
    if { [ llength $args ] > 1 } {
        # 2nd param is max wait time
        set defMaxWait [lindex $args 1]
    }


    # using the time now and the interval, fine seconds to next interval
    set now [ clock seconds ]
    set partInt [expr $now % [lindex $args 0]]
    set nextTime [expr $now - $partInt + [lindex $args 0]]
    set waitTime [expr $nextTime - $now]

    if { $waitTime < $defMaxWait } {
        # do not wait for the full default time
        return $waitTime
    }
        
    return $defMaxWait
}    
# End Proc: calcIntervalWaitTime
#*******************************************************************


#***********************************************************************
# Start Proc: getMidnightTime --> calculates clocks for Midnight (unix time)
#                              
#  Arguments: 
#             
#
#  Return: midnight_seconds
#
#
proc getMidnightTime { args } {

    # return what GMT will be at local midnight time
    set nextMidNight [clock scan "tomorrow" -base [clock scan 00:00]]

    return $nextMidNight
}    
# End Proc: getMidnightTime
#*******************************************************************




#*******************************************************************
# Start Proc: saveListToFile --> builds a file name and save a list
#
#  Arguments: Item_List - tcl list of items to write
#             File_Name - name of the file
#             Path_Name - full path for file
#

proc saveListToNewFile { args } {

    if { [ llength $args ] == 3 } {
        # build the file name
        set aFileName [lindex $args 2]
        append aFileName [lindex $args 1]


        # write out the list line by line
        set fc [ open $aFileName w ]
        
        # loop through each item in the list
        foreach item [lindex $args 0] {
            puts $fc $item
        }
        close $fc
        
    } else {
        dout "Invalid number of Args: saveListToFile"
    }
    
    return
}    
#
# End Proc: saveListToFile
#*******************************************************************


#*******************************************************************
# Start Proc: appendListToFile --> builds a file name and append to file
#
#  Arguments: Item_List - tcl list of items to write
#             File_Name - name of the file
#             Path_Name - full path for file
#

proc appendListToFile { args } {

    if { [ llength $args ] == 3 } {
        # build the file name
        set aFileName [lindex $args 2]
        append aFileName [lindex $args 1]


        # write out the list line by line
        # open the file for append
        set fc [ open $aFileName a+ ]
        
        # loop through each item in the list
        foreach item [lindex $args 0] {
            puts $fc $item
        }
        close $fc
        
    } else {
        dout "Invalid number of Args: appendListToFile"
    }
    
    return
}    
#
# End Proc: appendListToFile
#*******************************************************************


#*******************************************************************
# Start Proc: loadListFromFile --> builds a file name and reads a list
#
#  Arguments: File_Name - name of the file
#             Path_Name - full path for file
#             
#
#  Return: name_list
#

proc loadListFromFile { args } {

    set myList [list]

    if { [ llength $args ] == 2 } {
        # build the file name
        set aFileName [lindex $args 1]
        append aFileName [lindex $args 0]


        # read the list line by line
        set fc [ open $aFileName r ]

        # read the list line by line to the end of file
        while { ! [eof $fc] } {
        
            # read a line from the file
            set temp [ gets $fc ]
            
            
            if {[string length $temp] > 0} {
                lappend myList $temp
            }
            
        }

        # stuff into our param
        close $fc
        
    } else {
        dout "Invalid number of Args: loadListFromFile"
    }
    
    return $myList
}    
#
# End Proc: loadListFromFile
#*******************************************************************


#***************************************************************************
# Start Proc: startSchedule --> write a file to FTP dir to start a schedule
#
#  Arguments: Sched_Name - Schedule Name to Start
#             File_Name - name to write
#             Path_Name - full path for file
#

proc startSchedule { args } {

    if { [ llength $args ] == 3 } {
        # build the file name
        set aFileName [lindex $args 2]
        append aFileName [lindex $args 1]


        # write out the list line by line
        set fc [ open $aFileName w ]
        
        # build our string 
        set cmdText "START,"
        append cmdText [lindex $args 0]
        
        # loop through each item in the list
        puts $fc $cmdText
        close $fc
        
    } else {
        dout "Invalid number of Args: startSchedule"
    }
    
    return
}    
#
# End Proc: startSchedule
#*******************************************************************



#***************************************************************************
# Start Proc: getMeterCountLogText --> builds a log text with counts from 
#                                      2 meter lists
#
#  Arguments: Good_List - List of good meters
#             Fail_List - List of Failed Meters
#             
#
#  Return: Log_Text
#

proc getMeterCountLogText { args } {

    set Message " Meters Read: "

    if { [ llength $args ] == 2 } {
        # build the log message
        
        # set the good count
        append Message [lindex $args 0]

        append Message "  Failed: "

        # set the failed count
        append Message [lindex $args 1]
        
    } else {
        dout "Invalid number of Args: getMeterCountLogText"
    }
    
    return $Message
}    
#
# End Proc: getMeterCountLogText
#*******************************************************************


#***************************************************************************
# Start Proc: getTryCountLogText --> builds a log text with try number from 
#                                    a parameter
#
#  Arguments: Try_Number - A counter with our try number
#             Intial_Text - Optional text (i.e. schedule name)
#
#  Return: Log_Text
#

proc getTryCountLogText { args } {

    set Message1 " First Try "
    set Message2 " Retry #"

    if { [ llength $args ] < 2 } {
        # set default text
        set MessLog "" 
    } else {
        # set the extra static text
        set MessLog [lindex $args 1] 
    }

    if { [ llength $args ] > 0 } {
        # add the count text
        
        if { [lindex $args 0] < 0 } {
            # -1 is considered a 1st try
            append MessLog $Message1 
        } else {
            # set text and add count
            append MessLog $Message2 
            append MessLog [lindex $args 0] 
        }
    }
    
    return $MessLog
}    
#
# End Proc: getTryCountLogText
#*******************************************************************




#*******************************************************************
# Start Proc: runBatchProgram --> runs an external program
#
#  Arguments: program_name - name of program to run with full path
#
proc runBatchProgram { args } {

    if [ catch { 
    
        if { [ llength $args ] == 1 } {
            # run an external program
            set batFileName [lindex $args 0]
    
            dout [exec cmd.exe /c $batFileName]
            
        } else {
            dout "Invalid number of Args: runBatchProgram"
        }
    
    } result] {
        dout "Caught Error in: runBatchProgram"
    }

}    
#
# End Proc: runBatchProgram
#*******************************************************************



#*******************************************************************
# Start Proc: exportBillingFile --> runs the Java Billing program
#
#  Arguments: group_name  - name metering group
#             group_Type  - Optional group type: collect (def), test, bill
#             Format_Type - Optional file fomrat: cticsv (def), ...
#             File_Name   - Optional export file name
#             File_Path   - Optional export path
#             Energy_Days - Optional number of days to go back for energy
#             Demand_Days - Optional number of days to go back for demand
#
proc exportBillingFile { args } {

    set groupType "collect"
    set billFormat "format:"
    set energyDays "ene:"
    set demandDays "dem:"
    set exportFile "c:\\yukon\\client\\export\\"
    set exportSpec "file:"

    if [ catch { 
        set argCount [ llength $args ]

        if { $argCount >= 1 } {
            # there must be at least 1 paramter
            set groupName [lindex $args 0]

            if { $argCount > 1 } {
                set groupType [lindex $args 1]
            }

            append groupType ":"
            append groupType [lindex $args 0]

            if { $argCount > 2 } {
                # format was specified
                append billFormat [lindex $args 2]
            } else {
                # default the format
                append billFormat "CTI-CSV"
            }

            if { $argCount < 4 } {
                # export the file with 2-3 parameters
                
                dout [exec cmd.exe /c "\\yukon\\client\\bin\\billing.bat" $groupType $billFormat]
                return
            }

            if { $argCount >= 5 } {
                # path has been specified
                set exportFile [lindex $args 4]
            }

            # add the file name to path
            append exportFile [lindex $args 3]
            append exportSpec $exportFile

            if { $argCount < 6 } {
                # export the file with 5 parameters
                dout [exec cmd.exe /c "\\yukon\\client\\bin\\billing.bat" $groupType $billFormat $exportSpec]
                return
            }
            
            if { $argCount >= 6 } {
                append energyDays [lindex $args 5]
            } else {
                append energyDays "7"
            }

            if { $argCount == 7 } {
                append demandDays [lindex $args 6]
            } else {
                append demandDays "30"
            }


            # export the file with 7 parameters
            dout [exec cmd.exe /c "\\yukon\\client\\bin\\billing.bat" $groupType $billFormat $exportSpec $energyDays $demandDays]
            
        } else {
            dout "No Args for exportBillingFile"
        }
        return

    } result] {
        return
    }

}    
#
# End Proc: exportBillingFile
#*******************************************************************

#*******************************************************************
# Start Proc: getTodaysFileName --> Determine a filename that is
# unique for today.
#
# Arguments: prefix    - string to prefix the file name with
#            suffix    - string to suffix the file name with
#
# Example:
# set missedFileName [getTodaysFileName "missed" ".txt"]
proc getTodaysFileName { prefix suffix } {
    set fileName $prefix
    append fileName [clock format [clock seconds] -format %m%d%y] $suffix
    return $fileName
}

#
# End Proc: getTodaysFileName
#*******************************************************************
