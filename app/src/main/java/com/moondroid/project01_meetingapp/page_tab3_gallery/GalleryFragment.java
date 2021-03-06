package com.moondroid.project01_meetingapp.page_tab3_gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moondroid.project01_meetingapp.R;
import com.moondroid.project01_meetingapp.global.G;
import com.moondroid.project01_meetingapp.library.GridLayoutManagerWrapper;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<String> imgs;
    private GalleryAdapter adapter;
    private FloatingActionButton actionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_tab3_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_gallery);
        recyclerView.setLayoutManager(new GridLayoutManagerWrapper(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        imgs = new ArrayList<>();
        adapter = new GalleryAdapter(getContext(), imgs);
        recyclerView.setAdapter(adapter);

        actionButton = view.findViewById(R.id.action_button_gallery);

        if (G.currentItemMembers.indexOf(G.myProfile.getUserId()) < 0) {
            actionButton.setVisibility(View.GONE);
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChoicePictureActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadImg();
    }

    public void loadImg() {
        imgs.clear();
        adapter.notifyDataSetChanged();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("GalleryImgs/").child(G.currentItemBase.getMeetName());
        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    imgs.add(ds.getValue(String.class));
                    adapter.notifyItemInserted(imgs.size() - 1);
                }
            }
        });
    }
}
