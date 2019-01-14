/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2011 - 2018.  Author phweda : phweda1@yahoo.com
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

package com.github.phweda.mfm.mame;

import com.github.phweda.mfm.MFM;
import com.github.phweda.mfm.MFMSettings;
import com.github.phweda.mfm.utils.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/5/2016
 * Time: 12:32 PM
 */
public class LoadNonMAMEresources {

    private static HashMap<String, String> HistoryDAT = new HashMap<String, String>();
    private static HashMap<String, String> MAMEInfoDAT = new HashMap<String, String>();
    private static HashMap<String, String> MESSInfoDAT = new HashMap<String, String>();
    private static HashMap<String, String> SYSInfoDAT = new HashMap<String, String>();
    private static HashMap<String, String> Version = new HashMap<String, String>();
    private static HashMap<String, String> nplayers = new HashMap<String, String>();
    private static HashMap<String, String> MachinetoCategoryMap = new HashMap<String, String>();

    private static MFMSettings mfmSettings = MFMSettings.getInstance();

    static {
        loadCatverINI();
        loadNplayersINI();

        /* Load history.dat if it exists */     // && !MAMESettings.HistoryDATFILENAME().equals("")
        if (mfmSettings.getHistoryDAT() != null && !mfmSettings.getHistoryDAT().equals("") && !MFM.isListOnly()) {
            loadHistoryDAT();
        }

        /* Load MAMEInfo.dat if it exists */
        if (mfmSettings.getMAMEInfoDAT() != null && !mfmSettings.getMAMEInfoDAT().equals("")) {
            loadMAMEInfoDAT();
        }

        /* Load MESSInfo.dat if it exists */
        if (mfmSettings.getMESSInfoDAT() != null && !mfmSettings.getMESSInfoDAT().equals("")) {
            loadMESSInfoDAT();
        }

        /* Load SYSInfo.dat if it exists */
        if (mfmSettings.getSYSInfoDAT() != null && !mfmSettings.getSYSInfoDAT().equals("")) {
            loadSYSInfoDAT();
        }
    }

    static HashMap<String, String> getHistoryDAT() {
        return HistoryDAT;
    }

    static HashMap<String, String> getMAMEInfoDAT() {
        return MAMEInfoDAT;
    }

    static HashMap<String, String> getMESSInfoDAT() {
        return MESSInfoDAT;
    }

    static HashMap<String, String> getSYSInfoDAT() {
        return SYSInfoDAT;
    }

    static HashMap<String, String> getVersion() {
        return Version;
    }

    static HashMap<String, String> getNplayers() {
        return nplayers;
    }

    public static HashMap<String, String> getMachinetoCategoryMap() {
        return MachinetoCategoryMap;
    }

    private static void loadHistoryDAT() {
        try {
            new ParseHistoryDAT(mfmSettings.getHistoryDAT(), HistoryDAT).processFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadMAMEInfoDAT() {
        try {
            new ParseExtrasInfoDATs(mfmSettings.getMAMEInfoDAT(), MAMEInfoDAT).processFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadMESSInfoDAT() {
        try {
            new ParseExtrasInfoDATs(mfmSettings.getMESSInfoDAT(), MESSInfoDAT).processFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadSYSInfoDAT() {
        try {
            new ParseSYSINFODAT(mfmSettings.getSYSInfoDAT(), SYSInfoDAT).processFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadCatverINI() {
        try {
            if (MFM.isDebug()) {
                MFM.getLogger().addToList("In loadCatverINI");
            }
            Map CatverINImap = new HashMap();
            CatverINImap.put("category", MachinetoCategoryMap);
            CatverINImap.put("version", Version);

            new ParseCatverINI(mfmSettings.getCatverINI(), CatverINImap).processFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadNplayersINI() {
        try {
            if (MFM.isDebug()) {
                MFM.getLogger().addToList("In loadNplayersINI");
            }
            new ParsenPlayerINI(mfmSettings.getnPlayerINI(), nplayers).processFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
