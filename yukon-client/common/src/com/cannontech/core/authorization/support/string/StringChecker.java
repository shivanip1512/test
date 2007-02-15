package com.cannontech.core.authorization.support.string;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.Checker;

/**
 * Class used to check a string against a list of accepted strings
 */
public class StringChecker implements Checker<String> {

    private List<String> checkStringList = null;

    public void setCheckStringList(List<String> checkStringList) {
        this.checkStringList = checkStringList;
    }

    public void setCheckString(String checkString) {
        this.checkStringList = new ArrayList<String>();
        this.checkStringList.add(checkString);
    }

    /**
     * Method to check a string
     * @param string - String to check
     * @return True if the string is in the checkStringList
     */
    public boolean check(String string) {

        for (String checkString : this.checkStringList) {
            if (checkString.equals(string)) {
                return true;
            }
        }
        return false;
    }

}
