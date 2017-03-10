/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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
 * Date: 10/26/2015
 * Time: 12:19 PM
 */
public class QuadState {
    private final int first = 1;
    private final int second = 2;
    private final int third = 3;
    public final int all = 0;

    private int state;

    public static final String ALL = "All";
    private final String firstName;
    private final String secondName;
    private final String thirdName;

    public QuadState(String first, String second, String third, String state) {
        this.firstName = first;
        this.secondName = second;
        this.thirdName = third;
        setState(state);
    }

    public void setState(String stateIn) {
        if (stateIn.equals(firstName)) {
            state = first;
        } else if (stateIn.equals(secondName)) {
            state = second;
        } else if (stateIn.equals(thirdName)) {
            state = third;
        } else if (stateIn.equals(ALL)) {
            state = all;
        } else {
            // NOTE never get here!!!
        }
    }

    public String getState() {
        if (state == first) {
            return firstName;
        } else if (state == second) {
            return secondName;
        } else if (state == third) {
            return thirdName;
        } else if (state == all) {
            return ALL;
        }
        // NOTE Error condition.
        return null;
    }

}
