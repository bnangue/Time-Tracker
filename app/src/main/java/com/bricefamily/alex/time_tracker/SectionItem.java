package com.bricefamily.alex.time_tracker;

/**
 * Created by praktikum on 27/01/16.
 */
public class SectionItem implements Item{

    private final String title;

    public SectionItem(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

}
