package com.bricefamily.alex.time_tracker;

import java.util.Comparator;

/**
 * Created by praktikum on 01/07/15.
 */
public class ComparatorOrderType implements Comparator<EventObject> {
    @Override
    public int compare(EventObject lhs, EventObject rhs) {
        String orderType1= lhs.creationTime;
        String orderType2=rhs.creationTime;

        int idf = orderType1.compareTo(orderType2);

        return idf;
    }
    @Override
    public boolean equals(Object object) {
        return false;
    }
}
