package com.clement.livetools.auth;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 卡密驗證管理器
 * 對接 http://apk.sxkiss.top:8089/AppEn.php
 * 原版: http://ht.xjjvip.cn/AppEn.php
 */
public class AuthManager {

    private static final String APP_ID = "20230903";
    private static final String SERVER_URL = "http://apk.sxkiss.top:8089/AppEn.php";

    private final Context context;

    public AuthManager(Context context) {
        this.context = context;
    }

    /**
     * 获取设备码（IMEI 替代方案）
     */
    public String getDeviceId() {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return "ANDROID_" + (androidId != null ? androidId : "unknown");
    }

    /**
     * 激活卡密
     * @return JSONObject { code, msg, data }
     */
    public JSONObject activate(String cardKey) {
        return request("login", "kami=" + cardKey + "&imei=" + getDeviceId());
    }

    /**
     * 检查登录状态（启动时调用）
     */
    public JSONObject checkLoginStatus() {
        return request("take_login_info", "imei=" + getDeviceId());
    }

    /**
     * 获取卡密信息
     */
    public JSONObject getUserInfo(String cardKey) {
        return request("take_user_info", "kami=" + cardKey);
    }

    /**
     * 获取到期时间
     */
    public JSONObject getExpireTime() {
        return request("take_user_expire_time", "imei=" + getDeviceId());
    }

    /**
     * 解绑卡密
     */
    public JSONObject unbind(String cardKey) {
        return request("unbind", "kami=" + cardKey);
    }

    /**
     * 注销登录
     */
    public JSONObject logout() {
        return request("take_user_exit_login", "imei=" + getDeviceId());
    }

    /**
     * 充值（延长30天）
     */
    public JSONObject recharge(String cardKey) {
        return request("take_user_recharge", "kami=" + cardKey);
    }

    /**
     * 获取软件公告
     */
    public JSONObject getNotice() {
        return request("take_gg_info", "");
    }

    /**
     * 获取软件描述
     */
    public JSONObject getDescription() {
        return request("take_describe_message", "");
    }

    /**
     * 获取版本信息
     */
    public JSONObject getVersionInfo() {
        return request("take_version_code", "");
    }

    /**
     * 通用 HTTP 请求
     */
    private JSONObject request(String act, String extraParams) {
        try {
            String params = "appid=" + APP_ID + "&act=" + act;
            if (extraParams != null && !extraParams.isEmpty()) {
                params += "&" + extraParams;
            }

            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                return new JSONObject(sb.toString());
            } else {
                JSONObject err = new JSONObject();
                err.put("code", -1);
                err.put("msg", "HTTP " + code);
                return err;
            }
        } catch (Exception e) {
            try {
                JSONObject err = new JSONObject();
                err.put("code", -1);
                err.put("msg", "网络错误: " + e.getMessage());
                return err;
            } catch (Exception ex) {
                return new JSONObject();
            }
        }
    }
}