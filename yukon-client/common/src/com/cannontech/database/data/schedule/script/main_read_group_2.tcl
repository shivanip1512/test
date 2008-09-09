    dout "Retry all the Missed Meters"
    select list $MissedList

    if { $RetryCount <= $QueueOffCount } {
        #Turn off queueing on the last few attempts
        dout "Attempting read WITHOUT queueing turned on"
        
        if {[catch {getvalue kwh update timeout $PorterTimeout noqueue} errmsg] } {
            dout "Setting retries to 0"
            #1 is the new 0 in this case
            set RetryCount 1
        }
    } else {
        #Queueing is turned on
        dout "Attempting read WITH queueing turned on"
        
        if {[catch {getvalue kwh update timeout $PorterTimeout} errmsg] } {
            dout "Setting retries to 0"
            #1 is the new 0 in this case
            set RetryCount 1
        }
    }

