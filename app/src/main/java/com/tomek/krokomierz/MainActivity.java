package com.tomek.krokomierz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private SensorManager mySensorManager; //menedzer czujnikow
    private Sensor myAccelerometer; //obiekt klasy Sensor (czyli czujnik)
    private PowerManager powerManager;
    private PowerManager.WakeLock mWakeLock;
    private Button buttonStart, buttonStop, buttonSave, buttonReset;
    private double NS2S = 0.000000001; //do konwersji nanosekund do sekund
    private boolean pomiar;
    private int licznik;
    private double startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sensor
        SensorManager mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener((SensorEventListener) this, myAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        //umozliwienie dzialania programu pomimo zgaszonego ekranu
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Tag_1");

        //zapis przycisków
        buttonStart = (Button) findViewById(R.id.btnStart);
        buttonStop = (Button) findViewById(R.id.btnStop);
        buttonSave = (Button) findViewById(R.id.btnSave);
        buttonReset = (Button) findViewById(R.id.btnReset);

        //widok przyciskow na aktywnosci
        buttonStop.setVisibility(View.GONE);
        buttonSave.setVisibility(View.GONE);
        buttonReset.setVisibility(View.GONE);
        buttonStart.setVisibility(View.VISIBLE);

        //przygotowanie do zachowania funkcjonalnosci aplikacji po obrocie urzadzenia
        if (savedInstanceState != null) {
            Point point = new Point(savedInstanceState.getFloat("aX"), savedInstanceState.getFloat("aY"), savedInstanceState.getFloat("aZ"), savedInstanceState.getFloat("aT"));
            licznik = savedInstanceState.getInt("licznik");
            pomiar = savedInstanceState.getBoolean("pomiar");
            marker = savedInstanceState.getBoolean("marker");
            string = savedInstanceState.getString("string");
            startTime = savedInstanceState.getDouble("startTime");
        }

        if (marker) {
            buttonStart.setVisibility(View.GONE);
            buttonStop.setVisibility(View.VISIBLE);
            buttonSave.setVisibility(View.GONE);
            buttonReset.setVisibility(View.GONE);
            mWakeLock.acquire();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { //gdy czujnik wykryje zmiane


        if (pomiar) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //znalezienie akcelerometru

                //if do ustalenia prawidlowego czasu - zapis pierwszego odczytu i od kolejnych odejmowanie jego wartosci - czas startuje dzieki temu od zera
                if (licznik == 0) {
                    startTime = sensorEvent.timestamp;
                }

                Point point = new Point(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2], ((sensorEvent.timestamp - startTime) * NS2S));  ; //skladowa X,Y,Z przyspieszenia plus czas dla danego pkt

                poleAx = (TextView) findViewById(R.id.poleAx);
                poleAy = (TextView) findViewById(R.id.poleAy);
                poleAz = (TextView) findViewById(R.id.poleAz);
                poleAt = (TextView) findViewById(R.id.poleAt);
                poleLK = (TextView) findViewById(R.id.poleLK);


                //wyswietlanie sformatowanych danych pomiarowych
                poleAx.setText(String.valueOf(numberFormat.format(aX)));
                poleAy.setText(String.valueOf(numberFormat.format(aY)));
                poleAz.setText(String.valueOf(numberFormat.format(aZ)));
                poleAt.setText(String.valueOf(numberFormat.format(aT)));

                //kolejne wiersze z parametrami - do zapisu w txt
//            string = string + numberFormat.format(aT) + "\t" + numberFormat2.format(aX) + "\t" + numberFormat2.format(aY) + "\t" + numberFormat2.format(aZ) + "\n";

                // dodanie zmiennych do serii
                xTSeria.add(aT, aX);
                yTSeria.add(aT, aY);
                zTSeria.add(aT, aZ);


                licznik++;

                // umozliwienie wyswietlania wykresu "na biezaco"
                mrenderer.setXAxisMin(xTSeria.getMaxX() - 2);
                mrenderer.setXAxisMax(xTSeria.getMaxX());
                chartView.repaint();

// ALGORYTM POMIARU LICZBY KROKOW:
                updateAccelParameters(aX, aY, aZ);

                if ((!shakeInitiated) && isAccelerationChanged()) {
                    shakeInitiated = true;
                } else if ((shakeInitiated) && isAccelerationChanged()) {
                    executeShakeAction();

                } else if ((shakeInitiated) && (!isAccelerationChanged())) {
                    shakeInitiated = false;
                }


            }

        }

    }
}