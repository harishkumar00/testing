
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNRentlyMeariSpec.h"

@interface RentlyMeari : NSObject <NativeRentlyMeariSpec>
#else
#import <React/RCTBridgeModule.h>

@interface RentlyMeari : NSObject <RCTBridgeModule>
#endif

@end
