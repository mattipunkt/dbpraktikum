package dev.numerouno.importer;

import java.util.ArrayList;
import java.util.List;

class Category {
    private final String name;
    Category parent;
    List<Category> children = new ArrayList<>();
    List<Product> items = new ArrayList<>();

    Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }
}
