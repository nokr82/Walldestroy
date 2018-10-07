package com.hdu.walldestroy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Bitmap ch;//캐릭터이미지
    int ch_x,ch_y;//캐릭터위치
    Bitmap Right,Left;//화살표이미지
    int Left_x,Left_y;
    int Right_x,Right_y;
    int button_width;
    int chWidth;
    Bitmap screen;//배경이미지
    int Width,Height;//사용자해상도
    int score;


    //미사일장치
    Bitmap AttackButton;
    int AttackButton_x,AttackButton_y;
    int AttackWidth;
    int Attack_middle;//공격크기반
    Bitmap Motion;
    Bitmap Wallimg;

    int count;
    ArrayList<MyAttack> myA;
    ArrayList<Wall> Wall;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));

 /*     setContentView(new MyView(this)); 대신에
 MyView m = new MyView(this);  setContentView(m);  이런 방법으로 해도 된다 */

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        //사용중인 가로,세로크기를 변수에 대입
        Width = display.getWidth();
        Height = display.getHeight();


        myA = new ArrayList<MyAttack>();
        Wall = new ArrayList<Wall>();



        //캐릭터의 그림파일의크기를 임의로 조절하는 소스,단말기의 해상도에 맞게 적용
        ch = BitmapFactory.decodeResource(getResources(),R.drawable.ch);
        int y = Height/11;
        int x = Width/8;
        ch = Bitmap.createScaledBitmap(ch,x,y,true);
        chWidth = ch.getWidth();
        //Bitmap클래스의 getWidth메소드를 활용해서 그림크기를 구할 수 있다.
        ch_x =Width*4/9;
        ch_y = Height*6/9;


        //왼쪽이동처리
        Left = BitmapFactory.decodeResource(getResources(),R.drawable.wleft);
        Left_x = Width*5/9;
        Left_y = Height*7/9;
        button_width = Width/6;
        Left = Bitmap.createScaledBitmap(Left,button_width,button_width,true);

        //오른쪽처리
        Right = BitmapFactory.decodeResource(getResources(),R.drawable.wright);
        Right_x = Width*7/9;
        Right_y = Height*7/9;
        Right = Bitmap.createScaledBitmap(Right,button_width,button_width,true);

        //공격버튼 처리
        AttackButton = BitmapFactory.decodeResource(getResources(),R.drawable.attack);
        AttackButton = Bitmap.createScaledBitmap(AttackButton,button_width,button_width,true);
        AttackButton_x = Width*1/11;
        AttackButton_y = Height*7/9;

        //공격처리
        Motion = BitmapFactory.decodeResource(getResources(),R.drawable.motion);
        Motion = Bitmap.createScaledBitmap(Motion,x,y,true);
        AttackWidth=Motion.getWidth();

        //벽처리
        Wallimg = BitmapFactory.decodeResource(getResources(),R.drawable.wall);
        Wallimg = Bitmap.createScaledBitmap(Wallimg,Width,button_width,true);




        //배경처리
        screen = BitmapFactory.decodeResource(getResources(),R.drawable.screen);
        screen = Bitmap.createScaledBitmap(screen,Width,Height,true);


    }

    class MyView extends View {
        MyView(Context context) {
            super(context);//상위클래스의 생성자 호출
            gHendler.sendEmptyMessageDelayed(0,1000);

        }

        @Override
        synchronized public void onDraw(Canvas canvas) {
            //Random r1 = new Random();
            int x = ch_x-10;
            if (Wall.size()<10)
                Wall.add(new Wall(x, -100));



            //이곳에 화면에 나타낼 그림이나 문자를 처리
            Paint p1 = new Paint();
            p1.setColor(Color.RED);
            p1.setTextSize(50);
            canvas.drawBitmap(screen,0,0,p1);

            canvas.drawText(Integer.toString(count),0,300,p1);
            canvas.drawText("점수: "+Integer.toString(score),0,200,p1);
            canvas.drawBitmap(ch,ch_x,ch_y,p1);
            canvas.drawBitmap(Right,Right_x,Right_y,p1);
            canvas.drawBitmap(Left,Left_x,Left_y,p1);
            canvas.drawBitmap(AttackButton,AttackButton_x,AttackButton_y,p1);

            for (MyAttack tmp:myA)
                canvas.drawBitmap(Motion,tmp.x,tmp.y,p1);

            for (Wall tmp: Wall)
                canvas.drawBitmap(Wallimg,tmp.x,tmp.y,p1);

            moveMissile();
            movePlanet();
            checkCollision();
            count++;


        }

        public void moveMissile(){
            for(int i = myA.size()-1;i>=0;i--) {
                myA.get(i).move();
            }
            for (int i = myA.size()-1;i>=0;i--){
                //공격이화면에 벗어나면 없어지도록
                if (myA.get(i).y<0)
                    myA.remove(i);
            }
        }

        public void movePlanet(){

            for (int i=Wall.size()-1;i>=0;i--){
                Wall.get(i).move();
            }
            for (int i=Wall.size()-1;i>=0;i--){
                //공격이 화면을 벗어나게되면 없에도록 한다
                if (Wall.get(i).y>Height)
                    Wall.remove(i);
            }
        }
        public void checkCollision(){
            for (int i=Wall.size() -1; i>=0; i--){
                for (int j=myA.size()-1; j>=0; j--){
                    if (myA.get(j).x + Attack_middle > Wall.get(i).x
                            &&myA.get(j).x + Attack_middle < Wall.get(i).x+button_width
                            &&myA.get(j).y > Wall.get(i).y
                            &&myA.get(j).y < Wall.get(i).y + button_width){
                        Wall.remove(i);
                        myA.get(j).y=-300;
                        score+=10;//벽을 부술 경우 점수가 10점씩 증가
                    }
                }
            }
        }

        Handler gHendler= new Handler(){
            public void handleMessage(Message msg){
                //반복처리부분
                invalidate();
                gHendler.sendEmptyMessageDelayed(0,30);
                //1000으로 하면 1초에 한번실행
            }
        };
        @Override
        public boolean onTouchEvent(MotionEvent event){
            //화면을 터치했을 경우 처리
            int x=0,y=0;
            if (event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
                x= (int)event.getX();
                y=(int)event.getY();
            }

            //이동버튼 클릭시 이동거리
            if ((x > Left_x) && (x < Left_x + button_width) && (y > Left_y) && (x < Left_y + button_width))
                ch_x -= 20;


            if ((x > Right_x) && (x < Right_x + button_width) && (y > Right_y) && (x < Right_y + button_width))
                ch_x += 20;

            if (event.getAction() == MotionEvent.ACTION_DOWN)
                if ((x > AttackButton_x) && (x < AttackButton_x + button_width) && (y > AttackButton_y) && (x < AttackButton_y + button_width))

                    if (myA.size() < 1) {
                        myA.add(new MyAttack(ch_x + chWidth / 2 - AttackWidth / 2, ch_y));//공격이나가는방향
                    }

            return true;
        }
    }
}