package com.development.borissu.demoapp.activities;

import android.content.Context;
import android.os.Vibrator;
import android.os.Bundle;
import android.widget.Toast;

import com.development.borissu.demoapp.R;

import butterknife.OnClick;

public class VibrationActivity extends BaseActivity {

    Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }


    @OnClick(R.id.btn_hasVibrator)
    public void onClickHasVibrator() {
        Toast.makeText(this
                , (mVibrator.hasVibrator() ? "此裝置有震動器" : "此裝置沒有震動器")
                , Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.btn_short)
    public void onClickShort() {
        mVibrator.cancel();
        mVibrator.vibrate(new long[]{100, 200, 100, 200}, 0);
    }

    @OnClick(R.id.btn_long)
    public void onClickLong() {
        mVibrator.cancel();
        mVibrator.vibrate(new long[]{100, 100, 100, 1000}, 0);
    }

    @OnClick(R.id.btn_effect)
    public void onClickEffect() {
        mVibrator.cancel();
        mVibrator.vibrate(new long[]{500, 100, 500, 100, 500, 100}, 0);
    }

    @OnClick(R.id.btn_cancel)
    public void onClickCancel() {
        mVibrator.cancel();

    }
}
