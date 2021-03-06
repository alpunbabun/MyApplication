package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    MyAdapter myAdapter;
    ArrayList<Product> productArrayList;
    ProgressDialog progressDialog;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        myAdapter = new MyAdapter(WelcomeActivity.this, productArrayList);

        recyclerView.setAdapter(myAdapter);

        EventChangeListener();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemAtoz:
                //sort a to z
                Collections.sort(productArrayList, Product.ProductItemAtoZComparator);
                Toast.makeText(WelcomeActivity.this, "Sort A to Z", Toast.LENGTH_SHORT).show();
                myAdapter.notifyDataSetChanged();
                return true;
            case R.id.itemZtoa:
                //sort z to a
                Collections.sort(productArrayList, Product.ProductItemZtoAComparator);
                Toast.makeText(WelcomeActivity.this, "Sort Z TO A", Toast.LENGTH_SHORT).show();
                myAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void EventChangeListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        db.collection("Products")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
//                                        @Nullable FirebaseFirestoreException e) {
//
//                        if (e != null) {
//                            Log.w("YourTag", "Listen failed.", e);
//                            return;
//                        }
//
//                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                            if (doc.exists()){
//                                Product product = doc.toObject(Product.class);
//                                productArrayList.add(product);
//                                myAdapter.notifyDataSetChanged();
//                                if (progressDialog.isShowing())
//                                progressDialog.dismiss();
//                            }
//                        }
//                        Log.d("YourTag", "messageList: " + productArrayList);
//                    }
//                });

//          db.collection("Products")
//                  .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                      private String TAG;
//                      @Override
//                      public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                          if (error != null) {
//                            Log.e(TAG, "listen:error", error);
//                            return;
//                        }
//                          if (value != null) {
//                              Log.d(TAG, "onEvent: ");
//                              List<DocumentChange> productArrayList = value.getDocumentChanges();
//                              for (DocumentChange documentChange: productArrayList) {
//                                  Log.d(TAG, "onEvent: " + documentChange.getDocument().getData());
//                              }
//                          } else {
//                              Log.e(TAG, "onEvent: query snapshot was null");
//                          }
//                      }
//                  });

//        db.collection("Products")
//                .whereEqualTo("state", "CA")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    private String TAG;
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value,
//                                        @Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.w(TAG, "listen:error", e);
//                            return;
//                        }
//
//                        for (DocumentChange dc : value.getDocumentChanges()) {
//                            switch (dc.getType()) {
//                                case ADDED:
//                                    Log.d(TAG, "New product: " + dc.getDocument().getData());
//                                    break;
//                                case MODIFIED:
//                                    Log.d(TAG, "Modified product: " + dc.getDocument().getData());
//                                    break;
//                                case REMOVED:
//                                    Log.d(TAG, "Removed product: " + dc.getDocument().getData());
//                                    break;
//                            }
//                            myAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//                });

        db.collection("Products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    private String TAG;

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        productArrayList.clear();
                        for (QueryDocumentSnapshot document: value) {
                            productArrayList.add(new Product(document.getString("Product"), document.getString("Block"), document.getString("Fullname"), document.getString("SerialNumber")));
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.d(TAG, "onComplete" + document.getId());
                            Log.d(TAG, "onComplete" + document.getData());
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                });

//        db.collection("Products")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
////                        Toast.makeText(WelcomeActivity.this, "gasfgdhasvgdhafsdfsa", Toast.LENGTH_SHORT).show();
////                        if (progressDialog.isShowing())
////                                progressDialog.dismiss();
//                        if (error != null) {
//                            if (progressDialog.isShowing())
//                                progressDialog.dismiss();
//                            Log.e("Firestore error", error.getMessage());
//                        }
//                        for (DocumentChange dc : value.getDocumentChanges()) {
//                            if (dc.getType() == DocumentChange.Type.ADDED) {
//                                productArrayList.add(dc.getDocument().toObject(Product.class));
//                            }
//                            if (dc.getType() == DocumentChange.Type.MODIFIED) {
//                                productArrayList.add(dc.getDocument().toObject(Product.class));
//                                productArrayList.remove(dc.getDocument().toObject(Product.class));
//                            }
//                            if (dc.getType() == DocumentChange.Type.REMOVED) {
//                                productArrayList.remove(dc.getDocument().toObject(Product.class));
//                            }
//                            myAdapter.notifyDataSetChanged();
//                            if (progressDialog.isShowing())
//                                progressDialog.dismiss();
//                        }
//                    }
//                });

    }

}