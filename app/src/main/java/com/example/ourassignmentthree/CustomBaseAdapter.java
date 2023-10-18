package com.example.ourassignmentthree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*
 * This is the custom adapter which sets the layout for each row in the camera listview.
 */
public class CustomBaseAdapter extends BaseAdapter {
    //Declare variables
    Context context;
    String[] webCameraList;
    LayoutInflater layoutInflater;
    /*
     * This is the constructor.
     */
    public CustomBaseAdapter(Context context, String[] webCameras){
        this.context = context;
        this.webCameraList = webCameras;
        layoutInflater = LayoutInflater.from(context);
    }
    /*
     * This counts the items in the list.
     */
    @Override
    public int getCount() {
        return webCameraList.length;
    }
    /*
     * This returns the object.
     */
    @Override
    public Object getItem(int i) {
        return null;
    }
    /*
     * This gets the item id.
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }
    //This gets and sets the view.
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //Set the view
        convertView = layoutInflater.inflate(R.layout.activity_custom_list_view, null);
        //Create a textview variable and set it to the textview in the xml layout
        TextView textView = (TextView) convertView.findViewById(R.id.tv_camera_name);
        //Set to strings from the list
        textView.setText(webCameraList[i]);
        //Return the view
        return convertView;
    }
}
