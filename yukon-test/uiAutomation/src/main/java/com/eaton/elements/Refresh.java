package com.eaton.elements;

public class Refresh<T> {
	
	//Private
	private T obj;
	private boolean isDirty;
	
	//================================================================================
    // Constructors Section
    //================================================================================
	
	public Refresh()
	{
		setObject(null);
	}
	
	public Refresh(T obj)
	{
		setObject(obj);
	}
	
	//================================================================================
    // Getters/Setters Section
    //================================================================================
		
	public boolean getIsDirty()
	{
		return isDirty;
	}
	
	public void setIsDirty(boolean isDirty)
	{
		this.isDirty = isDirty;
	}
	
	public void setObject(T obj)
	{
		this.obj = obj;
		if(obj == null) {
			isDirty = true;
		}
		else {
			isDirty = false;
		}
			
    }
	
    public T getObject()
    {
    	return obj;
    }
}
