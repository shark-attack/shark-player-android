package com.sharkattack.data;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ShowDownloader extends AsyncTask<String, Integer, Long> {

    /** show download listener */
    protected List<OnShowDownloadListener> listeners = new ArrayList<OnShowDownloadListener>();

    /**
     * background task
     * @param zipurls
     */
    public Long doInBackground(String... zipurls) {
        Log.v("Blastanova::" + this.getClass().toString(), "Download " + zipurls[0]);
        long totalSize = 0;
        try {
            URL url = new URL(zipurls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            File storage = new File(Environment.getExternalStorageDirectory(), "sharkattack/currentshow");
            if (!storage.exists()) {
                storage.mkdirs();
            }

            if (storage.isDirectory())
            {
                String[] children = storage.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(storage, children[i]).delete();
                }
            }

            File file = new File(storage,"currentshow.zip");
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();

            totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

                float prog = ( (float) downloadedSize/totalSize ) * 100;
                publishProgress( (int) prog, ShowDownloadEvent.DOWNLOADING);

            }

            fileOutput.close();
            unZipIt("currentshow.zip", storage);

        } catch (MalformedURLException e) {
            Log.e("Blastanova::" + this.getClass().toString(), e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Blastanova::" + this.getClass().toString(), e.toString());
            e.printStackTrace();
        }
        return totalSize;
    };

    /**
     * set the download listener
     * @param l listener
     */
    public void setOnShowDownloadListener(OnShowDownloadListener l) {
        listeners.add(l);
    }


    protected void onProgressUpdate(Integer... progress) {
        for (OnShowDownloadListener listener : listeners) {
            ShowDownloadEvent se = new ShowDownloadEvent(ShowDownloadEvent.DOWNLOAD_PROGRESS);
            se.progress = ((int) progress[0]);
            se.mode = ((int) progress[1]);
            listener.onShowDownloadProgress(se);
        }
    }

    protected void onPostExecute(Long result) {
        for (OnShowDownloadListener listener : listeners) {
            ShowDownloadEvent se = new ShowDownloadEvent(ShowDownloadEvent.DOWNLOAD_COMPLETE);
            listener.onShowDownloadComplete(se);
        }
    }

    /**
     * Unzip it
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public void unZipIt(String zipFile, File outputFolder){
        publishProgress(100, ShowDownloadEvent.UNZIPPING);
        byte[] buffer = new byte[1024];

        try{

            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(outputFolder.getAbsolutePath() + File.separator + zipFile));
            Log.v("Shark::" + this.getClass().toString(), "unzip : "+ outputFolder.getAbsolutePath() + File.separator + zipFile);
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){
                String fileName = ze.getName();
                File newFile = new File(outputFolder, fileName);

                //Log.v("Shark::" + this.getClass().toString(), "file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            Log.v("Blastanova::" + this.getClass().toString(), "Unzip done");

        }catch(IOException ex){
            Log.e("Blastanova::" + this.getClass().toString(), ex.toString());
            ex.printStackTrace();
        }
        return;
    }
}
