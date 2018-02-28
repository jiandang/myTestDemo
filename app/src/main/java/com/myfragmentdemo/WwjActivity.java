package com.myfragmentdemo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myfragmentdemo.base.ZegoApiManager;
import com.myfragmentdemo.entity.WwjRoom;
import com.myfragmentdemo.entity.ZegoStream;
import com.myfragmentdemo.utils.BoardState;
import com.myfragmentdemo.utils.CommandUtil;
import com.myfragmentdemo.utils.PreferenceUtil;
import com.myfragmentdemo.utils.WindowUtils;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;
import com.zego.zegoliveroom.entity.ZegoUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：NM-127
 * 创建时间：2017/11/27 18:47
 */

public class WwjActivity extends Activity implements View.OnClickListener {
    static final private String ROOM1_ID = "WWJ_ZEGO_00d1f40f4a56";
    static final private String ROOM1_STREAM1_ID = "WWJ_ZEGO_STREAM_00d1f40f4a56";
    static final private String ROOM1_STREAM2_ID = "WWJ_ZEGO_STREAM_00d1f40f4a56_2";
    private WwjRoom mWwjRoom;//房间信息
    /**
     * 流状态.
     */
    private TextView mTvStreamSate;

    /**
     * 切换摄像头.
     */
    private ImageButton mIbtnSwitchCamera;

    /**
     * 操作.
     */
    private ImageButton mIBtnLeft;
    private ImageButton mIBtnForward;
    private ImageButton mIBtnBackward;
    private ImageButton mIBtnRight;
    private ImageButton mIBtnGO;

    /**
     * 预约按钮.
     */
    private ImageButton mIBtnApply;
    private TextView mTvApply;

    /**
     * 抓娃娃操作面板.
     */
    private RelativeLayout mRlytControlPannel;

    /**
     * 上机倒计时.
     */
    private TextView mTvBoardingCountDown;

    /**
     * 切换摄像头次数, 用于记录当前正在显示"哪一条流"
     */
    private int mSwitchCameraTimes = 0;

    private List<ZegoStream> mListStream = new ArrayList<>();

    /**
     * 当前排队人数.
     */
    private int mCurrentQueueCount = 0;

    /**
     * "确认上机"计时器.
     */
    private CountDownTimer mCountDownTimerConfirmBoard;

    /**
     * "上机操作"计时器.
     */
    private CountDownTimer mCountDownTimerBoarding;

    /**
     * 网络质量.
     */
    private ImageView mIvQuality;
    private TextView mTvQuality;

    /**
     * 房间人数.
     */
    private TextView mTvRoomUserCount;

    /**
     * "确认上机"对话框.
     */
    private AlertDialog mDialogConfirmGameReady;

