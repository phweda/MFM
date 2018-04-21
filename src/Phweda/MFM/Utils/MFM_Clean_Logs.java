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

package Phweda.MFM.Utils;

import Phweda.MFM.MFM;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 7/9/13
 * Time: 4:04 PM
 */
public class MFM_Clean_Logs {
    public static void cleanLogs() {
        // 86,400,000 == 1 day in milliseconds
        long deleteTime = System.currentTimeMillis() - 86400000;

        File logsDirectory = new File(MFM.MFM_LOGS_DIR);
        for (File child : logsDirectory.listFiles()) {
            if (child.lastModified() < deleteTime)
                try {
                    Files.deleteIfExists(Paths.get(child.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
