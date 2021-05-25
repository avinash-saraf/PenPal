package com.example.penpal.interests.ui.main;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.penpal.interests.model.Interest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class PageViewModel extends ViewModel {

    private MutableLiveData<HashMap<String, Object>> mMap = new MutableLiveData<>();

    private LiveData<ArrayList<Interest>> mInterestList = Transformations.map(mMap,  new Function<HashMap<String, Object>, ArrayList<Interest>>() {

        @Override
        public ArrayList<Interest> apply(HashMap<String, Object> inputMap) {

            ArrayList<String> selectedInterests = (ArrayList<String>) inputMap.get("selectedInterests");
            if(selectedInterests.isEmpty()){
                Log.println(Log.ASSERT, "selected Interests List", "isEmpty");
            }
            // init
            ArrayList<Interest> interestsList = new ArrayList<>();
            Context context = (Context) inputMap.get("context");
            String input = (String) inputMap.get("type");
            BufferedReader br1, br2;
            String hobby,hobbyImage;
            try{
                //init -> buffered reader
                AssetManager am = context.getAssets();
                br1 = new BufferedReader(new InputStreamReader(am.open(input + ".txt")));
                br2 = new BufferedReader(new InputStreamReader(am.open(input + "_images.txt")));

                while ((hobby = br1.readLine()) != null){
                    //if !null add hobby to list
                    hobbyImage = br2.readLine();
                    Interest currentInterest = new Interest(hobbyImage, hobby, input);
                    if(selectedInterests.contains(hobby)){
                        currentInterest.setmInterestClicked(true);
                    }
                    interestsList.add(currentInterest);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return interestsList;

        }
    });

    public LiveData<ArrayList<Interest>> getmInterestList(){
        return mInterestList;
    }

    public void setmMap(HashMap<String, Object> hashMap){
        mMap.setValue(hashMap);
    }

    public MutableLiveData<HashMap<String, Object>> getmMap(){return this.mMap;}


}