# react-native-gzip

Unzip/Zip gzip and tar format files

- IOS implementation is based on [NVHTarGzipV2](https://github.com/fallending/NVHTarGzipV2)
- Android implementation is based on [CompressorStreamFactory](https://commons.apache.org/proper/commons-compress/apidocs/org/apache/commons/compress/compressors/CompressorStreamFactory.html)和[ArchiveStreamFactory](https://commons.apache.org/proper/commons-compress/javadocs/api-1.18/org/apache/commons/compress/archivers/ArchiveStreamFactory.html)

## Install

```
npm install @fengweichong/react-native-gzip --save

ios -> pod install
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

| Name       | Description                                     | Mandatory |
| ---------- | ----------------------------------------------- | --------- |
| sourcePath | Target file address (without file://)           | true      |
| targetPath | Unzip the destination address (without file://) | true      |
| force      | Whether to overwrite the target address         | true      |

## Compression Usage

```javascript
import Gzip from "@fengweichong/react-native-gzip";

const sourcePath = `${PATH}/xxx`;
const targetPath = `${PATH}/xxx.tar.gz`;
const force = true;

// Compress content of sourcePath toe .tar.gz
Gzip.gzipTar(sourcePath, targetPath, force).then((res) => {
  console.log(res);
});
```

### Parameter

| Name       | Description                                                  | Mandatory |
| ---------- | ------------------------------------------------------------ | --------- |
| sourcePath | Content of this directory will be compress (without file://) | true      |
| targetPath | The target file location (without file://)                   | true      |
| force      | Whether to overwrite the target address                      | true      |
