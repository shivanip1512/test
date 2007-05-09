package com.cannontech.cbc.model;


public interface CBCCreationModel extends EditorDataModel{

    /**
     * Returns the type that is selected based on on the fact that if the 
     *  secondaryType is set return it, else return type
     * @return
     */
    public abstract int getSelectedType();

    /**
     * Returns all the CBC that require a port for communications.
     * @return
     */
    public abstract boolean isPortNeeded();

    /**
     * @return
     */
    public abstract int getWizPaoType();

    /**
     * @param i
     */
    public abstract void setWizPaoType(int i);

    /**
     * @return
     */
    public abstract Boolean getDisabled();

    /**
     * @return
     */
    public abstract String getName();

    /**
     * @param boolean1
     */
    public abstract void setDisabled(Boolean boolean1);

    /**
     * @param string
     */
    public abstract void setName(String string);

    /**
     * @return
     */
    public abstract Integer getSecondaryType();

    /**
     * @param integer
     */
    public abstract void setSecondaryType(Integer integer);

    /**
     * @return
     */
    public abstract Integer getPortID();

    /**
     * @param integer
     */
    public abstract void setPortID(Integer integer);

    /**
     * @return
     */
    public abstract CBCCreationModel getNestedWizard();

    /**
     * @return
     */
    public abstract boolean isCreateNested();

    /**
     * @return
     */
    public abstract void setCreateNested(boolean val);

    public abstract void updateDataModel();

}