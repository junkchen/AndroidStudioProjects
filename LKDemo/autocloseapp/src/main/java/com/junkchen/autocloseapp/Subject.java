package com.junkchen.autocloseapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junk on 2017/8/19.
 */

public class Subject {
    private List<Observer> observers = new ArrayList<>();

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
        observers.clear();
    }
}
