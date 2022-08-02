package com.example.messenger.recycleView;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;
import com.example.messenger.glide.GlideApp;
import com.example.messenger.model.ChatDetails;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {


    private ArrayList<ChatDetails> chatDetails=new ArrayList<>();
    private Context context;
    private String messageId;
    //To display Image
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    //To display record
    public static MediaPlayer mediaPlayer = null;
    public static   boolean isPlaying = false;
    public static ChatHolder holderInstance=null;
    public static int positionInstance=0;
    private Handler seekbarHandler;
    private   Runnable updateSeekbar;
    private int lastPosition=0;

    public ArrayList<ChatDetails> getChatDetails() {
        return chatDetails;
    }

    public void setChatDetails(ArrayList<ChatDetails> chatDetails) {
        this.chatDetails = chatDetails;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public ChatAdapter() {
    }

    public ChatAdapter(ArrayList<ChatDetails> chatDetails, Context context, String messageId) {
        this.chatDetails = chatDetails;
        this.context = context;
        this.messageId = messageId;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chat_message,parent,false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holderInstance=holder;
        positionInstance=position;
        if (chatDetails.get(position).getType_text().equals("TEXT")){
            holder.card_img_sender.setVisibility(View.GONE);
            holder.card_img_recipient.setVisibility(View.GONE);
            holder.card_record_sender.setVisibility(View.GONE);
            holder.card_record_recipient.setVisibility(View.GONE);

            if (messageId.equals(chatDetails.get(position).getSenderId())){
                holder.card_txt_sender.setVisibility(View.VISIBLE);
                holder.card_txt_recipient.setVisibility(View.GONE);
                holder.txt_name_sender.setText(chatDetails.get(position).getText());
                holder.txt_date_sender.setText(formateDateFromstring("dd/MM/yyyy hh:mm:ss:SS a","hh:mm a",chatDetails.get(position).getDate()));

            }else{
                holder.card_txt_sender.setVisibility(View.GONE);
                holder.card_txt_recipient.setVisibility(View.VISIBLE);
                holder.txt_name_recipient.setText(chatDetails.get(position).getText());
                holder.txt_date_recipient.setText(formateDateFromstring("dd/MM/yyyy hh:mm:ss:SS a","hh:mm a",chatDetails.get(position).getDate()));

            }



        }else if (chatDetails.get(position).getType_text().equals("IMAGE")){
            holder.card_txt_sender.setVisibility(View.GONE);
            holder.card_txt_recipient.setVisibility(View.GONE);
            holder.card_record_sender.setVisibility(View.GONE);
            holder.card_record_recipient.setVisibility(View.GONE);
            if (messageId.equals(chatDetails.get(position).getSenderId())){
                holder.card_img_sender.setVisibility(View.VISIBLE);
                holder.textImageSenderTime.setVisibility(View.VISIBLE);
                holder.card_img_recipient.setVisibility(View.GONE);
                if (!chatDetails.get(position).getImagePath().isEmpty()){

                    GlideApp.with(context).load(firebaseStorage.getReference(chatDetails.get(position).getImagePath())).placeholder(R.drawable.gallery_img).into(holder.img_sender);
                }else {
                    GlideApp.with(context).load(R.drawable.gallery_img).into(holder.img_sender);
                }
                holder.textImageSenderTime.setText(formateDateFromstring("dd/MM/yyyy hh:mm:ss:SS a","hh:mm a",chatDetails.get(position).getDate()));

            }else{
                holder.card_img_sender.setVisibility(View.GONE);
                holder.card_img_recipient.setVisibility(View.VISIBLE);
                holder.textImageRecipientTime.setVisibility(View.VISIBLE);
                if (!chatDetails.get(position).getImagePath().isEmpty()){
                    GlideApp.with(context).load(firebaseStorage.getReference(chatDetails.get(position).getImagePath())).placeholder(R.drawable.gallery_img).into(holder.img_recipient);
                }else {
                    GlideApp.with(context).load(R.drawable.gallery_img).into(holder.img_recipient);
                }
                holder.textImageRecipientTime.setText(formateDateFromstring("dd/MM/yyyy hh:mm:ss:SS a","hh:mm a",chatDetails.get(position).getDate()));
            }
        }else{

            holder.card_txt_sender.setVisibility(View.GONE);
            holder.card_txt_recipient.setVisibility(View.GONE);
            holder.card_img_sender.setVisibility(View.GONE);
            holder.card_img_recipient.setVisibility(View.GONE);
            if (messageId.equals(chatDetails.get(position).getSenderId())){
                holder.card_record_sender.setVisibility(View.VISIBLE);
                holder.card_record_recipient.setVisibility(View.GONE);
                holder.txt_title_sender.setText(chatDetails.get(position).getAudioName());
                holder.txt_time_sender.setText(formateDateFromstring("dd/MM/yyyy hh:mm:ss:SS a","hh:mm a",chatDetails.get(position).getDate()));
                holder.img_record_sender.setImageDrawable(context.getResources().getDrawable(R.drawable.play, null));
                holder.seekBar_sender.setEnabled(false);
                holder.seekBar_sender.setProgress(0);
                holder.img_record_sender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lastPosition!=position ){ //5 3
                            if (mediaPlayer!=null){
                                stopAudio(holder,position);
                                notifyItemChanged(lastPosition);
                            }
                            lastPosition=position; //5
                            playAudio(holder,position);
                        }else if (lastPosition == position ) {
                            if (mediaPlayer != null){
                                if(isPlaying){
                                    pauseAudio(holder,position);
                                } else {
                                    if(chatDetails.get(position).getAudioUri() != null ){
                                        resumeAudio(holder,position);
                                    }
                                }
                            }else {
                                playAudio(holder,position);
                            }

                        }
                    }
                });
                holder.seekBar_sender.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        pauseAudio(holder, position);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int progress = seekBar.getProgress();
                        if (mediaPlayer!=null){
                            mediaPlayer.seekTo(progress);
                        }
                        resumeAudio(holder, position);
                    }
                });

            }else{
                holder.card_record_recipient.setVisibility(View.VISIBLE);
                holder.card_record_sender.setVisibility(View.GONE);
                holder.txt_title_recipient.setText(chatDetails.get(position).getAudioName());
                holder.txt_time_recipient.setText(formateDateFromstring("dd/MM/yyyy hh:mm:ss:SS a","hh:mm a",chatDetails.get(position).getDate()));
                holder.img_record_recipient.setImageDrawable(context.getResources().getDrawable(R.drawable.play, null));
                holder.seekBar_recipient.setEnabled(false);
                holder.seekBar_recipient.setProgress(0);
                holder.img_record_recipient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lastPosition!=position ){ //5 3
                            if (mediaPlayer!=null){
                                stopAudio(holder,position);
                                notifyItemChanged(lastPosition);
                            }
                            lastPosition=position; //5
                            playAudio(holder,position);
                        }else if (lastPosition == position ) {
                            if (mediaPlayer != null){
                                if(isPlaying){
                                    pauseAudio(holder,position);
                                } else {
                                    if(chatDetails.get(position).getAudioUri() != null ){
                                        resumeAudio(holder,position);
                                    }
                                }
                            }else {
                                playAudio(holder,position);
                            }

                        }
                    }
                });
                holder.seekBar_recipient.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        pauseAudio(holder, position);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int progress = seekBar.getProgress();
                        if (mediaPlayer!=null){
                            mediaPlayer.seekTo(progress);
                        }
                        resumeAudio(holder, position);
                    }
                });

            }
        }

    }

    @Override
    public int getItemCount() {
        return chatDetails.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder{
        //To set Gone or Visibility
        CardView card_txt_sender,card_txt_recipient,card_img_sender,card_img_recipient,card_record_sender,card_record_recipient;
        TextView textImageSenderTime,textImageRecipientTime;

        //To define variables of text
        TextView txt_name_sender,txt_name_recipient,txt_date_sender,txt_date_recipient;

        //To define variables of image
        ImageView img_sender,img_recipient;

        //To define variables of record
        TextView txt_title_sender,txt_title_recipient,txt_time_sender,txt_time_recipient;
        ImageButton img_record_sender,img_record_recipient;
        SeekBar seekBar_sender,seekBar_recipient;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            //To determine variables of visibility
            card_txt_sender=itemView.findViewById(R.id.chat_text_sender);
            card_txt_recipient=itemView.findViewById(R.id.chat_text_recipient);
            card_img_sender=itemView.findViewById(R.id.chat_image_sender);
            card_img_recipient=itemView.findViewById(R.id.chat_image_recipient);
            card_record_sender=itemView.findViewById(R.id.chat_record_sender);
            card_record_recipient=itemView.findViewById(R.id.chat_record_recipient);
            textImageSenderTime=itemView.findViewById(R.id.chatImageTimeSender);
            textImageRecipientTime=itemView.findViewById(R.id.chatImageTimeRecipient);

            //To determine variables of text
            txt_name_sender=itemView.findViewById(R.id.chat_message_sender);
            txt_name_recipient=itemView.findViewById(R.id.chat_recipient_message);
            txt_date_sender=itemView.findViewById(R.id.chat_time_sender);
            txt_date_recipient=itemView.findViewById(R.id.chat_recipient_time);

            //To determine variables of image
            img_sender=itemView.findViewById(R.id.chatSenderImage);
            img_recipient=itemView.findViewById(R.id.chatRecipientImage);

            //To determine variables of record
            txt_title_sender=itemView.findViewById(R.id.chat_record_title_sender);
            txt_title_recipient=itemView.findViewById(R.id.chat_record_title_recipient);
            txt_time_sender=itemView.findViewById(R.id.chat_record_date_sender);
            txt_time_recipient=itemView.findViewById(R.id.chat_record_date_recipient);
            img_record_sender=itemView.findViewById(R.id.chat_record_image_sender);
            img_record_recipient=itemView.findViewById(R.id.chat_record_image_recipient);
            seekBar_sender=itemView.findViewById(R.id.chat_record_player_seekbar_sender);
            seekBar_recipient=itemView.findViewById(R.id.chat_record_player_seekbar_recipient);



        }

    }


    private void pauseAudio(ChatHolder holder, int position) {
        if (mediaPlayer!=null) {
            mediaPlayer.pause();
            if (messageId.equals(chatDetails.get(position).getSenderId())){
                holder.img_record_sender.setImageDrawable(context.getResources().getDrawable(R.drawable.play, null));
            }else {
                holder.img_record_recipient.setImageDrawable(context.getResources().getDrawable(R.drawable.play, null));
            }

            isPlaying = false;
            seekbarHandler.removeCallbacks(updateSeekbar);
        }

    }

    private void resumeAudio(ChatHolder holder, int position) {

        if (mediaPlayer !=null){
            mediaPlayer.start();
            if (messageId.equals(chatDetails.get(position).getSenderId())){
                holder.img_record_sender.setImageDrawable(context.getResources().getDrawable(R.drawable.pause,null));
            }else {
                holder.img_record_recipient.setImageDrawable(context.getResources().getDrawable(R.drawable.pause,null));
            }

            isPlaying = true;

            updateRunnable(holder,position);
            seekbarHandler.postDelayed(updateSeekbar, 0);
        }
    }

    private void playAudio(ChatHolder holder, int position) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(chatDetails.get(position).getAudioUri());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    if (messageId.equals(chatDetails.get(position).getSenderId())){
                        holder.img_record_sender.setImageDrawable(context.getResources().getDrawable(R.drawable.pause, null));
                        holder.seekBar_sender.setEnabled(true);
                    }else {
                        holder.img_record_recipient.setImageDrawable(context.getResources().getDrawable(R.drawable.pause, null));
                        holder.seekBar_recipient.setEnabled(true);
                    }

                    isPlaying = true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                stopAudio(holder,position);
            }
        });

        if (messageId.equals(chatDetails.get(position).getSenderId())){
            holder.seekBar_sender.setMax(mediaPlayer.getDuration());
        }else {
            holder.seekBar_recipient.setMax(mediaPlayer.getDuration());
        }

        seekbarHandler = new Handler();
        updateRunnable(holder,position);
        seekbarHandler.postDelayed(updateSeekbar, 0);


    }

    public void stopAudio(ChatHolder holder,int position) {
        //Stop The Audio
        if (messageId.equals(chatDetails.get(position).getSenderId())){
            holder.img_record_sender.setImageDrawable(context.getResources().getDrawable(R.drawable.play, null));
            holder.seekBar_sender.setEnabled(false);
            holder.seekBar_sender.setProgress(0);
        }else {
            holder.img_record_recipient.setImageDrawable(context.getResources().getDrawable(R.drawable.play, null));
            holder.seekBar_recipient.setEnabled(false);
            holder.seekBar_recipient.setProgress(0);
        }

        isPlaying = false;
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if (seekbarHandler!=null){
            seekbarHandler.removeCallbacks(updateSeekbar);
        }
    }

    private void updateRunnable(ChatHolder holder,int position) {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                if (messageId.equals(chatDetails.get(position).getSenderId())){
                    holder.seekBar_sender.setProgress(mediaPlayer.getCurrentPosition());
                }else {
                    holder.seekBar_recipient.setProgress(mediaPlayer.getCurrentPosition());
                }

                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    public  String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }
}
