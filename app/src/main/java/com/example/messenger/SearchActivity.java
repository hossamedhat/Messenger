package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.messenger.model.UsersAccount;
import com.example.messenger.recycleView.ChatItem;
import com.example.messenger.recycleView.SearchItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.Section;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private  androidx.appcompat.widget.SearchView searchView;
    private RecyclerView recyclerView_search;
    private GroupAdapter groupAdapter=new GroupAdapter<ViewHolder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }
        inti();


    }

    private void inti() {
        toolbar=findViewById(R.id.tool_search_people);
        recyclerView_search=findViewById(R.id.recycle_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.search_people);
        searchView= (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search");



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    return false;
                }

                db.collection("users")
                        .orderBy("name")
                        .startAt(newText.trim())
                        .endAt(newText.trim() + "\uf8ff")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        groupAdapter.clear();
                        for (DocumentSnapshot document:queryDocumentSnapshots.getDocuments()){
                            groupAdapter.add(new SearchItem(document.toObject(UsersAccount.class),document.getId(),SearchActivity.this));
                        }
                    }
                });

                recyclerView_search.setAdapter(groupAdapter);

                groupAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull Item item, @NonNull View view) {
                        if (item instanceof SearchItem){
                            Intent intent=new Intent(SearchActivity.this, ChatActivity.class);
                            intent.putExtra("user_name",((SearchItem) item).usersAccount.getName());
                            intent.putExtra("user_img",((SearchItem) item).usersAccount.getProfileImage());
                            intent.putExtra("user_id",((SearchItem) item).getUid());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            item.notifyChanged();
                        }
                    }
                });

                return false;
            }
        });
        return true;
    }

}