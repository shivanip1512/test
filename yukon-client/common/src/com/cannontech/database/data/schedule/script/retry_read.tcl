#Read the List (only one attempt)
select list $Missed
if {[catch {getvalue kwh update timeout $PorterTimeout noqueue} errmsg] } {
}

