#
# PowerShell v1.0 compatible script to auto-generate the files needed to build yukon-resources.dll
#  Run from the yukon-shared\src\main\scripts directory within the ISE.
#

$xsds = @()
$paos = @()
$points = @()
$index = @{}

$root = '"..\\..\\yukon-shared\\src\\main\\resources\\pao\\definition\\'

$server_resource_dir = '..\..\..\..\yukon-server\Resource'

# get all the files in an array
$complete_list = gci -Recurse -include *.x?? -Path ..\resources\pao\definition | select fullname

# sort them into buckets by type (schema, pao xml, point xml)
$complete_list | % {
    $_.fullname -match '^.*\\definition\\(.*\.(xml|xsd))$'

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
        else
        {
            $paos += $path
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

## write out the schemas - adding each to the index

$xsd_id = 500

'#define RC_XSD_START_ID    ' + $xsd_id.ToString() | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

$xsds | % {
    $xsd_id.ToString() + '        XSD        ' + $root + $_ + '"' | Out-File -Append -FilePath $server_resource_file -encoding ASCII
    $index.Add( $xsd_id.ToString(), $_ )
    $xsd_id++
}

'#define RC_XSD_END_ID      ' + $xsd_id.ToString() | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

## repeat for paos

$pao_id = 1000

'#define RC_PAO_START_ID    ' + $pao_id.ToString() | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

$paos | % {
    $pao_id.ToString() + '       XML        ' + $root + $_ + '"' | Out-File -Append -FilePath $server_resource_file -encoding ASCII
    $index.Add( $pao_id.ToString(), $_ )
    $pao_id++
}

'#define RC_PAO_END_ID      ' + $pao_id.ToString() | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

## repeat for points

$point_id = 2000

'#define RC_POINT_START_ID  ' + $point_id.ToString() | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

$points | % {
    $point_id.ToString() + '       XML        ' + $root + $_ + '"' | Out-File -Append -FilePath $server_resource_file -encoding ASCII
    $index.Add( $point_id.ToString(), $_ )
    $point_id++
}

'#define RC_POINT_END_ID    ' + $point_id.ToString() | Out-File -Append -FilePath $server_resource_header_file -encoding ASCII

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

$index_id.ToString() + '       XML        "' + $resource_index_file + '"' | Out-File -Append -FilePath $server_resource_file -encoding ASCII

$index.Add( $index_id.ToString(), $resource_index_file )

## write the index file

$index | ConvertTo-Xml -As String -NoTypeInformation | Out-File -Append -FilePath $resource_index_fullpath -encoding ASCII
