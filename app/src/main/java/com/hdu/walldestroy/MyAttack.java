package com.hdu.walldestroy;

public class MyAttack {

    int x,y;
    int missileSpeed=35;

    MyAttack(int x,int y){
        this.x=x;
        this.y=y;

    }
    public void move(){
        y-=missileSpeed;

    }
}
