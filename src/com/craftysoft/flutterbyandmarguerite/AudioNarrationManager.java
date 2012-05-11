package com.craftysoft.flutterbyandmarguerite;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnPreparedListener;

public final class AudioNarrationManager {

	
/*	recorder = new MediaRecorder();    
	ContentValues values = new ContentValues(3);
	    values.put(MediaStore.MediaColumns.TITLE, SOME_NAME_HERE);   
	     values.put(MediaStore.MediaColumns.TIMESTAMP, System.currentTimeMillis());    
	     values.put(MediaStore.MediaColumns.MIME_TYPE, recorder.getMimeContentType());       
	      ContentResolver contentResolver = new ContentResolver();      
	        Uri base = MediaStore.Audio.INTERNAL_CONTENT_URI;    
	        Uri newUri = contentResolver.insert(base, values);      
	          if (newUri == null) {       
	           // need to handle exception here - we were not able to create a new       
	            *  // content entry    }      
	            *    String path = contentResolver.getDataFilePath(newUri);   
	            *     // could use setPreviewDisplay() to display a preview to suitable View here      
	            *       recorder.setAudioSource(MediaRecorder.AudioSource.MIC);    
	            *       recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  
	            *         recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);   
	            *          recorder.setOutputFile(path);      
	            *            recorder.prepare();    
	            *            recorder.start();
*/

	/*
	 * Narration profiles will be directories prefixed with "fby_" located in the flutterby directory of the mounted sdcard	 
	 * ./flutterby/nar_audio/fby_grandma
	 */

	public static MediaRecorder recorder = null;
//	private final static String appDirectory = "flutterby";//root folder on the sd card of the application
	private final static String _profileRootFolderName = "nar_audio";//folder that will hold all the saved profiles
	private final static String _profileFolderPrefix = "";//fby_";//all profile folders will have this prefix

	//checks if the sdcard is available
	public static boolean isSdCardAvailable()
	{	    
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	//profile folder name format: fby_SOMEFOLDER
	private static String getProfileFolderName(String profileName)
	{
		return  _profileFolderPrefix + profileName;
	}
	
	//narration audio file format: n1.3pg
	private static String getNarrationFileName(int pageIndex)
	{
		return  "n" + pageIndex + ".3gp";
	}
	
	private static String getProfileRoot(Context context)
	{
		//return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + _profileRootFolderName + "/";
		return context.getExternalFilesDir(null) + "/" + _profileRootFolderName + "/";
		//return context.getFilesDir() + "/" + _profileRootFolderName + "/";
		
	}
	
	private static String getProfileRootFolder(Context context, String profile)
	{
		return getProfileRoot(context) + getProfileFolderName(profile) + "/";
	}
		
	public static File getNarrationAudioFile(Context context, String profileName, int pageIndex)
	{
//		if(isSdCardAvailable())
//		{
			File file = new File(getProfileRootFolder(context, profileName) + getNarrationFileName(pageIndex));
			
			if(file.exists())
				return file;
			else
				return null;
//		}
//		else
//			return null;
	}
	
//	public static void createNarrationFile(Context context, String profileName, int pageIndex)
//	{
//		if(isSdCardAvailable())
//		{
//				String path = getProfileRootFolder(profileName);
//
//				File file = new File(path);
//				if(!file.mkdirs())
//					return "";
//				else
//					return path + getAudioFileName(pageIndex);
//			}
//			else
//				return "";
		
//	}
	
	public static boolean deleteNarrationAudioFile(Context context, String profileName, int pageIndex)
	{
//		if(isSdCardAvailable())
//		{
			File file = getNarrationAudioFile(context, profileName, pageIndex);
			
			if(file != null)
				return file.delete();	
			else
				return false;
//		}
//		else
//			return false;
	}

	
	
	//make sure to call createAudioNarrationFile first
	public static boolean startRecording(String fileName)
	{		
	    try 
	    {
	    	if(recorder == null)
	    		recorder  = new MediaRecorder();
		    
	    	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//.DEFAULT);
		    recorder.setOutputFile(fileName);
		    recorder.setMaxDuration(30000);//25 seconds max
			recorder.prepare();
			recorder.start();
			
			return true;
		} 
	    catch (IllegalStateException e) 
	    {
	    	recorder = null;
			return false;
		} 
	    catch (Exception e) 
	    {
	    	recorder = null;
			return false;
		}
	}
	
	public static MediaPlayer mPlayer = null;
    public static void playRecording(String mFileName) 
    {        
    	if(mPlayer == null)
    		mPlayer = new MediaPlayer();        
    	
    	try 
    	{            
    		mPlayer.setDataSource(mFileName); 
    		mPlayer.prepare();            
    		mPlayer.setOnPreparedListener(new OnPreparedListener(){

				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					mPlayer.start();
				}});
    	} 
    	catch (Exception e) 
    	{      
    		//do nothing
    	}    
    }
    
