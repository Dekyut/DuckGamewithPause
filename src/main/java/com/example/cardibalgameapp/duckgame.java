package com.example.cardibalgameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.VideoView;
import android.net.Uri;

import java.util.Random;

public class duckgame extends AppCompatActivity implements Runnable {

    private Handler handler;
    private ImageView[] ducks;
    private int[] duckSpeedXs;
    private int[] duckSpeedYs;
    private int screenWidth;
    private int screenHeight;

    private ImageView quit;

    private ImageView resume;
    private Random random;
    private static final int FRAME_RATE = 30; // Adjust the frame rate for smoother animation
    private ImageView equivalentImageView; // Declare equivalentImageView as a class variable

    private ImageView instructionImageView;

    private MediaPlayer ducksound;
    private MediaPlayer duckquack;

    private MediaPlayer menusound;
    private MediaPlayer chain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duckgame);

        // Initialize duck ImageViews
        ducks = new ImageView[]{
                findViewById(R.id.duck1),
                findViewById(R.id.duck2),
                findViewById(R.id.duck3),
                findViewById(R.id.duck4),
                findViewById(R.id.duck5),
                findViewById(R.id.duck6)
        };

        resume = findViewById(R.id.resume);
        quit = findViewById(R.id.quit);

        // Setup VideoView
        VideoView videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.waterbg);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Initialize random number generator
        random = new Random();

        // Initialize duck speeds
        duckSpeedXs = new int[]{10, -10, 12, -8, 15, -11};
        duckSpeedYs = new int[]{10, 8, -12, -9, 11, -14};

        // Initialize handler and start updating duck position
        handler = new Handler();
        handler.post(this);

        // Find the ImageView with ID equivalent
        equivalentImageView = findViewById(R.id.equivalent);
        instructionImageView = findViewById(R.id.duckinstructions);

        // Initialize MediaPlayer
        ducksound = MediaPlayer.create(this, R.raw.duckmusic);
        duckquack = MediaPlayer.create(this, R.raw.quack);
        chain = MediaPlayer.create(this, R.raw.chainsound);
        menusound = MediaPlayer.create(this, R.raw.cardiow);

        ducksound.setLooping(true);
        ducksound.start();
    }

    @Override
    public void run() {
        if (findViewById(R.id.pausemenulayout).getVisibility() == View.VISIBLE) {
            // If pause menu is visible, do nothing and return
            disableDuckClickListeners(); // Disable click listeners for ducks
            handler.postDelayed(this, 1000 / FRAME_RATE);
            return;
        } else {
            enableDuckClickListeners(); // Enable click listeners for ducks
        }

        for (int i = 0; i < ducks.length; i++) {
            updateDuckPosition(i);
        }

        handler.postDelayed(this, 1000 / FRAME_RATE);
    }

    private void disableDuckClickListeners() {
        for (ImageView duck : ducks) {
            duck.setOnClickListener(null); // Disable click listener for duck
        }
    }

    private void enableDuckClickListeners() {
        for (ImageView duck : ducks) {
            duck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    duckclicked(v); // Re-enable click listener for duck
                }
            });
        }
    }




    private void updateDuckPosition(int duckIndex) {
        ImageView duck = ducks[duckIndex];
        int duckSpeedX = duckSpeedXs[duckIndex];
        int duckSpeedY = duckSpeedYs[duckIndex];

        // Calculate new position based on current position and speed
        float newX = duck.getX() + duckSpeedX;
        float newY = duck.getY() + duckSpeedY;

        // Reverse direction if the duck reaches the edge of the screen
        if (newX <= 0) {
            duckSpeedX = Math.abs(duckSpeedX); // Change direction to move right
            duck.setImageResource(R.drawable.rubberduck1); // Change image to rubberduck1
        } else if (newX >= screenWidth - duck.getWidth()) {
            duckSpeedX = -Math.abs(duckSpeedX); // Change direction to move left
            duck.setImageResource(R.drawable.rubberduck2); // Change image to rubberduck2
        }
        if (newY <= 0 || newY >= screenHeight - duck.getHeight()) {
            duckSpeedY = -duckSpeedY; // Reverse vertical direction if hitting top or bottom
        }

        // Update duck speeds
        duckSpeedXs[duckIndex] = duckSpeedX;
        duckSpeedYs[duckIndex] = duckSpeedY;

        // Set new position for the duck with interpolation for smoother animation
        duck.animate().x(newX).y(newY).setDuration(1000 / FRAME_RATE).start();
    }

    public void MenuClicked(View view) {
        ducksound.pause();
        ImageView pauseMenuLayout = findViewById(R.id.pausemenulayout);
        if (pauseMenuLayout != null) {
            if (pauseMenuLayout.getVisibility() == View.VISIBLE) {
                pauseMenuLayout.setVisibility(View.INVISIBLE);
            } else {
                pauseMenuLayout.setVisibility(View.VISIBLE);
                if (resume != null) {
                    resume.setVisibility(View.VISIBLE);
                }
                if (quit != null) {
                    quit.setVisibility(View.VISIBLE);
                }
            }
        }
        if (menusound != null) {
            menusound.start();
        }
    }

    public void resumeClicked(View view) {
        ImageView pauseMenuLayout = findViewById(R.id.pausemenulayout);
        if (pauseMenuLayout != null) {
            if (pauseMenuLayout.getVisibility() == View.VISIBLE) {
                pauseMenuLayout.setVisibility(View.INVISIBLE);
                resume.setVisibility(View.INVISIBLE);
                quit.setVisibility(View.INVISIBLE);
            } else {
                pauseMenuLayout.setVisibility(View.VISIBLE);
                if (resume != null) {
                    resume.setVisibility(View.INVISIBLE);
                    resume.setClickable(false); // Disable click listener
                }
                if (quit != null) {
                    quit.setVisibility(View.INVISIBLE);
                    quit.setClickable(false); // Disable click listener
                }
            }
        }
        // Move ducksound.start() outside of the conditional statement
        if (ducksound != null) {
            ducksound.start();
        }

    }



    public void backtoMenu(View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Animation ended, proceed with going back to the main menu
                Intent i = new Intent(duckgame.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(i, 0);
                overridePendingTransition(0, 0);
                finish();
            }
        }, 0); // You can adjust the delay here if needed
    }



    public void duckclicked(final View view) {
        if (equivalentImageView.getVisibility() != View.VISIBLE) { // Check if equivalent image is not visible
            // Apply alpha animation to make the duck darker
            view.animate()
                    .alpha(0.6f) // Set alpha to 0.5 (darker shade)
                    .setDuration(400) // Set animation duration to 500 milliseconds
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // Animation ended, proceed with duck functionality
                            duckClickedFunction(view);
                        }
                    })
                    .start();
        }
    }

    private void duckClickedFunction(final View view) {
        // Get the ID of the clicked duck
        int clickedDuckId = view.getId();

        // Generate a random number between 1 and 9 (inclusive)
        int randomImageIndex = random.nextInt(9) + 1;

        // Form the resource ID for the randomly chosen duck image
        int resourceId = getResources().getIdentifier("duck" + randomImageIndex, "drawable", getPackageName());

        // Set the image resource of the equivalentImageView
        equivalentImageView.setImageResource(resourceId);

        // Make the equivalent image visible
        equivalentImageView.setVisibility(View.VISIBLE);

        // Apply zoom-in animation
        equivalentImageView.setScaleX(0); // Set initial scaleX to 0
        equivalentImageView.setScaleY(0); // Set initial scaleY to 0
        equivalentImageView.animate()
                .scaleX(1) // Set final scaleX to 1
                .scaleY(1) // Set final scaleY to 1
                .setDuration(500) // Set animation duration to 500 milliseconds
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        // Animation ended, proceed with any additional actions
                        duckquack.start(); // Start the duck quack sound
                        // No need to reverse the alpha animation here
                    }
                })
                .start();
        duckquack.start();
    }
    public void equivalClicked(View view) {
        // Check if the equivalent image is visible, if so, hide it
        if (equivalentImageView.getVisibility() == View.VISIBLE) {
            equivalentImageView.setVisibility(View.INVISIBLE);

            // Loop through all ducks and reset their alpha to 1.0 (original color)
            for (ImageView duck : ducks) {
                duck.animate()
                        .alpha(1.0f) // Set alpha to 1.0 (original color)
                        .setDuration(400) // Set animation duration to 400 milliseconds
                        .start();
            }
        }
    }

    public void InstructionClicked(View view) {
        // Create an ObjectAnimator to animate the translationY property
        ObjectAnimator animator = ObjectAnimator.ofFloat(instructionImageView, "translationY", 0, -instructionImageView.getHeight());
        animator.setDuration(1400); // Set duration for the animation in milliseconds
        animator.start(); // Start the animation
        chain.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chain != null && chain.isPlaying()) {
                    chain.pause();
                }
            }
        }, 1500);

        // Disable click listener to prevent further clicks
        instructionImageView.setOnClickListener(null);
        }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause or release MediaPlayer when activity is paused
        if (ducksound != null) {
            if (findViewById(R.id.pausemenulayout).getVisibility() == View.VISIBLE) {
                ducksound.pause(); // Pause the MediaPlayer if pause menu is visible
            } else {
                ducksound.start(); // Resume the MediaPlayer if pause menu is not visible
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer when activity is destroyed
        if (ducksound != null) {
            ducksound.release(); // Release the MediaPlayer resources
            ducksound = null; // Set MediaPlayer reference to null
        }

    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(i, 0);
        overridePendingTransition(0,0);
        menusound.start();
        finish();

    }
    }



