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

package Phweda.MFM;

import Phweda.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/3/2016
 * Time: 11:42 AM
 */
class MAME_Compatible {

    private static final Double DBL_172 = 0.172d; // last version before MAME DTD change 2016

    static boolean versionNew(String version) {
        String result = version.substring(version.indexOf('0'));
        if (MFM.isSystemDebug()) {
            System.out.println(result);
        }
        return Double.valueOf(result) > DBL_172 && compareDoctype();
    }

    private static boolean compareDoctype() {
        Document dom = null;

        Process process = null;
        try {
            // we use 9 since there are only a few of them arcade & MESS
            process = MAMEexe.runListXML("9*");
        } catch (MAMEexe.MAME_Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            dom = XMLUtils.parseXmlFile(process.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        DocumentType doctype = dom.getDoctype();
        if (doctype == null) {
            System.out.println("DOCTYPE is null");
            return false;
        } else {
            if (MAME_DTD.equalsIgnoreCase(doctype.getInternalSubset())) {
                System.out.println("SUCCESS with DTD");
                return true;
            }
            // System.out.println("DOCTYPE node:\n" + doctype.getInternalSubset());
        }
        return false;
    }

    // From 177 -listxml
    private static final String MAME_DTD =
            "<!ELEMENT mame (machine+)>\n" +
                    "<!ATTLIST mame build CDATA #IMPLIED>\n" +
                    "<!ATTLIST mame debug (yes|no) 'no'>\n" +
                    "<!ATTLIST mame mameconfig CDATA #REQUIRED>\n" +
                    "<!ELEMENT machine (description,year?,manufacturer?,biosset*,rom*,disk*,device_ref*,sample*,chip*,display*,sound?,input?,dipswitch*,configuration*,port*,adjuster*,driver?,device*,slot*,softwarelist*,ramoption*)>\n" +
                    "<!ATTLIST machine name CDATA #REQUIRED>\n" +
                    "<!ATTLIST machine sourcefile CDATA #IMPLIED>\n" +
                    "<!ATTLIST machine isbios (yes|no) 'no'>\n" +
                    "<!ATTLIST machine isdevice (yes|no) 'no'>\n" +
                    "<!ATTLIST machine ismechanical (yes|no) 'no'>\n" +
                    "<!ATTLIST machine runnable (yes|no) 'yes'>\n" +
                    "<!ATTLIST machine cloneof CDATA #IMPLIED>\n" +
                    "<!ATTLIST machine romof CDATA #IMPLIED>\n" +
                    "<!ATTLIST machine sampleof CDATA #IMPLIED>\n" +
                    "<!ELEMENT description (#PCDATA)>\n" +
                    "<!ELEMENT year (#PCDATA)>\n" +
                    "<!ELEMENT manufacturer (#PCDATA)>\n" +
                    "<!ELEMENT biosset EMPTY>\n" +
                    "<!ATTLIST biosset name CDATA #REQUIRED>\n" +
                    "<!ATTLIST biosset description CDATA #REQUIRED>\n" +
                    "<!ATTLIST biosset default (yes|no) 'no'>\n" +
                    "<!ELEMENT rom EMPTY>\n" +
                    "<!ATTLIST rom name CDATA #REQUIRED>\n" +
                    "<!ATTLIST rom bios CDATA #IMPLIED>\n" +
                    "<!ATTLIST rom size CDATA #REQUIRED>\n" +
                    "<!ATTLIST rom crc CDATA #IMPLIED>\n" +
                    "<!ATTLIST rom sha1 CDATA #IMPLIED>\n" +
                    "<!ATTLIST rom merge CDATA #IMPLIED>\n" +
                    "<!ATTLIST rom region CDATA #IMPLIED>\n" +
                    "<!ATTLIST rom offset CDATA #IMPLIED>\n" +
                    "<!ATTLIST rom status (baddump|nodump|good) 'good'>\n" +
                    "<!ATTLIST rom optional (yes|no) 'no'>\n" +
                    "<!ELEMENT disk EMPTY>\n" +
                    "<!ATTLIST disk name CDATA #REQUIRED>\n" +
                    "<!ATTLIST disk sha1 CDATA #IMPLIED>\n" +
                    "<!ATTLIST disk merge CDATA #IMPLIED>\n" +
                    "<!ATTLIST disk region CDATA #IMPLIED>\n" +
                    "<!ATTLIST disk index CDATA #IMPLIED>\n" +
                    "<!ATTLIST disk writable (yes|no) 'no'>\n" +
                    "<!ATTLIST disk status (baddump|nodump|good) 'good'>\n" +
                    "<!ATTLIST disk optional (yes|no) 'no'>\n" +
                    "<!ELEMENT device_ref EMPTY>\n" +
                    "<!ATTLIST device_ref name CDATA #REQUIRED>\n" +
                    "<!ELEMENT sample EMPTY>\n" +
                    "<!ATTLIST sample name CDATA #REQUIRED>\n" +
                    "<!ELEMENT chip EMPTY>\n" +
                    "<!ATTLIST chip name CDATA #REQUIRED>\n" +
                    "<!ATTLIST chip tag CDATA #IMPLIED>\n" +
                    "<!ATTLIST chip type (cpu|audio) #REQUIRED>\n" +
                    "<!ATTLIST chip clock CDATA #IMPLIED>\n" +
                    "<!ELEMENT display EMPTY>\n" +
                    "<!ATTLIST display tag CDATA #IMPLIED>\n" +
                    "<!ATTLIST display type (raster|vector|lcd|unknown) #REQUIRED>\n" +
                    "<!ATTLIST display rotate (0|90|180|270) #REQUIRED>\n" +
                    "<!ATTLIST display flipx (yes|no) 'no'>\n" +
                    "<!ATTLIST display width CDATA #IMPLIED>\n" +
                    "<!ATTLIST display height CDATA #IMPLIED>\n" +
                    "<!ATTLIST display refresh CDATA #REQUIRED>\n" +
                    "<!ATTLIST display pixclock CDATA #IMPLIED>\n" +
                    "<!ATTLIST display htotal CDATA #IMPLIED>\n" +
                    "<!ATTLIST display hbend CDATA #IMPLIED>\n" +
                    "<!ATTLIST display hbstart CDATA #IMPLIED>\n" +
                    "<!ATTLIST display vtotal CDATA #IMPLIED>\n" +
                    "<!ATTLIST display vbend CDATA #IMPLIED>\n" +
                    "<!ATTLIST display vbstart CDATA #IMPLIED>\n" +
                    "<!ELEMENT sound EMPTY>\n" +
                    "<!ATTLIST sound channels CDATA #REQUIRED>\n" +
                    "<!ELEMENT input (control*)>\n" +
                    "<!ATTLIST input service (yes|no) 'no'>\n" +
                    "<!ATTLIST input tilt (yes|no) 'no'>\n" +
                    "<!ATTLIST input players CDATA #REQUIRED>\n" +
                    "<!ATTLIST input coins CDATA #IMPLIED>\n" +
                    "<!ELEMENT control EMPTY>\n" +
                    "<!ATTLIST control type CDATA #REQUIRED>\n" +
                    "<!ATTLIST control player CDATA #IMPLIED>\n" +
                    "<!ATTLIST control buttons CDATA #IMPLIED>\n" +
                    "<!ATTLIST control reqbuttons CDATA #IMPLIED>\n" +
                    "<!ATTLIST control minimum CDATA #IMPLIED>\n" +
                    "<!ATTLIST control maximum CDATA #IMPLIED>\n" +
                    "<!ATTLIST control sensitivity CDATA #IMPLIED>\n" +
                    "<!ATTLIST control keydelta CDATA #IMPLIED>\n" +
                    "<!ATTLIST control reverse (yes|no) 'no'>\n" +
                    "<!ATTLIST control ways CDATA #IMPLIED>\n" +
                    "<!ATTLIST control ways2 CDATA #IMPLIED>\n" +
                    "<!ATTLIST control ways3 CDATA #IMPLIED>\n" +
                    "<!ELEMENT dipswitch (dipvalue*)>\n" +
                    "<!ATTLIST dipswitch name CDATA #REQUIRED>\n" +
                    "<!ATTLIST dipswitch tag CDATA #REQUIRED>\n" +
                    "<!ATTLIST dipswitch mask CDATA #REQUIRED>\n" +
                    "<!ELEMENT dipvalue EMPTY>\n" +
                    "<!ATTLIST dipvalue name CDATA #REQUIRED>\n" +
                    "<!ATTLIST dipvalue value CDATA #REQUIRED>\n" +
                    "<!ATTLIST dipvalue default (yes|no) 'no'>\n" +
                    "<!ELEMENT configuration (confsetting*)>\n" +
                    "<!ATTLIST configuration name CDATA #REQUIRED>\n" +
                    "<!ATTLIST configuration tag CDATA #REQUIRED>\n" +
                    "<!ATTLIST configuration mask CDATA #REQUIRED>\n" +
                    "<!ELEMENT confsetting EMPTY>\n" +
                    "<!ATTLIST confsetting name CDATA #REQUIRED>\n" +
                    "<!ATTLIST confsetting value CDATA #REQUIRED>\n" +
                    "<!ATTLIST confsetting default (yes|no) 'no'>\n" +
                    "<!ELEMENT port (analog*)>\n" +
                    "<!ATTLIST port tag CDATA #REQUIRED>\n" +
                    "<!ELEMENT analog EMPTY>\n" +
                    "<!ATTLIST analog mask CDATA #REQUIRED>\n" +
                    "<!ELEMENT adjuster EMPTY>\n" +
                    "<!ATTLIST adjuster name CDATA #REQUIRED>\n" +
                    "<!ATTLIST adjuster default CDATA #REQUIRED>\n" +
                    "<!ELEMENT driver EMPTY>\n" +
                    "<!ATTLIST driver status (good|imperfect|preliminary) #REQUIRED>\n" +
                    "<!ATTLIST driver emulation (good|imperfect|preliminary) #REQUIRED>\n" +
                    "<!ATTLIST driver color (good|imperfect|preliminary) #REQUIRED>\n" +
                    "<!ATTLIST driver sound (good|imperfect|preliminary) #REQUIRED>\n" +
                    "<!ATTLIST driver graphic (good|imperfect|preliminary) #REQUIRED>\n" +
                    "<!ATTLIST driver cocktail (good|imperfect|preliminary) #IMPLIED>\n" +
                    "<!ATTLIST driver protection (good|imperfect|preliminary) #IMPLIED>\n" +
                    "<!ATTLIST driver savestate (supported|unsupported) #REQUIRED>\n" +
                    "<!ELEMENT device (instance*,extension*)>\n" +
                    "<!ATTLIST device type CDATA #REQUIRED>\n" +
                    "<!ATTLIST device tag CDATA #IMPLIED>\n" +
                    "<!ATTLIST device fixed_image CDATA #IMPLIED>\n" +
                    "<!ATTLIST device mandatory CDATA #IMPLIED>\n" +
                    "<!ATTLIST device interface CDATA #IMPLIED>\n" +
                    "<!ELEMENT instance EMPTY>\n" +
                    "<!ATTLIST instance name CDATA #REQUIRED>\n" +
                    "<!ATTLIST instance briefname CDATA #REQUIRED>\n" +
                    "<!ELEMENT extension EMPTY>\n" +
                    "<!ATTLIST extension name CDATA #REQUIRED>\n" +
                    "<!ELEMENT slot (slotoption*)>\n" +
                    "<!ATTLIST slot name CDATA #REQUIRED>\n" +
                    "<!ELEMENT slotoption EMPTY>\n" +
                    "<!ATTLIST slotoption name CDATA #REQUIRED>\n" +
                    "<!ATTLIST slotoption devname CDATA #REQUIRED>\n" +
                    "<!ATTLIST slotoption default (yes|no) 'no'>\n" +
                    "<!ELEMENT softwarelist EMPTY>\n" +
                    "<!ATTLIST softwarelist name CDATA #REQUIRED>\n" +
                    "<!ATTLIST softwarelist status (original|compatible) #REQUIRED>\n" +
                    "<!ATTLIST softwarelist filter CDATA #IMPLIED>\n" +
                    "<!ELEMENT ramoption (#PCDATA)>\n" +
                    "<!ATTLIST ramoption default CDATA #IMPLIED>\n";

/*      FROM 0.177
DOCTYPE node:
<!ELEMENT mame (machine+)>
<!ATTLIST mame build CDATA #IMPLIED>
<!ATTLIST mame debug (yes|no) 'no'>
<!ATTLIST mame mameconfig CDATA #REQUIRED>
<!ELEMENT machine (description,year?,manufacturer?,biosset*,rom*,disk*,device_ref*,sample*,chip*,display*,sound?,input?,dipswitch*,configuration*,port*,adjuster*,driver?,device*,slot*,softwarelist*,ramoption*)>
<!ATTLIST machine name CDATA #REQUIRED>
<!ATTLIST machine sourcefile CDATA #IMPLIED>
<!ATTLIST machine isbios (yes|no) 'no'>
<!ATTLIST machine isdevice (yes|no) 'no'>
<!ATTLIST machine ismechanical (yes|no) 'no'>
<!ATTLIST machine runnable (yes|no) 'yes'>
<!ATTLIST machine cloneof CDATA #IMPLIED>
<!ATTLIST machine romof CDATA #IMPLIED>
<!ATTLIST machine sampleof CDATA #IMPLIED>
<!ELEMENT description (#PCDATA)>
<!ELEMENT year (#PCDATA)>
<!ELEMENT manufacturer (#PCDATA)>
<!ELEMENT biosset EMPTY>
<!ATTLIST biosset name CDATA #REQUIRED>
<!ATTLIST biosset description CDATA #REQUIRED>
<!ATTLIST biosset default (yes|no) 'no'>
<!ELEMENT rom EMPTY>
<!ATTLIST rom name CDATA #REQUIRED>
<!ATTLIST rom bios CDATA #IMPLIED>
<!ATTLIST rom size CDATA #REQUIRED>
<!ATTLIST rom crc CDATA #IMPLIED>
<!ATTLIST rom sha1 CDATA #IMPLIED>
<!ATTLIST rom merge CDATA #IMPLIED>
<!ATTLIST rom region CDATA #IMPLIED>
<!ATTLIST rom offset CDATA #IMPLIED>
<!ATTLIST rom status (baddump|nodump|good) 'good'>
<!ATTLIST rom optional (yes|no) 'no'>
<!ELEMENT disk EMPTY>
<!ATTLIST disk name CDATA #REQUIRED>
<!ATTLIST disk sha1 CDATA #IMPLIED>
<!ATTLIST disk merge CDATA #IMPLIED>
<!ATTLIST disk region CDATA #IMPLIED>
<!ATTLIST disk index CDATA #IMPLIED>
<!ATTLIST disk writable (yes|no) 'no'>
<!ATTLIST disk status (baddump|nodump|good) 'good'>
<!ATTLIST disk optional (yes|no) 'no'>
<!ELEMENT device_ref EMPTY>
<!ATTLIST device_ref name CDATA #REQUIRED>
<!ELEMENT sample EMPTY>
<!ATTLIST sample name CDATA #REQUIRED>
<!ELEMENT chip EMPTY>
<!ATTLIST chip name CDATA #REQUIRED>
<!ATTLIST chip tag CDATA #IMPLIED>
<!ATTLIST chip type (cpu|audio) #REQUIRED>
<!ATTLIST chip clock CDATA #IMPLIED>
<!ELEMENT display EMPTY>
<!ATTLIST display tag CDATA #IMPLIED>
<!ATTLIST display type (raster|vector|lcd|unknown) #REQUIRED>
<!ATTLIST display rotate (0|90|180|270) #REQUIRED>
<!ATTLIST display flipx (yes|no) 'no'>
<!ATTLIST display width CDATA #IMPLIED>
<!ATTLIST display height CDATA #IMPLIED>
<!ATTLIST display refresh CDATA #REQUIRED>
<!ATTLIST display pixclock CDATA #IMPLIED>
<!ATTLIST display htotal CDATA #IMPLIED>
<!ATTLIST display hbend CDATA #IMPLIED>
<!ATTLIST display hbstart CDATA #IMPLIED>
<!ATTLIST display vtotal CDATA #IMPLIED>
<!ATTLIST display vbend CDATA #IMPLIED>
<!ATTLIST display vbstart CDATA #IMPLIED>
<!ELEMENT sound EMPTY>
<!ATTLIST sound channels CDATA #REQUIRED>
<!ELEMENT input (control*)>
<!ATTLIST input service (yes|no) 'no'>
<!ATTLIST input tilt (yes|no) 'no'>
<!ATTLIST input players CDATA #REQUIRED>
<!ATTLIST input coins CDATA #IMPLIED>
<!ELEMENT control EMPTY>
<!ATTLIST control type CDATA #REQUIRED>
<!ATTLIST control player CDATA #IMPLIED>
<!ATTLIST control buttons CDATA #IMPLIED>
<!ATTLIST control reqbuttons CDATA #IMPLIED>
<!ATTLIST control minimum CDATA #IMPLIED>
<!ATTLIST control maximum CDATA #IMPLIED>
<!ATTLIST control sensitivity CDATA #IMPLIED>
<!ATTLIST control keydelta CDATA #IMPLIED>
<!ATTLIST control reverse (yes|no) 'no'>
<!ATTLIST control ways CDATA #IMPLIED>
<!ATTLIST control ways2 CDATA #IMPLIED>
<!ATTLIST control ways3 CDATA #IMPLIED>
<!ELEMENT dipswitch (dipvalue*)>
<!ATTLIST dipswitch name CDATA #REQUIRED>
<!ATTLIST dipswitch tag CDATA #REQUIRED>
<!ATTLIST dipswitch mask CDATA #REQUIRED>
<!ELEMENT dipvalue EMPTY>
<!ATTLIST dipvalue name CDATA #REQUIRED>
<!ATTLIST dipvalue value CDATA #REQUIRED>
<!ATTLIST dipvalue default (yes|no) 'no'>
<!ELEMENT configuration (confsetting*)>
<!ATTLIST configuration name CDATA #REQUIRED>
<!ATTLIST configuration tag CDATA #REQUIRED>
<!ATTLIST configuration mask CDATA #REQUIRED>
<!ELEMENT confsetting EMPTY>
<!ATTLIST confsetting name CDATA #REQUIRED>
<!ATTLIST confsetting value CDATA #REQUIRED>
<!ATTLIST confsetting default (yes|no) 'no'>
<!ELEMENT port (analog*)>
<!ATTLIST port tag CDATA #REQUIRED>
<!ELEMENT analog EMPTY>
<!ATTLIST analog mask CDATA #REQUIRED>
<!ELEMENT adjuster EMPTY>
<!ATTLIST adjuster name CDATA #REQUIRED>
<!ATTLIST adjuster default CDATA #REQUIRED>
<!ELEMENT driver EMPTY>
<!ATTLIST driver status (good|imperfect|preliminary) #REQUIRED>
<!ATTLIST driver emulation (good|imperfect|preliminary) #REQUIRED>
<!ATTLIST driver color (good|imperfect|preliminary) #REQUIRED>
<!ATTLIST driver sound (good|imperfect|preliminary) #REQUIRED>
<!ATTLIST driver graphic (good|imperfect|preliminary) #REQUIRED>
<!ATTLIST driver cocktail (good|imperfect|preliminary) #IMPLIED>
<!ATTLIST driver protection (good|imperfect|preliminary) #IMPLIED>
<!ATTLIST driver savestate (supported|unsupported) #REQUIRED>
<!ELEMENT device (instance*,extension*)>
<!ATTLIST device type CDATA #REQUIRED>
<!ATTLIST device tag CDATA #IMPLIED>
<!ATTLIST device fixed_image CDATA #IMPLIED>
<!ATTLIST device mandatory CDATA #IMPLIED>
<!ATTLIST device interface CDATA #IMPLIED>
<!ELEMENT instance EMPTY>
<!ATTLIST instance name CDATA #REQUIRED>
<!ATTLIST instance briefname CDATA #REQUIRED>
<!ELEMENT extension EMPTY>
<!ATTLIST extension name CDATA #REQUIRED>
<!ELEMENT slot (slotoption*)>
<!ATTLIST slot name CDATA #REQUIRED>
<!ELEMENT slotoption EMPTY>
<!ATTLIST slotoption name CDATA #REQUIRED>
<!ATTLIST slotoption devname CDATA #REQUIRED>
<!ATTLIST slotoption default (yes|no) 'no'>
<!ELEMENT softwarelist EMPTY>
<!ATTLIST softwarelist name CDATA #REQUIRED>
<!ATTLIST softwarelist status (original|compatible) #REQUIRED>
<!ATTLIST softwarelist filter CDATA #IMPLIED>
<!ELEMENT ramoption (#PCDATA)>
<!ATTLIST ramoption default CDATA #IMPLIED>

     */
}
