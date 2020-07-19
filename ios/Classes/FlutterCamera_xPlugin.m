#import "FlutterCamera_xPlugin.h"
#if __has_include(<flutter_camera_x/flutter_camera_x-Swift.h>)
#import <flutter_camera_x/flutter_camera_x-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_camera_x-Swift.h"
#endif

@implementation FlutterCamera_xPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterCamera_xPlugin registerWithRegistrar:registrar];
}
@end
