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

package com.github.phweda.utils;

/**
 * Created by IntelliJ IDEA.
 * User: phweda
 * Date: 10/26/2015
 * Time: 12:19 PM
 */
public class QuadState {
    public static final String ALL = "All";
    private static final int ALL_THREE = 0;
    private static final int FIRST = 1;
    private static final int SECOND = 2;
    private static final int THIRD = 3;
    private final String firstName;
    private final String secondName;
    private final String thirdName;
    private int state;

    public QuadState(String first, String second, String third, String state) {
        this.firstName = first;
        this.secondName = second;
        this.thirdName = third;
        setState(state);
    }

    public String getState() {
        if (state == FIRST) {
            return firstName;
        } else if (state == SECOND) {
            return secondName;
        } else if (state == THIRD) {
            return thirdName;
        } else if (state == ALL_THREE) {
            return ALL;
        }
        // NOTE Error condition.
        return null;
    }

    public void setState(String stateIn) {
        if (stateIn.equals(firstName)) {
            state = FIRST;
        } else if (stateIn.equals(secondName)) {
            state = SECOND;
        } else if (stateIn.equals(thirdName)) {
            state = THIRD;
        } else if (stateIn.equals(ALL)) {
            state = ALL_THREE;
        }
    }
}
