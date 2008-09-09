#Read the List (only one attempt)
select list $Missed
if {[catch {getvalue ied kwh $TOURate update timeout $PorterTimeout noqueue} errmsg] } {
}

