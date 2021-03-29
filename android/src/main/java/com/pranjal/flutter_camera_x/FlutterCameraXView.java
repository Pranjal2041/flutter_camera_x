package com.pranjal.flutter_camera_x;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaActionSound;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.PreviewConfigProvider;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.DisplayOrientedMeteringPointFactory;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformView;

public class FlutterCameraXView implements PlatformView, MethodChannel.MethodCallHandler {

    private final MethodChannel methodChannel;
    PreviewView mPreviewView;
    private Executor executor = Executors.newSingleThreadExecutor();
    //    private int REQUEST_CODE_PERMISSIONS = 1001;
//    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    Camera camera;
    int flashMode = ImageCapture.FLASH_MODE_OFF;
    ImageCapture imageCapture;
    int cameraId = 0;
    int lensFacing = CameraSelector.LENS_FACING_BACK;
    FlutterPlugin.FlutterPluginBinding flutterPluginBinding;
    FlutterCameraXPlugin plugin;
    Context context;
    Rational aspectRatio = new Rational(16,9);
    ProcessCameraProvider cameraProvider;
    int CAMERA_REQUEST_ID = 513469796;
    boolean playSoundOnClick = false;
    boolean saveToFile = true;
    boolean torchMode = false;


    FlutterCameraXView(Context context, BinaryMessenger messenger, int id, FlutterPlugin.FlutterPluginBinding flutterPluginBinding,FlutterCameraXPlugin plugin) {

//        textView = new TextView(context);
        methodChannel = new MethodChannel(messenger, Constants.channel_id +"_"+0);
        this.cameraId = id;
        this.context = context;
        this.plugin = plugin;
        this.flutterPluginBinding = flutterPluginBinding;
        methodChannel.setMethodCallHandler(this);
        mPreviewView = new PreviewView(context);
        mPreviewView.setImportantForAccessibility(0);
        mPreviewView.setMinimumHeight(100);
        mPreviewView.setMinimumWidth(100);
        mPreviewView.setContentDescription("Description Here");

//        startCamera(context,flutterPluginBinding,plugin); //start camera if permission has been granted by user
    }

    private void startCamera(final Context context, final FlutterPlugin.FlutterPluginBinding flutterPluginBinding, final FlutterCameraXPlugin plugin) {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    if(cameraProvider!=null)
                        return;
                    cameraProvider = cameraProviderFuture.get();

                    bindPreview(cameraProvider,context,flutterPluginBinding,plugin);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(context));
    }

    @SuppressLint({"ClickableViewAccessibility", "RestrictedApi"})
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider, Context context, FlutterPlugin.FlutterPluginBinding flutterPluginBinding, FlutterCameraXPlugin plugin) {


//        PreviewConfig previewConfig = new PreviewConfig.Builder()
//                .setTargetResolution(new Size(720, 720))
//                .build();


//        DisplayMetrics disMetrics = new DisplayMetrics();
//        context.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int a =  Resources.getSystem().getDisplayMetrics().widthPixels;

        Preview.Builder previewBuilder = new Preview.Builder();
        @SuppressLint("RestrictedApi")
        Preview preview = previewBuilder
                .setTargetResolution(new Size(a, (int) (a*16.0/9.0)))

                // .setTargetAspectRatioCustom(new Rational(16,9))
                .build();
//        CameraSelector cameraSelector = new CameraSelector().Builder()


        final CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing==CameraSelector.LENS_FACING_BACK?CameraSelector.LENS_FACING_BACK:CameraSelector.LENS_FACING_FRONT)
                .build();


        final ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        imageCapture = builder
                .setTargetResolution(new Size(1080,1920))
                .setTargetRotation(plugin.activityPluginBinding.getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .build();


        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        imageCapture.setFlashMode(flashMode);

        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle(((LifecycleOwner) plugin.activityPluginBinding.getActivity()), cameraSelector, preview, imageAnalysis, imageCapture);

        camera.getCameraControl().enableTorch(torchMode);

        final CameraControl cameraControl = camera.getCameraControl();
//        val captureSize = imageCaptureUseCase.attachedSurfaceResolution ?: Size(0, 0)
//        val previewSize = previewUseCase.attachedSurfaceResolution ?: Size(0, 0)
//        Size prevSize = preview.getAttachedSurfaceResolution();

        mPreviewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MeteringPoint meteringPoint = new DisplayOrientedMeteringPointFactory(mPreviewView.getDisplay(), cameraSelector, mPreviewView.getWidth(), mPreviewView.getHeight()).createPoint(motionEvent.getX(), motionEvent.getY());
                FocusMeteringAction action = new FocusMeteringAction.Builder(meteringPoint).build();
                cameraControl.startFocusAndMetering(action);
                return false;
            }
        });
        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("sda","yoyo");
            }
        });

    }

    private Bitmap toBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    void captureImage(String path, final MethodChannel.Result result){
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        File file = new File(path);//getDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.setFlashMode(flashMode);
//        if(playSoundOnClick)
//            playClickSound();



        if(!saveToFile) {
            imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull final ImageProxy image) {
                    playClickSound();
                    plugin.activityPluginBinding.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            @SuppressLint("UnsafeExperimentalUsageError")
                            Bitmap bim = toBitmap(Objects.requireNonNull(image.getImage()));
                            int size     = bim.getRowBytes() * bim.getHeight();

                            ByteBuffer b = ByteBuffer.allocate(size);

                            bim.copyPixelsToBuffer(b);

                            byte[] bytes = new byte[size];
                            try {
                                b.get(bytes, 0, bytes.length);
                            } catch (BufferUnderflowException e) {}

                            result.success(bytes);

                        }
                    });
                    super.onCaptureSuccess(image);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                }
            });
        }
        else {
            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {

                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    if(playSoundOnClick)
                        playClickSound();
                    plugin.activityPluginBinding.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String, Object> arguments2 = new HashMap<>();
                            arguments2.put("pictureTaken", true);
                            methodChannel.invokeMethod("pictureClicked",arguments2);
                            result.success(true);
                        }
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    final ImageCaptureException err = error;
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            err.printStackTrace();
                            result.error("-1", "error while capturing image", err.getMessage());
                        }
                    });

                }
            });
        }
    }

