package com.example.penpal.interests.ui.main;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.penpal.R;
import com.example.penpal.interests.InterestsAdapter;
import com.example.penpal.interests.model.Interest;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private View root;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private RecyclerView mRecyclerView;
    private FastScroller fastScroller;
    private InterestsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getCurrentUser().getUid();
    private DatabaseReference interestsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("interests");
    private final ArrayList<String> selectedInterests = new ArrayList<>();
    //private boolean alreadyExists = false;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }

        ArrayList<String> interestsTypes = new ArrayList<>();
        interestsTypes.add("home");
        interestsTypes.add("indoors");
        interestsTypes.add("outdoors");
        interestsTypes.add("education");

        String type = interestsTypes.get(index);

        Context context = getContext();
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("context", context);
        hm.put("type", type);

      //  Log.d(TAG, "onCreate: getSelectedInterests" + selectedInterests.get(0));
        hm.put("selectedInterests", getSelectedInterests());

        pageViewModel.setmMap(hm);
    }
    private ArrayList<String> getSelectedInterests(){
        final ArrayList<String> selected = new ArrayList<>();
        interestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selected.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    selected.add(dataSnapshot1.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return selected;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_interests, container, false);

        mRecyclerView = root.findViewById(R.id.interests_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(root.getContext());
        fastScroller = root.findViewById(R.id.fastScroll);


        pageViewModel.getmInterestList().observe(this, new Observer<ArrayList<Interest>>() {
            @Override
            public void onChanged(final ArrayList<Interest> interests) {
                mAdapter = new InterestsAdapter(interests);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        switch (direction){
                            case ItemTouchHelper.LEFT:
                                interests.get(position).setmInterestClicked(false);
                                mAdapter.notifyItemChanged(position);
                                removeInterestFromDatabase(interests.get(position));
                                break;
                            case ItemTouchHelper.RIGHT:
                                interests.get(position).setmInterestClicked(true);
                                mAdapter.notifyItemChanged(position);
                                addInterestToDatabase(interests.get(position));
                                break;
                        }
                    }
                };

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(mRecyclerView);

                fastScroller.setHandleColor(R.color.colorPrimaryDark);
                fastScroller.setBubbleColor(R.color.colorPrimaryDark);
                fastScroller.setRecyclerView(mRecyclerView);

                mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

               // setSelectedInterestsList(interests);
            }
        });

        return root;
    }

    private void addInterestToDatabase(Interest interest){
            Log.println(Log.ASSERT, "addInterests", "does not exist already");
            HashMap<String, Object> interestsMap = new HashMap<>();
            interestsMap.put("name", interest.getmInterestName());
            interestsMap.put("image", interest.getmImageLink());
            interestsMap.put("type", interest.getmInterestType());

            interestsRef.child(interest.getmInterestName()).updateChildren(interestsMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.println(Log.ASSERT, "addInterests", "added successfully");
                                Snackbar.make(root, "Interest Added" ,Snackbar.LENGTH_SHORT)
                                        .setAction("No Action", null).show();
                            } else{
                                Log.println(Log.ASSERT, "addInterests", "error occured");
                                Snackbar.make(root, "Error Occurred: " + task.getException().getMessage(),Snackbar.LENGTH_SHORT)
                                        .setAction("No Action", null).show();
                            }
                        }
                    });

    }

    private void removeInterestFromDatabase(final Interest interest){
        interestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(interest.getmInterestName())){
                    interestsRef.child(interest.getmInterestName()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Snackbar.make(root, "Interest Deselected" ,Snackbar.LENGTH_SHORT)
                                                .setAction("UNDO", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        addInterestToDatabase(interest);
                                                        interest.setmInterestClicked(true);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}