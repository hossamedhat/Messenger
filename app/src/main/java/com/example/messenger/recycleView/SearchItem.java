package com.example.messenger.recycleView;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.messenger.R;
import com.example.messenger.databinding.SearchItemBinding;
import com.example.messenger.glide.GlideApp;
import com.example.messenger.model.UsersAccount;
import com.google.firebase.storage.FirebaseStorage;
import com.xwray.groupie.Item;
import com.xwray.groupie.databinding.BindableItem;

import java.util.Objects;

public class SearchItem extends BindableItem<SearchItemBinding> {
    public UsersAccount usersAccount;
    private Context context;
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private String uid;


    public SearchItem(UsersAccount usersAccount,String id ,Context context) {
        this.usersAccount = usersAccount;
        this.context = context;
        uid=id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public void bind(@NonNull SearchItemBinding viewBinding, int position) {
        if (!usersAccount.getProfileImage().isEmpty()){
            GlideApp.with(context).load(firebaseStorage.getReference(usersAccount.getProfileImage())).placeholder(R.drawable.ic_baseline_person_pin_24).into(viewBinding.imageUserSearch);
        }else {
            GlideApp.with(context).load(R.drawable.user_img).into(viewBinding.imageUserSearch);
        }
        viewBinding.nameUserSearch.setText(usersAccount.getName());
    }

    @Override
    public boolean isSameAs(Item other) {
        if (other instanceof SearchItem)
            return false;

        return true;
    }



    @Override
    public int hashCode() {
        int i = usersAccount.hashCode();
        i=31*i+context.hashCode();
        return i;
    }

    @Override
    public boolean equals(Object o)
    {
        return isSameAs((SearchItem) o);
    }

    @Override
    public int getLayout() {
        return R.layout.search_item;
    }
}
