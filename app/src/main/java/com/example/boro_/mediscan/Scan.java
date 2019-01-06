package com.example.boro_.mediscan;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.boro_.mediscan.CameraTextRecognition.AutoFitTextureView;
import com.example.boro_.mediscan.CameraTextRecognition.CameraHandler;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;



/**
 * A simple {@link Fragment} subclass.
 */
public class Scan extends Fragment {

    CameraHandler cameraHandler;
    View v;

    public Scan() {
        // Required empty public constructor


    }


    CameraHandler.OnTextRecognizedSuccessListener onTextRecognizedSuccessListener = new CameraHandler.OnTextRecognizedSuccessListener() {
        @Override
        public void onTextRecognized(FirebaseVisionDocumentText text) {

        }
    };

    CameraHandler.OnTextRecognizedFailedListener onTextRecognizedFailedListener = new CameraHandler.OnTextRecognizedFailedListener() {
        @Override
        public void onTextRecognizedFailed(String msg) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_scan, container, false);

        ActivityCompat.requestPermissions(getActivity(),new String[]
                {Manifest.permission.CAMERA}, 1);

        this.v = v;
        return v;//inflater.inflate(R.layout.fragment_conversion, container, false);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(cameraHandler == null){

            cameraHandler = new CameraHandler(this.getContext(),(AutoFitTextureView) v.findViewById(R.id.previewWindow),(ImageButton) v.findViewById(R.id.cameraSnap));
            cameraHandler.setOnTextRecognizedSuccessListener(onTextRecognizedSuccessListener);
            cameraHandler.setOnTextRecognizedFailedListener(onTextRecognizedFailedListener);
            cameraHandler.startCamera();
        }
        else {
            //cameraHandler.closeCamera();
            cameraHandler.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(cameraHandler != null){
           cameraHandler.closeCamera();

        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(cameraHandler != null){
            cameraHandler.closeCamera();
            cameraHandler = null;
        }
    }
}