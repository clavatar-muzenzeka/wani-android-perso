package cd.clavatar.wani.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.List;

import cd.clavatar.wani.R;
import cd.clavatar.wani.ScanConfig;

public class SettingsActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Switch mScanOkWithPlaySound;
    private Switch mScanOkWithPlayVibrate;
    private Switch mIdentifyInverseQRcode;
    private Switch mIdentifyMoreCode;
    private View mVibrateBlockLine;
    private TextView mCurrentPPI;
    private ImageView mChoosePPI;
    private RelativeLayout mVibrateBlock;
    private EditText mLightBrightTime;
    private EditText mLightDrownTime;
    private Spinner mLightIndex;
    private Spinner mScanMode;
    private Spinner mOpenLight;
    private List<String> LightIndexList = new ArrayList<String>();
    private List<String> ScanModeList = new ArrayList<String>();
    private List<String> OpenLightList = new ArrayList<String>();
    private ArrayAdapter<String> LightIndexAdapter;
    private ArrayAdapter<String> ScanModeAdapter;
    private ArrayAdapter<String> OpenLightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    public void back(View view) {

        String str = mLightBrightTime.getText().toString().trim();

        if (!str.isEmpty()) {
            ScanConfig.LIGHT_BRIGHT_TIME = Integer.parseInt(str);
        }

        str = mLightDrownTime.getText().toString().trim();

        if (!str.isEmpty()) {
            ScanConfig.LIGHT_DROWN_TIME = Integer.parseInt(str);
        }

        finish();

    }

    private void initView() {
        mChoosePPI = (ImageView) findViewById(R.id.next);
        mCurrentPPI = (TextView) findViewById(R.id.current_ppi);
        mScanOkWithPlaySound = (Switch) findViewById(R.id.play_sound);
        mScanOkWithPlayVibrate = (Switch) findViewById(R.id.play_vibrate);
        mIdentifyInverseQRcode = (Switch) findViewById(R.id.inverse_qr_code);
        mIdentifyMoreCode = (Switch) findViewById(R.id.identify_more_code);
        mCurrentPPI.setOnClickListener(this);
        mChoosePPI.setOnClickListener(this);
        mScanOkWithPlaySound.setOnCheckedChangeListener(this);
        mScanOkWithPlayVibrate.setOnCheckedChangeListener(this);
        mIdentifyInverseQRcode.setOnCheckedChangeListener(this);
        mIdentifyMoreCode.setOnCheckedChangeListener(this);

        mVibrateBlock = (RelativeLayout) findViewById(R.id.vibrate_block);
        mVibrateBlockLine = findViewById(R.id.vibrate_block_line);
        if (android.os.Build.MODEL.startsWith("V1")) {
            mVibrateBlock.setVisibility(View.GONE);
            mVibrateBlockLine.setVisibility(View.GONE);
        }

        if (ScanConfig.CURRENT_PPI == ChoosePPIActivity.PPI_640_480) {

            mCurrentPPI.setText("640*480");
        } else if (ScanConfig.CURRENT_PPI == ChoosePPIActivity.PPI_160_120) {

            mCurrentPPI.setText("160*120");
        } else if (ScanConfig.CURRENT_PPI == ChoosePPIActivity.PPI_176_144) {

            mCurrentPPI.setText("176*144");
        } else if (ScanConfig.CURRENT_PPI == ChoosePPIActivity.PPI_320_240) {

            mCurrentPPI.setText("320*240");
        } else if (ScanConfig.CURRENT_PPI == ChoosePPIActivity.PPI_352_288) {

            mCurrentPPI.setText("352*288");
        }
        mScanOkWithPlaySound.setChecked(ScanConfig.PLAY_SOUND);
        mScanOkWithPlayVibrate.setChecked(ScanConfig.PLAY_VIBRATE);
        mIdentifyInverseQRcode.setChecked(ScanConfig.IDENTIFY_INVERSE_QR_CODE);
        mIdentifyMoreCode.setChecked(ScanConfig.IDENTIFY_MORE_CODE);

        mScanMode = (Spinner) findViewById(R.id.scan_mode);
        mOpenLight = (Spinner) findViewById(R.id.is_open_light);
        mLightIndex = (Spinner) findViewById(R.id.light_index);
        mLightBrightTime = (EditText) findViewById(R.id.light_bright_time);
        mLightDrownTime = (EditText) findViewById(R.id.light_drown_time);

        mLightBrightTime.setText("" + ScanConfig.LIGHT_BRIGHT_TIME);
        mLightDrownTime.setText("" + ScanConfig.LIGHT_DROWN_TIME);

        ScanModeList.add("单次");
        ScanModeList.add("循环");

        ScanModeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, ScanModeList);
        ScanModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mScanMode.setAdapter(ScanModeAdapter);

        if (ScanConfig.SCAN_MODE) {
            mScanMode.setSelection(1, true);
        } else {
            mScanMode.setSelection(0, true);
        }
        mScanMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (arg2 == 0) {
                    ScanConfig.SCAN_MODE = false;
                } else if (arg2 == 1) {
                    ScanConfig.SCAN_MODE = true;
                }

                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

                arg0.setVisibility(View.VISIBLE);
            }
        });

        LightIndexList.add("NFC");
        LightIndexList.add("相机");

        LightIndexAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, LightIndexList);
        LightIndexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mLightIndex.setAdapter(LightIndexAdapter);
        mLightIndex.setSelection(ScanConfig.LIGHT_INDEX, true);

        mLightIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                ScanConfig.LIGHT_INDEX = arg2;
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

                arg0.setVisibility(View.VISIBLE);
            }
        });

        OpenLightList.add("关");
        OpenLightList.add("开");

        OpenLightAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, OpenLightList);
        OpenLightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mOpenLight.setAdapter(OpenLightAdapter);

        if (ScanConfig.IS_OPEN_LIGHT) {
            mOpenLight.setSelection(1, true);
        } else {
            mOpenLight.setSelection(0, true);
        }


        mOpenLight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (arg2 == 0) {
                    ScanConfig.IS_OPEN_LIGHT = false;
                } else if (arg2 == 1) {
                    ScanConfig.IS_OPEN_LIGHT = true;
                }

                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

                arg0.setVisibility(View.VISIBLE);
            }
        });


    }


    private static final int CHOOSE_PPI = 0x0001;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
            case R.id.current_ppi:
                startActivityForResult(new Intent(this, ChoosePPIActivity.class), CHOOSE_PPI);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PPI) {
            switch (resultCode) {
                case ChoosePPIActivity.PPI_640_480:
                    ScanConfig.CURRENT_PPI = ChoosePPIActivity.PPI_640_480;
                    mCurrentPPI.setText("640*480");
                    break;
                case ChoosePPIActivity.PPI_160_120:
                    ScanConfig.CURRENT_PPI = ChoosePPIActivity.PPI_160_120;
                    mCurrentPPI.setText("160*120");
                    break;
                case ChoosePPIActivity.PPI_176_144:
                    ScanConfig.CURRENT_PPI = ChoosePPIActivity.PPI_176_144;
                    mCurrentPPI.setText("176*144");
                    break;
                case ChoosePPIActivity.PPI_320_240:
                    ScanConfig.CURRENT_PPI = ChoosePPIActivity.PPI_320_240;
                    mCurrentPPI.setText("320*240");
                    break;
                case ChoosePPIActivity.PPI_352_288:
                    ScanConfig.CURRENT_PPI = ChoosePPIActivity.PPI_352_288;
                    mCurrentPPI.setText("352*288");
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.play_sound:
                ScanConfig.PLAY_SOUND = isChecked;
                break;
            case R.id.play_vibrate:
                ScanConfig.PLAY_VIBRATE = isChecked;
                break;
            case R.id.inverse_qr_code:
                ScanConfig.IDENTIFY_INVERSE_QR_CODE = isChecked;
                break;
            case R.id.identify_more_code:
                ScanConfig.IDENTIFY_MORE_CODE = isChecked;
                break;
            default:
                break;
        }
    }
}
