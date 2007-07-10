    dout "Retry all the Missed Meters"
    select list $MissedList

    if { $RetryCount <= $QueueOffCount } {
        #Turn off queueing on the last few attempts
        dout "Attempting read WITHOUT queueing turned on"
        getvalue kwh update timeout $PorterTimeout noqueue
    } else {
        #Queueing is turned on
        dout "Attempting read WITH queueing turned on"
        getvalue kwh update timeout $PorterTimeout
    }

