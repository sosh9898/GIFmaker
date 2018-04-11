# GIFmaker
MediaPlayer 와 MediaMetadataRetriever 를 활용한 android gif 변환 Semi Project



## # 주요기능

* 내부 저장소에서 동영상을 선택하여 재생
* seekbar를 통한 기본적인 영상 제어
* MediaMetadataRetriever 를 활용하여 영상 프레임 캡쳐
* 캡쳐한 프레임을 AnimatedGifEncoder 를 통해 gif로 변환
* 변환된 gif 내부 저장소에 저장



## #MediaPlayer

Android MediaPlayer API는 오디오와 비디오 재생을 담당하는 기본 API로 파일과 스트림을 모두 지원합니다. 

* 미디어 제공 방식
  * Local Resource
  * ContentResolver 등을 활용한 내부 URL
  * 외부 URL

해당 프로젝트에서는 Local Resource , 내부 저장소의 동영상을 재생하는데 사용됩니다.

````Java
	//OnInfoListener 리스너를 통해 영상의 버퍼링 상태를 보여줍니다
	//다음 리스너는 주로 정보 혹은 경고를 알리는데 사용됩니다.
	videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
             @Override
             public boolean onInfo(MediaPlayer mp, int what, int extra) {
                   switch(what){
                      case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                      // 버퍼링 시작을 알립니다.
                      // 이하 생략
                      break;
                      case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                      // 버퍼링이 끝남을 알립니다.
                      // 이하 생략
                      break;
                      }
                      return false;
                    }
                }

        );

	//OnPreparedListener 리스너는 영상을 재생할 준비가되면 호출됩니다.
	videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               //영상재생 준비가 끝나면 onPrepared 함수가 호출됩니다.
               //이하 생략
            }
        });


````



## # MediaMetadataRetriever

MediaMetadataRetriever 클래스는 입력 미디어 파일에서 프레임 및 메타 데이터를 검색하기위한 통합 인터페이스를 제공합니다. 즉, Media 의 metadata 를 가져올 수 있는 class 입니다.

```Java
	//mediaMetadataRetriever 인스턴스를 만들고 메타데이터 및 프레임을 추출한 파일의 경로를 지정합니다.
	private void initMediaMetadataRetriever(){
		mediaMetadataRetriever = new MediaMetadataRetriever();
		mediaMetadataRetriever.setDataSource('파일 경로');
	}

	// mediaMetadataRetriever 클래스의 getFramAtTime 메소드를 호출하여 해당 시간의 프레임을 bitmap으	로 반환받습니다.
	public void captureFrame(){
        int currentPosition = videoView.getCurrentPosition();

        for(int i = currentPosition; i<videoView.getDuration(); i+=300) {
            Bitmap bitmap = mediaMetadataRetriever
                    .getFrameAtTime(i * 1000);
            list.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true));
            bitmap.recycle();
        }
    }
```

해당 프로젝트에서는 원하는 영상의 프레임을 가져오기위해 사용되었습니다.

## # AnimatedGifEncoder

AnimatedGifEncoder 는 GIF를 안드로이에서 인코딩 할 수 있는 라이브러리입니다.

````java
//bitmap 을 gif 로 인코딩하는 메소드입니다. 
private byte[] generateGIF() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(100);
     	//지연시간 설정 (100ms)
        encoder.start(bos);

        for (Bitmap bitmap : list) {
            encoder.addFrame(bitmap);
            //mediaMetadataRetriever 클래스의 getFramAtTime 메소드를 통해 반환				받은 bitmap 들을 인코딩합니다.
        }
        list.clear();
        encoder.finish();

        return bos.toByteArray();
    }
````



## # 추후 개선사항

* ExoPlayer 의 사용
* NDK / FFmpeg Cross Compile





