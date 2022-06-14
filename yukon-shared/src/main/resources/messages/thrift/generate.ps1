param (
    [int16]$OnlyRecentlyModified=7,
    [string]$Files="*.thrift",
    [array]$types=("java","cpp")
)

$THRIFT_VERSION="0.13.0"
$THRIFT_EXE="${env:DEV_ENVIRONMENT}\libraries\apache\thrift\${THRIFT_VERSION}\bin\thrift-${THRIFT_VERSION}.exe"
$JAVA_OUT="..\..\..\..\..\..\yukon-client\common\src"
$CPP_OUT ="..\..\..\..\..\..\yukon-server\Message\Serialization\Thrift"
$JAVA_GEN="java:beans,generated_annotations=suppress"
$CPP_GEN ="cpp"

# Utility function to pluralize X day/days, X file/files
function Pluralize {
    param (
        [int]$Count,
        [string]$Unit
    )
    
    If($Count -eq 1) {
        "$Count $Unit"
    } Else {
        "$Count ${Unit}s"
    }
}

# If caller specified one or more individual filenames/wildcards
if ($PSBoundParameters.ContainsKey('Files')) {
    $Files = -split $Files # split space-separated list to array if needed
    Write-Host "Limiting to filenames: $Files"
}

# Get the initial file list based on the files/wildcards
$thrift_files = Get-ChildItem -Path * -File -Include $Files

# Filter to only recent files if needed
if ($OnlyRecentlyModified -gt 0) {
    Write-Host "Limiting to files modified within the last" (Pluralize $OnlyRecentlyModified day)
    $recent_write_time = (Get-Date).AddDays(-1 * $OnlyRecentlyModified)
    $thrift_files = $thrift_files | Where-Object { $_.LastWriteTime -ge $recent_write_time }
}

if ($thrift_files.Length -eq 0) {
    Write-Host "No files found for update"
    Exit
}

Write-Host "Updating" (Pluralize ($thrift_files).count file)

function ValidateOutPath {
    param (
        [string]$Type,
        [string]$Path
    )
    if (Test-Path -Path $Path) {
        Write-Host "Found $Type output folder at" (Resolve-Path -Path $Path)
        $true
    } else {
        $NormalizedPath = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($Path)
        Write-Host "Can't find $Type output folder at $NormalizedPath"
        $false
    }
}

switch ($types) {
    "java" { 
        if ( ! (ValidateOutPath "Java" $JAVA_OUT) ) {
            throw "Can't find Java output folder"
        }}
    "cpp" { 
        if ( ! (ValidateOutPath "C++" $CPP_OUT) ) {
            throw "Can't find C++ output folder"
        }}
}

# Reusable function to log and invoke the Thrift compiler
function CompileThrift {
    param (
        [System.IO.FileInfo]$ThriftFile,
        [string]$Type,
        [string]$Out,
        [string]$Gen
    )
    Write-Host "Generating $Type classes for" $ThriftFile.Name
    & $THRIFT_EXE -out $Out --gen $Gen $ThriftFile.FullName
}

switch ($types) {
    "java" { $thrift_files | Foreach-Object { CompileThrift $_ "Java" $JAVA_OUT $JAVA_GEN } }
    "cpp"  { $thrift_files | Foreach-Object { CompileThrift $_ "C++"  $CPP_OUT  $CPP_GEN  } }
    default { throw "Unknown type $_" }
}
