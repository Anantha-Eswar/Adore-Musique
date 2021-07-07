package com.anantheswar.adoremusique.nowplaying;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.anantheswar.adoremusique.MusicPlayer;
import com.anantheswar.adoremusique.MusicService;
import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.activities.BaseActivity;
import com.anantheswar.adoremusique.adapters.BaseQueueAdapter;
import com.anantheswar.adoremusique.adapters.SlidingQueueAdapter;
import com.anantheswar.adoremusique.dataloaders.QueueLoader;
import com.anantheswar.adoremusique.listeners.MusicStateListener;
import com.anantheswar.adoremusique.timely.TimelyView;
import com.anantheswar.adoremusique.utils.Helpers;
import com.anantheswar.adoremusique.utils.NavigationUtils;
import com.anantheswar.adoremusique.utils.PreferencesUtility;
import com.anantheswar.adoremusique.utils.SlideTrackSwitcher;
import com.anantheswar.adoremusique.utils.TimberUtils;
import com.anantheswar.adoremusique.widgets.CircularSeekBar;
import com.anantheswar.adoremusique.widgets.DividerItemDecoration;
import com.anantheswar.adoremusique.widgets.PlayPauseButton;
import com.anantheswar.adoremusique.widgets.PlayPauseDrawable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.security.InvalidParameterException;

public class BaseNowplayingFragment extends Fragment implements MusicStateListener {

    private MaterialIconView previous, next;
    private PlayPauseButton mPlayPause;
    private PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    private FloatingActionButton playPauseFloating;
    private View playPauseWrapper;

    private String ateKey;
    private int overflowcounter = 0;
    private TextView songtitle, songalbum, songartist, songduration, elapsedtime;
    private SeekBar mProgress;
    boolean fragmentPaused = false;

    private CircularSeekBar mCircularProgress;
    private BaseQueueAdapter mAdapter;
    private SlidingQueueAdapter slidingQueueAdapter;

    private TimelyView timelyView11, timelyView12, timelyView13, timelyView14, timelyView15;
    private TextView hourColon;
    private int[] timeArr = new int[]{0, 0, 0, 0, 0};
    private Handler mElapsedTimeHandler;
    private boolean duetoplaypause = false;

    public ImageView albumart, shuffle, repeat;
    public int accentColor;
    public RecyclerView recyclerView;

