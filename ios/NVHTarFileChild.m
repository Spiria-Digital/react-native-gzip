#define TAR_ERROR_DOMAIN                       @"io.nvh.targzip.tar.error"
#define TAR_ERROR_CODE_BAD_BLOCK               1
#define TAR_ERROR_CODE_SOURCE_NOT_FOUND        2
#define TAR_BLOCK_SIZE                  512

#import "NVHTarFileChild.h"

@interface NVHTarFileChild()
@end

@implementation NVHTarFileChild

- (void)packFilesAndDirectoriesAtPath:(NSArray <NSString *> *)source completion:(void (^)(NSError *))completion {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError* error = nil;
        [self innerPackFilesAndDirectoriesAtPath:source error:&error];
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(error);
        });
    });
}

- (BOOL)innerPackFilesAndDirectoriesAtPath:(NSArray <NSString *> *)path error:(NSError **)error
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    BOOL allFileExists = YES;
    for (NSString *file in path) {
        if(![fileManager fileExistsAtPath:file]) {
            allFileExists = NO;
            break;
        }
    }

    if (allFileExists) {
        [fileManager removeItemAtPath:self.filePath error:nil];
        [@"" writeToFile:self.filePath atomically:NO encoding:NSUTF8StringEncoding error:nil];
        NSFileHandle *fileHandle = [NSFileHandle fileHandleForWritingAtPath:self.filePath];
        BOOL result = [self packFilesAndDirectoriesAtPath:path withTarObject:fileHandle error:error];
        [fileHandle closeFile];
        return result;
    }
    
    NSDictionary *userInfo = [NSDictionary dictionaryWithObject:@"One or more files to be packed not found"
                                                         forKey:NSLocalizedDescriptionKey];
    
    if (error != NULL) *error = [NSError errorWithDomain:TAR_ERROR_DOMAIN code:TAR_ERROR_CODE_SOURCE_NOT_FOUND userInfo:userInfo];
    
    return NO;
}

- (BOOL)packFilesAndDirectoriesAtPath:(NSArray <NSString *> *)files withTarObject:(id)object error:(NSError **)error
{
    NSFileManager *fileManager = [NSFileManager defaultManager];

    for (NSString *file in files) {
        BOOL isParentDir = NO;
        [fileManager fileExistsAtPath:file isDirectory:&isParentDir];

        NSString *fileName = [file lastPathComponent];
        NSString *basePath = [file stringByDeletingLastPathComponent];
        NSData *tarContent = [self binaryEncodeDataForPath:fileName inDirectory:basePath isDirectory:isParentDir];
        [object writeData:tarContent];

        if(isParentDir){
            NSDirectoryEnumerator *directoryEnumerator = [fileManager enumeratorAtPath:file];
            for (NSString *innerFile in [directoryEnumerator allObjects]) {
                NSString *innerFileName = [fileName stringByAppendingPathComponent:innerFile];
                NSString *innerFullPath = [file stringByAppendingPathComponent:innerFile];
                    
                BOOL isChildDir = NO;
                [fileManager fileExistsAtPath:innerFullPath isDirectory:&isChildDir];
                NSData *innerTarContent = [self binaryEncodeDataForPath:innerFileName inDirectory:basePath isDirectory:isChildDir];
                [object writeData:innerTarContent];
            }
        }
    }

    // Append two empty blocks to indicate end
    char block[TAR_BLOCK_SIZE*2];
    memset(&block, '\0', TAR_BLOCK_SIZE*2);
    [object writeData:[NSData dataWithBytes:block length:TAR_BLOCK_SIZE*2]];
    
    return YES;
}

@end