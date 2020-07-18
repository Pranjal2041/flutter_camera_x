import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_camerax/CameraXController.dart';
import 'package:flutter_camerax/flutter_cameraX.dart';
import 'package:flutter_camerax/models/enums.dart';
import 'package:flutter_camerax/CameraXDescriptor.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState()  {
    initializeCamera();
    super.initState();
  }

  initializeCamera() async {
    var cameras = await CameraXDescriptor.getAvailableCameras();
    _cameraXController = CameraXController(cameras[1]);
//    _cameraXController.initialize();
    if (mounted) {
      setState(() {});
    }
  }
  CameraXController _cameraXController;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Container(
          child: SafeArea(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                Expanded(
                  flex: 1,
                  child: AspectRatio(
                    aspectRatio: 16/9,
                    child: _cameraXController!=null?CameraXPreview(
//                      onCameraXCreated: _onCameraXViewCreated,
                    cameraXController: _cameraXController,
                    ):Text("Loading"),
                  ),
                ),
                Align(
                  alignment: Alignment.bottomCenter,
                  child: InkWell(
                    onTap: () {
                      _cameraXController.takePicture("");
                    },
                    child: Container(
                      height: 50,
                      child: Text("Take Picture"),
                    ),
                  ),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _onCameraXViewCreated(CameraXController controller) {
    controller.setFlashMode(FlashModeX.Auto);
  }
}
