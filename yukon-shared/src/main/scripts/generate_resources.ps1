#
# PowerShell v2.0 compatible script to auto-generate the files needed to build yukon-resources.dll
#  Run from the yukon-shared\src\main\scripts directory within the ISE.
#

param (
    [switch]$vstudio = $false
)

$xsds = @()
$paos = @()
$points = @()
$configs_xsd = @()
$configs_xml = @()
$index = @{}

$root = '"..\\..\\yukon-shared\\src\\main\\resources\\'

if ( $vstudio ) 
{
    $server_resource_dir = '.'
    $path = '..\..\yukon-shared\src\main\resources'
} 
else 
{
    $server_resource_dir = '..\..\..\..\yukon-server\Resource'
    $path = '..\resources'
}

# get all the files in an array
$complete_list = gci -Recurse -include *.x?? -Path $path | select fullname

# sort them into buckets by type (schema, pao xml, point xml)
$complete_list | % {
    if ( $_.fullname -match '\\definition\\(.*\.(xml|xsd))$' )
    {
        $path = $matches[1].ToLower() -replace('\\', '\\')  # i know, right?
        $type = $matches[2].ToUpper()

        if ( $type -eq 'XSD' )
        {
            $xsds += $path
        }
        elseif ( $path -match '^.*\\points\\.*$' )
        {
            $points += $path
        }
        elseif ( $path -match '^.*\\.*$' )
        {
            $paos += $path
        }
    }
    elseif ( $_.fullname -match '^.*\\config\\(.*\.(xml|xsd))$'  )
    {
        $path = $matches[1].ToLower() -replace('\\', '\\')  # i know, right?
        $type = $matches[2].ToUpper()

        if ( $type -eq 'XSD' )
        {
            $configs_xsd += $path
        }
        else
        {
            $configs_xml += $path
        }
    }
}

## check to see if the file hashes changed since we last generated the indexes

$sha1 = New-Object System.Security.Cryptography.SHA1CryptoServiceProvider 

## compute the new hashes...
$new_hashes = $complete_list | % {

    '{1} {0}' -f $_.fullname,[System.Convert]::ToBase64String( $sha1.ComputeHash([System.IO.File]::ReadAllBytes($_.fullname)))
}

$resource_hashes_file = $server_resource_dir + '\resource_hashes.txt'

## ... and compare against the old hashes, if they exist
if ( Test-Path -Path $resource_hashes_file -PathType Leaf )
{
    $old_hashes = [System.IO.File]::ReadAllLines($resource_hashes_file)

    if ( !(diff $new_hashes $old_hashes) )
    {
        ## no differences, so exit without updating the files
        Exit 0
    }
}

$new_hashes | Out-File -FilePath $resource_hashes_file

function delete_if_exists( $file )
{
    if ( Test-Path -Path $file -PathType Leaf )
    {
        Remove-Item -Path $file
    }
}

## remove old auto-generated files

$server_resource_header_file = $server_resource_dir + '\include\autogen_resource_ids.h'
$server_resource_file        = $server_resource_dir + '\autogen_resource.rc'

delete_if_exists $server_resource_header_file
delete_if_exists $server_resource_file

function write_files( $id, $def_name, $type, $branch, $list )
{
    $start_str = '#define RC_{0}_START_ID' -f $def_name;
    '{0,-27} {1,6}' -f $start_str,$id | Out-File -FilePath $server_resource_header_file -Append -encoding ASCII

    $list | % {
        '{0,-12}{1,-11}{2}{3}{4}"' -f $id,$type,$root,$branch,$_ | Out-File -FilePath $server_resource_file  -Append -encoding ASCII
        $index.Add( $id.ToString(), $_ )
        $id++
    }

    $end_str = '#define RC_{0}_END_ID' -f $def_name;
    '{0,-27} {1,6}' -f $end_str,$id | Out-File -FilePath $server_resource_header_file -Append -encoding ASCII
}

## write out the schemas - adding each to the index

write_files 500  'XSD'    'XSD' 'pao\\definition\\' $xsds
write_files 600  'CFGXSD' 'XSD' 'device\\config\\'  $configs_xsd
write_files 700  'CFGXML' 'XML' 'device\\config\\'  $configs_xml
write_files 1000 'PAO'    'XML' 'pao\\definition\\' $paos
write_files 2000 'POINT'  'XML' 'pao\\definition\\' $points

## create the index xml file

$resource_index_file = 'resource_index.xml'
$resource_index_fullpath = $server_resource_dir + '\' + $resource_index_file

delete_if_exists $resource_index_fullpath

## write the index files ID

$index_id = 3000

'#define RC_INDEX_ID {0,14}' -f $index_id | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

'{0,-12}{1,-11}"{2}"' -f $index_id,"XML",$resource_index_file  | Out-File -Append -FilePath $server_resource_file -encoding ASCII

$index.Add( $index_id.ToString(), $resource_index_file )

## write the index file

$index | ConvertTo-Xml -As String -NoTypeInformation | Out-File -Append -FilePath $resource_index_fullpath -encoding ASCII
