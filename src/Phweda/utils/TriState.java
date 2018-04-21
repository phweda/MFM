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
 * User: Phweda
 * Date: 10/12/2015
 * Time: 2:28 PM
 */

/**
 * TriState encapsulates settings that need an either or, or both state
 */
public class TriState {

    public static final String BOTH = "all";
    private static final int first = 0;
    private static final int second = 1;
    private static final int both = 2;
    private final String firstName;
    private final String secondName;
    private int state;

    public TriState(String first, String second, String state) {
        this.firstName = first;
        this.secondName = second;
        setState(state);
    }

    public String getState() {
        if (state == first) {
            return firstName;
        } else if (state == second) {
            return secondName;
        } else if (state == both) {
            return BOTH;
        }
        // NOTE Error condition.
        return "ERROR";
    }

    public void setState(String stateIn) {
        if (stateIn.equals(firstName)) {
            state = first;
        } else if (stateIn.equals(secondName)) {
            state = second;
        } else if (stateIn.equals(BOTH)) {
            state = both;
        } else {
            // NOTE never get here!!!
        }
    }
}
