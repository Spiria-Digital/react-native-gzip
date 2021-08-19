#import <Foundation/Foundation.h>
#import "NVHTarFile.h"

@interface NVHTarFileChild : NVHTarFile
- (void)packFilesAndDirectoriesAtPath:(NSArray <NSString *> *)sourcePath completion:(void (^)(NSError *))completion;
- (NSData *)binaryEncodeDataForPath:(NSString *) path inDirectory:(NSString *)basepath  isDirectory:(BOOL) isDirectory;
@end