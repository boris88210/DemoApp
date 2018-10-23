package com.development.borissu.demoapp.dataModel;

public class SelectionItem {

    private int id;
    private String name;

    public SelectionItem() {
    }

    public SelectionItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SelectionItem){
            if (this.id == ((SelectionItem)obj).id){
                return true;
            }
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
