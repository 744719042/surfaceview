package com.example.surfaceview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Runnable,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int DEFAULT_WAVE_LENGTH = 200;
    private static final int DEFAULT_AMPLITUDE = 200;

    private SurfaceView surfaceView;
    private Button button;
    private SeekBar waveLength;
    private SeekBar amplitude;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private volatile int width = 0;
    private volatile int height = 0;
    private volatile boolean isSurfaceReady = false;
    private volatile boolean isRunning = false;
    private volatile int waveLengthValue = DEFAULT_WAVE_LENGTH;
    private volatile int amplitudeValue = DEFAULT_AMPLITUDE;
    private int phaseValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        surfaceHolder = surfaceView.getHolder();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        waveLength = (SeekBar) findViewById(R.id.wavelength);
        amplitude = (SeekBar) findViewById(R.id.amplitude);
        waveLength.setOnSeekBarChangeListener(this);
        amplitude.setOnSeekBarChangeListener(this);
        waveLength.setProgress(waveLengthValue);
        amplitude.setProgress(amplitudeValue);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceReady = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceReady = false;
    }

    @Override
    public void run() {
        while (isSurfaceReady) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                if (isRunning) {
                    phaseValue = (phaseValue + 1) % 360;
                    Path path = new Path();
                    int offsetY = height / 2;
                    path.moveTo(0, (float) (Math.sin(phaseValue * 3 * Math.PI / 180) * amplitudeValue) + offsetY);
                    int repeatCount = width * 90 / waveLengthValue;
                    for (int i = 1; i < repeatCount; i++) {
                        path.lineTo(i * waveLengthValue / 90,
                                (float) (Math.sin((i + phaseValue) * 3 * Math.PI / 180) * amplitudeValue) + offsetY);
                    }
                    canvas.drawPath(path, paint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            if (width == 0 || height == 0) {
                width = surfaceView.getWidth();
                height = surfaceView.getHeight();
            }

            if (width <= 0 || height <= 0) {
                return;
            }

            if (isRunning) {
                button.setText(R.string.surface_start);
                isRunning = false;
            } else {
                button.setText(R.string.surface_stop);
                isRunning = true;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == waveLength) {
            waveLengthValue = progress;
        } else if (seekBar == amplitude) {
            amplitudeValue = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
