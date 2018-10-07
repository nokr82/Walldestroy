package com.hdu.walldestroy;

public class Wall {

    int x,y;
    int WallSpeed = 15;

    Wall(int x, int y){
        this.x=x;
        this.y=y;

    }


    public void move(){
        y+=WallSpeed;
    }
}
