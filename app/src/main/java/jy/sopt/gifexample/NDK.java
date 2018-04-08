package jy.sopt.gifexample;

/**
 * Created by jyoung on 2018. 4. 6..
 */

public class NDK {

    static {
        System.loadLibrary("sample-ffmpeg");
    }

    public native String getNDKTestString();
}
