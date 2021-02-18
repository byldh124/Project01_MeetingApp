package com.moondroid.project01_meetingapp.page_tab1_info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moondroid.project01_meetingapp.R;
import com.moondroid.project01_meetingapp.global.G;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class InformationFragment extends Fragment {

    ImageView ivIntroImg, ivInterestIcon;
    TextView tvMessage, tvMeetName;
    RecyclerView recyclerViewJungMo, recyclerViewMembers;
    ArrayList<String> interestList;
    Button btnJoin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_tab1_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //xml Reference
        ivInterestIcon = view.findViewById(R.id.iv_page_modify_interest_icon);
        ivIntroImg = view.findViewById(R.id.iv_page_modify_intro_image);
        tvMessage = view.findViewById(R.id.tv_page_modify_message);
        tvMeetName = view.findViewById(R.id.tv_page_modify_title);
        recyclerViewJungMo = view.findViewById(R.id.recycler_page_moim_information);
        recyclerViewMembers = view.findViewById(R.id.recycler_page_members);
        btnJoin = view.findViewById(R.id.btn_join);



        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setMessage("가입하시겠습니까?").setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        G.itemsRef.child(G.currentItemBase.meetName).child("members").child("member").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    arrayList.add(ds.getValue(String.class));
                                }
                                arrayList.add(G.myProfile.userId);
                                G.itemsRef.child(G.currentItemBase.meetName).child("members").child("member").setValue(arrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "가입됨", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).setNegativeButton("아니요", null).create().show();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();


        if (G.currentItemDetail != null && G.currentItemDetail.introImgUrl != null){
            Glide.with(getContext()).load(G.currentItemDetail.introImgUrl).into(ivIntroImg);
        }

        interestList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_list)));
        int interestNum = interestList.indexOf(G.currentItemBase.interest);
        Glide.with(getContext()).load(getResources().getStringArray(R.array.interest_icon_img_url)[interestNum]).into(ivInterestIcon);
        tvMeetName.setText(G.currentItemBase.meetName);

        if (G.currentItemDetail == null || G.currentItemDetail.message == null) {
            G.currentItemDetail.message = "모임 설명을 작성해 주십시오";
        }
        tvMessage.setText(G.currentItemDetail.message);

    }
}
