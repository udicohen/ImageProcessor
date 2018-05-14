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
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(picture, 30, 15, false);
        getPixelData(resizedBitmap);
    }

    protected void getPixelData(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        String colors_in_string = "[255,255,255][255,255,255][255,255,255][255,255,255][255,255,255][255,255,255][255,255,255][255,255,255]";
        int length =  height*width + 6 + height + (height+1)/2;
        for(int i=0;i<height;i++) {
            String curr_line = "";
            for (int j=0;j<width;j++) {
                int p = bitmap.getPixel(j,i);

                int R = (p & 0xff0000) >> 16;
                int G = (p & 0x00ff00) >> 8;
                int B = (p & 0x0000ff) >> 0;

                if (i%2==0){
                    curr_line = "["+R+","+G+","+B+"]" + curr_line;
                }else{
                    curr_line += "["+R+","+G+","+B+"]";
                }

            }
            colors_in_string += curr_line;

            colors_in_string += "[255,255,255]";
            if (i%2!=0){
                colors_in_string += "[255,255,255]";
            }

        }

        send_colors_to_strip(length, colors_in_string);
    }


    public void send_colors_to_strip(int length, String color_in_string) {
        final int length_ = length;

        final String color_in_string_ = color_in_string;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.0.100:3101/test";
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
                params.put("length",  Integer.toString(length_));
                params.put("data", color_in_string_);

                return params;
            }
        };
        queue.add(postRequest);

    }

}
