# react-native-gzip

Compress / Decompress .gz and .tar format files

- iOS implementation is based on [NVHTarGzipV2](https://github.com/fallending/NVHTarGzipV2)
- Android implementation is based on [CompressorStreamFactory](https://commons.apache.org/proper/commons-compress/apidocs/org/apache/commons/compress/compressors/CompressorStreamFactory.html) and [ArchiveStreamFactory](https://commons.apache.org/proper/commons-compress/javadocs/api-1.18/org/apache/commons/compress/archivers/ArchiveStreamFactory.html)

## Install

```
npm install @fengweichong/react-native-gzip --save

yarn add @fengweichong/react-native-gzip
```

## Decompression Usage

```javascript
import Gzip from "@fengweichong/react-native-gzip";

const sourcePath = `${PATH}/xxx.gz`;
const targetPath = `${PATH}/xxx`;
const force = true;

// Decompress tar
Gzip.unTar(sourcePath, targetPath, force).then((res) => {
  console.log(res);
});

// Decompress gzip
Gzip.unGzip(sourcePath, targetPath, force).then((res) => {
  console.log(res);
});

// Decompress gzip and tar
Gzip.unGzipTar(sourcePath, targetPath, force).then((res) => {
  console.log(res);
});
```

### Parameter

| Name       | Description                          | Mandatory |
| ---------- | ------------------------------------ | --------- |
| sourcePath | Target file address                  | true      |
| targetPath | Unzip the destination address        | true      |
| force      | Whether to overwrite the target path | true      |

## Compression Usage

```javascript
import Gzip from "@fengweichong/react-native-gzip";

const sourcePath = `${PATH}/xxx`;
const multipleSourcePaths = [`${PATH}/xxx1`, `${PATH}/xxx2`, `${PATH}/file1.mp4`];
const targetPath = `${PATH}/xxx.tar.gz`;
const force = true;

// Compress content of sourcePath to a single .tar.gz
Gzip.gzipTar(sourcePath, targetPath, force).then((res) => {
  console.log(res);
});

// Compress multiple directories/files to a single .tar.gz
Gzip.gzipTarMultiplePaths(multipleSourcePaths, targetPath, force).then((res) => {
  console.log(res);
});
```

### Parameter

| Name                | Description                                  | Mandatory                              |
| ------------------- | -------------------------------------------- | -------------------------------------- |
| sourcePath          | Content of this directory will be compress   | true (for gzipTar method)              |
| multipleSourcePaths | The directories and files will be compressed | true (for gzipTarMultiplePaths method) |
| targetPath          | The target file location                     | true                                   |
| force               | Whether to overwrite the target path         | true                                   |
