param (
    [string]$dest,
    [string]$source
)

Add-Type -Assembly System.IO.Compression;
Add-Type -Assembly System.IO.Compression.FileSystem;

$sourceFilename = $source | Split-Path -Leaf;

$zipfile = [System.IO.Compression.ZipFile]::Open($dest.Replace(".log.zip", ".zip"), [System.IO.Compression.ZipArchiveMode]::Create);

[System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zipfile, $source, $sourceFilename, [System.IO.Compression.CompressionLevel]::Optimal);

$zipfile.Dispose();