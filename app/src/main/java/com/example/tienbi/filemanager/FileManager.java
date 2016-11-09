package com.example.tienbi.filemanager;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.tienbi.filemanager.TFile.MimeType;
import com.example.tienbi.filemanager.util.FileUtils;

public class FileManager {
	
	public static final int DEFAULT_MAX_CHOOSED_CNT = 10;
	public static final long DEFAULT_MAX_FILESIZE = 10*1024*1024;
	
	private int maxFileCnt = DEFAULT_MAX_CHOOSED_CNT ;
	private long maxFileSize = DEFAULT_MAX_FILESIZE;
	
	private static FileManager instance;
	private final Map<String ,MimeType> map;
	private final Map<MimeType,Integer> resMap;
	private final List<TFile> choosedFiles;
	public static FileManager getInstance(){
		if(null == instance){
			instance = new FileManager();
		}
		return instance;
	}

	public void reSetDefaultConfiguration(){
		maxFileCnt = DEFAULT_MAX_CHOOSED_CNT ;
		maxFileSize = DEFAULT_MAX_FILESIZE;
	}

	public void initConfiguration(int maxChoosedCnt , long maxFileSize){
		if(maxChoosedCnt>0)
			this.maxFileCnt = maxChoosedCnt;
		if(maxFileSize>0)
			this.maxFileSize = maxFileSize;
	}
	
	public int getMaxFileCnt() {
		return maxFileCnt;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	private FileManager(){
		map = new HashMap<String,MimeType>();
		map.put(".amr", MimeType.MUSIC);
		map.put(".mp3", MimeType.MUSIC);
		map.put(".ogg", MimeType.MUSIC);
		map.put(".wav", MimeType.MUSIC);
		map.put(".3gp", MimeType.VIDEO);
		map.put(".mp4", MimeType.VIDEO);
		map.put(".rmvb", MimeType.VIDEO);
		map.put(".mpeg", MimeType.VIDEO);
		map.put(".mpg", MimeType.VIDEO);
		map.put(".asf", MimeType.VIDEO);
		map.put(".avi", MimeType.VIDEO);
		map.put(".wmv", MimeType.VIDEO);
		map.put(".apk", MimeType.APK);
		map.put(".bmp", MimeType.IMAGE);
		map.put(".gif", MimeType.IMAGE);
		map.put(".jpeg", MimeType.IMAGE);
		map.put(".jpg", MimeType.IMAGE);
		map.put(".png", MimeType.IMAGE);
		map.put(".doc", MimeType.DOC);
		map.put(".docx", MimeType.DOC);
		map.put(".rtf", MimeType.DOC);
		map.put(".wps", MimeType.DOC);
		map.put(".xls", MimeType.XLS);
		map.put(".xlsx", MimeType.XLS);
		map.put(".gtar", MimeType.RAR);
		map.put(".gz", MimeType.RAR);
		map.put(".zip", MimeType.RAR);
		map.put(".tar", MimeType.RAR);
		map.put(".rar", MimeType.RAR);
		map.put(".jar", MimeType.RAR);
		map.put(".htm", MimeType.HTML);
		map.put(".html", MimeType.HTML);
		map.put(".xhtml", MimeType.HTML);
		map.put(".java", MimeType.TXT);
		map.put(".txt", MimeType.TXT);
		map.put(".xml", MimeType.TXT);
		map.put(".log", MimeType.TXT);
		map.put(".pdf", MimeType.PDF);
		map.put(".ppt", MimeType.PPT);
		map.put(".pptx", MimeType.PPT);
		
		resMap = new HashMap<MimeType,Integer>();
		resMap.put(MimeType.APK, R.drawable.bxfile_file_apk);
		resMap.put(MimeType.DOC, R.drawable.bxfile_file_doc);
		resMap.put(MimeType.HTML, R.drawable.bxfile_file_html);
		resMap.put(MimeType.IMAGE, R.drawable.bxfile_file_unknow);
		resMap.put(MimeType.MUSIC, R.drawable.bxfile_file_mp3);
		resMap.put(MimeType.VIDEO, R.drawable.bxfile_file_video);
		resMap.put(MimeType.PDF, R.drawable.bxfile_file_pdf);
		resMap.put(MimeType.PPT, R.drawable.bxfile_file_ppt);
		resMap.put(MimeType.RAR, R.drawable.bxfile_file_zip);
		resMap.put(MimeType.TXT, R.drawable.bxfile_file_txt);
		resMap.put(MimeType.XLS, R.drawable.bxfile_file_xls);
		resMap.put(MimeType.UNKNOWN, R.drawable.bxfile_file_unknow);
		
		choosedFiles = new ArrayList<TFile>();
	}
	
	public MimeType getMimeType(String exspansion){
		return map.get(exspansion.toLowerCase());
	}
	public Integer getMimeDrawable(MimeType type){
		return resMap.get(type);
	}

	public List<TFile> getChoosedFiles() {
		return choosedFiles;
	}

	public String getFilesSizes(){
		long sum = 0;
		for(TFile f:choosedFiles){
			sum+=f.getFileSize();
		}
		return FileUtils.getFileSizeStr(sum);
	}

	public int getFilesCnt(){
		return choosedFiles.size();
	}
	
	public void clear(){
		choosedFiles.clear();
	}

	public boolean isOverMaxCnt(){
		return getFilesCnt()>=maxFileCnt;
	}

	public synchronized List<TFile> getMediaFiles(Activity cxt , Uri uri) {
		Cursor mCursor = cxt.managedQuery(
				uri,
				new String[] {MediaStore.Audio.Media.DATA}, null,
				null, " date_modified desc");
		cxt.startManagingCursor(mCursor);
		int count = mCursor.getCount();
		if(count>0){
			List<TFile> data = new ArrayList<TFile>();
			if (mCursor.moveToFirst()) {
				do {
					TFile.Builder builder = new TFile.Builder(mCursor.getString(0));
					TFile bxfile = builder.build();
					if(null != bxfile)
						data.add(bxfile);
				} while (mCursor.moveToNext());
			}
			return data;
		}else{
			return null;
		}
	}

	public synchronized int getMediaFilesCnt(Activity cxt , Uri uri) {
		Cursor mCursor = cxt.managedQuery(
				uri,
				new String[] {MediaStore.Audio.Media.DATA}, null,
				null, null);
		cxt.startManagingCursor(mCursor);
		int cnt = mCursor.getCount();
		return cnt;
	}

}