//    public String getDirectoryName() {
//        String app_folder_path = Environment.getExternalStorageDirectory().toString() + "/DCIM";
//        File dir = new File(app_folder_path);
//        if (!dir.exists()) {
//            boolean res =  dir.mkdirs();
//        }
//        return app_folder_path;
//    }

    void playClickSound(){
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                break;
        }
    }

    private void setFlashMode(String mode){
        if(mode.equals("Torch")){
            setTorchMode(true);
            mode = "On";
        }else {
            setTorchMode(false);
        }
        flashMode = Utils.getFlashModeFromString(mode);
        if(imageCapture!=null)
            imageCapture.setFlashMode(flashMode);
    }

    private void setTorchMode(boolean mode){
        torchMode = mode;
        try {
            if (camera != null) {
                camera.getCameraControl().enableTorch(mode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPlaySoundOnClick(boolean value){
        playSoundOnClick = value;
    }

    private void setLensFacing(String lensFacing){
        this.lensFacing = Utils.getLensFacingFromString(lensFacing);
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull MethodChannel.Result result) {
        switch ((String)(call.method)) {
            case Constants.capture_image_method_name:
                captureImage((String) call.argument("data"), result);
//                result.success(true);
                break;
            case Constants.set_flash_method_name:
                setFlashMode((String) call.argument("data"));
                result.success(true);
                break;
//            case Constants.set_torch_method_name:
//                setTorchMode((boolean) call.argument("data"));
//                result.success(true);
//                break;
            case Constants.set_lens_facing_method_name:
                setLensFacing((String) call.argument("data"));
                result.success(true);
                break;
            case Constants.initializeCamera:
                setLensFacing((String)call.argument("lensFacing"));
                if(call.argument("saveToFile")!=null && !(Boolean)call.argument("saveToFile")){
                    saveToFile = true;
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    plugin.activityPluginBinding.getActivity().requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            513469796);
                    plugin.activityPluginBinding.addRequestPermissionsResultListener(new PluginRegistry.RequestPermissionsResultListener() {
                        @Override
                        public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                            if(requestCode==CAMERA_REQUEST_ID && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                                startCamera(context, flutterPluginBinding, plugin);  //start camera if permission has been granted by user
                            return false;
                        }
                    });
                }
                break;
            case Constants.set_preview_aspect_ratio_method_name:
                try {
                    aspectRatio = new Rational((int)(call.argument("num")), (int)(call.argument("denom")));
                    result.success(true);
                }catch (Exception e){
                    result.error("-2","Invalid Aspect Ratio","Invalid Aspect Ratio");
                }
                break;
            case Constants.play_sound_on_click_method_name:
                setPlaySoundOnClick((Boolean) call.argument("data"));
            default:
                result.notImplemented();
        }
    }

//    private boolean allPermissionsGranted(){
//
//        for(String permission : REQUIRED_PERMISSIONS){
//            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
//                return false;
//            }
//        }
//        return true;
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if(requestCode == REQUEST_CODE_PERMISSIONS){
//            if(allPermissionsGranted()){
//                startCamera(context,flutterPluginBinding,plugin);  //start camera if permission has been granted by user
//            } else{
////                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
//                this.finish();
//            }
//        }
//    }



    @Override
    public View getView() {
        return mPreviewView;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void dispose() {
        cameraProvider.unbindAll();
        cameraProvider.shutdown();
        camera = null;
//        mPreviewView = null;
        imageCapture = null;
    }
}
