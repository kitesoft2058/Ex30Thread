package com.kitesoft.ex30thread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int num=0;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv= findViewById(R.id.tv);

    }

    public void clickBtn(View v){
        //오래걸리는 작업
        //별도의 Thread를 사용하지 않았으므로 Main Thread가 처리함.
        for(int i=0; i<20; i++){

            num++;
            //화면에 num값 출력 /////////
            tv.setText(num+"");

            //0.5초간 대기
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //MainThread가 위 작업을 수행하는 약 20초동안은 다른 작업은 할 수 없으므로..
        //다른 버튼을 눌러도 앱이 응답(동작)하지 않는 사태가 발생함.( 위 작업 도중에 아래 버튼을 누르면 5초 후 에러남)
        //이를 ANR(Application Not Respondiong)에러라 함

        // 반복하면서 TextView에 num값을 출력하는 작업을 요청하지만..
        //Main Thread가 반복문 밖으로 나와서 화면에 그려주는(텍스트를 출력하는) 작업을 수행할 수 없기에
        //변경된 내용을 확인 할 수 없으며.. 반복문이 모두 종료된 후 이 clickBtn() 메소드가 종료되면
        //그때 출력하므로 최종 20 이라는 숫자만 보이게 되버림.

    }//clickBtn Method...



    public void clickBtn2(View view) {
        //오래 걸리는 작업수행..(ex. network, db작업)
        //하는 직원객체(MyThread) 생성 및 실행
        MyThread t= new MyThread();
        t.start(); // run()메소드가 실행
    }


    //오래걸리는 작업을 수행하는 스레드의 설계
    class MyThread extends Thread{
        @Override
        public void run() {
            //오래걸리는 작업
            for(int i=0; i<20; i++){

                num++;

                //화면에 num값 출력/////////
                //UI변경작업은 반드시
                //UI Thread(Main Thread)만이 수행가능 [ API 29버전부터 별도 Thread에서 runOnUIThread없이도 UI변경됨. ]
                //MainThread에게 UI변경작업 요청!!

                //방법1. Handler를 이용하는 방법
                //handler.sendEmptyMessage(0);

                //방법2. Activity클래스의 메소드인
                //      runOnUiThread()라는 메소드 이용
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //MainThread에게 UI변경에 대한
                        //위임장을 받은 Runable객체이므로
                        //이 메소드안에서 UI변경 가능
                        tv.setText(num+"");
                    }
                });


                //0.5초간 대기
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //MainActivity class 멤버변수
    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //sendEmptyMessage()가 호출되면
            //자동으로 실행되는 메소드
            //이 메소드안에서는 UI변경작업이 가능!!
            tv.setText(num+"");
        }
    };


}//MainActivity class...
