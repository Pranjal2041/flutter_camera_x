#import "FlutterCameraxPlugin.h"
#if __has_include(<flutter_camerax/flutter_camerax-Swift.h>)
#import <flutter_camerax/flutter_camerax-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_camerax-Swift.h"
#endif

@implementation FlutterCameraxPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterCameraxPlugin registerWithRegistrar:registrar];
}
@end
