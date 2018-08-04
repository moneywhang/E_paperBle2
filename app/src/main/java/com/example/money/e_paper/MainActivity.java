package com.example.money.e_paper;

import android.app.Activity;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    bluetooth_40 Ble_all;
    bluetooth_41 Ble_1;
    bluetooth_42 Ble_2;
    TextView tx1;
    Thread Ble_thread1,Ble_thread2,Ble_thread3;
    boolean A1_stus =false,A2_stus=false,A3_stus=false;
    int count1 =0,count2=0;
    private boolean stop_thread =true,stop_thread1=true,stop_thread2=true;
    Message msg;
    int A_btncount=0,B_btncount=0,C_btncount=0;
    int dd=0;
    String sendDATA ="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tx1 = (TextView)findViewById(R.id.tx1);
        msg =new Message();

        Ble_1 =new bluetooth_41(this);
       //Ble_2 =new bluetooth_42(this);


    }
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.im1:
                sendDATA ="1";
                //---------A_btncount 第一次案
                if(A_btncount==0){
                    Ble_1.Ble_Disconnect1();
                   // Ble_2.Ble_Disconnect2();
                    Ble_all =new bluetooth_40(this);
                    Ble_connect1();
                }else{
                    Ble_all.Ble_Reconnect();
                    Ble_connect1();
                }
                A_btncount+=1;
                break;
            case R.id.im2:
                sendDATA ="2";
                Ble_all.Ble_Reconnect();
                 stop_thread =true;
                  Ble_connect1();

                break;
            case R.id.im3:
              /*  sendDATA ="3";
                Ble_all.Ble_Reconnect();
                Ble_connect1();*/
                break;

        }
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    Ble_all.Read_Rssi();
                    Log.i("jim","Ble_all___"+Ble_all.rssi_int1);
                    if(Ble_all.rssi_int1>-45&&Ble_all.rssi_int1!=0){
                        //Log.i("jim","A:很靠近了");
                        Ble_all.sendMessage(sendDATA);
                        Ble_all.Ble_Disconnect();
                        Ble_1.Ble_Reconnect1();
                        //stop_thread1=true;

                        Ble_connect2();
                    }

                    break;
                case 2:
                    Ble_1.Read_Rssi1();
                    Log.i("jim","RSSI_B  :   "+Ble_1.rssi_int2);
                    if(Ble_1.rssi_int2>-45&&Ble_1.rssi_int2!=0){
                        Log.i("jim","B:很靠近了");
                        Ble_1.sendMessage1(sendDATA);
                        Ble_1.Ble_Disconnect1();
                       // Ble_2.Ble_Reconnect2();
                       // Ble_connect3();
                       // stop_thread1 =false;
                    }
                    break;
                case 3:
                    Ble_2.Read_Rssi2();
                    Log.i("jim","RSSI_C  :   "+Ble_2.rssi_int3);
                    if(Ble_2.rssi_int3>-45&&Ble_2.rssi_int3!=0){
                        Log.i("jim","C:很靠近了");
                        Ble_2.sendMessage1(sendDATA);
                        Ble_2.Ble_Disconnect2();
                    }
                    break;
            }
        }
    };
  private  void Ble_connect1(){

       Ble_thread1 =new Thread(new Runnable() {
           @Override
           public void run() {
               while(stop_thread){
                   try {
                       Thread.sleep(1000);
                       Log.i("jim","1111");
                       if(Ble_all.BLe_stus ==true){
                           Message ss =new Message();
                           ss.what =1;
                           mHandler.sendMessage(ss);
                       }

                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }

               }
           }
       });
       Ble_thread1.start();
  }
  private  void Ble_connect2(){
      Ble_thread2 =new Thread(new Runnable() {
          @Override
          public void run() {
              while(stop_thread1){
                  try {
                      Thread.sleep(1000);
                      if(Ble_1.BLe_stus1 ==true){
                          Message ss1 =new Message();
                          ss1.what =2;
                          Log.i("jim________","1111");
                          mHandler.sendMessage(ss1);
                      }

                  }catch (Exception e){

                  }
              }
          }
      });
      Ble_thread2.start();
  }
  private  void Ble_connect3() {
      Ble_thread3 =new Thread(new Runnable() {
          @Override
          public void run() {
              while(stop_thread2){
                  try {
                      Thread.sleep(1000);
                      if(Ble_2.BLe_stus2 ==true){
                          Message ss2 =new Message();
                          ss2.what =3;
                          mHandler.sendMessage(ss2);
                      }
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }



          }
      });

      Ble_thread3.start();

  }
}
