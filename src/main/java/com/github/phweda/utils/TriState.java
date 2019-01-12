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

package Phweda.utils;

/**
 * Created by IntelliJ IDEA.
 * User: phweda
 * Date: 10/12/2015
 * Time: 2:28 PM
 */

/**
 * TriState encapsulates settings that need an either or, or BOTHINT state
 */
public class TriState {

    public static final String BOTH = "all";
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int BOTHINT = 2;
    private final String firstName;
    private final String secondName;
    private int state;

    public TriState(String first, String second, String state) {
        this.firstName = first;
        this.secondName = second;
        setState(state);
    }

    public String getState() {
        if (state == FIRST) {
            return firstName;
        } else if (state == SECOND) {
            return secondName;
        } else if (state == BOTHINT) {
            return BOTH;
        }
        // NOTE Error condition.
        return "ERROR";
    }

    public void setState(String stateIn) {
        if (stateIn.equals(firstName)) {
            state = FIRST;
        } else if (stateIn.equals(secondName)) {
            state = SECOND;
        } else if (stateIn.equals(BOTH)) {
            state = BOTHINT;
        }
    }
}
