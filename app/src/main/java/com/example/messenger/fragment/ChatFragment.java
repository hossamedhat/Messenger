package com.example.messenger.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.SearchActivity;
import com.example.messenger.model.ChatDetails;
import com.example.messenger.model.LastMessage;
import com.example.messenger.model.UsersAccount;
import com.example.messenger.recycleView.ChatItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

   private TextView txt_activity;
   private RecyclerView mRecycle;
   private FirebaseFirestore firestoreInstance=FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private DocumentReference curDocumentReference;
    private EditText editTextSearch;
    private DocumentReference dcReference;
    private String pathImage,nameUser;


    private GroupAdapter groupAdapter=new GroupAdapter<ViewHolder>();
    private ArrayList<LastMessage>lastMessages=new ArrayList<>();
    private UsersAccount usersAccount=new UsersAccount();
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS a", Locale.US);


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat, container, false);

        txt_activity=getActivity().findViewById(R.id.txt_toolbar);
        txt_activity.setText("Chat");
        mRecycle=view.findViewById(R.id.recycle_user);
        editTextSearch=view.findViewById(R.id.search_people);

        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });


        addChatListener();

        return view;
    }

    private void addChatListener() {

        firestoreInstance.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("sharedChat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            groupAdapter.clear();
                            lastMessages.clear();

                                for (DocumentSnapshot documentSnapshot:task.getResult()){
                                    if(documentSnapshot.get("date")!=null){
                                        LastMessage lastMessage=new LastMessage();
                                        String id = documentSnapshot.getId();

                                        if (!id.equals(documentSnapshot.getString("senderId"))){
                                            lastMessage.setImage_path(documentSnapshot.getString("image_path_re"));
                                            lastMessage.setName(documentSnapshot.getString("recipientName"));
                                            lastMessage.setDate(documentSnapshot.get("date").toString());
                                            lastMessage.setRecipient(documentSnapshot.get("recipient").toString());
                                            lastMessage.setChatId(documentSnapshot.get("chatId").toString());
                                            lastMessage.setType(documentSnapshot.get("type").toString());
                                            lastMessage.setText(documentSnapshot.get("text").toString());
                                        }else {
                                            lastMessage.setImage_path(documentSnapshot.getString("image_path_se"));
                                            lastMessage.setName(documentSnapshot.getString("senderName"));
                                            lastMessage.setDate(documentSnapshot.get("date").toString());
                                            lastMessage.setRecipient(documentSnapshot.get("senderId").toString());
                                            lastMessage.setChatId(documentSnapshot.get("chatId").toString());
                                            lastMessage.setType(documentSnapshot.get("type").toString());
                                            lastMessage.setText(documentSnapshot.get("text").toString());
                                        }


                                        lastMessages.add(lastMessage);

                                    }

                                }
                                if(lastMessages.size()>0){
                                    Collections.sort(lastMessages, new Comparator<LastMessage>() {
                                        @Override
                                        public int compare(LastMessage lhs, LastMessage rhs) {
                                            Date date1 = null;
                                            Date date2 = null;
                                            try {
                                                date1 = dateFormat.parse(lhs.getDate().toString());
                                                date2 = dateFormat.parse(rhs.getDate().toString());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            assert date2 != null;
                                            return date2.compareTo(date1);
                                        }
                                    });
                                }

                            for (LastMessage lastMessage:lastMessages){
                                groupAdapter.add(new ChatItem(lastMessage,getActivity()));
                            }
                            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            mRecycle.setLayoutManager(llm);
                            mRecycle.setAdapter(groupAdapter);
                            groupAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(@NonNull Item item, @NonNull View view) {
                                    if (item instanceof ChatItem){
                                        Intent intent=new Intent(getActivity(), ChatActivity.class);
                                        intent.putExtra("user_name",((ChatItem) item).lastMessage.getName());
                                        intent.putExtra("user_img",((ChatItem) item).lastMessage.getImage_path());
                                        intent.putExtra("user_id",((ChatItem) item).lastMessage.getRecipient());
                                        getActivity().startActivity(intent);
                                        item.notifyChanged();
                                    }
                                }
                            });
                        }
                    }
                });
    }


}
