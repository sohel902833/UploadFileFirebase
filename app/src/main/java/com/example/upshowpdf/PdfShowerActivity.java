package com.example.upshowpdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class PdfShowerActivity extends AppCompatActivity {

    private ListView recyclerView;

    DatabaseReference databaseReference;
    List<UploadPdf> uploadPdfList=new ArrayList<>();





    //for downloading
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference ref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_shower);

        recyclerView=findViewById(R.id.pdfRecyclerViewid);

        viewPDFFiles();




        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            UploadPdf uploadPdf=uploadPdfList.get(position);
                            downloadfile(uploadPdf.getUrl(),uploadPdf.getFileName());
                        /*    Intent intent=new Intent(PdfShowerActivity.this,ShowActivity.class);
                            intent.putExtra("URL",uploadPdf.getUrl());
                            startActivity(intent);*/

              /*  Intent intent=new Intent();
                intent.setData(Uri.parse(uploadPdf.getUrl()));
                startActivity(intent);*/


            }
        });



















    }

    private void downloadfile(String url,long fileName) {

       final String name=String.valueOf(fileName);
            storageReference=FirebaseStorage.getInstance().getReference("upload");
            ref=storageReference.child(name+".pdf");


            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                        downloadFiles(PdfShowerActivity.this,name,".pdf",DIRECTORY_DOWNLOADS,uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PdfShowerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


    }

    private void downloadFiles(Context context,String fileName,String fileExtension,String destinationDirectory,String url) {
        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtension);
        downloadManager.enqueue(request);



    }

    private void viewPDFFiles() {

        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploadPdfList.clear();
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        UploadPdf uploadPdf=dataSnapshot1.getValue(UploadPdf.class);
                        uploadPdfList.add(uploadPdf);
                    }


                    String[] uploads=new String[uploadPdfList.size()];

                            for(int i=0; i<uploads.length; i++){
                                uploads[i]=uploadPdfList.get(i).getName();
                            }


                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,uploads);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
