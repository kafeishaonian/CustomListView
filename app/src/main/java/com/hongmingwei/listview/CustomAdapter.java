package com.hongmingwei.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hongmingwei on 2017/2/24 10:44
 */
public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<String> strings = new ArrayList<>();

    public CustomAdapter(Context context){
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(ArrayList<String> str){
        strings.clear();
        strings.addAll(str);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Handler handler;
        if (convertView == null){
            handler = new Handler();
            convertView = inflater.inflate(R.layout.list_item, null);
            handler.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(handler);
        } else {
            handler = (Handler) convertView.getTag();
        }
        handler.text.setText(strings.get(position));
        return convertView;
    }


    class Handler{
        TextView text;
    }
}
