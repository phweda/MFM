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

package Phweda.MFM.mame;

import Phweda.MFM.mame.softwarelist.Softwarelist;
import Phweda.MFM.mame.softwarelist.Softwarelists;
import Phweda.utils.PersistUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/26/2017
 * Time: 5:26 PM
 */
public class ParseSoftwareLists {

    /**
     * Import all Software Lists and populate into Softwarelists
     *
     * @param softwarelists
     * @param directory
     */
    public static void generateSoftwareLists(Softwarelists softwarelists, String directory) {
        TreeSet<Softwarelist> set = new TreeSet<>();

        try {
            Files.list(Paths.get(directory))
                    .forEach((Path file) -> {
                        if (file.toString().endsWith(".xml")) {
                            Softwarelist softwareList =
                                    (Phweda.MFM.mame.softwarelist.Softwarelist) PersistUtils.retrieveJAXB(
                                            file.toAbsolutePath().toString(), Phweda.MFM.mame.softwarelist.Softwarelist.class);
                            set.add(softwareList);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        softwarelists.setSoftwarelists(set);
    }

}

