package ch.wiss.mossigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements Runnable{

    final static int GAME_ACTIVITY = 42;
    Handler handy;
    final static int DELAY = 1000;
    int lastScore = 0;
    int highscore;
    TextView textHighscore;
    private LinearLayout nameInputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textHighscore = findViewById(R.id.textHighscore);
        handy = new Handler();
        handy.postDelayed(this,DELAY);

        nameInputDialog = findViewById(R.id.nameInputDialog);
    }

    public void onClickButton(View v){
        Intent indy = new Intent(this, GameActivity.class);
        startActivityForResult(indy, GAME_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestId, int result, Intent i) {
        super.onActivityResult(requestId, result, i);

        Log.d("RESULT ID", String.valueOf(requestId));
        Log.d("RESULT", String.valueOf(result));
        //writeScore("fabo", result);

        if(result > lastScore){
            textHighscore.setText("" + result);
            highscore = result;
            nameInputDialog.setVisibility(View.VISIBLE);
        }
        lastScore = result;

    }

    public void speichernButtonClicked(View v){
        TextView editTextName = findViewById(R.id.lblEditTextName);
        String name = editTextName.getText().toString();
        writeScore(name, highscore);
        nameInputDialog.setVisibility(View.INVISIBLE);
    }

    private void writeScore(String name, int score){
        (new Thread(() -> {
            try{
                URL url = new URL("https://green-orca.com/wiss/mossigame/logscore.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                BufferedOutputStream buffy = new BufferedOutputStream(
                        conn.getOutputStream()
                );
                String params = "name="+ URLEncoder.encode(name, "UTF-8")
                        + "&score=" + score;
                buffy.write(params.getBytes(StandardCharsets.UTF_8));
                buffy.flush();
                buffy.close();

                InputStreamReader easy = new InputStreamReader(conn.getInputStream());
                BufferedReader bReader = new BufferedReader(easy);
                while(easy.ready()){
                    String line = bReader.readLine();
                    Log.d("MAIN", "EASY: "+line);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        })).start();
    }

    @Override
    public void run(){
        Calendar cal = Calendar.getInstance();
        handy.postDelayed(this,DELAY);
    }

    public void closeActivity(View v){
        finish();
    }
}