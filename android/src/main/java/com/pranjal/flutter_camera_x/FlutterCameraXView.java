package com.pranjal.flutter_camera_x;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class FlutterCameraXView implements PlatformView, MethodChannel.MethodCallHandler {

    private final MethodChannel methodChannel;
    PreviewView mPreviewView;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    Camera camera;
    int flashMode = ImageCapture.FLASH_MODE_AUTO;
    ImageCapture imageCapture;
    int cameraId = 0;
    int lensFacing = CameraSelector.LENS_FACING_BACK;
    FlutterPlugin.FlutterPluginBinding flutterPluginBinding;
    FlutterCameraXPlugin plugin;
    Context context;
    Rational aspectRatio = new Rational(16,9);


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

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

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
        Preview.Builder previewBuilder = new Preview.Builder();
        @SuppressLint("RestrictedApi")
        Preview preview = previewBuilder.setTargetAspectRatioCustom(aspectRatio).build();
//        CameraSelector cameraSelector = new CameraSelector().Builder()

        final CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraId==0?CameraSelector.LENS_FACING_BACK:CameraSelector.LENS_FACING_FRONT)
                .build();
        CameraManager cameraManager = (CameraManager) plugin.activityPluginBinding.getActivity().getSystemService(Context.CAMERA_SERVICE);


        final ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        imageCapture = builder
                .setTargetRotation(plugin.activityPluginBinding.getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .build();


        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        imageCapture.setFlashMode(flashMode);

        Camera camera = cameraProvider.bindToLifecycle(((LifecycleOwner) plugin.activityPluginBinding.getActivity()), cameraSelector, preview, imageAnalysis, imageCapture);
        final CameraControl cameraControl = camera.getCameraControl();
//        val captureSize = imageCaptureUseCase.attachedSurfaceResolution ?: Size(0, 0)
//        val previewSize = previewUseCase.attachedSurfaceResolution ?: Size(0, 0)
          Size prevSize = preview.getAttachedSurfaceResolution();
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

    void captureImage(String path, final MethodChannel.Result result){
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        File file = new File(path);//getDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.setFlashMode(flashMode);

        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                result.success(true);
            }
            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
//                result.error("-1","error while capturing image",error.getMessage());
            }
        });
    }

    public String getDirectoryName() {
        String app_folder_path = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File dir = new File(app_folder_path);
        if (!dir.exists()) {
            boolean res =  dir.mkdirs();
        }
        return app_folder_path;
    }

    private void setFlashMode(String mode){
        flashMode = Utils.getFlashModeFromString(mode);
        if(imageCapture!=null)
            imageCapture.setFlashMode(flashMode);
    }

    private void setLensFacing(String lensFacing){
        this.lensFacing = Utils.getLensFacingFromString(lensFacing);
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case Constants.capture_image_method_name:
                captureImage((String) call.argument("data"), result);
                result.success(true);
                break;
            case Constants.set_flash_method_name:
                setFlashMode((String) call.argument("data"));
                result.success(true);
                break;
            case Constants.set_lens_facing_method_name:
                setLensFacing((String) call.argument("data"));
                result.success(true);
                break;
            case Constants.initializeCamera:
                setLensFacing((String)call.argument("lensFacing"));
                startCamera(context,flutterPluginBinding,plugin);
                result.success(true);
                break;
            case Constants.set_preview_aspect_ratio_method_name:
                try {
                    aspectRatio = new Rational((int)(call.argument("num")), (int)(call.argument("denom")));
                    result.success(true);
                }catch (Exception e){
                    result.error("-2","Invalid Aspect Ratio","Invalid Aspect Ratio");
                }
                break;
            default:
                result.notImplemented();
        }
    }



    @Override
    public View getView() {
        return mPreviewView;
    }

    @Override
    public void dispose() {

    }
}
