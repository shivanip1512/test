$src=$args[0]
$dest=$args[1]

Add-Type -Assembly System.IO.Compression.FileSystem
$compressionLevel = [System.IO.Compression.CompressionLevel]::Optimal
[System.IO.Compression.ZipFile]::CreateFromDirectory($src, $dest, $compressionLevel, $false)
