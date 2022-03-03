param (
    [string]$src,
    [string]$dest
)

Add-Type -Assembly System.IO.Compression.FileSystem
$compressionLevel = [System.IO.Compression.CompressionLevel]::Optimal
[System.IO.Compression.ZipFile]::CreateFromDirectory($src, $dest, $compressionLevel, $false)
