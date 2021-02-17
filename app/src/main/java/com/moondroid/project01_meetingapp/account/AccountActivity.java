package com.moondroid.project01_meetingapp.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.moondroid.project01_meetingapp.main.MainActivity;
import com.moondroid.project01_meetingapp.R;
import com.moondroid.project01_meetingapp.global.G;
import com.moondroid.project01_meetingapp.variableobject.UserBaseVO;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    final int REQUEST_CODE_FOR_LOCATION_CHOICE = 0;
    final int REQUEST_CODE_FOR_INTEREST_SELECT = 1;

    Toolbar toolbarAccountActivity;
    TextView tvLocation, tvBirthDate, tvUserInterest;
    EditText etId, etPassword, etPasswordCheck, etName;
    RadioGroup radioGroup;
    String userId, userPassword, userName, userBirthDate, userGender, userAddress, userInterest;

    int y = 0, m = 0, d = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //xml Reference
        tvLocation = findViewById(R.id.tv_location_add_account);
        tvBirthDate = findViewById(R.id.tv_birth_date_add_account);
        tvUserInterest = findViewById(R.id.tv_interest_add_account);
        etId = findViewById(R.id.et_id_add_account);
        etPassword = findViewById(R.id.et_password_add_account);
        etPasswordCheck = findViewById(R.id.et_password_check_add_account);
        etName = findViewById(R.id.et_name_add_account);
        radioGroup = findViewById(R.id.radio_group_account);

        toolbarAccountActivity = findViewById(R.id.toolbar_account_activity);
        setSupportActionBar(toolbarAccountActivity);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userGender = "남자";
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                userGender = radioButton.getText().toString();
            }
        });


    }

    public void saveData() {
        G.myProfile = new UserBaseVO(userId, userPassword, userName, userBirthDate, userGender, userAddress, userInterest, null, null);
        G.usersRef.child(userId).child("base").setValue(G.myProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", userId).commit();
                Toast.makeText(AccountActivity.this, "아이디 올라갔다잉", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickLocationAccount(View view) {
        Intent intent = new Intent(this, LocationChoiceActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FOR_LOCATION_CHOICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_CODE_FOR_LOCATION_CHOICE:
                userAddress = data.getStringExtra("location");
                tvLocation.setText(userAddress);
                break;
            case REQUEST_CODE_FOR_INTEREST_SELECT:
                userInterest = data.getStringExtra("interest");
                tvUserInterest.setText(userInterest);
                break;
        }

    }

    public void clickSave(View view) {

        userId = etId.getText().toString();
        userPassword = etPassword.getText().toString();
        userName = etName.getText().toString();
        userBirthDate = tvBirthDate.getText().toString();

        userAddress = tvLocation.getText().toString();

        if (userId == null || userId.equals("")) {
            Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword == null || userPassword.equals("") || !(userPassword.equals(etPasswordCheck.getText().toString()))) {
            Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userName == null || userName.equals("")) {
            Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userBirthDate == null || userBirthDate.equals("")) {
            Toast.makeText(this, "생년월일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userAddress == null || userAddress.equals("")) {
            Toast.makeText(this, "주소를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userInterest == null || userInterest.equals("")) {
            Toast.makeText(this, "관심사를 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        saveData();

    }

    public void clickBirth(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month + 1;
                d = dayOfMonth;

                tvBirthDate.setText("" + y + "." + m + "." + d);

            }
        }, 1990, 0, 1);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.show();

    }

    public void clickInterest(View view) {
        Intent intent = new Intent(this, InterestActivity.class);
        intent.putExtra("sendClass", "Account");
        startActivityForResult(intent, REQUEST_CODE_FOR_INTEREST_SELECT);
    }

    public void clickAccountCheck(View view) {
//        ArrayList<String> names = new ArrayList<>();
//        G.usersRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()){
//                    Toast.makeText(AccountActivity.this, ""+ds.getKey(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(AccountActivity.this, ""+etName.getText().toString(), Toast.LENGTH_SHORT).show();
//                    if (ds.getKey().equals(etName.getText().toString())) names.add(ds.getKey());
//                }
//                Toast.makeText(AccountActivity.this, "" + names.size(), Toast.LENGTH_SHORT).show();
//                if (names.isEmpty())
//                    Toast.makeText(AccountActivity.this, "아이디가 존재하지 않습니다,", Toast.LENGTH_SHORT).show();
//                else Toast.makeText(AccountActivity.this, "아이디가 존재 합니다.", Toast.LENGTH_SHORT).show();
//            }
//        });

        G.usersRef.child(etId.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    Toast.makeText(AccountActivity.this, "아이디 존재 안함", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountActivity.this, "아이디 존재 함", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}