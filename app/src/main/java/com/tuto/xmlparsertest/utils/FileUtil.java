package com.tuto.xmlparsertest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static boolean existFile(String path){
        File file = new File(path);
        return file.exists();
    }

    public static boolean makeDir(String path){
        if( !existFile(path)){
            File file = new File(path);
            return file.mkdirs();
        }else{
            return false;
        }

    }

    public static byte[] readFileToByte(String path) throws Exception {

        FileInputStream input = null;
        byte[] readBytes = null;

        try{
            input = new FileInputStream(new File(path));
            int length = input.available();

            if (length > 0) {
                readBytes = new byte[length];
                input.read(readBytes);
            }
            else {

                return null;
            }
        }catch(IOException e){
            throw e;
        }finally{
            if( input != null){
                try{
                    input.close();
                }catch(Exception e){}
            }
        }

        return readBytes;
    }

    public static void writeFile(String filePath, byte[] src) throws Exception{
        int endIdx = filePath.lastIndexOf(File.separator);
        if( endIdx>0){
            String dirPath = filePath.substring(0, endIdx);
            makeDir(dirPath);
        }

        File f = new File(filePath);
        if (false == f.exists())
            f.createNewFile();

        FileOutputStream out = null;
        try{
            out = new FileOutputStream(new File(filePath));
            out.write(src);
            out.flush();

        } catch( IOException e ){
            throw e;
        } finally{
            try{
                if( out != null)
                    out.close();
            } catch( IOException ex ){
            }
        }
    }
}
