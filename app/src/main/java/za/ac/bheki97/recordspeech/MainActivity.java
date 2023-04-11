package za.ac.bheki97.recordspeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
import java.util.UUID;

import za.ac.bheki97.recordspeech.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String transLang;
    private String originLang;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean audioRecordingPermissionGranted;
    private MediaRecorder mediaRecorder;
    private Handler mainHandler;
    private boolean isRecording;
    private String recordedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainHandler = new Handler();
        isRecording = false;
        mediaRecorder = new MediaRecorder();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        binding.translationSelectLang.setAdapter(adapter);
        binding.originSelectLang.setAdapter(adapter);

        //set Onclick Listener for the Dropdown
        setOnClickListenerForTranslationSelectLang();

        setOnclickListenerForOriginSelectLang();


        //set Onclick Listener for the Record Button
        recordAudio();


    }

    private void recordAudio() {
        binding.recordBtn.setOnClickListener(view ->{
            try{
                if(audioRecordingPermissionGranted){
                    startAudioRecording();
                }else{
                    stopAudioRecording();
                    convertSpeech();
                }



            }catch(IOException ex){
                ex.printStackTrace();
            }
        });
    }

    private void convertSpeech() {


    }

    private void setOnClickListenerForTranslationSelectLang() {
        binding.translationSelectLang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                transLang = adapterView.getItemAtPosition(position).toString();

            }
        });
    }
    private void setOnclickListenerForOriginSelectLang(){
        binding.originSelectLang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                originLang = adapterView.getItemAtPosition(position).toString();

            }
        });
    }

    private void startAudioRecording() throws IOException {
        toggleRecording();
        String uuid = UUID.randomUUID().toString();
        recordedFileName = getFilesDir().getPath() + "/" + uuid + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(recordedFileName);
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    private void stopAudioRecording(){
        toggleRecording();
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();

    }

    private void toggleRecording() {
        isRecording = !isRecording;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    binding.recordBtn.setImageIcon(Icon.createWithResource(MainActivity.this,R.drawable.ic_stop));
                } else {
                    binding.recordBtn.setImageIcon(Icon.createWithResource(MainActivity.this,R.drawable.ic_microphone));
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                audioRecordingPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }

        if (!audioRecordingPermissionGranted) {
            finish();
        }
    }


}