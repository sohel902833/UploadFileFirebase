package com.example.upshowpdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {


    private EditText nameEdittext;
    private Button  uploadButton;
    private Button  showButtonid;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nameEdittext=findViewById(R.id.pdf_NameEdittextid);
        uploadButton=findViewById(R.id.uploadButtonid);
        showButtonid=findViewById(R.id.showButtonid);

        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 name=nameEdittext.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter An Name ", Toast.LENGTH_SHORT).show();
                }else {
                    selectPdfFile();
                }
            }
        });

        showButtonid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,PdfShowerActivity.class);
                startActivity(intent);
            }
        });









    }

    private void selectPdfFile() {

        Intent intent=new Intent();
    intent.setType("application/pdf");
       // intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF File"),1);







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null && data !=null){
            uploadPDFFile(data.getData());
        }









    }

    private void uploadPDFFile(Uri data) {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading.......");
        progressDialog.show();
                    final long time=System.currentTimeMillis();
                StorageReference reference=storageReference.child("upload/"+time+".pdf");
                reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isSuccessful());
                            Uri url=uri.getResult();

                            UploadPdf uploadPdf=new UploadPdf(name,url.toString(),time);

                            databaseReference.child(databaseReference.push().getKey()).setValue(uploadPdf).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Upload Successfull", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage("Uploaded :"+(int)progress+"%");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




    }
}
