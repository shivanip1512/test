package com.cannontech.dbeditor.editor.user;

public abstract class ChangePasswordDialog {
    
    public static ChangePasswordDialog createPasswordDialog(String title, String message) {
        return new ChangePasswordDialog() {
            @Override
            public String getNewPassword() {
                return "new password";
            }
            
            @Override
            public boolean show() {
                return true;
            }
        };
    }
    
    /**
     * Blocks until dialog is dismissed.
     * @return true if OK was pressed, false otherwise
     */
    public abstract boolean show();
    
    /**
     * @return the new password entered by user
     */
    public abstract String getNewPassword();
}
