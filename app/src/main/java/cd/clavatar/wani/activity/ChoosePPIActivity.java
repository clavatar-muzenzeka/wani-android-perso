package cd.clavatar.wani.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import cd.clavatar.wani.R;
import cd.clavatar.wani.ScanConfig;

public class ChoosePPIActivity extends Activity implements View.OnClickListener {

    public static final int PPI_640_480 = 1;
    public static final int PPI_160_120 = 2;
    public static final int PPI_176_144 = 3;
    public static final int PPI_320_240 = 4;
    public static final int PPI_352_288 = 5;

    private RelativeLayout mSelectedOneBlock;
    private RelativeLayout mSelectedTwoBlock;
    private RelativeLayout mSelectedThreeBlock;
    private RelativeLayout mSelectedFourBlock;
    private RelativeLayout mSelectedFiveBlock;
    private ImageView mSelectedOneImageView;
    private ImageView mSelectedTwoImageView;
    private ImageView mSelectedThreeImageView;
    private ImageView mSelectedFourImageView;
    private ImageView mSelectedFiveImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ppi);
        innitView();
    }

    private void innitView() {
        mSelectedOneBlock = (RelativeLayout) findViewById(R.id.select_one_block);
        mSelectedTwoBlock = (RelativeLayout) findViewById(R.id.select_two_block);
        mSelectedThreeBlock = (RelativeLayout) findViewById(R.id.select_three_block);
        mSelectedFourBlock = (RelativeLayout) findViewById(R.id.select_four_block);
        mSelectedFiveBlock = (RelativeLayout) findViewById(R.id.select_five_block);

        mSelectedOneImageView = (ImageView) findViewById(R.id.select_one);
        mSelectedTwoImageView = (ImageView) findViewById(R.id.select_two);
        mSelectedThreeImageView = (ImageView) findViewById(R.id.select_three);
        mSelectedFourImageView = (ImageView) findViewById(R.id.select_four);
        mSelectedFiveImageView = (ImageView) findViewById(R.id.select_five);

        mSelectedOneBlock.setOnClickListener(this);
        mSelectedTwoBlock.setOnClickListener(this);
        mSelectedThreeBlock.setOnClickListener(this);
        mSelectedFourBlock.setOnClickListener(this);
        mSelectedFiveBlock.setOnClickListener(this);
        if (ScanConfig.CURRENT_PPI == PPI_640_480) {
            mSelectedOneImageView.setVisibility(View.VISIBLE);
        } else if (ScanConfig.CURRENT_PPI == PPI_160_120) {
            mSelectedTwoImageView.setVisibility(View.VISIBLE);
        }else if (ScanConfig.CURRENT_PPI == PPI_176_144) {
            mSelectedThreeImageView.setVisibility(View.VISIBLE);
        }else if (ScanConfig.CURRENT_PPI == PPI_320_240) {
            mSelectedFourImageView.setVisibility(View.VISIBLE);
        }else if (ScanConfig.CURRENT_PPI == PPI_352_288) {
            mSelectedFiveImageView.setVisibility(View.VISIBLE);
        }
    }

    public void back(View view) {
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_one_block:
                mSelectedOneImageView.setVisibility(View.VISIBLE);
                mSelectedTwoImageView.setVisibility(View.GONE);
                mSelectedThreeImageView.setVisibility(View.GONE);
                mSelectedFourImageView.setVisibility(View.GONE);
                mSelectedFiveImageView.setVisibility(View.GONE);
                setResult(PPI_640_480);
                finish();
                break;
            case R.id.select_two_block:
                mSelectedOneImageView.setVisibility(View.GONE);
                mSelectedTwoImageView.setVisibility(View.VISIBLE);
                mSelectedThreeImageView.setVisibility(View.GONE);
                mSelectedFourImageView.setVisibility(View.GONE);
                mSelectedFiveImageView.setVisibility(View.GONE);
                setResult(PPI_160_120);
                finish();
                break;
            case R.id.select_three_block:
                mSelectedOneImageView.setVisibility(View.GONE);
                mSelectedTwoImageView.setVisibility(View.GONE);
                mSelectedThreeImageView.setVisibility(View.VISIBLE);
                mSelectedFourImageView.setVisibility(View.GONE);
                mSelectedFiveImageView.setVisibility(View.GONE);
                setResult(PPI_176_144);
                finish();
                break;
            case R.id.select_four_block:
                mSelectedOneImageView.setVisibility(View.GONE);
                mSelectedTwoImageView.setVisibility(View.GONE);
                mSelectedThreeImageView.setVisibility(View.GONE);
                mSelectedFourImageView.setVisibility(View.VISIBLE);
                mSelectedFiveImageView.setVisibility(View.GONE);
                setResult(PPI_320_240);
                finish();
                break;
            case R.id.select_five_block:
                mSelectedOneImageView.setVisibility(View.GONE);
                mSelectedTwoImageView.setVisibility(View.GONE);
                mSelectedThreeImageView.setVisibility(View.GONE);
                mSelectedFourImageView.setVisibility(View.GONE);
                mSelectedFiveImageView.setVisibility(View.VISIBLE);
                setResult(PPI_352_288);
                finish();
                break;
            default:
                break;

        }
    }
}
