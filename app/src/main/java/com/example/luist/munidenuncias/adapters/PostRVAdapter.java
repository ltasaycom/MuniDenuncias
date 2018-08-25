package com.example.luist.munidenuncias.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luist.munidenuncias.R;
import com.example.luist.munidenuncias.models.Post;
import com.example.luist.munidenuncias.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.ViewHolder>{

    private static final String TAG = PostRVAdapter.class.getSimpleName();

    private List<Post> posts;

    public PostRVAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public PostRVAdapter() {
        this.posts = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView displaynameText;
        TextView likesText;
        LikeButton likeButton;
        ImageView pictureImage;
        TextView titleText;
        TextView bodyText;

        ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_picture);
            displaynameText = itemView.findViewById(R.id.user_displayname);
            likesText = itemView.findViewById(R.id.like_count);
            likeButton = itemView.findViewById(R.id.like_button);
            pictureImage = itemView.findViewById(R.id.post_picture);
            titleText = itemView.findViewById(R.id.post_title);
            bodyText = itemView.findViewById(R.id.post_body);
        }
    }

    @NonNull
    @Override
    public PostRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_denuncias, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final PostRVAdapter.ViewHolder viewHolder, int position) {
        final Post post = posts.get(position);

        viewHolder.titleText.setText(post.getTitle());
        viewHolder.bodyText.setText(post.getBody());
        // Download photo from Firebase Storage
        if(post.getPhotoUrl() != null) {
            Picasso.with(viewHolder.itemView.getContext()).load(post.getPhotoUrl()).into(viewHolder.pictureImage);
        }else{
            viewHolder.pictureImage.setImageResource(R.drawable.ic_picture);
        }

        // Obteniendo datos del usuario asociado al post (una vez, sin realtime)
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(post.getUserid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange " + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);

                Picasso.with(viewHolder.itemView.getContext()).load(user.getPhotoUrl()).placeholder(R.drawable.ic_profile).into(viewHolder.userImage);
                viewHolder.displaynameText.setText(user.getDisplayName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        });

        viewHolder.likesText.setText(String.format(Locale.getDefault(), "%d views", post.getView().size()));

        // Get currentuser from FirebaseAuth
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

        // Marcando el like button siempre y cuando el uid del usuario actual se encuentre en la lista de likes
        viewHolder.likeButton.setLiked(post.getView().containsKey(currentUser.getUid()));
        //viewHolder.likeButton.setIcon(IconType.Thumb);

        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("posts")
                .child(post.getId())
                .child("view");

        // Implementando el evento click del like button
        viewHolder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Log.d(TAG, "Like it!");
                likesRef.child(currentUser.getUid()).setValue(true)
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
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Log.d(TAG, "Doesn't like it!");
                likesRef.child(currentUser.getUid()).removeValue()
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }
}
