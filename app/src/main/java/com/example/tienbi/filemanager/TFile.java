package com.example.tienbi.filemanager;

import android.text.TextUtils;

import com.example.tienbi.filemanager.util.FileUtils;
import com.example.tienbi.filemanager.util.TimeUtils;

import java.io.File;
import java.io.Serializable;

public class TFile implements Comparable<TFile>, Serializable {
	
	public enum MimeType{
		APK,
		TXT,
		IMAGE,
		RAR,
		DOC,
		PPT,
		XLS,
		HTML,
		MUSIC,
		VIDEO,
		PDF,
		UNKNOWN
	}

	public enum FileState{
		DOWNLOADED,
		UNLOAD,
		SENDED,
		UNSEND
	}
	
	private TFile(){}
	
	private String fileName;
	private String fileUrl;
	private String filePath;
	private boolean isDir;
	private long lastModifyTime;
	private long fileSize;
	private String fileSizeStr;
	private String lastModifyTimeStr;
	private MimeType mimeType;
	private FileState fileState;
	
	public FileState getFileState() {
		return fileState;
	}
	public void setFileState(FileState fileState) {
		this.fileState = fileState;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public String getFileName() {
		return fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public boolean isDir() {
		return isDir;
	}
	public long getLastModifyTime() {
		return lastModifyTime;
	}
	public long getFileSize() {
		return fileSize;
	}
	public String getFileSizeStr() {
		return fileSizeStr;
	}
	public MimeType getMimeType() {
		return mimeType;
	}
	public String getLastModifyTimeStr() {
		return lastModifyTimeStr;
	}

	public static class Builder{
		TFile bxFile;
		
		public Builder(String path){
			if(FileUtils.isFileExist(path)){
				File file = new File(path); 
				bxFile = new TFile();
				bxFile.fileName = file.getName();
				bxFile.filePath = file.getAbsolutePath();
				boolean isDir = file.isDirectory();
				bxFile.isDir = isDir;
				//if(!isDir){
					bxFile.fileSize = file.length();
					bxFile.fileSizeStr = FileUtils.getFileSizeStr(bxFile.fileSize);
					bxFile.lastModifyTime = file.lastModified();
					bxFile.lastModifyTimeStr = TimeUtils.getDateTime(bxFile.lastModifyTime);
					String exspansion = FileUtils.getExspansion(bxFile.fileName);
					if(TextUtils.isEmpty(exspansion))
						bxFile.mimeType = MimeType.UNKNOWN;
					else{
						MimeType mimeType = FileManager.getInstance().getMimeType(exspansion);
						bxFile.mimeType = null==mimeType? MimeType.UNKNOWN:mimeType;
					}
				//}
			}
		}
		
		public TFile build(){
			return bxFile;
		}
	}

	public static class UrlBuilder{
		TFile bxFile;
		
		public UrlBuilder(String fileUrl , String fileName , long fileSize , String savedPath , FileState fileState){
			bxFile = new TFile();
			bxFile.fileUrl = fileUrl;
			bxFile.fileName = fileName;
			bxFile.fileSize = fileSize;
			bxFile.fileState = fileState;
			bxFile.fileSizeStr = FileUtils.getFileSizeStr(fileSize);
			bxFile.filePath = savedPath;
			String exspansion = FileUtils.getExspansion(fileName);
			if(TextUtils.isEmpty(exspansion))
				bxFile.mimeType = MimeType.UNKNOWN;
			else{ 
				MimeType mimeType = FileManager.getInstance().getMimeType(exspansion);
				bxFile.mimeType = null==mimeType? MimeType.UNKNOWN:mimeType;
			}
		}
		
		public TFile build(){
			return bxFile;
		}
	}

	@Override
	public int compareTo(TFile another) {
		if(isDir()){
			if(another.isDir())
				return fileName.compareToIgnoreCase(another.getFileName());
			else
				return -1;
		}else{
			if(another.isDir())
				return 1;
			else
				return fileName.compareToIgnoreCase(another.getFileName());
		}
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(null == o)
			return false;
		if(o instanceof TFile){
			TFile other = (TFile)o;
			return other.filePath.equals(filePath);
		}else{
			return false;
		}
	}
	@Override
	public int hashCode() {
		return filePath.hashCode();
	}
	
	public boolean isFileSizeValid(){
		return fileSize<FileManager.getInstance().getMaxFileSize();
	}
}
