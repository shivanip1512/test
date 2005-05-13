
// -------------------------------------------
//Ensures the given string is a positive integer
// -------------------------------------------
function isPosInt( strValue )
{
	var valids = "0123456789";
	return isAllCharsInSet( strValue, valids );
}

// -------------------------------------------
//Discovers if all of a given strings characters are in
// the given set.
// -------------------------------------------
function isAllCharsInSet( strValue, validStrSet )
{
	if( strValue == null || validStrSet == null )
		return false;

	for(var i = 0; i < strValue.length; ++i)
	{
		if( validStrSet.indexOf(strValue.charAt(i),0) == -1 )
		{
			return false;
		}
	}
	
	return true;
}
