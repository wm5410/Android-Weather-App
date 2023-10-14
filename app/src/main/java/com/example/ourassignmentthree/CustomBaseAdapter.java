package com.example.ourassignmentthree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> webCameraList;
    LayoutInflater layoutInflater;
    public CustomBaseAdapter(Context context, ArrayList<String> webCameras){
        this.context = context;
        this.webCameraList = webCameras;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return webCameraList.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //Set the view
        convertView = layoutInflater.inflate(R.layout.activity_custom_list_view, null);
        //Create a textview variable and set it to the textview in the xml layout
        TextView textView = (TextView) convertView.findViewById(R.id.tv_camera_name);
        //Set to strings from the list
        textView.setText(webCameraList.get(i));
        //Return the view
        return convertView;
    }
}
