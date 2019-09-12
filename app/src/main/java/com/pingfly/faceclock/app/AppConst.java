package com.pingfly.faceclock.app;

import com.pingfly.faceclock.util.FileUtils;
import com.pingfly.faceclock.util.LogUtils;

import java.util.UUID;

public class AppConst {

    public static final String TAG = "FACE_CLOCK";
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//日志输出级别

    public static final String REGION = "86";



    /*================== 广播Action begin ==================*/
    //全局数据获取
    public static final String FETCH_COMPLETE = "fetch_complete";
    //好友
    public static final String UPDATE_FRIEND = "update_friend";
    public static final String UPDATE_RED_DOT = "update_red_dot";
    //群组
    public static final String UPDATE_GROUP_NAME = "update_group_name";
    public static final String GROUP_LIST_UPDATE = "group_list_update";
    public static final String UPDATE_GROUP = "update_group";
    public static final String UPDATE_GROUP_MEMBER = "update_group_member";
    public static final String GROUP_DISMISS = "group_dismiss";
    //个人信息
    public static final String CHANGE_INFO_FOR_ME = "change_info_for_me";
    public static final String CHANGE_INFO_FOR_RESET_NAME = "change_info_for_reset_name";
    public static final String CHANGE_INFO_FOR_USER_INFO = "change_info_for_user_info";
    public static final String CHANGE_INFO_FOR_CHANGE_PASSWORD = "change_info_for_change_password";
    //会话
    public static final String UPDATE_CONVERSATIONS = "update_conversations";
    public static final String UPDATE_CURRENT_SESSION = "update_current_session";
    public static final String UPDATE_CURRENT_SESSION_NAME = "update_current_session_name";
    public static final String REFRESH_CURRENT_SESSION = "refresh_current_session";
    public static final String CLOSE_CURRENT_SESSION = "close_current_session";
    //周围蓝牙设备信息
    public static final String UPDATE_BLUETOOTH_DEVICES="update_bluetooth_devices";
    public static final String ALARM_CLOCK_LIST_UPDATE="alarm_clock_list_update";

    public static String BONDED_DEVICE = "BONDED_DEVICE";

    public static final int MESSAGE_TYPE_SEND_TXT=0;
    public static final int MESSAGE_TYPE_RECEIVE_TXT=1;

    public static final int DATA_TYPE_DEVICE_BONDED=2;
    public static final int DATA_TYPE_DEVICE_NEW=3;
    public static final int DATA_TYPE_DEVICE_NEW_HEADER=4;
    public static final int DATA_TYPE_DEVICE_BONDED_HEADER=5;
    public static final int DATA_TYPE_NO_DEVICE_FOUND=6;

    public static final int CONNECT_TYPE_SERVER = 0;
    public static final int CONNECT_TYPE_CLIENT = 1;




    //Start flag
    public static final byte MESSAGE_COMMAND_START_FLAG = (byte) 0xFF;
    //Protocol version
    public static final byte MESSAGE_COMMAND_PROTOCOL_VERSION = (byte) 0x01;

    /*Send Command Type*/
    public static final byte VISE_COMMAND_TYPE_NONE = (byte) 0x00;
    public static final byte VISE_COMMAND_TYPE_TEXT = (byte) 0x01;
    public static final byte VISE_COMMAND_TYPE_FILE = (byte) 0x02;
    public static final byte VISE_COMMAND_TYPE_IMAGE = (byte) 0x03;
    public static final byte VISE_COMMAND_TYPE_AUDIO = (byte) 0x04;
    public static final byte VISE_COMMAND_TYPE_VIDEO = (byte) 0x05;

    /*KEY*/
    public static final String NAME_SECURE = "BluetoothChatSecure";
    public static final String NAME_INSECURE = "BluetoothChatInsecure";

    /*UUID*/
    public static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    /*Message Type*/
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";


    /**
     * SharedPreferences属性信息文件
     */
    public static final String EXTRA_FACECLOCK_SHARE = "extra_faceclock_shared_preferences_file";





    public static final class User {
        public static final String ID = "id";
        public static final String PHONE = "phone";
        //        public static final String ACCOUNT = "account";
        public static final String TOKEN = "token";
    }

    public static final class UserInfo {
        public static final String ID = "id";
        public static final String PHONE = "phone";
        //        public static final String ACCOUNT = "account";
        public static final String TOKEN = "token";
    }



    public static final int LOGIN_FRAGMENT = 0X01;
    public static final int REGISTER_FRAGMENT = 0X02;


    /**
     * 保存的AlarmClock单例
     */
    public static final String ALARM_CLOCK = "alarm_clock";




    /**
     * 默认闹铃小时
     */
    public static final String DEFAULT_ALARM_HOUR = "default_alarm_hour";

    /**
     * 默认闹铃分钟
     */
    public static final String DEFAULT_ALARM_MINUTE = "default_alarm_minute";

    /**
     * 保存的闹钟铃声音量
     */
    public static final String ALARM_VOLUME = "alarm_volume";

    /**
     * 请求的铃声选择类型：0，闹钟；1，计时器
     */
    public static final String RING_REQUEST_TYPE = "ring_request_type";

    public static final String NEW_ALARM_CLOCK_FOR_SELECT_RING = "new_alarm_clock_for_select_ring";

    /**
     * 铃声名
     */
    public static final String RING_NAME = "ring_name";


    /**
     * 铃声地址
     */
    public static final String RING_URL = "ring_url";

    /**
     * 默认铃声地址
     */
    public static final String DEFAULT_RING_URL = "ring_url";

    /**
     * 铃声选择界面位置
     */
    public static final String RING_PAGER = "ring_pager_position";

    /**
     * 位置
     */
    public static final String POSITION = "position";

    /**
     * 类型(1：重命名，2：删除，3：批量删除，4：详情）
     */
    public static final String TYPE = "type";

    /**
     * 新文件Url
     */
    public static final String NEW_URL = "url_new";

    /**
     * 最大录音时常10分钟
     */
    public static final int MAX_RECORD_LENGTH = 1000 * 60 * 10;

    /**
     * 标题
     */
    public static final String TITLE = "title";

    /**
     * 详情
     */
    public static final String DETAIL = "detail";



    /**
     * 无铃声Url标记
     */

    public static final String NO_RING_URL = "no_ring_url";

    /**
     * 时间
     */
    public static final String TIME = "time";

    /**
     * 图片地址
     */
    public static final String IMAGE_URL = "image_url";

    /**
     * 闹钟不响可能原因提示
     */
    public static final String ALARM_CLOCK_EXPLAIN = "alarm_clock_explain";




    //音频存放位置
    public static final String AUDIO_SAVE_DIR = FileUtils.getDir("audio");
    public static final int DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND = 120;

    /**
     * 录音文件存放地址
     */
    public static final String RECORD_SAVE_PATH = "/FaceClock/audio/record";


    //视频存放位置
    public static final String VIDEO_SAVE_DIR = FileUtils.getDir("video");

    //照片(人脸)存放位置
    public static final String PHOTO_SAVE_DIR = FileUtils.getDir("photo");

    //头像保存位置
    public static final String HEADER_SAVE_DIR = FileUtils.getDir("header");

}
