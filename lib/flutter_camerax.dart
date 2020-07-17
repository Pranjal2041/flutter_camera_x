import 'dart:async';

import 'package:flutter/services.dart';

class FlutterCamerax {
  static const MethodChannel _channel =
      const MethodChannel('flutter_camerax');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
