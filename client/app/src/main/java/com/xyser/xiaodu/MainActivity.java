package com.xyser.xiaodu;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.umeng.analytics.MobclickAgent;
import com.xyser.xiaodu.adatper.ChatMessageAdatper;
import com.xyser.xiaodu.api.BaiduApi;
import com.xyser.xiaodu.api.TulingRobot;
import com.xyser.xiaodu.bean.BaiduApiReturn;
import com.xyser.xiaodu.bean.ChatMessage;
import com.xyser.xiaodu.unit.Constant;
import com.xyser.xiaodu.unit.SystemServiceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listView;
    private ChatMessageAdatper mAdapter;
    private List<ChatMessage> mDatas;

    private EditText mInputMsg;
    private ImageButton mSendMsg;

    private LinearLayout morePanel;
    private ImageButton btn_add;
    private ImageButton btn_voice;
    private ImageButton btn_keyboard;

    //private SpeechRecognizer speechRecognizer;
    private VoiceRecognitionClient mASREngine;
    private Boolean IsRecognition = false;


    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            // 等待接收，子线程完成数据的返回
            ChatMessage fromMessge = (ChatMessage) msg.obj;
            mDatas.add(fromMessge);
            mAdapter.notifyDataSetChanged();
            listView.setSelection(mDatas.size()-1);
        };

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
        initDatas();
        initListener();

        // 友盟APP统计
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setDebugMode( true );

        // 获取识别器对象
        mASREngine = VoiceRecognitionClient.getInstance(this);
        // 获取授权
        mASREngine.setTokenApis(Constant.EXTRA_KEY, Constant.EXTRA_SECRET);
    }

    private void initDatas() {
        mDatas = new ArrayList<ChatMessage>();
        mDatas.add(new ChatMessage("你好", ChatMessage.Type.INCOMING, new Date()));
        mAdapter = new ChatMessageAdatper(this, mDatas);
        listView.setAdapter(mAdapter);
    }

    private void updateListView(ChatMessage chatMsg)
    {
        mDatas.add(chatMsg);
        mAdapter.notifyDataSetChanged();
        listView.setSelection(mDatas.size()-1);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.id_listview);
        mSendMsg = (ImageButton) findViewById(R.id.id_send_btn);
        mInputMsg = (EditText) findViewById(R.id.id_input_msg);

        morePanel = (LinearLayout) findViewById(R.id.morePanel);
        btn_add = (ImageButton) findViewById(R.id.btn_add);
        btn_voice = (ImageButton) findViewById(R.id.btn_voice);
        btn_keyboard = (ImageButton) findViewById(R.id.btn_keyboard);

        LinearLayout BtnPhoto = (LinearLayout) findViewById(R.id.btn_image);
        BtnPhoto.setOnClickListener(myOnClickListener);
    }

    private void initListener()
    {
        mSendMsg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String toMsg = mInputMsg.getText().toString();
                if (TextUtils.isEmpty(toMsg))
                {
                    Toast.makeText(MainActivity.this, "发送消息不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(IsRecognition) {
                    mASREngine.stopVoiceRecognition();
                    btn_voice.setBackgroundResource(R.drawable.ic_voice);
                    IsRecognition = false;
                }

                ChatMessage toMessage = new ChatMessage();
                toMessage.setDate(new Date());
                toMessage.setMsg(toMsg);
                toMessage.setType(ChatMessage.Type.OUTCOMING);
                mDatas.add(toMessage);
                mAdapter.notifyDataSetChanged();
                listView.setSelection(mDatas.size()-1);

                mInputMsg.setText("");

                new Thread()
                {
                    public void run()
                    {
                        ChatMessage fromMessage = null;
                        try {
                            fromMessage = TulingRobot.sendMessage(toMsg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Message m = Message.obtain();
                        m.obj = fromMessage;
                        mHandler.sendMessage(m);
                    };
                }.start();

            }
        });

        btn_add.setOnClickListener(myOnClickListener);
        btn_voice.setOnClickListener(voiceSwitchKeyboard);
        //btn_keyboard.setOnClickListener(voiceSwitchKeyboard);
        listView.setOnItemLongClickListener(listenerOnLongClickListener);
    }

    private AdapterView.OnItemLongClickListener listenerOnLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            String msg = mDatas.get(position).getMsg();

            // 复制到建剪切板
            SystemServiceManager.setClipboardText(MainActivity.this, msg);
            // 震动
            SystemServiceManager.shortVibrator(MainActivity.this);

            Toast.makeText(getApplicationContext(),
                    "内容已复制到剪切板", Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    private View.OnClickListener voiceSwitchKeyboard = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.btn_voice) {
                Log.i(TAG, "voice: " + IsRecognition.toString());


                Log.i(TAG, "voiceSwitchKeyboard:" + IsRecognition.toString());
                if(IsRecognition) {
                    // 停止响应
                    mASREngine.stopVoiceRecognition();
                    btn_voice.setBackgroundResource(R.drawable.ic_voice);
                    IsRecognition = false;
                } else {
                    // 需要开始新识别,首先设置参数
                    VoiceRecognitionConfig config = new VoiceRecognitionConfig();

                    //开启语义解析
                    config.enableNLU();
                    //开启音量反馈
                    //config.enableVoicePower(true);
                    config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); //设置采样率
                    //使用默认的麦克风作为音频来源
                    config.setUseDefaultAudioSource(true);
                    // 下面发起识别
                    int code = mASREngine.startVoiceRecognition(
                            mListener, config);
                    if (code == VoiceRecognitionClient.START_WORK_RESULT_WORKING)
                    { // 能够开始识别，改变界面
                        Log.i(TAG, "能够开始识别" + code);
                        btn_voice.setBackgroundResource(R.drawable.ic_voice_hover);
                        IsRecognition = true;
                    } else {
                        Log.i(TAG, "启动失败" + code);
                    }
                }
            }
            if(id == R.id.btn_keyboard) {
                Log.i(TAG, "btn_keyboard");
                btn_voice.setVisibility(View.VISIBLE);
                btn_keyboard.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy()
    {
        mASREngine.releaseInstance(); // 释放识别库
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        if (IsRecognition) {
            mASREngine.stopVoiceRecognition(); // 取消识别
            IsRecognition = false;
        }
        super.onPause();
        MobclickAgent.onPause(this);
    }

    //手动增加代码开始
    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //增加自己的代码......
            int id = v.getId();
            if(id == R.id.btn_add) {

                if(morePanel.isShown()) {
                    morePanel.setVisibility(View.GONE);
                } else {
                    morePanel.setVisibility(View.VISIBLE);
                }
                Log.i(TAG, "btn_add Click");
            }
            if (id == R.id.btn_image){
                Log.i(TAG, "图片按钮被单击");

                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            Log.i(TAG, "onActivityResult");
            morePanel.setVisibility(View.GONE);

            ContentResolver cr = this.getContentResolver();

            // base64image
            final String bitString;
            final Bitmap bitmap;
            final String picPath;
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                picPath = data.getStringExtra("photo_path");
                Log.i(TAG, "picPath" + picPath);
                /* 将Bitmap设定到ImageView */
                //ImageView imageView = (ImageView) findViewById(R.id.iv01);
                //imageView.setImageBitmap(bitmap);
                //bitmap = BitmapCompress.compressImage(bitmap);
                //bitString = BitmapBase64.bitmapToBase64(bitmap);
                //Log.i(TAG, bitString);


                ChatMessage fromMessge = new ChatMessage("", ChatMessage.Type.OUTIMAGE, new Date());
                fromMessge.setBitmap(bitmap);
                updateListView(fromMessge);


                // 图片识别
                new Thread()
                {
                    public void run()
                    {
                        ChatMessage fromMessage = null;
                        BaiduApiReturn str = BaiduApi.getOcr(bitmap);
                        if(str.getType() == 200) {
                            try {
                                fromMessage = TulingRobot.sendMessage(str.getData());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (str.getType() == 201) {
                            fromMessage = new ChatMessage(str.getData(), ChatMessage.Type.INCOMING, new Date());
                        } else {
                            fromMessage = new ChatMessage(str.getData(), ChatMessage.Type.INCOMING, new Date());
                        }

                        // 将消息传出
                        Message m = Message.obtain();
                        m.obj = fromMessage;
                        mHandler.sendMessage(m);
                    }
                }.start();

            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 语音识别监听
     */
    private VoiceRecognitionClient.VoiceClientStatusChangeListener mListener=new VoiceRecognitionClient.VoiceClientStatusChangeListener()
    {
        public void onClientStatusChange(int status, Object result) {
            Log.i(TAG, "onClientStatusChange");
            switch (status) {
                // 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                    IsRecognition = true;
                    break;
                // 检测到语音起点
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                    break;
                //这里可以什么都不用作，简单地对传入的数据做下记录
                case VoiceRecognitionClient.CLIENT_STATUS_AUDIO_DATA:
                    break;
                // 已经检测到语音终点，等待网络返回
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                    break;
                // 语音识别完成，显示obj中的结果
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                    UpdateRecognitionResult(result);
                    IsRecognition = false;
                    break;
                // 处理连续上屏
                case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                    UpdateRecognitionResult(result);
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    IsRecognition = false;
                    break;
                default:
                    break;
            }


        }

        /**
         * 更新语音识别结果
         * @param result
         */
        private void UpdateRecognitionResult(Object result) {
            if (result != null && result instanceof List) {
                @SuppressWarnings("rawtypes")
                List results = (List) result;
                if (results.size() > 0) {
                    mInputMsg.setText(results.get(0).toString());
                }
            }
        }

        @Override
        public void onError(int errorType, int errorCode) {
            IsRecognition = false;
            Log.i(TAG, "onError");
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj)
        {
            // 这里不做任何操作不影响简单识别
            Log.i(TAG, "onNetworkStatusChange");
        }
    };
}
