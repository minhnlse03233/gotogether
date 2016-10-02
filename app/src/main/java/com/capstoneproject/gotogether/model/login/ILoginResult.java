package com.capstoneproject.gotogether.model.login;

/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public interface ILoginResult {
    void loginSuccess();
    void loginCancel();
    void loginError();
    void loginStatus(String status);
}
