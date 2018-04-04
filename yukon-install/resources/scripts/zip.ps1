param (
    [string]$source,
    [string]$dest
)

Add-Type -Assembly System.IO.Compression;

$sourceFilename = $source | Split-Path -Leaf;

$zipfile = [System.IO.Compression.ZipFile]::Open($dest, [System.IO.Compression.ZipArchiveMode]::Create);

[System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zipfile, $source, $sourceFilename, [System.IO.Compression.CompressionLevel]::Optimal);

$zipfile.Dispose();