cat .\OracleTableCreation.sql | % {$_ -replace "BINMax","BLOB  "} | out-file -Encoding utf8 .\OracleTableCreation.processed.sql
Remove-Item .\OracleTableCreation.orig.sql
Move-Item .\OracleTableCreation.sql .\OracleTableCreation.orig.sql
Move-Item .\OracleTableCreation.processed.sql .\OracleTableCreation.sql

cat .\SqlServerTableCreation.sql | % {$_ -replace "BINMax        ","varbinary(Max)"} | out-file -Encoding utf8 .\SqlServerTableCreation.processed.sql
Remove-Item .\SqlServerTableCreation.orig.sql
Move-Item .\SqlServerTableCreation.sql .\SqlServerTableCreation.orig.sql
Move-Item .\SqlServerTableCreation.processed.sql .\SqlServerTableCreation.sql