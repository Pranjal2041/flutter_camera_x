package com.pranjal.flutter_camerax;

import android.content.Context;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class CameraXViewFactory extends PlatformViewFactory {

    private final BinaryMessenger messenger;
    FlutterPlugin.FlutterPluginBinding flutterPluginBinding;
    FlutterCameraxPlugin plugin;

    public CameraXViewFactory(BinaryMessenger messenger, FlutterPlugin.FlutterPluginBinding flutterPluginBinding,FlutterCameraxPlugin plugin) {
        super(StandardMessageCodec.INSTANCE);
        this.plugin = plugin;
        this.flutterPluginBinding = flutterPluginBinding;
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        return new FlutterCameraXView(context,messenger, id,flutterPluginBinding,plugin);
    }

}
