package com.bricefamily.alex.time_tracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by bricenangue on 07/02/16.
 */
public class ViewFriendsProfile extends Fragment {

    public static ViewFriendsProfile newInstance(User user){
        ViewFriendsProfile fragment= new ViewFriendsProfile();
        Bundle args=new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        User u=getArguments().getParcelable("user");

        View v=inflater.inflate(R.layout.view_friends_fragment, container, false);
        TextView username=(TextView)v.findViewById(R.id.usernameviewfragment);
        username.setText(u.username);
        return v;
    }
}
