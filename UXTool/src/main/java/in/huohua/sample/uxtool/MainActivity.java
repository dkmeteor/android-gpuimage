package in.huohua.sample.uxtool;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 1;
    private GPUImageFilter mFilter;
    private GPUImageView mGPUImageView;
    private GPUImageFilterGroup group;
    private GPUImageBrightnessFilter brightnessFilter;
    private GPUImageSaturationFilter saturationFilter;
    private GPUImageContrastFilter contrastFilter;
    private GPUImageHueFilter hueFilter;
    private GPUImageExposureFilter exposureFilter;


    @Bind(R.id.seek_brightness)
    SeekBar seekBrightness;
    @Bind(R.id.seek_saturation)
    SeekBar seekSaturation;
    @Bind(R.id.seek_contrast)
    SeekBar seekContrast;
    @Bind(R.id.seek_hue)
    SeekBar seekHue;
    @Bind(R.id.seek_exposure)
    SeekBar seekExposure;

    ClipboardManager myClipboard;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);
        mGPUImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
            }
        });
        initFilters();
        initSeekBars();


        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = "brightness: " + calculateBrightNess(seekBrightness.getProgress()) + "\n" +
                "saturation: " + calculateSaturation(seekSaturation.getProgress()) + "\n" +
                "contrast: " + calculateContrast(seekContrast.getProgress()) + "\n" +
                "hue: " + calculateHue(seekHue.getProgress()) + "\n" +
                "exposure: " + calculateExposure(seekExposure.getProgress());

        Dialog dialog = new AlertDialog.Builder(this).setMessage(message).create();
        dialog.show();

        ClipData myClip;
        myClip = ClipData.newPlainText("text", message);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(this, "以上内容已复制到剪贴板", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Show Detail");
        return super.onCreateOptionsMenu(menu);
    }

    private void initSeekBars() {
        //default 0 (-1.0, 1.0)
        seekBrightness.setProgress(500);
        //default 1 (0, 1)
        seekSaturation.setProgress(500);
        //default 1 (0, 4)
        seekContrast.setProgress(250);
        //default 90 (0, 360)
        seekHue.setProgress(0);
        //defalut 0 (-10, 10)
        seekExposure.setProgress(500);

        seekBrightness.setOnSeekBarChangeListener(mOnSeekBarChangeListene);
        seekSaturation.setOnSeekBarChangeListener(mOnSeekBarChangeListene);
        seekContrast.setOnSeekBarChangeListener(mOnSeekBarChangeListene);
        seekHue.setOnSeekBarChangeListener(mOnSeekBarChangeListene);
        seekExposure.setOnSeekBarChangeListener(mOnSeekBarChangeListene);
    }

    private void initFilters() {
        brightnessFilter = new GPUImageBrightnessFilter();
        saturationFilter = new GPUImageSaturationFilter();
        contrastFilter = new GPUImageContrastFilter();
        hueFilter = new GPUImageHueFilter(0f);
        exposureFilter = new GPUImageExposureFilter(0f);

        group = new GPUImageFilterGroup();
        group.addFilter(brightnessFilter);
        group.addFilter(saturationFilter);
        group.addFilter(contrastFilter);
        group.addFilter(hueFilter);
        group.addFilter(exposureFilter);

        applyFilterTo(group);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    handleImage(data.getData());
                } else {
                    finish();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void applyFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
        }
    }

    private void handleImage(final Uri selectedImage) {
        mGPUImageView.setImage(selectedImage);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListene = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (seekBar == seekBrightness) {
                brightnessFilter.setBrightness(calculateBrightNess(i));
            } else if (seekBar == seekSaturation) {
                saturationFilter.setSaturation(calculateSaturation(i));
            } else if (seekBar == seekContrast) {
                contrastFilter.setContrast(calculateContrast(i));
            } else if (seekBar == seekHue) {
                hueFilter.setHue(calculateHue(i));
            } else if (seekBar == seekExposure) {
                exposureFilter.setExposure(calculateExposure(i));
            }
            mGPUImageView.requestRender();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    private float calculateBrightNess(int progress) {
        return (progress - 500) / 500f;
    }

    private float calculateSaturation(int progress) {
        return progress / 500f;
    }

    private float calculateContrast(int progress) {
        return progress / 250f;
    }

    private float calculateHue(int progress) {
        return progress / 250f * 90f;
    }

    private float calculateExposure(int progress) {
        return (progress - 500f) / 50f;
    }
}
