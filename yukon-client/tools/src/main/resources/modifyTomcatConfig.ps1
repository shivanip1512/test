
$file = $args[0]

$originalConnectorProtocolString = "org.apache.coyote.http11.Http11Protocol"
$replacedConnectorProtocolString = "HTTP/1.1"

$originalPortString = 'Server port="8005" shutdown="SHUTDOWN"'
$replacedPortString = 'Server port="-1" shutdown="SHUTDOWN"'

(Get-Content $file) -replace $originalConnectorProtocolString, $replacedConnectorProtocolString -replace $originalPortString, $replacedPortString | Set-Content $file
