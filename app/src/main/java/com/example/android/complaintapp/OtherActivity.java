package com.example.android.complaintapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.viewmodel.RequestCodes;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class OtherActivity extends AppCompatActivity {

    private ImageButton mGalleryBtn;
    private ImageButton mCameraBtn;
    private EditText mBhawanName;
    private EditText mComplainDes;
    private Button mSubmitBtn;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private static final int RC_PHOTO_PICKER = 2;
    private static final int CAMERA_REQUEST_CODE= 1;

    private Uri imageUri;
    private Task<Uri> downloadUrl;
    private Uri mImageUri =null;
    private int upload_count=0;

    private ProgressDialog mProgress;

    ArrayList<Uri> imageList = new ArrayList<Uri>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        mGalleryBtn =findViewById(R.id.imageBtn);
        mCameraBtn =findViewById(R.id.cameraBtn);
        mBhawanName = findViewById(R.id.bhawanName);
        mComplainDes =findViewById(R.id.complainDes);
        mSubmitBtn =findViewById(R.id.submitBtn);

        mStorage = FirebaseStorage.getInstance().getReference().child("photos");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("other_complain");

        mProgress=new ProgressDialog(this);

        mGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST_CODE);
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting(){
        mProgress.setMessage("submitting complain");
        mProgress.show();

        String bhawanName= mBhawanName.getText().toString().trim();
        String complainDes= mComplainDes.getText().toString().trim();

        if(!TextUtils.isEmpty(bhawanName) && !TextUtils.isEmpty(complainDes) && mImageUri!=null){
                DatabaseReference otherPost =mDatabase.push();
                otherPost.child("BhawanName").setValue(bhawanName);
                otherPost.child("ComplainDes").setValue(complainDes);
                otherPost.child("image").setValue(downloadUrl.toString());

            mProgress.dismiss();
            Toast.makeText(OtherActivity.this,"complain submitted",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==RC_PHOTO_PICKER && resultCode == RESULT_OK){
           if(data.getClipData()!=null){
               mProgress.setMessage("selecting image");
               mProgress.show();
               int countClipData = data.getClipData().getItemCount();
               int currentImageSelect=0;

               while(currentImageSelect<countClipData){
                   imageUri =data.getClipData().getItemAt(currentImageSelect).getUri();
                   imageList.add(imageUri);
                   currentImageSelect = currentImageSelect+1;
               }

               for(upload_count=0;upload_count<imageList.size();upload_count++){
                   mImageUri =imageList.get(upload_count);
                   StorageReference imageName=mStorage.child(mImageUri.getLastPathSegment());
                   imageName.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           downloadUrl =taskSnapshot.getStorage().getDownloadUrl();
                           mProgress.dismiss();
                           Toast.makeText(OtherActivity.this,"image selected",Toast.LENGTH_LONG).show();

                       }
                   });
               }

           }if(data.getClipData()==null){
                mProgress.setMessage("selecting image");
                mProgress.show();

               mImageUri = data.getData();


                // Get a reference to store file at chat_photos/<FILENAME>
                final StorageReference photoRef = mStorage.child(mImageUri.getLastPathSegment());
                photoRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl =taskSnapshot.getStorage().getDownloadUrl();
                        mProgress.dismiss();
                        Toast.makeText(OtherActivity.this,"image selected",Toast.LENGTH_LONG).show();

                    }
                });
            }


        }

        if(requestCode ==CAMERA_REQUEST_CODE && resultCode == RESULT_OK){

            mProgress.setMessage("selecting image");
            mProgress.show();
            mImageUri=data.getData();
            StorageReference filePath =mStorage.child("photos").child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl =taskSnapshot.getStorage().getDownloadUrl();

                    mProgress.dismiss();
                    Toast.makeText(OtherActivity.this,"image selected",Toast.LENGTH_LONG).show();
                }
            });
        }

    }




}