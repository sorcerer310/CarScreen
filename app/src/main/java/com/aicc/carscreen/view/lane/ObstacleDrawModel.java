package com.aicc.carscreen.view.lane;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.HashMap;
import java.util.Map;

import aicc_adas.AiccAdas;

/**
 * 障碍物绘制对象
 */
public class ObstacleDrawModel {
    private HashMap<Integer,SingleObstacle> mso = new HashMap<>();
    public HashMap<Integer, SingleObstacle> getHashMapSingleObstacle() {
        return mso;
    }

    public boolean drawCompleted = true;
    /**
     * 增加一个障碍物
     * @param pcipv
     */
    public void addObstacle(AiccAdas.CIPV pcipv){
        SingleObstacle so;
        if(mso.containsKey(pcipv.getCipv().getId())) {
            so = mso.get(pcipv.getCipv().getId());
        } else {
            //临时增加该语句，在增加新的障碍物时清空掉容器，这样保证始终只有一个障碍物在屏幕上
            mso.clear();
            so = new SingleObstacle();
        }

        //如果没有障碍物，设置该障碍物隐藏
        if(pcipv.getHasObstacleCase().getNumber()<=0) so.setHide(true);

        so.cipv = pcipv;
        //汽车坐标系上为x轴，左为y轴。因此前方的车的距离为x轴方向距离
        so.point[0] = pcipv.getCipv().getDistance();
        so.point[1] = 0;
        mso.put(pcipv.getCipv().getId(),so);
    }

    /**
     * 设置Bitmap对象，用于绘制
     * @param id
     * @param bitmap
     */
    public void setBitmap(int id,Bitmap bitmap){
        if(mso.get(id).bitmap==null) {
            float scaleWidth = ((float)120)/bitmap.getWidth();
            float scaleHeight = ((float)120)/bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth,scaleHeight);
            mso.get(id).bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }
    }

    /**
     * 障碍物对象
     */
    class SingleObstacle {
        private AiccAdas.CIPV cipv;
        private float[] point = new float[2];
        private float[] drawPoint = new float[2];
        private Bitmap bitmap = null;
        private boolean hide = false;
        public SingleObstacle(){}

        public AiccAdas.CIPV getCipv() {
            return cipv;
        }

        public void setCipv(AiccAdas.CIPV cipv) {
            this.cipv = cipv;
        }

        public float[] getPoint() {
            return point;
        }

        public void setPoint(float[] point) {
            this.point = point;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public float[] getDrawPoint() {
            return drawPoint;
        }

        public void setDrawPoint(float[] drawPoint) {
            this.drawPoint = drawPoint;
        }

        public boolean isHide() {
            return hide;
        }

        public void setHide(boolean hide) {
            this.hide = hide;
        }
    }
}
