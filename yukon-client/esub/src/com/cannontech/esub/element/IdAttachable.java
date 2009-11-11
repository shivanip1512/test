package com.cannontech.esub.element;

public interface IdAttachable {

    /**
     * Interface method to be implemented by drawings elements that
     * can reference points or devices.  Returns true if the element needs
     * to be revised by a human.
     * @return needsAttention
     */
    public boolean fixIds();
}
