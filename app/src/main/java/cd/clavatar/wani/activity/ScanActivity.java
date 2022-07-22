package cd.clavatar.wani.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.scan.Config;
import com.sunmi.scan.Image;
import com.sunmi.scan.ImageScanner;
import com.sunmi.scan.Symbol;
import com.sunmi.scan.SymbolSet;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import cd.clavatar.wani.R;
import cd.clavatar.wani.ScanConfig;
import cd.clavatar.wani.utils.BitmapTransformUtils;
import cd.clavatar.wani.utils.EaiUtil;
import cd.clavatar.wani.utils.LogUtil;
import cd.clavatar.wani.utils.SoundUtils;



public class ScanActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "ScanActivity";
    private static final int PHOTO_REQUEST_GALLERY = 0x0001;// 从相册中选择
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private SurfaceView mSurfaceView;
    private ImageScanner mImageScanner;//声明扫描器
    private Handler mAutoFocusHandler;
    private AsyncDecode asyncDecode;
    private SoundUtils soundUtils;
    private RelativeLayout mSetting;
    private RelativeLayout mOpenAlbum;
    private AtomicBoolean isRUN = new AtomicBoolean(false);
    private TextView mTimes;
    private TextView mType;
    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_min);
        setConfig();
        init();
    }

    Properties prop = new Properties();


    private void setConfig() {
        Log.i(TAG, "setConfig: ");
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            LogUtil.e(TAG, "intent.getExtras() != nul");
            //当前分辨率
            ScanConfig.CURRENT_PPI = intent.getIntExtra("CURRENT_PPI", 0X0003);
            //扫描完成声音提示
            ScanConfig.PLAY_SOUND = intent.getBooleanExtra("PLAY_SOUND", true);
            //扫描完成震动
            ScanConfig.PLAY_VIBRATE = intent.getBooleanExtra("PLAY_VIBRATE", false);
            //识别反色二维码
            ScanConfig.IDENTIFY_INVERSE_QR_CODE = intent.getBooleanExtra("IDENTIFY_INVERSE_QR_CODE", true);
            //识别画面中多个二维码
            ScanConfig.IDENTIFY_MORE_CODE = intent.getBooleanExtra("IDENTIFY_MORE_CODE", false);
            //是否显示设置按钮
            ScanConfig.IS_SHOW_SETTING = intent.getBooleanExtra("IS_SHOW_SETTING", true);
            //是否显示选择相册按钮
            ScanConfig.IS_SHOW_ALBUM = intent.getBooleanExtra("IS_SHOW_ALBUM", true);
            //是否显示闪光灯
            ScanConfig.IS_OPEN_LIGHT = intent.getBooleanExtra("IS_OPEN_LIGHT", false);
            //是否是循环模式
            ScanConfig.SCAN_MODE = intent.getBooleanExtra("SCAN_MODE", false);

        }
    }

    private void init() {
        mTimes = (TextView) findViewById(R.id.times);
        mType = (TextView) findViewById(R.id.type);
        mResult = (TextView) findViewById(R.id.result);
        mSetting = (RelativeLayout) findViewById(R.id.scan_setting);
        mOpenAlbum = (RelativeLayout) findViewById(R.id.open_album);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mAutoFocusHandler = new Handler();
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
        mImageScanner = new ImageScanner();//创建扫描器
        mImageScanner.setConfig(0, Config.X_DENSITY, 2);//行扫描间隔
        mImageScanner.setConfig(0, Config.Y_DENSITY, 2);//列扫描间隔
        mSetting.setOnClickListener(this);
        mOpenAlbum.setOnClickListener(this);
        if (!ScanConfig.IS_SHOW_ALBUM) {
            findViewById(R.id.album_item).setVisibility(View.GONE);
        }
        if (!ScanConfig.IS_SHOW_SETTING) {
            mSetting.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBeepSound();
        initScanConfig();
        startFlash();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void startFlash() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ScanConfig.IS_OPEN_LIGHT) {
                    EaiUtil.controlLamp("1", ScanConfig.LIGHT_BRIGHT_TIME, ScanConfig.LIGHT_DROWN_TIME, EaiUtil.LED_CAM_NAME);
                } else {
                    EaiUtil.controlLamp("0", ScanConfig.LIGHT_BRIGHT_TIME, ScanConfig.LIGHT_DROWN_TIME, EaiUtil.LED_CAM_NAME);
                }
            }
        }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EaiUtil.controlLamp("0", ScanConfig.LIGHT_BRIGHT_TIME, ScanConfig.LIGHT_DROWN_TIME, EaiUtil.LED_CAM_NAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAutoFocusHandler != null) {
            mAutoFocusHandler.removeCallbacksAndMessages(null);
            mAutoFocusHandler = null;
        }
        if (mImageScanner != null) {
            mImageScanner.destroy();
        }
        if (soundUtils != null) {
            soundUtils.destroy();
        }
    }

    private void initScanConfig() {
        //是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
        if (ScanConfig.IDENTIFY_MORE_CODE) {
            mImageScanner.setConfig(0, Config.ENABLE_MULTILESYMS, 1);
        } else {
            mImageScanner.setConfig(0, Config.ENABLE_MULTILESYMS, 0);
        }
        //是否开启识别反色二维码,0表示不识别，1为识别
        if (ScanConfig.IDENTIFY_INVERSE_QR_CODE) {
            mImageScanner.setConfig(0, Config.ENABLE_INVERSE, 1);
        } else {
            mImageScanner.setConfig(0, Config.ENABLE_INVERSE, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.open_album:
                openAlbum();
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.e(TAG, "surfaceCreated");
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            LogUtil.e(TAG, "Camera.open()");
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.e(TAG, "surfaceChanged");
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        try {
            setCameraParameters();
//            mCamera.setDisplayOrientation(90);//竖屏显示
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            LogUtil.e("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.e(TAG, "surfaceDestroyed");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 打开图库选择图片
     */
    private void openAlbum() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    Image source = BitmapTransformUtils.getImageByBitmap(bitmap);
                    asyncDecode = new AsyncDecode();
                    asyncDecode.execute(source);//调用异步执行解码
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void back(View view) {
        finish();
    }

    // 获取最佳的屏幕显示尺寸
    private Point getBestCameraResolution(Camera.Parameters parameters, Point screenResolution) {
        float tmp = 0f;
        float mindiff = 100f;
        float x_d_y = (float) screenResolution.x / (float) screenResolution.y;
        Size best = null;
        List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Size s : supportedPreviewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - x_d_y);
            if (tmp < mindiff) {
                mindiff = tmp;
                best = s;
            }
        }
        return new Point(best.width, best.height);
    }

    private void setCameraParameters() {
        if (mCamera == null) return;
        LogUtil.e(TAG, "ScanConfig.CURRENT_PPI=" + ScanConfig.CURRENT_PPI);
        //摄像头预览分辨率设置和图像放大参数设置，非必须，根据实际解码效果可取舍
        Camera.Parameters parameters = mCamera.getParameters();

        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        ScanConfig.BEST_RESOLUTION = getBestCameraResolution(parameters, new Point(display.getWidth(), display.getHeight()));
        switch (ScanConfig.CURRENT_PPI) {
            case ChoosePPIActivity.PPI_640_480:
                parameters.setPreviewSize(320, 240);
                break;
            case ChoosePPIActivity.PPI_352_288:
                parameters.setPreviewSize(320, 240);
                break;
            case ChoosePPIActivity.PPI_320_240:
                parameters.setPreviewSize(320, 240);
                break;
            case ChoosePPIActivity.PPI_176_144:
                parameters.setPreviewSize(320, 240);
                break;
            case ChoosePPIActivity.PPI_160_120:
                parameters.setPreviewSize(320, 240);
                break;
            default:
                parameters.setPreviewSize(320, 240);
                break;
        }
//        parameters.set("orientation", "portrait");
//        parameters.set("zoom", String.valueOf(27 / 10.0));//放大图像2.7倍
        mCamera.setParameters(parameters);
    }

    /**
     * 预览数据
     */
    PreviewCallback previewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (isRUN.compareAndSet(false, true)) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();//获取预览分辨率

                //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
                Image source = new Image(size.width, size.height, "Y800");
                //图片旋转了90度，将扫描框的TOP作为left裁剪
                source.setData(data);//填充数据
                asyncDecode = new AsyncDecode();
                asyncDecode.execute(source);//调用异步执行解码
            }
        }
    };

    private class AsyncDecode extends AsyncTask<Image, Void, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(Image... params) {

            final List<HashMap<String, String>> result = new ArrayList<>();
            Image src_data = params[0];//获取灰度数据
            //解码，返回值为0代表失败，>0表示成功
            final int data = mImageScanner.scanImage(src_data);
            if (data != 0) {
                playBeepSoundAndVibrate();//解码成功播放提示音
                SymbolSet syms = mImageScanner.getResults();//获取解码结果
                for (Symbol sym : syms) {
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put(ScanConfig.TYPE, sym.getSymbolName());
                    temp.put(ScanConfig.VALUE, sym.getResult());
                    result.add(temp);
                    if (!ScanConfig.IDENTIFY_MORE_CODE) {
                        break;
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> result) {
            super.onPostExecute(result);

            if (!result.isEmpty()) {
                LogUtil.e(TAG, "!result.isEmpty()");

                if (ScanConfig.SCAN_MODE) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleResult(result);
                        }
                    });

                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", (Serializable) result);//序列化
                    intent.putExtras(bundle);//发送数据
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                isRUN.set(false);
            }
        }

    }

    private int count = 0;

    private void handleResult(List<HashMap<String, String>> result) {
        count++;
        mTimes.setText(count + "");
        mType.setText(result.get(0).get(ScanConfig.TYPE));
        mResult.setText(result.get(0).get(ScanConfig.VALUE));
    }

    /**
     * 自动对焦回调
     */
    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            if (mAutoFocusHandler != null) {
                mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        }
    };

    //自动对焦
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (null == mCamera || null == autoFocusCallback) {
                return;
            }
            mCamera.autoFocus(autoFocusCallback);
        }
    };

    private void initBeepSound() {
        if (soundUtils == null) {
            soundUtils = new SoundUtils(this, SoundUtils.RING_SOUND);
            soundUtils.putSound(0, R.raw.beep);
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (soundUtils != null && ScanConfig.PLAY_SOUND) {
            soundUtils.playSound(0, SoundUtils.SINGLE_PLAY);
        }
        if (ScanConfig.PLAY_VIBRATE) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

}
