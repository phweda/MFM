/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2017.  Author phweda : phweda1@yahoo.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package Phweda.MFM;

import Phweda.MFM.mame.Mame;
import Phweda.utils.PersistUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/19/2015
 * Time: 6:23 PM
 */

/**
 * fixme again singleton pattern with mostly static methods
 */
public class MFM_Data {
    private static final MFM_Data ourInstance;

    private static HashMap<String, Object> settings = new HashMap<String, Object>();

    private static File controllersLabelsFile;
    private static File folderNamesFile;

    // Machine:built-in(catver,nplayers,category roots, category/arcade:system):
    private static HashMap<String, Object> permData = new HashMap<String, Object>();
    private static Mame mame;

    private static boolean staticChanged = false;
    private static boolean firstCall = true;
    private static boolean loaded = false;

    static {
        if (MFM.isSystemDebug()) {
            System.out.println("MFM_Data static initializer");
        }
        ourInstance = new MFM_Data();
        ourInstance.loadData();
    }

    public static MFM_Data getInstance() {
        return ourInstance;
    }

    static boolean isLoaded() {
        return loaded;
    }

    private static void persistMameData() {
        // TODO when we have user triggered Parse from UI need to put this on its own thread
        // TODO and flag to block closing MFM during save
        PersistUtils.saveJAXB(mame, MFM.MFM_SETTINGS_DIR + MFM.MFM_MAME_FILE, mame.getClass());
    }

    public static void persistStaticData() {
        // TODO is this boolean needed? Maybe in the future with updates to Data?
        if (!staticChanged) {
            return;
        }
        Thread persist = new Thread() {
            @Override
            public void run() {
                if (MFM.isSystemDebug()) {
                    System.out.print("##");
                }

                synchronized (this) {
                    String logMessage = "persistStaticData: " + permData.size() + " : " +
                            permData.keySet() + "\n\n";
                    System.out.println(logMessage);
                    MFM.logger.addToList(logMessage);

                    PersistUtils.saveAnObject(permData, MFM.MFM_SETTINGS_DIR + MFM.MFM_CACHE_SER);
                    //    PersistUtils.saveAnObjectXML(permData, MFM.MFM_SETTINGS_DIR + MFM.MFM_CACHE_XML);
                    setStaticChanged(false);
                }
            }
        };
        persist.start();

/*

        Thread persist = new Thread(){
            @Override
            public void run() {
                if(MFM.isSystemDebug()){
                    System.out.print("##");
                }
                MFM.logger.addToList("##");
                int trys = 0;
                while (trys < 20) {
                    trys++;
                    if (isStaticChanged()){
                        try {
                            synchronized (this){
                                if(MFM.isSystemDebug()){
                                    System.out.println("##");
                                }
                                MFM.logger.addToList("##");
                                // Wait 20 seconds as hack to ensure repeated saves from multiple calls
                                wait(20000);

                                String logMessage = "persistStaticData: " + permData.size() + " : " +
                                        permData.keySet() + "\n\n";
                                System.out.println(logMessage);
                                MFM.logger.addToList(logMessage);

                                PersistUtils.saveAnObject(permData, MFM.MFM_SETTINGS_DIR + MFM.MFM_CACHE_SER);
                                setStaticChanged(false);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        persist.start();
*/
    }

    public static boolean contains(String key) {
        return (settings.containsKey(key) || permData.containsKey(key));
    }

