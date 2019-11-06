function update-sql-keywords {
    param ( 
        [string] $filename, 
        [hashtable] $replacements 
    )
    $rewritten = $filename + '.rewritten'
    $filesize = (Get-Item $filename).Length
    # use script scope so the callback can increment it
    $script:changes = 0  
    # join all keys into a single regex
    $regex = [regex]($replacements.Keys -join "|") 
    # callback used with Regex.Replace uses the found value to index into the replacement hashmap
    $replacement_callback = { 
        $script:changes++; 
        $replacements[$args[0].Value] 
    } 
    
    # Pipe to a file so we don't load all 700+kB of the file in at once
    cat $filename | % { 
        $processed += ([string]$_).Length + 2 # +2 for CRLF
        $new_percent = $processed / $filesize * 100
        if ($new_percent -ge $percent + 1) {
            $percent = $new_percent
            Write-Progress -Activity "Updating $filename" -PercentComplete $percent
        }
        $regex.Replace($_, $replacement_callback)  # replace all regexes at once using the callback
    } | out-file -Encoding ASCII $rewritten 
    # TODO - Update Encoding to UTF8NoBom when we get PowerShell 6: https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.utility/out-file?view=powershell-6

    if (!$changes) {
        Write-Output "$filename had no changes"
        Remove-Item $rewritten
    } elseif (!(Test-Path -PathType Leaf $rewritten)) {
        Write-Error "$filename could not be rewritten to $rewritten"
    } else {
        $pluralized = @('replacement','replacements')[$changes -ne 1]  # use false/true to index into the array for outputting the correct plural
        Write-Output "$filename had $changes $pluralized"
        Remove-Item $filename
        Move-Item $rewritten $filename
    }
}

$sql_filename = ".\SqlServerTableCreation.sql"
$sql_replacements = @{ "BINMax        " = 
                       "varbinary(Max)" }
update-sql-keywords $sql_filename $sql_replacements

$ora_filename = ".\OracleTableCreation.sql"
$ora_replacements = @{ "BINMax" = "BLOB  " }
update-sql-keywords $ora_filename $ora_replacements