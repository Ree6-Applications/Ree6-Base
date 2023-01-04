package de.presti.ree6.menu;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MenuUtil {

    @Getter(AccessLevel.PACKAGE)
    static List<MenuItem> items = new ArrayList<>();

    public void addMenuItem(MenuItem item) {
        items.add(item);
    }

    public void remove(MenuItem item) {
        items.remove(item);
    }
}
