    dout "Retry all the Missed Meters"
    select list $MissedList

    if { $RetryCount <= $QueueOffCount } {
        #Turn off queueing on the last few attempts
        dout "Attempting read WITHOUT queueing turned on"
        getvalue demand update timeout $PorterTimeout noqueue
        getvalue outage 1 update timeout $PorterTimeout noqueue
        getvalue outage 3 update timeout $PorterTimeout noqueue
        getvalue outage 5 update timeout $PorterTimeout noqueue
    } else {
        #Queueing is turned on
        dout "Attempting read WITH queueing turned on"
        getvalue demand update timeout $PorterTimeout
        getvalue outage 1 update timeout $PorterTimeout
        getvalue outage 3 update timeout $PorterTimeout
        getvalue outage 5 update timeout $PorterTimeout
    }

