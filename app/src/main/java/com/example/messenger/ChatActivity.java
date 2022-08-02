package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.example.messenger.glide.GlideApp;
import com.example.messenger.model.AudioRecorder;
import com.example.messenger.model.ChatDetails;
import com.example.messenger.model.NotificationData;
import com.example.messenger.model.NotificationSender;
import com.example.messenger.model.UsersAccount;
import com.example.messenger.recycleView.ChatAdapter;
import com.example.messenger.services.APIService;
import com.example.messenger.services.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.ViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class ChatActivity extends AppCompatActivity  {
    private Toolbar toolbar;
    private CircleImageView imageView;
    private TextView txt_name;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private StorageReference ref;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestoreInstance;
    private DocumentReference dcReference,currentUserDocRef;
    private ImageView imageView_back,emo_btn;
    private  String user_name="",user_img="",user_id="",currentUserChatId="",recipientToken="",nameCurrentUser="",recipientName="";
    private EditText edit_message;
    private ImageButton img_send;
    private ChatDetails chatDetails=new ChatDetails();
    private CollectionReference chatChannelsCollect;
    private String mCurrentUser="";
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter=new GroupAdapter<ViewHolder>();
    private ImageButton actionButton,img_social;
    private String pathCurUser=" ";




    private static final int im_code=1;
    private Uri uri=null;
    private UsersAccount currentUser;
    private APIService apiService;
    private boolean flag =false;
    final Handler handler=new Handler();

    //To bring record

    private LinearLayout linearLayout;
    private static  final String AUDIO_RECORDER_FOLDER ="/voices/messenger" ;
    private static String LAST_PATH_OF_RECORD = null;
    private AudioRecorder audioRecorder;
    private File recordFile;
    private RecordView recordView;
    private RecordButton recordButton;
    private String recordName=null;

    //To read Voices


    private ArrayList<ChatDetails> chatDetailsArrayList=new ArrayList<>();
    private ChatAdapter chatAdapter=new ChatAdapter();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }
        inti();
        getUser();
        getUserInfo();
        createChatChannel();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        playRecordVoice();

        dcReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    pathCurUser=documentSnapshot.getString("profileImage");
                }
            }
        });

        firestoreInstance.collection("users")
                .document(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot result = task.getResult();
                            Map<String, Object> data = result.getData();
                            recipientToken = data.get("token").toString();
                            recipientName=data.get("name").toString();
                        }
                    }
                });

        firestoreInstance.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot result = task.getResult();
                            Map<String, Object> data = result.getData();
                            nameCurrentUser = data.get("name").toString();
                        }
                    }
                });


        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });




        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),im_code);
            }
        });



        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=edit_message.getText().toString().trim();
                if (text.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Text is empty...!", Toast.LENGTH_SHORT).show();
                }else {
                    chatDetails.setType_text("TEXT");
                    chatDetails.setAudioName("");
                    chatDetails.setAudioUri("");
                    chatDetails.setImagePath(user_img);
                    chatDetails.setText(text);
                    chatDetails.setSenderId(mCurrentUser);
                    chatDetails.setDate(getDateTime());
                    chatDetails.setRecipient(user_id);
                    chatDetails.setSenderName(currentUser.getName());
                    chatDetails.setRecipientName(recipientName);
                    sentMessage(chatDetails,currentUserChatId,text);
                    edit_message.setText("");
                    sendNotification(recipientToken,nameCurrentUser,text);
                    getMessage(currentUserChatId);
                    createChatChannel();
                }

            }
        });


        edit_message.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (edit_message.getText().toString().length()>0){
                        actionButton.setVisibility(View.GONE);
                        recordButton.setVisibility(View.GONE);
                        img_send.setVisibility(View.VISIBLE);
                    }else{
                        actionButton.setVisibility(View.VISIBLE);
                        recordButton.setVisibility(View.VISIBLE);
                        img_send.setVisibility(View.GONE);
                    }

                }
            });

        EmojiPopup popup=EmojiPopup.Builder
                .fromRootView(findViewById(R.id.root_view))
                .build(edit_message);

        emo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });




    }




    private void inti() {
        emo_btn=findViewById(R.id.emoji_face);
        toolbar=findViewById(R.id.toolbar_chat);
        txt_name=findViewById(R.id.txt_toolbar_chat);
        imageView=findViewById(R.id.img_profile_chat);
        imageView_back=findViewById(R.id.img_back);
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference(mAuth.getCurrentUser().getUid());
        firestoreInstance=FirebaseFirestore.getInstance();
        dcReference=firestoreInstance.document("users/"+mAuth.getCurrentUser().getUid());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatChannelsCollect = firestoreInstance.collection("chats");
        img_send=findViewById(R.id.imageView_send);
        edit_message=findViewById(R.id.message_people);
        recyclerView=findViewById(R.id.rec_chat);
        actionButton=findViewById(R.id.floating_btn_img);
        currentUserDocRef=firestoreInstance.document("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        //Recording
        linearLayout=findViewById(R.id.lay_chat);
        audioRecorder = new AudioRecorder();
        recordView =  findViewById(R.id.record_view);
        recordButton =  findViewById(R.id.record_button);
        img_social=findViewById(R.id.img_social);
    }
    private void getUser(){

        Intent intent=getIntent();
        user_name = intent.getStringExtra("user_name");
        user_img = intent.getStringExtra("user_img");
        user_id=intent.getStringExtra("user_id");
        if (!user_name.isEmpty()){
            txt_name.setText(user_name);
        }
        if (!user_img.isEmpty()){
            GlideApp.with(ChatActivity.this).load(firebaseStorage.getReference(user_img)).placeholder(R.drawable.ic_baseline_person_pin_24).into(imageView);
        }
    }

    private void createChatChannel(){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference newChatChannel = firestoreInstance.collection("users").document();


        firestoreInstance.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("sharedChat")
                .document(user_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            onComplete(documentSnapshot.get("chatId").toString());
                            return;
                        }
                        Map<String, Object> userMap = new HashMap<String, Object>();
                        userMap.put("chatId",newChatChannel.getId());

                        firestoreInstance.collection("users")
                                .document(user_id)
                                .collection("sharedChat")
                                .document(currentUser)
                                .set(userMap);

                        firestoreInstance.collection("users")
                                .document(currentUser)
                                .collection("sharedChat")
                                .document(user_id)
                                .set(userMap);

                        onComplete(newChatChannel.getId());
                    }
                });
    }

    private void onComplete(String idChannel) {
        currentUserChatId=idChannel;
        getMessage(idChannel);
    }

    private void sentMessage(ChatDetails textMessage, String id, String text){
        Map<String, Object> contentMessage = new HashMap<String, Object>();
        contentMessage.put("chatId",id);
        contentMessage.put("text",text);
        contentMessage.put("senderId",textMessage.getSenderId());
        contentMessage.put("recipient",textMessage.getRecipient());
        contentMessage.put("senderName",textMessage.getSenderName());
        contentMessage.put("recipientName",textMessage.getRecipientName());
        contentMessage.put("date",textMessage.getDate());
        contentMessage.put("type",textMessage.getType_text());
        contentMessage.put("image_path_re",user_img);
        contentMessage.put("image_path_se",pathCurUser);



        chatChannelsCollect.document(id).collection("messages").add(textMessage);

        firestoreInstance.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("sharedChat")
                .document(user_id)
                .set(contentMessage);

        firestoreInstance.collection("users")
                .document(user_id)
                .collection("sharedChat")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(contentMessage);

    }


    private void getMessage(String channelId){
        CollectionReference query =  chatChannelsCollect.document(channelId).collection("messages");

        query.orderBy("date",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    chatDetailsArrayList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ChatDetails chatDetails1=new ChatDetails();
                        String type_text = document.getData().get("type_text").toString();
                        if (type_text.equals("TEXT")){
                            chatDetails1.setType_text("TEXT");
                            chatDetails1.setAudioName("");
                            chatDetails1.setAudioUri("");
                            chatDetails1.setImagePath("");
                            chatDetails1.setText(document.getData().get("text").toString());
                            chatDetails1.setDate( document.getData().get("date").toString());
                            chatDetails1.setSenderId(document.getData().get("senderId").toString());
                            chatDetails1.setRecipient(document.getData().get("recipient").toString());
                            chatDetails1.setRecipientName(document.getData().get("recipientName").toString());
                            chatDetails1.setSenderName(document.getData().get("senderName").toString());

                        }else if(type_text.equals("IMAGE")) {
                            chatDetails1.setAudioName("");
                            chatDetails1.setAudioUri("");
                            chatDetails1.setText("");
                            chatDetails1.setType_text("IMAGE");
                            chatDetails1.setImagePath(document.getData().get("imagePath").toString());
                            chatDetails1.setDate(document.getData().get("date").toString());
                            chatDetails1.setSenderId(document.getData().get("senderId").toString());
                            chatDetails1.setRecipient(document.getData().get("recipient").toString());
                            chatDetails1.setRecipientName(document.getData().get("recipientName").toString());
                            chatDetails1.setSenderName(document.getData().get("senderName").toString());

                        }else {
                            chatDetails1.setText("");
                            chatDetails1.setImagePath("");
                            chatDetails1.setType_text("RECORD");
                            chatDetails1.setAudioName((String) document.getData().get("audioName"));
                            chatDetails1.setAudioUri((String) document.getData().get("audioUri"));
                            chatDetails1.setDate(document.getData().get("date").toString());
                            chatDetails1.setSenderId(document.getData().get("senderId").toString());
                            chatDetails1.setRecipient(document.getData().get("recipient").toString());
                            chatDetails1.setRecipientName(document.getData().get("recipientName").toString());
                            chatDetails1.setSenderName(document.getData().get("senderName").toString());
                        }
                        chatDetailsArrayList.add(chatDetails1);
                        }
                    }
                    chatAdapter.setChatDetails(chatDetailsArrayList);
                    chatAdapter.setContext(ChatActivity.this);
                    chatAdapter.setMessageId(mCurrentUser);
                    recyclerView.setAdapter(chatAdapter);
                }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Error: "+e.getMessage().toString() , Toast.LENGTH_SHORT).show();
            }
        });

        flag=true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==im_code && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytesStream);
                byte[] bytesOutImg = bytesStream.toByteArray();
                uploadImage(bytesOutImg);
                Map<String, Object> contentMessage = new HashMap<String, Object>();

                chatDetails.setAudioName("");
                chatDetails.setAudioUri("");
                chatDetails.setText("");
                chatDetails.setType_text("IMAGE");
                chatDetails.setImagePath(ref.getPath());
                chatDetails.setDate(getDateTime());
                chatDetails.setSenderId(mCurrentUser);
                chatDetails.setRecipient(user_id);
                chatDetails.setSenderName(currentUser.getName());
                chatDetails.setRecipientName(recipientName);

                contentMessage.put("chatId",currentUserChatId);
                contentMessage.put("text","Photo");
                contentMessage.put("senderId",mCurrentUser);
                contentMessage.put("recipient",user_id);
                contentMessage.put("senderName",currentUser.getName());
                contentMessage.put("recipientName",recipientName);
                contentMessage.put("date",getDateTime());
                contentMessage.put("type","IMAGE");
                contentMessage.put("image_path_re",user_img);
                contentMessage.put("image_path_se",pathCurUser);

                chatChannelsCollect.document(currentUserChatId).collection("messages").add(chatDetails);

                firestoreInstance.collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("sharedChat")
                        .document(user_id)
                        .set(contentMessage);

                firestoreInstance.collection("users")
                        .document(user_id)
                        .collection("sharedChat")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(contentMessage);

                getMessage(currentUserChatId);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error:"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }


        }
    }

    private void uploadImage(byte[] bytesOutImg) {
        ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/images/" + UUID.nameUUIDFromBytes(bytesOutImg));

                ref.putBytes(bytesOutImg)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                        }else {
                            Toast.makeText(ChatActivity.this, "Loading not done", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Image not loading error : "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo(){
        currentUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser=documentSnapshot.toObject(UsersAccount.class);
            }
        });
    }

    private void sendNotification(String recipientToken,String title,String message){

        NotificationData data=new NotificationData(title,message);
        NotificationSender sender=new NotificationSender(data,recipientToken,getApplicationContext(),ChatActivity.this);
        sender.SendNotifications();

    }


    private void playRecordVoice(){


        //IMPORTANT
        recordButton.setRecordView(recordView);

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);


        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);


        recordView.setSlideToCancelText("Slide To Cancel");


        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                linearLayout.setVisibility(View.GONE);
                if (ChatAdapter.mediaPlayer!=null){
                    chatAdapter.stopAudio(ChatAdapter.holderInstance,ChatAdapter.positionInstance);
                    chatAdapter.notifyDataSetChanged();
                }


                recordFile = new File(getFilename());
                try {
                    audioRecorder.start(recordFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {

                linearLayout.setVisibility(View.VISIBLE);

                stopRecording(true);



                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                uploadAudio(recordFile);
                getMessage(currentUserChatId);
                linearLayout.setVisibility(View.VISIBLE);
                stopRecording(false);

            }

            @Override
            public void onLessThanSecond() {
                linearLayout.setVisibility(View.VISIBLE);

                stopRecording(true);

                Toast.makeText(ChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

        recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                boolean recordPermissionAvailable =
                        ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }


                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);

                return false;

            }
        });
    }

    private void uploadAudio(File recordFile) {
        Uri uri=Uri.fromFile(new File(recordFile.getPath()));
        UUID audioName=UUID.randomUUID();
        ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/audios/" + audioName);
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    saveAudioUri(audioName);
                }
            }
        });
    }

    private void saveAudioUri(UUID audioName) {

        storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/audios/" + audioName)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String, Object> contentMessage = new HashMap<String, Object>();

                        chatDetails.setText("");
                        chatDetails.setImagePath("");
                        chatDetails.setType_text("RECORD");
                        chatDetails.setAudioUri(uri.toString());
                        chatDetails.setAudioName(recordName);
                        chatDetails.setDate(getDateTime());
                        chatDetails.setSenderId(mCurrentUser);
                        chatDetails.setRecipient(user_id);
                        chatDetails.setSenderName(currentUser.getName());
                        chatDetails.setRecipientName(recipientName);


                        contentMessage.put("chatId",currentUserChatId);
                        contentMessage.put("text","Record");
                        contentMessage.put("senderId",mCurrentUser);
                        contentMessage.put("recipient",user_id);
                        contentMessage.put("senderName",currentUser.getName());
                        contentMessage.put("recipientName",recipientName);
                        contentMessage.put("date",getDateTime());
                        contentMessage.put("type","RECORD");
                        contentMessage.put("image_path_re",user_img);
                        contentMessage.put("image_path_se",pathCurUser);

                        chatChannelsCollect.document(currentUserChatId).collection("messages").add(chatDetails);

                        firestoreInstance.collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("sharedChat")
                                .document(user_id)
                                .set(contentMessage);

                        firestoreInstance.collection("users")
                                .document(user_id)
                                .collection("sharedChat")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(contentMessage);
                    }
                });

        getMessage(currentUserChatId);

    }

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }

    public String getFilename(){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(),
                AUDIO_RECORDER_FOLDER);
        Log.e("file : ", file.getAbsolutePath());
        if(!file.exists()){
            file.mkdirs();
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_ss", Locale.CANADA);
        Date now = new Date();
        recordName = "Record_"+formatter.format(now)+ ".3gp";
        return (file.getAbsolutePath() + "/" + recordName);
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ChatActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (ChatAdapter.mediaPlayer!=null)
        {
            chatAdapter.stopAudio(ChatAdapter.holderInstance,ChatAdapter.positionInstance);
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }


}