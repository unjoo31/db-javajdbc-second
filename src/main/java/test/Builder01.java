package test;

import lombok.Getter;

@Getter
class Person {
    private Integer id;
    private String name;
    private Integer age;

    public Person(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}

public class Builder01 {

    public static void main(String[] args) {
        Person p1 = new Person(null, "홍길동", 20);
    }
}
