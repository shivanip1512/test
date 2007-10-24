#Send Demand Resets (for 400 Series IED meters)
select $GroupType $GroupName
while {$ResetCount > 0} {

    set ResetCount [expr $ResetCount - 1]

    #Reset the Demand
    putvalue ied reset $IEDType

    #Wait 2 minutes and send the Reset again
    dout "Wait 120 after Demand Reset"
    wait 120
}

