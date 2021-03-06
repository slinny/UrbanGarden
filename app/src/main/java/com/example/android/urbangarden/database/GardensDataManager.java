package com.example.android.urbangarden.database;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.urbangarden.model.Garden;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by amirahoxendine on 3/3/18.
 */

public class GardensDataManager {
    //populate db
    //get a list of saved
    //update saved
    //delete
    private static String TAG = GardensDataManager.class.getSimpleName();
    public static List<Garden> allGardenList;
  public static List<Garden> savedGardenList;
    public static void populateDBWithList(final List<Garden> gardenList, final GardensDatabase db){
    new GardensDBTasks(new DatabaseTaskListener() {
        @Override
        public void backgroundTask() {
            db.gardensDao().insertAllGardens(gardenList);
            Log.d(TAG, "pop method: insert gardens list size: " + gardenList.size());
            int count = db.gardensDao().countGardens();
            Log.d(TAG, "pop method: gardens count: " + count);
        }

        @Override
        public void taskPostExecute() {

        }
    }).execute();
    }

    public static List<Garden> getAllGardens(final GardensDatabase db){
        allGardenList = new ArrayList<>();

        new GardensDBTasks(new DatabaseTaskListener() {
            @Override
            public void backgroundTask() {
                allGardenList.addAll(db.gardensDao().getAll());
                Log.d(TAG, "all size: " + allGardenList.size());
            }
            @Override
            public void taskPostExecute() {

            }
        }).execute();
        return savedGardenList;
    }

    public static List<Garden> getSavedGardens(final GardensDatabase db){
        savedGardenList =new ArrayList<>();

        new GardensDBTasks(new DatabaseTaskListener() {
            @Override
            public void backgroundTask() {
               savedGardenList.addAll(db.gardensDao().getSaved());
                Collections.reverse(savedGardenList);
               Log.d(TAG, "saved size: " + savedGardenList.size());
            }
            @Override
            public void taskPostExecute() {

            }
        }).execute();
        return savedGardenList;
    }

    public static void addGarden(final Garden garden, final GardensDatabase db){
        new GardensDBTasks(new DatabaseTaskListener() {
            @Override
            public void backgroundTask() {
                db.gardensDao().insertGarden(garden);
            }

            @Override
            public void taskPostExecute() {

            }
        }).execute();
    }

    public static void updateGardenToSaved(final String id, final GardensDatabase db){
        new GardensDBTasks(new DatabaseTaskListener() {
            @Override
            public void backgroundTask() {
                db.gardensDao().saveGarden(id);
                Log.d(TAG, "SAVED GARDEN MANAGER : GARDEN SAVED");
            }

            @Override
            public void taskPostExecute() {
            }
        }).execute();
    }
    public static List<Garden> getAllDummyGardens(final GardensDatabase db, final DummyDataListener listener){
        allGardenList = new ArrayList<>();

        new GardensDBTasks(new DatabaseTaskListener() {
            @Override
            public void backgroundTask() {
                allGardenList.addAll(db.gardensDao().getAll());
                Log.d(TAG, "all size: " + allGardenList.size());
            }
            @Override
            public void taskPostExecute() {
                listener.postExecute(allGardenList);

            }
        }).execute();
        return savedGardenList;
    }

    public static List<Garden> getDummySavedGardens(final GardensDatabase db, final DummyDataListener listener){
        savedGardenList =new ArrayList<>();

        new GardensDBTasks(new DatabaseTaskListener() {
            @Override
            public void backgroundTask() {
                savedGardenList.addAll(db.gardensDao().getSaved());
                Log.d(TAG, "saved size: " + savedGardenList.size());
            }
            @Override
            public void taskPostExecute() {
                listener.postExecute(savedGardenList);

            }
        }).execute();
        return savedGardenList;
    }


    public interface DatabaseTaskListener{
        void backgroundTask();
        void taskPostExecute();
    }

    public static class GardensDBTasks extends AsyncTask<Void, Void, Void>{
        DatabaseTaskListener taskListener;

        public GardensDBTasks(DatabaseTaskListener listener){
            this.taskListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            taskListener.backgroundTask();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            taskListener.taskPostExecute();
        }
    }
}
