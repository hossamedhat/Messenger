package com.example.messenger.recycleView;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.messenger.R;
import com.example.messenger.databinding.RecyclerViewItemBinding;
import com.example.messenger.glide.GlideApp;
import com.example.messenger.model.ChatDetails;
import com.example.messenger.model.LastMessage;
import com.example.messenger.model.UsersAccount;
import com.google.firebase.storage.FirebaseStorage;
import com.xwray.groupie.databinding.BindableItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ChatItem extends BindableItem<RecyclerViewItemBinding> {

    public LastMessage lastMessage;
    private Context mContext;
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();


    public ChatItem() {
    }

    public ChatItem(LastMessage lastMessage,  Context mContext) {
        this.lastMessage = lastMessage;
        this.mContext = mContext;
    }



    @Override
    public void bind(@NonNull RecyclerViewItemBinding viewBinding, int position) {
        if (!lastMessage.getImage_path().isEmpty()){
            GlideApp.with(mContext).load(firebaseStorage.getReference(lastMessage.getImage_path())).placeholder(R.drawable.ic_baseline_person_pin_24).into(viewBinding.image);
        }else {
            GlideApp.with(mContext).load(R.drawable.user_img).into(viewBinding.image);
        }
        viewBinding.user.setText(lastMessage.getName());
        viewBinding.date.setText(formateDateFromstring("dd/MM/yyyy hh:mm:ss:SS a","hh:mm a",lastMessage.getDate()));
        viewBinding.last.setText(lastMessage.getText());

    }

    @Override
    public int getLayout() {
        return R.layout.recycler_view_item;
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