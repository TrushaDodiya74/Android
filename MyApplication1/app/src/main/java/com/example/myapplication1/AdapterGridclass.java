package com.example.myapplication1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterGridclass extends BaseAdapter {
    Context ctx;
    String[] str1,str2;
    int[] imgs;
    public AdapterGridclass(Context c,String[] s1,String[] s2,int[] img)
    {
        this.ctx=c;
        this.str1=s1;
        this.str2=s2;
        this.imgs=img;
    }

    @Override
    public int getCount() {
        return imgs.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView==null)
        {
            LayoutInflater li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=li.inflate(R.layout.gridlayout,null);
        }
        else
        {
            v = convertView;
        }
        ImageView iv = v.findViewById(R.id.imageView);
        TextView tv1 = v.findViewById(R.id.textView);
        TextView tv2 = v.findViewById(R.id.textView2);

        iv.setImageResource(imgs[position]);
        tv1.setText(str1[position]);
        tv2.setText(str2[position]);

        return v;

    }
}
