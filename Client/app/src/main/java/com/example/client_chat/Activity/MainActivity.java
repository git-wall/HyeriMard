package com.example.client_chat.Activity;

import static androidx.core.content.res.ResourcesCompat.getFont;
import static com.example.client_chat.Utils.SocketIO.mSocket;
import static com.example.client_chat.Utils.StringSet.MY_REQUEST_AUDIO;
import static com.example.client_chat.Utils.StringSet.MY_REQUEST_CODE;
import static com.example.client_chat.Utils.StringSet.MY_REQUEST_PICTURE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client_chat.Adapter.ContentImageAdapter;
import com.example.client_chat.Adapter.UserAdapter;
import com.example.client_chat.Model.Messenger;
import com.example.client_chat.Model.User;
import com.example.client_chat.Other.NetworkChangeListener;
import com.example.client_chat.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.socket.emitter.Emitter;
import rm.com.audiowave.AudioWaveView;
import rm.com.audiowave.OnProgressListener;
import www.sanju.motiontoast.MotionToast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static int Gender;
    private EditText textField;
    private ImageButton sendButton, ChoseImgButton, ChoseOther, ChoseCamera, Like, Mic;
    public static String uniqueId, Username, imgProfile, idMessage;
    private Boolean hasConnection = false;
    private RecyclerView rclView1, rclViewUser;
    private Thread thread2;
    private boolean startTyping = false;
    private int time = 2;
    private ArrayList<Messenger> messageFormats;
    private ArrayList<User> users;
    float a = 0;
    private MediaRecorder myRecorder;
    int switchNumber = 0;
    private long PauseOff = 0;
    private boolean isPause = false;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private ContentImageAdapter contentImageAdapter;
    private UserAdapter userAdapter;
    private String path = null;
    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) {
                        return;
                    }
                    try {
                        FileInputStream os = (FileInputStream) getContentResolver().openInputStream(data.getData());
                        Bitmap image = BitmapFactory.decodeStream(os);
                        sendImage(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: typing stopped " + startTyping);
            if (time == 0) {
                setTitle("SocketIO");
                Log.i(TAG, "handleMessage: typing stopped time is " + time);
                startTyping = false;
                time = 2;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Username = getIntent().getStringExtra("ten");
        Gender = getIntent().getIntExtra("gt", 1);
        imgProfile = getIntent().getStringExtra("profile");
        uniqueId = UUID.randomUUID().toString();
        idMessage = UUID.randomUUID().toString();
        Hello();
        if (savedInstanceState != null) {
            hasConnection = savedInstanceState.getBoolean("hasConnection");
        }
        if (hasConnection) {

        } else {
            Socket();
            JSONObject userId = new JSONObject();
            try {
                userId.put("uniqueId", uniqueId);
                userId.put("username", Username);
                userId.put("imageInfo", imgProfile);
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Mirror();
        hasConnection = true;
        rclView1 = findViewById(R.id.rclView1);
        rclViewUser = findViewById(R.id.rclViewUser);
        //User List
        showListUser();
        // Message List
        showListMessage();
        onTypeButtonEnable();
        Anime();
        Mic.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_REQUEST_AUDIO);
            } else {
                showDialog();
            }
        });
        ChoseOther.setOnClickListener(view -> {
            final BottomSheetDialog bottomSheetMaterialDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheer, (LinearLayout) findViewById(R.id.linearBottomSheet));
            bottomSheetView.findViewById(R.id.linear_location).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bottomSheetMaterialDialog.dismiss();
                }
            });
            bottomSheetMaterialDialog.setContentView(bottomSheetView);
            bottomSheetMaterialDialog.show();
        });
        ChoseCamera.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, MY_REQUEST_PICTURE);
        });
        ChoseImgButton.setOnClickListener(view -> {
            onClickRequestPermission();
        });
        Like.setOnClickListener(view -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.like);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            String base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("imgInfo", imgProfile);
                jsonObject.put("username", Username);
                jsonObject.put("uniqueId", uniqueId);
                jsonObject.put("image", base64);
                jsonObject.put("type", 2);
                mSocket.emit("image", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_mic);
        RelativeLayout relativeLayout = dialog.findViewById(R.id.constraint1);
        FloatingMusicActionButton musicFab = relativeLayout.findViewById(R.id.play_pause);
        AudioWaveView wave = relativeLayout.findViewById(R.id.wave);
        Chronometer number = relativeLayout.findViewById(R.id.number);
        ImageView send = relativeLayout.findViewById(R.id.sendBird);
        musicFab.setOnClickListener(view -> {
            if (switchNumber == 0) {
                startRecorder();
                number.setBase(SystemClock.elapsedRealtime() - PauseOff);
                number.start();
                switchNumber = 1;
                musicFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);
                musicFab.playAnimation();
                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.setDataSource(path);
                    FileInputStream inputStream = new FileInputStream(path);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    for (int readNum; (readNum = inputStream.read(b)) != -1; ) {
                        bos.write(b, 0, readNum);
                    }
                    byte[] arr = bos.toByteArray();
                    wave.setRawData(arr);
                    wave.setScaledData(arr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int m = mp.getCurrentPosition() / 1000;
                wave.setExpansionDuration(mp.getDuration());
                wave.setProgress(m);
                wave.setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onStartTracking(float v) {

                    }

                    @Override
                    public void onStopTracking(float v) {

                    }

                    @Override
                    public void onProgressChanged(float v, boolean b) {
                        wave.isExpansionAnimated();
                    }
                });
            } else {
                number.stop();
                PauseOff = SystemClock.elapsedRealtime() - number.getBase();
                musicFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);
                musicFab.playAnimation();
                myRecorder.stop();
                myRecorder.release();
                myRecorder = null;
                switchNumber = 2;
            }
        });
        send.setOnClickListener(view1 -> {
            myRecorder.stop();
            myRecorder.release();
            myRecorder = null;
            sendSound();
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void sendSound() {
        String path1 = Environment.getExternalStorageDirectory().getAbsolutePath();
        path1 += "/thanh.3gpp";
        encodeAudio(path1);
    }

    private void encodeAudio(String Path) {
        byte[] audioBytes;
        try {
            File audioFile = new File(Path);
            long fileSize = audioFile.length();
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(Path);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                bao.write(buf, 0, n);
            audioBytes = bao.toByteArray();
            String audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT);
            JSONObject jsonObject = new JSONObject();
            try {
                if (imgProfile != null) {
                    jsonObject.put("imgInfo", imgProfile);
                }
                jsonObject.put("username", Username);
                jsonObject.put("uniqueId", uniqueId);
                jsonObject.put("audio", audioBase64);
                jsonObject.put("type", 3);
                mSocket.emit("audio", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resumeRecorder() {
        myRecorder.resume();
        isPause = false;
    }

    @SuppressLint("WrongConstant")
    private void startRecorder() {
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/thanh.3gpp";
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        myRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
//        MediaRecorder.OutputFormat.AMR_NB
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        myRecorder.setOutputFile(path);
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Vip Toast
    private void Hello() {
        MotionToast.Companion.darkToast(MainActivity.this,
                "Hello " + Username,
                "Wish you a lucky day !!!",
                MotionToast.TOAST_INFO,
                MotionToast.GRAVITY_CENTER,
                MotionToast.LONG_DURATION,
                getFont(MainActivity.this, R.font.helvetica_regular));
    }

    // seng image from base64 to string
    private void sendImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        String base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imgInfo", imgProfile);
            jsonObject.put("username", Username);
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("image", base64);
            jsonObject.put("type", 2);
            mSocket.emit("image", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Socket() {
        mSocket.connect();
        mSocket.on("connect user", onNewUser);
        mSocket.on("chat message", onNewMessage);
        mSocket.on("on typing", onTyping);
        mSocket.on("img", onImg);
        mSocket.on("audio", onAudio);
    }

    // Anh xa
    public void Mirror() {
        ChoseImgButton = findViewById(R.id.ChoseImgButton);
        textField = findViewById(R.id.textField);
        sendButton = findViewById(R.id.sendButton);
        ChoseOther = findViewById(R.id.ChoseOther);
        ChoseCamera = findViewById(R.id.ChoseCamera);
        Like = findViewById(R.id.Like);
        Mic = findViewById(R.id.Mic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_PICTURE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
            sendImage(image);
        } else if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {
            FileInputStream os = null;
            try {
                os = (FileInputStream) getContentResolver().openInputStream(Objects.requireNonNull(data).getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap image = BitmapFactory.decodeStream(os);
            sendImage(image);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ObsoleteSdkInt")
    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.RECORD_AUDIO};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        } else if (requestCode == MY_REQUEST_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDialog();
            } else {
                //User denied Permission.
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    //Animation
    public void Anime() {
        textField.setTranslationY(300);
        sendButton.setTranslationY(300);
        ChoseImgButton.setTranslationY(300);
        ChoseOther.setTranslationY(300);
        ChoseCamera.setTranslationY(300);
        Like.setTranslationY(300);
        Mic.setTranslationY(300);

        textField.setAlpha(a);
        sendButton.setAlpha(a);
        ChoseImgButton.setAlpha(a);
        ChoseOther.setAlpha(a);
        ChoseCamera.setAlpha(a);
        Like.setTranslationY(300);
        Mic.setTranslationY(300);

        textField.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        sendButton.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        ChoseImgButton.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        ChoseOther.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        ChoseCamera.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        Mic.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        Like.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
    }

    public void onTypeButtonEnable() {
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                JSONObject onTyping = new JSONObject();
                try {
                    onTyping.put("typing", true);
                    onTyping.put("username", Username);
                    onTyping.put("uniqueId", uniqueId);
                    mSocket.emit("on typing", onTyping);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                    sendButton.setImageResource(R.drawable.ic_send_blue_24dp);
                } else {
                    sendButton.setEnabled(false);
                    sendButton.setImageResource(R.drawable.ic_send_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //get Audio
    Emitter.Listener onAudio = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                String username;
                String audio;
                String id;
                int type;
                String imgInfo;
                try {
                    imgInfo = data.getString("imgInfo");
                    username = data.getString("username");
                    audio = data.getString("audio");
                    id = data.getString("uniqueId");
                    type = data.getInt("type");
                    Messenger format = new Messenger(imgInfo, id, username, audio, Gender, type, idMessage);
                    messageFormats.add(format);
                    contentImageAdapter.notifyItemInserted(messageFormats.indexOf(format));
                    rclView1.smoothScrollToPosition(messageFormats.size());
                } catch (Exception ignored) {
                }
            });
        }
    };
    //get new Image
    Emitter.Listener onImg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                String username;
                String img;
                String id;
                int type;
                String imgInfo;
                try {
                    imgInfo = data.getString("imgInfo");
                    username = data.getString("username");
                    img = data.getString("image");
                    id = data.getString("uniqueId");
                    type = data.getInt("type");
                    Messenger format = new Messenger(imgInfo, id, username, img, Gender, type, idMessage);
                    messageFormats.add(format);
                    contentImageAdapter.notifyItemInserted(messageFormats.indexOf(format));
                    rclView1.smoothScrollToPosition(messageFormats.size());
                } catch (Exception ignored) {
                }
            });
        }
    };
    //get new content
    Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                String username;
                String message;
                String id;
                String imgInfo;
                int type;
                try {
                    imgInfo = data.getString("imgInfo");
                    username = data.getString("username");
                    message = data.getString("message");
                    id = data.getString("uniqueId");
                    type = data.getInt("type");
                    Messenger format = new Messenger(imgInfo, id, username, message, Gender, type, idMessage);
                    messageFormats.add(format);
                    contentImageAdapter.notifyItemInserted(messageFormats.indexOf(format));
                    rclView1.smoothScrollToPosition(messageFormats.size());
                } catch (Exception ignored) {
                }
            });
        }
    };
    //get new user
    Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(() -> {
                String username = args[0].toString();
                String id;
                String img;
                try {
                    JSONObject object = new JSONObject(username);
                    username = object.getString("username");
                    id = object.getString("uniqueId");
                    img = object.getString("imageInfo");
                    User us;
                        us = new User(img, username, id, Gender);
                        users.add(us);
                        userAdapter.notifyItemInserted(messageFormats.indexOf(us));
                        rclViewUser.smoothScrollToPosition(users.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    };
    // typing or not
    Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        boolean typingOrNot = data.getBoolean("typing");
                        String userName = data.getString("username") + " is Typing......";
                        String id = data.getString("uniqueId");
                        if (id.equals(uniqueId)) {
                            typingOrNot = false;
                        } else {
                            setTitle(userName);
                        }
                        if (typingOrNot) {
                            if (!startTyping) {
                                startTyping = true;
                                thread2 = new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                while (time > 0) {
                                                    synchronized (this) {
                                                        try {
                                                            wait(1000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        time--;
                                                    }
                                                    handler2.sendEmptyMessage(0);
                                                }
                                            }
                                        }
                                );
                                thread2.start();
                            } else {
                                time = 2;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    // send content to sever
    public void sendMessage(View view) {
        String message = textField.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Log.i(TAG, "sendMessage:2 ");
            return;
        }
        textField.setText("");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imgInfo", imgProfile);
            jsonObject.put("message", message);
            jsonObject.put("username", Username);
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "sendMessage: 1" + mSocket.emit("chat message", jsonObject));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            JSONObject userId = new JSONObject();
            try {
                userId.put("username", Username);
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.disconnect();
            mSocket.off("chat message", onNewMessage);
            mSocket.off("connect user", onNewUser);
            mSocket.off("on typing", onTyping);
            mSocket.off("img", onImg);
            mSocket.off("audio", onAudio);
            Username = "";
        } else {
            Log.i(TAG, "onDestroy: is rotating.....");
        }
    }

    private void showListUser() {
        users = new ArrayList<User>();
        rclViewUser.setHasFixedSize(true);
        userAdapter = new UserAdapter(users, MainActivity.this);
        final LinearLayoutManager linearLayoutUser = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rclViewUser.setLayoutManager(linearLayoutUser);
        rclViewUser.setAdapter(userAdapter);
    }

    private void showListMessage() {
        messageFormats = new ArrayList<Messenger>();
        rclView1.setHasFixedSize(true);
        contentImageAdapter = new ContentImageAdapter(messageFormats, MainActivity.this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rclView1.setLayoutManager(linearLayoutManager);
        rclView1.setAdapter(contentImageAdapter);
        contentImageAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            MaterialDialog mDialog = new MaterialDialog.Builder(this)
                    .setAnimation(R.raw.salad_cat)
                    .setTitle("You want to exit !!")
                    .setMessage("Are you sure about that !")
                    .setCancelable(false)
                    .setPositiveButton("Yes", R.drawable.ic_baseline_exit_to_app_24, (dialogInterface, which) -> {
                        onDestroy();
                        finish();
                    })
                    .setNegativeButton("No", R.drawable.ic_baseline_close_24, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            mDialog.show();
        } else if (item.getItemId() == R.id.aboutUs) {
            Context context = getApplicationContext();
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            String myVersionName = "not available";
            try {
                myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            MaterialDialog mDialog = new MaterialDialog.Builder(this)
                    .setAnimation(R.raw.bubel_chat)
                    .setTitle("Alo chat")
                    .setMessage("Version : " + myVersionName)
                    .setCancelable(false)
                    .setPositiveButton("Ok", R.drawable.like, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            mDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}