package c.aman.aimusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String[] itemsAll ;
    private ListView mSongsList;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSongsList = (ListView)findViewById(R.id.songsList);

        addExternalStoragePermission();

requestAudioPermissions();


    }




    //Requesting run-time permissions

    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback


    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }

    }







    public void addExternalStoragePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displayAudioSongsName();
                    }

                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {

                    }

                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();
    }







   public ArrayList<File> readonlyaudiosongs(File file)            //add songs to array list
   {
       ArrayList<File> arrayList = new ArrayList<>();

       File[] allFiles = file.listFiles();

       for(File individualFile : allFiles)
       {
           if(individualFile.isDirectory() && !individualFile.isHidden())
           {
               arrayList.addAll(readonlyaudiosongs(individualFile));
           }
           else
           {
               if(individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".abc") ||
                       individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma")
                       )
               {
                   arrayList.add(individualFile);
               }
           }
       }

       return arrayList;

   }

   private void displayAudioSongsName()
   {
       final ArrayList<File> audioSongs = readonlyaudiosongs(Environment.getExternalStorageDirectory());
       itemsAll = new String[audioSongs.size()];
       for(int songCounter = 0 ; songCounter < audioSongs.size() ; songCounter ++)
       {
           itemsAll[songCounter] = audioSongs.get(songCounter).getName();
       }

       ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this ,
               android.R.layout.simple_list_item_1,itemsAll);
       mSongsList.setAdapter(arrayAdapter);

       mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id)
           {
               String songName = mSongsList.getItemAtPosition(position).toString();
               Intent intent = new Intent(MainActivity.this , SmartPlayActivity.class);
               intent.putExtra("song" , audioSongs);
               intent.putExtra("name" , songName);
               intent.putExtra("position" , position);
               startActivity(intent);

           }
       });

   }

}
