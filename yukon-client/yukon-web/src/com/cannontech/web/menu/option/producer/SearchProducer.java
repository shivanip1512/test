package com.cannontech.web.menu.option.producer;

import java.util.List;

public interface SearchProducer {

	/**
	 * Get the search form's action path.
	 * @return A URL path to the search controller. Example: "/spring/stars/operator/account/search"
	 */
	public String getFormMethod();
	
	/**
	 * Get the search form's method.
	 * @return GET or POST
	 */
	public String getFormAction();
	
	/**
	 * Get the name of the search form's text field input element.
	 * @return A field name. Example "seachValue" would produce {@literal <input type="text" name="searchValue">}
	 */
	public String getFieldName();
	
	/**
	 * Optional. Get a list of the available SearchTypes.
	 * The search types list will render as a drop down of of available search types.
	 * Used to tell the search controller what type of search to do with the search value. 
	 * For example, in the Operator module a user may search by several different value types like Account Number, Last Name, Phone #, etc.
	 */
	public List<SearchType> getTypeOptions();
	
	/**
	 * Optional. For use with getTypeOptions().
	 * Get the name of the search form's search type drop down field input element.
	 * @return A field name. Example "searchType" would produce {@literal  <select name="searchType">...</select>}
	 */
	public String getTypeName();
}
