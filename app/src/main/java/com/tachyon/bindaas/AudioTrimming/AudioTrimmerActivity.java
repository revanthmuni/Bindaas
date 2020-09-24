//  The MIT License (MIT)

//  Copyright (c) 2018 Intuz Pvt Ltd.

//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
//  (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
//  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.tachyon.bindaas.AudioTrimming;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tachyon.bindaas.AudioTrimming.customAudioViews.MarkerView;
import com.tachyon.bindaas.AudioTrimming.customAudioViews.SamplePlayer;
import com.tachyon.bindaas.AudioTrimming.customAudioViews.SoundFile;
import com.tachyon.bindaas.AudioTrimming.customAudioViews.WaveformView;
import com.tachyon.bindaas.AudioTrimming.utils.FileUtilsForAudio;
import com.tachyon.bindaas.AudioTrimming.utils.Utility;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AudioTrimmerActivity extends AppCompatActivity implements View.OnClickListener,
        MarkerView.MarkerListener,
        WaveformView.WaveformListener {

    /* Audio trimmer*/

    private static final String TAG = "AudioTrimmerActivity";
    private TextView txtAudioCancel;
    private TextView txtAudioUpload;
    private TextView txtStartPosition;
    private TextView txtEndPosition;
    private LinearLayout llAudioCapture;
    private TextView txtAudioRecord;
    private TextView txtAudioRecordTime;
    private RelativeLayout rlAudioEdit;
    private MarkerView markerStart;
    private MarkerView markerEnd;
    private WaveformView audioWaveform;
    private TextView txtAudioRecordTimeUpdate;
    private TextView txtAudioReset;
    private TextView txtAudioDone;
    private TextView txtAudioPlay;
    private TextView txtAudioRecordUpdate;
    private TextView txtAudioCrop;
    private TextView txtAudioTitle;
    private String fileName;

    private boolean isAudioRecording = false;
    private long mRecordingLastUpdateTime;
    private double mRecordingTime;
    private boolean mRecordingKeepGoing;
    private SoundFile mLoadedSoundFile;
    private SoundFile mRecordedSoundFile;
    private SamplePlayer mPlayer;

    private Handler mHandler;

    private boolean mTouchDragging;
    private float mTouchStart;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private int mTouchInitialEndPos;
    private float mDensity;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMarkerBottomOffset;

    private int mTextLeftInset;
    private int mTextRightInset;
    private int mTextTopOffset;
    private int mTextBottomOffset;

    private int mOffset;
    private int mOffsetGoal;
    private int mFlingVelocity;
    private int mPlayEndMillSec;
    private int mWidth;
    private int mMaxPos;
    private int mStartPos;
    private int mEndPos;

    private boolean mStartVisible;
    private boolean mEndVisible;
    private int mLastDisplayedStartPos;
    private int mLastDisplayedEndPos;
    private boolean mIsPlaying = false;
    private boolean mKeyDown;
    private ProgressDialog mProgressDialog;
    private long mLoadingLastUpdateTime;
    private boolean mLoadingKeepGoing;
    private File mFile;


    public AudioTrimmerActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_trim);
        try {

            if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            mHandler = new Handler();

            txtAudioCancel = (TextView) findViewById(R.id.txtAudioCancel);
            txtAudioUpload = (TextView) findViewById(R.id.txtAudioUpload);
            txtStartPosition = (TextView) findViewById(R.id.txtStartPosition);
            txtEndPosition = (TextView) findViewById(R.id.txtEndPosition);
            llAudioCapture = (LinearLayout) findViewById(R.id.llAudioCapture);
            txtAudioRecord = (TextView) findViewById(R.id.txtAudioRecord);
            txtAudioRecordTime = (TextView) findViewById(R.id.txtAudioRecordTime);
            rlAudioEdit = (RelativeLayout) findViewById(R.id.rlAudioEdit);
            markerStart = (MarkerView) findViewById(R.id.markerStart);
            markerEnd = (MarkerView) findViewById(R.id.markerEnd);
            audioWaveform = (WaveformView) findViewById(R.id.audioWaveform);
            txtAudioRecordTimeUpdate = (TextView) findViewById(R.id.txtAudioRecordTimeUpdate);
            txtAudioReset = (TextView) findViewById(R.id.txtAudioReset);
            txtAudioDone = (TextView) findViewById(R.id.txtAudioDone);
            txtAudioPlay = (TextView) findViewById(R.id.txtAudioPlay);
            txtAudioRecordUpdate = (TextView) findViewById(R.id.txtAudioRecordUpdate);
            txtAudioCrop = (TextView) findViewById(R.id.txtAudioCrop);
            txtAudioTitle = (TextView) findViewById(R.id.txtAudioEditTitle);
            txtAudioTitle.setText("Edit Audio (Max " + (Variables.max_recording_duration/1000) + " seconds)");

            mRecordedSoundFile = null;
            mKeyDown = false;
            audioWaveform.setListener(this);

            markerStart.setListener(this);
            markerStart.setAlpha(1f);
            markerStart.setFocusable(true);
            markerStart.setFocusableInTouchMode(true);
            mStartVisible = true;

            markerEnd.setListener(this);
            markerEnd.setAlpha(1f);
            markerEnd.setFocusable(true);
            markerEnd.setFocusableInTouchMode(true);
            mEndVisible = true;

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mDensity = metrics.density;

            /**
             * Change this for marker handle as per your view
             */
            mMarkerLeftInset = (int) (17.5 * mDensity);
            mMarkerRightInset = (int) (19.5 * mDensity);
            mMarkerTopOffset = (int) (6 * mDensity);
            mMarkerBottomOffset = (int) (6 * mDensity);

            /**
             * Change this for duration text as per your view
             */

            mTextLeftInset = (int) (20 * mDensity);
            mTextTopOffset = (int) (-1 * mDensity);
            mTextRightInset = (int) (19 * mDensity);
            mTextBottomOffset = (int) (-40 * mDensity);

            txtAudioCancel.setOnClickListener(this);
            txtAudioUpload.setOnClickListener(this);
            txtAudioRecord.setOnClickListener(this);
            txtAudioDone.setOnClickListener(this);
            txtAudioPlay.setOnClickListener(this);
            txtAudioRecordUpdate.setOnClickListener(this);
            txtAudioCrop.setOnClickListener(this);
            txtAudioReset.setOnClickListener(this);

            mHandler.postDelayed(mTimerRunnable, 100);

//       pickUpFile();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }


    private Runnable mTimerRunnable = new Runnable() {
        public void run() {
            // Updating Text is slow on Android.  Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos) {
                txtStartPosition.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }

            if (mEndPos != mLastDisplayedEndPos) {
                txtEndPosition.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }

            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };

    private void pickUpFile() {
        try {
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 2000);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handlePause();
    }

    @Override
    public void onClick(View view) {
        try {
            if (view == txtAudioRecord) {
                Log.d(TAG, "onClick: start Recording");
            /*if (isAudioRecording) {
                isAudioRecording = false;
                mRecordingKeepGoing = false;
            } else {
                isAudioRecording = true;
                txtAudioRecord.setBackgroundResource(R.drawable.ic_stop_btn1);
                txtAudioRecordTime.setVisibility(View.VISIBLE);
                startRecording();
                mRecordingLastUpdateTime = Utility.getCurrentTime();
                mRecordingKeepGoing = true;
            }*//*
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload, 2000);*/

                pickUpFile();


            } else if (view == txtAudioCancel) {
                Log.d(TAG, "onClick: cancel Recording");
                handlePause();
                finish();
            } else if (view == txtAudioRecordUpdate) {
                Log.d(TAG, "onClick: txtAudioRecordUpdate Recording");
                rlAudioEdit.setVisibility(View.GONE);
                txtAudioUpload.setVisibility(View.GONE);
                llAudioCapture.setVisibility(View.VISIBLE);
                isAudioRecording = true;
                txtAudioRecord.setBackgroundResource(R.drawable.ic_stop_btn1);
                txtAudioRecordTime.setVisibility(View.VISIBLE);
                startRecording();
                mRecordingLastUpdateTime = Utility.getCurrentTime();
                mRecordingKeepGoing = true;
//            txtAudioCrop.setBackgroundResource(R.drawable.ic_crop_btn);
                txtAudioDone.setVisibility(View.GONE);
                txtAudioCrop.setVisibility(View.VISIBLE);
                txtAudioPlay.setBackgroundResource(R.drawable.ic_play_btn);
                markerStart.setVisibility(View.INVISIBLE);
                markerEnd.setVisibility(View.INVISIBLE);
                txtStartPosition.setVisibility(View.VISIBLE);
                txtEndPosition.setVisibility(View.VISIBLE);


            } else if (view == txtAudioPlay) {

                Log.d(TAG, "onClick: txtAudioPlay");
                if (!mIsPlaying) {
                    txtAudioPlay.setBackgroundResource(R.drawable.ic_pause_btn);
                } else {
                    txtAudioPlay.setBackgroundResource(R.drawable.ic_play_btn);
                }
                onPlay(mStartPos);
            } else if (view == txtAudioDone) {
                Log.d(TAG, "onClick: txtAudioDone");
                double startTime = audioWaveform.pixelsToSeconds(mStartPos);
                double endTime = audioWaveform.pixelsToSeconds(mEndPos);

                double difference = endTime - startTime;

                if (difference <= 0) {
                    Toast.makeText(AudioTrimmerActivity.this, "Trim seconds should be greater than 0 seconds", Toast.LENGTH_SHORT).show();
                } else if (difference > Variables.max_recording_duration) {
                    Toast.makeText(AudioTrimmerActivity.this, "Trim seconds should be less than 1 minute", Toast.LENGTH_SHORT).show();
                } else {
                    if (mIsPlaying) {
                        handlePause();
                    }
                    saveRingtone(0);

                    txtAudioDone.setVisibility(View.GONE);
                    txtAudioReset.setVisibility(View.VISIBLE);
//                txtAudioCrop.setBackgroundResource(R.drawable.ic_crop_btn_fill);
                    txtAudioCrop.setVisibility(View.VISIBLE);

                    markerStart.setVisibility(View.INVISIBLE);
                    markerEnd.setVisibility(View.INVISIBLE);
                    txtStartPosition.setVisibility(View.INVISIBLE);
                    txtEndPosition.setVisibility(View.INVISIBLE);
                }


            } else if (view == txtAudioReset) {

                Log.d(TAG, "onClick: txtAudioReset");
                audioWaveform.setIsDrawBorder(true);
                mPlayer = new SamplePlayer(mRecordedSoundFile);
                finishOpeningSoundFile(mRecordedSoundFile, 1);

            } else if (view == txtAudioCrop) {
                Log.d(TAG, "onClick: txtAudioCrop");
//            txtAudioCrop.setBackgroundResource(R.drawable.ic_crop_btn);
                txtAudioCrop.setVisibility(View.GONE);
                txtAudioDone.setVisibility(View.VISIBLE);
                txtAudioReset.setVisibility(View.VISIBLE);

                audioWaveform.setIsDrawBorder(true);
                audioWaveform.setBackgroundColor(getResources().getColor(R.color.colorWaveformBg));
                markerStart.setVisibility(View.VISIBLE);
                markerEnd.setVisibility(View.VISIBLE);
                txtStartPosition.setVisibility(View.VISIBLE);
                txtEndPosition.setVisibility(View.VISIBLE);


            } else if (view == txtAudioUpload) {

                double startTime = audioWaveform.pixelsToSeconds(mStartPos);
                double endTime = audioWaveform.pixelsToSeconds(mEndPos);

                double difference = endTime - startTime;

                if (difference<=Variables.max_recording_duration) {
                    Log.d(TAG, "onClick: txtAudioUpload");
                    if (txtAudioDone.getVisibility() == View.VISIBLE) {
                        if (mIsPlaying) {
                            handlePause();
                        }
                        saveRingtone(1);
                    } else {
                        Log.d(TAG, "onClick: else conditions");
                        Bundle conData = new Bundle();
                        conData.putString("INTENT_AUDIO_FILE", mFile.getAbsolutePath());
                        Intent intent = new Intent();
                        intent.putExtra("file", mFile);
                        intent.putExtras(conData);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }else{
                    Toast.makeText(this, "Audio Length should be less than 30 sec", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

            if (cursor == null) {
                return contentUri.getPath();
            } else {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }

        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void audioGraph(String mFilename) {
        try {
            mFile = new File(mFilename);
            mLoadingLastUpdateTime = Utility.getCurrentTime();
            mLoadingKeepGoing = true;
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setTitle("Loading ...");
            mProgressDialog.show();

            final SoundFile.ProgressListener listener =
                    new SoundFile.ProgressListener() {
                        public boolean reportProgress(double fractionComplete) {

                            long now = Utility.getCurrentTime();
                            if (now - mLoadingLastUpdateTime > 100) {
                                mProgressDialog.setProgress(
                                        (int) (mProgressDialog.getMax() * fractionComplete));
                                mLoadingLastUpdateTime = now;
                            }
                            return mLoadingKeepGoing;
                        }
                    };

            // Load the sound file in a background thread
            Thread mLoadSoundFileThread = new Thread() {
                public void run() {
                    try {
                        mRecordedSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);
                        if (mRecordedSoundFile == null) {
                            mProgressDialog.dismiss();
                            String name = mFile.getName().toLowerCase();
                            String[] components = name.split("\\.");
                            String err;
                            if (components.length < 2) {
                                err = "No Extension";
                            } else {
                                err = "Bad Extension";
                            }
                            final String finalErr = err;
                            Log.e(" >> ", "" + finalErr);
                            return;
                        }
                        mPlayer = new SamplePlayer(mRecordedSoundFile);
                    } catch (final Exception e) {
                        mProgressDialog.dismiss();
                        e.printStackTrace();
                        return;
                    }
                    mProgressDialog.dismiss();
                    if (mLoadingKeepGoing) {
                        Runnable runnable = new Runnable() {
                            public void run() {

                                audioWaveform.setIsDrawBorder(true);
                                finishOpeningSoundFile(mRecordedSoundFile, 0);
                                txtAudioRecord.setBackgroundResource(R.drawable.ic_stop_btn1);
                                txtAudioRecordTime.setVisibility(View.INVISIBLE);
                                txtStartPosition.setVisibility(View.VISIBLE);
                                txtEndPosition.setVisibility(View.VISIBLE);
                                markerEnd.setVisibility(View.VISIBLE);
                                markerStart.setVisibility(View.VISIBLE);
                                llAudioCapture.setVisibility(View.GONE);
                                rlAudioEdit.setVisibility(View.VISIBLE);
                                txtAudioUpload.setVisibility(View.VISIBLE);

                                txtAudioReset.setVisibility(View.VISIBLE);
                                txtAudioCrop.setVisibility(View.GONE);
                                txtAudioDone.setVisibility(View.VISIBLE);

                            }
                        };
                        mHandler.post(runnable);
                    }
                }
            };
            mLoadSoundFileThread.start();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 2000) {
//                Toast.makeText(this, "" + data.getData(), Toast.LENGTH_SHORT).show();
                    Uri uri = data.getData();
                    if (uri != null) {
                        File fil = new File(uri.toString());
                        String path = fil.getAbsolutePath();
                        fileName = path.substring(path.lastIndexOf("/")+1);
                        //fileName = real_path.substring(path.lastIndexOf("/")+1);
                        String real_path = new FileUtilsForAudio(this).getPath(uri);
                        fileName = real_path.substring(real_path.lastIndexOf("/")+1,real_path.lastIndexOf("."));
                        Log.d("Audio_Path", "Intent results::::" + path);
                        Log.d("Audio_Path", "provider path::::" + real_path);

                   /* if (real_path != null) {
                        audioGraph(real_path);
                    } else {
                        Toast.makeText(this, "Can't accessing this file try another way to choose this file", Toast.LENGTH_SHORT).show();
                    }*/
                        if (real_path != null) {
                            audioGraph(real_path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }

    /**
     * Start recording
     */
    private void startRecording() {
        try {
            final SoundFile.ProgressListener listener =
                    new SoundFile.ProgressListener() {
                        public boolean reportProgress(double elapsedTime) {
                            long now = Utility.getCurrentTime();
                            if (now - mRecordingLastUpdateTime > 5) {
                                mRecordingTime = elapsedTime;
                                // Only UI thread can update Views such as TextViews.
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        int min = (int) (mRecordingTime / 60);
                                        float sec = (float) (mRecordingTime - 60 * min);
                                        txtAudioRecordTime.setText(String.format(Locale.US, "%02d:%05.2f", min, sec));
                                    }
                                });
                                mRecordingLastUpdateTime = now;
                            }
                            return mRecordingKeepGoing;
                        }
                    };

            // Record the audio stream in a background thread
            Thread mRecordAudioThread = new Thread() {
                public void run() {
                    try {
                        mRecordedSoundFile = SoundFile.record(listener);
                        if (mRecordedSoundFile == null) {
                            finish();
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    Log.e("error >> ", "sound file null");
                                }
                            };
                            mHandler.post(runnable);
                            return;
                        }
                        mPlayer = new SamplePlayer(mRecordedSoundFile);
                    } catch (final Exception e) {
                        finish();
                        e.printStackTrace();
                        return;
                    }

                    Runnable runnable = new Runnable() {
                        public void run() {

                            audioWaveform.setIsDrawBorder(true);
                            finishOpeningSoundFile(mRecordedSoundFile, 0);
                            txtAudioRecord.setBackgroundResource(R.drawable.ic_stop_btn1);
                            txtAudioRecordTime.setVisibility(View.INVISIBLE);
                            txtStartPosition.setVisibility(View.VISIBLE);
                            txtEndPosition.setVisibility(View.VISIBLE);
                            markerEnd.setVisibility(View.VISIBLE);
                            markerStart.setVisibility(View.VISIBLE);
                            llAudioCapture.setVisibility(View.GONE);
                            rlAudioEdit.setVisibility(View.VISIBLE);
                            txtAudioUpload.setVisibility(View.VISIBLE);

                            txtAudioReset.setVisibility(View.VISIBLE);
                            txtAudioCrop.setVisibility(View.GONE);
                            txtAudioDone.setVisibility(View.VISIBLE);

                        }
                    };
                    mHandler.post(runnable);
                }
            };
            mRecordAudioThread.start();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    /**
     * After recording finish do necessary steps
     *
     * @param mSoundFile sound file
     * @param isReset    isReset
     */
    private void finishOpeningSoundFile(SoundFile mSoundFile, int isReset) {
        try {
            audioWaveform.setVisibility(View.VISIBLE);
            audioWaveform.setSoundFile(mSoundFile);
            audioWaveform.recomputeHeights(mDensity);

            mMaxPos = audioWaveform.maxPos();
            mLastDisplayedStartPos = -1;
            mLastDisplayedEndPos = -1;

            mTouchDragging = false;

            mOffset = 0;
            mOffsetGoal = 0;
            mFlingVelocity = 0;
            resetPositions();
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            if (isReset == 1) {
                mStartPos = audioWaveform.secondsToPixels(0);
                mEndPos = audioWaveform.secondsToPixels(audioWaveform.pixelsToSeconds(mMaxPos));
            }

            if (audioWaveform != null && audioWaveform.isInitialized()) {
                double seconds = audioWaveform.pixelsToSeconds(mMaxPos);
                int min = (int) (seconds / 60);
                float sec = (float) (seconds - 60 * min);
                txtAudioRecordTimeUpdate.setText(String.format(Locale.US, "%02d:%05.2f", min, sec));
            }

            updateDisplay();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    /**
     * Update views
     */

    private synchronized void updateDisplay() {

        try {
            if (mIsPlaying) {
                int now = mPlayer.getCurrentPosition();
                int frames = audioWaveform.millisecsToPixels(now);
                audioWaveform.setPlayback(frames);
                Log.e("mWidth >> ", "" + mWidth);
                setOffsetGoalNoUpdate(frames - mWidth / 2);
                if (now >= mPlayEndMillSec) {
                    handlePause();
                }
            }

            if (!mTouchDragging) {
                int offsetDelta;

                if (mFlingVelocity != 0) {
                    offsetDelta = mFlingVelocity / 30;
                    if (mFlingVelocity > 80) {
                        mFlingVelocity -= 80;
                    } else if (mFlingVelocity < -80) {
                        mFlingVelocity += 80;
                    } else {
                        mFlingVelocity = 0;
                    }

                    mOffset += offsetDelta;

                    if (mOffset + mWidth / 2 > mMaxPos) {
                        mOffset = mMaxPos - mWidth / 2;
                        mFlingVelocity = 0;
                    }
                    if (mOffset < 0) {
                        mOffset = 0;
                        mFlingVelocity = 0;
                    }
                    mOffsetGoal = mOffset;
                } else {
                    offsetDelta = mOffsetGoal - mOffset;

                    if (offsetDelta > 10)
                        offsetDelta = offsetDelta / 10;
                    else if (offsetDelta > 0)
                        offsetDelta = 1;
                    else if (offsetDelta < -10)
                        offsetDelta = offsetDelta / 10;
                    else if (offsetDelta < 0)
                        offsetDelta = -1;
                    else
                        offsetDelta = 0;

                    mOffset += offsetDelta;
                }
            }

            audioWaveform.setParameters(mStartPos, mEndPos, mOffset);
            audioWaveform.invalidate();

            markerStart.setContentDescription(
                    " Start Marker" +
                            formatTime(mStartPos));
            markerEnd.setContentDescription(
                    " End Marker" +
                            formatTime(mEndPos));

            int startX = mStartPos - mOffset - mMarkerLeftInset;
            if (startX + markerStart.getWidth() >= 0) {
                if (!mStartVisible) {
                    // Delay this to avoid flicker
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            mStartVisible = true;
                            markerStart.setAlpha(1f);
                            txtStartPosition.setAlpha(1f);
                        }
                    }, 0);
                }
            } else {
                if (mStartVisible) {
                    markerStart.setAlpha(0f);
                    txtStartPosition.setAlpha(0f);
                    mStartVisible = false;
                }
                startX = 0;
            }


            int startTextX = mStartPos - mOffset - mTextLeftInset;
            if (startTextX + markerStart.getWidth() < 0) {
                startTextX = 0;
            }


            int endX = mEndPos - mOffset - markerEnd.getWidth() + mMarkerRightInset;
            if (endX + markerEnd.getWidth() >= 0) {
                if (!mEndVisible) {
                    // Delay this to avoid flicker
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            mEndVisible = true;
                            markerEnd.setAlpha(1f);
                        }
                    }, 0);
                }
            } else {
                if (mEndVisible) {
                    markerEnd.setAlpha(0f);
                    mEndVisible = false;
                }
                endX = 0;
            }

            int endTextX = mEndPos - mOffset - txtEndPosition.getWidth() + mTextRightInset;
            if (endTextX + markerEnd.getWidth() < 0) {
                endTextX = 0;
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(
//                startX,
//                mMarkerTopOffset,
//                -markerStart.getWidth(),
//                -markerStart.getHeight());
            params.setMargins(
                    startX,
                    audioWaveform.getMeasuredHeight() / 2 + mMarkerTopOffset,
                    -markerStart.getWidth(),
                    -markerStart.getHeight());
            markerStart.setLayoutParams(params);


            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(
                    startTextX,
                    mTextTopOffset,
                    -txtStartPosition.getWidth(),
                    -txtStartPosition.getHeight());
            txtStartPosition.setLayoutParams(params);


            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(
                    endX,
                    audioWaveform.getMeasuredHeight() / 2 + mMarkerBottomOffset,
                    -markerEnd.getWidth(),
                    -markerEnd.getHeight());
//        params.setMargins(
//                endX,
//                audioWaveform.getMeasuredHeight() - markerEnd.getHeight() - mMarkerBottomOffset,
//                -markerEnd.getWidth(),
//                -markerEnd.getHeight());
            markerEnd.setLayoutParams(params);


            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(
                    endTextX,
                    audioWaveform.getMeasuredHeight() - txtEndPosition.getHeight() - mTextBottomOffset,
                    -txtEndPosition.getWidth(),
                    -txtEndPosition.getHeight());

            txtEndPosition.setLayoutParams(params);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    /**
     * Reset all positions
     */

    private void resetPositions() {
        try {
            mStartPos = audioWaveform.secondsToPixels(0.0);
            mEndPos = audioWaveform.secondsToPixels(15.0);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void setOffsetGoalNoUpdate(int offset) {
        try {
            if (mTouchDragging) {
                return;
            }

            mOffsetGoal = offset;
            if (mOffsetGoal + mWidth / 2 > mMaxPos)
                mOffsetGoal = mMaxPos - mWidth / 2;
            if (mOffsetGoal < 0)
                mOffsetGoal = 0;
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }

    private String formatTime(int pixels) {
        if (audioWaveform != null && audioWaveform.isInitialized()) {
            return formatDecimal(audioWaveform.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    private String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) (100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; //Round up
            xFrac -= 100; //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; //we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10) {
            if (xWhole < 10)
                return "0" + xWhole + ".0" + xFrac;
            else
                return xWhole + ".0" + xFrac;
        } else {
            if (xWhole < 10)
                return "0" + xWhole + "." + xFrac;
            else
                return xWhole + "." + xFrac;

        }
    }

    private int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

    private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    private void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        try {
            mTouchDragging = true;
            mTouchStart = x;
            mTouchInitialStartPos = mStartPos;
            mTouchInitialEndPos = mEndPos;
            handlePause();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void markerTouchMove(MarkerView marker, float x) {
        try {
            float delta = x - mTouchStart;

            if (marker == markerStart) {
                mStartPos = trap((int) (mTouchInitialStartPos + delta));
                mEndPos = trap((int) (mTouchInitialEndPos + delta));
            } else {
                mEndPos = trap((int) (mTouchInitialEndPos + delta));
                if (mEndPos < mStartPos)
                    mEndPos = mStartPos;
            }

            updateDisplay();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void markerTouchEnd(MarkerView marker) {
        try {
            mTouchDragging = false;
            if (marker == markerStart) {
                setOffsetGoalStart();
            } else {
                setOffsetGoalEnd();
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        try {
            mKeyDown = true;

            if (marker == markerStart) {
                int saveStart = mStartPos;
                mStartPos = trap(mStartPos - velocity);
                mEndPos = trap(mEndPos - (saveStart - mStartPos));
                setOffsetGoalStart();
            }

            if (marker == markerEnd) {
                if (mEndPos == mStartPos) {
                    mStartPos = trap(mStartPos - velocity);
                    mEndPos = mStartPos;
                } else {
                    mEndPos = trap(mEndPos - velocity);
                }

                setOffsetGoalEnd();
            }

            updateDisplay();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void markerRight(MarkerView marker, int velocity) {
        try {
            mKeyDown = true;

            if (marker == markerStart) {
                int saveStart = mStartPos;
                mStartPos += velocity;
                if (mStartPos > mMaxPos)
                    mStartPos = mMaxPos;
                mEndPos += (mStartPos - saveStart);
                if (mEndPos > mMaxPos)
                    mEndPos = mMaxPos;

                setOffsetGoalStart();
            }

            if (marker == markerEnd) {
                mEndPos += velocity;
                if (mEndPos > mMaxPos)
                    mEndPos = mMaxPos;

                setOffsetGoalEnd();
            }

            updateDisplay();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        try {
            mKeyDown = false;
            updateDisplay();

        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void markerFocus(MarkerView marker) {
        try {
            mKeyDown = false;
            if (marker == markerStart) {
                setOffsetGoalStartNoUpdate();
            } else {
                setOffsetGoalEndNoUpdate();
            }

            // Delay updaing the display because if this focus was in
            // response to a touch event, we want to receive the touch
            // event too before updating the display.
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    updateDisplay();
                }
            }, 100);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    //
    // WaveformListener
    //

    /**
     * Every time we get a message that our waveform drew, see if we need to
     * animate and trigger another redraw.
     */
    public void waveformDraw() {
        try {
            mWidth = audioWaveform.getMeasuredWidth();
            if (mOffsetGoal != mOffset && !mKeyDown)
                updateDisplay();
            else if (mIsPlaying) {
                updateDisplay();
            } else if (mFlingVelocity != 0) {
                updateDisplay();
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void waveformTouchStart(float x) {
        try {
            mTouchDragging = true;
            mTouchStart = x;
            mTouchInitialOffset = mOffset;
            mFlingVelocity = 0;
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
//        long mWaveformTouchStartMsec = Utility.getCurrentTime();
    }

    public void waveformTouchMove(float x) {
        try {
            mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
            updateDisplay();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void waveformTouchEnd() {
        /*mTouchDragging = false;
        mOffsetGoal = mOffset;

        long elapsedMsec = Utility.Utility.getCurrentTime() - mWaveformTouchStartMsec;
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                int seekMsec = audioWaveform.pixelsToMillisecs(
                        (int) (mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec &&
                        seekMsec < mPlayEndMillSec) {
                    mPlayer.seekTo(seekMsec);
                } else {
//                    handlePause();
                }
            } else {
                onPlay((int) (mTouchStart + mOffset));
            }
        }*/
    }

    private synchronized void handlePause() {
        txtAudioPlay.setBackgroundResource(R.drawable.ic_play_btn);
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        audioWaveform.setPlayback(-1);
        mIsPlaying = false;
    }

    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            int mPlayStartMsec = audioWaveform.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mEndPos);
            }
            mPlayer.setOnCompletionListener(new SamplePlayer.OnCompletionListener() {
                @Override
                public void onCompletion() {
                    handlePause();
                }
            });
            mIsPlaying = true;

            mPlayer.seekTo(mPlayStartMsec);
            mPlayer.start();
            updateDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waveformFling(float vx) {
        try{
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    public void waveformZoomIn() {
        /*audioWaveform.zoomIn();
        mStartPos = audioWaveform.getStart();
        mEndPos = audioWaveform.getEnd();
        mMaxPos = audioWaveform.maxPos();
        mOffset = audioWaveform.getOffset();
        mOffsetGoal = mOffset;
        updateDisplay();*/
    }

    public void waveformZoomOut() {
        /*audioWaveform.zoomOut();
        mStartPos = audioWaveform.getStart();
        mEndPos = audioWaveform.getEnd();
        mMaxPos = audioWaveform.maxPos();
        mOffset = audioWaveform.getOffset();
        mOffsetGoal = mOffset;
        updateDisplay();*/
    }

    /**
     * Save sound file as ringtone
     *
     * @param finish flag for finish
     */

    private void saveRingtone(final int finish) {
        try{
        double startTime = audioWaveform.pixelsToSeconds(mStartPos);
        double endTime = audioWaveform.pixelsToSeconds(mEndPos);
        final int startFrame = audioWaveform.secondsToFrames(startTime);
        final int endFrame = audioWaveform.secondsToFrames(endTime - 0.04);
        final int duration = (int) (endTime - startTime + 0.5);

        if (duration>Variables.max_recording_duration){
            Toast.makeText(this, "length is high", Toast.LENGTH_SHORT).show();
        }else {
            // Create an indeterminate progress dialog
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("Saving....");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            // Save the sound file in a background thread
            Thread mSaveSoundFileThread = new Thread() {
                public void run() {
                    // Try AAC first.
                    //String outPath = makeRingtoneFilename("Bindaas" + Calendar.getInstance().getTimeInMillis(), Utility.AUDIO_FORMAT);
                    String outPath = makeRingtoneFilename(fileName, Utility.AUDIO_FORMAT);
                    if (outPath == null) {
                        Log.e(" >> ", "Unable to find unique filename");
                        return;
                    }
                    File outFile = new File(outPath);
                    try {
                        // Write the new file
                        mRecordedSoundFile.WriteFile(outFile, startFrame, endFrame - startFrame);
                    } catch (Exception e) {
                        // log the error and try to create a .wav file instead
                        if (outFile.exists()) {
                            outFile.delete();
                        }
                        e.printStackTrace();
                    }

                    mProgressDialog.dismiss();

                    final String finalOutPath = outPath;
                    Runnable runnable = new Runnable() {
                        public void run() {
                            afterSavingRingtone("AUDIO_TEMP",
                                    finalOutPath,
                                    duration, finish);
                        }
                    };
                    mHandler.post(runnable);
                }
            };
            mSaveSoundFileThread.start();
        }
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    /**
     * After saving as ringtone set its content values
     *
     * @param title    title
     * @param outPath  output path
     * @param duration duration of file
     * @param finish   flag for finish
     */
    private void afterSavingRingtone(CharSequence title,
                                     String outPath,
                                     int duration, int finish) {
        try{
        File outFile = new File(outPath);
        long fileSize = outFile.length();

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, outPath);
        values.put(MediaStore.MediaColumns.TITLE, title.toString());
        values.put(MediaStore.MediaColumns.SIZE, fileSize);
        values.put(MediaStore.MediaColumns.MIME_TYPE, Utility.AUDIO_MIME_TYPE);

        values.put(MediaStore.Audio.Media.ARTIST, getApplicationInfo().name);
        values.put(MediaStore.Audio.Media.DURATION, duration);

        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(outPath);
        final Uri newUri = getContentResolver().insert(uri, values);
        Log.e("final URI >> ", newUri + " >> " + outPath);

        if (finish == 0) {
            loadFromFile(outPath);
        } else if (finish == 1) {
            Bundle conData = new Bundle();
            conData.putString("INTENT_AUDIO_FILE", outPath);
            Intent intent = getIntent();
            intent.putExtras(conData);
            setResult(RESULT_OK, intent);
            finish();
        }
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    /**
     * Generating name for ringtone
     *
     * @param title     title of file
     * @param extension extension for file
     * @return filename
     */

    private String makeRingtoneFilename(CharSequence title, String extension) {
        String subDir;
        String externalRootDir = Environment.getExternalStorageDirectory().getPath();
        if (!externalRootDir.endsWith("/")) {
            externalRootDir += "/";
        }
        subDir = "Bindaas/Cropped/";
        String parentDir = externalRootDir + subDir;

        // Create the parent directory
        File parentDirFile = new File(parentDir);
        parentDirFile.mkdirs();

        // If we can't write to that special path, try just writing
        // directly to the sdcard
        if (!parentDirFile.isDirectory()) {
            parentDir = externalRootDir;
        }

        // Turn the title into a filename
        String filename = "";
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetterOrDigit(title.charAt(i))) {
                filename += title.charAt(i);
            }else if(Character.isSpaceChar(title.charAt(i))){
                filename += title.charAt(i);
            }
        }

        // Try to make the filename unique
        String path = null;
        for (int i = 0; i < 100; i++) {
            String testPath;
            if (i > 0)
                testPath = parentDir + filename + i + extension;
            else
                testPath = parentDir + filename + extension;

            try {
                RandomAccessFile f = new RandomAccessFile(new File(testPath), "r");
                f.close();
            } catch (Exception e) {
                // Good, the file didn't exist
                path = testPath;
                break;
            }
        }

        return path;
    }

    /**
     * Load file from path
     *
     * @param mFilename file name
     */

    private void loadFromFile(String mFilename) {
        try{
        mFile = new File(mFilename);
//        SongMetadataReader metadataReader = new SongMetadataReader(this, mFilename);
        mLoadingLastUpdateTime = Utility.getCurrentTime();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("Loading ...");
        mProgressDialog.show();

        final SoundFile.ProgressListener listener =
                new SoundFile.ProgressListener() {
                    public boolean reportProgress(double fractionComplete) {

                        long now = Utility.getCurrentTime();
                        if (now - mLoadingLastUpdateTime > 100) {
                            mProgressDialog.setProgress(
                                    (int) (mProgressDialog.getMax() * fractionComplete));
                            mLoadingLastUpdateTime = now;
                        }
                        return mLoadingKeepGoing;
                    }
                };

        // Load the sound file in a background thread
        Thread mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mLoadedSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);
                    if (mLoadedSoundFile == null) {
                        mProgressDialog.dismiss();
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = "No Extension";
                        } else {
                            err = "Bad Extension";
                        }
                        final String finalErr = err;
                        Log.e(" >> ", "" + finalErr);
                        return;
                    }
                    mPlayer = new SamplePlayer(mLoadedSoundFile);
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    return;
                }
                mProgressDialog.dismiss();
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            audioWaveform.setVisibility(View.INVISIBLE);
                            audioWaveform.setBackgroundColor(getResources().getColor(R.color.waveformUnselectedBackground));
                            audioWaveform.setIsDrawBorder(false);
                            finishOpeningSoundFile(mLoadedSoundFile, 0);
                        }
                    };
                    mHandler.post(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }
}
