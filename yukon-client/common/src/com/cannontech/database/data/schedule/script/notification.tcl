#START_NOTIFICATION
#Send email notification
set EMessage "\n\n"
append EMessage $Message
sendnotification $NotifyGroup $EmailSubject $EMessage
#END_NOTIFICATION
