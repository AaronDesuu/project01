package com.fujielectricmeter.blemeter;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DLMS {
    public final static int RANK_HHU = 2;
    public final static int RANK_COM = 3;
    public final static int RANK_PUB = 4;
    public String SERIAL_ID;
    private Long timestamp;
    private final String TAG = DLMS.class.getSimpleName();
    private int seed0;
    private long seed1, seed2, seed3;
    private int mCurrentMeter = 0;

    private Context mContext;

    DLMS(Context context) {
        mContext = context;
    }

    public final static int IST_LOGICAL_NAME = 0;    //1:COSEMLogicalDeviceName
    public final static int IST_SERIAL_NO = (IST_LOGICAL_NAME + 1);    //2:ID番号
    public final static int IST_EVENT_CODE = (IST_SERIAL_NO + 1);    //3:イベントコード
    public final static int IST_FAULT_MAX = (IST_EVENT_CODE + 1);    //4:相互接続認証エラー上限回数
    public final static int IST_PRODUCT_ID = (IST_FAULT_MAX + 1);    //5:計器型式
    public final static int IST_PHASE_LINE = (IST_PRODUCT_ID + 1);    //6:相線式種別
    public final static int IST_NUM_AMPR_RATIO = (IST_PHASE_LINE + 1);    //7:変流比(分子)
    public final static int IST_NUM_VOLT_RATIO = (IST_NUM_AMPR_RATIO + 1);    //8:変圧比(分子)
    public final static int IST_NUM_TRANS_RATIO = (IST_NUM_VOLT_RATIO + 1);    //9:変成比(分子)
    public final static int IST_DEN_AMPR_RATIO = (IST_NUM_TRANS_RATIO + 1);    //10:変流比(分母)
    public final static int IST_DEN_VOLT_RATIO = (IST_DEN_AMPR_RATIO + 1);    //11:変圧比(分母)
    public final static int IST_DEN_TRANS_RATIO = (IST_DEN_VOLT_RATIO + 1);    //12:変成比(分母)
    public final static int IST_TIME_NOW = (IST_DEN_TRANS_RATIO + 1);    //13:現在時刻
    public final static int IST_DATE_NOW = (IST_TIME_NOW + 1);    //14:現在月日
    public final static int IST_DIGIT = (IST_DATE_NOW + 1);    //15:計器桁数
    public final static int IST_FUNCTION = (IST_DIGIT + 1);    //16:計器機能
    public final static int IST_VERSION = (IST_FUNCTION + 1);    //17:仕様書対応改版数
    public final static int IST_TRANS_RATIO = (IST_VERSION + 1);    //18:乗率
    public final static int IST_TRANS_RATIO_TYPE = (IST_TRANS_RATIO + 1);    //19:乗率方式
    public final static int IST_ENABLE_EVENT = (IST_TRANS_RATIO_TYPE + 1);    //20:イベントの記録有効/無効
    public final static int IST_ENABLE_EXTRA = (IST_ENABLE_EVENT + 1);    //21:イベントコードの拡張設定有効/無効
    public final static int IST_ENABLE_DETAIL = (IST_ENABLE_EXTRA + 1);    //22:各イベントの詳細記録設定有効/無効
    public final static int IST_ENABLE_DISPLAY = (IST_ENABLE_DETAIL + 1);    //23:その他表示有効/無効
    public final static int IST_DISPLAY_VALUE = (IST_ENABLE_DISPLAY + 1);    //24:その他表示値
    public final static int IST_ENABLE_FLICKER = (IST_DISPLAY_VALUE + 1);    //25:画面フリッカ有効/無効
    public final static int IST_FLICKER_STATE = (IST_ENABLE_FLICKER + 1);    //26:画面フリッカ状態
    public final static int IST_BREAKER = (IST_FLICKER_STATE + 1);    //27:開閉区分
    public final static int IST_LIMIT_STD = (IST_BREAKER + 1);    //28:負荷制限(基本設定)
    public final static int IST_LIMIT_TMP = (IST_LIMIT_STD + 1);    //29:負荷制限(臨時設定)
    public final static int IST_LIMIT_CUR = (IST_LIMIT_TMP + 1);    //30:負荷制限(動作設定値)
    public final static int IST_LIMIT_RSV = (IST_LIMIT_CUR + 1);    //31:負荷制限予約
    public final static int IST_BREAKER_COUNT = (IST_LIMIT_RSV + 1);    //32:開閉器動作回数
    public final static int IST_SET_ACT_TIME = (IST_BREAKER_COUNT + 1);    //33:通電開始時刻設定
    public final static int IST_SET_ACT_SINGLE = (IST_SET_ACT_TIME + 1);    //34:個別通電設定
    public final static int IST_SET_ACT_MULTI = (IST_SET_ACT_SINGLE + 1);    //35:多段通電設定
    public final static int IST_APPROVAL_MODE = (IST_SET_ACT_MULTI + 1);    //36:検定モード，画面表示切替
    public final static int IST_EVENT_NO = (IST_APPROVAL_MODE + 1);    //37:イベントデータレコード番号
    public final static int IST_ACTIVE30_NO = (IST_EVENT_NO + 1);    //38:有効電力量30分値レコード番号
    public final static int IST_VOLT30_NO = (IST_ACTIVE30_NO + 1);    //39:平均電圧30分値レコード番号
    public final static int IST_BREAKER_NO = (IST_VOLT30_NO + 1);    //40:開閉器動作履歴レコード番号
    public final static int IST_REACTIVE30_NO = (IST_BREAKER_NO + 1);    //41:無効電力量30分値レコード番号
    public final static int IST_VOLT01_NO = (IST_REACTIVE30_NO + 1);    //42:平均電圧1分値レコード番号
    public final static int IST_SURVEY_WEEK_NO = (IST_VOLT01_NO + 1);    //43:24時間統計データレコード番号
    public final static int IST_SURVEY_MONTH_NO = (IST_SURVEY_WEEK_NO + 1);    //44:月間統計データレコード番号
    public final static int IST_SURVEY_YEAR_NO = (IST_SURVEY_MONTH_NO + 1);    //45:年間統計データレコード番号
    public final static int IST_FAULT_LOCK = (IST_SURVEY_YEAR_NO + 1);    //46:相互接続認証エラー通信ロック時間
    public final static int IST_SPEC_VOLT = (IST_FAULT_LOCK + 1);    //47:定格電圧
    public final static int IST_SPEC_AMPR = (IST_SPEC_VOLT + 1);    //48:定格電流
    public final static int IST_FWD_ENERGY = (IST_SPEC_AMPR + 1);    //49:有効電力量(順潮流)
    public final static int IST_FWD_ENERGY_CENT = (IST_FWD_ENERGY + 1);    //50:有効電力量(順潮流)1/100
    public final static int IST_FWD_SURVEY = (IST_FWD_ENERGY_CENT + 1);    //51:有効電力量(順潮流)ロードサーベイ値
    public final static int IST_FWD_POWER = (IST_FWD_SURVEY + 1);    //52:平均有効電力(順潮流)
    public final static int IST_AVE_FWD_SURVEY = (IST_FWD_POWER + 1);    //53:24時間平均有効電力量(順潮流)
    public final static int IST_AVE_AVE_FWD_SURVEY = (IST_AVE_FWD_SURVEY + 1);    //54:週間平均有効電力量(順潮流)平均値
    public final static int IST_AVE_MAX_FWD_SURVEY = (IST_AVE_AVE_FWD_SURVEY + 1);    //55:週間最大有効電力量(順潮流)平均値
    public final static int IST_BAK_ENERGY = (IST_AVE_MAX_FWD_SURVEY + 1);    //56:有効電力量(逆潮流)
    public final static int IST_BAK_ENERGY_CENT = (IST_BAK_ENERGY + 1);    //57:有効電力量(逆潮流)1/100
    public final static int IST_BAK_SURVEY = (IST_BAK_ENERGY_CENT + 1);    //58:有効電力量(逆潮流)ロードサーベイ値
    public final static int IST_BAK_POWER = (IST_BAK_SURVEY + 1);    //59:平均有効電力(逆潮流)
    public final static int IST_AVE_BAK_SURVEY = (IST_BAK_POWER + 1);    //60:24時間平均有効電力量(逆潮流)
    public final static int IST_AVE_AVE_BAK_SURVEY = (IST_AVE_BAK_SURVEY + 1);    //61:週間平均有効電力量(逆潮流)平均値
    public final static int IST_AVE_MAX_BAK_SURVEY = (IST_AVE_AVE_BAK_SURVEY + 1);    //62:週間最大有効電力量(逆潮流)平均値
    public final static int IST_REACTIVE_L = (IST_AVE_MAX_BAK_SURVEY + 1);    //63:無効電力量(遅れ)
    public final static int IST_REACTIVE_C = (IST_REACTIVE_L + 1);    //64:無効電力量(進み)
    public final static int IST_AMPR1 = (IST_REACTIVE_C + 1);    //65:平均電流値(L1)
    public final static int IST_VOLT1_30 = (IST_AMPR1 + 1);    //66:30分平均電圧値(L1)
    public final static int IST_VOLT1_01 = (IST_VOLT1_30 + 1);    //67:1分平均電圧値(L1)
    public final static int IST_VOLT1 = (IST_VOLT1_01 + 1);    //68:平均電圧値(L1)
    public final static int IST_AMPR3 = (IST_VOLT1 + 1);    //69:平均電流値(L3)
    public final static int IST_VOLT3_30 = (IST_AMPR3 + 1);    //70:30分平均電圧値(L3)
    public final static int IST_VOLT3_01 = (IST_VOLT3_30 + 1);    //71:1分平均電圧値(L3)
    public final static int IST_VOLT3 = (IST_VOLT3_01 + 1);    //72:平均電圧値(L3)
    public final static int IST_COMBINE_AMPR13 = (IST_VOLT3 + 1);    //73:平均合成電流(L1+L3)
    public final static int IST_FWD_DISPLAY = (IST_COMBINE_AMPR13 + 1);    //74:順潮流電力量表示時間
    public final static int IST_BAK_DISPLAY = (IST_FWD_DISPLAY + 1);    //75:逆潮流電力量表示時間
    public final static int IST_IDLE_TIME = (IST_BAK_DISPLAY + 1);    //76:通信部未要求時間
    public final static int IST_OFFLINE_TIME = (IST_IDLE_TIME + 1);    //77:通信部供給電源切断時間
    public final static int IST_MAX_FWD_SURVEY = (IST_OFFLINE_TIME + 1);    //78:24時間最大有効電力量(順潮流)
    public final static int IST_MAX_FWD_SURVEY_MONTH = (IST_MAX_FWD_SURVEY + 1);    //79:月間最大有効電力量(順潮流)
    public final static int IST_MAX_FWD_SURVEY_YEAR = (IST_MAX_FWD_SURVEY_MONTH + 1);    //80:年間最大有効電力量(順潮流)
    public final static int IST_MAX_BAK_SURVEY = (IST_MAX_FWD_SURVEY_YEAR + 1);    //81:24時間最大有効電力量(逆潮流)
    public final static int IST_MAX_BAK_SURVEY_MONTH = (IST_MAX_BAK_SURVEY + 1);    //82:月間最大有効電力量(逆潮流)
    public final static int IST_MAX_BAK_SURVEY_YEAR = (IST_MAX_BAK_SURVEY_MONTH + 1);    //83:年間最大有効電力量(逆潮流)
    public final static int IST_EVENT_RECORD = (IST_MAX_BAK_SURVEY_YEAR + 1);    //84:イベントデータ
    public final static int IST_BREAKER_RECORD = (IST_EVENT_RECORD + 1);    //85:開閉器動作履歴
    public final static int IST_ACTIVE30_RECORD = (IST_BREAKER_RECORD + 1);    //86:有効電力量30分値
    public final static int IST_VOLT30_RECORD = (IST_ACTIVE30_RECORD + 1);    //87:平均電圧30分値
    public final static int IST_REACTIVE30_RECORD = (IST_VOLT30_RECORD + 1);    //88:無効電力量30分値
    public final static int IST_VOLT01_RECORD = (IST_REACTIVE30_RECORD + 1);    //89:平均電圧1分値
    public final static int IST_SURVEY_WEEK_RECORD = (IST_VOLT01_RECORD + 1);    //90:24時間統計データ
    public final static int IST_SURVEY_MONTH_RECORD = (IST_SURVEY_WEEK_RECORD + 1);    //91:月間統計データ
    public final static int IST_SURVEY_YEAR_RECORD = (IST_SURVEY_MONTH_RECORD + 1);    //92:年間統計データ
    public final static int IST_SPECIFICATION = (IST_SURVEY_YEAR_RECORD + 1);    //93:計器諸元
    public final static int IST_CHECK_SETTING = (IST_SPECIFICATION + 1);    //94:設定値一括確認
    public final static int IST_CHECK_STATE = (IST_CHECK_SETTING + 1);    //95:計器状態確認
    public final static int IST_CHECK_DEMAND = (IST_CHECK_STATE + 1);    //96:負荷制限確認
    public final static int IST_CHECK_ACT_TIME = (IST_CHECK_DEMAND + 1);    //97:通電開始時刻確認
    public final static int IST_CHECK_ACT_SINGLE = (IST_CHECK_ACT_TIME + 1);    //98:個別通電確認
    public final static int IST_CONF_MEASURE = (IST_CHECK_ACT_SINGLE + 1);    //99:現在値検針
    public final static int IST_CHECK_MEASURE = (IST_CONF_MEASURE + 1);    //100:現在値確認
    public final static int IST_CHECK_BREAKER = (IST_CHECK_MEASURE + 1);    //101:開閉器状態確認
    public final static int IST_AVE_SURVEY = (IST_CHECK_BREAKER + 1);    //102:週間詳細データ
    public final static int IST_DATETIME_NOW = (IST_AVE_SURVEY + 1);    //103:現在日時
    public final static int IST_GLOBAL_RESET = (IST_DATETIME_NOW + 1);    //104:統計データリセット
    public final static int IST_ASSO_LN0 = (IST_GLOBAL_RESET + 1);    //105:CurrentAsso
    public final static int IST_ASSO_LN1 = (IST_ASSO_LN0 + 1);    //106:検定用クライアントAsso
    public final static int IST_ASSO_LN2 = (IST_ASSO_LN1 + 1);    //107:HT用クライアントAsso
    public final static int IST_ASSO_LN3 = (IST_ASSO_LN2 + 1);    //108:通信用クライアントAsso
    public final static int IST_SETUP_HDLC = (IST_ASSO_LN3 + 1);    //109:HDLC設定
    public final static int IST_SETUP_SECURITY = (IST_SETUP_HDLC + 1);    //110:暗号化/認証無しセキュリティ設定
    public final static int IST_SETUP_AUTH = (IST_SETUP_SECURITY + 1);    //111:暗号化/認証有りセキュリティ設定


    private final byte[][] g_ist = {
            {(byte) 1, (byte) 0x00, (byte) 0x00, (byte) 0x2a, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x01, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0b, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x00, (byte) 0x41, (byte) 0x2b, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x04, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x03, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x04, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x05, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x06, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x07, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x02, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x10, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x11, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x11, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x21, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x21, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x22, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x22, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x81, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x81, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x81, (byte) 0x02, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x82, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x83, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x8e, (byte) 0x00, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x8e, (byte) 0x01, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x8e, (byte) 0x02, (byte) 0xff},
            {(byte) 1, (byte) 0x01, (byte) 0x7f, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x01, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x02, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x03, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x04, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x06, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x0a, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x0b, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x0c, (byte) 0xff},
            {(byte) 3, (byte) 0x00, (byte) 0x41, (byte) 0x2b, (byte) 0x00, (byte) 0x01, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x01, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x08, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x08, (byte) 0x80, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x09, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x19, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x19, (byte) 0x10, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x19, (byte) 0x11, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x1a, (byte) 0x11, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x08, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x08, (byte) 0x80, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x09, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x19, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x19, (byte) 0x10, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x19, (byte) 0x11, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x1a, (byte) 0x11, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x08, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x08, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x1f, (byte) 0x19, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x20, (byte) 0x05, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x20, (byte) 0x05, (byte) 0x01, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x20, (byte) 0x19, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x47, (byte) 0x19, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x48, (byte) 0x05, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x48, (byte) 0x05, (byte) 0x01, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x48, (byte) 0x19, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x00, (byte) 0x5a, (byte) 0x19, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x20, (byte) 0x01, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0xff},
            {(byte) 3, (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x30, (byte) 0x01, (byte) 0xff},
            {(byte) 4, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x1a, (byte) 0x10, (byte) 0xff},
            {(byte) 4, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x1a, (byte) 0x12, (byte) 0xff},
            {(byte) 4, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x1a, (byte) 0x13, (byte) 0xff},
            {(byte) 4, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x1a, (byte) 0x10, (byte) 0xff},
            {(byte) 4, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x1a, (byte) 0x12, (byte) 0xff},
            {(byte) 4, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x1a, (byte) 0x13, (byte) 0xff},
            {(byte) 7, (byte) 0x00, (byte) 0x00, (byte) 0x63, (byte) 0x62, (byte) 0x00, (byte) 0xff},
            {(byte) 7, (byte) 0x00, (byte) 0x00, (byte) 0x63, (byte) 0x62, (byte) 0x02, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x01, (byte) 0x00, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x01, (byte) 0x01, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x01, (byte) 0x02, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x01, (byte) 0x11, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x02, (byte) 0x00, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x02, (byte) 0x02, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x02, (byte) 0x03, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x62, (byte) 0x63, (byte) 0x00, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x62, (byte) 0x63, (byte) 0x01, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x62, (byte) 0x63, (byte) 0x02, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x62, (byte) 0x63, (byte) 0x03, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x62, (byte) 0x63, (byte) 0x30, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x62, (byte) 0x63, (byte) 0x31, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x63, (byte) 0x63, (byte) 0x00, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x63, (byte) 0x63, (byte) 0x01, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x41, (byte) 0x63, (byte) 0x63, (byte) 0x02, (byte) 0xff},
            {(byte) 7, (byte) 0x01, (byte) 0x00, (byte) 0x63, (byte) 0x02, (byte) 0x01, (byte) 0xff},
            {(byte) 8, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 9, (byte) 0x00, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 15, (byte) 0x00, (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 15, (byte) 0x00, (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x01, (byte) 0xff},
            {(byte) 15, (byte) 0x00, (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x02, (byte) 0xff},
            {(byte) 15, (byte) 0x00, (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x03, (byte) 0xff},
            {(byte) 23, (byte) 0x00, (byte) 0x00, (byte) 0x16, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 64, (byte) 0x00, (byte) 0x00, (byte) 0x2b, (byte) 0x00, (byte) 0x00, (byte) 0xff},
            {(byte) 64, (byte) 0x00, (byte) 0x00, (byte) 0x2b, (byte) 0x00, (byte) 0x01, (byte) 0xff},
    };

    private final int[] YEAR = {
            0,      //	365	2010	2	365
            365,    //	365	2010	2	365
            730,    //	730	2011	3	365
            1096,   //	1096	2012	0	366
            1461,   //	1461	2013	1	365
            1826,   //	1826	2014	2	365
            2191,   //	2191	2015	3	365
            2557,   //	2557	2016	0	366
            2922,   //	2922	2017	1	365
            3287,   //	3287	2018	2	365
            3652,   //	3652	2019	3	365
            4018,   //	4018	2020	0	366
            4383,   //	4383	2021	1	365
            4748,   //	4748	2022	2	365
            5113,   //	5113	2023	3	365
            5479,   //	5479	2024	0	366
            5844,   //	5844	2025	1	365
            6209,   //	6209	2026	2	365
            6574,   //	6574	2027	3	365
            6940,   //	6940	2028	0	366
            7305,   //	7305	2029	1	365
            7670,   //	7670	2030	2	365
            8035,   //	8035	2031	3	365
            8401,   //	8401	2032	0	366
            8766,   //	8766	2033	1	365
            9131,   //	9131	2034	2	365
            9496,   //	9496	2035	3	365
            9862,   //	9862	2036	0	366
            10227,  //	10227	2037	1	365
            10592,  //	10592	2038	2	365
            10957,  //	10957	2039	3	365
            11323,  //	11323	2040	0	366
            11688,  //	11688	2041	1	365
            12053,  //	12053	2042	2	365
            12418,  //	12418	2043	3	365
            12784,  //	12784	2044	0	366
            13149,  //	13149	2045	1	365
            13514,//	13514	2046	2	365
            13879,//	13879	2047	3	365
            14245,//	14245	2048	0	366
            14610,//	14610	2049	1	365
            14975,//	14975	2050	2	365
            15340,//	15340	2051	3	365
            15706,//	15706	2052	0	366
            16071,//	16071	2053	1	365
            16436,//	16436	2054	2	365
            16801,//	16801	2055	3	365
            17167,//	17167	2056	0	366
            17532,//	17532	2057	1	365
            17897,//	17897	2058	2	365
            18262,//	18262	2059	3	365
            18628,//	18628	2060	0	366
            18993,//	18993	2061	1	365
            19358,//	19358	2062	2	365
            19723,//	19723	2063	3	365
            20089,//	20089	2064	0	366
            20454,//	20454	2065	1	365
            20819,//	20819	2066	2	365
            21184,//	21184	2067	3	365
            21550,//	21550	2068	0	366
            21915,//	21915	2069	1	365
            22280,//	22280	2070	2	365
            22645,//	22645	2071	3	365
            23011,//	23011	2072	0	366
            23376,//	23376	2073	1	365
            23741,//	23741	2074	2	365
            24106,//	24106	2075	3	365
            24472,//	24472	2076	0	366
            24837,//	24837	2077	1	365
            25202,//	25202	2078	2	365
            25567,//	25567	2079	3	365
            25933,//	25933	2080	0	366
            26298,//	26298	2081	1	365
            26663,//	26663	2082	2	365
            27028,//	27028	2083	3	365
            27394,//	27394	2084	0	366
            27759,//	27759	2085	1	365
            28124,//	28124	2086	2	365
            28489,//	28489	2087	3	365
            28855,//	28855	2088	0	366
            29220,//	29220	2089	1	365
            29585,//	29585	2090	2	365
            29950,//	29950	2091	3	365
            30316,//	30316	2092	0	366
            30681,//	30681	2093	1	365
            31046,//	31046	2094	2	365
            31411,//	31411	2095	3	365
            31777,//	31777	2096	0	366
            32142,//	32142	2097	1	365
            32507,//	32507	2098	2	365
            32872,//	32872	2099	3	365
            33238//	33238	2100	0	366
    };
    private final int[] MONTH = {
            0,
            31,     //31
            59,     //28
            90,     //31
            120,    //30
            151,    //31
            181,    //30
            212,    //31
            243,    //31
            273,    //30
            304,    //31
            334,    //30
            365,    //31
    };

    public long DatetimeToSec(String datetime) {    /*yyyy/mm/dd hh:mm:ss*/
        int y = Integer.parseInt(datetime.substring(0, 4));
        int m = Integer.parseInt(datetime.substring(5, 7));
        int d = Integer.parseInt(datetime.substring(8, 10));
        int h = Integer.parseInt(datetime.substring(11, 13));
        int k = Integer.parseInt(datetime.substring(14, 16));
        int s = Integer.parseInt(datetime.substring(17, 19));

        int days = (int) YEAR[y - 2010] + (int) MONTH[m - 1] + d - 1;
        if ((y % 4) == 0) {
            if (m > 2) {
                days++;
            }
        }
        long sec = days;
        sec *= 86400;
        sec += h * 3600;
        sec += k * 60;
        sec += s;
        return sec;
    }

    public String SecToDatetime(final long sec) {

        int d;
        int m;
        int y;
        int h;
        int k;
        int s;

        d = (int) (sec / 86400);
        for (y = 0; y < 100; y++) {
            if (YEAR[y] > d) {
                y--;
                break;
            }
        }
        d -= YEAR[y];
        for (m = 0; m < 12; m++) {
            if (MONTH[m] > d) {
                m--;
                break;
            }
        }
        d -= MONTH[m];
        s = (int) (sec % 86400);
        h = s / 3600;
        s %= 3600;
        k = s / 60;
        s %= 60;
        return String.format("%04d/%02d/%02d %02d:%02d:%02d", y + 2010, m + 1, d, h, k, s);
    }
    public long CurrentDatetimeSec() {    /*yyyy/mm/dd hh:mm:ss*/

        long sec;
        String datetime;
        android.icu.text.SimpleDateFormat sdf = new android.icu.text.SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        datetime = sdf.format(date);
        sec = MainActivity.d.DatetimeToSec(
                String.format("%04d/%02d/%02d %02d:%02d:%02d",
                        Integer.parseInt(datetime.substring(0, 4)),
                        Integer.parseInt(datetime.substring(4, 6)),
                        Integer.parseInt(datetime.substring(6, 8)),
                        Integer.parseInt(datetime.substring(8, 10)),
                        Integer.parseInt(datetime.substring(10, 12)),
                        Integer.parseInt(datetime.substring(12, 14))
                ));
        return sec;
    }

    public String SecToRawDatetime(final long sec) {

        int d;
        int m;
        int y;
        int h;
        int k;
        int s;

        d = (int) (sec / 86400);
        for (y = 0; y < 100; y++) {
            if (YEAR[y] > d) {
                y--;
                break;
            }
        }
        d -= YEAR[y];
        for (m = 0; m < 12; m++) {
            if (MONTH[m] > d) {
                m--;
                break;
            }
        }
        d -= MONTH[m];
        s = (int) (sec % 86400);
        h = s / 3600;
        s %= 3600;
        k = s / 60;
        s %= 60;
        return String.format("%04x%02x%02xff%02x%02x%02xff800000", y + 2010, m + 1, d, h, k, s);
    }





    private int getUI8(final byte[] in, final int offset) {
        int ret;
        if (in[offset] < 0)
            ret = 256 + in[offset];
        else
            ret = in[offset];
        return ret;
    }

    private int getI8(final byte[] in, final int offset) {
        int ret;
        ret = in[offset];
        return ret;
    }

    private int getUI16(final byte[] in, final int offset) {
        int ret;

        ret = 0;
        ret += getUI8(in, offset + 0);
        ret <<= 8;
        ret += getUI8(in, offset + 1);
        return ret;
    }

    private int getI16(final byte[] in, final int offset) {
        int ret;
        ret = 0;
        ret += getUI8(in, offset + 0);
        ret <<= 8;
        ret += getUI8(in, offset + 1);
        if (ret > 0x7fff) {
            ret -= 65536;
        }
        return ret;
    }

    private long getUI32(final byte[] in, final int offset) {
        long ret;
        ret = 0;
        ret += getUI8(in, offset + 0);
        ret <<= 8;
        ret += getUI8(in, offset + 1);
        ret <<= 8;
        ret += getUI8(in, offset + 2);
        ret <<= 8;
        ret += getUI8(in, offset + 3);
        return ret;
    }

    private long getI32(final byte[] in, final int offset) {
        long ret;
        ret = 0;
        ret += getUI8(in, offset + 0);
        ret <<= 8;
        ret += getUI8(in, offset + 1);
        ret <<= 8;
        ret += getUI8(in, offset + 2);
        ret <<= 8;
        ret += getUI8(in, offset + 3);
        if (ret > 0x7fffffff) {
            ret -= 0x100000000L;
        }
        return ret;
    }

    public String getBitsStr(final String bits) {
        StringBuffer ret = new StringBuffer();
        long eval = 1, val = Long.parseLong(bits);
        if (val > 0) {
            ret.append(String.format("%d (bit", val));
            for (int i = 0; i < 32; i++) {
                if ((val & eval) > 0) {
                    ret.append(String.format(" %d", i));
                }
                eval <<= 1;
            }
            ret.append(")");
        } else {
            ret.append(String.format("%X (off)", val));
        }
        return ret.toString();
    }

    public String setOct2Str(final byte[] oct, final int offset, final int length) {
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < length; i++) {
            ret.append(String.format("%02X", getUI8(oct, offset + i)));
        }
        return ret.toString();
    }

    public String setStr2Str(final byte[] oct, final int offset, final int length) {
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < length; i++) {
            ret.append(String.format("%c", getUI8(oct, offset + i)));
        }
        return ret.toString();
    }

    public byte[] setStr2Oct(final String str) {
        int len = str.length();
        byte[] out = new byte[len / 2 + len % 2];
        byte[] in = str.getBytes();

        int dat;
        for (int i = 0; i < in.length; i++) {
            dat = getUI8(in, i);
            switch (dat) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    dat -= '0';
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    dat -= 'A';
                    dat += 0x0a;
                    break;
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    dat -= 'a';
                    dat += 0x0a;
                    break;
            }
            if ((i % 2) > 0) {
                out[i / 2] |= dat;
            } else {
                out[i / 2] = (byte) (dat << 4);
            }
        }
        return out;
    }

    public int setUInt32(byte[] buff, final int offset, final int val) {
        int dat = val;
        buff[offset + 3] = (byte) (dat & 0xff);
        dat >>= 8;
        buff[offset + 2] = (byte) (dat & 0xff);
        dat >>= 8;
        buff[offset + 1] = (byte) (dat & 0xff);
        dat >>= 8;
        buff[offset + 0] = (byte) (dat & 0xff);
        return (offset + 4);
    }

    public int setUInt16(byte[] buff, final int offset, final int val) {
        int dat = val;
        buff[offset + 1] = (byte) (dat & 0xff);
        dat >>= 8;
        buff[offset + 0] = (byte) (dat & 0xff);
        return (offset + 2);
    }

    public int setUInt8(byte[] buff, final int offset, final int val) {
        int dat = val;
        buff[offset + 0] = (byte) (dat & 0xff);
        return (offset + 1);
    }

    public double Float(final double div, final String in) {
        return ((double) Long.parseLong(in)) / div;
    }


    public String arrange6_int(final String in){
        Long val = Long.parseLong(in);
        return String.format("%06d", val);
    }

    public String arrange_boolean(final String t, final String f,final String in) {
        if(in.equals("false")){
            return f;
        } else {
            return t;
        }
    }
    public void addViewData(final String data) {
        if (!data.isEmpty()) {
            File file = new File(MainActivity.folderFiles, "viewData");
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearViewData() {
        File file = new File(MainActivity.folderFiles, "viewData");
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readViewData() {
        File file = new File(MainActivity.folderFiles, "viewData");
        ArrayList<String> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (true) {
                String read = br.readLine();
                if (read != null) {
                    out.add(read + "\n");
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    private void writeFile(String data, File file) {
        // try-with-resources
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(data + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ファイルを読み出し*---
    private String readFile(File file) {
        String text = null;
        // try-with-resources
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String read;
            while (true) {
                read = br.readLine();
                if (read != null) {
                    text = read;
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public void writeScan(final String scan) {
        File file = new File(MainActivity.folderFiles, "scan");
        writeFile(scan, file);
    }

    public String readScan() {
        String scan;
        File file = new File(MainActivity.folderFiles, "scan");
        scan = readFile(file);
        if (scan == null) {
            scan = String.valueOf(MainActivity.mScanTick);
            writeScan(scan);
        }
        return scan;
    }

    public void writeTick(final String tick) {
        File file = new File(MainActivity.folderFiles, "tick");
        writeFile(tick, file);
    }

    public String readTick() {
        String tick;
        File file = new File(MainActivity.folderFiles, "tick");
        tick = readFile(file);
        if (tick == null) {
            tick = String.valueOf(MainActivity.mTick);
            writeTick(tick);
        }
        return tick;
    }

    public void writeInterval(final String interval) {
        File file = new File(MainActivity.folderFiles, "interval");
        writeFile(interval, file);
    }

    public String readInterval() {
        String interval;
        File file = new File(MainActivity.folderFiles, "interval");
        interval = readFile(file);
        if (interval == null) {
            interval = "100";
            writeInterval(interval);
        }
        return interval;
    }

    private final int[] fcs16Table = {
            0x0000, 0x1189, 0x2312, 0x329B, 0x4624, 0x57AD, 0x6536, 0x74BF,
            0x8C48, 0x9DC1, 0xAF5A, 0xBED3, 0xCA6C, 0xDBE5, 0xE97E, 0xF8F7,
            0x1081, 0x0108, 0x3393, 0x221A, 0x56A5, 0x472C, 0x75B7, 0x643E,
            0x9CC9, 0x8D40, 0xBFDB, 0xAE52, 0xDAED, 0xCB64, 0xF9FF, 0xE876,
            0x2102, 0x308B, 0x0210, 0x1399, 0x6726, 0x76AF, 0x4434, 0x55BD,
            0xAD4A, 0xBCC3, 0x8E58, 0x9FD1, 0xEB6E, 0xFAE7, 0xC87C, 0xD9F5,
            0x3183, 0x200A, 0x1291, 0x0318, 0x77A7, 0x662E, 0x54B5, 0x453C,
            0xBDCB, 0xAC42, 0x9ED9, 0x8F50, 0xFBEF, 0xEA66, 0xD8FD, 0xC974,
            0x4204, 0x538D, 0x6116, 0x709F, 0x0420, 0x15A9, 0x2732, 0x36BB,
            0xCE4C, 0xDFC5, 0xED5E, 0xFCD7, 0x8868, 0x99E1, 0xAB7A, 0xBAF3,
            0x5285, 0x430C, 0x7197, 0x601E, 0x14A1, 0x0528, 0x37B3, 0x263A,
            0xDECD, 0xCF44, 0xFDDF, 0xEC56, 0x98E9, 0x8960, 0xBBFB, 0xAA72,
            0x6306, 0x728F, 0x4014, 0x519D, 0x2522, 0x34AB, 0x0630, 0x17B9,
            0xEF4E, 0xFEC7, 0xCC5C, 0xDDD5, 0xA96A, 0xB8E3, 0x8A78, 0x9BF1,
            0x7387, 0x620E, 0x5095, 0x411C, 0x35A3, 0x242A, 0x16B1, 0x0738,
            0xFFCF, 0xEE46, 0xDCDD, 0xCD54, 0xB9EB, 0xA862, 0x9AF9, 0x8B70,
            0x8408, 0x9581, 0xA71A, 0xB693, 0xC22C, 0xD3A5, 0xE13E, 0xF0B7,
            0x0840, 0x19C9, 0x2B52, 0x3ADB, 0x4E64, 0x5FED, 0x6D76, 0x7CFF,
            0x9489, 0x8500, 0xB79B, 0xA612, 0xD2AD, 0xC324, 0xF1BF, 0xE036,
            0x18C1, 0x0948, 0x3BD3, 0x2A5A, 0x5EE5, 0x4F6C, 0x7DF7, 0x6C7E,
            0xA50A, 0xB483, 0x8618, 0x9791, 0xE32E, 0xF2A7, 0xC03C, 0xD1B5,
            0x2942, 0x38CB, 0x0A50, 0x1BD9, 0x6F66, 0x7EEF, 0x4C74, 0x5DFD,
            0xB58B, 0xA402, 0x9699, 0x8710, 0xF3AF, 0xE226, 0xD0BD, 0xC134,
            0x39C3, 0x284A, 0x1AD1, 0x0B58, 0x7FE7, 0x6E6E, 0x5CF5, 0x4D7C,
            0xC60C, 0xD785, 0xE51E, 0xF497, 0x8028, 0x91A1, 0xA33A, 0xB2B3,
            0x4A44, 0x5BCD, 0x6956, 0x78DF, 0x0C60, 0x1DE9, 0x2F72, 0x3EFB,
            0xD68D, 0xC704, 0xF59F, 0xE416, 0x90A9, 0x8120, 0xB3BB, 0xA232,
            0x5AC5, 0x4B4C, 0x79D7, 0x685E, 0x1CE1, 0x0D68, 0x3FF3, 0x2E7A,
            0xE70E, 0xF687, 0xC41C, 0xD595, 0xA12A, 0xB0A3, 0x8238, 0x93B1,
            0x6B46, 0x7ACF, 0x4854, 0x59DD, 0x2D62, 0x3CEB, 0x0E70, 0x1FF9,
            0xF78F, 0xE606, 0xD49D, 0xC514, 0xB1AB, 0xA022, 0x92B9, 0x8330,
            0x7BC7, 0x6A4E, 0x58D5, 0x495C, 0x3DE3, 0x2C6A, 0x1EF1, 0x0F78
    };

    private final int CRC16(final byte[] buff, final int count) {
        int fcs16 = 0xFFFF;
        for (int pos = 1; pos < 1 + count; ++pos) {
            fcs16 = (int) (((fcs16 >> 8)
                    ^ fcs16Table[(fcs16 ^ (int) buff[pos]) & 0xFF]) & 0xFFFF);
        }
        fcs16 = ~fcs16;
        fcs16 = ((fcs16 >> 8) & 0xFF) | (fcs16 << 8);
        return (fcs16 & 0xFFFF);
    }
    private final static byte AARQ = (byte) 0x60;
    private final static byte AARE = (byte) 0x61;
    private final static byte RLRQ = (byte) 0x62;
    private final static byte[] def_app1 = {(byte) 0xa1, (byte) 0x09, (byte) 0x06, (byte) 0x07, (byte) 0x60, (byte) 0x85, (byte) 0x74, (byte) 0x05, (byte) 0x08, (byte) 0x01, (byte) 0x01};
    private final static byte[] def_app6 = {(byte) 0xa6, (byte) 0x0a, (byte) 0x04, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55};
    private final static byte[] def_app10 = {(byte) 0x8a, (byte) 0x02, (byte) 0x07, (byte) 0x80};
    private final static byte[] def_app11 = {(byte) 0x8b, (byte) 0x07, (byte) 0x60, (byte) 0x85, (byte) 0x74, (byte) 0x05, (byte) 0x08, (byte) 0x02, (byte) 0x01};
    private final static byte def_app12 = (byte) 0xac;//(byte) 0x12, (byte) 0x80, (byte) 0x10};
    private final static byte def_app30 = (byte) 0xbe;//(byte) 0x10, (byte) 0x04, (byte) 0x0e};
    private final static byte[] def_conf = {(byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x5f, (byte) 0x1f, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x1d, (byte) 0x03, (byte) 0x00};/*224*/
    //    private final static byte [] def_conf = {(byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x5f, (byte) 0x1f, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x1d, (byte) 0x03, (byte) 0x00};
    private final static byte[] def_rlrq = {(byte) 0x80, (byte) 0x01, (byte) 0x00};/*224*/
    private byte[] app1;
    private byte[] app6;
    private byte[] app10;
    private byte[] app11;
    private byte[] app12;
    private byte[] app30;
    private int client;

    private final static byte[] GETRQ = {
            (byte) 0xc0, (byte) 0x01, (byte) 0x41,
            (byte) 0x00, (byte) 0x01,
            (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0xff,
            (byte) 0x02, (byte) 0x00
    };
    private final static byte[] GTNRQ = {
            (byte) 0xc0, (byte) 0x02, (byte) 0x41, 0x00, 0x00, 0x00, 0x00
    };
    private final static byte[] SETRQ = {
            (byte) 0xc1, (byte) 0x01, (byte) 0x41,
            (byte) 0x00, (byte) 0x03,
            (byte) 0x01, (byte) 0x41, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0xff,
            (byte) 0x02, (byte) 0x00
    };//

    private final static byte[] ACTRQ = {
            (byte) 0xc3, (byte) 0x01, (byte) 0x41,
            (byte) 0x00, (byte) 0x03,
            (byte) 0x00, (byte) 0x00, (byte) 0x60, (byte) 0x0f, (byte) 0x00, (byte) 0xff,
            (byte) 0x01
    };

    private final static String[] ERROR = {
            "Success",// = (0),
            "Hardware fault",// = (1),
            "Temporary failure",// = (2),
            "Read write denied",// = (3),
            "Object undefined",// = (4),
            "Object class inconsistent",// = (9),
            "Object unavailable",// = (11),
            "Type unmatched",// = (12),
            "Scope of access violated",// = (13),
            "Data block unavailable",// = (14),
            "Long get aborted",// = (15),
            "No long get in progress",// = (16),
            "Long set aborted",// = (17),
            "No long set in progress",// = (18),
            "Data block number invalid",// = (19),
            "Other reason",// = (250)
    };

    private final static String[] alert1 = {
            "Clock invalid(0)",
            "unregistered(1)",
            "unregistered(2)",
            "unregistered(3)",
            "unregistered(4)",
            "unregistered(5)",
            "unregistered(6)",
            "unregistered(7)",
            "Program memory error(8)",
            "unregistered(9)",
            "unregistered(10)",
            "unregistered(11)",
            "unregistered(12)",
            "Missing neutral(13)",
            "Phase and neutral interchange(14)",
            "In and out interchange(15)",
            "Terminal cover open(16)",
            "unregistered(17)",
            "unregistered(18)",
            "unregistered(19)",
            "unregistered(20)",
            "unregistered(21)",
            "unregistered(22)",
            "unregistered(23)",
            "unregistered(24)",
            "unregistered(25)",
            "unregistered(26)",
            "unregistered(27)",
            "unregistered(28)",
            "unregistered(29)",
            "unregistered(30)",
            "unregistered(31)"
    };

    private final static String[] alert2 = {
            "Total Power Failure(0)",
            "Power Resume(1)",
            "Missing L1 volt (2)",
            "Missing L2 volt (3)",
            "Missing L3 volt (4)",
            "Normal L1 volt(5)",
            "Normal L2 volt(6)",
            "Normal L3 volt(7)",
            "unregistered(8)",
            "unregistered(9)",
            "Current Reversal(10)",
            "Wrong Phase Sequence(11)",
            "unregistered(12)",
            "unregistered(13)",
            "Bad Voltage Quality L1(14)",
            "Bad Voltage Quality L2(15)",
            "Bad Voltage Quality L3(16)",
            "unregistered(17)",
            "Local communication attempt(18)",
            "unregistered(19)",
            "unregistered(20)",
            "unregistered(21)",
            "unregistered(22)",
            "unregistered(23)",
            "unregistered(24)",
            "unregistered(25)",
            "unregistered(26)",
            "unregistered(27)",
            "unregistered(28)",
            "unregistered(29)",
            "unregistered(30)",
            "unregistered(31)",
    };
    public static String PQCODE[] =
            {
                    "RSV(0)",
                    "RSV(1)",
                    "RSV(2)",
                    "RSV(3)",
                    "RSV(4)",
                    "RSV(5)",
                    "RSV(6)",
                    "RSV(7)",
                    "RSV(8)",
                    "RSV(9)",
                    "RSV(10)",
                    "RSV(11)",
                    "RSV(12)",
                    "RSV(13)",
                    "RSV(14)",
                    "RSV(15)",
                    "RSV(16)",
                    "RSV(17)",
                    "RSV(18)",
                    "RSV(19)",
                    "RSV(20)",
                    "RSV(21)",
                    "RSV(22)",
                    "RSV(23)",
                    "RSV(24)",
                    "RSV(25)",
                    "RSV(26)",
                    "RSV(27)",
                    "RSV(28)",
                    "RSV(29)",
                    "RSV(30)",
                    "RSV(31)",
                    "RSV(32)",
                    "RSV(33)",
                    "RSV(34)",
                    "RSV(35)",
                    "RSV(36)",
                    "RSV(37)",
                    "RSV(38)",
                    "RSV(39)",
                    "RSV(40)",
                    "RSV(41)",
                    "RSV(42)",
                    "RSV(43)",
                    "RSV(44)",
                    "RSV(45)",
                    "RSV(46)",
                    "RSV(47)",
                    "RSV(48)",
                    "RSV(49)",
                    "RSV(50)",
                    "RSV(51)",
                    "RSV(52)",
                    "RSV(53)",
                    "RSV(54)",
                    "RSV(55)",
                    "RSV(56)",
                    "RSV(57)",
                    "RSV(58)",
                    "RSV(59)",
                    "RSV(60)",
                    "RSV(61)",
                    "RSV(62)",
                    "RSV(63)",
                    "RSV(64)",
                    "RSV(65)",
                    "RSV(66)",
                    "RSV(67)",
                    "RSV(68)",
                    "RSV(69)",
                    "RSV(70)",
                    "RSV(71)",
                    "RSV(72)",
                    "RSV(73)",
                    "RSV(74)",
                    "RSV(75)",
                    "RSV(76)",
                    "RSV(77)",
                    "RSV(78)",
                    "RSV(79)",
                    "RSV(80)",
                    "RSV(81)",
                    "RSV(82)",
                    "RSV(83)",
                    "RSV(84)",
                    "NORMAL_V1(85)",
                    "NORMAL_V2(86)",
                    "NORMAL_V3(87)",
                    "RSV(88)",
                    "RSV(89)",
                    "RSV(90)",
                    "RSV(91)",
                    "RSV(92)",
                    "RSV(93)",
                    "RSV(94)",
                    "RSV(95)",
                    "RSV(96)",
                    "RSV(97)",
                    "RSV(98)",
                    "RSV(99)",
                    "RSV(100)",
                    "RSV(101)",
                    "RSV(102)",
                    "RSV(103)",
                    "RSV(104)",
                    "RSV(105)",
                    "RSV(106)",
                    "RSV(107)",
                    "RSV(108)",
                    "RSV(109)",
                    "RSV(110)",
                    "RSV(111)",
                    "RSV(112)",
                    "RSV(113)",
                    "RSV(114)",
                    "RSV(115)",
                    "RSV(116)",
                    "RSV(117)",
                    "RSV(118)",
                    "RSV(119)",
                    "RSV(120)",
                    "RSV(121)",
                    "RSV(122)",
                    "RSV(123)",
                    "RSV(124)",
                    "RSV(125)",
                    "RSV(126)",
                    "RSV(127)",
                    "RSV(128)",
                    "RSV(129)",
                    "RSV(130)",
                    "RSV(131)",
                    "RSV(132)",
                    "RSV(133)",
                    "RSV(134)",
                    "RSV(135)",
                    "RSV(136)",
                    "RSV(137)",
                    "RSV(138)",
                    "RSV(139)",
                    "RSV(140)",
                    "RSV(141)",
                    "RSV(142)",
                    "RSV(143)",
                    "RSV(144)",
                    "RSV(145)",
                    "RSV(146)",
                    "RSV(147)",
                    "RSV(148)",
                    "RSV(149)",
                    "RSV(150)",
                    "RSV(151)",
                    "RSV(152)",
                    "RSV(153)",
                    "RSV(154)",
                    "RSV(155)",
                    "RSV(156)",
                    "RSV(157)",
                    "RSV(158)",
                    "RSV(159)",
                    "RSV(160)",
                    "RSV(161)",
                    "RSV(162)",
                    "RSV(163)",
                    "RSV(164)",
                    "RSV(165)",
                    "RSV(166)",
                    "RSV(167)",
                    "RSV(168)",
                    "RSV(169)",
                    "RSV(170)",
                    "RSV(171)",
                    "RSV(172)",
                    "RSV(173)",
                    "RSV(174)",
                    "RSV(175)",
                    "RSV(176)",
                    "RSV(177)",
                    "RSV(178)",
                    "RSV(179)",
                    "RSV(180)",
                    "RSV(181)",
                    "RSV(182)",
                    "RSV(183)",
                    "RSV(184)",
                    "RSV(185)",
                    "RSV(186)",
                    "RSV(187)",
                    "RSV(188)",
                    "RSV(189)",
                    "RSV(190)",
                    "RSV(191)",
                    "RSV(192)",
                    "RSV(193)",
                    "RSV(194)",
                    "RSV(195)",
                    "RSV(196)",
                    "RSV(197)",
                    "RSV(198)",
                    "RSV(199)",
                    "RSV(200)",
                    "RSV(201)",
                    "UNDER_V1(202)",
                    "UNDER_V2(203)",
                    "UNDER_V3(204)",
                    "UNDEF(205)",
                    "UNDEF(206)",
                    "UNDEF(207)",
                    "UNDEF(208)",
                    "OVER_V1(209)",
                    "OVER_V2(210)",
                    "OVER_V3(211)",
                    "DEMAND_RESET(212)",
                    "HRD_ERR(213)",
                    "DAT_INVR(214)",
                    "CLOCK_CHANGE1(215)",
                    "MISS_VLT2(216)",
                    "MISS_VLT2R(217)",
                    "MISS_VLT3(218)",
                    "MISS_VLT3R(219)",
                    "BATTLO(220)",
                    "CLOCK_CHANGE0(221)",
                    "DAT_INV(222)",
                    "POWER_DOWN(223)",
                    "POWER_UP(224)",
                    "PRG_ERR(225)",
                    "PRG_ERRR(226)",
                    "CLK_INV(227)",
                    "CLK_INVR(228)",
                    "MISS_VLT1(229)",
                    "MISS_VLT1R(230)",
                    "UNBALANCE(231)",
                    "UNBALANCER(232)",
                    "REVERSAL(233)",
                    "REVERSALR(234)",
                    "PN_CHGV(235)",
                    "PN_CHGVR(236)",
                    "NEUTRAL(237)",
                    "NEUTRALR(238)",
                    "OPTICAL(239)",
                    "OPTICALR(240)",
                    "BATT_STS(241)",
                    "FLASH_START(242)",
                    "WRONG(243)",
                    "WRONGR(244)",
                    "INOUT(245)",
                    "INOUTR(246)",
                    "COVER(247)",
                    "COVERR(248)",
                    "BATTLOR(249)",
                    "UNDEF(250)",
                    "UNDEF(251)",
                    "UNDEF(252)",
                    "UNDEF(253)",
                    "CLEAR_LP(254)",
                    "CLEAR_PQ(255)",
            };

    public static String LOGCODE0[] =
            {
                    "Unknown",//0:予約
                    "LOG_FUKUDEN",//1:復電検出
                    "LOG_TEIDEN",//2:停電検出
                    "LOG_CHGLVI",//3:WDT検出
                    "LOG_CLK_INV",//4:予約
                    "LOG_CLK_INVR",//5:予約
                    "LOG_KIDO",//6:
                    "LOG_CPU",//7:LVIによるﾘｾｯﾄ
                    "LOG_RTC",//8:RTC状態判別
                    "LOG_ONM",//9:
                    "LOG_FLASH",//10:FWUpdate発生
                    "LOG_SIGN",//11:シグニチャ更新
                    "LOG_CLKSET",//12:時刻変更
                    "LOG_KEYCHG",//13:鍵交換
                    "LOG_RECRST",//14:レコードリセット
                    "LOG_ALM",//15:予約
                    "LOG_BATTLV",//16:予約
                    "LOG_OBJMAP",//17:予約
                    "LOG_MANUAL",//18:手動ログ
                    "LOG_METER",//19:予約
                    "LOG_DEFSET",//20:予約
                    "LOG_USERSET",//21:予約
                    "LOG_RESET",//22:予約
                    "LOG_MEMORY",//23:予約
                    "LOG_CMD",//24:コマンド通信
                    "LOG_BATT",//25:予約
                    "LOG_BATTR",//26:予約
                    "LOG_TSN_OPT",//27:Optical通信開始
                    "LOG_TSN_OPTR",//28:Optical通信終了
                    "LOG_MISS_V",//1側電圧喪失検出(検出解除相関ｱﾘ)
                    "LOG_MISS_VR",//1側電圧喪失解除
                    "LOG_VLT_HI",//31:1側電圧上昇検出(検出解除相関ｱﾘ)
                    "LOG_VLT_HIR",//1側電圧上昇解除
                    "LOG_VLT_LO",//1側電圧低下検出(検出解除相関ｱﾘ)
                    "LOG_VLT_LOR",//1側電圧低下解除
                    "LOG_MAG",//磁気検出(検出解除相関ｱﾘ)
                    "LOG_MAGR",//磁気解除
                    "LOG_AMP_A",//電流タンパーA(検出解除相関ｱﾘ)
                    "LOG_AMP_AR",//電流タンパーA解除
                    "LOG_AMP_B",//電流タンパーB(検出解除相関ｱﾘ)
                    "LOG_AMP_BR",//電流タンパーB解除
                    "LOG_AMP_N",//電流タンパーC(検出解除相関ｱﾘ)
                    "LOG_AMP_NR",//電流タンパーC解除
                    "LOG_AMP_D",//電流タンパーD(検出解除相関ｱﾘ)
                    "LOG_AMP_DR",//電流タンパーD解除
                    "LOG_CLIENT",//電流タンパーD解除
                    "LOG_ACCTBL",//電流タンパーD解除
                    "LOG_AMP_L",//電流タンパーD(検出解除相関ｱﾘ)
                    "LOG_AMP_LR",//電流タンパーD解除
                    "ELOG_CPU",//
                    "ELOG_CPUR",//
                    "ELOG_WAT",//有効電力異常による故障発生
                    "ELOG_WATR",//有効電力異常による故障復帰
                    "ELOG_VAR",//無効電力異常による故障発生
                    "ELOG_VARR",//無効電力異常による故障復帰
                    "ELOG_VI",//電圧･電流固定異常による故障発生
                    "ELOG_VIR",//電圧･電流固定異常による故障復帰
                    "ELOG_EEP",//EEPROMﾉ読ﾐ書ｷ異常による故障発生
                    "ELOG_EEPR",//EEPROMﾉ読ﾐ書ｷ異常による故障発生
                    "ELOG_RTC",//
                    "ELOG_RTCR",//
                    "ELOG_MAG",//
                    "ELOG_MAGR",//
                    "ELOG_TSN",//
                    "ELOG_TSNR",//
                    "ETSN1",//
                    "ETSN2",//
            };

    public static String LOGCODE[] =
            {
                    "LOG_RSV0",
                    "LOG_FUKUDEN",
                    "LOG_TEIDEN",
                    "LOG_LVI",
                    "LOG_CLK_INV",
                    "LOG_CLK_INVR",
                    "LOG_CLK_INVC",
                    "LOG_KIDO",
                    "LOG_CPU",
                    "LOG_RTC",
                    "LOG_ONM",
                    "LOG_FLASH",
                    "LOG_SIG_STS",
                    "LOG_CLK_CHG0",
                    "LOG_KEY_CHG",
                    "LOG_REC_RST",
                    "LOG_ARM",
                    "LOG_BATT_STS",
                    "LOG_OBJMAP",
                    "LOG_MANUAL",
                    "LOG_METER",
                    "LOG_DEFSET",
                    "LOG_USERSET",
                    "LOG_RESET",
                    "LOG_MEMORY",
                    "LOG_CMD",
                    "LOG_BATT",
                    "LOG_BATTR",
                    "LOG_BATTC",
                    "LOG_TSN_OPC",
                    "LOG_TSN_OPCR",
                    "LOG_TSN_OPCC",
                    "LOG_MISS_V",
                    "LOG_MISS_VR",
                    "LOG_MISS_VC",
                    "LOG_VLT_H",
                    "LOG_VLT_HR",
                    "LOG_VLT_HC",
                    "LOG_VLT_L",
                    "LOG_VLT_LR",
                    "LOG_VLT_LC",
                    "LOG_MAG",
                    "LOG_MAGR",
                    "LOG_MAGC",
                    "LOG_AMP_A",
                    "LOG_AMP_AR",
                    "LOG_AMP_AC",
                    "LOG_AMP_B",
                    "LOG_AMP_BR",
                    "LOG_AMP_BC",
                    "LOG_VLT_N",
                    "LOG_VLT_NR",
                    "LOG_VLT_NC",
                    "LOG_AMP_D",
                    "LOG_AMP_DR",
                    "LOG_AMP_DC",
                    "LOG_VLT_P",
                    "LOG_VLT_PR",
                    "LOG_VLT_PC",
                    "LOG_INOUT",
                    "LOG_INOUT_R",
                    "LOG_INOUT_C",
                    "LOG_COVER",
                    "LOG_COVER_R",
                    "LOG_COVER_C",
                    "LOG_WRONG",
                    "LOG_WRONG_R",
                    "LOG_WRONG_C",
                    "LOG_CLIENT",
                    "LOG_ACCESS_TBL",
                    "ELOG_CPU",
                    "ELOG_CPUR",
                    "ELOG_CPUC",
                    "ELOG_WAT",
                    "ELOG_WATR",
                    "ELOG_WATC",
                    "ELOG_VAR",
                    "ELOG_VARR",
                    "ELOG_VARC",
                    "ELOG_VI",
                    "ELOG_VIR",
                    "ELOG_VIC",
                    "ELOG_EEP",
                    "ELOG_EEPR",
                    "ELOG_EEPC",
                    "ELOG_RTC",
                    "ELOG_RTCR",
                    "ELOG_RTCC",
                    "ELOG_MAG",
                    "ELOG_MAGR",
                    "ELOG_MAGC",
                    "ELOG_TSN",
                    "ELOG_TSNR",
                    "ELOG_TSNC",
                    "ETSN1",
                    "ETSN2",
                    "LOG_CLK_CHG1",
                    "LOG_ALM_OFF",
                    "LOG_ALM_ACT",
            };

    private int getAlert(ArrayList<String> out, final String in, final String[] alert) {
        long eval = 1;
        int detect = 0;
        long tmp = Long.parseLong(in);
        byte[] hex = new byte[4];
        setUInt32(hex, 0, (int) tmp);
        for (int i = 0; i < 32; i++) {
            if ((tmp & eval) > 0) {
                out.add(String.format("  %s", alert[i]));
                detect++;
            }
            eval <<= 1;
        }
        return detect;
    }

    public int getAlert1(ArrayList<String> out, final String in) {
        long eval = 1;
        int detect = 0;
        long tmp = Long.parseLong(in);
        byte[] hex = new byte[4];
        setUInt32(hex, 0, (int) tmp);
        for (int i = 0; i < 32; i++) {
            if ((tmp & eval) > 0) {
                out.add(alert1[i]);
                detect++;
            }
            eval <<= 1;
        }
        return detect;
    }

    public int getAlert2(ArrayList<String> out, final String in) {
        long eval = 1;
        int detect = 0;
        long tmp = Long.parseLong(in);
        byte[] hex = new byte[4];
        setUInt32(hex, 0, (int) tmp);
        for (int i = 0; i < 32; i++) {
            if ((tmp & eval) > 0) {
                out.add(alert2[i]);
                detect++;
            }
            eval <<= 1;
        }
        return detect;
    }

    private Gson modelingData(ArrayList<String> out, final ArrayList<String> data) {
        Long val;
        Gson gson = new Gson();
        int idx, mod;
        float f0, f1;

        ArrayList<String> tmp = new ArrayList<String>();
        switch (mObj) {
            case IST_LOGICAL_NAME://1:COSEMLogicalDeviceName
                break;
            case IST_SERIAL_NO://2:ID番号
                out.add(String.format("Serial NO: %s", data.get(0)));
                break;
            case IST_EVENT_CODE://3:イベントコード
                break;
            case IST_FAULT_MAX://4:相互接続認証エラー上限回数
                break;
            case IST_PRODUCT_ID://5:計器型式
                break;
            case IST_PHASE_LINE://6:相線式種別
                break;
            case IST_NUM_AMPR_RATIO://7:変流比(分子)
                break;
            case IST_NUM_VOLT_RATIO://8:変圧比(分子)
                break;
            case IST_NUM_TRANS_RATIO://9:変成比(分子)
                break;
            case IST_DEN_AMPR_RATIO://10:変流比(分母)
                break;
            case IST_DEN_VOLT_RATIO://11:変圧比(分母)
                break;
            case IST_DEN_TRANS_RATIO://12:変成比(分母)
                break;
            case IST_TIME_NOW://13:現在時刻
                break;
            case IST_DATE_NOW://14:現在月日
                break;
            case IST_DIGIT://15:計器桁数
                break;
            case IST_FUNCTION://16:計器機能
                break;
            case IST_VERSION://17:仕様書対応改版数
                break;
            case IST_TRANS_RATIO://18:乗率
                break;
            case IST_TRANS_RATIO_TYPE://19:乗率方式
                break;
            case IST_ENABLE_EVENT://20:イベントの記録有効/無効
                break;
            case IST_ENABLE_EXTRA://21:イベントコードの拡張設定有効/無効
                break;
            case IST_ENABLE_DETAIL://22:各イベントの詳細記録設定有効/無効
                break;
            case IST_ENABLE_DISPLAY://23:その他表示有効/無効
                break;
            case IST_DISPLAY_VALUE://24:その他表示値
                break;
            case IST_ENABLE_FLICKER://25:画面フリッカ有効/無効
                break;
            case IST_FLICKER_STATE://26:画面フリッカ状態
                break;
            case IST_BREAKER://27:開閉区分
                break;
            case IST_LIMIT_STD://28:負荷制限(基本設定)
                break;
            case IST_LIMIT_TMP://29:負荷制限(臨時設定)
                break;
            case IST_LIMIT_CUR://30:負荷制限(動作設定値)
                break;
            case IST_LIMIT_RSV://31:負荷制限予約
                break;
            case IST_BREAKER_COUNT://32:開閉器動作回数
                break;
            case IST_SET_ACT_TIME://33:通電開始時刻設定
                break;
            case IST_SET_ACT_SINGLE://34:個別通電設定
                break;
            case IST_SET_ACT_MULTI://35:多段通電設定
                break;
            case IST_APPROVAL_MODE://36:検定モード，画面表示切替
                break;
            case IST_EVENT_NO://37:イベントデータレコード番号
                break;
            case IST_ACTIVE30_NO://38:有効電力量30分値レコード番号
                break;
            case IST_VOLT30_NO://39:平均電圧30分値レコード番号
                break;
            case IST_BREAKER_NO://40:開閉器動作履歴レコード番号
                break;
            case IST_REACTIVE30_NO://41:無効電力量30分値レコード番号
                break;
            case IST_VOLT01_NO://42:平均電圧1分値レコード番号
                break;
            case IST_SURVEY_WEEK_NO://43:24時間統計データレコード番号
                break;
            case IST_SURVEY_MONTH_NO://44:月間統計データレコード番号
                break;
            case IST_SURVEY_YEAR_NO://45:年間統計データレコード番号
                break;
            case IST_FAULT_LOCK://46:相互接続認証エラー通信ロック時間
                break;
            case IST_SPEC_VOLT://47:定格電圧
                break;
            case IST_SPEC_AMPR://48:定格電流
                break;
            case IST_FWD_ENERGY://49:有効電力量(順潮流)
                break;
            case IST_FWD_ENERGY_CENT://50:有効電力量(順潮流)1/100
                break;
            case IST_FWD_SURVEY://51:有効電力量(順潮流)ロードサーベイ値
                break;
            case IST_FWD_POWER://52:平均有効電力(順潮流)
                break;
            case IST_AVE_FWD_SURVEY://53:24時間平均有効電力量(順潮流)
                break;
            case IST_AVE_AVE_FWD_SURVEY://54:週間平均有効電力量(順潮流)平均値
                break;
            case IST_AVE_MAX_FWD_SURVEY://55:週間最大有効電力量(順潮流)平均値
                break;
            case IST_BAK_ENERGY://56:有効電力量(逆潮流)
                break;
            case IST_BAK_ENERGY_CENT://57:有効電力量(逆潮流)1/100
                break;
            case IST_BAK_SURVEY://58:有効電力量(逆潮流)ロードサーベイ値
                break;
            case IST_BAK_POWER://59:平均有効電力(逆潮流)
                break;
            case IST_AVE_BAK_SURVEY://60:24時間平均有効電力量(逆潮流)
                break;
            case IST_AVE_AVE_BAK_SURVEY://61:週間平均有効電力量(逆潮流)平均値
                break;
            case IST_AVE_MAX_BAK_SURVEY://62:週間最大有効電力量(逆潮流)平均値
                break;
            case IST_REACTIVE_L://63:無効電力量(遅れ)
                break;
            case IST_REACTIVE_C://64:無効電力量(進み)
                break;
            case IST_AMPR1://65:平均電流値(L1)
                break;
            case IST_VOLT1_30://66:30分平均電圧値(L1)
                break;
            case IST_VOLT1_01://67:1分平均電圧値(L1)
                break;
            case IST_VOLT1://68:平均電圧値(L1)
                break;
            case IST_AMPR3://69:平均電流値(L3)
                break;
            case IST_VOLT3_30://70:30分平均電圧値(L3)
                break;
            case IST_VOLT3_01://71:1分平均電圧値(L3)
                break;
            case IST_VOLT3://72:平均電圧値(L3)
                break;
            case IST_COMBINE_AMPR13://73:平均合成電流(L1+L3)
                break;
            case IST_FWD_DISPLAY://74:順潮流電力量表示時間
                break;
            case IST_BAK_DISPLAY://75:逆潮流電力量表示時間
                break;
            case IST_IDLE_TIME://76:通信部未要求時間
                break;
            case IST_OFFLINE_TIME://77:通信部供給電源切断時間
                break;
            case IST_MAX_FWD_SURVEY://78:24時間最大有効電力量(順潮流)
                break;
            case IST_MAX_FWD_SURVEY_MONTH://79:月間最大有効電力量(順潮流)
                break;
            case IST_MAX_FWD_SURVEY_YEAR://80:年間最大有効電力量(順潮流)
                break;
            case IST_MAX_BAK_SURVEY://81:24時間最大有効電力量(逆潮流)
                break;
            case IST_MAX_BAK_SURVEY_MONTH://82:月間最大有効電力量(逆潮流)
                break;
            case IST_MAX_BAK_SURVEY_YEAR://83:年間最大有効電力量(逆潮流)
                break;
            case IST_EVENT_RECORD://84:イベントデータ
                if (data.size() > 3) {
                    if (data.get(3).length() < 11) {
                        mod = 1;
                    } else {
                        mod = 0;
                    }
                } else {
                    mod = 0;
                }
                for (int i = 0; i < data.size(); ) {
                    out.add(data.get(i++));
                    idx = Integer.parseInt(data.get(i++));
                    out.add(String.format("Event: %s", PQCODE[idx]));
                    if (mod == 0) {
                        out.add(String.format("V1: %.2f [V]", Float(100.0, data.get(i++))));
                    } else {
                        out.add(String.format("V1: %.2f [V], V2: %.2f [V], V3: %.2f [V]", Float(100.0, data.get(i++)), Float(100.0, data.get(i++)), Float(100.0, data.get(i++))));
                    }
                }
                break;
            case IST_BREAKER_RECORD://85:開閉器動作履歴
                break;
            case IST_ACTIVE30_RECORD://86:有効電力量30分値
                if (data.size() > 5) {
                    if (data.get(5).length() < 11) {
                        mod = 1;
                    } else {
                        mod = 0;
                    }
                } else {
                    mod = 0;
                }
                for (int i = 0; i < data.size(); ) {
                    out.add(data.get(i++));
                    out.add(String.format("Status   : %s", getBitsStr(data.get(i++))));
                    if (mod == 0) {
                        out.add(String.format("V1: %.2f [V]", Float(100.0, data.get(i++))));
                    } else {
                        out.add(String.format("V1: %.2f [V], V2: %.2f [V], V3: %.2f [V]", Float(100.0, data.get(i++)), Float(100.0, data.get(i++)), Float(100.0, data.get(i++))));
                    }
                    out.add(String.format("Imp: %.3f [kW], Exp: %.3f [kW]", Float(1000.0, data.get(i++)), Float(1000.0, data.get(i++))));
                }
                break;
            case IST_VOLT30_RECORD://87:平均電圧30分値
                break;
            case IST_REACTIVE30_RECORD://88:無効電力量30分値
                break;
            case IST_VOLT01_RECORD://89:平均電圧1分値
                break;
            case IST_SURVEY_WEEK_RECORD://90:24時間統計データ
                break;
            case IST_SURVEY_MONTH_RECORD://91:月間統計データ
                break;
            case IST_SURVEY_YEAR_RECORD://92:年間統計データ
                break;
            case IST_SPECIFICATION://93:計器諸元
                out.add(data.get(0));
                out.add(String.format("Serial NO.: %s", data.get(4)));
                out.add(String.format("Battery Lev: %s", data.get(9)));
                if (data.size() < 16) {
                    out.add(String.format("Last status: %s", getBitsStr(data.get(10))));
                    idx = Integer.parseInt(data.get(11));
                    out.add(String.format("Last event : %s", PQCODE[idx]));
                    out.add(String.format("Alert1 Dsc : %s", data.get(13)));
                    tmp.clear();
                    getAlert(tmp, data.get(13), alert1);
                    out.addAll(tmp);
                    out.add(String.format("Alert2 Dsc : %s", data.get(14)));
                    tmp.clear();
                    getAlert(tmp, data.get(14), alert2);
                    out.addAll(tmp);
                } else {
                    out.add(String.format("Potential  : %s", data.get(10)));
                    out.add(String.format("Last status: %s", getBitsStr(data.get(11))));
                    idx = Integer.parseInt(data.get(12));
                    out.add(String.format("Last event : %s", PQCODE[idx]));
                    out.add(String.format("Alert1 Dsc : %s", data.get(14)));
                    tmp.clear();
                    getAlert(tmp, data.get(14), alert1);
                    out.addAll(tmp);
                    out.add(String.format("Alert2 Dsc : %s", data.get(15)));
                    tmp.clear();
                    getAlert(tmp, data.get(15), alert2);
                    out.addAll(tmp);
                }
                break;
            case IST_CHECK_SETTING://94:設定値一括確認
                break;
            case IST_CHECK_STATE://95:計器状態確認
                break;
            case IST_CHECK_DEMAND://96:負荷制限確認
                break;
            case IST_CHECK_ACT_TIME://97:通電開始時刻確認
                break;
            case IST_CHECK_ACT_SINGLE://98:個別通電確認
                break;
            case IST_CONF_MEASURE://99:現在値検針
                break;
            case IST_CHECK_MEASURE://100:現在値確認
                if (data.size() < 19) {
                    out.add(data.get(0));                                                           //0
                    out.add(String.format("Serial NO.: %s\n", data.get(1)));                         //1
                    out.add(String.format("IMP: %.3f [kWh]", Float(1000.0, data.get(2))));          //2
                    out.add(String.format("EXP: %.3f [kWh]", Float(1000.0, data.get(3))));          //3
                    out.add(String.format("ABS: %.3f [kWh]", Float(1000.0, data.get(4))));          //4
                    out.add(String.format("NET: %.3f [kWh]", Float(1000.0, data.get(5))));          //5
                    out.add(String.format("Max Imp : %.3f [kW]", Float(1000.0, data.get(6))));      //6
                    out.add(String.format("Max Exp : %.3f [kW]", Float(1000.0, data.get(8))));      //7
                    out.add(String.format("Inst Imp: %.3f [kW]", Float(1000.0, data.get(10))));     //8
                    out.add(String.format("Inst Exp: %.3f [kW]", Float(1000.0, data.get(11))));     //9
                    out.add(String.format("V1: %.2f [V]", Float(100.0, data.get(12))));             //10
                    out.add(String.format("Min V1: %.2f [V]", Float(100.0, data.get(13))));         //11
                    out.add(String.format("I1: %.2f [A]", Float(100.0, data.get(14))));            //12
                    out.add(String.format("PF0: %.2f ", Float(100.0, data.get(15))));               //13
                    out.add(String.format("Imp: %.3f [kW]", Float(1000.0, data.get(16))));          //14
                    out.add(String.format("Exp: %.3f [kW]", Float(1000.0, data.get(17))));          //15
                } else {
                    out.add(data.get(0));
                    out.add(String.format("Serial NO.: %s", data.get(1)));
                    out.add(String.format("IMP: %.3f [kWh]", Float(1000.0, data.get(2))));
                    out.add(String.format("EXP: %.3f [kWh]", Float(1000.0, data.get(3))));
                    out.add(String.format("ABS: %.3f [kWh]", Float(1000.0, data.get(4))));
                    out.add(String.format("NET: %.3f [kWh]", Float(1000.0, data.get(5))));
                    out.add(String.format("Max Imp : %.3f [kW], Exp: %.3f [kW]", Float(1000.0, data.get(6)), Float(1000.0, data.get(8))));
                    out.add(String.format("Inst Imp: %.3f [kW], Exp: %.3f [kW]", Float(1000.0, data.get(10)), Float(1000.0, data.get(11))));
                    out.add(String.format("Volt0: %.2f [V], Min: %.2f [V]", Float(100.0, data.get(12)), Float(100.0, data.get(13))));
                    out.add(String.format("Current L1: %.2f [A], L2: %.2f [A]", Float(100.0, data.get(14)), Float(100.0, data.get(15))));
                    out.add(String.format("Power factor: %.2f ", Float(100.0, data.get(16))));
                    out.add(String.format("Block Imp: %.3f [kW], Exp: %.3f [kW]", Float(1000.0, data.get(17)), Float(1000.0, data.get(18))));
                }
                break;
            case IST_CHECK_BREAKER://101:開閉器状態確認
                break;
            case IST_AVE_SURVEY://102:週間詳細データ
                break;
            case IST_DATETIME_NOW://103:現在日時
                break;
            case IST_GLOBAL_RESET://104:統計データリセット
                break;
            case IST_ASSO_LN0://105:CurrentAsso
                break;
            case IST_ASSO_LN1://106:検定用クライアントAsso
                break;
            case IST_ASSO_LN2://107:HT用クライアントAsso
                break;
            case IST_ASSO_LN3://108:通信用クライアントAsso
                break;
            case IST_SETUP_HDLC://109:HDLC設定
                break;
            case IST_SETUP_SECURITY://110:暗号化/認証無しセキュリティ設定
                break;
            case IST_SETUP_AUTH://111:暗号化/認証有りセキュリティ設定
                break;
             default:
                break;
        }
        return gson;
    }

    private void TimeStamp(){
        timestamp = System.currentTimeMillis();
    }

    public String getStampDate(final boolean modeling) {
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(timestamp);

        if (modeling)
            return "==== " + df.format(date) + " ====";
        else
            return df.format(date);
    }

    private String dataAccessResult(final byte[] in, final int offset) {
        String ret;
        int code = getUI8(in, offset);

        switch (code) {
            case 0:
                ret = String.valueOf("success (0)");
                break;
            case 1:
                ret = String.valueOf("hardware fault (1)");/**/
                break;
            case 2:
                ret = String.valueOf("temporary failure (2)");/**/
                break;
            case 3:
                ret = String.valueOf("read write denied (3)");/**/
                break;
            case 4:
                ret = String.valueOf("object undefined (4)");/**/
                break;
            case 9:
                ret = String.valueOf("object class inconsistent (9)");/**/
                break;
            case 11:
                ret = String.valueOf("object unavailable (11)");/**/
                break;
            case 12:
                ret = String.valueOf("type unmatched (12)");/**/
                break;
            case 13:
                ret = String.valueOf("scope of access violated (13)");/**/
                break;
            case 14:
                ret = String.valueOf("data block unavailable (14)");/**/
                break;
            case 15:
                ret = String.valueOf("long get aborted (15)");/**/
                break;
            case 16:
                ret = String.valueOf("no long get in progress (16)");/**/
                break;
            case 17:
                ret = String.valueOf("long set aborted (17)");/**/
                break;
            case 18:
                ret = String.valueOf("no long set in progress (18)");/**/
                break;
            case 19:
                ret = String.valueOf("data block number invalid (19)");/**/
                break;
            case 250:
                ret = String.valueOf("other reason(250)");/**/
                break;
            default:
                ret = String.format("unknown code (%d)", code);/**/
                break;
        }
        return ret;
    }

    private byte[] setTag(final byte id, final byte[] data) {
        byte[] out;
        int offset = 0, len;
        len = data.length;
        if (data.length > 65535) {
            offset = 6;
            out = new byte[len + offset];
            out[5] = (byte) (len & 0xff);
            len >>= 8;
            out[4] = (byte) (len & 0xff);
            len >>= 8;
            out[3] = (byte) (len & 0xff);
            len >>= 8;
            out[2] = (byte) (len & 0xff);
            out[1] = (byte) 0x84;
        } else {
            if (len > 255) {
                offset = 4;
                out = new byte[len + offset];
                out[3] = (byte) (len & 0xff);
                len >>= 8;
                out[2] = (byte) (len & 0xff);
                out[1] = (byte) 0x82;
            } else {
                if (data.length > 127) {
                    offset = 3;
                    out = new byte[len + offset];
                    out[2] = (byte) (len & 0xff);
                    out[1] = (byte) 0x81;
                } else {
                    offset = 2;
                    out = new byte[len + offset];
                    out[1] = (byte) (len & 0xff);
                }
            }
        }
        out[0] = (byte) id;
        System.arraycopy(data, 0, out, offset, data.length);
        return out;
    }

    private byte[] getTag(int[] inf, final byte[] in) {
        byte[] out;
        int len = 0;
        /*inf[0]:tag, inf[1]: offset*/
        inf[0] = in[inf[1]++];  /*tag*/
        if ((in[inf[1]] & 0x80) > 0) {
            int cnt = getUI8(in, inf[1]++);
            cnt &= 0x7f;
            for (int i = 0; i < cnt; i++) {
                len <<= 8;
                len += getUI8(in, inf[1]++);
            }
        } else {
            len = getUI8(in, inf[1]++);
        }
        if ((len + inf[1]) > in.length) {
            return null;
        }
        out = new byte[len];
        System.arraycopy(in, inf[1], out, 0, len);
        inf[1] += len;
        return out;
    }

    private void getCount(int[] io, final byte[] in) {
        /*io[0]:offset,io[1]:count*/
        byte tmp = in[io[0]];
        if ((tmp & 0x80) > 0) {
            int cnt = tmp & 0x7f;
            io[0]++;
            io[1] = 0;
            for (int i = 0; i < cnt; i++) {
                io[1] <<= 8;
                io[1] += getUI8(in, io[0]++);
            }
        } else {
            io[1] = getUI8(in, io[0]++);
        }
    }

    private void getData(ArrayList<String> data, int[] io, final byte[] in) {
        byte[] buff;
        long val;
        int i, type, count;
        int year, mon, day, hour, min, sec;

        if (in.length > 0) {
            type = in[io[0]++];
            switch (type) {
                case 0:      //"null_data"
                    break;
                case 1:      //"array"
                case 2:      //"structure"
                    getCount(io, in);
                    count = io[1];
                    for (i = 0; (i < count) && (io[0] < in.length); i++) {
                        getData(data, io, in);
                    }
                    break;
                case 3:      //"boolean"
                    val = getUI8(in, io[0]);
                    if (val > 0) {
                        data.add("true");
                    } else {
                        data.add("false");
                    }
                    io[0]++;
                    break;
                case 4:      //"bit_string"
                    break;
                case 5:      //"double_long"
                    val = getI32(in, io[0]);
                    data.add(String.format("%d", val));
                    io[0] += 4;
                    break;
                case 6:        //"double_long_unsigned"
                    val = getUI32(in, io[0]);
                    data.add(String.format("%d", val));
                    io[0] += 4;
                    break;
                case 7:        //"floating_point"
                    break;
                case 9:        //"octet_string"
                    getCount(io, in);
                    if (io[1] == 12) {  /*サイズで強制的に日時に変換*/
                        year = getUI16(in, io[0]);
                        io[0] += 2;
                        mon = getUI8(in, io[0]);
                        io[0]++;
                        day = getUI8(in, io[0]);
                        io[0]++;
                        io[0]++;//day of week
                        hour = getUI8(in, io[0]);
                        io[0]++;
                        min = getUI8(in, io[0]);
                        io[0]++;
                        sec = getUI8(in, io[0]);
                        io[0]++;
                        io[0] += 4;
//                        data.add(String.format("%02d/%02d/%04d %02d:%02d:%02d", day, mon, year, hour, min, sec));
                        data.add(String.format("%04d/%02d/%02d %02d:%02d:%02d", year, mon, day, hour, min, sec));
                    } else {
                        data.add(setOct2Str(in, io[0], io[1]));
                        io[0] += io[1];
                    }
                    break;
                case 10:    //"visible_string"
                    getCount(io, in);
                    data.add(setStr2Str(in, io[0], io[1]));
                    io[0] += io[1];
                    break;
                case 13:    //"bcd"
                    break;
                case 15:    //"integer"
                    data.add(String.format("%d", getI8(in, io[0])));
                    io[0] += 1;
                    break;
                case 16:    //"long"
                    data.add(String.format("%d", getI16(in, io[0])));
                    io[0] += 2;
                    break;
                case 17:    //"unsigned"
                case 22:    //"enum"
                    data.add(String.format("%d", getUI8(in, io[0])));
                    io[0] += 1;
                    break;
                case 18:    //"long_unsigned"
                    data.add(String.format("%d", getUI16(in, io[0])));
                    io[0] += 2;
                    break;
                case 19:    //"compact_array"
                    break;
                case 20:    //"long64"
                    break;
                case 21:    //"long64_unsigned"
                    break;
                case 23:    //"float32"
                    io[0] += 4;
                    break;
                case 24:    //"float64"
                    io[0] += 8;
                    break;
                case 25:    //"date_time"
                    year = getUI16(in, io[0]);
                    io[0] += 2;
                    mon = getUI8(in, io[0]);
                    io[0]++;
                    day = getUI8(in, io[0]);
                    io[0]++;
                    io[0]++;//day of week
                    hour = getUI8(in, io[0]);
                    io[0]++;
                    min = getUI8(in, io[0]);
                    io[0]++;
                    sec = getUI8(in, io[0]);
                    io[0]++;
                    io[0] += 4;
                    data.add(String.format("%04d/%02d/%02d %02d:%02d:%02d", year, mon, day, hour, min, sec));
                    break;
                case 26:    //"date"
                    year = getUI16(in, io[0]);
                    io[0] += 2;
                    mon = getUI8(in, io[0]);
                    io[0]++;
                    day = getUI8(in, io[0]);
                    io[0]++;
                    io[0]++;//day of week
                    data.add(String.format("%02d/%02d/%04d", day, mon, year));
                    break;
                case 27:    //"time"
                    hour = getUI8(in, io[0]);
                    io[0]++;
                    min = getUI8(in, io[0]);
                    io[0]++;
                    sec = getUI8(in, io[0]);
                    io[0]++;
                    io[0] += 4;
                    data.add(String.format("%02d:%02d:%02d", hour, min, sec));
                    break;
                case 255:    //"don't_care"
                    break;
            }
            ;
        }
    }

    private byte[] adr1 = {0x03};
    private byte cmd;
    private byte ns, nr;
    private byte ws, wr, cws, cwr;
    private int ss, sr;
    private byte[] info;
    private int mRank;
    private final byte[] def_info = {
            (byte) 0x81, (byte) 0x80, (byte) 0x0c,
            (byte) 0x05, (byte) 0x01, (byte) 0x00,
            (byte) 0x06, (byte) 0x01, (byte) 0x00,
            (byte) 0x07, (byte) 0x01, (byte) 0x00,
            (byte) 0x08, (byte) 0x01, (byte) 0x00
    };

    public void init(final byte sizes, final byte sizer,
                     final byte wins, final byte winr) {
        if (info != null) {
            info = null;
        }
        info = new byte[def_info.length];
        System.arraycopy(def_info, 0, info, 0, def_info.length);
        info[5] = (byte) sizes;
        info[8] = (byte) sizer;
        info[11] = (byte) wins;
        info[14] = (byte) winr;
    }
    public int Rank(){
        return mRank;
    }
    public void Rank(final int rank) {
        mRank = rank;
    }
    byte Addr(){
        byte ret;
        switch (mRank) {
            case RANK_HHU:
                ret = (byte)0x23;
                break;
            case RANK_COM:
                ret = (byte)0x25;
                break;
            default:
                ret = (byte)0x21;
                break;
        }
        return ret;
    }
    final byte [][] title6 = {
            {'P','U','B','0','0','0','0','0'},
            {'H','H','U','0','0','0','0','0'},
            {'C','O','M','0','0','0','0','0'}
    };
    byte [] Account(){
        switch (mRank) {
            case RANK_HHU:
                return title6[1];
            case RANK_COM:
                return title6[2];
            default:
                return title6[0];
        }
    }
    private byte[] hdlcs(final byte cmd, final byte[] llc) {
        int crc, len, offset;
        byte tmp;
        byte[] s;

        len = 7;
        if (llc != null) {
            len += llc.length;
            len += 2 + 3;/*crc+llc headder*/
        }
        s = new byte[len + 2];

        offset = 0;
        s[offset++] = (byte) 0x7e;
        s[offset] = (byte) 0xa0;
        tmp = (byte) (len >> 8);
        s[offset++] += (byte) (tmp & 0x0f);
        s[offset++] = (byte) (len & 0xff);
        s[offset++] = (byte) adr1[0];
        s[offset++] = Addr();
        s[offset++] = cmd;
        crc = CRC16(s, offset - 1);
        offset = setUInt16(s, offset, crc);
        if (llc != null) {
            s[offset++] = (byte) 0xe6;
            s[offset++] = (byte) 0xe6;
            s[offset++] = (byte) 0x00;
            System.arraycopy(llc, 0, s, offset, llc.length);
            offset += llc.length;
            crc = CRC16(s, offset - 1);
            offset = setUInt16(s, offset, crc);
        }
        s[offset] = (byte) 0x7e;
        TimeStamp();
        return s;
    }

    private byte[] hdlcr(int[] ret, final byte[] in) {
        int crc, len;
        byte tmp;

        ret[0] = 0;
        ret[1] = 0;
        if (in.length >= 9) {
            //in[offset]=(byte)0x7e;
            ret[1]++;
            len = getUI8(in, ret[1]++);
            len &= 0x0f;
            len <<= 8;
            len += getUI8(in, ret[1]++);
            if (in.length == (len + ret[1] - 1)) {
                if (in[ret[1]++] == Addr()) {
                    if (in[ret[1]++] == (byte) adr1[0]) {
                        ret[0] = in[ret[1]++];
                        crc = CRC16(in, ret[1] - 1);
                        tmp = (byte) (crc >> 8);
                        if (in[ret[1]++] == (byte) tmp) {
                            if (in[ret[1]++] == (byte) (crc & 0xff)) {
                                if (len > 12) {
                                    len -= 12;
                                    ret[1] += 3;//llc header
                                    byte[] llc = new byte[len];
                                    System.arraycopy(in, ret[1], llc, 0, len);
                                    ret[1] += len;
                                    crc = CRC16(in, ret[1] - 1);
                                    tmp = (byte) (crc >> 8);
                                    if (in[ret[1]++] == (byte) tmp) {
                                        if (in[ret[1]] == (byte) (crc & 0xff)) {
                                            return llc;
                                        } else {
                                            ret[0] = 0;
                                        }
                                    } else {
                                        ret[0] = 0;
                                    }
                                } else {
                                    /*UA?*/
                                }
                            } else {
                                ret[0] = 0;
                            }
                        } else {
                            ret[0] = 0;
                        }
                    }
                }
            }
        }
        return null;
    }

    private byte[] svAppTitle = null;
    private byte[] clAppTitle = null;

    private void setClientAppTitle(final byte[] in, final int offset) {
        if (this.clAppTitle != null) {
            this.clAppTitle = null;
        }
        this.clAppTitle = new byte[8];
        System.arraycopy(in, offset, clAppTitle, 0, 8);
    }

    private void setServerAppTitle(final byte[] in, final int offset) {
        if (this.svAppTitle != null) {
            this.svAppTitle = null;
        }
        this.svAppTitle = new byte[8];
        System.arraycopy(in, offset, svAppTitle, 0, 8);
        SERIAL_ID = String.format("F%02d%c%06d",svAppTitle[3],svAppTitle[4], (getUI8(svAppTitle,5)*256+getUI8(svAppTitle,6))*256+getUI8(svAppTitle,7));
    }

    private int mObj;
    private byte mMode;
    private byte mAtr;
    private byte mSel;
    private int mBlockNo;

    private int setApp1() {

        int len = def_app1.length;
        if (app1 == null) {
            app1 = new byte[def_app1.length];
        }
        System.arraycopy(def_app1, 0, app1, 0, def_app1.length);
        app1[app1.length - 1] = 1;
        return len;
    }

    private int setApp6() {

        int len = def_app6.length;
        byte[] account = Account();
        if (app6 == null) {
            app6 = new byte[def_app6.length];
        }
        System.arraycopy(def_app6, 0, app6, 0, def_app6.length);
        System.arraycopy(account, 0, app6, 4, account.length);
        return len;
    }

    private int setApp10() {

        int len = def_app10.length;
        switch (mRank) {
            case RANK_HHU:
            case RANK_COM:
                if (app10 == null) {
                    app10 = new byte[def_app10.length];
                }
                System.arraycopy(def_app10, 0, app10, 0, def_app10.length);
                break;
            default:
                app10 = null;
                len = 0;
                break;
        }
        return len;
    }

    private int setApp11() {

        int len = def_app11.length;

        switch (mRank) {
            case RANK_HHU:
            case RANK_COM:
                if (app11 == null) {
                    app11 = new byte[def_app11.length];
                }
                System.arraycopy(def_app11, 0, app11, 0, def_app11.length);
                app11[app11.length - 1] = 1;
                break;
            default:
                app11 = null;
                len = 0;
                break;
        }
        return len;
    }
    final byte [] password = {(byte)0x52,(byte)0x45,(byte)0x5a,(byte)0x49,(byte)0x4c,(byte)0x20,(byte)0x50,(byte)0x41,(byte)0x53,(byte)0x53,(byte)0x57,(byte)0x4f,(byte)0x52,(byte)0x44,(byte)0x30,(byte)0x30};
//    final byte [] password = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
    private int setApp12() {

        int len;
        byte[] octet;
        switch (mRank) {
            case RANK_HHU:
            case RANK_COM:
                octet = setTag((byte) 0x80, password);
                app12 = setTag(def_app12, octet);
                len = app12.length;
                break;
            default:
                app12 = null;
                len = 0;
                break;
        }
        return len;
    }

    private byte[] global;

    private int setApp30() {
        app30 = null;
        byte[] octet;
        byte[] userinfo;

        octet = new byte[def_conf.length + 2];
        octet[0] = 0x01;
        octet[1] = 0x00;/*ded*/
        System.arraycopy(def_conf, 0, octet, 2, def_conf.length);
        userinfo = setTag((byte) 0x04, octet);
        app30 = setTag(def_app30, userinfo);
        return app30.length;
    }

    public byte[] Release() {
        byte[] data = new byte[def_rlrq.length];
        System.arraycopy(def_rlrq, 0, data, 0, def_rlrq.length);
        return hdlcs((byte) 0x13, setTag(RLRQ, data));
    }

    public byte[] Close(int[] ret, final byte[] res) {

        byte[] receive = null;

        receive = hdlcr(ret, res);
        if (0 == ret[0]) {
            ret[1] = -2;
            return null;
        }
        if (receive[4] != 0) {
            ret[0] = 0;
            ret[1] = -1;
            return null;
        }
        return hdlcs((byte) 0x53, null);
    }

    public void Finish(int[] ret, final byte[] res) {

        hdlcr(ret, res);
        if (0 == ret[0]) {
            ret[1] = -2;
        }
    }

    public byte[] Open() {
        ns = 0;
        nr = 0;
        if (info != null) {
            info = null;
        }
        return hdlcs((byte) 0x93, null);
    }

    public byte[] Session(int[] ret, final byte[] res) {

        byte[] receive = null;
        mBlockNo = 0;

        hdlcr(ret, res);
        if (0 == ret[0]) {
            ret[1] = -2;
            return null;
        }
        int len = 0, offset;
        len += setApp1();
        len += setApp6();
        len += setApp10();
        len += setApp11();
        len += setApp12();
        len += setApp30();

        offset = 0;
        byte[] data = new byte[len];
        if (app1 != null) {
            System.arraycopy(app1, 0, data, offset, app1.length);
            offset += app1.length;
        }
        if (app6 != null) {
            System.arraycopy(app6, 0, data, offset, app6.length);
            offset += app6.length;
        }
        if (app10 != null) {
            System.arraycopy(app10, 0, data, offset, app10.length);
            offset += app10.length;
        }
        if (app11 != null) {
            System.arraycopy(app11, 0, data, offset, app11.length);
            offset += app11.length;
        }
        if (app12 != null) {
            System.arraycopy(app12, 0, data, offset, app12.length);
            offset += app12.length;
        }
        if (app30 != null) {
            System.arraycopy(app30, 0, data, offset, app30.length);
            offset += app30.length;
        }
        return hdlcs((byte) 0x13, setTag(AARQ, data));
    }

    public byte[] Challenge(int[] ret, final byte[] res) {
        byte id = 0;
        byte[] llc = null;
        byte[] initR = null;

        llc = hdlcr(ret, res);
        if (0 == ret[0]) {
            ret[1] = -2;
            return null;
        }
        boolean ok = true;
        ret[0] = 0;
        ret[1] = 0;
        llc = getTag(ret, llc);
        if (ret[0] != AARE) {
            return null;
        }
        ret[0] = 0;
        ret[1] = 0;
        for (ret[1] = 0; ret[1] < llc.length && ok; ) {
            byte[] app = null;
            app = getTag(ret, llc);
            ok = false;
            if (app != null) {
                id = (byte) (ret[0] & 0x00ff);
                switch (id) {
                    case (byte) 0xa1:
                        if (app.length == 9) {
                            ok = true;
                        }
                        break;
                    case (byte) 0xa2:
                        if (app.length == 3) {
                            ok = app[2] == 0x00;
                        }
                        break;
                    case (byte) 0xa3:
                        if (app.length == 5) {
                            ok = app[4] == 0x00;
                        }
                        break;
                    case (byte) 0xa4:
                        if (app.length == 10) {
                            setServerAppTitle(app, 2);
                            ok = true;
                        }
                        break;
                    case (byte) 0x88:
                        if (app.length == 2) {
                            ok = true;
                        }
                        break;
                    case (byte) 0x89:
                        if (app.length == 7) {
                            ok = true;
                        }
                        break;
                    case (byte) 0xaa:
                        if (app.length == 0x21) {
                            ok = true;
                        }
                        break;
                    case (byte) 0xbe:
                        if (app.length > 0) {
                            int[] inf = new int[2];
                            inf[0] = 0;
                            inf[1] = 0;
                            initR = getTag(inf, app);
                            ok = initR[0] == 0x08;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (ok == false) {
            ret[0] = 0;
            return null;
        } else {
            ret[0] = 1;
        }
        return null;
    }

    public byte[] getReq(final int idx, final byte atr, final byte sel, final String attach, final byte pos) {

        int len, offset;
        byte[] getQ;
        byte[] param;
        if (attach != null) {
            param = setStr2Oct(attach);
        } else {
            param = new byte[0];
        }

        if (mBlockNo == 0) {
            mObj = idx;
            mMode = 0;
            mAtr = atr;
            mSel = sel;
            len = GETRQ.length;
            if (sel > 0) {
                len++;
            }
            if (param.length > 0) {
                len += param.length;
            }
            getQ = new byte[len];
            System.arraycopy(GETRQ, 0, getQ, 0, GETRQ.length);
            if (pos == 0) {
                System.arraycopy(g_ist[idx], 0, getQ, 4, 7);
            } else {
                System.arraycopy(g_ist[idx], 0, getQ, 4, 6);
                getQ[10] = pos;
            }
            getQ[11] = atr;
            if (sel > 0) {
                offset = GETRQ.length - 1;
                getQ[offset++] = (byte) 0x01;
                getQ[offset++] = sel;
            } else {
                offset = GETRQ.length;
            }
            if (param.length > 0) {
                System.arraycopy(param, 0, getQ, offset, param.length);
            }
        } else {
            len = GTNRQ.length;
            getQ = new byte[len];
            System.arraycopy(GTNRQ, 0, getQ, 0, len);
            setUInt32(getQ, len - 4, mBlockNo);
        }
        return hdlcs((byte) 0x13, getQ);
    }

    public byte[] setReq(final int idx, final byte atr, final byte sel, final String attach, final byte pos) {

        int len, offset;
        byte[] param;
        if (attach != null) {
            param = setStr2Oct(attach);
        } else {
            param = new byte[0];
        }

        len = SETRQ.length;
        if (sel > 0) {
            len++;
        }
        if (param.length > 0) {
            len += param.length;
        }
        byte[] setQ = new byte[len];
        System.arraycopy(SETRQ, 0, setQ, 0, SETRQ.length);
        if (pos == 0) {
            System.arraycopy(g_ist[idx], 0, setQ, 4, 7);
        } else {
            System.arraycopy(g_ist[idx], 0, setQ, 4, 6);
            setQ[10] = pos;
        }

        setQ[11] = atr;
        if (sel > 0) {
            offset = SETRQ.length - 1;
            setQ[offset++] = (byte) 0x01;
            setQ[offset++] = sel;
        } else {
            offset = SETRQ.length;
        }
        if (param.length > 0) {
            System.arraycopy(param, 0, setQ, offset, param.length);
        }
        mObj = idx;
        mMode = 1;
        mAtr = atr;
        mSel = sel;
        return hdlcs((byte) 0x13, setQ);
    }

    public byte[] actReq(final int idx, final byte mth, final String attach, final byte pos) {
        int len, offset;
        byte[] param;

        if (attach != null) {
            param = setStr2Oct(attach);
        } else {
            param = new byte[0];
        }

        len = ACTRQ.length;
        if (param.length > 0) {
            len++;
            len += param.length;
        }
        byte[] actQ = new byte[len];
        System.arraycopy(ACTRQ, 0, actQ, 0, ACTRQ.length);
        if (pos == 0) {
            System.arraycopy(g_ist[idx], 0, actQ, 4, 7);
        } else {
            System.arraycopy(g_ist[idx], 0, actQ, 4, 6);
            actQ[10] = pos;
        }
        actQ[11] = mth;
        offset = ACTRQ.length;
        if (param.length > 0) {
            actQ[offset++] = (byte) 0x01;
            System.arraycopy(param, 0, actQ, offset, param.length);
        }
        mObj = idx;
        mMode = 3;
        mAtr = mth;
        mSel = 0;
        return hdlcs((byte) 0x13, actQ);
    }

    public ArrayList<String> DataRes(int[] ret, final byte[] in, final boolean modeling) {
        ArrayList<String> data = new ArrayList<String>();
        ArrayList<String> out = new ArrayList<String>();
        ret[0] = 0;
        ret[1] = 0;

        int[] len = new int[2];
        len[0] = 0;
        len[1] = 0;

        byte[] llc = hdlcr(len, in);
        if (0 == len[0]) {
            ret[1] = -2;
            mBlockNo = 0;
//          out.add(String.format("Fatal error: Invalid HDLC frame: %s", setOct2Str(in, 0, in.length)));
            return out;
        }
        if (llc[0] == 0x0e) {
            ret[1] = -1;
//            out.add(String.format("Confirm service error: %d,%d,%d", getUI8(llc, 1), getUI8(llc, 2), getUI8(llc, 3)));
            return out;
        }
        byte[] _res = llc;
        switch (mMode) {
            case 0: /*get*/
                if (_res.length > 3) {
                    int[] io = new int[2];
                    if (_res[1] == 1) {    /*normal*/
                        /*get:3, 4*/
                        if (_res[3] == 0x00) {
                            out.add(getStampDate(modeling));
                            io[0] = 0;
                            io[1] = 0;
                            byte app[] = new byte[_res.length - 4];
                            System.arraycopy(_res, 4, app, 0, _res.length - 4);
                            getData(data, io, app);
                            if (modeling) {
                                modelingData(out, data);
                            } else {
                                out.addAll(data);
                            }
                        } else {
                            out.add(dataAccessResult(_res, 4));
                            ret[1] = _res[4];
                            /*error*/
                        }
                        mBlockNo = 0;
                    } else {   /*Block*/
                        int no = 0, size;
                        no = (int) getUI32(_res, 4);    /*send block no*/
                        len[0] = 8;
                        len[1] = 0;
                        if (_res[len[0]] == 0) {
                            len[0]++; /*RAW 0*/
                            getCount(len, _res); /*raw size*/
                            size = len[1];
                            if (mBlockNo == 0) {
                                out.add(getStampDate(modeling));
                                len[0]++;/* ARRAY */
                                getCount(len, _res); /*record count*/
                            }
                            mBlockNo = no;
                            len[1] = _res.length - len[0];
                            byte app[] = new byte[len[1]];
                            System.arraycopy(_res, len[0], app, 0, len[1]);
                            for (io[0] = 0, io[1] = 0; io[0] < len[1]; ) {
                                getData(data, io, app);
                            }
                            if (modeling) {
                                modelingData(out, data);
                            } else {
                                out.addAll(data);
                            }
                            if (_res[3] == 0) {
                                ret[0] = 2; /*continue*/
                            } else {
                                ret[0] = 0;
                                mBlockNo = 0;
                            }
                        } else {
                            out.add(dataAccessResult(_res, len[0] + 1));
                            ret[1] = _res[len[0] + 1];
                            mBlockNo = 0;
                        }
                    }
                }
                break;
            case 1:/*set*/
                out.add(getStampDate(modeling));
                out.add(dataAccessResult(_res, 3));
                break;
            case 3:/*act*/
                out.add(getStampDate(modeling));
                out.add(dataAccessResult(_res, 3));
                ret[1] = _res[3];
                mBlockNo = 0;
                break;
            default:
                break;
        }
        return out;
    }
}
