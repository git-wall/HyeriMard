package com.example.client_chat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.client_chat.Activity.MainActivity;
import com.example.client_chat.Model.Messenger;
import com.example.client_chat.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Messenger> {
    public MessageAdapter(Context context, int resource, List<Messenger> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Messenger message = getItem(position);
//        if (TextUtils.isEmpty(message.getMessage())) {
//            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.user_connected, parent, false);
//           TextView messageText = convertView.findViewById(R.id.userConnected);
//           String userConnected = message.getUsername();
//          messageText.setText(userConnected);
//        } else
        if (message.getUniqueId().equals(MainActivity.uniqueId)) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.my_message, parent, false);
            ImageView img = convertView.findViewById(R.id.myImage);
            TextView messageText = convertView.findViewById(R.id.message_body);
            if (TextUtils.isEmpty(message.getMessage())) {
                messageText.setVisibility(View.GONE);
                Bitmap bitmap = getBitmapFromString(message.getMessage());
                img.setImageBitmap(bitmap);
            } else {
                img.setVisibility(View.GONE);
                messageText.setText(message.getMessage());
            }
        } else {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.their_message, parent, false);
            ImageView img = convertView.findViewById(R.id.theirImage);
            TextView username = convertView.findViewById(R.id.name);
            TextView message_body = convertView.findViewById(R.id.message_body);
            if (TextUtils.isEmpty(message.getMessage())) {
                message_body.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                username.setVisibility(View.VISIBLE);
                Bitmap bitmap = getBitmapFromString(message.getMessage());
                img.setImageBitmap(bitmap);
            } else {
                message_body.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
                username.setVisibility(View.VISIBLE);
                username.setText(message.getUsername());
                message_body.setText(message.getMessage());
            }
        }
        return convertView;
    }

    private Bitmap getBitmapFromString(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
