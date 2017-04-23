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

package Phweda.MFM.mame;

import Phweda.MFM.MAMEexe;
import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MFMSettings;
import Phweda.MFM.MFM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/1/13
 * Time: 5:26 PM
 */
public class MAME_Game_Prefixes {
    Set<String> prefixes;
    Set<String> games;

    private static final int breakInt = 1000; // Arbitrary number based on experience


    public MAME_Game_Prefixes() throws IOException {
        games = getGames();
        prefixes = getPrefixes();
        System.out.println(prefixes.toString());
        if (MFM.isDebug()) {
            MFM.logger.addToList(prefixes.toString());
        }
    }

    protected TreeSet<String> getPrefixes() {
        TreeSet<String> ts = new TreeSet<String>();
        populatePrefixes(ts);
        return ts;
    }

    private void populatePrefixes(TreeSet<String> ts) {
        HashMap<String, AtomicInteger> hm = new HashMap<String, AtomicInteger>();
        countGames(hm, 1, null);
        for (String s : hm.keySet()) {
            ts.add(s);
        }
        MFM.logger.addToList(hm.toString());
    }

    private void countGames(HashMap<String, AtomicInteger> hm, int length, String startsWith) {
        for (String game : games) {
            String str = null;
            if (game.length() >= length) {
                str = game.substring(0, length);
            }
            if (str != null && (startsWith == null || str.startsWith(startsWith))) {
                if (hm.containsKey(str)) {
                    hm.get(str).incrementAndGet();
                } else {
                    hm.put(str, new AtomicInteger(1));
                }
            }
        }
        if (length == 1) {
            checkKeys(hm, length);
        }
    }

    private void checkKeys(HashMap<String, AtomicInteger> hm, int length) {
        ArrayList<String> keys = new ArrayList<String>(10);
        for (String key : hm.keySet()) {
            if (hm.get(key).intValue() > breakInt) {
                keys.add(key);
            }
        }
        keys.trimToSize();
        if (keys.size() > 0) {
            int newLength = length + 1;
            for (String str : keys) {
                countGames(hm, newLength, str);
            }
            for (String str : keys) {
                hm.remove(str);
            }
            checkKeys(hm, length + 1);
        }
    }

    private static TreeSet<String> getGames() throws IOException {
        TreeSet<String> ts = new TreeSet<>();
        ArrayList<String> fullListargs = new ArrayList<String>();
        fullListargs.add("-listfull");
        Process process = null;
        try {
            process = MAMEexe.run(fullListargs);
        } catch (MAMEexe.MAME_Exception e) {
            e.printStackTrace(MFM.logger.Writer());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        // Skip the first line we know it is : "getName:             Description: "
        br.readLine();
        while ((line = br.readLine()) != null) {
            ts.add(line.substring(0, line.indexOf(' ')));
        }
        br.close();
        return ts;
    }

    /**
     * Testing
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            new MFM();
            if (MFMSettings.getInstance().isLoaded()) {
                MAMEInfo.getInstance(false);
            } else {
                MFM.logger.addToList("No MAME Settings ", true);
            }
            new MAME_Game_Prefixes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}