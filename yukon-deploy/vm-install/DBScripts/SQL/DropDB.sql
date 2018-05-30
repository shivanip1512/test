Use master;
IF EXISTS(SELECT name 
		  FROM sys.databases 
		  WHERE name = 'tempDatabase')
	BEGIN
		ALTER DATABASE tempDatabase 
			SET SINGLE_USER 
			WITH ROLLBACK IMMEDIATE
		DROP DATABASE tempDatabase
    	DROP LOGIN tempDatabase
	END
