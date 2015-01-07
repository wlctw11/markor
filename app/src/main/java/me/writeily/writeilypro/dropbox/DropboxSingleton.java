package me.writeily.writeilypro.dropbox;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFileSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by jeff on 14-12-11.
 */
public class DropboxSingleton {

    private static DropboxSingleton dropboxSingletonInstance = null;
    private static File notesLastDirectory = null;
    private DbxFileSystem dbxFileSystem;
    private DbxAccountManager dbxAccountManager;

    private static void WriteilySingleton() {

    }

    public static DropboxSingleton getInstance() {
        if (dropboxSingletonInstance == null) {
            dropboxSingletonInstance = new DropboxSingleton();
        }

        return dropboxSingletonInstance;
    }

    public File getNotesLastDirectory() {
        return notesLastDirectory;
    }

    public void setNotesLastDirectory(File notesLastDirectory) {
        DropboxSingleton.notesLastDirectory = notesLastDirectory;
    }

    public void copyFile(File file, String destinationDir) {
        try {
            String filename = file.getName();

            File outputFile = new File(destinationDir + File.separator + filename);
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            FileInputStream is = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);

            String fileContent;
            StringBuilder fileContentBuilder = new StringBuilder();

            while ((fileContent = br.readLine()) != null) {
                fileContentBuilder.append(fileContent + "\n");
            }

            writer.write(fileContentBuilder.toString());
            writer.flush();

            writer.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveFile(File file, String destinationDir) {
        copyFile(file, destinationDir);

        // Delete the old file after copying it over
        deleteFile(file);
    }

    public boolean deleteFile(File file) {
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                deleteFile(childFile);
            }
        }

        return file.delete();
    }

    public void deleteSelectedNotes(ListView notesListView, BaseAdapter notesAdapter) {
        SparseBooleanArray checkedIndices = notesListView.getCheckedItemPositions();
        for (int i = 0; i < checkedIndices.size(); i++) {
            if (checkedIndices.valueAt(i)) {
                File file = (File) notesAdapter.getItem(checkedIndices.keyAt(i));
                deleteFile(file);
            }
        }
    }

    public void moveSelectedNotes(ListView filesListView, BaseAdapter filesAdapter, String destination) {
        SparseBooleanArray checkedIndices = filesListView.getCheckedItemPositions();
        for (int i = 0; i < checkedIndices.size(); i++) {
            if (checkedIndices.valueAt(i)) {
                File file = (File) filesAdapter.getItem(checkedIndices.keyAt(i));
                moveFile(file, destination);
            }
        }
    }

    public void copySelectedNotes(SparseBooleanArray checkedIndices, BaseAdapter notesAdapter, String destination) {
        for (int i = 0; i < checkedIndices.size(); i++) {
            if (checkedIndices.valueAt(i)) {
                File file = (File) notesAdapter.getItem(checkedIndices.keyAt(i));
                copyFile(file, destination);
            }
        }
    }

    /**
     * Hide the header when getting to the external dir so the app doesn't show too much.
     */
    public boolean isRootDir(File previousDir, File compareDir) {
        return (previousDir == null || previousDir.getPath().equalsIgnoreCase(compareDir.getAbsolutePath()));
    }

    public boolean isDirectoryEmpty(ArrayList<File> files) {
        return (files == null || files.isEmpty());
    }

    /**
     * Recursively add all files from the specified directory
     * @param dir the directory to add files from
     */
    public ArrayList<File> addFilesFromDirectory(File dir, ArrayList<File> files) {
        for (File f : dir.listFiles()) {
            if (!f.getName().startsWith(".")) {
                Log.d("Adding file:", f.getAbsolutePath());
                files.add(f);
            }
        }
        return files;
    }

    /**
     * Recursively add all files from the specified directory
     * @param dir the directory to add files from
     */
    public ArrayList<File> addDirectories(File dir, ArrayList<File> files) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                Log.d("Adding directory:", f.getAbsolutePath());
                files.add(f);
            }
        }
        return files;
    }

    public void setDbxFileSystem(DbxFileSystem dbxFileSystem) {
        this.dbxFileSystem = dbxFileSystem;
    }

    public void setDbxAccountManager(DbxAccountManager dbxAccountManager) {
        this.dbxAccountManager = dbxAccountManager;
    }

    public DbxAccountManager getDbxAccountManager() {
        return dbxAccountManager;
    }

    public DbxFileSystem getDbxFileSystem() {
        return dbxFileSystem;
    }
}
