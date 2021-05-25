package com.example.penpal.interests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.penpal.R;
import com.example.penpal.interests.model.Interest;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder> implements SectionTitleProvider {

    private ArrayList<Interest> mInterestList;
    private OnItemClickListener mOnItemClickListener;

    @Override
    public String getSectionTitle(int position) {
        return mInterestList.get(position).getmInterestName().substring(0,1);
    }

    public static class InterestsViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView, mImageClicked;
        public TextView mTextView;

        public InterestsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.interest_imageView);
            mTextView = itemView.findViewById(R.id.interest_name);
            mImageClicked = itemView.findViewById(R.id.interest_clicked);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                            mImageClicked.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public InterestsAdapter(ArrayList<Interest> interestsList){
        mInterestList = interestsList; }

        public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
            mOnItemClickListener = onItemClickListener;
        }


    @NonNull
    @Override
    public InterestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_cardview, parent, false);
        return (new InterestsViewHolder(v, mOnItemClickListener));

    }

    @Override
    public void onBindViewHolder(@NonNull InterestsViewHolder holder, int position) {
        Interest currentInterest = mInterestList.get(position);
        Glide.with(holder.itemView.getContext()).load(currentInterest.getmImageLink()).centerCrop().placeholder(R.drawable.profile_icon).into(holder.mImageView);
        holder.mTextView.setText(currentInterest.getmInterestName());
        if(currentInterest.ismInterestClicked()) {holder.mImageClicked.setVisibility(View.VISIBLE);}
        else{holder.mImageClicked.setVisibility(View.INVISIBLE);}

    }


    @Override
    public int getItemCount() {
        return mInterestList.size();
    }

}

