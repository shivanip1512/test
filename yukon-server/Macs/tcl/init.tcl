##
# init.tcl
#
# After each TCL interpreter is created this script is executed.
# Commonly used procedures and variables can be placed here and
# accessed from MACS schedules.
#
# Modified: April 2, 2003 - BDW
#
##

                                      
# System procedures below, do not edit
set Selected "Nothing selected"
set SuccessList [ list ]
set MissedList  [ list ]

proc select { args } {
    global Selected
    if { [ llength $args ] == 0 } {
        dout $Selected
    } else {
        set Selected [ linsert $args 0 select ]
               
    }
}

# Added for compatibility with existing scripts
proc Select { args } {    
    eval [ linsert $args 0 select ]             
}

select name "System Device"




#*******************************************************************
# Start Proc: getTopOfHour --> calculates the next top of hour
proc getTopOfHour { args } {

    set cursec [ clock seconds ]  
    set partInt [expr $cursec % 3600]
    return [expr $cursec - $partInt + 3600]
}    
# End Proc: getTopOfHour
#*******************************************************************



#***********************************************************************
# Start Proc: calcWaitTime --> calculates how long to wait until
#                              the target time.  Target time is pass in
#  Arguments: target_time - future time to wait for
#             max_wait    - optional maximum time that will be returned
#
proc calcWaitTime { args } {

    set defMaxWait 300

    if { [ llength $args ] == 0 } {
        dout "Invalid number of Args: calcWaitTime"
        return $defMaxWait
    }
        
    if { [ llength $args ] > 1 } {
        # 2nd param is max wait time
        set defMaxWait [lindex $args 1]
    }

    set now [ clock seconds ]  
    
    if { [expr [lindex $args 0] - $now] < $defMaxWait } {
        # do not wait for the full default time
        return [expr [lindex $args 0] - $now]
    }
        
    return $defMaxWait
}    
# End Proc: calc the next top of hour
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
    
        return
    } result] {
        dout "Caught Error in: runBatchProgram"
        return
    }


    # run an external program
    #dout [exec java.exe -jar c:\\yukon\\client\\bin\\SomeJar.jar]
}    
#
# End Proc: runBatchProgram
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

