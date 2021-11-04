import { NativeModules } from "react-native";

const { Gzip } = NativeModules;

const normalizeFilePath = (path) => (path.startsWith("file://") ? path.slice(7) : path);

export default class Gzip {
  static unTar(source, target, force) {
    return Gzip.unTar(normalizeFilePath(source), normalizeFilePath(target), force);
  }

  static unGzip(source, target, force) {
    return Gzip.unGzip(normalizeFilePath(source), normalizeFilePath(target), force);
  }

  static unGzipTar(source, target, force) {
    return Gzip.unGzipTar(normalizeFilePath(source), normalizeFilePath(target), force);
  }

  static gzipTar(source, target, force) {
    return Gzip.gzipTar(normalizeFilePath(source), normalizeFilePath(target), force);
  }

  static gzipTarMultiplePaths(sourcePaths, target, force) {
    return Gzip.gzipTarMultiplePaths(
      sourcePaths.map(normalizeFilePath),
      normalizeFilePath(target),
      force
    );
  }
}
