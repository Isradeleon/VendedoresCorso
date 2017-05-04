package com.utt.application.isradeleon.vendedorescorso;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Isra on 4/13/2017.
 */

public class ErrorAdapter extends ArrayAdapter {
    LayoutInflater inflater;
    Context con;
    List obj;

    public ErrorAdapter(Context context, List objects, LayoutInflater in) {
        super(context, R.layout.error_item, objects);
        inflater=in;
        con=context;
        obj=objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView=convertView;
        if (itemView==null){
            itemView=inflater.inflate(R.layout.error_item,parent,false);
        }
        itemView.setContentDescription("ErrorMsg");
        ((TextView)itemView.findViewById(R.id.error_message)).setText(
                String.valueOf(
                        obj.get(position)
                )
        );
        return itemView;
    }
}
