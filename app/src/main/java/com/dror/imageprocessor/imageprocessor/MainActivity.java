package com.dror.imageprocessor.imageprocessor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap picture = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this
        getPixelData(picture);
    }

    int[][][] color_pixel;
    protected void getPixelData(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        color_pixel = new int[width][][];
        for(int i=0;i<width-1;i++) {
            color_pixel[i] = new int[height][];
            for (int j=0;j<height-1;j++) {
                int p = bitmap.getPixel(i,j);

                int R = (p & 0xff0000) >> 16;
                int G = (p & 0x00ff00) >> 8;
                int B = (p & 0x0000ff) >> 0;

                int[] curr_color = new int[]{R,G,B};

                color_pixel[i][j] = curr_color;

            }
        }

        send_colors_to_strip(color_pixel);
    }

    public void send_colors_to_strip(int[][][] color_pixel) {
        //Todo: send to strip

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        String res = response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String res = error.message;
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }
}
