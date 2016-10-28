package com.devin.client.shellapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.devin.client.shellapp.context.ApplicationContext;

/**
 * 项目名称：ShellApp
 * 类描述：网络相关的工具类，判断网络是否可用，wifi，数据上网开关等
 * 创建人：LHM
 * 创建时间：2016/10/28 4:13
 * 修改人：LHM
 * 修改时间：2016/10/28 4:13
 * 修改备注：
 */

public class NetworkUtils {
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_WIFI = 2;
    public static final int NETWORK_TYPE_CMWAP = 3;
    public static final int NETWORK_TYPE_CMNET = 4;
    public static final int NETWORK_TYPE_CTNET = 5;
    public static final int NETWORK_TYPE_CTWAP = 6;
    public static final int NETWORK_TYPE_3GWAP = 7;
    public static final int NETWORK_TYPE_3GNET = 8;
    public static final int NETWORK_TYPE_UNIWAP = 9;
    public static final int NETWORK_TYPE_UNINET = 10;

    private Context context;
    private ConnectivityManager connManager;

    public NetworkUtils(Context context) {
        this.context = context;
        connManager = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    /**
     * 网络是否连接可用
     *
     * @return
     */
    public boolean isNetworkConnected() {

        if (connManager == null) {
            connManager = (ConnectivityManager) ApplicationContext.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (connManager != null) {
            final NetworkInfo networkinfo = connManager.getActiveNetworkInfo();

            if (networkinfo != null) {
                return networkinfo.isConnected();
            }
        } else {
            return true;
        }

        return false;
    }
}
