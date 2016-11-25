package com.xyser.xiaodu.adatper;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyser.xiaodu.R;
import com.xyser.xiaodu.bean.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * Created by dingdayu on 2016/11/7.
 */

public class ChatMessageAdatper extends BaseAdapter {

    private LayoutInflater mInflater;
    private List <ChatMessage> mDatas;

    public ChatMessageAdatper(Context context, List <ChatMessage> mDatas) {
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        com.xyser.xiaodu.bean.ChatMessage chatMessage = mDatas.get(position);
        if(chatMessage.getType() == ChatMessage.Type.INCOMING) {
            return 0;
        }
        if(chatMessage.getType() == ChatMessage.Type.OUTCOMING)
        {
            return 1;
        }
        if(chatMessage.getType() == ChatMessage.Type.INIMAGE)
        {
            return 2;
        }
        if(chatMessage.getType() == ChatMessage.Type.OUTIMAGE)
        {
            return 3;
        }
        return 0;
    }

    @Override

    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = mDatas.get(position);
        ViewHolder viewHolder = new ViewHolder();
        if(convertView == null) {
            // 通过itemType设置不同的布局
            if(getItemViewType(position) == 0){
                Log.i("getItemViewType", "0");
                convertView = mInflater.inflate(R.layout.item_from_msg, parent, false);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_from_msg_date);
                viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_from_msg_info);
                viewHolder.mImage = (ImageView) convertView.findViewById(R.id.id_to_msg_img);
            } else if(getItemViewType(position) == 1){
                Log.i("getItemViewType", "1");
                convertView = mInflater.inflate(R.layout.item_to_msg, parent, false);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_to_msg_date);
                viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_to_msg_info);
                viewHolder.mImage = (ImageView) convertView.findViewById(R.id.id_to_msg_img);
            } else if(getItemViewType(position) == 2){
                Log.i("getItemViewType", "2");

                convertView = mInflater.inflate(R.layout.item_from_img, parent, false);

                viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_to_msg_date);
                viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_to_msg_info);
                viewHolder.mImage = (ImageView) convertView.findViewById(R.id.id_to_msg_img);
            } else {
                Log.i("getItemViewType", "3");
                convertView = mInflater.inflate(R.layout.item_to_img, parent, false);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_to_msg_date);
                viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_to_msg_info);
                viewHolder.mImage = (ImageView) convertView.findViewById(R.id.id_to_msg_img);
            }
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 设置数据
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(chatMessage.getDate());

        viewHolder.mDate.setText(time);
        viewHolder.mMsg.setText(chatMessage.getMsg());


        if(getItemViewType(position) == 0 || getItemViewType(position) == 1) {
            viewHolder.mMsg.setVisibility(View.VISIBLE);
            viewHolder.mImage.setVisibility(View.GONE);
        } else {
            viewHolder.mImage.setImageBitmap(chatMessage.getBitmap());
            viewHolder.mMsg.setVisibility(View.GONE);
            viewHolder.mImage.setVisibility(View.VISIBLE);
        }


        return convertView;
    }

    private final class ViewHolder
    {
        TextView mDate;
        TextView mMsg;
        ImageView mImage;
    }
}
