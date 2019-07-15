package c.aman.aimusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent ;
    private String keeper = "";

    private ImageView PausePlayBtn , NextBtn , PreviousBtn ;
    private TextView SongNameText ;

    private ImageView imageView;
    private RelativeLayout LowerRelativeLayout ;
    private Button VoiceEnabledButton;

    private String mode = "ON";

    private static MediaPlayer myMediaPlayer ;
    private int position ;
    private ArrayList<File> mySongs ;
    private String mSongsName ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_play);

        checkVoiceCommandPermission();



        PausePlayBtn = (ImageView)findViewById(R.id.play_pause_btn);
        NextBtn = (ImageView)findViewById(R.id.next_btn);
        PreviousBtn = (ImageView)findViewById(R.id.previous_btn);
        imageView = (ImageView)findViewById(R.id.logo);

        SongNameText = (TextView)findViewById(R.id.songName);

        LowerRelativeLayout = (RelativeLayout)findViewById(R.id.lowerRelativeLaout);
        VoiceEnabledButton = (Button)findViewById(R.id.voice_enable_btn);



        parentRelativeLayout = (RelativeLayout)findViewById(R.id.parent_relative_layout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE , Locale.getDefault());

        validateReceiveValuesAndStartPlaying();

        imageView.setBackgroundResource(R.drawable.logo);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results)
            {
                ArrayList<String> MatchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(MatchesFound!=null)
                {
                    if(mode.equals("ON"))
                    {

                        keeper = MatchesFound.get(0);

                        if(keeper.equals("pause the song") || keeper.equals("pause"))
                        {
                            playPauseSong();
                            Toast.makeText(getApplicationContext(),"Command = " + keeper , Toast.LENGTH_LONG).show();
                        }
                        else if(keeper.equals("play the song") || keeper.equals("play"))
                        {
                            playPauseSong();
                            Toast.makeText(getApplicationContext() , "Command = " + keeper , Toast.LENGTH_LONG).show();
                        }

                        else if(keeper.equals("play next song") || keeper.equals("next song"))
                        {
                         playNextSong();
                         Toast.makeText(getApplicationContext() , "Command = " + keeper , Toast.LENGTH_LONG).show();
                        }

                        else if(keeper.equals("play previous song") || keeper.equals("previous song"))
                        {
                            playPreviousSong();
                            Toast.makeText(getApplicationContext() , "Command = " + keeper , Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN :
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                      break;

                      case MotionEvent.ACTION_UP:
                          speechRecognizer.stopListening();
                          break;
                }

                return false;
            }
        });

        VoiceEnabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mode.equals("ON"))
                {
                    mode = "OFF";
                    VoiceEnabledButton.setText("Voice Enabled Mode - OFF");
                    LowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode = "ON";
                    VoiceEnabledButton.setText("Voice Enabled Mode - ON");
                    LowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });


        PausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                playPauseSong();
            }
        });



        PreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(myMediaPlayer.getCurrentPosition()>0)
                {
                 playPreviousSong();
                }
            }
        });


        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(myMediaPlayer.getCurrentPosition()>0)
                {
                    playNextSong();
                }
            }
        });






    }


    private void validateReceiveValuesAndStartPlaying()
    {
        if(myMediaPlayer!=null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList)bundle.getParcelableArrayList("song");
        mSongsName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name") ;

        SongNameText.setText(songName);
        SongNameText.setSelected(true);

        position = bundle.getInt("position" , 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(SmartPlayActivity.this , uri);
        myMediaPlayer.start();

    }


    private void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M)     //Android verion check
        {
            if(!(ContextCompat.checkSelfPermission(SmartPlayActivity.this , Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS , Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

private void playPauseSong()
{
    imageView.setBackgroundResource(R.drawable.four);

    if(myMediaPlayer.isPlaying())
    {
        PausePlayBtn.setImageResource(R.drawable.play);
        myMediaPlayer.pause();
    }
    else
    {
        PausePlayBtn.setImageResource(R.drawable.pause);
        myMediaPlayer.start();
        imageView.setImageResource(R.drawable.five);
    }
}

private void playNextSong()
{
    myMediaPlayer.pause();
    myMediaPlayer.stop();
    myMediaPlayer.release();

    position = ((position + 1) %mySongs.size());
    Uri uri = Uri.parse(mySongs.get(position).toString());

    myMediaPlayer = MediaPlayer.create(SmartPlayActivity.this , uri);
    mSongsName = mySongs.get(position).toString();
    SongNameText.setText(mSongsName);
    myMediaPlayer.start();
    imageView.setBackgroundResource(R.drawable.three);


    if(myMediaPlayer.isPlaying())
    {
        PausePlayBtn.setImageResource(R.drawable.pause);
    }
    else
    {
        PausePlayBtn.setImageResource(R.drawable.play);
        imageView.setImageResource(R.drawable.five);
    }


}

private void playPreviousSong()
{
    myMediaPlayer.pause();
    myMediaPlayer.stop();
    myMediaPlayer.release();

    position = ((position-1)<0 ? (mySongs.size()-1)  :  (position-1));

    Uri uri = Uri.parse(mySongs.get(position).toString());

    myMediaPlayer = MediaPlayer.create(SmartPlayActivity.this , uri);
    mSongsName = mySongs.get(position).toString();
    SongNameText.setText(mSongsName);
    myMediaPlayer.start();
    imageView.setBackgroundResource(R.drawable.two);


    if(myMediaPlayer.isPlaying())
    {
        PausePlayBtn.setImageResource(R.drawable.pause);
    }
    else
    {
        PausePlayBtn.setImageResource(R.drawable.play);
        imageView.setImageResource(R.drawable.five);
    }


}

public void privacyPolicy(View view)
{
    Intent intent = new Intent(SmartPlayActivity.this , PrivacyPolicyActivity.class);
    startActivity(intent);
}


}
