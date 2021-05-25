package com.example.penpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupsFragment extends Fragment {
    View groupFragmentView;
    private ListView listViewGroups;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfGroups = new ArrayList<>();

    private DatabaseReference groupRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        InitializeFields();

        RetrieveAndDisplayGroups();

        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                //String tmpGroupName = parent.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                startActivity(groupChatIntent);
            }
        });

        return groupFragmentView;
    }

    private void InitializeFields() {
        listViewGroups = groupFragmentView.findViewById(R.id.list_view_groups);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listOfGroups);
        listViewGroups.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext()){
                    //String firebaseGrpName = ((DataSnapshot)iterator.next()).getKey();
                   // String grpName = firebaseGrpName.substring(0,firebaseGrpName.indexOf("_"));
                    set.add(((DataSnapshot)iterator.next()).getKey());

                }
                listOfGroups.clear();
                listOfGroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