    private ZegoLiveRoom mZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initData();
        initViews();
        startPlay();
    }

    private void initData() {
        mWwjRoom = new WwjRoom();
        mWwjRoom.roomName = "娃娃机1";
        mWwjRoom.roomID = ROOM1_ID;
        mWwjRoom.streamList.add(ROOM1_STREAM1_ID);
        mWwjRoom.streamList.add(ROOM1_STREAM2_ID);
        mWwjRoom.publishStreamID = "zego_wawaji_room1_publish-" + PreferenceUtil.getInstance().getUserID();
        initStreamList();
    }

    private ZegoStream constructStream(int index, String streamID) {
        String sateStrings[];
        TextureView textureView;
        if (index == 0) {
            sateStrings = getResources().getStringArray(R.array.video1_state);
            textureView = (TextureView) findViewById(R.id.textureview1);
        } else {
            sateStrings = getResources().getStringArray(R.array.video2_state);
            textureView = (TextureView) findViewById(R.id.textureview2);
        }
        return new ZegoStream(streamID, textureView, sateStrings);
    }

    /**
     * 初始化流信息. 当前, 一个房间内只需要2条流用于播放，少了用"空流"替代.
     */
    private void initStreamList() {
        int streamSize = mWwjRoom.streamList.size() >= 2 ? 2 : mWwjRoom.streamList.size();
        for (int index = 0; index < streamSize; index++) {
            mListStream.add(constructStream(index, mWwjRoom.streamList.get(index)));
        }
        if (mListStream.size() < 2) {
            for (int index = mListStream.size(); index < 2; index++) {
                mListStream.add(constructStream(index, null));
            }
        }
    }

    private void initViews() {
        mTvBoardingCountDown = (TextView) findViewById(R.id.tv_boarding_countdown);
        mTvStreamSate = (TextView) findViewById(R.id.tv_stream_state);
        mTvStreamSate.setText(mListStream.get(0).getStateString());

        mRlytControlPannel = (RelativeLayout) findViewById(R.id.rlyt_control_pannel);

        mTvApply = (TextView) findViewById(R.id.tv_apply);
        mTvApply.setText(getString(R.string.apply_grub));

        mIBtnApply = (ImageButton) findViewById(R.id.ibtn_apply);//预约抓娃娃
        mIBtnApply.setEnabled(false);
        mIBtnApply.setOnClickListener(this);//开始预约

        mIBtnGO = (ImageButton) findViewById(R.id.ibtn_go);
        mIBtnGO.setOnClickListener(this);//抓

        mIbtnSwitchCamera = (ImageButton) findViewById(R.id.ibtn_switch_camera);
        mIbtnSwitchCamera.setEnabled(false);
        mIbtnSwitchCamera.setOnClickListener(this);//调转镜头

        mIBtnLeft = (ImageButton) findViewById(R.id.ibtn_left);
        mIBtnLeft.setOnClickListener(this);

        mIBtnForward = (ImageButton) findViewById(R.id.ibtn_forward);
        mIBtnForward.setOnClickListener(this);

        mIBtnBackward = (ImageButton) findViewById(R.id.ibtn_backward);
        mIBtnBackward.setOnClickListener(this);

        mIBtnRight = (ImageButton) findViewById(R.id.ibtn_right);
        mIBtnRight.setOnClickListener(this);
        setControlPannel(false);

        mIvQuality = (ImageView) findViewById(R.id.iv_quality);
        mTvQuality = (TextView) findViewById(R.id.tv_quality);
        mTvRoomUserCount = (TextView) findViewById(R.id.tv_room_user_count);
        initMWindows();
    }
    private Button mFloatingButton;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private void initMWindows(){
//        mFloatingButton = new Button(this);
//        mFloatingButton.setText("chuangkou");
//        mLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,0,0, PixelFormat.TRANSPARENT);
//        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
//        mLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
//        mLayoutParams.x = 100;
//        mLayoutParams.y = 300;
//        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        mWindowManager.addView(mFloatingButton,mLayoutParams);
        WindowUtils.showPopupWindow(getApplicationContext());
    }
    private void setControlPannel(boolean enable) {
        if (enable) {
            mRlytControlPannel.setVisibility(View.VISIBLE);
        } else {
            mRlytControlPannel.setVisibility(View.INVISIBLE);
        }

        mIBtnLeft.setEnabled(enable);
        mIBtnForward.setEnabled(enable);
        mIBtnRight.setEnabled(enable);
        mIBtnBackward.setEnabled(enable);
        mIBtnGO.setEnabled(enable);
        mTvBoardingCountDown.setText("");
    }

    private void pauseControlPannel() {
        mIBtnLeft.setEnabled(false);
        mIBtnForward.setEnabled(false);
        mIBtnRight.setEnabled(false);
        mIBtnBackward.setEnabled(false);
        mIBtnGO.setEnabled(false);
        mTvBoardingCountDown.setText("");
    }

    private void sendCMDFail(String cmd) {
        CommandUtil.getInstance().printLog("send cmd error: " + cmd);
        Toast.makeText(this, getString(R.string.send_cmd_error), Toast.LENGTH_SHORT).show();
        reinitGame();
    }

    private void reinitGame() {
        CommandUtil.getInstance().setCurrentBoardSate(BoardState.Ended);
        setControlPannel(false);
        mIBtnApply.setEnabled(true);
        String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, mCurrentQueueCount + "");
        showApplyText(msg, 6);
        mIBtnApply.setVisibility(View.VISIBLE);
        mTvApply.setVisibility(View.VISIBLE);
    }

    private void showApplyText(String msg, int separatePostion) {
        SpannableString ss = new SpannableString(msg);
        ss.setSpan(new TextAppearanceSpan(this, R.style.tv_style1), 0, separatePostion,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TextAppearanceSpan(this, R.style.tv_style2), separatePostion,
                msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvApply.setText(ss, TextView.BufferType.SPANNABLE);
    }

    private void startPlay() {
        mZegoLiveRoom.loginRoom(mWwjRoom.roomID, ZegoConstants.RoomRole.Audience, new IZegoLoginCompletionCallback() {
            @Override
            public void onLoginCompletion(int errCode, ZegoStreamInfo[] zegoStreamInfos) {
                CommandUtil.getInstance().printLog("[onLoginCompletion], roomID: " + mWwjRoom.roomID + ", errorCode: " + errCode + ", streamCount: " + zegoStreamInfos.length);
                if (errCode == 0) {
                    for (ZegoStreamInfo streamInfo : zegoStreamInfos) {
                        if (!TextUtils.isEmpty(streamInfo.extraInfo)) {
                            CommandUtil.getInstance().printLog("[onLoginCompletion], streamID: " + streamInfo.streamID + ", extraInfo: " + streamInfo.extraInfo);

                            ZegoUser zegoUser = new ZegoUser();
                            zegoUser.userID = streamInfo.userID;
                            zegoUser.userName = streamInfo.userName;
                            CommandUtil.getInstance().setAnchor(zegoUser);

                            mIBtnApply.setEnabled(true);

                            Map<String, Object> map = getMapFromJson(streamInfo.extraInfo);
                            if (map != null) {
                                int count = ((Double) map.get("queue_number")).intValue();
                                mCurrentQueueCount = count;

                                String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, count + "");
                                showApplyText(msg, 6);

                                int total = ((Double) map.get("total")).intValue();
                                mTvRoomUserCount.setText(getString(R.string.room_user_count, total + ""));
                                mTvRoomUserCount.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    mIbtnSwitchCamera.setEnabled(true);
                }
            }
        });

        // 拉两路流
        mListStream.get(0).playStream(100);
        mListStream.get(1).playStream(0);

        mZegoLiveRoom.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {
            @Override
            public void onPlayStateUpdate(int errCode, String streamID) {

                int currentShowIndex = mSwitchCameraTimes % 2;

                if (errCode != 0) {
                    // 设置流的状态
                    for (ZegoStream zegoStream : mListStream) {
                        if (zegoStream.getStreamID().equals(streamID)) {
                            zegoStream.setStreamSate(ZegoStream.StreamState.PlayFail);
                            break;
                        }
                    }

                    ZegoStream currentShowStream = mListStream.get(currentShowIndex);
                    if (currentShowStream.getStreamID().equals(streamID)) {
                        mTvStreamSate.setText(currentShowStream.getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                }
                CommandUtil.getInstance().printLog("[onPlayStateUpdate], streamID: " + streamID + " ,errorCode: " + errCode + ", currentShowIndex: " + currentShowIndex);
            }

            @Override
            public void onPlayQualityUpdate(String streamID, ZegoStreamQuality zegoStreamQuality) {
                // 当前显示的流质量
                if (mListStream.get(mSwitchCameraTimes % 2).getStreamID().equals(streamID)) {
                    switch (zegoStreamQuality.quality) {
                        case 0:
                            mTvQuality.setText("网络优秀");
                            mIvQuality.setImageResource(R.mipmap.excellent);
                            mTvQuality.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                            break;
                        case 1:
                            mTvQuality.setText("网络流畅");
                            mIvQuality.setImageResource(R.mipmap.good);
                            mTvQuality.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                            break;
                        case 2:
                            mTvQuality.setText("网络缓慢");
                            mIvQuality.setImageResource(R.mipmap.average);
                            mTvQuality.setTextColor(getResources().getColor(R.color.bg_yellow_p));
                            break;
                        case 3:
                            mTvQuality.setText("网络拥堵");
                            mIvQuality.setImageResource(R.mipmap.pool);
                            mTvQuality.setTextColor(getResources().getColor(R.color.text_red));
                            break;
                    }
                    mIvQuality.setVisibility(View.VISIBLE);
                    mTvQuality.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onInviteJoinLiveRequest(int i, String s, String s1, String s2) {

            }

            @Override
            public void onRecvEndJoinLiveCommand(String s, String s1, String s2) {

            }

            @Override
            public void onVideoSizeChangedTo(String streamID, int i, int i1) {
                for (ZegoStream zegoStream : mListStream) {
                    if (zegoStream.getStreamID().equals(streamID)) {
                        zegoStream.setStreamSate(ZegoStream.StreamState.PlaySuccess);
                        break;
                    }
                }

                int currentShowIndex = mSwitchCameraTimes % 2;
                ZegoStream currentShowStream = mListStream.get(currentShowIndex);
                if (currentShowStream.getStreamID().equals(streamID)) {
                    mTvStreamSate.setVisibility(View.GONE);
                    currentShowStream.show();
                }

                CommandUtil.getInstance().printLog("[onVideoSizeChanged], streamID: " + streamID + ", currentShowIndex: " + currentShowIndex);
            }
        });

        mZegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {
            @Override
            public void onPublishStateUpdate(int errCode, String streamID, HashMap<String, Object> hashMap) {
                CommandUtil.getInstance().printLog("[onPublishStateUpdate], streamID: " + streamID + ", errorCode: " + errCode);
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {

            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {

            }

            @Override
            public AuxData onAuxCallback(int i) {
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int i, int i1) {

            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {

            }
        });

        mZegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {
            @Override
            public void onKickOut(int reason, String roomID) {

            }

            @Override
            public void onDisconnect(int errorCode, String roomID) {
            }

            @Override
            public void onReconnect(int i, String s) {

            }

            @Override
            public void onTempBroken(int i, String s) {

            }

            @Override
            public void onStreamUpdated(final int type, final ZegoStreamInfo[] listStream, final String roomID) {
            }

            @Override
            public void onStreamExtraInfoUpdated(ZegoStreamInfo[] zegoStreamInfos, String s) {

            }

            @Override
            public void onRecvCustomCommand(String userID, String userName, String content, String roomID) {
                // 只接收当前房间，当前主播发来的消息
                if (CommandUtil.getInstance().isCommandFromAnchor(userID) && mWwjRoom.roomID.equals(roomID)) {
                    if (!TextUtils.isEmpty(content)) {
                        handleRecvCustomCMD(content);
                    }
                }
            }
        });
    }

    /**
     * 处理来自服务器的消息.
     */
    private void handleRecvCustomCMD(String msg) {
        CommandUtil.getInstance().printLog("[handleRecvCustomCMD], " + msg);

        Map<String, Object> map = getMapFromJson(msg);
        if (map == null) {
            CommandUtil.getInstance().printLog("[handleRecvCustomCMD], map is null");
            return;
        }

        int cmd = ((Double) map.get("cmd")).intValue();
        int rspSeq = ((Double) map.get("seq")).intValue();
        Map<String, Object> data = (Map<String, Object>) map.get("data");
        switch (cmd) {
            case CommandUtil.CMD_APPLY_RESULT:
                handleApplyResult(data);
                break;
            case CommandUtil.CMD_GAME_READY:
                handleGameReady(rspSeq, data);
                break;
            case CommandUtil.CMD_CONFIRM_BOARD_REPLY:
                handleConfirmBoardReply(data);
                break;
            case CommandUtil.CMD_GAME_RESULT:
                handleGameResult(rspSeq, data);
                break;
            case CommandUtil.CMD_USER_UPDATE:
                handleUserCountUpdate(data);
                break;
        }
    }

    /**
     * "预约上机"结果.
     */
    private void handleApplyResult(Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleApplyResult], data is null");
            return;
        }

        CommandUtil.getInstance().printLog("[handleApplyResult], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.Applying) {
            CommandUtil.getInstance().printLog("[handleApplyResult], state mismatch");
            return;
        }

        // 校验seq
        int rspSeq = ((Double) data.get("seq")).intValue();
        if (CommandUtil.getInstance().getCurrentSeq() != rspSeq) {
            CommandUtil.getInstance().printLog("[handleApplyResult], seq mismatch, rspSeq: " + rspSeq + ", currentSeq: " + CommandUtil.getInstance().getCurrentSeq());
            return;
        }

        int result = ((Double) data.get("result")).intValue();
        if (result == 0) {
            CommandUtil.getInstance().setCurrentBoardSate(BoardState.WaitingBoard);
            String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.apply_success, ((Double) data.get("index")).intValue() + "");
            showApplyText(msg, 6);
        } else {
            Toast.makeText(this, getString(R.string.apply_faile), Toast.LENGTH_SHORT).show();
            reinitGame();
        }
    }

    /**
     * 准备游戏.
     */
    private void handleGameReady(final int rspSeq, Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleGameReady], data is null");
            return;
        }

        CommandUtil.getInstance().printLog("[handleGameReady], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Applying) {
            CommandUtil.getInstance().printLog("[handleGameReady], fix state");

            CommandUtil.getInstance().setCurrentBoardSate(BoardState.WaitingBoard);
        }

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.WaitingBoard) {
            CommandUtil.getInstance().printLog("[handleGameReady], state mismatch");
            return;
        }

        final String sessionData = (String) data.get("session_data");

        // 通知服务器，客户端已经收到GameReady指令
        CommandUtil.getInstance().replyRecvGameReady(rspSeq, sessionData);

        if (mDialogConfirmGameReady != null && mDialogConfirmGameReady.isShowing()) {
            CommandUtil.getInstance().printLog("[handleGameReady], confirm dialog is showing");
            return;
        }

        mDialogConfirmGameReady = new AlertDialog.Builder(this).setMessage(getString(R.string.confirm_board, "10")).setTitle("提示").setPositiveButton("上机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCountDownTimerConfirmBoard.cancel();

                // 确认上机
                CommandUtil.getInstance().confirmBoard(rspSeq, sessionData, 1, new CommandUtil.OnCommandSendCallback() {
                    @Override
                    public void onSendFail() {
                        sendCMDFail("ConfirmBoard: 1");
                    }
                });
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCountDownTimerConfirmBoard.cancel();

                // 放弃上机
                CommandUtil.getInstance().confirmBoard(rspSeq, sessionData, 0, new CommandUtil.OnCommandSendCallback() {
                    @Override
                    public void onSendFail() {
                        sendCMDFail("ConfirmBoard: 0");
                    }
                });

                reinitGame();
            }
        }).create();
        mDialogConfirmGameReady.setCanceledOnTouchOutside(false);
        mDialogConfirmGameReady.show();

        mCountDownTimerConfirmBoard = new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                    mDialogConfirmGameReady.setMessage(getString(R.string.confirm_board, ((millisUntilFinished / 1000) + 1) + ""));
                }
            }

            @Override
            public void onFinish() {
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                    mDialogConfirmGameReady.dismiss();
                    reinitGame();
                }
            }
        }.start();
    }

    private void handleConfirmBoardReply(Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleConfirmBoardReply], data is null");
            return;
        }

        CommandUtil.getInstance().printLog("[handleConfirmBoardReply], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.ConfirmBoard) {
            CommandUtil.getInstance().printLog("[handleConfirmBoardReply], state mismatch");
            return;
        }

        if (CommandUtil.getInstance().isConfirmBoard()) {

            // 从 CDN 切换到 ZEGO 服务器拉流，降低视频延迟
            switchPlaySource(true);

            // 正在上机
            CommandUtil.getInstance().setCurrentBoardSate(BoardState.Boarding);
            startGame();

            mCountDownTimerBoarding = new CountDownTimer(30000, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                        mTvBoardingCountDown.setVisibility(View.VISIBLE);
                        mTvBoardingCountDown.setText(((millisUntilFinished / 1000) + 1) + "s");
                    }
                }

                @Override
                public void onFinish() {
                    if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                        pauseControlPannel();

                        CommandUtil.getInstance().grub(new CommandUtil.OnCommandSendCallback() {
                            @Override
                            public void onSendFail() {
                                sendCMDFail("Grub");
                            }
                        });
                    }
                }
            }.start();
        }
    }

    private void startGame() {
        mIBtnApply.setVisibility(View.INVISIBLE);
        mTvApply.setVisibility(View.INVISIBLE);
        setControlPannel(true);
    }

    private void handleGameResult(final int rspSeq, Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleGameResult], data is null");
            return;
        }

        Map<String, Object> player = (Map<String, Object>) data.get("player");
        if (player == null) {
            CommandUtil.getInstance().printLog("[handleGameResult], player is null");
            return;
        }

        if (!PreferenceUtil.getInstance().getUserID().equals(player.get("id"))) {
            CommandUtil.getInstance().printLog("[handleGameResult], not my message, don't care");
            return;
        }

        CommandUtil.getInstance().printLog("[handleGameResult], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.WaitingGameResult) {
            String sessionData = (String) data.get("session_data");
            CommandUtil.getInstance().confirmGameResult(rspSeq, sessionData);
            CommandUtil.getInstance().printLog("[handleGameResult], remain handleGameResult from Server!");
            return;
        }

        CommandUtil.getInstance().setCurrentBoardSate(BoardState.Ended);

        int result = ((Double) data.get("result")).intValue();
        String message;
        if (result == 1) {
            message = getString(R.string.grub_successfully);
        } else {
            message = getString(R.string.grub_failed);
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(message).setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        reinitGame();

        // 从 ZEGO 服务器切换到 CDN 拉流，节约成本
        switchPlaySource(false);
    }

    /**
     * 房间人数更新.
     */
    private void handleUserCountUpdate(Map<String, Object> data) {
        CommandUtil.getInstance().printLog("[handleUserCountUpdate]");
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleUserCountUpdate], data is null");
            return;
        }

        ArrayList queueList = (ArrayList) data.get("queue");

        if (queueList != null) {
            mCurrentQueueCount = queueList.size();
            if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Ended) {
                String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, mCurrentQueueCount + "");
                showApplyText(msg, 6);
            } else if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                for (int i = 0, size = queueList.size(); i < size; i++) {
                    if (PreferenceUtil.getInstance().getUserID().equals(((Map<String, Object>) queueList.get(i)).get("userId"))) {
                        String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.apply_success, i + "");
                        showApplyText(msg, 6);
                        break;
                    }
                }
            }
        }

        int total = ((Double) data.get("total")).intValue();
        mTvRoomUserCount.setText(getString(R.string.room_user_count, total + ""));
    }

    private void switchPlaySource(boolean useUltraSource) {

        // 停止推流
        for (ZegoStream zegoStream : mListStream) {
            zegoStream.stopPlayStream();
        }

        String config;
        if (useUltraSource) {
            // 切到zego服务器拉流
            config = ZegoConstants.Config.PREFER_PLAY_ULTRA_SOURCE + "=1";
        } else {
            //切回cdn拉流
            config = ZegoConstants.Config.PREFER_PLAY_ULTRA_SOURCE + "=0";
        }

        ZegoLiveRoom.setConfig(config);

        int currentShowIndex = mSwitchCameraTimes % 2;
        if (currentShowIndex == 0) {
            mListStream.get(0).playStream(100);
            mListStream.get(1).playStream(0);
        } else {
            mListStream.get(0).playStream(0);
            mListStream.get(1).playStream(100);
        }
    }

    private Map<String, Object> getMapFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map = gson.fromJson(json, map.getClass());
        return map;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doLogout();
    }

    private void doLogout() {

        for (ZegoStream zegoStream : mListStream) {
            zegoStream.stopPlayStream();
        }

        mZegoLiveRoom.stopPublishing();
        mZegoLiveRoom.logoutRoom();
        CommandUtil.getInstance().reset();

        if (mCountDownTimerConfirmBoard != null) {
            mCountDownTimerConfirmBoard.cancel();
        }

        if (mCountDownTimerBoarding != null) {
            mCountDownTimerBoarding.cancel();
        }
    }

    private void logout() {
        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.Ended) {
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage("正在游戏中，确定要离开吗？").setTitle("提示").setPositiveButton("离开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    doLogout();
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 退出
            logout();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_apply://开始预约
                mIBtnApply.setEnabled(false);
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Ended) {
                    CommandUtil.getInstance().apply(new CommandUtil.OnCommandSendCallback() {
                        @Override
                        public void onSendFail() {
                            sendCMDFail("Apply");
                        }
                    });
                }
                break;
            case R.id.ibtn_go://抓
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                    mCountDownTimerBoarding.cancel();
                    pauseControlPannel();
                    CommandUtil.getInstance().grub(new CommandUtil.OnCommandSendCallback() {
                        @Override
                        public void onSendFail() {
                            sendCMDFail("Grub");
                        }
                    });
                }
                break;
            case R.id.ibtn_switch_camera://调转镜头
                mSwitchCameraTimes++;

                mTvStreamSate.setVisibility(View.GONE);

                int currentShowIndex = mSwitchCameraTimes % 2;
                if (currentShowIndex == 1) {
                    // 隐藏第一路流
                    mListStream.get(0).hide();

                    // 显示第二路流
                    if (mListStream.get(1).isPlaySuccess()) {
                        mListStream.get(1).show();
                    } else {
                        mTvStreamSate.setText(mListStream.get(1).getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                } else {
                    // 隐藏第二路流
                    mListStream.get(1).hide();

                    // 显示第一路流
                    if (mListStream.get(0).isPlaySuccess()) {
                        mListStream.get(0).show();
                    } else {
                        mTvStreamSate.setText(mListStream.get(0).getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.ibtn_left:
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveLeft();
                } else {
                    CommandUtil.getInstance().moveForward();
                }
                break;
            case R.id.ibtn_forward:
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveForward();
                } else {
                    CommandUtil.getInstance().moveRight();
                }
                break;
            case R.id.ibtn_backward:
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveBackward();
                } else {
                    CommandUtil.getInstance().moveLeft();
                }
                break;
            case R.id.ibtn_right:
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveRight();
                } else {
                    CommandUtil.getInstance().moveBackward();
                }
                break;
            default:
                break;
        }
    }
}
