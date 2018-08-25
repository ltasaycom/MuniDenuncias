package com.example.luist.munidenuncias.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.luist.munidenuncias.R;
import com.example.luist.munidenuncias.adapters.PostRVAdapter;
import com.example.luist.munidenuncias.models.Post;
import com.example.luist.munidenuncias.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private static final String TAG = ListaActivity.class.getSimpleName();
    private static final int REGISTER_FORM_REQUEST = 100;
    private static final int REGISTER_MAPS_REQUEST = 200;

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        // Get currentuser from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

        // Save/Update current user to Firebase Database
        User user = new User();
        user.setUid(currentUser.getUid());
        user.setDisplayName(currentUser.getDisplayName());
        user.setEmail(currentUser.getEmail());
        user.setPhotoUrl((currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null));

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(user.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onSuccess");
                        } else {
                            Log.e(TAG, "onFailure", task.getException());
                        }
                    }
                });

        // Obteniendo datos del usuario de Firebase en tiempo real
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange " + dataSnapshot.getKey());

                // Obteniendo datos del usuario
                User user = dataSnapshot.getValue(User.class);
                setTitle("Bienvenido " + user.getDisplayName());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        });

        // Lista de post con RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final PostRVAdapter postRVAdapter = new PostRVAdapter();
        recyclerView.setAdapter(postRVAdapter);

        // Obteniendo lista de posts de Firebase (con realtime)
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded " + dataSnapshot.getKey());

                // Obteniendo nuevo post de Firebase
                String postKey = dataSnapshot.getKey();
                final Post addedPost = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "addedPost " + addedPost);

                // Actualizando adapter datasource
                List<Post> posts = postRVAdapter.getPosts();
                posts.add(0, addedPost);
                postRVAdapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged " + dataSnapshot.getKey());

                // Obteniendo post modificado de Firebase
                String postKey = dataSnapshot.getKey();
                Post changedPost = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "changedPost " + changedPost);

                // Actualizando adapter datasource
                List<Post> posts = postRVAdapter.getPosts();
                int index = posts.indexOf(changedPost); // Necesario implementar Post.equals()
                if(index != -1){
                    posts.set(index, changedPost);
                }
                postRVAdapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved " + dataSnapshot.getKey());

                // Obteniendo post eliminado de Firebase
                String postKey = dataSnapshot.getKey();
                Post removedPost = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "removedPost " + removedPost);

                // Actualizando adapter datasource
                List<Post> posts = postRVAdapter.getPosts();
                posts.remove(removedPost); // Necesario implementar Post.equals()
                postRVAdapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved " + dataSnapshot.getKey());

                // A post has changed position, use the key to determine if we are
                // displaying this post and if so move it.
                Post movedPost = dataSnapshot.getValue(Post.class);
                String postKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        };
        postsRef.addChildEventListener(childEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logaut, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                callLogout(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callLogout(View view){
        Log.d(TAG, "Ssign out user");
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void showRegister(View view){
        startActivityForResult(new Intent(this, RegistrarActivity.class), REGISTER_FORM_REQUEST);
    }

    public void showMaps(View view)
    {
        startActivityForResult(new Intent(this, MapsActivity.class), REGISTER_MAPS_REQUEST);
    }

}
