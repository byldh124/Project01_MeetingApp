package com.moondroid.project01_meetingapp.page_tab4_chatting;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moondroid.project01_meetingapp.R;
import com.moondroid.project01_meetingapp.global.G;
import com.moondroid.project01_meetingapp.library.RetrofitHelper;
import com.moondroid.project01_meetingapp.library.RetrofitService;
import com.moondroid.project01_meetingapp.variableobject.ChatItemVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingFragment extends Fragment {

    private LinearLayout chatContainer;
    private ListView listView;
    private Button btnSend;
    private EditText etMessage;
    private ArrayList<ChatItemVO> chatItems;
    private TextView tvAccessText;
    private ChatAdapter chatAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatRef;
    private ChatItemVO chatItem;
    private int i = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_tab4_chatting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //xml Reference
        chatContainer = view.findViewById(R.id.chat_container);
        listView = view.findViewById(R.id.list_view_chat);
        btnSend = view.findViewById(R.id.btn_chat_send);
        etMessage = view.findViewById(R.id.et_chat_message);
        tvAccessText = view.findViewById(R.id.chat_none_access_text);

        btnSend.setOnClickListener(onClickListener);
        chatItems = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), chatItems);
        listView.setAdapter(chatAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        chatRef = firebaseDatabase.getReference("chat/" + G.currentItemBase.getMeetName());
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chatItem = snapshot.getValue(ChatItemVO.class);
                try {
                    int pos = G.currentItemMembers.indexOf(chatItem.getUserId());
                    chatItem.setUserName(G.currentChatItems.get(pos).getUserName());
                    chatItem.setProfileImgUrl(G.currentChatItems.get(pos).getProfileImgUrl());

                } catch (Exception e) {

                } finally {
                    chatItems.add(chatItem);
                    chatAdapter.notifyDataSetChanged();
                    listView.setSelection(chatItems.size() - 1);
                }

//                RetrofitHelper.getRetrofitInstanceScalars().create(RetrofitService.class).loadChatInfo(chatItem.getUserId()).enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        if (response.body() != null) {
//                            String[] responses = response.body().split("&&");
//                            chatItem.setUserName(responses[0]);
//                            chatItem.setProfileImgUrl(responses[1]);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                    }
//                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAccess();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ChatItemVO chatItemVO = new ChatItemVO(G.myProfile.getUserId(), G.myProfile.getUserName(), new SimpleDateFormat("MM.dd HH:mm").format(new Date()), G.myProfile.getUserProfileImgUrl(), etMessage.getText().toString());
            chatRef.child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).setValue(chatItemVO).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    etMessage.setText("");
                    RetrofitHelper.getRetrofitInstanceScalars().create(RetrofitService.class).sendFCMMessage(G.currentItemBase.getMeetName(), G.myProfile.getUserId()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(ChattingFragment.this.getActivity(), "Send Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    };

    public void checkAccess(){
        if (!G.currentItemMembers.contains(G.myProfile.getUserId())) {
            //모임에 가입되지 않은 사람은 채팅이 보이지 않도록
            chatContainer.setVisibility(View.INVISIBLE);
            tvAccessText.setVisibility(View.VISIBLE);
        } else {
            chatContainer.setVisibility(View.VISIBLE);
            tvAccessText.setVisibility(View.INVISIBLE);
        }
    }
}
