declare module "@fengweichong/react-native-gzip" {
  export type Result = {
    path: string;
  };
  class Gzip {
    static unTar: (source: string, target: string, force: boolean) => Promise<Result>;
    static unGzip: (source: string, target: string, force: boolean) => Promise<Result>;
    static unGzipTar: (source: string, target: string, force: boolean) => Promise<Result>;
    static gzipTar: (source: string, target: string, force: boolean) => Promise<Result>;
    static gzipTarMultiplePaths: (
      source: Array<string>,
      target: string,
      force: boolean
    ) => Promise<Result>;
  }

  export default Gzip;
}
