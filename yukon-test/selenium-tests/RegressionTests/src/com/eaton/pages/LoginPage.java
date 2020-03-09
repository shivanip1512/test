package com.eaton.pages;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.ValidUserLogin;

public class LoginPage extends PageBase {

    private boolean loggedIn;

    public LoginPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        SeleniumTestSetup.navigateToLoginPage();
        requiresLogin = false;
        loggedIn = false;
    }

    public void login(String userName, String password) {
        getUserName().setInputValue(userName);
        getPassword().setInputValue(password);

        getLoginBtn().click();
    }

    public boolean login() {

        if (!loggedIn) {
            getUserName().setInputValue(ValidUserLogin.getUserName());
            getPassword().setInputValue(ValidUserLogin.getPassword());

            getLoginBtn().click();

            loggedIn = true;
        }

        return loggedIn;
    }

    public TextEditElement getUserName() {
        return new TextEditElement(this.driverExt, "USERNAME");
    }

    public TextEditElement getPassword() {
        return new TextEditElement(this.driverExt, "PASSWORD");
    }

    public Button getLoginBtn() {
        return new Button(this.driverExt, "Login");
    }
}
