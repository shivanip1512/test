package com.cannontech.web.dev.model;

public class Person {
    
    public Person() {}
    
    public Person(int id, String name, String email, int age, boolean spam) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.spam = spam;
    }
    
    private int id;
    private String name;
    private String email;
    private int age;
    private boolean spam;
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean isSpam() { return spam; }
    public void setSpam(boolean spam) { this.spam = spam; }
    
}