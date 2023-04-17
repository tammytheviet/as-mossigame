package ch.wiss.mossigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements Runnable{

    Handler handy;
    final static int DELAY = 1000;
    FrameLayout spielfeld;
    Random zufallsgenerator;
    float massstab;
    int punkte;
    TextView lblPunkte;
    TextView lblRound;
    TextView lblTimeRemaining;
    TextView lblMossisRemaining;
    int anzahlMossis;
    private int goalMossis;
    private int round;
    int LEVEL_TIME = 20;
    private Date tStart;
    private long timeRemaining;
    int mossisLeft;
    TextView lblGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        spielfeld = findViewById(R.id.spielfeld);
        zufallsgenerator = new Random();
        massstab = getResources().getDisplayMetrics().density;

        lblPunkte = findViewById(R.id.textPoints);
        lblRound = findViewById(R.id.textRound);
        lblGameOver = findViewById(R.id.textGameOver);
        lblGameOver.setVisibility(View.INVISIBLE);
        lblTimeRemaining = findViewById(R.id.textTimeRemaining);
        lblMossisRemaining = findViewById(R.id.textMossisRemaining);

        startLevel();

        handy = new Handler();
        handy.postDelayed(this,DELAY);
    }

    @Override
    public void run(){
        decrementTime();
        //handy.postDelayed(this,DELAY);
    }

    // Zeit wird heruntergezählt
    public void decrementTime(){
        timeRemaining = LEVEL_TIME - (new Date().getTime() - tStart.getTime())/1000;
        Log.d("GAME", String.valueOf(timeRemaining));
        Log.d("GAME", String.valueOf(gameFinished()));

        if(!gameFinished()){
            if(!levelFinished()){
                createMossi(spielfeld);
                lblTimeRemaining.setText("" + timeRemaining);
                mossisLeft = goalMossis - anzahlMossis;
                lblMossisRemaining.setText("" + mossisLeft);
            } else {
                startLevel();
            }
            handy.postDelayed(this,DELAY);
        } else {
            showGameOver(spielfeld);
        }
    }

    // ein neuer Mossi wird erstellt
    public void createMossi(View v){

        ImageView mossi = new ImageView(this);
        mossi.setOnClickListener(view -> removeMossi(view));

        mossi.setBackgroundResource(R.drawable.mosquito);
        int breite = spielfeld.getWidth();
        int hoehe = spielfeld.getHeight();
        Log.d("Game", hoehe+ ", " + breite);
        int muecke_breite = Math.round(massstab + 100);
        int muecke_hoehe = Math.round(massstab + 84);
        int links = zufallsgenerator.nextInt(breite - muecke_breite);
        int oben = zufallsgenerator.nextInt(hoehe - muecke_hoehe);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(muecke_breite, muecke_hoehe);
        params.leftMargin = links;
        params.topMargin = oben;
        params.gravity = Gravity.TOP+Gravity.LEFT;
        spielfeld.addView(mossi, params);
    }

    // Mossi wird entfernt und Punktestand wird erhöht
    private void removeMossi(View v) {
        punkte++;
        anzahlMossis++;
        spielfeld.removeView(v);
        lblPunkte.setText("Punkte: "+ punkte *10);

    }

    // neues Level wird gestartet
    private void startLevel(){
        round++;
        lblRound.setText("Runde: "+ round);
        anzahlMossis = 0;
        goalMossis = round * 10;
        LEVEL_TIME = round * 10 + 10;
        tStart = new Date();
    }

    // es wird gecheckt ob man verloren hat
    private boolean gameFinished(){
        return timeRemaining <= 0 && anzahlMossis < goalMossis;
    }

    // es wird gecheckt ob man das Level erfolgreich abgeschlossen hat
    private boolean levelFinished(){
        return anzahlMossis >= goalMossis;
    }

    // Der Game-Over Screen wird gezeigt und alle Mossis werden gelöscht
    public void showGameOver(View v){
        lblGameOver.setVisibility(View.VISIBLE);
        int anzahlElemente = spielfeld.getChildCount();
        for (int i = 0; i < anzahlElemente; i++){
            spielfeld.getChildAt(i).setOnClickListener(null);
        }

        handy.postDelayed(() -> {
            setResult(punkte * 10);
            finish();
        }, 3000);


    }

}