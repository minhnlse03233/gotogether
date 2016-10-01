package com.capstoneproject.gotogether.view;

/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public interface ILoginView {
    public void loginChangeInten(String id);
    public void loginCancel();
    public void loginError();
    void statusUser(String status);
}
