package com.example.boro_.mediscan;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.boro_.mediscan.CameraTextRecognition.AutoFitTextureView;
import com.example.boro_.mediscan.CameraTextRecognition.CloudLabelManipulator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.content.Context.CAMERA_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class    Scan extends Fragment {

    private static final int PREVIEW_STATE = 0;
    private static final int AWAIT_LOCK_STATE = 1;
    private static final int AWAITING_PRECAPTURE_STATE = 2;
    private static final int AWAITING_NON_PRECAPTURE_STATE = 3;
    private static final int PICTURE_TAKEN_STATE = 4;
    private int currentCameraState = 0;

    //private CameraManager cameraManager;
    private AutoFitTextureView cameraView;
    private ImageButton snapShotButton;
    private ProgressBar progressBar;
    private ScanningView scanView;
    private Size streamsize;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureBuilder;
    private CaptureRequest capturePreviewRequest;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private int rotation;
    private int texturewidth;
    private int textureheight;
    private String selectedcameraId;
    private boolean mFlashSupported;

    private HandlerThread backGroundThread;
    private Handler backGroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);


    //Size supported by preview window
    private final static int MAX_PREVIEW_HEIGHT = 1080;
    private final static int MAX_PREVIEW_WIDTH = 1920;

    //Size is enough for text capture
    private final static int TEXT_CAPTURE_WIDTH = 1024;
    private final static int TEXT_CAPTURE_HEIGHT = 680;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    View v;

    public Scan() {
        // Required empty public constructor


    }

    public static Scan newInstance() {
        Scan fragment = new Scan();
        return fragment;
    }

    private void startBackGroundThread(){
        backGroundThread = new HandlerThread("Background Thread");
        backGroundThread.start();
        backGroundHandler = new Handler(backGroundThread.getLooper());
    }


    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            texturewidth = width;
            textureheight = height;
            openCamera();
            transformImage(width, height);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            texturewidth = width;
            textureheight = height;

            transformImage(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private View.OnClickListener onSnapshotClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Begin the photo capture process

            focusLock();

        }
    };

    public void openCamera() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        if (!cameraView.isAvailable()) {
            cameraView.setSurfaceTextureListener(textureListener);
            return;
        }

        try {

            CameraManager cameraManager = (CameraManager) getContext().getSystemService(CAMERA_SERVICE);

            selectedcameraId = null;

            for (String cameraId : cameraManager.getCameraIdList()) {

                //Get properties from the selected camera
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                //We want to use back camera
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {

                    //Get the resolutions from the selected camera that can be used in a TextureView
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    streamsize = chooseOptimalPreviewSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), texturewidth, textureheight);

                    // Check if the flash is supported.
                    Boolean available = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    mFlashSupported = available == null ? false : available;

                    selectedcameraId = cameraId;
                }
            }


            rotation = getRotationCompensation(selectedcameraId, getActivity(), getContext());

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            int orientation = getContext().getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                cameraView.setAspectRatio(
                        streamsize.getWidth(), streamsize.getHeight());
            } else {
                cameraView.setAspectRatio(
                        streamsize.getHeight(), streamsize.getWidth());
            }

            //Acquire camera and open it
            try {
                if(!mCameraOpenCloseLock.tryAcquire(2500,TimeUnit.MILLISECONDS)){

                    throw new RuntimeException("Time out waiting to lock camera opening.");
                }
                cameraManager.openCamera(selectedcameraId, camerastateCallback, backGroundHandler);
            }
            catch (CameraAccessException e){
                e.printStackTrace();
            }


            //Keep screen on
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //This selects the closest matching image size the camera outputs to the surface
    private Size chooseOptimalPreviewSize(Size[] sizes_available, int width, int height) {

        //Get the device screen size
        Point displaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);

        int maxPreviewWidth = displaySize.x;
        int maxPreviewHeight = displaySize.y;

        Size largest_image = Collections.max(
                Arrays.asList(sizes_available),
                new Comparator<Size>() {

                    @Override
                    public int compare(Size o1, Size o2) {

                        return Long.signum((o1.getWidth() * o1.getHeight()) - (o2.getWidth() * o2.getHeight()));
                    }
                });

        int w = largest_image.getWidth();
        int h = largest_image.getHeight();


        ArrayList<Size> sizelistAbove = new ArrayList<>();
        ArrayList<Size> sizelistBelow = new ArrayList<>();

        for (Size size : sizes_available) {

            //Landscape mode
            if (width > height) {

                if(maxPreviewWidth > MAX_PREVIEW_WIDTH){
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }
                if(maxPreviewHeight > MAX_PREVIEW_HEIGHT){
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                if(size.getWidth() <= maxPreviewWidth && size.getHeight() <= maxPreviewHeight && size.getHeight() == size.getWidth() * h / w){

                    //If the size is bigger or match our preview window
                    if (size.getWidth() >= width && size.getHeight() >= height) {

                        sizelistAbove.add(size);
                    }
                    else{
                        sizelistBelow.add(size);
                    }
                }

            }
            //Portrait mode
            else {

                if(maxPreviewWidth > MAX_PREVIEW_HEIGHT){
                    maxPreviewWidth = MAX_PREVIEW_HEIGHT;
                }
                if(maxPreviewHeight > MAX_PREVIEW_WIDTH){
                    maxPreviewHeight = MAX_PREVIEW_WIDTH;
                }

                if(size.getWidth() <= maxPreviewHeight && size.getHeight() <= maxPreviewWidth && (size.getWidth() == size.getHeight() * w / h)){

                    if (size.getWidth() >= height && size.getHeight() >= width) {

                        sizelistAbove.add(size);
                    }
                    else{
                        sizelistBelow.add(size);
                    }
                }

            }
        }

        //Select the biggest closest match
        if (sizelistAbove.size() > 0) {

            //Compare resolutions in list to find the closest
            Size optimal_size = Collections.min(sizelistAbove, new Comparator<Size>() {

                @Override
                public int compare(Size o1, Size o2) {

                    return Long.signum((o1.getWidth() * o1.getHeight()) - (o2.getWidth() * o2.getHeight()));
                }
            });

            return optimal_size;
        }
        //Select the closest of the smaller sizes
        else if(sizelistBelow.size() > 0){

            //Compare resolutions in list to find the closest
            Size optimal_size = Collections.max(sizelistBelow, new Comparator<Size>() {

                @Override
                public int compare(Size o1, Size o2) {

                    return Long.signum((o1.getWidth() * o1.getHeight()) - (o2.getWidth() * o2.getHeight()));
                }
            });

            return optimal_size;
        }
        else{
            //If no optimal found, return the biggest
            return sizes_available[0];
        }

    }

    private CameraDevice.StateCallback camerastateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

            mCameraOpenCloseLock.release();
            cameraDevice = camera;

            startCameraCapture();

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

            mCameraOpenCloseLock.release();

            camera.close();
            cameraDevice = null;

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

            mCameraOpenCloseLock.release();
            camera.close();
            cameraDevice = null;

            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };

    //Transforms the preview when rotating
    private void transformImage(int width, int height) {
        if (streamsize == null || cameraView == null) {
            return;
        }
        Matrix matrix = new Matrix();

        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();


        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, streamsize.getHeight(), streamsize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();

        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(),
                    centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / streamsize.getWidth(),
                    (float) height / streamsize.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        cameraView.setTransform(matrix);
    }


    private void startCameraCapture() {

        if (cameraDevice == null || !cameraView.isAvailable() || selectedcameraId == null) {
            return;
        }

        //Texture for preview window
        SurfaceTexture texture = cameraView.getSurfaceTexture();
        if (texture == null) {
            return;
        }
        texture.setDefaultBufferSize(streamsize.getWidth(), streamsize.getHeight());
        Surface surface = new Surface(texture);

        //Imagereader for images used for textrecognition
        imageReader = ImageReader.newInstance(TEXT_CAPTURE_WIDTH, TEXT_CAPTURE_HEIGHT, ImageFormat.YUV_420_888, 1);


        //Setup a capture request
        try {
            captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Setup the capture session

        imageReader.setOnImageAvailableListener(ImageAvailable, backGroundHandler);

        captureBuilder.addTarget(surface);

        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface,imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    captureSession = session;
                    getUpdatedPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUpdatedPreview() {
        if (cameraDevice == null) {
            return;
        }

        captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        setAutoFlash(captureBuilder);
        try {

            capturePreviewRequest = captureBuilder.build();
            captureSession.setRepeatingRequest(capturePreviewRequest, captureCallback, backGroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {


        private void process(CaptureResult result){

            switch (currentCameraState){

                case PREVIEW_STATE: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case AWAIT_LOCK_STATE: {

                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);

                    if (afState == null) {
                        snapImage();

                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);

                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            currentCameraState = PICTURE_TAKEN_STATE;
                            snapImage();
                        } else {
                            preCaptureSequence();
                        }
                    }
                    break;
                }
                case AWAITING_PRECAPTURE_STATE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);

                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        currentCameraState = AWAITING_NON_PRECAPTURE_STATE;
                    }
                    break;
                }
                case AWAITING_NON_PRECAPTURE_STATE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);

                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        currentCameraState = PICTURE_TAKEN_STATE;
                        snapImage();
                    }
                    break;
                }

            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);

            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            process(result);
        }

    };

    private ImageReader.OnImageAvailableListener ImageAvailable = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(final ImageReader reader) {

           //Start the textrecognition with the captured image
           backGroundHandler.post(new TextFromImageRecognizer(reader.acquireNextImage(),rotation));
        }
    };


    private void focusLock(){

        try {

            captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);

            currentCameraState = AWAIT_LOCK_STATE;
            captureSession.capture(captureBuilder.build(),captureCallback, backGroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void focusUnLock(){
        try {

            captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);

            setAutoFlash(captureBuilder);

            captureSession.capture(captureBuilder.build(),captureCallback, backGroundHandler);

            currentCameraState = PREVIEW_STATE;

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void preCaptureSequence() {
        try {
            // Tell the camera to trigger.
            captureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Wait for the precapture sequence to be set.
            currentCameraState = AWAITING_PRECAPTURE_STATE;
            captureSession.capture(captureBuilder.build(), captureCallback,
                    backGroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void snapImage(){

        try {

            final Activity activity = getActivity();
            if (null == activity || null == cameraDevice) {
                return;
            }

            CaptureRequest.Builder snapCaptureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            snapCaptureBuilder.addTarget(imageReader.getSurface());

            snapCaptureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            setAutoFlash(snapCaptureBuilder);

            snapCaptureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            CameraCaptureSession.CaptureCallback snapCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    focusUnLock();
                }
            };

            waitForSnapshotResult();

            captureSession.capture(snapCaptureBuilder.build(),snapCallback,null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
            enableSnapShot();
        }

    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

        }
    }

    private void showToast(final String msg){

        Activity activity = getActivity();

        if(activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void stopPreview(){

        if(captureSession != null){

            try {
                captureSession.stopRepeating();
                captureSession.abortCaptures();

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartPreview(){

        if(captureSession == null || capturePreviewRequest == null) return;

        try {
            captureSession.setRepeatingRequest(capturePreviewRequest, captureCallback, backGroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeBackgroundThread() {

        if (backGroundThread != null) {

            backGroundThread.quitSafely();

            try {
                backGroundThread.join();
                backGroundThread = null;
                backGroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeCamera() {

        try {
            mCameraOpenCloseLock.acquire();

            if(captureSession != null){

                captureSession.close();
                captureSession = null;
            }

            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            mCameraOpenCloseLock.release();
        }
        closeBackgroundThread();
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);

        if(cameraManager == null) return 0;

        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e("CAMERA_ROT_COMPENSATION", "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }


    private class TextFromImageRecognizer implements Runnable{

        private FirebaseVisionDocumentTextRecognizer textRecognizer;
        private FirebaseVisionImage firebaseVisionImage;


        public TextFromImageRecognizer(Image image, int image_rotation) {

            textRecognizer = FirebaseVision.getInstance().getCloudDocumentTextRecognizer();
            firebaseVisionImage = FirebaseVisionImage.fromMediaImage(image, image_rotation);

            image.close();
        }

        @Override
        public void run() {

            if(firebaseVisionImage != null && textRecognizer != null){

                //Start the textrecognition process
                textRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                    @Override
                    public void onSuccess(final FirebaseVisionDocumentText firebaseVisionDocumentText) {

                        if (firebaseVisionDocumentText != null && !firebaseVisionDocumentText.getBlocks().isEmpty()) {

                            Log.d("FIREBASE_TEXT_REC", firebaseVisionDocumentText.getText());

                            getImageStrings(firebaseVisionDocumentText);

                        }
                        else{

                            showToast(getString(R.string.no_text_found));
                            enableSnapShot();
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        showToast(getString(R.string.textrec_not_available));
                        enableSnapShot();
                    }
                });
            }

        }
    }


    public void SecondSearch(String InternalID){

        //String InternalID = object.optString("InternalID");

        String searchlang = ((MainActivity)getActivity()).SearchLanguage;
        if (searchlang.equals("")){searchlang = "en";}

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://213.66.251.184/Bottles/BottlesService.asmx/SecondSearch?id="+InternalID+"&language_sv_en="+searchlang;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj == null)
                            {
                                return;
                            }

                            ((MainActivity)getActivity()).CreateItem(obj);


                        } catch (JSONException e) {
                            enableSnapShot();
                            e.printStackTrace();
                        }

                        // Display the first 500 characters of the response string.
                        //                       mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               //TODO Handle different volley errors
                Toast.makeText(getContext(), R.string.Communication_Error, Toast.LENGTH_SHORT).show();
                enableSnapShot();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }



    public void onClick(SelectDrugDialogFragment dialog, int which){

    }


    public void getImageStrings(FirebaseVisionDocumentText result){
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String searchlang = ((MainActivity)getActivity()).SearchLanguage;
        if (searchlang.equals("")){searchlang = "en";}

        final CloudLabelManipulator Apistr = new CloudLabelManipulator(result); // Creates a cloudlabelmanipulator object out of the result from the firebasedocumenttext object we made earlier. we use functions in this class to find relevant text
        String url="http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearch?name="+Apistr.getFirstStr()+"&strength="+Apistr.getDosage()+"&language="+searchlang+"&fbclid=IwAR00DSzecqYioxMBf3h53q42YNhFrjCbpfjE1BWDGsPg3yZkCqQqg3nxWko";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("[]")){  // The API we have sends this if there is nothing to fetch so this is the same as 404

                            showToast(getString(R.string.no_product_match) + " " + Apistr.getFirstStr());
                            enableSnapShot();
                            return;
                        }
                        try {
                            JSONArray reader = new JSONArray(response); // Makes a reader of the response we got from the API

                            if(reader.isNull(0))
                            {
                                return;
                            }

                            final SelectDrugDialogFragment dialog = new SelectDrugDialogFragment();
                            dialog.CreateList(reader, getActivity());
                            onProductFound();
                            dialog.show(getFragmentManager(), "SelectDrugs");



                            dialog.addCloseListener(new SelectDrugDialogFragment.OnClose() {
                                @Override
                                public void onClose() {
                                    SecondSearch(dialog.getInternalID());

                                }

                                //If back button is pressed when dialog is up
                                @Override
                                public void onDismiss() {
                                    enableSnapShot();
                                }
                            });

                        } catch (JSONException e) {
                            showToast(getString(R.string.Product_Error));
                            enableSnapShot();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//
                //TODO Handle different volley errors
                showToast(error.getMessage());
                enableSnapShot();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void onProductFound(){

        Activity activity = getActivity();

        if(activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scanView.endAnimation();

            }
        });
    }

    private void waitForSnapshotResult(){

        cameraView.setOnClickListener(null);

        stopPreview();

        Activity activity = getActivity();

        if(activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scanView.startScanAnimation();

            }
        });

    }

    private void enableSnapShot(){

        Activity activity = getActivity();

        if(activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scanView.endAnimation();
            }
        });

        restartPreview();

        cameraView.setOnClickListener(onSnapshotClick);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this.getContext());
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        cameraView = view.findViewById(R.id.previewWindow);
        cameraView.setOnClickListener(onSnapshotClick);
        scanView = view.findViewById(R.id.scanView);

    }

    @Override
    public void onResume() {
        super.onResume();

        startBackGroundThread();

        openCamera();

    }

    @Override
    public void onPause() {

        closeCamera();
        super.onPause();

    }


    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //TODO If permissions is not granted
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}