package com.cannontech.web.dev.model;

public class Person {
    
    public Person() {}
    
    private String name;
    private String email;
    private int age;
    private boolean spam;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean isSpam() { return spam; }
    public void setSpam(boolean spam) { this.spam = spam; }
    
}