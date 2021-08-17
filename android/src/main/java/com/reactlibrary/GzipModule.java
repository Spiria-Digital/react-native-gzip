package com.reactlibrary;

import android.annotation.TargetApi;
import android.os.Build;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.io.BufferedInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

public class GzipModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public GzipModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "Gzip";
    }

    @ReactMethod
    public void unTar(String source, String target, Boolean force, Promise promise) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if(!checkDir(sourceFile, targetFile, force)){
            promise.reject("-2", "error");
            return;
        }

        ArchiveInputStream inputStream = null;
        FileInputStream fileInputStream;

        try{
            fileInputStream = FileUtils.openInputStream(sourceFile);
            inputStream = new ArchiveStreamFactory()
                    .createArchiveInputStream(ArchiveStreamFactory.TAR, fileInputStream);

            ArchiveEntry archiveEntry = inputStream.getNextEntry();

            while (archiveEntry != null) {
                File destFile = new File(targetFile, archiveEntry.getName());
                if (archiveEntry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    final FileOutputStream outputStream = FileUtils.openOutputStream(destFile);
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.close();
                }
                archiveEntry = inputStream.getNextEntry();
            }

            WritableMap map = Arguments.createMap();
            map.putString("path", targetFile.getAbsolutePath());
            promise.resolve(map);
        } catch (ArchiveException | IOException  e) {
            e.printStackTrace();
            promise.reject("-2", "untar error");
        }
    }

    @ReactMethod
    public void unGzip(String source, String target, Boolean force, Promise promise) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if(!checkDir(sourceFile, targetFile, force)){
            promise.reject("-2", "error");
            return;
        }

        FileInputStream fileInputStream;

        try{
            fileInputStream = FileUtils.openInputStream(sourceFile);
            final CompressorInputStream compressorInputStream = new CompressorStreamFactory()
                    .createCompressorInputStream(CompressorStreamFactory.GZIP, fileInputStream);

            final FileOutputStream outputStream = FileUtils.openOutputStream(targetFile);
            IOUtils.copy(compressorInputStream, outputStream);
            outputStream.close();

            WritableMap map = Arguments.createMap();
            map.putString("path", targetFile.getAbsolutePath());
            promise.resolve(map);
        } catch (IOException | CompressorException e) {
            e.printStackTrace();
            promise.reject("-2", "ungzip error");
        }
    }

    @ReactMethod
    public void unGzipTar(String source, String target, Boolean force, Promise promise) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if(!checkDir(sourceFile, targetFile, force)){
            promise.reject("-2", "error");
            return;
        }

        ArchiveInputStream inputStream = null;
        FileInputStream fileInputStream;

        try{
            fileInputStream = FileUtils.openInputStream(sourceFile);
            final CompressorInputStream compressorInputStream = new CompressorStreamFactory()
                    .createCompressorInputStream(CompressorStreamFactory.GZIP, fileInputStream);
            inputStream = new ArchiveStreamFactory()
                    .createArchiveInputStream(ArchiveStreamFactory.TAR, compressorInputStream);
            ArchiveEntry archiveEntry = inputStream.getNextEntry();

            while (archiveEntry != null) {
                File destFile = new File(targetFile, archiveEntry.getName());
                if (archiveEntry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    final FileOutputStream outputStream = FileUtils.openOutputStream(destFile);
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.close();
                }
                archiveEntry = inputStream.getNextEntry();
            }

            WritableMap map = Arguments.createMap();
            map.putString("path", targetFile.getAbsolutePath());
            promise.resolve(map);
        } catch (IOException | CompressorException | ArchiveException e) {
            e.printStackTrace();
            promise.reject("-2", "ungzip error");
        }
    }

    @ReactMethod
    public void gzipTar(String source, String target, Boolean force, Promise promise) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if(!checkDir(sourceFile, targetFile, force, false)){
            promise.reject("-2", "error");
            return;
        }

        TarArchiveOutputStream tarOs = null;
        try {
            // Using input name to create output name
            FileOutputStream fos = new FileOutputStream(target);
            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            tarOs = new TarArchiveOutputStream(gos);
            addFilesToTarGZ(source, "", tarOs, true);
        } catch (IOException e) {
            promise.reject("-2", e.getMessage());
            return;
        }finally{
            try {
                tarOs.close();
            } catch (IOException e) {
                promise.reject("-2", e.getMessage());
                return;
            }
        }

        WritableMap map = Arguments.createMap();
        map.putString("path", targetFile.getAbsolutePath());
        promise.resolve(map);
    }

    private void addFilesToTarGZ(String filePath, String parent, TarArchiveOutputStream tarArchive, Boolean isFirstParent) throws IOException {
        File file = new File(filePath);
        if(file.isFile()){
            tarArchive.putArchiveEntry(new TarArchiveEntry(file, parent + file.getName()));
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            // Write file content to archive
            IOUtils.copy(bis, tarArchive);
            tarArchive.closeArchiveEntry();
            bis.close();
        }else if(file.isDirectory()){
            String entryName = "";
            //ignore first parent directory
            if(!isFirstParent){
                entryName = parent + file.getName();
                tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
                tarArchive.closeArchiveEntry();
            }

            // for files in the directories
            for(File f : file.listFiles()){
                String newParent = entryName + (!isFirstParent ? File.separator : "");
                // recursively call the method for all the subdirectories
                addFilesToTarGZ(f.getAbsolutePath(), newParent, tarArchive, false);
            }
        }
    }

    private Boolean checkDir(File sourceFile, File targetFile, Boolean force) {
        return checkDir(sourceFile, targetFile, force, true);
    }

    private Boolean checkDir(File sourceFile, File targetFile, Boolean force, Boolean createDir) {
        if (!sourceFile.exists()) {
            return false;
        }

        if (targetFile.exists()) {
            if (!force) {
                return false;
            }

            try {
                if (targetFile.isDirectory()) {
                    FileUtils.deleteDirectory(targetFile);
                } else {
                    targetFile.delete();
                }
                if(createDir){
                    targetFile.mkdirs();
                }
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }
}

