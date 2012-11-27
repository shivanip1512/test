package com.cannontech.web.menu.option.producer;

import java.util.List;

public class SearchFormData {
    private String formMethod = "get";
    private String formAction;
    private String fieldName;
    private List<SearchType> typeOptions = null;
    private String typeName = null;

    public SearchFormData(String formAction, String fieldName) {
        super();
        this.formAction = formAction;
        this.fieldName = fieldName;
    }

    /**
     * Get the search form's action path.
     * @return A URL path to the search controller. Example:
     *         "/stars/operator/account/search"
     */
    public String getFormMethod() {
        return formMethod;
    }

    /**
     * Get the search form's method.
     * @return GET or POST
     */
    public String getFormAction() {
        return formAction;
    }

    /**
     * Get the name of the search form's text field input element.
     * @return A field name. Example "seachValue" would produce {@literal <input
     *         type="text" name="searchValue">}
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Optional. Get a list of the available SearchTypes. The search types list
     * will render as a drop down of of available search types. Used to tell the
     * search controller what type of search to do with the search value. For
     * example, in the Operator module a user may search by several different
     * value types like Account Number, Last Name, Phone #, etc.
     */
    public List<SearchType> getTypeOptions() {
        return typeOptions;
    }

    /**
     * Optional. For use with getTypeOptions(). Get the name of the search
     * form's search type drop down field input element.
     * @return A field name. Example "searchType" would produce {@literal
     *         <select name="searchType">...</select>}
     */
    public String getTypeName() {
        return typeName;
    }

    public void setFormMethod(String formMethod) {
        this.formMethod = formMethod;
    }

    public void setFormAction(String formAction) {
        this.formAction = formAction;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setTypeOptions(List<SearchType> typeOptions) {
        this.typeOptions = typeOptions;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
