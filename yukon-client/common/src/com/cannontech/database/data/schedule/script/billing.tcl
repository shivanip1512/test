#START_BILLING
#Generate billing file
if { $BillingFormat != "none" } {
    #Wait a bit to allow dispatch to complete
    wait 300

    exportBillingFile $BillGroupName $BillingGroupType $BillingFormat $BillingFileName $BillingFilePath $BillingEnergyDays $BillingDemandDays
}
#END_BILLING