    private void loadData() {
        MFM.showBusy(true, true);
        if (MFM.isSystemDebug()) {
            System.out.print("Loading data");
        }

        try {
            if (new File(MFM.MFM_SETTINGS_DIR + MFM.MFM_MAME_FILE).exists()) {
                long millis = System.currentTimeMillis();
                if (MFM.isSystemDebug()) {
                    System.out.println("\nMAME load starting: " + new Date(millis));
                }

                mame = (Mame) PersistUtils.retrieveJAXB(MFM.MFM_SETTINGS_DIR + MFM.MFM_MAME_FILE, Mame.class);
                if (MFM.isSystemDebug()) {
                    System.out.println("MAME load took: " +
                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis));
                }
            }

            if (new File(MFM.MFM_SETTINGS_DIR + MFM.MFM_SETTINGS_FILE).exists()) {
                settings = (HashMap<String, Object>)
                        PersistUtils.loadAnObjectXML(MFM.MFM_SETTINGS_DIR + MFM.MFM_SETTINGS_FILE);
            }

            if (new File(MFM.MFM_SETTINGS_DIR + MFM.MAME_CONTROLLERS).exists()) {
                controllersLabelsFile = new File(MFM.MFM_SETTINGS_DIR + MFM.MAME_CONTROLLERS);
            }

            if (new File(MFM.MFM_SETTINGS_DIR + MFM.MAME_FOLDER_NAMES_FILE).exists()) {
                folderNamesFile = new File(MFM.MFM_SETTINGS_DIR + MFM.MAME_FOLDER_NAMES_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (new File(MFM.MFM_SETTINGS_DIR + MFM.MFM_CACHE_SER).exists()) {
                permData = (HashMap<String, Object>)
                        PersistUtils.loadAnObject(MFM.MFM_SETTINGS_DIR + MFM.MFM_CACHE_SER);

/*
            if (new File(MFM.MFM_SETTINGS_DIR + MFM.MFM_CACHE_XML).exists()) {
                permData = (HashMap<String, Object>)
                        PersistUtils.loadAnObjectXML(MFM.MFM_SETTINGS_DIR + MFM.MFM_CACHE_XML);
*/
                if (permData.isEmpty() || !permData.containsKey(MFMListBuilder.CATEGORY_LISTS_HASHMAP)) {
                    try {
                        HashMap<String, ArrayList<String>> categoryRootsMap = (HashMap<String, ArrayList<String>>)
                                PersistUtils.loadAnObjectXML(MFM.MFM_CATEGORY_DIR + MFM.MFM_CATEGORY_DATA_FILE);
                        permData.put(MFMListBuilder.CATEGORY_LISTS_HASHMAP, categoryRootsMap);
                        // NOTE should not need persist call here
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loaded = true;
        MFM.showBusy(false, true);
    }

    File getControllerLabelsFile() {
        return controllersLabelsFile;
    }

    File getFolderNamesFile() {
        return folderNamesFile;
    }

    final void setObject(String key, Object obj) {
        settings.put(key, obj);
        persistSettings();
    }

    final Object getObject(String key) {
        return settings.get(key);
    }

    final void setStaticData(String key, Object obj) {
        if (MFM.isSystemDebug()) {
            System.out.println("setStaticData : " + key);
        }
        permData.put(key, obj);
        setStaticChanged(true);
/*
        if (firstCall) {
            firstCall = false;
            persistStaticData();
        }
*/
        //   persistStaticData();
    }

    final Object getStaticData(String key) {
        if (MFM.isSystemDebug()) {
            System.out.println("Get static data " + key);
        }
        return permData.get(key);
    }

    public boolean isStaticChanged() {
        return staticChanged;
    }

    private static void setStaticChanged(boolean staticChanged) {
        MFM_Data.staticChanged = staticChanged;
    }

    public Mame getMame() {
        return mame;
    }

    public void setMame(Mame mame) {
        MFM_Data.mame = mame;
        persistMameData();
    }

    private void persistSettings() {
        PersistUtils.saveAnObjectXML(settings, MFM.MFM_SETTINGS_DIR + MFM.MFM_SETTINGS_FILE);
    }

/*  Note does not save significant memory. We have live references to all these objects
    public static void flush(){
        permData = null;
        System.gc();
        if (MFM.isSystemDebug()) {
            System.out.println("Perm Data purged from memory");
        }
    }
*/

    /**
     * Deserialization hook to ensure Singleton behavior
     *
     * @return Object instance
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return ourInstance;
    }
}
