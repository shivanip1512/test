#Send Demand Resets (for 360/370 IED meters)
select $GroupType $GroupName
while {$ResetCount > 0} {

    set ResetCount [expr $ResetCount - 1]

    #Reset the Demand
    putvalue reset ied

    #Move MCT to read the frozen register
    eval $ReadFrozen

    #Wait 2 minutes and send the Reset again
    dout "Wait 120 after Demand Reset"
    wait 120
}

