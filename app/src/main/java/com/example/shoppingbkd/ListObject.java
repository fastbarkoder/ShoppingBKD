package com.example.shoppingbkd;

public class ListObject {
    private int id;
    private String name;
    private String type;
    private String code;

    public ListObject() {
        this(-1);
    }

    public ListObject(int id) {
        this.id = id;
        name = "";
        type = "";
        code = "";
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
