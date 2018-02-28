package com.myfragmentdemo.base;


import android.text.TextUtils;
import android.widget.Toast;

import com.myfragmentdemo.utils.PreferenceUtil;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;


/**
 * des: zego api管理器.
 */
public class ZegoApiManager {

    private static ZegoApiManager sInstance = null;

    private ZegoLiveRoom mZegoLiveRoom = null;

    private ZegoAvConfig mZegoAvConfig = null;

    private long mAppID = 0;
    private byte[] mSignKey = null;

    private String mUserID = null;

    private ZegoApiManager() {
        mZegoLiveRoom = new ZegoLiveRoom();
    }

    public static ZegoApiManager getInstance() {
        if (sInstance == null) {
            synchronized (ZegoApiManager.class) {
                if (sInstance == null) {
                    sInstance = new ZegoApiManager();
                }
            }
        }
        return sInstance;
    }

    private void initUserInfo(){
        // 初始化用户信息
        mUserID = PreferenceUtil.getInstance().getUserID();
        String userName = PreferenceUtil.getInstance().getUserName();

        if (TextUtils.isEmpty(mUserID) || TextUtils.isEmpty(userName)) {
            long ms = System.currentTimeMillis();
            mUserID = "wawaji_android_" + ms + "_" + (int)(Math.random() * 100);
            userName = mUserID;

            // 保存用户信息
            PreferenceUtil.getInstance().setUserID(mUserID);
            PreferenceUtil.getInstance().setUserName(userName);
        }
        // 必须设置用户信息
        ZegoLiveRoom.setUser(mUserID, userName);
    }


    private void init(long appID, byte[] signKey){

        initUserInfo();

        mAppID = appID;
        mSignKey = signKey;


        // 初始化sdk
        boolean ret = mZegoLiveRoom.initSDK(appID, signKey, ZegoApplication.sApplicationContext);
        if(!ret){
            // sdk初始化失败
            Toast.makeText(ZegoApplication.sApplicationContext, "Zego SDK初始化失败!", Toast.LENGTH_LONG).show();
        } else {
            // 初始化设置级别为"High"
            mZegoAvConfig = new ZegoAvConfig(ZegoAvConfig.Level.High);
            mZegoLiveRoom.setAVConfig(mZegoAvConfig);
            mZegoLiveRoom.setTestEnv(true);
        }
    }

    /**
     * 初始化sdk.
     */
    public void initSDK(){
        long appID = 4044847084L;
        byte[] signKey = {
                (byte)0xb6,(byte)0xdf,(byte)0x2d,(byte)0xe9,(byte)0x52,(byte)0xf7,
                (byte)0xc9,(byte)0x7e,(byte)0x60,(byte)0x8a,(byte)0x18,(byte)0x38,
                (byte)0xdd,(byte)0x50,(byte)0xb9,(byte)0xab,(byte)0x5c,(byte)0x5d,
                (byte)0xbf,(byte)0x66,(byte)0x6d,(byte)0xf8,(byte)0xf7,(byte)0x6d,
                (byte)0x33,(byte)0x40,(byte)0x9e,(byte)0xfa,(byte)0x2f,(byte)0x0f,
                (byte)0xfd,(byte)0xdf
        };

        init(appID, signKey);
    }

    public void releaseSDK() {
        mZegoLiveRoom.unInitSDK();
    }

    public ZegoLiveRoom getZegoLiveRoom() {
        return mZegoLiveRoom;
    }

    public long getAppID(){
        return mAppID;
    }
}
