package com.demo.pr4;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CameraActivity extends Activity implements OnClickListener,Callback {
	
	private SurfaceView   mSurfaceView;//�����Ƶ���
	private ImageView     mImageView;  //��Ƭ
	private SurfaceHolder mSurfaceHolder;
	private ImageView     shutter;    //���հ�ť
	private Camera        mCamera = null;    //���
	private boolean       mPreviewRunning;//����������
	private static final int MENU_START = 1;
	private static final int MENU_SENSOR = 2;
	private Bitmap bitmap;           //��ƬBitmap
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//���ر���
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //���ò����ļ�
		setContentView(R.layout.cameralayout);
		mSurfaceView = (SurfaceView) findViewById(R.id.camera);
		mImageView = (ImageView) findViewById(R.id.image);
		shutter= (ImageView) findViewById(R.id.shutter);
		//���ÿ��հ�ť�¼�
		shutter.setOnClickListener(this);
		mImageView.setVisibility(View.GONE);
		mSurfaceHolder = mSurfaceView.getHolder();
		//����SurfaceHolder�ص��¼�
		mSurfaceHolder.addCallback(this);
		
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		
	}
	//���հ�ť�����¼�
	public void onClick(View v) {
	  
	  //�ж��Ƿ���Խ�������
	  if (mPreviewRunning) {
		  
		    shutter.setEnabled(false);
			//�����Զ��Խ�
			mCamera.autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					//�ۼ����������
				mCamera.takePicture(mShutterCallback,
					 null, mPictureCallback);
			}});
		}
	}
	//���ͼƬ���ջص�����
	PictureCallback mPictureCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera1) {
				//�϶���Ƭ�����Ƿ�Ϊ��
				if (data != null) {
					saveAndShow(data);
				}
		}
	};
	//���ջص�����
	ShutterCallback mShutterCallback = new ShutterCallback() {

		public void onShutter() {
			System.out.println("���ջص�����.....");
		}
	};
	//SurfaceView�ı�ʱ����
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	   try{
			//�ж��Ƿ�������������о�ֹͣ��
			if (mPreviewRunning) {
				mCamera.stopPreview();
			}
			//�������
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			mPreviewRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//SurfaceView����ʱ����
	public void surfaceCreated(SurfaceHolder holder) {
		
		setCameraParams();
	}
	//����Camera����
	public  void  setCameraParams()
	{
		if(mCamera != null)
		{
			return ; 
		}
		//��������������
		mCamera = Camera.open();
		//�����������
		Parameters params = mCamera.getParameters();
		//�����Զ��Խ�
		params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
		//����Ԥ��֡����
		 params.setPreviewFrameRate(3);
		//����Ԥ����ʽ
		params.setPreviewFormat(PixelFormat.YCbCr_422_SP);
		//����ͼƬ�����ٷֱ�
		params.set("jpeg-quality",85);
		//��ȡ���֧��ͼƬ�ֱ���
		List<Size> list = params.getSupportedPictureSizes();
		Size size = list.get(0);
		int w = size.width;
		int h = size.height;
		//����ͼƬ��С
		params.setPictureSize(w, h);
		//�����Զ������
		params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
	}
	
	//SurfaceView����ʱ����
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mCamera!=null)
		{  
			//ֹͣ���Ԥ��
			mCamera.stopPreview();
			mPreviewRunning = false;
			//�������
			mCamera.release();
			mCamera = null;
		}
	}
	//�����˵�
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_START,0,  "����");
		menu.add(0, MENU_SENSOR,0, "�����");
		return super.onCreateOptionsMenu(menu);
	}
	//�˵��¼�
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_START) {
			//�����������
			setRequestedOrientation(ActivityInfo.
					SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.
					SCREEN_ORIENTATION_LANDSCAPE);
			return true;
		}else if(item.getItemId() == MENU_SENSOR)
		{
		    Intent intent = new Intent(this,AlbumActivity.class);
       	    startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	//�������ʾͼƬ
	public void  saveAndShow(byte[] data)
	{
		try
		{
			//ͼƬid
			String imageId=System.currentTimeMillis()+"";
		    //��Ƭ����·��
		    String pathName = android.os.Environment.
		    getExternalStorageDirectory().getPath()
		    +"/com.demo.pr4";
		    //�����ļ�
		    File file=new File(pathName);
			if(!file.exists())
			{
			    file.mkdirs();
			}
			//�����ļ�
		    pathName+="/"+imageId+".jpeg";
	        file=new File(pathName);
		    if(!file.exists()){
				file.createNewFile();//�ļ����������½�
		    }
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.write(data);
		    fos.close();
		    AlbumActivity album=new AlbumActivity();
		    	        
	        //��ȡ��ƬBitmap
		    bitmap = album.loadImage(pathName);
	        //bitmap = BitmapFactory.decodeFile(pathName,options); 
	        //���õ��ؼ�����ʾ
	        mImageView.setImageBitmap(bitmap);
	        mImageView.setVisibility(View.VISIBLE);
			mSurfaceView.setVisibility(View.GONE);
		    //ֹͣ������
	        if(mPreviewRunning) {
			   mCamera.stopPreview();
			   mPreviewRunning = false;
		    } 
		    shutter.setEnabled(true);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
