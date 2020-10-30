    
New-Variable -Name "VirtualMachineName" -Visibility Public -Value "PSPLSWPR$Env:bamboo_repository_pr_key"
<#
.SYNOPSIS
    Connect to VSphere VI Server using PowerCLI cmdlt
.DESCRIPTION 
    Connect to VSphere VI Server using PowerCLI cmdlt. The VI Server connection details will be passed using Bamboo Parameters.
.EXAMPLE
    Connect-VICenter
#>
Function Connect-VICenter () {
    Write-Host "Connecting to VI Center server"          
    $vCenterInstance = $Env:bamboo_VICENTERHOST;
    $vCenterUser = $Env:bamboo_VICENTERUSER;
    $vCenterPass = $Env:bamboo_VICENTERKEY;

    Write-Host "Instance Name $vCenterInstance, Instance User $vCenterUser, Instance pasword $vCenterPass, Pull Request Number $VirtualMachineName" 
    # This is done to ignore any certificate issue while connecting to the VSphere VI Center server
    Set-PowerCLIConfiguration -InvalidCertificateAction Ignore -Confirm:$false

    Set-PowerCLIConfiguration -WebOperationTimeoutSeconds 900 -Confirm:$false
    Connect-VIServer -Server $vCenterInstance -Protocol https -User $vCenterUser -Password $vCenterPass -WarningAction SilentlyContinue 
    Write-Host "Successfully connected to the VI Center server"
}
<#
.SYNOPSIS
    Get IP Address of the Virtual Machine
.DESCRIPTION 
    Get IP Address of the Virtual Machine
.EXAMPLE
    Get-IPAddress
#>
Function Get-IPAddress() {
    # Get IP Addrss of the Virtual machine using PowerCLI command
    $global:IPAddress = Get-VM $VirtualMachineName | Select @{N="IP Address";E={@($_.guest.IPAddress[0])}} | Select -ExpandProperty "IP Address"
    Write-Host "IP Address Found - $IPAddress"
}

<#
.SYNOPSIS
    Get Email Id of the committer 
.DESCRIPTION 
    Get Email Id of the committer
.EXAMPLE
    Get-EmailId
#>
Function Get-EmailId() {
    # Get Email Id of the committer using GIT log command
    $global:emailID = git log -1 --pretty=format:'%ae'
}

<#
.SYNOPSIS
    Prepare and send the email to the branch committer. 
.DESCRIPTION 
    Prepare and send the email to the branch committer.
.EXAMPLE
    PrepareAndSend-Email
#>
Function PrepareAndSend-Email() {
    $branchName = $Env:bamboo_repository_git_branch;
    $yukonURL = "http://" + $IPAddress + ":8080";
    $apiDucumentationUrl = "http://" + $IPAddress + ":8080/apiDocumentation";
    $messageObj = new-object Net.Mail.MailMessage;
    $messageObj.From = "pspl-swbuild@eaton.com";
    $messageObj.To.Add($emailID);
    $messageObj.Subject = "Branch Deployment details for Branch - $branchName";
    $messageObj.Body = "Branch Deployment for branch $branchName is successful. Your Changes for branch $branchName is deployed on below server details :-
    
    Yukon Url = $yukonURL

    API Documentation Url = $apiDucumentationUrl

    Please visit the Above mentioned URL to test your deployed changes.
    
    ";

    $smtp = new-object Net.Mail.SmtpClient("mail.etn.com", "25");
    $smtp.send($messageObj);
    write-host "Mail Sent" ;
}

Function DisConnect-VICenter () {
    Write-Host "Disconnect from VI Center server"
    Disconnect-VIServer -Server $Env:bamboo_VICENTERHOST -Confirm:$false
}

Connect-VICenter
Get-IPAddress
Get-EmailId
PrepareAndSend-Email