    //seekbar
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            if (mProgress != null) {
                mProgress.setProgress((int) position);
                if (elapsedtime != null && getActivity() != null)
                    elapsedtime.setText(TimberUtils.makeShortTimeString(getActivity(), position / 1000));
            }
            overflowcounter--;
            int delay = 250; //not sure why this delay was so high before
            if (overflowcounter < 0 && !fragmentPaused) {
                    overflowcounter++;
                    mProgress.postDelayed(mUpdateProgress, delay); //delay
            }
        }
    };

    //circular seekbar
    public Runnable mUpdateCircularProgress = new Runnable() {

        @Override
        public void run() {
            long position = MusicPlayer.position();
            if (mCircularProgress != null) {
                mCircularProgress.setProgress((int) position);
                if (elapsedtime != null && getActivity() != null)
                    elapsedtime.setText(TimberUtils.makeShortTimeString(getActivity(), position / 1000));

            }
            overflowcounter--;
            //Set delay of circular bar to update while playing
            if (MusicPlayer.isPlaying()) {
                int delay = (int) (1500 - (position % 1000));
                if (overflowcounter < 0 && !fragmentPaused) {
                    overflowcounter++;
                    mCircularProgress.postDelayed(mUpdateCircularProgress, delay);
                }
            }

        }
    };

    //Delay millis will set the update to the Elapsed time.
    public Runnable mUpdateElapsedTime = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
                String time = TimberUtils.makeShortTimeString(getActivity(), MusicPlayer.position() / 1000);
                if (time.length() < 5) {
                    timelyView11.setVisibility(View.GONE);
                    timelyView12.setVisibility(View.GONE);
                    hourColon.setVisibility(View.GONE);
                    tv13(time.charAt(0) - '0');
                    tv14(time.charAt(2) - '0');
                    tv15(time.charAt(3) - '0');
                } else if (time.length() == 5) {
                    timelyView12.setVisibility(View.VISIBLE);
                    tv12(time.charAt(0) - '0');
                    tv13(time.charAt(1) - '0');
                    tv14(time.charAt(3) - '0');
                    tv15(time.charAt(4) - '0');
                } else {
                    timelyView11.setVisibility(View.VISIBLE);
                    hourColon.setVisibility(View.VISIBLE);
                    tv11(time.charAt(0) - '0');
                    tv12(time.charAt(2) - '0');
                    tv13(time.charAt(3) - '0');
                    tv14(time.charAt(5) - '0');
                    tv15(time.charAt(6) - '0');
                }
                mElapsedTimeHandler.postDelayed(this, 1000);
            }

        }
    };

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            } else {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                    if (recyclerView != null && recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();
                }
            }, 200);


        }
    };

    private final View.OnClickListener mFLoatingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if(MusicPlayer.getCurrentTrack() == null) {
                Toast.makeText(getContext(), getString(R.string.now_playing_no_track_selected), Toast.LENGTH_SHORT).show();
            } else {
                playPauseDrawable.transformToPlay(true);
                playPauseDrawable.transformToPause(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                        if (recyclerView != null && recyclerView.getAdapter() != null)
                            recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, 250);
            }



        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ateKey = Helpers.getATEKey(getActivity());
        accentColor = Config.accentColor(getActivity(), ateKey);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.now_playing, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_go_to_album:
                NavigationUtils.goToAlbum(getContext(), MusicPlayer.getCurrentAlbumId());
                break;
            case R.id.menu_go_to_artist:
                NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                break;
            case R.id.action_lyrics:
                NavigationUtils.goToLyrics(getContext());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPaused = false;
        if (mProgress != null)
            mProgress.postDelayed(mUpdateProgress, 10);

        if (mCircularProgress != null)
            mCircularProgress.postDelayed(mUpdateCircularProgress, 10);
    }

    public void setSongDetails(View view) {
        albumart = view.findViewById(R.id.album_art);
        AlphaAnimation one = new AlphaAnimation(0.0f, 1.0f);
        one.setDuration(1100);
        one.setStartOffset(150);
        AlphaAnimation two = new AlphaAnimation(0.0f, 1.0f);
        two.setDuration(1500);
        two.setStartOffset(500);
        shuffle = view.findViewById(R.id.shuffle);

        repeat = view.findViewById(R.id.repeat);
        next = view.findViewById(R.id.next);

        previous = view.findViewById(R.id.previous);

        mPlayPause = view.findViewById(R.id.playpause);
        playPauseFloating = view.findViewById(R.id.playpausefloating);
        playPauseWrapper = view.findViewById(R.id.playpausewrapper);

        songtitle = view.findViewById(R.id.song_title);
        songtitle.startAnimation(one);
        songalbum = view.findViewById(R.id.song_album);
        songartist = view.findViewById(R.id.song_artist);
        songartist.startAnimation(two);
        songduration = view.findViewById(R.id.song_duration);
        elapsedtime = view.findViewById(R.id.song_elapsed_time);

        timelyView11 = view.findViewById(R.id.timelyView11);
        timelyView12 = view.findViewById(R.id.timelyView12);
        timelyView13 = view.findViewById(R.id.timelyView13);
        timelyView14 = view.findViewById(R.id.timelyView14);
        timelyView15 = view.findViewById(R.id.timelyView15);
        hourColon = view.findViewById(R.id.hour_colon);

        mProgress = view.findViewById(R.id.song_progress);
        mCircularProgress = view.findViewById(R.id.song_progress_circular);

        recyclerView = view.findViewById(R.id.queue_recyclerview);


        songtitle.setSelected(true);


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
        if (mPlayPause != null && getActivity() != null) {
            mPlayPause.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }

        if (playPauseFloating != null) {
            playPauseDrawable.setColorFilter(TimberUtils.getBlackWhiteColor(accentColor), PorterDuff.Mode.MULTIPLY);
            playPauseFloating.setImageDrawable(playPauseDrawable);
            if (MusicPlayer.isPlaying())
                playPauseDrawable.transformToPause(false);
            else playPauseDrawable.transformToPlay(false);
        }

        if (mCircularProgress != null) {
            mCircularProgress.setCircleProgressColor(accentColor);
            mCircularProgress.setPointerColor(accentColor);
            mCircularProgress.setPointerHaloColor(accentColor);
        }

        if (timelyView11 != null) {
            String time = TimberUtils.makeShortTimeString(getActivity(), MusicPlayer.position() / 1000);
            if (time.length() < 5) {
                timelyView11.setVisibility(View.GONE);
                timelyView12.setVisibility(View.GONE);
                hourColon.setVisibility(View.GONE);

                changeDigit(timelyView13, time.charAt(0) - '0');
                changeDigit(timelyView14, time.charAt(2) - '0');
                changeDigit(timelyView15, time.charAt(3) - '0');

            } else if (time.length() == 5) {
                timelyView12.setVisibility(View.VISIBLE);
                changeDigit(timelyView12, time.charAt(0) - '0');
                changeDigit(timelyView13, time.charAt(1) - '0');
                changeDigit(timelyView14, time.charAt(3) - '0');
                changeDigit(timelyView15, time.charAt(4) - '0');
            } else {
                timelyView11.setVisibility(View.VISIBLE);
                hourColon.setVisibility(View.VISIBLE);
                changeDigit(timelyView11, time.charAt(0) - '0');
                changeDigit(timelyView12, time.charAt(2) - '0');
                changeDigit(timelyView13, time.charAt(3) - '0');
                changeDigit(timelyView14, time.charAt(5) - '0');
                changeDigit(timelyView15, time.charAt(6) - '0');
            }
        }

        setSongDetails();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
    }

    private void setSongDetails() {
        updateSongDetails();

        if (recyclerView != null)
            setQueueSongs();

        setSeekBarListener();

        if (next != null) {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.next();
                            notifyPlayingDrawableChange();
                        }
                    }, 100);

                }
            });
        }
        if (previous != null) {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.previous(getActivity(), false);
                            notifyPlayingDrawableChange();
                        }
                    }, 100);

                }
            });
        }

        if (playPauseWrapper != null)
            playPauseWrapper.setOnClickListener(mButtonListener);

        if (playPauseFloating != null)
            playPauseFloating.setOnClickListener(mFLoatingButtonListener);

        updateShuffleState();
        updateRepeatState();

    }

    public void updateShuffleState() {
        if (shuffle != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                    .setSizeDp(30);

            if (getActivity() != null) {
                if (MusicPlayer.getShuffleMode() == 0) {
                    builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                } else builder.setColor(Config.accentColor(getActivity(), ateKey));
            }

            shuffle.setImageDrawable(builder.build());
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleShuffle();
                    updateShuffleState();
                    updateRepeatState();
                }
            });
        }
    }

    public void updateRepeatState() {
        if (repeat != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setSizeDp(30);

                if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_NONE) {
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                    builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_CURRENT) {
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
                    builder.setColor(Config.accentColor(getActivity(), ateKey));
                } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_ALL) {
                    builder.setColor(Config.accentColor(getActivity(), ateKey));
                    builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                }


            repeat.setImageDrawable(builder.build());
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleRepeat();
                    updateRepeatState();
                    updateShuffleState();
                }
            });
        }
    }

    private void setSeekBarListener() {
        if (mProgress != null)
            mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        MusicPlayer.seek((long) i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        if (mCircularProgress != null) {
            mCircularProgress.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        MusicPlayer.seek((long) progress);
                    }
                }

                @Override
                public void onStopTrackingTouch(CircularSeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(CircularSeekBar seekBar) {

                }
            });
        }
    }

    public void updateSongDetails() {
        //do not reload image if it was a play/pause change
        if (!duetoplaypause) {
            if (albumart != null) {
                ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .showImageOnFail(R.drawable.ic_empty_music2)
                                .build(), new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                doAlbumArtStuff(loadedImage);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
                                doAlbumArtStuff(failedBitmap);
                            }

                        });
            }
            if (songtitle != null && MusicPlayer.getTrackName() != null) {
                    songtitle.setText(MusicPlayer.getTrackName());
                    if(MusicPlayer.getTrackName().length() <= 23){
                        songtitle.setTextSize(25);
                    }
                    else if(MusicPlayer.getTrackName().length() >= 30){
                        songtitle.setTextSize(18);
                    }
                    else{
                        songtitle.setTextSize(18 + (MusicPlayer.getTrackName().length() - 24));
                    }
                    Log.v("BaseNowPlayingFrag", "Title Text Size: " + songtitle.getTextSize());
            }
            if (songartist != null) {
                songartist.setText(MusicPlayer.getArtistName());
                songartist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                    }
                });
            }
            if (songalbum != null)
                songalbum.setText(MusicPlayer.getAlbumName());

        }
        duetoplaypause = false;

        if (mPlayPause != null)
            updatePlayPauseButton();

        if (playPauseFloating != null)
            updatePlayPauseFloatingButton();

        if (songduration != null && getActivity() != null)
            songduration.setText(TimberUtils.makeShortTimeString(getActivity(), MusicPlayer.duration() / 1000));

        if (mProgress != null) {
            mProgress.setMax((int) MusicPlayer.duration());
            if (mUpdateProgress != null) {
                mProgress.removeCallbacks(mUpdateProgress);
            }
            mProgress.postDelayed(mUpdateProgress, 10);
        }
        if (mCircularProgress != null) {
            mCircularProgress.setMax((int) MusicPlayer.duration());
            if (mUpdateCircularProgress != null) {
                mCircularProgress.removeCallbacks(mUpdateCircularProgress);
            }
            mCircularProgress.postDelayed(mUpdateCircularProgress, 10);
        }

        if (timelyView11 != null) {
            mElapsedTimeHandler = new Handler();
            mElapsedTimeHandler.postDelayed(mUpdateElapsedTime, 0);
        }
    }

    public void setQueueSongs() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //load queue songs in asynctask
        if (getActivity() != null)
            new loadQueueSongs().execute("");

    }

    public void updatePlayPauseButton() {
        if (MusicPlayer.isPlaying()) {
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
        } else {
            if (mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
        }
    }

    public void updatePlayPauseFloatingButton() {
        if (MusicPlayer.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);
        }
    }

    public void notifyPlayingDrawableChange() {
        int position = MusicPlayer.getQueuePosition();
        BaseQueueAdapter.currentlyPlayingPosition = position;
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        updateSongDetails();

        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setMusicStateListener() {
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
    }

    public void doAlbumArtStuff(Bitmap loadedImage) {

    }

    public void changeDigit(TimelyView tv, int end) {
        ObjectAnimator obja = tv.animate(end);
        obja.setDuration(500);
        obja.start();
    }

    public void changeDigit(TimelyView tv, int start, int end) {
        try {
            ObjectAnimator obja = tv.animate(start, end);
            obja.setDuration(500);
            obja.start();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
    }

    public void tv11(int a) {
        if (a != timeArr[0]) {
            changeDigit(timelyView11, timeArr[0], a);
            timeArr[0] = a;
        }
    }

    public void tv12(int a) {
        if (a != timeArr[1]) {
            changeDigit(timelyView12, timeArr[1], a);
            timeArr[1] = a;
        }
    }

    public void tv13(int a) {
        if (a != timeArr[2]) {
            changeDigit(timelyView13, timeArr[2], a);
            timeArr[2] = a;
        }
    }

    public void tv14(int a) {
        if (a != timeArr[3]) {
            changeDigit(timelyView14, timeArr[3], a);
            timeArr[3] = a;
        }
    }

    public void tv15(int a) {
        if (a != timeArr[4]) {
            changeDigit(timelyView15, timeArr[4], a);
            timeArr[4] = a;
        }
    }

    protected void initGestures(View v) {
        if (PreferencesUtility.getInstance(v.getContext()).isGesturesEnabled()) {
            new SlideTrackSwitcher() {
                @Override
                public void onSwipeBottom() {
                    getActivity().finish();
                }
            }.attach(v);
        }
    }

    private class loadQueueSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null) {
                mAdapter = new BaseQueueAdapter((AppCompatActivity) getActivity(), QueueLoader.getQueueSongs(getActivity()));
                return "Executed";
            } else return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                recyclerView.setAdapter(mAdapter);
                if (getActivity() != null)
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                recyclerView.scrollToPosition(MusicPlayer.getQueuePosition() - 1);
            }

        }

        @Override
        protected void onPreExecute() {
        }
    }
}