    public static void stopPlaybackRecording() 
    {        
    	if(mPlayer == null)
    		return;
    	
    	try 
    	{           
    		if(mPlayer.isPlaying())
    			mPlayer.stop();
    		mPlayer.release();
    		mPlayer = null;
    	} 
    	catch (Exception e) 
    	{      
    		//do nothing
    	}    
    }


	public static boolean stopRecording() 
	{
		try
		{
			if(recorder == null)
				return false;
		
			recorder.stop();
			recorder.release();		  
			recorder = null;
			  
			return true;
		} 
	    catch (IllegalStateException e) 
	    {
			recorder = null;

			return false;
		} 
	    catch (Exception e) 
	    {			
	    	recorder = null;
			return false;
		}
	}
	
	//gets the narration profile folder, create if not already created
	//SAVING FILES LOCALLY!!
	private static String getNarrationProfilesFolderPath(Context context)
	{
		try
		{
//			String narrationRootPath = context.getExternalFilesDir(null) + "/" + _profileRootFolderName + "/";
	//		String narrationRootPath = context.getFilesDir() + "/" + _profileRootFolderName + "/";
			String narrationRootPath = getProfileRoot(context);
			
			File root = new File(narrationRootPath);
	
			if(!root.exists())
			{
				if(root.mkdirs())
					return narrationRootPath;
				else
					return "";
			}
			else
				return narrationRootPath;			
		}
		catch(Exception ex)
		{
			return "";
		}
	}
	
	//return an array of narration profiles found on the sd card
	//returns null if none found
	public static String[] getNarrationProfiles(Context context) throws AudioManagerException
	{		
		String[] profileFolders = null;
		
		try
		{
			String narrationPath = getNarrationProfilesFolderPath(context);
			
			if(narrationPath.equals(""))
				throw new AudioManagerException("An error occurred while trying to retrieve the narration profiles.");
			
			File rootProfileFolder = new File(narrationPath);
	
			if(rootProfileFolder.exists())
			{
				//check for profile folders
				File[] flds = rootProfileFolder.listFiles(new ProfileDirectoryFilter());
				profileFolders = new String[flds.length];
		
				for(int i = 0; i < flds.length; i++)
					profileFolders[i] = flds[i].getName();
			}
			
			return profileFolders;
		}
		catch(Exception ex)
		{
			throw new AudioManagerException("An error occurred while trying to retrieve the narration profiles.");
		}
	}

	//remove an audio narration profile
	public static boolean deleteNarrationProfile(Context context, String profileName)
	{
		return deleteDirectory(new File(getNarrationProfilePath(context, profileName)));
	}
	
	private static boolean deleteDirectory(File path) 
	{
		if( path.exists() ) 
		{
			File[] files = path.listFiles();
		      
			for(int i=0; i<files.length; i++) 
			{
		         if(files[i].isDirectory()) 
		         {
		        	 deleteDirectory(files[i]);
		         }
		         else 
		         {
		        	 files[i].delete();
		         }
			}
		}
		return(path.delete());
	}
	
	private static String getNarrationProfilePath(Context context, String profileName)
	{
		return getNarrationProfilesFolderPath(context) + _profileFolderPrefix + profileName;
	}
	
	public static String getNarrationFilePath(Context context, String profileName, int pageIndex)
	{
		return getNarrationProfilePath(context, profileName) + "/" + getNarrationFileName(pageIndex);
	}


	//creates a narration profile which is only a folder with the profile name prefixed by _profilePrefix
	public static void createNarrationProfile(Context context, String profileName) throws AudioManagerException
	{
			File profileFolder = new File(getNarrationProfilePath(context, profileName));
			
			if (profileFolder.exists()) 
				throw new AudioManagerException("Narration profile with the same name already exists.");	
			else
			{
				if(profileFolder.mkdirs())
					return;
				else
					throw new AudioManagerException("An error occurred while trying to create narration profile.");
			}
	}

	  //SUBCLASS
	  private static class ProfileDirectoryFilter implements FilenameFilter
	  {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(_profileFolderPrefix);
		}
	 }
}