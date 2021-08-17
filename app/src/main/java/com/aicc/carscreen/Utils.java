package com.aicc.carscreen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 工具类
 */
public class Utils {
    public enum IconMode {on, off, standby,black}

    public static void changeIconColor(ImageView iv, IconMode im) {
        ColorMatrix matrix = new ColorMatrix();
        switch (im) {
            case off:
                matrix.setSaturation(0);
                break;
            case standby:
                //颜色
                //图片得绿色值为65 180 85，目标黄色值为243 201 81
                //但不能直接设置243 201 81，因为ColorMatrix会把两个颜色叠加最后形成得颜色是65+243 201+180 81+85
                //需要为矩阵设置243-65=178 201-180=21 81-85=0
                float[] src1 = new float[]{
                        1, 0, 0, 0, 178,
                        0, 1, 0, 0, 21,
                        0, 0, 1, 0, 0,
                        0, 0, 0, 1, 0
                };
                matrix.set(src1);
                break;
            case black:
                float[] src3 = new float[]{
                        1,0,0,0,-255,
                        0,1,0,0,-255,
                        0,0,1,0,-255,
                        0,0,0,1,0
                };
                matrix.set(src3);
                break;
            case on:
            default:
                float[] src2 = new float[]{
                        1, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        0, 0, 0, 1, 0
                };
                matrix.set(src2);
        }
        iv.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    /**
     * 改变文字图标颜色
     * @param tv
     * @param im
     */
    public static void changeTextColor(TextView tv, IconMode im){
        switch(im){
            case off:
                tv.setTextColor(Color.GRAY);
                break;
            case standby:
                tv.setTextColor(Color.rgb(243,201,81));
                break;
            case on:
                tv.setTextColor(Color.GREEN);
                break;
            default:
                tv.setTextColor(Color.GRAY);
                break;
        }
    }
    /**
     * 获得属性文件内容
     *
     * @return
     */
    public static Properties getProps(Context context) {
        Properties props = new Properties();
        try {
            InputStream in = context.getAssets().open("mqttRouter.properties");
            props.load(in);
        } catch (IOException e) {
            Log.e("properties load error", e.getMessage());
        }
        return props;
    }

    /**
     * 对Drawable对象进行缩放
     * @param image
     * @param scaleFactor
     * @param resources
     * @return
     */
    public static Drawable scaleImage(Drawable image, float scaleFactor, Resources resources){
        if((image == null) || !(image instanceof BitmapDrawable)){
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth()*scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight()*scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b,sizeX,sizeY,false);
        image = new BitmapDrawable(resources,bitmapResized);

        return image;
    }

    /**
     * 快速发送String类型的消息
     * @param h
     * @param key
     * @param smsg
     */
    public static void sendStringMessage(Handler h, String key,String smsg){
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(key,smsg);
        msg.setData(bundle);
        h.sendMessage(msg);
    }


    /**
     * 定义消息处理过程的接口，用于实现sendMessage的消息发送lambda表达式
     * @param <T>
     */
    @FunctionalInterface
    public interface MessageProcess <T>{
        void accept(Bundle bundle,T msg);
    }

    /**
     * 通用Handler发送消息函数，可以发送各种类型数据消息
     * @param h             要操作的handler对象
     * @param process      要执行的函数参数
     * @param <T>           消息值得类型
     */
    public static <T> void sendMessage(Handler h,T vmsg, MessageProcess<T> process){

        Message msg = new Message();
        Bundle bundle = new Bundle();
        process.accept(bundle,vmsg);
        msg.setData(bundle);
        h.sendMessage(msg);
    }
}
