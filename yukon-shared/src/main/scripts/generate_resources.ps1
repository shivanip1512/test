#
# PowerShell v1.0 compatible script to auto-generate the files needed to build yukon-resources.dll
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
    if ( $_.fullname -match '^.*\\definition\\(.*\.(xml|xsd))$' )
    {
        $path = $matches[1].ToLower() -replace('\\', '\\')  # i know, right?
        $type = $matches[2].ToUpper()

        if ( $type -eq 'XSD' )
        {
            $xsds += $path
        }
        else
        {
            if ( $path -match '^.*\\points\\.*$' )
            {
                $points += $path
            }
            elseif ( $path -match '^.*\\.*$' )
            {
                $paos += $path
            }
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

## remove old auto-generated files

$server_resource_header_file = $server_resource_dir + '\include\autogen_resource_ids.h'

if ( Test-Path -Path $server_resource_header_file -PathType Leaf )
{
    Remove-Item -Path $server_resource_header_file
}

$server_resource_file = $server_resource_dir + '\autogen_resource.rc'

if ( Test-Path -Path $server_resource_file -PathType Leaf )
{
    Remove-Item -Path $server_resource_file
}

function write_files( $id, $def_name, $type, $branch, $list )
{
    '#define RC_' + $def_name + '_START_ID    ' + $id.ToString() | Out-File -FilePath $server_resource_header_file -Append -encoding ASCII

    $list | % {
        $id.ToString() + '        ' + $type + '        ' + $root + $branch + $_ + '"' | Out-File -FilePath $server_resource_file  -Append -encoding ASCII
        $index.Add( $id.ToString(), $_ )
        $id++
    }

    '#define RC_' + $def_name + '_END_ID      ' + $id.ToString() | Out-File -FilePath $server_resource_header_file -Append -encoding ASCII
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

if ( Test-Path -Path $resource_index_fullpath -PathType Leaf )
{
    Remove-Item -Path $resource_index_fullpath
}

## write the index files ID

$index_id = 3000

'#define RC_INDEX_ID        ' + $index_id.ToString() | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

$index_id.ToString() + '        XML        "' + $resource_index_file + '"' | Out-File -Append -FilePath $server_resource_file -encoding ASCII

$index.Add( $index_id.ToString(), $resource_index_file )

## write the index file

$index | ConvertTo-Xml -As String -NoTypeInformation | Out-File -Append -FilePath $resource_index_fullpath -encoding ASCII
