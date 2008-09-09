#Read the Group (this is the First attempt)
select $GroupType $GroupName
if {[catch {getvalue kwh update timeout $PorterTimeout} errmsg] } {
    dout "Setting retries to 0"
    set RetryCount 0
}
