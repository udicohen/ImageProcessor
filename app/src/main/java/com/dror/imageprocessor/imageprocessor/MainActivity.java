package com.dror.imageprocessor.imageprocessor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

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
        int height = 25; //bitmap.getHeight();
        int width = 12; //bitmap.getWidth();
        color_pixel = new int[width][][];
        String colors_in_string = "";
        for(int i=0;i<width;i++) {
            color_pixel[i] = new int[height][];
            for (int j=0;j<height;j++) {
                int p = bitmap.getPixel(i,j);

                int R = (p & 0xff0000) >> 16;
                int G = (p & 0x00ff00) >> 8;
                int B = (p & 0x0000ff) >> 0;

                int[] curr_color = new int[]{R,G,B};

                colors_in_string += "["+R+","+G+","+B+"]";

                color_pixel[i][j] = curr_color;

            }
        }

        send_colors_to_strip(color_pixel, colors_in_string);
    }


    public void send_colors_to_strip(int[][][] color_pixel, String color_in_string) {
        final int[][][] color_pixel_ = color_pixel;
        final String color_in_string_ = color_in_string;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.29:3101/test";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //String res = response.substring(0,500);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        VolleyError error_ = error;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("width",  Integer.toString(color_pixel_.length));
                params.put("height",  Integer.toString(color_pixel_[0].length));
                params.put("data", color_in_string_);

                return params;
            }
        };
        queue.add(postRequest);

    }

}
