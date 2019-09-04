package com.dntutty.rtmp;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Iterator;
import java.util.List;


public class CameraHelper implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraHelper";
    private Activity activity;
    private int mHeight;
    private int mWidth;
    private int mCameraId;
    private Camera mCamera;
    private byte[] buffer;
    private SurfaceHolder mSurfaceHolder;
    private Camera.PreviewCallback mPreviewCallback;
    private int mRotation;
    private OnChangedSizeListener mOnChangedSizeListener;


    public CameraHelper(Activity activity, int mCameraId, int mWidth, int mHeight) {
        this.activity = activity;
        this.mCameraId = mCameraId;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview();
    }

    private void startPreview() {
        try {
//            获得camera对象
            mCamera = Camera.open(mCameraId);
//            配置camera的属性
            Camera.Parameters parameters = mCamera.getParameters();
//            设置预览数据格式为nv21
            parameters.setPreviewFormat(ImageFormat.NV21);
//            设置预览的宽高
            setPreviewSize(parameters);
//            设置摄像头 图像传感器的角度、方向
            setPreviewOrientation(parameters);
            mCamera.setParameters(parameters);
            buffer = new byte[mWidth * mHeight * 3 / 2];
//            数据缓存区
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);
//            设置预览画面
            mCamera.setPreviewDisplay(mSurfaceHolder);
            if (mOnChangedSizeListener != null) {
                mOnChangedSizeListener.onChanged(mWidth, mHeight);
            }

            mCamera.startPreview();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPreviewOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        mRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (mRotation) {
//            屏幕的原始方向的逆时针角度
            case Surface.ROTATION_0:
                degrees = 0;
                break;
//                横屏 左边是头部（home键在右边）
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
//                    横屏 头部在右边
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
//        弥补摄像头方向与展示方向的差值
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
//            compensate the mirror
            result = (360 - result) % 360;
        } else {//back-facing 无镜像
            result = (info.orientation - degrees + 360) % 360;
        Log.e(TAG, "setPreviewOrientation: info.orientation: "+info.orientation+" degrees :"+degrees );
        }
//        设置角度，参考源码注释
//        展示方向的差值
        mCamera.setDisplayOrientation(result);

    }

    private void setPreviewSize(Camera.Parameters parameters) {
//        获取摄像头支持的宽、高
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = supportedPreviewSizes.get(0);
        Log.d(TAG, "setPreviewSize: Camera支持： " + size.width + "x" + size.height);
//        选择一个与设置的差距最小的支持分辨率
        int delta = Math.abs(size.height * size.width - mWidth * mHeight);
        supportedPreviewSizes.remove(0);
        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
//        遍历
        while (iterator.hasNext()) {
            Camera.Size next = iterator.next();
            Log.d(TAG, "setPreviewSize: 支持" + next.width + "x" + next.height);
            int n = Math.abs(next.height * next.width - mWidth * mHeight);
            if (n < delta) {
                delta = n;
                size = next;
            }
        }
        mWidth = size.width;
        mHeight = size.height;
        parameters.setPreviewSize(mWidth, mHeight);
        Log.d(TAG, "setPreviewSize 预览分辨率width:" + size.width + " height:" + size.height);
    }

    private void stopPreview() {
        if (mCamera != null) {
            //预览数据回调接口
            mCamera.setPreviewCallback(null);
//            停止预览
            mCamera.stopPreview();
//            释放摄像头
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mPreviewCallback != null) {
            mPreviewCallback.onPreviewFrame(data, camera);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        释放摄像头
        stopPreview();
//        开启摄像头
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    public void setOnChangedSizeListener(OnChangedSizeListener listener) {
        mOnChangedSizeListener = listener;
    }

    public void setPreviewDisplay(SurfaceHolder mSurfaceHolder) {
        this.mSurfaceHolder = mSurfaceHolder;
        this.mSurfaceHolder.addCallback(this);
    }

    public interface OnChangedSizeListener {
        void onChanged(int w, int h);
    }
}
