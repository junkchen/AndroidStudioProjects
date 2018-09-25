package com.junkchen.autocloseapp;

/**
 * Created by Junk on 2017/8/19.
 */

public class ApplicationSubject extends Subject {
    public void exit() {
        notifyObservers();
    }
}
