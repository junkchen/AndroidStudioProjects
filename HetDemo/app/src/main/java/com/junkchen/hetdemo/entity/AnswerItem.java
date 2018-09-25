package com.junkchen.hetdemo.entity;

public class AnswerItem {
    private boolean isSelected = false;
    private String description;

    public AnswerItem() {
    }

    public AnswerItem(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
