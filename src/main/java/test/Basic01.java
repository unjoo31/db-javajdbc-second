package test;

import lombok.Getter;

@Getter
class 런닝머신 {
    private int cal;

    public void walk(int speed, int time){
        cal = speed * time * 10;
    }
}


class People {
    private int power;

    public People(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public void eat(int food) {
        this.power = food * 2;
    }

    public void ex(int min) {
        this.power = this.power - min*10;
    }
}

public class Basic01 {
    public static void main(String[] args) {
        People p = new People(0);
        p.eat(100);
        System.out.println(p.getPower());
    }
}
