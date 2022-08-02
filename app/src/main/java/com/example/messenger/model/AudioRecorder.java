package com.example.messenger.model;

import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;





    public void start(String filePath) throws IOException {

        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        mediaRecorder.setOutputFile(filePath);
        try {
            mediaRecorder.prepare();
            //Start Recording
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
