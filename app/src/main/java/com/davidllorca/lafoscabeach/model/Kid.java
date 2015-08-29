package com.davidllorca.lafoscabeach.model;

/**
 * Model class Kid.
 */
public class Kid {

    //Attributes
    private String name;
    private int age;

    //Constructor
    public Kid() {
    }

    public Kid(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Kid{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
